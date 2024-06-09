package me.zombie_striker.qg.guns.projectiles;

import java.util.ArrayList;
import java.util.List;

import com.cryptomorin.xseries.particles.XParticle;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.api.QAProjectileExplodeEvent;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.guns.utils.GunUtil;
import me.zombie_striker.qg.handlers.ExplosionHandler;
import me.zombie_striker.qg.handlers.ParticleHandlers;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class RocketProjectile implements RealtimeCalculationProjectile {
	public RocketProjectile() {
		ProjectileManager.add(this);
	}

	@Override
	public void spawn(final Gun g, final Location s, final Player player, final Vector dir) {
		new BukkitRunnable() {
			int distance = g.getMaxDistance();

			@Override
			public void run() {
				for (int tick = 0; tick < g.getVelocityForRealtimeCalculations(); tick++) {
					distance--;
					s.add(dir);
					ParticleHandlers.spawnGunParticles(g, s);
					boolean entityNear = false;
					try {
						List<Entity> e2 = new ArrayList<>(s.getWorld().getNearbyEntities(s, 1, 1, 1));
						for (Entity e : e2) {
							if (e != player
									&& (!(e instanceof Player) || ((Player) e).getGameMode() != GameMode.SPECTATOR))
								entityNear = true;
						}
					} catch (Error e) {
					}

					final Location explode = s.clone();
					if (GunUtil.isSolid(explode.getBlock(), explode) || entityNear || distance < 0) {
						boolean explodeDamage = false;

						if (QAMain.enableExplosionDamage) {
							QAProjectileExplodeEvent event = new QAProjectileExplodeEvent(RocketProjectile.this, explode);
							Bukkit.getPluginManager().callEvent(event);
							if (!event.isCancelled()) explodeDamage = ExplosionHandler.handleExplosion(explode, 4, 2);
						}
						try {
							//player.getWorld().playSound(s, WeaponSounds.WARHEAD_EXPLODE.getSoundName(), 10, 0.9f);
							player.getWorld().playSound(s, Sound.ENTITY_GENERIC_EXPLODE, 8, 0.7f);
							s.getWorld().spawnParticle(XParticle.EXPLOSION_EMITTER.get(), s, 0);

						} catch (Error e3) {
							s.getWorld().playEffect(s, Effect.valueOf("CLOUD"), 0);
							player.getWorld().playSound(s, Sound.valueOf("EXPLODE"), 8, 0.7f);
						}
						ExplosionHandler.handleAOEExplosion(player, s, g.getDamage(), g.getExplosionRadius());
						cancel();
						return;
					}
				}
			}
		}.runTaskTimer(QAMain.getInstance(), 0, 1);
	}

	@Override
	public String getName() {
		return ProjectileManager.RPG;
	}
}
