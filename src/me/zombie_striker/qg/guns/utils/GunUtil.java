package me.zombie_striker.qg.guns.utils;

import java.util.ArrayList;
import java.util.List;

import me.zombie_striker.qg.ItemFact;
import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.ammo.Ammo;
import me.zombie_striker.qg.ammo.AmmoUtil;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.handlers.AimManager;
import me.zombie_striker.qg.handlers.HeadShotUtil;
import me.zombie_striker.qg.handlers.Update19OffhandChecker;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class GunUtil {

	public static void shoot(Player p, double sway, double damage, int shots, int range) {
		for (int i = 0; i < shots; i++) {
			Location start = p.getEyeLocation().clone();
			Vector go = p.getLocation().getDirection().normalize();
			go.add(new Vector((Math.random() * 2 * sway) - sway, (Math.random() * 2 * sway) - sway,
					(Math.random() * 2 * sway) - sway));
			Vector step = go.clone().multiply(Main.bulletStep);

			// Simple values to make it easier on the search
			boolean posX = go.getX() > 0;
			boolean posZ = go.getZ() > 0;
			Entity hitTarget = null;
			double dis2 = range;

			boolean overrideocculde = false;

			boolean headShot = false;

			int maxDistance = range;
			Block b = p.getTargetBlock(null, range);
			if (isSolid(b, b.getLocation())) {
				maxDistance = (int) Math.min(range, b.getLocation().distance(start));
			}

			for (Entity e : p.getNearbyEntities(maxDistance, maxDistance, maxDistance)) {
				if (e != p && e != p.getVehicle())
					if (e.getLocation().getX() - start.getX() > 0 == posX)
						if (e.getLocation().getZ() - start.getZ() > 0 == posZ) {
							double dis = e.getLocation().distance(start);
							if (dis > dis2)
								continue;
							Location test = start.clone();
							if (HeadShotUtil.closeEnough(e, test.clone().add(go.clone().multiply(dis)))) {
								// If the entity is close to the line of fire.
								if (e instanceof Damageable) {
									boolean occulde = false;
									double lastingDist = dis;
									for (int dist = 0; dist < dis / Main.bulletStep; dist++) {
										test.add(step);
										if (isSolid(test.getBlock(), test)) {
											if (!HeadShotUtil.closeEnough(e, test)) {
												occulde = true;
												lastingDist = dist;
												break;
											}
										}
									}
									if (!occulde) {
										dis2 = lastingDist;
										overrideocculde = true;
										hitTarget = e;
										headShot = HeadShotUtil.isHeadShot(e, test);
										if (headShot) {
											try {
												p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 2, 1);
											} catch (Error | Exception h4) {
												p.playSound(p.getLocation(), Sound.valueOf("POP"), 1, 1);
											}
										}
									}
								}
							}
						}
			}
			if (hitTarget != null) {
				if (!(hitTarget instanceof Player) || Main.allowGunsInRegion(hitTarget.getLocation())) {
					((Damageable) hitTarget).damage(damage * (headShot ? 2 : 1), p);
					((LivingEntity) hitTarget).setNoDamageTicks(0);
				}
			}
			double smokeDistance = 0;
			for (int dist = 0; dist < (dis2 / Main.bulletStep); dist++) {
				start.add(step);
				if (dist % 25 == 0) {
					try {
						start.getWorld().playSound(start, Sound.BLOCK_DISPENSER_LAUNCH, 2, 2);
						start.getWorld().playSound(start, Sound.BLOCK_FIRE_EXTINGUISH, 2, 2);
					} catch (Error e) {
						start.getWorld().playSound(start, Sound.valueOf("SHOOT_ARROW"), 2, 2);
						start.getWorld().playSound(start, Sound.valueOf("FIRE_IGNITE"), 2, 2);
					}
				}
				if (overrideocculde || !isSolid(start.getBlock(), start)) {
					if (Main.enableBulletTrails)
						if (smokeDistance >= Main.smokeSpacing) {
							try {
								start.getWorld().spawnParticle((Particle) Main.bulletTrail, start, 0);
							} catch (Error e2) {
							}
							smokeDistance = 0;
						} else {
							smokeDistance += Main.bulletStep;
						}
				} else {
					// start.getWorld().spawnParticle(Particle.BLOCK_DUST,start,start.getBlock().getTypeId());
					start.getWorld().playEffect(start, Effect.STEP_SOUND, start.getBlock().getType());
					break;
				}
			}
		}
	}

	public static void basicShoot(boolean offhand, Gun g, Player player, double acc) {
		basicShoot(offhand, g, player, acc, 1);
	}

	public static void basicShoot(boolean offhand, Gun g, Player player, double acc, int times) {

		long showdelay = ((int) (g.getDelayBetweenShotsInSeconds() * 1000));

		if (g.getLastShotForGun().containsKey(player.getUniqueId())
				&& (System.currentTimeMillis() - g.getLastShotForGun().get(player.getUniqueId()) <= showdelay))
			return;
		g.getLastShotForGun().put(player.getUniqueId(), System.currentTimeMillis());

		@SuppressWarnings("deprecation")
		final ItemStack temp = offhand ? Update19OffhandChecker.getItemStackOFfhand(player)
				: player.getInventory().getItemInHand();
		ItemMeta im = temp.getItemMeta();

		GunUtil.shoot(player, acc * AimManager.getSway(g, player.getUniqueId()), g.getDamage(), times, 300);

		if (Main.enableVisibleAmounts)
			temp.setAmount(temp.getAmount() - 1);
		int slot;
		if (offhand) {
			slot = -1;
		} else {
			slot = player.getInventory().getHeldItemSlot();
		}
		im.setLore(ItemFact.getGunLore(g, temp, ItemFact.getAmount(temp)));
		temp.setItemMeta(im);
		if (slot == -1) {
			try {
				player.getInventory().setItemInOffHand(temp);
			} catch (Error e) {
			}
		} else {
			player.getInventory().setItem(slot, temp);
		}
		try {
			player.getWorld().playSound(player.getLocation(), g.getWeaponSound().getName(), 4, 1);
			/*
			 * player.getWorld().playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 5,
			 * 1); player.getWorld().playSound(player.getLocation(),
			 * Sound.ENTITY_WITHER_SHOOT, 8, 2);
			 * player.getWorld().playSound(player.getLocation(),
			 * Sound.ENTITY_ENDERDRAGON_FIREBALL_EXPLODE, 8, 2f);
			 */
		} catch (Error e2) {
			player.getWorld().playSound(player.getLocation(), Sound.valueOf("CLICK"), 5, 1);
			player.getWorld().playSound(player.getLocation(), Sound.valueOf("WITHER_SHOOT"), 8, 2);
			player.getWorld().playSound(player.getLocation(), Sound.valueOf("EXPLODE"), 8, 2f);
		}
	}

	public static boolean hasAmmo(Player player, Gun g) {
		return AmmoUtil.getAmmoAmount(player, g.getAmmoType()) > 0;
	}

	public static void basicReload(final Gun g, final Player player, boolean doNotRemoveAmmo) {
		basicReload(g, player, doNotRemoveAmmo, 1.5);
	}

	public static void basicReload(final Gun g, final Player player, boolean doNotRemoveAmmo, double seconds) {
		@SuppressWarnings("deprecation")
		final ItemStack temp = player.getInventory().getItemInHand();
		ItemMeta im = temp.getItemMeta();

		if (ItemFact.getAmount(temp) == g.getMaxBullets() - 1) {
			return;
		}

		if (im.getLore() != null && im.getDisplayName().contains("Reloading.")) {
			try {
				/*
				 * player.getWorld().playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 5,
				 * 1);
				 */

				player.getWorld().playSound(player.getLocation(), WeaponSounds.RELOAD_MAG_OUT.getName(), 1, 1f);
				return;
			} catch (Error e2) {
				player.getWorld().playSound(player.getLocation(), Sound.valueOf("CLICK"), 5, 1);
			}
		} else {
			try {
				/*
				 * player.getWorld().playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 5,
				 * 0.7f);
				 */
				player.getWorld().playSound(player.getLocation(), WeaponSounds.RELOAD_MAG_OUT.getName(), 1, 1f);
			} catch (Error e2) {
				player.getWorld().playSound(player.getLocation(), Sound.valueOf("CLICK"), 5, 0.7f);
			}
			final int slot = player.getInventory().getHeldItemSlot();

			Ammo ammo = g.getAmmoType();

			final int initialAmount = ItemFact.getAmount(temp) + 1;
			final int reloadAmount = doNotRemoveAmmo ? g.getMaxBullets()
					: Math.min(g.getMaxBullets(), initialAmount + AmmoUtil.getAmmoAmount(player, ammo));
			final int subtractAmount = reloadAmount - initialAmount;
			if (!doNotRemoveAmmo)
				AmmoUtil.removeAmmo(player, ammo, subtractAmount);

			im.setLore(ItemFact.getGunLore(g, temp, 1));
			im.setDisplayName(g.getDisplayName() + " [Reloading...]");
			temp.setItemMeta(im);
			if (Main.enableVisibleAmounts)
				temp.setAmount(1);
			player.getInventory().setItem(slot, temp);
			BukkitTask r = new BukkitRunnable() {
				@Override
				public void run() {
					try {
						/*
						 * player.getWorld().playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 5,
						 * 1); player.getWorld().playSound(player.getLocation(),
						 * Sound.BLOCK_LEVER_CLICK, 5, 1.4f);
						 */

						player.getWorld().playSound(player.getLocation(), WeaponSounds.RELOAD_MAG_IN.getName(), 1, 1f);
					} catch (Error e2) {
						player.getWorld().playSound(player.getLocation(), Sound.valueOf("CLICK"), 5, 1);
						player.getWorld().playSound(player.getLocation(), Sound.valueOf("CLICK"), 5, 1.4f);
					}
					ItemMeta newim = temp.getItemMeta();
					newim.setLore(ItemFact.getGunLore(g, temp, reloadAmount));
					newim.setDisplayName(g.getDisplayName());
					temp.setItemMeta(newim);
					if (Main.enableVisibleAmounts)
						temp.setAmount(reloadAmount);
					player.getInventory().setItem(slot, temp);
				}
			}.runTaskLater(Main.getInstance(), (long) (20 * seconds));
			if (!Main.reloadingTasks.containsKey(player.getUniqueId())) {
				Main.reloadingTasks.put(player.getUniqueId(), new ArrayList<BukkitTask>());
			}
			List<BukkitTask> rr = Main.reloadingTasks.get(player.getUniqueId());
			rr.add(r);
			Main.reloadingTasks.put(player.getUniqueId(), rr);

		}

	}

	@SuppressWarnings("deprecation")
	public static boolean isSolid(Block b, Location l) {
		if (b.getType() == Material.WATER) {
			if (Main.blockbullet_water)
				return true;
		}
		if (b.getType().name().contains("LEAVE")) {
			if (Main.blockbullet_leaves)
				return true;
		}
		if (b.getType().name().contains("SLAB") || b.getType().name().contains("STEP")) {
			if (!Main.blockbullet_halfslabs && ((l.getY() - l.getBlockY() > 0.5 && b.getData() == 0)
					|| (l.getY() - l.getBlockY() <= 0.5 && b.getData() == 1)))
				return false;
			return true;
		}
		if (b.getType() == Material.BED_BLOCK || b.getType().name().contains("DAYLIGHT_DETECTOR")) {
			if (!Main.blockbullet_halfslabs && (l.getY() - l.getBlockY() > 0.5))
				return false;
			return true;
		}
		if (b.getType().name().contains("DOOR")) {
			if (Main.blockbullet_door)
				return true;
			return false;
		}

		if (b.getType().isOccluding()) {
			return true;
		}
		if (b.getType().name().contains("STAIR")) {
			if (b.getData() < 4 && (l.getY() - l.getBlockY() < 0.5))
				return true;
			if (b.getData() >= 4 && (l.getY() - l.getBlockY() > 0.5))
				return true;
			switch (b.getData()) {
			case 0:
			case 4:
				return l.getX() - (0.5 + l.getBlockX()) > 0;
			case 1:
			case 5:
				return l.getX() - (0.5 + l.getBlockX()) < 0;
			case 2:
			case 6:
				return l.getZ() - (0.5 + l.getBlockZ()) > 0;
			case 3:
			case 7:
				return l.getZ() - (0.5 + l.getBlockZ()) < 0;

			}
		}

		return false;
	}
}
