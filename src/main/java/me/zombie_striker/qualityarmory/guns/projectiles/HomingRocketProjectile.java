package me.zombie_striker.qualityarmory.guns.projectiles;

import java.util.ArrayList;
import java.util.List;

import me.zombie_striker.qualityarmory.QAMain;
import me.zombie_striker.qualityarmory.api.events.QAProjectileExplodeEvent;
import me.zombie_striker.qualityarmory.api.QualityArmory;
import me.zombie_striker.qualityarmory.guns.Gun;
import me.zombie_striker.qualityarmory.guns.utils.GunUtil;
import me.zombie_striker.qualityarmory.guns.utils.WeaponSounds;
import me.zombie_striker.qualityarmory.utils.ExplosionUtil;
import me.zombie_striker.qualityarmory.utils.ParticleUtil;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class HomingRocketProjectile implements RealtimeCalculationProjectile {
	public HomingRocketProjectile() {
		ProjectileManager.add(this);
	}

	@Override
	public void spawn(final Gun g, final Location starting, final Player player, final Vector dir) {
		new BukkitRunnable() {
			Location RPGLOCATION = starting.clone();
			int distance = g.getMaxDistance();
			Vector vect = dir;

			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				for (int tick = 0; tick < g.getVelocityForRealtimeCalculations(); tick++) {
					distance--;
					RPGLOCATION.add(vect);
					Block lookat = player.getTargetBlock(null, 300);
					if (lookat != null && lookat.getType() != Material.AIR) {
						if (QualityArmory.isGun(player.getItemInHand())) {
							Gun g = me.zombie_striker.qualityarmory.api.QualityArmory.getGun(player.getItemInHand());
							if (g.usesCustomProjctiles() && g.getCustomProjectile() instanceof HomingRocketProjectile) {
								Vector newDir = lookat.getLocation().clone().subtract(RPGLOCATION).toVector()
										.normalize();
								vect = newDir;
							}
						}
					}
					ParticleUtil.spawnGunParticles(g, RPGLOCATION);
					try {
						player.getWorld().playSound(RPGLOCATION, MultiVersionLookup.getDragonGrowl(), 1, 2f);

					} catch (Error e2) {
						RPGLOCATION.getWorld().playEffect(RPGLOCATION, Effect.valueOf("CLOUD"), 0);
						player.getWorld().playSound(RPGLOCATION, Sound.valueOf("ENDERDRAGON_GROWL"), 1, 2f);
					}
					boolean entityNear = false;
					try {
						List<Entity> e2 = new ArrayList<>(
								RPGLOCATION.getWorld().getNearbyEntities(RPGLOCATION, 1, 1, 1));
						for(Entity e : e2) {
							if(e != player && (!(e instanceof Player) || ((Player)e).getGameMode()!=GameMode.SPECTATOR))
								entityNear = true;
						}
					} catch (Error e) {
					}

					if (GunUtil.isSolid(RPGLOCATION.getBlock(), RPGLOCATION) || entityNear || distance < 0) {
						if (QAMain.enableExplosionDamage) {
							QAProjectileExplodeEvent event = new QAProjectileExplodeEvent(HomingRocketProjectile.this, RPGLOCATION);
							Bukkit.getPluginManager().callEvent(event);
							if (!event.isCancelled()) ExplosionUtil.handleExplosion(RPGLOCATION, 4, 2);
						}
						try {
							player.getWorld().playSound(RPGLOCATION, WeaponSounds.WARHEAD_EXPLODE.getName(), 10, 0.9f);
							player.getWorld().playSound(RPGLOCATION, Sound.ENTITY_GENERIC_EXPLODE, 8, 0.7f);
							RPGLOCATION.getWorld().spawnParticle(org.bukkit.Particle.EXPLOSION_HUGE, RPGLOCATION, 0);

						} catch (Error e3) {
							RPGLOCATION.getWorld().playEffect(RPGLOCATION, Effect.valueOf("CLOUD"), 0);
							player.getWorld().playSound(RPGLOCATION, Sound.valueOf("EXPLODE"), 8, 0.7f);
						}
						ExplosionUtil.handleAOEExplosion(player, RPGLOCATION, g.getDamage(), g.getExplosionRadius());
						cancel();
						return;
					}
				}
			}
		}.runTaskTimer(QAMain.getInstance(), 0, 1);
	}

	@Override
	public String getName() {
		return ProjectileManager.HOMING_RPG;
	}
}
