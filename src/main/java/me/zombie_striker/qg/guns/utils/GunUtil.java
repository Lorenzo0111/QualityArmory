package me.zombie_striker.qg.guns.utils;

import org.bukkit.scheduler.BukkitTask;
import me.zombie_striker.qg.utils.FoliaRunnable;
import me.zombie_striker.qg.items.ItemState;
import me.zombie_striker.qg.utils.Compat;
import me.zombie_striker.qg.utils.ErrorReporter;

import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.alessiodp.parties.api.interfaces.Party;
import com.alessiodp.parties.api.interfaces.PartyPlayer;
import com.cryptomorin.xseries.XAttribute;
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
import me.zombie_striker.qg.utils.BlockRegenData;
import org.bukkit.*;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import ru.beykerykt.minecraft.lightapi.common.LightAPI;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class GunUtil {

	/**
	 * Stops the automatic fire of a player. The task must always be removed from the rapid fire tasks when it is
	 * cancelled, or the player will not be able to shoot anymore.
	 */
	public static void stopRapidFire(Player player) {
		BukkitTask task = QAMain.getPlayerState().getRapidFireTasks().remove(player.getUniqueId());
		if (task != null)
			task.cancel();
	}
	private static final double MIN_POSITIVE_STEP = 0.001;


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
		World world = test.getWorld();
		Block block = null;
		boolean air = true;
		for (double i = 0; i < maxDistance; i += v.length()) {
			if (block == null || block.getX() != test.getBlockX() || block.getY() != test.getBlockY()
					|| block.getZ() != test.getBlockZ()) {
				if (!world.isChunkLoaded(test.getBlockX() >> 4, test.getBlockZ() >> 4))
					return start.distance(test);
				block = test.getBlock();
				air = block.getType() == Material.AIR;
			}
			if (!air && isSolid(block, test))
				return start.distance(test);
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
			double bulletStep = Math.max(QAMain.getConfiguration().weapons.detection.bulletStep, MIN_POSITIVE_STEP);
			Vector step = normalizedDirection.clone().multiply(bulletStep);

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
					if (QAMain.getConfiguration().weapons.impenetrableEntities.contains(e.getType()))
						continue;

					if (e != p && e != p.getVehicle() && e != p.getPassenger()) {
						double entityDistanceSquared = e.getLocation().distanceSquared(start);
						if (entityDistanceSquared >= maxEntityDistanceSquared)
							continue;
						double entityDistance = e.getLocation().distance(start);

						AbstractBoundingBox box = BoundingBoxManager.getBoundingBox(e);

						Location bulletLocationTest = start.clone();
						// If the entity is close to the line of fire.

						if (QAMain.getConfiguration().interactions.preventHiddenPlayersHit && !canSee(p, e)) {
							QAMain.DEBUG("Prevented shooting a hidden player: " + e.getName());
							continue;
						}

						if (e instanceof Player) {
							Player player = (Player) e;
							if (player.getGameMode() == GameMode.SPECTATOR) {
								continue;
							}
							if (QAMain.getIntegrations().hasParties() && (!QAMain.getConfiguration().compat.friendlyFire)) {
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
								} catch (RuntimeException | LinkageError e43) {
									ErrorReporter.report("checking Parties friendly fire", e43);
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
						if (QAMain.getConfiguration().weapons.headshot.playNotificationSound) {
							playNotificationSound(p, QAMain.getConfiguration().weapons.headshot.notificationSound);
						}
					} else if (QAMain.getConfiguration().weapons.hitSound.enabled) {
						playNotificationSound(p, QAMain.getConfiguration().weapons.hitSound.sound);
					}

					boolean negateHeadshot = false;
					boolean bulletProtection = false;

					if (hitTarget instanceof Player) {
						bulletProtection = BulletProtectionUtil.stoppedBullet((Player) hitTarget, bulletHitLoc, normalizedDirection);
						if (headshot) {
							negateHeadshot = BulletProtectionUtil.negatesHeadshot((Player) hitTarget);
						}
					}

                    double damageMAX = damage * (bulletProtection ? 0.1 : 1)
                            * ((headshot && !negateHeadshot) ? (
                            QAMain.getConfiguration().weapons.headshot.instantKill && !QAMain.getConfiguration().weapons.headshot.blacklist.contains(hitTarget.getType())
                                    ? 50 * g.getHeadshotMultiplier() : g.getHeadshotMultiplier())
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
							if (!QAMain.getConfiguration().features.enableIgnoreArmorProtection && Compat.ITEM_ATTRIBUTES) {
								try {
									// damage = damage * ( 1 - min( 20, max( defensePoints / 5, defensePoints -
									// damage / ( toughness / 4 + 2 ) ) ) / 25 )
									double defensePoints = 0;
									double toughness = 0;
									for (ItemStack is : new ItemStack[]{player.getInventory().getHelmet(),
											player.getInventory().getChestplate(), player.getInventory().getLeggings(),
											player.getInventory().getBoots()}) {
										if (is != null && is.hasItemMeta()) {
											Collection<AttributeModifier> attributes = is.getItemMeta().getAttributeModifiers(XAttribute.ARMOR.get());
											Collection<AttributeModifier> toughnessAttributes = is.getItemMeta().getAttributeModifiers(XAttribute.ARMOR_TOUGHNESS.get());

											if (attributes != null && !attributes.isEmpty())
												for (AttributeModifier a : attributes)
													defensePoints += a.getAmount();
											if (toughnessAttributes != null && !toughnessAttributes.isEmpty())
												for (AttributeModifier a : toughnessAttributes)
													toughness += a.getAmount();
										}
									}

									QAMain.DEBUG("Applied armor protection: " + defensePoints);

									damageMAX = damageMAX * Math.max(0, 1 - QAMain.getConfiguration().combat.armorEffectiveness * Math.min(20, Math.max(defensePoints / 5,
											defensePoints - damageMAX / (toughness / 4 + 2))) / 25);
								} catch (RuntimeException e5) {
									ErrorReporter.report("applying armor protection", e5);
								}
							}

							if (!bulletProtection) {
								BulletWoundHandler.bulletHit((Player) hitTarget,
										g.getAmmoType() == null ? 1 : g.getAmmoType().getPiercingDamage());
							} else {
								hitTarget.sendMessage(QAMain.getMessages().bleeding.protectedByKevlar);
							}
						}

						if (hitTarget instanceof LivingEntity) {
							((LivingEntity) hitTarget).setNoDamageTicks(0);
							QAMain.DEBUG("Damaging entity " + hitTarget.getName() + " ( "
									+ ((LivingEntity) hitTarget).getHealth() + "/"
									+ ((LivingEntity) hitTarget).getMaxHealth() + " :" + damageMAX + " DAM)");
						}
						if (hitTarget instanceof Damageable) {
							GunDamageHandler.damage((Damageable) hitTarget, damageMAX,
									(QAMain.getConfiguration().features.anticheatFix || p.hasMetadata("NPC")) ? null : p, true);
						}


						if (hitTarget.getPassenger() instanceof Damageable) {
							QAMain.DEBUG("Found a passenger (" + hitTarget.getPassenger().getName() + "). Damaging it.");

							QAWeaponDamageEntityEvent passengerShoot = new QAWeaponDamageEntityEvent(p, g, hitTarget.getPassenger(), false,
									damage, bulletProtection);
							Bukkit.getPluginManager().callEvent(passengerShoot);

							if (!passengerShoot.isCancelled()) {
								GunDamageHandler.damage((Damageable) hitTarget.getPassenger(), damageMAX, p, true);
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
			if (QAMain.getConfiguration().features.enableBulletTrails) {
				List<Player> nonheard = start.getWorld().getPlayers();
				nonheard.remove(p);
				if (g.useMuzzleSmoke())
					ParticleHandlers.spawnMuzzleSmoke(p, start.clone().add(step.clone().multiply(7)));
				double distSqrt = maxEntityDistance;
				double smokeSpacing = Math.max(QAMain.getConfiguration().features.bulletTrailsSpacing, MIN_POSITIVE_STEP);
				Vector stepSmoke = normalizedDirection.clone().multiply(smokeSpacing);
				for (double dist = 0; dist < distSqrt; dist += smokeSpacing) {
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
							if ((QAMain.getBlockRules().isDestructable(type) || g.getBreakableMaterials().contains(type)) && ProtectionHandler.canBreak(start)) {
								blocksThatWillBreak.add(block);
							}
						}

						if (!solid) {
							continue;
						}
					}

					ParticleHandlers.spawnGunParticles(g, start);
				}

				final Map<Block, BlockRegenData> regenBlocks = new HashMap<>();
				for (Block l : blocksThatWillBreak) {
					QAMain.DEBUG("Breaking " + l.getX() + " " + l.getY() + " " + l.getZ() + ": " + l.getType());
					QAWeaponDamageBlockEvent event = new QAWeaponDamageBlockEvent(p,g,l);
					Bukkit.getPluginManager().callEvent(event);
					if (!event.isCancelled()) {
						if (!l.getType().equals(Material.AIR)) regenBlocks.put(l,new BlockRegenData(l));
						if (QAMain.getConfiguration().world.regenDestructableBlocksAfter > 0) {
							l.setType(Material.AIR);
						} else {
							l.breakNaturally();
						}
						CoreProtectHook.logBreak(l,p);
					}
				}

				if (QAMain.getConfiguration().world.regenDestructableBlocksAfter > 0) {
					QAMain.DEBUG("Scheduling replacement of " + regenBlocks.size() + " blocks");
					new FoliaRunnable() {
						@Override
						public void run() {
							QAMain.DEBUG("Replacing " + regenBlocks.size() + " blocks");

							for (Block l : regenBlocks.keySet()) {
								final Block block = l;
								final BlockRegenData data = regenBlocks.get(l);
								FoliaRunnable.runRegionTask(QAMain.getInstance(), block.getLocation(), () -> {
									data.place(block.getLocation());
									CoreProtectHook.logPlace(block, p);
								});
							}
						}
					}.runTaskLater(QAMain.getInstance(), QAMain.getConfiguration().world.regenDestructableBlocksAfter * 20L);
				}
			}

			time4point5 = System.currentTimeMillis();
			// TODO: Do lights n stuff
			try {
				if (Bukkit.getPluginManager().getPlugin("LightAPI") != null) {
					if (p.getEyeLocation().getBlock().getLightLevel() < g.getLightOnShoot()) {
						final Location loc = p.getEyeLocation().clone();
						LightAPI.get().setLightLevel(loc.getWorld().getName(),loc.getBlockX(),loc.getBlockY(),loc.getBlockZ(), g.getLightOnShoot());
						new FoliaRunnable() {

							@Override
							public void run() {
								LightAPI.get().setLightLevel(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), 0);
							}
						}.runTaskLater(QAMain.getInstance(), loc, 3);
					}
				}
			} catch (RuntimeException | LinkageError e5) {
				ErrorReporter.report("lighting the muzzle flash with LightAPI", e5);
			}

			// Breaking texture
			if (QAMain.getConfiguration().features.breakBlockTextureIfShot)
				for (@SuppressWarnings("unused")
						Location l : blocksThatWillPLAYBreak) {
					start.getWorld().playSound(start, SoundHandler.getSoundWhenShot(start.getBlock()), 2, 1);
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


		if (QAMain.getPlayerState().getRapidFireTasks().containsKey(player.getUniqueId())) {
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
			if (QAMain.getConfiguration().weapons.enableRecoil)
				addRecoil(player, g);
		}

		if (g.isAutomatic()) {
			QAMain.getPlayerState().getRapidFireTasks().put(player.getUniqueId(), new FoliaRunnable() {
				int slotUsed = player.getInventory().getHeldItemSlot();

				@Override
				public void run() {
					if (!player.isOnline()) {
						QAMain.DEBUG("Stopping Automatic Firing because player left");
						QAMain.getPlayerState().getRapidFireTasks().remove(player.getUniqueId());
						cancel();
						return;
					}

					if ((g.getChargingHandler() != null && g.getChargingHandler().isCharging(player)) || GunRefillerRunnable.isReloading(player)) {
						QAMain.DEBUG("Cancelling rapid fire shoot due to charging or reloading.");
						QAMain.getPlayerState().getRapidFireTasks().remove(player.getUniqueId());
						cancel();
						return;
					}

					if(!holdingRMB){
						if ((!g.hasIronSights() || !IronsightsHandler.isAiming(player)) && ((player.isSneaking() == QAMain.getConfiguration().weapons.ironsights.swapSneakToSingleFire))) {
							cancel();
							QAMain.DEBUG("Stopping Automatic Firing");
							QAMain.getPlayerState().getRapidFireTasks().remove(player.getUniqueId());
							return;
						}
					}

					ItemStack temp = IronsightsHandler.getItemAiming(player);

					if (QAMain.getConfiguration().weapons.durability && g.getDamage(temp) <= 0) {
						player.playSound(player.getLocation(), WeaponSounds.METALHIT.getSoundName(), 1, 1);
						QAMain.getPlayerState().getRapidFireTasks().remove(player.getUniqueId());
						QAMain.DEBUG("Canceld due to weapon durability = " + g.getDamage(temp));
						cancel();
						return;
					}

					int amount = Gun.getAmount(player);
					if(holdingRMB && QAMain.getConfiguration().features.swapReloadAndShootingControls){
						if(System.currentTimeMillis()-g.getLastTimeRMB(player) > 310){
							QAMain.getPlayerState().getRapidFireTasks().remove(player.getUniqueId());
							cancel();
							return;
						}
					}

					if (((!QAMain.getConfiguration().features.swapReloadAndShootingControls && player.isSneaking() == QAMain.getConfiguration().weapons.ironsights.swapSneakToSingleFire)) || slotUsed != player.getInventory().getHeldItemSlot() || amount <= 0) {
						QAMain.getPlayerState().getRapidFireTasks().remove(player.getUniqueId());
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
						if (QAMain.getConfiguration().weapons.enableRecoil)
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
                    if (QualityArmory.isIronSights(player.getItemInHand()))
                        Gun.updateAmmo(g, player.getItemInHand(), amount);
                    else Gun.updateAmmo(g, temp, amount);
					if(QAMain.getConfiguration().ui.showAmmoInXpBar){
						updateXPBar(player,g,amount);
					}

					if (slot == -1) {
						if (Compat.OFFHAND && QualityArmory.isIronSights(player.getItemInHand())) {
							player.getInventory().setItemInOffHand(temp);
							QAMain.DEBUG("Sett Offhand because ironsights in main hand");
							refreshIronsightsLore(player, g, amount);
						} else if (Compat.OFFHAND) {
							player.getInventory().setItemInHand(temp);
							QAMain.DEBUG("Set mainhand because ironsights not in main hand");
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
			}.runTaskTimer(QAMain.getInstance(), player, 10 / g.getFireRate(), 10 / g.getFireRate()));
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
			if (Compat.OFFHAND && QualityArmory.isIronSights(player.getItemInHand())) {
				player.getInventory().setItemInOffHand(firstGunInstance);
				QAMain.DEBUG("Sett Offhand because ironsights in main hand");
			} else if (Compat.OFFHAND) {
				player.getInventory().setItemInHand(firstGunInstance);
				QAMain.DEBUG("Set mainhand because ironsights not in main hand");
			}
		} else {
			player.getInventory().setItem(slot, firstGunInstance);
		}
		Gun.updateAmmo(g, player, amount);
		QAMain.DEBUG("New ammo: " + Gun.getAmount(player));

		refreshIronsightsLore(player, g, amount);
	}

	/**
	 * Re-applies the ammo lore to the gun in the offhand while aiming, which replays the item's
	 * "up and down" animation.
	 */
	private static void refreshIronsightsLore(Player player, Gun g, int amount) {
		if (!Compat.OFFHAND || !QAMain.getConfiguration().features.enableLoreGunBullets
				|| !QualityArmory.isIronSights(player.getItemInHand()))
			return;
		try {
			Gun.updateAmmoLore(g, player.getInventory().getItemInOffHand(), amount);
		} catch (RuntimeException e) {
			ErrorReporter.report("updating the lore of the aimed gun", e);
		}
	}

	/**
	 * Whether the shooter can see the entity. Before 1.18 only players can be hidden.
	 */
	private static boolean canSee(Player shooter, Entity entity) {
		if (Compat.CAN_SEE_ENTITY)
			return shooter.canSee(entity);
		return !(entity instanceof Player) || shooter.canSee((Player) entity);
	}

	/**
	 * Plays the headshot or hit notification sound. 1.8 can't play sounds by name, so it also plays the
	 * vanilla lava pop sound, as before.
	 */
	private static void playNotificationSound(Player player, String sound) {
		if (Compat.PLAYER_SOUND_BY_NAME) {
			player.playSound(player.getLocation(), sound, 2, 1);
			if (!Compat.MODERN_SOUNDS)
				player.playSound(player.getLocation(), Sound.valueOf("LAVA_POP"), 6, 1);
		} else {
			player.playSound(player.getLocation(), Sound.valueOf("LAVA_POP"), 1, 1);
		}
	}

	public static void updateXPBar(Player player, Gun g, int amount) {
		if (g == null || player == null) return;
		player.setLevel(amount);

		if (amount > 0) {
			player.setExp(Math.min((float) amount / (float) g.getMaxBullets(), 1));
		} else {
			player.setExp(0);
		}
	}

	public static void playShoot(final Gun g, final Player player) {
		g.damageDurability(player);
		new FoliaRunnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				if (Compat.WORLD_SOUND_BY_NAME) {
					String soundname;
					if (g.getWeaponSounds().size() > 1) {
						soundname = g.getWeaponSounds()
								.get(ThreadLocalRandom.current().nextInt(g.getWeaponSounds().size()));
					} else {
						soundname = g.getWeaponSound();
					}
					player.getWorld().playSound(player.getLocation(), soundname, (float) g.getVolume(), 1);
				} else {
					// 1.8 can't play the custom gun sounds
					player.getWorld().playSound(player.getLocation(), Sound.valueOf("CLICK"), 5, 1);
					player.getWorld().playSound(player.getLocation(), Sound.valueOf("WITHER_SHOOT"), 8, 2);
					player.getWorld().playSound(player.getLocation(), Sound.valueOf("EXPLODE"), 8, 2f);
				}

			}
		}.runTaskLater(QAMain.getInstance(), player, 1);
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
		if (im.getLore() != null && Gun.isReloading(temp)) {

		} else {
			if (Compat.WORLD_SOUND_BY_NAME)
				player.getWorld().playSound(player.getLocation(), WeaponSounds.RELOAD_MAG_OUT.getSoundName(), 1, 1f);
			else
				player.getWorld().playSound(player.getLocation(), Sound.valueOf("CLICK"), 5, 1);
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
			im.setDisplayName(g.getDisplayName() + QAMain.getMessages().weapon.reloading);
			temp.setItemMeta(im);
			ItemState.setReloading(temp, true);
			player.getInventory().setItem(slot, temp);

			if(QAMain.getConfiguration().ui.showAmmoInXpBar){
				updateXPBar(player,g,0);
			}
			new GunRefillerRunnable(player, temp, g, slot, initialAmount, reloadAmount, seconds, ammo, subtractAmount, !doNotRemoveAmmo);

		}

	}

	public static void addRecoil(final Player player, final Gun g) {
		if (g.getRecoil() == 0)
			return;
		if (g.getFireRate() >= 4) {
			if (QAMain.getPlayerState().getRecoilCounter().containsKey(player.getUniqueId())) {
				QAMain.getPlayerState().getRecoilCounter().put(player.getUniqueId(),
						QAMain.getPlayerState().getRecoilCounter().get(player.getUniqueId()) + g.getRecoil());
			} else {
				QAMain.getPlayerState().getRecoilCounter().put(player.getUniqueId(), g.getRecoil());
				new FoliaRunnable() {
					@Override
					public void run() {
						if (QAMain.getIntegrations().hasProtocolLib() && QAMain.isVersionHigherThan(1, 13) && !QAMain.getIntegrations().hasViaVersion()) {
							addRecoilWithProtocolLib(player, g, true);
						} else
							addRecoilWithTeleport(player, g, true);
					}
				}.runTaskLater(QAMain.getInstance(), player, 3);
			}
		} else {
			if (QAMain.getIntegrations().hasProtocolLib() && QAMain.isVersionHigherThan(1, 13)) {
				addRecoilWithProtocolLib(player, g, false);
			} else
				addRecoilWithTeleport(player, g, false);
		}
	}

	private static void addRecoilWithProtocolLib(Player player, Gun g, boolean useHighRecoil) {
		Vector newDir = player.getLocation().getDirection();
		newDir.normalize()
				.setY(newDir.getY()
						+ ((useHighRecoil ? QAMain.getPlayerState().getRecoilCounter().get(player.getUniqueId()) : g.getRecoil()) / 30))
				.multiply(20);
		if (useHighRecoil)
			QAMain.getPlayerState().getRecoilCounter().remove(player.getUniqueId());
		ProtocolLibHandler.sendYawChange(player, newDir);
	}

	private static void addRecoilWithTeleport(Player player, Gun g, boolean useHighRecoil) {
		Location tempCur = QAMain.getConfiguration().weapons.recoil.usePlayerMove ? (QAMain.getPlayerState().getLastMoveLocation().get(player.getUniqueId())) : null;
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
				- (useHighRecoil ? QAMain.getPlayerState().getRecoilCounter().get(player.getUniqueId()) : g.getRecoil())));
		if (useHighRecoil)
			QAMain.getPlayerState().getRecoilCounter().remove(player.getUniqueId());
		final Vector temp = player.getVelocity();
		// On Folia the teleport completes later, so the recoil state is only updated once it succeeded.
		FoliaRunnable.teleport(player, current).thenAccept(success -> {
			if (!success)
				return;
			if (!FoliaRunnable.isFolia()) {
				player.setVelocity(temp);
				QAMain.getPlayerState().getLastMoveLocation().put(player.getUniqueId(), current);
				return;
			}
			FoliaRunnable.runEntityTask(QAMain.getInstance(), player, () -> {
				player.setVelocity(temp);
				QAMain.getPlayerState().getLastMoveLocation().put(player.getUniqueId(), current);
			});
		});
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
        if (QAMain.getPlayerState().getLastWeaponSwitch().containsKey(player.getUniqueId()) &&
                System.currentTimeMillis() - QAMain.getPlayerState().getLastWeaponSwitch().get(player.getUniqueId()) < QAMain.getConfiguration().weapons.switchDelaySeconds * 1000)
            return true;

		int showdelay = ((int) (g.getDelayBetweenShotsInSeconds() * 1000));

		 return (g.getLastShotForGun().containsKey(player.getUniqueId())
				&& (System.currentTimeMillis() - g.getLastShotForGun().get(player.getUniqueId()) < showdelay));
	}
}
