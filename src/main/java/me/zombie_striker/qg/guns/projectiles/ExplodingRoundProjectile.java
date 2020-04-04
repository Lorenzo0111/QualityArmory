package me.zombie_striker.qg.guns.projectiles;

import java.util.ArrayList;
import java.util.List;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.guns.utils.GunUtil;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.handlers.ExplosionHandler;
import me.zombie_striker.qg.handlers.ParticleHandlers;

import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class ExplodingRoundProjectile implements RealtimeCalculationProjectile {
	public ExplodingRoundProjectile() {
		ProjectileManager.add(this);
	}

	@Override
	public void spawn(final Gun g, final Location s, final Player player, final Vector dir) {
		new BukkitRunnable() {
			int distance = g.getMaxDistance();

			@Override
			public void run() {
				dir.setY(dir.getY() - QAMain.gravity);
				for (int tick = 0; tick < g.getVelocityForRealtimeCalculations(); tick++) {
					distance--;
					s.add(dir);
					ParticleHandlers.spawnGunParticles(g, s);
					boolean entityNear = false;
					try {
						List<Entity> e2 = new ArrayList<>(s.getWorld().getNearbyEntities(s, 1, 1, 1));
								for(Entity e : e2) {
									if(e != player && (!(e instanceof Player) || ((Player)e).getGameMode()!=GameMode.SPECTATOR))
								entityNear = true;
								}
					} catch (Error e) {
					}

					if (GunUtil.isSolid(s.getBlock(), s) || entityNear || distance < 0) {
						ExplosionHandler.handleAOEExplosion(player, s, g.getDurabilityDamage(), g.getExplosionRadius());
						ParticleHandlers.spawnExplosion(s);
						try {
							player.getWorld().playSound(s, WeaponSounds.WARHEAD_EXPLODE.getSoundName(), 10, 1.5f);
							player.getWorld().playSound(s, Sound.ENTITY_GENERIC_EXPLODE, 8, 0.7f);
						} catch (Error e3) {
							s.getWorld().playEffect(s, Effect.valueOf("CLOUD"), 0);
							try {
							player.getWorld().playSound(s, Sound.valueOf("EXPLODE"), 8, 0.7f);
							} catch (Error e333) {
								player.getWorld().playSound(s, Sound.valueOf("ENTITY_GENERIC_EXPLODE"), 8, 0.7f);
							}
						}
						cancel();
						return;
					}
				}
			}
		}.runTaskTimer(QAMain.getInstance(), 0, 1);
	}

	@Override
	public String getName() {
		return ProjectileManager.EXPLODINGROUND;
	}
}
