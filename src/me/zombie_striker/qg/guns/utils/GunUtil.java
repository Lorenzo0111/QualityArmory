package me.zombie_striker.qg.guns.utils;

import java.util.ArrayList;
import java.util.List;

import me.zombie_striker.qg.ItemFact;
import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.ammo.Ammo;
import me.zombie_striker.qg.ammo.AmmoUtil;
import me.zombie_striker.qg.armor.BulletProtectionUtil;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.handlers.AimManager;
import me.zombie_striker.qg.handlers.BulletWoundHandler;
import me.zombie_striker.qg.handlers.HeadShotUtil;
import me.zombie_striker.qg.handlers.Update19OffhandChecker;
import me.zombie_striker.qg.handlers.gunvalues.ChargingHandlerEnum;

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

import com.alessiodp.partiesapi.Parties;

public class GunUtil {

	@SuppressWarnings("deprecation")
	public static void shoot(Gun g, Player p, double sway, double damage, int shots, int range) {
		for (int i = 0; i < shots; i++) {
			Location start = p.getEyeLocation().clone();
			Vector go = p.getLocation().getDirection().normalize();
			go.add(new Vector((Math.random() * 2 * sway) - sway, (Math.random() * 2 * sway) - sway,
					(Math.random() * 2 * sway) - sway));
			Vector step = go.clone().multiply(Main.bulletStep);

			// Simple values to make it easier on the search
			// boolean posX = go.getX() > 0;
			// boolean posZ = go.getZ() > 0;
			Entity hitTarget = null;
			double dis2 = range;

			boolean overrideocculde = false;

			boolean headShot = false;

			Location bulletHitLoc = null;

			int maxDistance = range;
			Block b = p.getTargetBlock(null, range);
			if (isSolid(b, b.getLocation())) {
				maxDistance = (int) Math.min(range, b.getLocation().distance(start));
			}

			double degreeVector = Math.atan2(go.getX(), go.getZ());
			if (degreeVector > Math.PI)
				degreeVector = 2 * Math.PI - degreeVector;

			for (Entity e : p.getNearbyEntities(maxDistance, maxDistance, maxDistance)) {
				if (e instanceof Damageable)
					if (e != p && e != p.getVehicle() && e != p.getPassenger()) {
						// if (e.getLocation().getX() - start.getX() > 0 == posX)
						// if (e.getLocation().getZ() - start.getZ() > 0 == posZ) {
						double degreeEntity = Math.atan2(e.getLocation().getX() - start.getX(),
								e.getLocation().getZ() - start.getZ());
						if (degreeEntity > Math.PI)
							degreeEntity = 2 * Math.PI - degreeEntity;
						if (Math.max(degreeEntity, degreeVector) - Math.min(degreeEntity, degreeVector) < Math.PI / 2) {

							double dis = e.getLocation().distance(start);
							if (dis > dis2)
								continue;
							Location test = start.clone();
							// If the entity is close to the line of fire.
							if (Main.hasParties && (!Main.friendlyFire)) {
								try {
									if (e instanceof Player)
										if (Parties.getApi().getPartyPlayer(e.getUniqueId()).getPartyName()
												.equalsIgnoreCase(Parties.getApi().getPartyPlayer(p.getUniqueId())
														.getPartyName()))
											continue;
								} catch (Error | Exception e43) {

								}
							}
							boolean occulde = false;
							double lastingDist = dis;
							boolean hit = false;
							for (int dist = 0; dist < dis / Main.bulletStep; dist++) {
								test.add(step);
								if (test.distance(e.getLocation()) < 7 && HeadShotUtil.closeEnough(e, test)) {
									hit = true;
									break;
								}
								if (isSolid(test.getBlock(), test)) {
									occulde = true;
									lastingDist = dist;
									break;
								}
							}
							if (occulde) {
								bulletHitLoc = test;
								dis2 = test.distance(start);
							} else if (hit) {
								bulletHitLoc = test;
								dis2 = lastingDist;
								overrideocculde = true;
								hitTarget = e;
								headShot = HeadShotUtil.isHeadShot(e, test);
								if (headShot) {
									try {
										p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 2, 1);
										if (!Main.isVersionHigherThan(1, 9))
											try {
												p.playSound(p.getLocation(), Sound.valueOf("LAVA_POP"), 6, 1);
											} catch (Error | Exception h4) {
											}
									} catch (Error | Exception h4) {
										p.playSound(p.getLocation(), Sound.valueOf("LAVA_POP"), 1, 1);
									}
								}
							}
						}
					}
			}
			if (hitTarget != null) {
				if (!(hitTarget instanceof Player) || Main.allowGunsInRegion(hitTarget.getLocation())) {

					boolean bulletProtection = false;
					if (hitTarget instanceof Player) {
						bulletProtection = BulletProtectionUtil.stoppedBullet(p, bulletHitLoc, go);
						if (!bulletProtection) {
							BulletWoundHandler.bulletHit((Player) hitTarget, g.getAmmoType().getPiercingDamage());
						} else {
							hitTarget.sendMessage(Main.S_BULLETPROOFSTOPPEDBLEEDING);
						}
					}

					((Damageable) hitTarget).damage(
							damage * (bulletProtection ? 0.1 : 1) * (headShot ? (Main.HeadshotOneHit ? 50 : 2) : 1), p);
					((LivingEntity) hitTarget).setNoDamageTicks(0);

				}
			}
			double smokeDistance = 0;
			List<Player> nonheard = start.getWorld().getPlayers();
			nonheard.remove(p);
			for (int dist = 0; dist < (dis2 / Main.bulletStep); dist++) {
				start.add(step);
				try {
					int control = 3;
					if (dist % control == 0) {
						List<Player> heard = new ArrayList<>();
						for (Player p2 : nonheard) {
							if (p2.getLocation().distance(start) < control * 2) {
								if (Main.isVersionHigherThan(1, 10)) {
									try {
										start.getWorld().playSound(start, Sound.BLOCK_DISPENSER_LAUNCH, 2, 3);
									} catch (Error e) {
										start.getWorld().playSound(start, Sound.valueOf("SHOOT_ARROW"), 2, 2);
									}
								} else {
									try {
										start.getWorld().playSound(start, Sound.BLOCK_DISPENSER_LAUNCH, 2, 3);
									} catch (Error e) {
										start.getWorld().playSound(start, Sound.valueOf("SHOOT_ARROW"), 2, 2);
									}
								}
								heard.add(p2);
							}
						}
						for (Player p3 : heard) {
							nonheard.remove(p3);
						}
					}

				} catch (Error | Exception e53) {
					if (dist % 30 == 0) {
						try {
							start.getWorld().playSound(start, Sound.BLOCK_DISPENSER_LAUNCH, 2, 2);
							start.getWorld().playSound(start, Sound.BLOCK_FIRE_EXTINGUISH, 2, 2);
						} catch (Error e) {
							start.getWorld().playSound(start, Sound.valueOf("SHOOT_ARROW"), 2, 2);
							start.getWorld().playSound(start, Sound.valueOf("FIRE_IGNITE"), 2, 2);
						}
					}
				}
				if (overrideocculde || !isSolid(start.getBlock(), start)) {
					if (Main.enableBulletTrails)
						if (smokeDistance + i >= Main.smokeSpacing * shots) {
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
		Main.DEBUG("About to shoot!");

		if (g.getChargingVal() != null
				&& (g.getChargingVal().isCharging(player) || g.getChargingVal().isReloading(player)))
			return;

		if (g.getLastShotForGun().containsKey(player.getUniqueId())
				&& (System.currentTimeMillis() - g.getLastShotForGun().get(player.getUniqueId()) <= showdelay))
			return;
		g.getLastShotForGun().put(player.getUniqueId(), System.currentTimeMillis());

		@SuppressWarnings("deprecation")
		final ItemStack temp = offhand ? Update19OffhandChecker.getItemStackOFfhand(player)
				: player.getInventory().getItemInHand();
		ItemMeta im = temp.getItemMeta();

		boolean regularshoot = true;
		if (g.getChargingVal() != null
				&& (!g.getChargingVal().isCharging(player) && !g.getChargingVal().isReloading(player))) {
			regularshoot = g.getChargingVal().shoot(g, player, temp);

		}

		if (regularshoot) {
			GunUtil.shoot(g, player, acc * AimManager.getSway(g, player.getUniqueId()), g.getDamage(),
					g.getBulletsPerShot(), g.getMaxDistance());
			playShoot(g, player);
		}

		int amount = ItemFact.getAmount(temp) - (g.getChargingVal() != null
				&& ChargingHandlerEnum.getEnumV(g.getChargingVal()) == ChargingHandlerEnum.RAPIDFIRE
						? g.getBulletsPerShot()
						: 1);

		if (amount < 0)
			amount = 0;

		if (Main.enableVisibleAmounts) {
			temp.setAmount(amount > 64 ? 64 : amount == 0 ? 1 : amount);
		}
		int slot;
		if (offhand) {
			slot = -1;
		} else {
			slot = player.getInventory().getHeldItemSlot();
		}
		im.setLore(ItemFact.getGunLore(g, temp, amount));
		temp.setItemMeta(im);
		if (slot == -1) {
			try {
				player.getInventory().setItemInOffHand(temp);
			} catch (Error e) {
			}
		} else {
			player.getInventory().setItem(slot, temp);
		}
	}

	public static void playShoot(final Gun g, final Player player) {
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					player.getWorld().playSound(player.getLocation(), g.getWeaponSound().getName(), 4, 1);
					/*
					 * player.getWorld().playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 5,
					 * 1); player.getWorld().playSound(player.getLocation(),
					 * Sound.ENTITY_WITHER_SHOOT, 8, 2);
					 * player.getWorld().playSound(player.getLocation(),
					 * Sound.ENTITY_ENDERDRAGON_FIREBALL_EXPLODE, 8, 2f);
					 */
					if (!Main.isVersionHigherThan(1, 9)) {
						try {
							player.getWorld().playSound(player.getLocation(), Sound.valueOf("CLICK"), 5, 1);
							player.getWorld().playSound(player.getLocation(), Sound.valueOf("WITHER_SHOOT"), 8, 2);
							player.getWorld().playSound(player.getLocation(), Sound.valueOf("EXPLODE"), 8, 2f);
						} catch (Error | Exception e3) {
							player.getWorld().playSound(player.getLocation(), Sound.valueOf("BLOCK_LEVER_CLICK"), 5, 1);
						}
					}
				} catch (Error e2) {
					player.getWorld().playSound(player.getLocation(), Sound.valueOf("CLICK"), 5, 1);
					player.getWorld().playSound(player.getLocation(), Sound.valueOf("WITHER_SHOOT"), 8, 2);
					player.getWorld().playSound(player.getLocation(), Sound.valueOf("EXPLODE"), 8, 2f);
				}

			}
		}.runTaskLater(Main.getInstance(), 1);
		// Simply delaying the sound by 1/20th of a second makes shooting so much more
		// immersive
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

		if (ItemFact.getAmount(temp) == g.getMaxBullets()) {
			return;
		}

		if (im.getLore() != null && im.getDisplayName().contains("Reloading.")) {
			/*
			 * try { /* player.getWorld().playSound(player.getLocation(),
			 * Sound.BLOCK_LEVER_CLICK, 5, 1);
			 *
			 * 
			 * player.getWorld().playSound(player.getLocation(),
			 * WeaponSounds.RELOAD_MAG_OUT.getName(), 1, 1f); if
			 * (!Main.isVersionHigherThan(1, 9)) { try {
			 * player.getWorld().playSound(player.getLocation(), Sound.valueOf("CLICK"), 5,
			 * 1); } catch (Error | Exception e3) {
			 * player.getWorld().playSound(player.getLocation(),
			 * Sound.valueOf("BLOCK_LEVER_CLICK"), 5, 1); } }
			 * 
			 * return; } catch (Error e2) { try {
			 * player.getWorld().playSound(player.getLocation(), Sound.valueOf("CLICK"), 5,
			 * 1); } catch (Error | Exception e3) {
			 * player.getWorld().playSound(player.getLocation(),
			 * Sound.valueOf("BLOCK_LEVER_CLICK"), 5, 1); } }
			 */
		} else {
			try {
				/*
				 * player.getWorld().playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 5,
				 * 0.7f);
				 */
				player.getWorld().playSound(player.getLocation(), WeaponSounds.RELOAD_MAG_OUT.getName(), 1, 1f);
			} catch (Error e2) {
				try {
					player.getWorld().playSound(player.getLocation(), Sound.valueOf("CLICK"), 5, 1);
				} catch (Error | Exception e3) {
					player.getWorld().playSound(player.getLocation(), Sound.valueOf("BLOCK_LEVER_CLICK"), 5, 1);
				}
			}
			final int slot = player.getInventory().getHeldItemSlot();

			Ammo ammo = g.getAmmoType();

			final int initialAmount = ItemFact.getAmount(temp);
			final int reloadAmount = doNotRemoveAmmo ? g.getMaxBullets()
					: Math.min(g.getMaxBullets(), initialAmount + AmmoUtil.getAmmoAmount(player, ammo));
			final int subtractAmount = reloadAmount - initialAmount;
			if (!doNotRemoveAmmo)
				AmmoUtil.removeAmmo(player, ammo, subtractAmount);

			if (g.getChargingVal() != null) {
				seconds = g.getChargingVal().reload(player, g, subtractAmount);
			}

			im.setLore(ItemFact.getGunLore(g, temp, 0));
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
						if (!Main.isVersionHigherThan(1, 9)) {
							try {
								player.getWorld().playSound(player.getLocation(), Sound.valueOf("CLICK"), 5, 1);
							} catch (Error | Exception e3) {
								player.getWorld().playSound(player.getLocation(), Sound.valueOf("BLOCK_LEVER_CLICK"), 5,
										1);
							}
						}
					} catch (Error e2) {
						try {
							player.getWorld().playSound(player.getLocation(), Sound.valueOf("CLICK"), 5, 1);
						} catch (Error | Exception e3) {
							player.getWorld().playSound(player.getLocation(), Sound.valueOf("BLOCK_LEVER_CLICK"), 5, 1);
						}
					}
					ItemMeta newim = temp.getItemMeta();
					newim.setLore(ItemFact.getGunLore(g, temp, reloadAmount));
					newim.setDisplayName(g.getDisplayName());
					temp.setItemMeta(newim);
					if (Main.enableVisibleAmounts)
						temp.setAmount(reloadAmount);
					player.getInventory().setItem(slot, temp);
					Main.sendHotbarGunAmmoCount(player, g, temp);
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
