package me.zombie_striker.qg.miscitems;

import me.zombie_striker.qg.utils.ErrorReporter;
import me.zombie_striker.qg.utils.Compat;
import com.cryptomorin.xseries.particles.XParticle;
import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.hooks.protection.ProtectionHandler;
import me.zombie_striker.qg.utils.FoliaRunnable;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class SmokeGrenades extends Grenade {
	private boolean useModernParticles = false;

	public SmokeGrenades(ItemStack[] ingg, double cost, double damage, double explosionreadius, String name,
			String displayname, List<String> lore, MaterialStorage ms) {
		super(ingg, cost, damage, explosionreadius, name, displayname, lore, ms);
	}

	@Override
	public boolean onPull(Player thrower, ItemStack usedItem) {
		if(!QAMain.getConfiguration().features.enableAutoArmGrenades)
            if (ThrowableItemRegistry.containsKey(thrower)) {
                thrower.sendMessage(QAMain.getMessages().general.prefix + QAMain.getMessages().grenade.alreadyPulled);
                thrower.playSound(thrower.getLocation(), WeaponSounds.RELOAD_BULLET.getSoundName(), 1, 1);
                return true;
            }
		thrower.getWorld().playSound(thrower.getLocation(), WeaponSounds.RELOAD_MAG_IN.getSoundName(), 2, 1);
		final ThrowableHolder h = new ThrowableHolder(thrower.getUniqueId(), thrower, this);
		h.setTimer(new FoliaRunnable() {
			@Override
			public void run() {
				Entity holderEntity = h.getHolder();
				if (holderEntity == null) {
					cancel();
					return;
				}
				FoliaRunnable.runEntityTask(QAMain.getInstance(), holderEntity,
						() -> handleThrowableTick(h),
						() -> {
							ThrowableItemRegistry.remove(holderEntity);
							BukkitTask t = h.getTask();
							if (t != null) t.cancel();
						});
			}
		}.runTaskTimer(QAMain.getInstance(), getFuseTicks(), 5));
		ThrowableItemRegistry.put(thrower, h);
		return true;

	}

	private void handleThrowableTick(ThrowableHolder h) {
				if (isUseModernParticles()) {
					if (!Compat.PARTICLES) {
						h.getHolder().getWorld().playEffect(h.getHolder().getLocation(), Effect.SMOKE, 0);
					} else try {
						// Main smoke cloud that gets larger over time
						double radius = (h.getTicks() / 10.0) + 1.0;
						int particleCount = Math.max(1, 50 - h.getTicks());

						// Create a spherical smoke cloud
						for (int i = 0; i < particleCount * 3; i++) {
							double angle1 = Math.random() * 2 * Math.PI;
							double angle2 = Math.random() * 2 * Math.PI;

							double x = radius * Math.sin(angle1) * Math.cos(angle2);
							double y = Math.abs(radius * Math.sin(angle2));
							double z = radius * Math.sin(angle1) * Math.sin(angle2);

							Location particleLoc = h.getHolder().getLocation().add(x, y, z);

							// Main smoke particles
							h.getHolder().getWorld().spawnParticle(XParticle.CAMPFIRE_COSY_SMOKE.get(),
									particleLoc, 1, 0.2, 0.2, 0.2, 0.01);
						}

						// Add some gray smoke for density
						if (h.getTicks() < 40) {
							for (int i = 0; i < 20 * 3; i++) {
								double x = (Math.random() - 0.5) * radius * 1.5;
								double y = Math.random() * radius;
								double z = (Math.random() - 0.5) * radius * 1.5;

								Location particleLoc = h.getHolder().getLocation().add(x, y, z);
								h.getHolder().getWorld().spawnParticle(XParticle.LARGE_SMOKE.get(),
										particleLoc, 1, 0.3, 0.3, 0.3, 0.02);
							}
						}

						// Initial explosion flash (first few ticks only)
						if (h.getTicks() < 5) {
							h.getHolder().getWorld().spawnParticle(XParticle.EXPLOSION.get(), h.getHolder().getLocation(), 2, 1, 1, 1, 0);
							h.getHolder().getWorld().spawnParticle(XParticle.POOF.get(), h.getHolder().getLocation(), 10, 2, 2, 2, 0.1);
						}
					} catch (IllegalArgumentException e) {
						// A particle that XSeries can't map on this version
						h.getHolder().getWorld().playEffect(h.getHolder().getLocation(), Effect.SMOKE, 0);
					}
				} else {
                    if (Compat.PARTICLES) {
                        h.getHolder().getWorld().spawnParticle(XParticle.EXPLOSION_EMITTER.get(),
                                h.getHolder().getLocation(), 0);
                    } else {
                        h.getHolder().getWorld().playEffect(h.getHolder().getLocation(), Effect.valueOf("CLOUD"), 0);
                    }
				}

                if (h.getTicks() % 2 == 0)
                    if (Compat.WORLD_SOUND_BY_NAME) {
                            h.getHolder().getWorld().playSound(h.getHolder().getLocation(),
                                    WeaponSounds.HISS.getSoundName(), 2f, 1f);
                    } else {
                        h.getHolder().getWorld().playSound(h.getHolder().getLocation(), Sound.valueOf("EXPLODE"), 3, 0.7f);
                    }

				h.setTicks(h.getTicks() + 1);
				if (h.getTicks() == 1) {
					if (h.getHolder() instanceof Player) {
						QAMain.DEBUG("Blinded player");
						((LivingEntity) h.getHolder())
								.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 10, 2));
						removeGrenade(((Player) h.getHolder()));
					}
				} else if (h.getTicks() == 80) {
					if (h.getHolder() instanceof Item) {
						Grenade.getGrenades().remove(h.getHolder());
						h.getHolder().remove();
					}
					ThrowableItemRegistry.remove(h.getHolder());
					if (h.getTask() != null) h.getTask().cancel();
				} else {
					for(Entity e : h.getHolder().getNearbyEntities(radius, radius, radius))
						if(e instanceof LivingEntity) {
							final LivingEntity target = (LivingEntity) e;
							QAMain.DEBUG("Blinding to "+target.getName());
							FoliaRunnable.runEntityTask(QAMain.getInstance(), target, () -> {
								try {
									if (ProtectionHandler.canPvp(target.getLocation())) {
										target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 10, 2));
									}
								} catch (LinkageError error) {
									ErrorReporter.report("checking PvP protection", error);
									target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 10, 2));
								}
							});
						}
				}
	}

	public boolean isUseModernParticles() {
		return useModernParticles;
	}

	public void setUseModernParticles(boolean useModernParticles) {
		this.useModernParticles = QAMain.isVersionHigherThan(1, 14) && useModernParticles;
	}
}
