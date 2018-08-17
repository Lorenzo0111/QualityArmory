package me.zombie_striker.qg.guns.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import me.zombie_striker.qg.ItemFact;
import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.ammo.Ammo;
import me.zombie_striker.qg.ammo.AmmoUtil;
import me.zombie_striker.qg.armor.BulletProtectionUtil;
import me.zombie_striker.qg.attachments.AttachmentBase;
import me.zombie_striker.qg.boundingbox.AbstractBoundingBox;
import me.zombie_striker.qg.boundingbox.BoundingBoxManager;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.handlers.*;
import ru.beykerykt.lightapi.LightAPI;
import ru.beykerykt.lightapi.chunks.ChunkInfo;

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

	protected static HashMap<UUID, Location> AF_locs = new HashMap<>();
	protected static HashMap<UUID, BukkitTask> AF_tasks = new HashMap<>();

	public static HashMap<UUID, BukkitTask> rapidfireshooters = new HashMap<>();

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
			try {
				Block b = p.getTargetBlock(null, range);
				if (isSolid(b, b.getLocation())) {
					maxDistance = (int) Math.min(range, b.getLocation().distance(start));
				}
			} catch (Error | Exception e45) {
			}

			double degreeVector = Math.atan2(go.getX(), go.getZ());
			if (degreeVector > Math.PI)
				degreeVector = 2 * Math.PI - degreeVector;

			List<Location> blocksThatWillPLAYBreak = new ArrayList<>();
			List<Location> blocksThatWillBreak = new ArrayList<>();

			for (Entity e : p.getNearbyEntities(maxDistance, maxDistance, maxDistance)) {
				if (e instanceof Damageable)
					if (!Main.ignoreArmorStands || !e.getType().name().equals("ARMOR_STAND"))
						if (e != p && e != p.getVehicle() && e != p.getPassenger()) {
							// if (e.getLocation().getX() - start.getX() > 0 == posX)
							// if (e.getLocation().getZ() - start.getZ() > 0 == posZ) {
							double dis = e.getLocation().distance(start);
							if (dis > dis2)
								continue;
							double degreeEntity = Math.atan2(e.getLocation().getX() - start.getX(),
									e.getLocation().getZ() - start.getZ());
							if (degreeEntity > Math.PI)
								degreeEntity = 2 * Math.PI - degreeEntity;
							if (Math.max(degreeEntity, degreeVector)
									- Math.min(degreeEntity, degreeVector) < (dis > 10 ? Math.PI / 7 : Math.PI / 2)) {

								AbstractBoundingBox box = BoundingBoxManager.getBoundingBox(e);

								Location test = start.clone();
								// If the entity is close to the line of fire.
								if (Main.hasParties && (!Main.friendlyFire)) {
									try {
										if (e instanceof Player)
											if (com.alessiodp.partiesapi.Parties.getApi()
													.getPartyPlayer(e.getUniqueId()).getPartyName()
													.equalsIgnoreCase(com.alessiodp.partiesapi.Parties.getApi()
															.getPartyPlayer(p.getUniqueId()).getPartyName()))
												continue;
									} catch (Error | Exception e43) {

									}
								}
								boolean occulde = false;
								double lastingDist = dis;
								boolean hit = false;
								// Clear this to make sure
								for (int dist = 0; dist < dis / Main.bulletStep; dist++) {
									test.add(step);
									if (box.intersects(test, e)) {
										hit = true;
										break;
									}
									boolean solid = isSolid(test.getBlock(), test);
									if ((solid || isBreakable(test.getBlock(), test))
											&& !blocksThatWillPLAYBreak.contains(new Location(test.getWorld(),
													test.getBlockX(), test.getBlockY(), test.getBlockZ()))) {
										blocksThatWillPLAYBreak.add(new Location(test.getWorld(), test.getBlockX(),
												test.getBlockY(), test.getBlockZ()));
									}
									if (Main.destructableBlocks.contains(test.getBlock().getType())) {
										blocksThatWillBreak.add(test);
									}
									if (solid) {
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
									headShot = box.allowsHeadshots() ? box.isHeadShot(test, e) : false;
									if (headShot) {
										Main.DEBUG("Headshot!");
										if (Main.headshotPling) {
											try {
												p.playSound(p.getLocation(), MultiVersionLookup.getPling(), 2, 1);
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

					((Damageable) hitTarget).damage(damage * (bulletProtection ? 0.1 : 1)
							* (headShot ? (Main.HeadshotOneHit ? 50 : g.getHeadshotMultiplier()) : 1), p);
					if (hitTarget instanceof LivingEntity)
						((LivingEntity) hitTarget).setNoDamageTicks(0);
					Main.DEBUG("Damaging entity " + hitTarget.getName());

				}
			} else {
				Main.DEBUG("No enities hit.");
			}
			double smokeDistance = 0;
			List<Player> nonheard = start.getWorld().getPlayers();
			nonheard.remove(p);
			if (g.useMuzzleSmoke())
				ParticleHandlers.spawnMuzzleSmoke(p, start.clone().add(step.clone().multiply(7)));

			for (int dist = 0; dist < (dis2 / Main.bulletStep); dist++) {
				start.add(step);
				try {
					int control = 3;
					if (dist % control == 0) {
						List<Player> heard = new ArrayList<>();
						for (Player p2 : nonheard) {
							if (p2.getLocation().distance(start) < control * 2) {
								try {
									start.getWorld().playSound(start, Sound.BLOCK_DISPENSER_LAUNCH, 2, 3);
								} catch (Error e) {
									start.getWorld().playSound(start, Sound.valueOf("SHOOT_ARROW"), 2, 2);
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
								// start.getWorld().spawnParticle((Particle) Main.bulletTrail, start, 0);
								ParticleHandlers.spawnGunParticles(g, start);
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

			for (Location l : blocksThatWillBreak) {
				l.getBlock().breakNaturally();
			}

			// TODO: Do lights n stuff
			try {
				if (Bukkit.getPluginManager().getPlugin("LightAPI") != null) {
					final Location loc = p.getEyeLocation().clone();
					LightAPI.createLight(loc, g.getLightOnShoot(), false);
					for (ChunkInfo c : LightAPI.collectChunks(loc)) {
						LightAPI.updateChunk(c);
					}
					new BukkitRunnable() {

						@Override
						public void run() {
							LightAPI.deleteLight(loc, false);
							for (ChunkInfo c : LightAPI.collectChunks(loc)) {
								LightAPI.updateChunk(c);
							}
						}
					}.runTaskLater(Main.getInstance(), 3);
				}
			} catch (Error | Exception e5) {
			}

			// Breaking texture
			if (Main.blockBreakTexture)
				for (@SuppressWarnings("unused")
				Location l : blocksThatWillPLAYBreak) {
					start.getWorld().playSound(start, SoundHandler.getSoundWhenShot(start.getBlock()), 2, 1);
					try {/*
							 * for (Player p2 : l.getWorld().getPlayers()) {
							 * com.comphenix.protocol.events.PacketContainer packet = new
							 * com.comphenix.protocol.events.PacketContainer(
							 * com.comphenix.protocol.Packets.Server.BLOCK_BREAK_ANIMATION);
							 * packet.getIntegers().write(0, p2.getEntityId());
							 * packet.getBlockPositionModifier().write(1, new
							 * com.comphenix.protocol.wrappers.BlockPosition(l.getBlockX(), l.getBlockY(),
							 * l.getBlockZ())); packet.getBytes().write(2, (byte) 4);
							 * com.comphenix.protocol.ProtocolLibrary.getProtocolManager().sendServerPacket(
							 * p2, packet); }
							 */
					} catch (Error | Exception e4) {
					}
				}
		}
	}

	public static void basicShoot(boolean offhand, Gun g, AttachmentBase attachmentBase, Player player, double acc) {
		basicShoot(offhand, g, attachmentBase, player, acc, 1);
	}

	@SuppressWarnings("deprecation")
	public static void basicShoot(boolean offhand, final Gun g, final AttachmentBase attachmentBase,
			final Player player, final double acc, int times) {
		long showdelay = ((int) (g.getDelayBetweenShotsInSeconds() * 1000));
		Main.DEBUG("About to shoot!");

		if (g.getChargingVal() != null && (g.getChargingVal().isCharging(player)
				|| (g.getReloadingingVal() != null && g.getReloadingingVal().isReloading(player))))
			return;

		if (g.getLastShotForGun().containsKey(player.getUniqueId())
				&& (System.currentTimeMillis() - g.getLastShotForGun().get(player.getUniqueId()) <= showdelay)) {
			Main.DEBUG("Shooting canceled due to last shot being too soon.");
			return;
		}
		g.getLastShotForGun().put(player.getUniqueId(), System.currentTimeMillis());

		final ItemStack temp = offhand ? Update19OffhandChecker.getItemStackOFfhand(player)
				: player.getInventory().getItemInHand();
		ItemMeta im = temp.getItemMeta();

		if (rapidfireshooters.containsKey(player.getUniqueId())) {
			Main.DEBUG("Shooting canceled due to rapid fire being enabled.");
			return;
		}

		boolean regularshoot = true;
		if (g.getChargingVal() != null && (!g.getChargingVal().isCharging(player)
				&& (g.getReloadingingVal() == null || !g.getReloadingingVal().isReloading(player)))) {
			regularshoot = g.getChargingVal().shoot(g, player, temp);
			Main.DEBUG("Charging shoot debug: " + g.getName() + " = " + g.getChargingVal() == null ? "null"
					: g.getChargingVal().getName());
		}
		if (regularshoot) {
			GunUtil.shoot(g, player, acc * AimManager.getSway(g, player.getUniqueId()), g.getDamage(),
					g.getBulletsPerShot(), g.getMaxDistance());
			playShoot(g, attachmentBase, player);
		}
		if (g.isAutomatic()) {
			rapidfireshooters.put(player.getUniqueId(), new BukkitRunnable() {
				int slotUsed = player.getInventory().getHeldItemSlot();
				boolean offhand = Main.isIS(player.getItemInHand());

				@Override
				public void run() {
					final AttachmentBase attach = Main.getGunWithAttchments(temp);

					int amount = ItemFact.getAmount(temp);
					if (!player.isSneaking() || slotUsed != player.getInventory().getHeldItemSlot() || amount <= 0) {
						rapidfireshooters.remove(player.getUniqueId()).cancel();
						return;
					}

					boolean regularshoot = true;
					if (g.getChargingVal() != null && (!g.getChargingVal().isCharging(player)
							&& (g.getReloadingingVal() == null || !g.getReloadingingVal().isReloading(player)))) {
						regularshoot = g.getChargingVal().shoot(g, player, temp);
						Main.DEBUG(
								"Charging (rapidfire) shoot debug: " + g.getName() + " = " + g.getChargingVal() == null
										? "null"
										: g.getChargingVal().getName());
					}

					if (regularshoot) {
						GunUtil.shoot(g, player, acc * AimManager.getSway(g, player.getUniqueId()), g.getDamage(),
								g.getBulletsPerShot(), g.getMaxDistance());
						playShoot(g, attachmentBase, player);
					}

					// GunUtil.shoot(g, player, g.getSway() * AimManager.getSway(g,
					// player.getUniqueId()), g.getDamage(), 1,
					// g.getMaxDistance());
					// GunUtil.playShoot(g, attach, player);
					amount--;

					if (amount < 0)
						amount = 0;

					if (Main.enableVisibleAmounts) {
						temp.setAmount(amount > 64 ? 64 : amount == 0 ? 1 : amount);
					}
					ItemMeta im = temp.getItemMeta();
					int slot;
					if (offhand) {
						slot = -1;
					} else {
						slot = player.getInventory().getHeldItemSlot();
					}
					im.setLore(ItemFact.getGunLore(g, attach, temp, amount));
					temp.setItemMeta(im);
					if (slot == -1) {
						try {
							if (Main.isIS(player.getItemInHand())) {
								player.getInventory().setItemInOffHand(temp);
							} else {
								player.getInventory().setItemInHand(temp);
							}

						} catch (Error e) {
						}
					} else {
						player.getInventory().setItem(slot, temp);
					}
					Main.sendHotbarGunAmmoCount(player, g, attach, temp, false);
				}
			}.runTaskTimer(Main.getInstance(), 10 / g.getFireRate(), 10 / g.getFireRate()));
		}

		int amount = ItemFact.getAmount(temp) - /*
												 * (g.getChargingVal() != null &&
												 * ChargingHandlerEnum.getEnumV(g.getChargingVal()) ==
												 * ChargingHandlerEnum.RAPIDFIRE ? g.getBulletsPerShot() : 1)
												 */1;

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
		im.setLore(ItemFact.getGunLore(g, attachmentBase, temp, amount));
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

	public static void playShoot(final Gun g, final AttachmentBase attach, final Player player) {
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					player.getWorld().playSound(player.getLocation(),
							(attach != null && attach.hasNewSound()) ? attach.getNewSound() : g.getWeaponSound(), 4, 1);
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

	public static void basicReload(final Gun g, AttachmentBase attach, final Player player, boolean doNotRemoveAmmo) {
		basicReload(g, attach, player, doNotRemoveAmmo, 1.5);
	}

	public static void basicReload(final Gun g, final AttachmentBase attachment, final Player player,
			boolean doNotRemoveAmmo, double seconds) {
		@SuppressWarnings("deprecation")

		final ItemStack temp = player.getInventory().getItemInHand();
		ItemMeta im = temp.getItemMeta();

		final boolean att = attachment != null;

		if (ItemFact.getAmount(temp) == g.getMaxBullets()) {
			return;
		}

		if (im.getLore() != null && im.getDisplayName().contains(Main.S_RELOADING_MESSAGE)) {
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

			if (g.getReloadingingVal() != null) {
				seconds = g.getReloadingingVal().reload(player, g, subtractAmount);
			}

			im.setLore(ItemFact.getGunLore(g, attachment, temp, 0));
			im.setDisplayName(att ? attachment.getDisplayName() : g.getDisplayName() + Main.S_RELOADING_MESSAGE);
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
					newim.setLore(ItemFact.getGunLore(g, attachment, temp, reloadAmount));
					newim.setDisplayName(att ? attachment.getDisplayName() : g.getDisplayName());
					temp.setItemMeta(newim);
					if (Main.enableVisibleAmounts)
						temp.setAmount(reloadAmount);
					player.getInventory().setItem(slot, temp);
					Main.sendHotbarGunAmmoCount(player, g, attachment, temp, false);
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

	public static boolean isBreakable(Block b, Location l) {
		if (b.getType().name().contains("GLASS"))
			return true;
		return false;

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
		if (b.getType().name().contains("BED_") || b.getType().name().contains("_BED")
				|| b.getType().name().contains("DAYLIGHT_DETECTOR")) {
			if (!Main.blockbullet_halfslabs && (l.getY() - l.getBlockY() > 0.5))
				return false;
			return true;
		}
		if (b.getType().name().contains("DOOR")) {
			if (Main.blockbullet_door)
				return true;
			return false;
		}
		if (b.getType().name().contains("GLASS")) {
			if (Main.blockbullet_glass)
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
