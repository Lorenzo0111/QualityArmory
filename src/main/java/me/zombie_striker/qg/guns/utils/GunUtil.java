package me.zombie_striker.qg.guns.utils;

import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.alessiodp.parties.api.interfaces.Party;
import com.alessiodp.parties.api.interfaces.PartyPlayer;
import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.ammo.Ammo;
import me.zombie_striker.qg.api.QAHeadShotEvent;
import me.zombie_striker.qg.api.QAWeaponDamageBlockEvent;
import me.zombie_striker.qg.api.QAWeaponDamageEntityEvent;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.armor.BulletProtectionUtil;
import me.zombie_striker.qg.boundingbox.AbstractBoundingBox;
import me.zombie_striker.qg.boundingbox.BoundingBoxManager;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.handlers.*;
import me.zombie_striker.qg.hooks.CoreProtectHook;
import me.zombie_striker.qg.hooks.protection.ProtectionHandler;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import ru.beykerykt.minecraft.lightapi.common.LightAPI;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class GunUtil {

	public static HashMap<UUID, BukkitTask> rapidfireshooters = new HashMap<>();
	public static HashMap<UUID, Double> highRecoilCounter = new HashMap<>();
	protected static HashMap<UUID, Location> AF_locs = new HashMap<>();
	protected static HashMap<UUID, BukkitTask> AF_tasks = new HashMap<>();

	public static void shootHandler(Gun g, Player p) {
		shootHandler(g, p, g.getBulletsPerShot());
	}

	public static void shootHandler(Gun g, Player p, int numberOfBullets) {
		double sway = g.getSway() * AimManager.getSway(g, p.getUniqueId());
		if (g.usesCustomProjctiles()) {
			for (int i = 0; i < numberOfBullets; i++) {
				Vector go = p.getLocation().getDirection().normalize();
				go.add(new Vector((Math.random() * 2 * sway) - sway, (Math.random() * 2 * sway) - sway,
						(Math.random() * 2 * sway) - sway)).normalize();
				Vector two = go.clone();// .multiply();
				g.getCustomProjectile().spawn(g, p.getEyeLocation(), p, two);
			}
		} else {
			shootInstantVector(g, p, sway, g.getDamage(), g.getBulletsPerShot(), g.getMaxDistance());
		}
	}

	public static double getTargetedSolidMaxDistance(Vector v, Location start, double maxDistance) {
		Location test = start.clone();
		Block previous = null;
		for (double i = 0; i < maxDistance; i += v.length()) {
			if (test.getBlock() == previous) {
				previous = test.getBlock();
				test.add(v);
				continue;
			}
			if (test.getBlock().getType() != Material.AIR) {
				if (isSolid(test.getBlock(), test))
					return start.distance(test);
			}
			previous = test.getBlock();
			test.add(v);
		}
		return maxDistance;
	}

	@SuppressWarnings("deprecation")
	public static void shootInstantVector(Gun g, Player p, double sway, double damage, int shots, int range) {
		boolean timingsReport = false;
		long time1 = System.currentTimeMillis();
		long time2 = 0;
		long time3 = 0;
		long time4point5 = 0;
		long time4 = 0;
		for (int i = 0; i < shots; i++) {
			Location start = p.getEyeLocation().clone();

			start.add(p.getVelocity());


			Vector normalizedDirection = p.getLocation().getDirection().normalize();
			normalizedDirection.add(new Vector((Math.random() * 2 * sway) - sway, (Math.random() * 2 * sway) - sway,
					(Math.random() * 2 * sway) - sway));
			normalizedDirection = normalizedDirection.normalize();
			Vector step = normalizedDirection.clone().multiply(QAMain.bulletStep);

			Entity hitTarget = null;
			AbstractBoundingBox hitBox = null;

			Location bulletHitLoc = null;

			double maxDistance = getTargetedSolidMaxDistance(step, start, range);
			double maxEntityDistance = maxDistance;
			double maxEntityDistanceSquared = maxDistance * maxDistance;

			List<Location> blocksThatWillPLAYBreak = new ArrayList<>();
			List<Block> blocksThatWillBreak = new ArrayList<>();

			Location centerTest = start.clone().add(normalizedDirection.clone().multiply(maxDistance / 2));

			for (Entity e : centerTest.getWorld().getNearbyEntities(centerTest, maxDistance / 2, maxDistance / 2,
					maxDistance / 2)) {
				if (e instanceof Damageable) {
					if (QAMain.avoidTypes.contains(e.getType()))
						continue;

					if (e != p && e != p.getVehicle() && e != p.getPassenger()) {
						double entityDistanceSquared = e.getLocation().distanceSquared(start);
						if (entityDistanceSquared >= maxEntityDistanceSquared)
							continue;
						double entityDistance = e.getLocation().distance(start);

						AbstractBoundingBox box = BoundingBoxManager.getBoundingBox(e);

						Location bulletLocationTest = start.clone();
						// If the entity is close to the line of fire.
						if (e instanceof Player) {
							Player player = (Player) e;
							if (player.getGameMode() == GameMode.SPECTATOR) {
								continue;
							}
							if (QAMain.hasParties && (!QAMain.friendlyFire)) {
								try {
									PartiesAPI api = Parties.getApi();
									PartyPlayer pp1 = api.getPartyPlayer(p.getUniqueId());
									PartyPlayer pp2 = api.getPartyPlayer(e.getUniqueId());
									if (pp1.getPartyId() != null && pp1.getPartyId().equals(pp2.getPartyId())) {
										Party party = api.getParty(pp1.getPartyId());
										if (party != null && party.isFriendlyFireProtected()) {
											continue;
										}
									}
								} catch (Error | Exception e43) {

								}
							}
						}
						// Clear this to make sure
						double checkDistanceMax = box.maximumCheckingDistance(e);
						double startDistance = Math.max(entityDistance - (checkDistanceMax), 0);

						//Get it somewhere close to the entity
						if (startDistance > 0)
							bulletLocationTest.add(normalizedDirection.clone().multiply(startDistance));

						for (double testDistance = startDistance; testDistance < entityDistance + (checkDistanceMax); testDistance += step.length()) {
							bulletLocationTest.add(step);
							if (box.intersects(p, bulletLocationTest, e)) {
								bulletHitLoc = bulletLocationTest;
								maxEntityDistance = entityDistance;
								maxEntityDistanceSquared = entityDistanceSquared;
								hitTarget = e;
								hitBox = box;
								//headShot = box.allowsHeadshots() ? box.intersectsHead(bulletLocationTest, e) : false;
								break;
							}
						}
					}
				}
			}
			time2 = System.currentTimeMillis();


			if (hitTarget != null) {
				if (QualityArmory.allowGunsInRegion(hitTarget.getLocation())) {

					boolean headshot = hitBox.allowsHeadshots() && hitBox.intersectsHead(bulletHitLoc, hitTarget);
					if (headshot) {
						QAMain.DEBUG("Headshot!");
						if (QAMain.headshotPling) {
							try {
								p.playSound(p.getLocation(), QAMain.headshot_sound, 2, 1);
								if (!QAMain.isVersionHigherThan(1, 9))
									try {
										p.playSound(p.getLocation(), Sound.valueOf("LAVA_POP"), 6, 1);
									} catch (Error | Exception h4) {
									}

							} catch (Error | Exception h4) {
								p.playSound(p.getLocation(), Sound.valueOf("LAVA_POP"), 1, 1);
							}
						}
					}

					boolean negateHeadshot = false;
					boolean bulletProtection = false;

					if (hitTarget instanceof Player) {
						bulletProtection = BulletProtectionUtil.stoppedBullet(p, bulletHitLoc, normalizedDirection);
						if (headshot) {
							negateHeadshot = BulletProtectionUtil.negatesHeadshot(p);
						}
					}

					double damageMAX = damage * (bulletProtection ? 0.1 : 1)
							* ((headshot && !negateHeadshot) ? (QAMain.HeadshotOneHit ? 50 * g.getHeadshotMultiplier() : g.getHeadshotMultiplier())
							: 1);


					QAWeaponDamageEntityEvent shootevent = new QAWeaponDamageEntityEvent(p, g, hitTarget, headshot,
							damage, bulletProtection);
					Bukkit.getPluginManager().callEvent(shootevent);
					if (!shootevent.isCancelled()) {
						if (headshot) {
							QAHeadShotEvent headshotevent = new QAHeadShotEvent(hitTarget, p, g);
							Bukkit.getPluginManager().callEvent(headshotevent);
							headshot = !headshotevent.isCancelled();
						}
						if (hitTarget instanceof Player) {
							Player player = (Player) hitTarget;
							if (!QAMain.enableArmorIgnore) {
								try {
									// damage = damage * ( 1 - min( 20, max( defensePoints / 5, defensePoints -
									// damage / ( toughness / 4 + 2 ) ) ) / 25 )
									double defensePoints = 0;
									double toughness = 0;
									for (ItemStack is : new ItemStack[]{player.getInventory().getHelmet(),
											player.getInventory().getChestplate(), player.getInventory().getLeggings(),
											player.getInventory().getBoots()}) {
										if (is != null) {
											Collection<AttributeModifier> attributes = is.getItemMeta().getAttributeModifiers(Attribute.GENERIC_ARMOR);
											Collection<AttributeModifier> toughnessAttributes = is.getItemMeta().getAttributeModifiers(Attribute.GENERIC_ARMOR_TOUGHNESS);

											if (attributes != null && !attributes.isEmpty())
												for (AttributeModifier a : attributes)
													defensePoints += a.getAmount();
											if (toughnessAttributes != null && !toughnessAttributes.isEmpty())
												for (AttributeModifier a : toughnessAttributes)
													toughness += a.getAmount();
										}
									}

									QAMain.DEBUG("Applied armor protection: " + defensePoints);

									damageMAX = damageMAX / (1 - Math.min(20, Math.max(defensePoints / 5,
											defensePoints - damageMAX / (toughness / 4 + 2))) / 25);
								} catch (Error | Exception e5) {
									QAMain.DEBUG("An error has occurred: " + e5.getMessage());
								}
							}

							if (!bulletProtection) {
								BulletWoundHandler.bulletHit((Player) hitTarget,
										g.getAmmoType() == null ? 1 : g.getAmmoType().getPiercingDamage());
							} else {
								hitTarget.sendMessage(QAMain.S_BULLETPROOFSTOPPEDBLEEDING);
							}
						}

						if (hitTarget instanceof LivingEntity) {
							((LivingEntity) hitTarget).setNoDamageTicks(0);
							QAMain.DEBUG("Damaging entity " + hitTarget.getName() + " ( "
									+ ((LivingEntity) hitTarget).getHealth() + "/"
									+ ((LivingEntity) hitTarget).getMaxHealth() + " :" + damageMAX + " DAM)");
						}
						if(QAMain.anticheatFix || p.hasMetadata("NPC")) {
							if (hitTarget instanceof Damageable) {
								((Damageable) hitTarget).damage(damageMAX);
							} else if (hitTarget instanceof EnderDragon) {
								((EnderDragon) hitTarget).damage(damageMAX);
							} else if (hitTarget instanceof EnderDragonPart) {
								((EnderDragonPart) hitTarget).damage(damageMAX);
							}
						}else {
							if (hitTarget instanceof Damageable) {
								((Damageable) hitTarget).damage(damageMAX, p);
							} else if (hitTarget instanceof EnderDragon) {
								((EnderDragon) hitTarget).damage(damageMAX, p);
							} else if (hitTarget instanceof EnderDragonPart) {
								((EnderDragonPart) hitTarget).damage(damageMAX, p);
							}
						}


						if (hitTarget.getPassenger() instanceof Damageable) {
							QAMain.DEBUG("Found a passenger (" + hitTarget.getPassenger().getName() + "). Damaging it.");

							QAWeaponDamageEntityEvent passengerShoot = new QAWeaponDamageEntityEvent(p, g, hitTarget.getPassenger(), false,
									damage, bulletProtection);
							Bukkit.getPluginManager().callEvent(passengerShoot);

							if (!passengerShoot.isCancelled()) {
								((Damageable) hitTarget.getPassenger()).damage(damageMAX, p);
							}
						}
					} else {
						if (hitTarget instanceof LivingEntity) {
							QAMain.DEBUG("Damaging entity CANCELED " + hitTarget.getName() + " ( "
									+ ((LivingEntity) hitTarget).getHealth() + "/"
									+ ((LivingEntity) hitTarget).getMaxHealth() + " :" + damageMAX + " DAM)");
						} else {
							QAMain.DEBUG("Damaging entity CANCELED " + hitTarget.getType() + ".");
						}
					}

				}
			} else {
				QAMain.DEBUG("No entities hit.");
			}
			time3 = System.currentTimeMillis();
			if (QAMain.enableBulletTrails) {
				List<Player> nonheard = start.getWorld().getPlayers();
				nonheard.remove(p);
				if (g.useMuzzleSmoke())
					ParticleHandlers.spawnMuzzleSmoke(p, start.clone().add(step.clone().multiply(7)));
				double distSqrt = maxEntityDistance;
				Vector stepSmoke = normalizedDirection.clone().multiply(QAMain.smokeSpacing);
				for (double dist = 0; dist < distSqrt; dist += QAMain.smokeSpacing) {
					start.add(stepSmoke);

					if (start.getBlock().getType() != Material.AIR) {
						boolean solid = isSolid(start.getBlock(), start);
						QAWeaponDamageBlockEvent blockevent = new QAWeaponDamageBlockEvent(p, g, start.getBlock());
						Bukkit.getPluginManager().callEvent(blockevent);
						if (!blockevent.isCancelled()) {
							if ((solid || isBreakable(start.getBlock(), start)) && !blocksThatWillPLAYBreak.contains(
									new Location(start.getWorld(), start.getBlockX(), start.getBlockY(), start.getBlockZ()))) {
								blocksThatWillPLAYBreak.add(
										new Location(start.getWorld(), start.getBlockX(), start.getBlockY(), start.getBlockZ()));
							}

							final Block block = start.getBlock();
							final Material type = block.getType();
							if ((QAMain.destructableBlocks.contains(type) || g.getBreakableMaterials().contains(type)) && ProtectionHandler.canBreak(start)) {
								blocksThatWillBreak.add(block);
							}
						}

						if (!solid) {
							continue;
						}
					}

					/*try {
						int control = 3;
						if (dist % control == 0) {
							List<Player> heard = new ArrayList<>();
							for (Player p2 : nonheard) {
								if (p2.getLocation().distanceSquared(start) <= (control * control * 4)) {
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
					}*/
					ParticleHandlers.spawnGunParticles(g, start);
				}

				final Map<Block,Material> regenBlocks = new HashMap<>();
				for (Block l : blocksThatWillBreak) {
					QAMain.DEBUG("Breaking " + l.getX() + " " + l.getY() + " " + l.getZ() + ": " + l.getType());
					QAWeaponDamageBlockEvent event = new QAWeaponDamageBlockEvent(p,g,l);
					Bukkit.getPluginManager().callEvent(event);
					if (!event.isCancelled()) {
						if (!l.getType().isAir()) regenBlocks.put(l,l.getType());
						if (QAMain.regenDestructableBlocksAfter > 0) {
							l.setType(Material.AIR);
						} else {
							l.breakNaturally();
						}
						CoreProtectHook.logBreak(l,p);
					}
				}

				if (QAMain.regenDestructableBlocksAfter > 0) {
					QAMain.DEBUG("Scheduling replacement of " + regenBlocks.size() + " blocks");
					new BukkitRunnable() {
						@Override
						public void run() {
							QAMain.DEBUG("Replacing " + regenBlocks.size() + " blocks");

							for (Block l : regenBlocks.keySet()) {
								l.setType(regenBlocks.get(l));
								CoreProtectHook.logPlace(l,p);
							}
						}
					}.runTaskLater(QAMain.getInstance(), QAMain.regenDestructableBlocksAfter * 20L);
				}
			}

			time4point5 = System.currentTimeMillis();
			// TODO: Do lights n stuff
			try {
				if (Bukkit.getPluginManager().getPlugin("LightAPI") != null) {
					if (p.getEyeLocation().getBlock().getLightLevel() < g.getLightOnShoot()) {
						final Location loc = p.getEyeLocation().clone();
						LightAPI.get().setLightLevel(loc.getWorld().getName(),loc.getBlockX(),loc.getBlockY(),loc.getBlockZ(), g.getLightOnShoot());
						new BukkitRunnable() {

							@Override
							public void run() {
								LightAPI.get().setLightLevel(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), 0);
							}
						}.runTaskLater(QAMain.getInstance(), 3);
					}
				}
			} catch (Error | Exception e5) {
			}

			// Breaking texture
			if (QAMain.blockBreakTexture)
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
			time4 = System.currentTimeMillis();
		}
		if (timingsReport) {
			System.out.println("time1 = " + time1);
			System.out.println("time2 = " + time2);
			System.out.println("time3 = " + time3);
			System.out.println("time3.5 = " + time4point5);
			System.out.println("time4 = " + time4);
		}
	}

	public static void basicShoot(boolean offhand, Gun g, Player player, double acc) {
		basicShoot(offhand, g, player, acc, 1, false);
	}
	public static void basicShoot(boolean offhand, Gun g, Player player, double acc, boolean holdingRMB) {
		basicShoot(offhand, g, player, acc, 1, holdingRMB);
	}

	public static void basicShoot(boolean offhand, final Gun g, final Player player, final double acc, int times) {
		basicShoot(offhand,g,player,acc,times,false);
	}
	@SuppressWarnings("deprecation")
	public static void basicShoot(boolean offhand, final Gun g, final Player player, final double acc, int times, boolean holdingRMB) {
		QAMain.DEBUG("About to shoot!");

		if (g.getChargingHandler() != null && (g.getChargingHandler().isCharging(player)
				|| (g.getReloadingingHandler() != null && g.getReloadingingHandler().isReloading(player))))
			return;

		if (isDelay(g,player)) {
			QAMain.DEBUG("Shooting canceled due to last shot being too soon.");
			return;
		}
		g.getLastShotForGun().put(player.getUniqueId(), System.currentTimeMillis());


		if (rapidfireshooters.containsKey(player.getUniqueId())) {
			QAMain.DEBUG("Shooting canceled due to rapid fire being enabled.");
			return;
		}

		ItemStack firstGunInstance = IronsightsHandler.getItemAiming(player);

		boolean regularshoot = true;

		if (g.getChargingHandler() != null) {
			QAMain.DEBUG("Charging shoot debug: " + g.getName() + " = " + (g.getChargingHandler() == null ? "null"
					: g.getChargingHandler().getName()));
			regularshoot = g.getChargingHandler().shoot(g, player, firstGunInstance);
		}

		if (regularshoot) {
			QAMain.DEBUG("Handling shoot and gun damage.");
			GunUtil.shootHandler(g, player);
			playShoot(g, player);
			if (QAMain.enableRecoil)
				addRecoil(player, g);
		}

		if (g.isAutomatic()) {
			rapidfireshooters.put(player.getUniqueId(), new BukkitRunnable() {
				int slotUsed = player.getInventory().getHeldItemSlot();

				@Override
				public void run() {
					if (!player.isOnline()) {
						QAMain.DEBUG("Stopping Automatic Firing because player left");
						rapidfireshooters.remove(player.getUniqueId());
						cancel();
						return;
					}

					if ((g.getChargingHandler() != null && g.getChargingHandler().isCharging(player)) || GunRefillerRunnable.isReloading(player)) {
						QAMain.DEBUG("Cancelling rapid fire shoot due to charging or reloading.");
						rapidfireshooters.remove(player.getUniqueId());
						cancel();
						return;
					}

					if(!holdingRMB){
						if ((!g.hasIronSights() || !IronsightsHandler.isAiming(player)) && ((player.isSneaking() == QAMain.SwapSneakToSingleFire))) {
							cancel();
							QAMain.DEBUG("Stopping Automatic Firing");
							rapidfireshooters.remove(player.getUniqueId());
							return;
						}
					}

					ItemStack temp = IronsightsHandler.getItemAiming(player);

					if (QAMain.enableDurability && g.getDamage(temp) <= 0) {
						player.playSound(player.getLocation(), WeaponSounds.METALHIT.getSoundName(), 1, 1);
						rapidfireshooters.remove(player.getUniqueId());
						QAMain.DEBUG("Canceld due to weapon durability = " + g.getDamage(temp));
						cancel();
						return;
					}

					int amount = Gun.getAmount(player);
					if(holdingRMB && !QAMain.SWAP_TO_LMB_SHOOT){
						if(System.currentTimeMillis()-g.getLastTimeRMB(player) > 310){
							rapidfireshooters.remove(player.getUniqueId());
							cancel();
							return;
						}
					}

					if (((QAMain.SWAP_TO_LMB_SHOOT && player.isSneaking() == QAMain.SwapSneakToSingleFire)) || slotUsed != player.getInventory().getHeldItemSlot() || amount <= 0) {
						rapidfireshooters.remove(player.getUniqueId());
						cancel();
						return;
					}

					boolean regularshoot = true;
					if (g.getChargingHandler() != null && (!g.getChargingHandler().isCharging(player)
							&& (g.getReloadingingHandler() == null || !g.getReloadingingHandler().isReloading(player)))) {
						regularshoot = g.getChargingHandler().shoot(g, player, temp);
						QAMain.DEBUG(
								"Charging (rapidfire) shoot debug: " + g.getName() + " = " + (g.getChargingHandler() == null
										? "null"
										: g.getChargingHandler().getName()));
					}
					if (regularshoot) {
						GunUtil.shootHandler(g, player);
						playShoot(g, player);
						if (QAMain.enableRecoil)
							addRecoil(player, g);
						// TODO: recoil
					}


					amount--;

					if (amount < 0)
						amount = 0;

					int slot;
					if (offhand) {
						slot = -1;
					} else {
						slot = player.getInventory().getHeldItemSlot();
					}
					Gun.updateAmmo(g, player.getItemInHand(), amount);
					if(QAMain.showAmmoInXPBar){
						updateXPBar(player,g,amount);
					}

					if (slot == -1) {
						try {
							if (QualityArmory.isIronSights(player.getItemInHand())) {
								player.getInventory().setItemInOffHand(temp);
								QAMain.DEBUG("Sett Offhand because ironsights in main hand");
							} else {
								player.getInventory().setItemInHand(temp);
								QAMain.DEBUG("Set mainhand because ironsights not in main hand");
							}

						} catch (Error e) {
						}
					} else {
						ItemStack tempCheck = player.getInventory().getItem(slot);
						if (QualityArmory.isIronSights(tempCheck)) {
							CustomBaseObject tempBase = QualityArmory.getCustomItem(Update19OffhandChecker.getItemStackOFfhand(player));
							if (tempBase != null && tempBase == g) {
								Update19OffhandChecker.setOffhand(player, temp);
							}
						} else {
							player.getInventory().setItem(slot, temp);
						}
					}
					QualityArmory.sendHotbarGunAmmoCount(player, g, temp, false);
				}
			}.runTaskTimer(QAMain.getInstance(), 10 / g.getFireRate(), 10 / g.getFireRate()));
		}

		int amount = Gun.getAmount(player) - 1;

		if (amount < 0)
			amount = 0;

		int slot;
		if (offhand) {
			slot = -1;
		} else {
			slot = player.getInventory().getHeldItemSlot();
		}

		QAMain.DEBUG("Ammo amount: " + amount);
		QAMain.DEBUG("Slot: " + slot);
		if (slot == -1) {
			try {
				if (QualityArmory.isIronSights(player.getItemInHand())) {
					player.getInventory().setItemInOffHand(firstGunInstance);
					QAMain.DEBUG("Sett Offhand because ironsights in main hand");
				} else {
					player.getInventory().setItemInHand(firstGunInstance);
					QAMain.DEBUG("Set mainhand because ironsights not in main hand");
				}
			} catch (Error e) {
			}
		} else {
			player.getInventory().setItem(slot, firstGunInstance);
		}
		Gun.updateAmmo(g, player, amount);
		QAMain.DEBUG("New ammo: " + Gun.getAmount(player));
	}

	public static void updateXPBar(Player player, Gun g, int amount) {
		player.setLevel(amount);
		// Todo exp
	}

	public static void playShoot(final Gun g, final Player player) {
		g.damageDurability(player);
		new BukkitRunnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				try {
					String soundname = null;
					if (g.getWeaponSounds().size() > 1) {
						soundname = g.getWeaponSounds()
								.get(ThreadLocalRandom.current().nextInt(g.getWeaponSounds().size()));
					} else {
						soundname = g.getWeaponSound();
					}
					player.getWorld().playSound(player.getLocation(), soundname, (float) g.getVolume(), 1);
					if (!QAMain.isVersionHigherThan(1, 9)) {
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
		}.runTaskLater(QAMain.getInstance(), 1);
		// Simply delaying the sound by 1/20th of a second makes shooting so much more
		// immersive
	}

	public static boolean hasAmmo(Player player, Gun g) {
		return QualityArmory.getAmmoInInventory(player, g.getAmmoType()) > 0;
	}

	public static void basicReload(final Gun g, final Player player, boolean doNotRemoveAmmo) {
		basicReload(g, player, doNotRemoveAmmo, 1.5);
	}

	public static void basicReload(final Gun g, final Player player, boolean doNotRemoveAmmo, double seconds) {

		final ItemStack temp = player.getInventory().getItemInHand();
		ItemMeta im = temp.getItemMeta();

		if (Gun.getAmount(player) == g.getMaxBullets()) {
			return;
		}
		if (im == null || !im.hasDisplayName())
			return;
		if (im.getLore() != null && im.getDisplayName().contains(QAMain.S_RELOADING_MESSAGE)) {

		} else {
			try {
				player.getWorld().playSound(player.getLocation(), WeaponSounds.RELOAD_MAG_OUT.getSoundName(), 1, 1f);
			} catch (Error e2) {
				try {
					player.getWorld().playSound(player.getLocation(), Sound.valueOf("CLICK"), 5, 1);
				} catch (Error | Exception e3) {
					player.getWorld().playSound(player.getLocation(), Sound.valueOf("BLOCK_LEVER_CLICK"), 5, 1);
				}
			}
			final int slot = player.getInventory().getHeldItemSlot();

			Ammo ammo = g.getAmmoType();

			final int initialAmount = Gun.getAmount(player);
			final int reloadAmount = doNotRemoveAmmo ? g.getMaxBullets()
					: Math.min(g.getMaxBullets(), initialAmount + QualityArmory.getAmmoInInventory(player, ammo));
			final int subtractAmount = reloadAmount - initialAmount;

			if (g.getReloadingingHandler() != null) {
				seconds = g.getReloadingingHandler().reload(player, g, subtractAmount);
			}
			QAMain.toggleNightvision(player, g, false);
			//Gun.updateAmmo(g, im, initialAmount);
			im.setDisplayName(g.getDisplayName() + QAMain.S_RELOADING_MESSAGE);
			temp.setItemMeta(im);
			player.getInventory().setItem(slot, temp);

			if(QAMain.showAmmoInXPBar){
				updateXPBar(player,g,0);
			}
			new GunRefillerRunnable(player, temp, g, slot, initialAmount, reloadAmount, seconds, ammo, subtractAmount, !doNotRemoveAmmo);

		}

	}

	public static void addRecoil(final Player player, final Gun g) {
		if (g.getRecoil() == 0)
			return;
		if (g.getFireRate() >= 4) {
			if (highRecoilCounter.containsKey(player.getUniqueId())) {
				highRecoilCounter.put(player.getUniqueId(),
						highRecoilCounter.get(player.getUniqueId()) + g.getRecoil());
			} else {
				highRecoilCounter.put(player.getUniqueId(), g.getRecoil());
				new BukkitRunnable() {
					@Override
					public void run() {
						if (QAMain.hasProtocolLib && QAMain.isVersionHigherThan(1, 13) && !QAMain.hasViaVersion) {
							addRecoilWithProtocolLib(player, g, true);
						} else
							addRecoilWithTeleport(player, g, true);
					}
				}.runTaskLater(QAMain.getInstance(), 3);
			}
		} else {
			if (QAMain.hasProtocolLib && QAMain.isVersionHigherThan(1, 13)) {
				addRecoilWithProtocolLib(player, g, false);
			} else
				addRecoilWithTeleport(player, g, false);
		}
	}

	private static void addRecoilWithProtocolLib(Player player, Gun g, boolean useHighRecoil) {
		Vector newDir = player.getLocation().getDirection();
		newDir.normalize()
				.setY(newDir.getY()
						+ ((useHighRecoil ? highRecoilCounter.get(player.getUniqueId()) : g.getRecoil()) / 30))
				.multiply(20);
		if (useHighRecoil)
			highRecoilCounter.remove(player.getUniqueId());
		ProtocolLibHandler.sendYawChange(player, newDir);
	}

	private static void addRecoilWithTeleport(Player player, Gun g, boolean useHighRecoil) {
		Location tempCur = (QAMain.recoilHelperMovedLocation.get(player.getUniqueId()));
		final Location current;
		if (tempCur == null) {
			current = player.getLocation();
		} else {
			current = tempCur;
		}
		Vector movementOffset = player.getVelocity().multiply(0.2);
		if (movementOffset.getY() > -0.1 && movementOffset.getY() < 0)
			movementOffset.setY(0);
		current.add(movementOffset);
		current.setPitch((float) (current.getPitch()
				- (useHighRecoil ? highRecoilCounter.get(player.getUniqueId()) : g.getRecoil())));
		if (useHighRecoil)
			highRecoilCounter.remove(player.getUniqueId());
		Vector temp = player.getVelocity();
		// player.getLocation().setDirection(vector);
		player.teleport(current);
		player.setVelocity(temp);
	}

	public static boolean isBreakable(Block b, Location l) {
		if (b.getType().name().contains("GLASS"))
			return true;
		return false;

	}

	@SuppressWarnings("deprecation")
	public static boolean isSolid(Block b, Location l) {
		return BlockCollisionUtil.isSolid(b, l);
	}

	@NotNull
	public static String locationToString(@NotNull Location l) {
		return "X: " + l.getX() + " Y: " + l.getY() + " Z: " + l.getZ();
	}

	public static boolean isDelay(Gun g, Player player) {
		int showdelay = ((int) (g.getDelayBetweenShotsInSeconds() * 1000));

		 return (g.getLastShotForGun().containsKey(player.getUniqueId())
				&& (System.currentTimeMillis() - g.getLastShotForGun().get(player.getUniqueId()) < showdelay));
	}
}
