package me.zombie_striker.qg.guns.projectiles;

import me.zombie_striker.qg.utils.Compat;
import java.util.ArrayList;
import java.util.List;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.api.QAProjectileExplodeEvent;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.guns.utils.GunUtil;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.handlers.ExplosionHandler;
import me.zombie_striker.qg.handlers.MultiVersionLookup;
import me.zombie_striker.qg.handlers.ParticleHandlers;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import me.zombie_striker.qg.utils.FoliaRunnable;
import org.bukkit.util.Vector;

public class MiniNukeProjectile implements RealtimeCalculationProjectile{
public MiniNukeProjectile() {
	ProjectileManager.add(this);
}
	@Override
	public void spawn(final Gun g, final Location s, final Player player, final Vector dir) {
		new FoliaRunnable() {
			int distance = g.getMaxDistance();

			@Override
			public void run() {
				dir.setY(dir.getY() - QAMain.getConfiguration().weapons.detection.gravity);
				for (int tick = 0; tick < Math.round(0.99+g.getVelocityForRealtimeCalculations()); tick++) {
				distance--;
				s.add(dir);
				ParticleHandlers.spawnGunParticles(g, s);
				if (Compat.MODERN_SOUNDS) {
					player.getWorld().playSound(s, MultiVersionLookup.getDragonGrowl(), 1, 2f);

				} else {
					player.getWorld().playSound(s, Sound.valueOf("ENDERDRAGON_GROWL"), 1, 2f);
				}
				boolean entityNear = false;
				List<Entity> e2 = new ArrayList<>(s.getWorld().getNearbyEntities(s, 1, 1, 1));
				for(Entity e : e2) {
					if(e != player && (!(e instanceof Player) || ((Player)e).getGameMode()!=GameMode.SPECTATOR))
						entityNear = true;
				}

				if (GunUtil.isSolid(s.getBlock(), s) || entityNear || distance < 0) {
					if (QAMain.getConfiguration().features.enableExplosionDamage) {
						QAProjectileExplodeEvent event = new QAProjectileExplodeEvent(MiniNukeProjectile.this, s);
						Bukkit.getPluginManager().callEvent(event);
						if (!event.isCancelled()) ExplosionHandler.handleExplosion(s, Math.toIntExact(Math.round(g.getExplosionRadius())), 2);
					}
					if (Compat.WORLD_SOUND_BY_NAME) {
						player.getWorld().playSound(s, WeaponSounds.WARHEAD_EXPLODE.getSoundName(), 10, 0.9f);
						player.getWorld().playSound(s, Sound.ENTITY_GENERIC_EXPLODE, 8, 0.7f);
						ParticleHandlers.spawnMushroomCloud(s);
						final Location sClone = s.clone();
						new FoliaRunnable() {

							@Override
							public void run() {
								ParticleHandlers.spawnMushroomCloud(sClone);
							}
						}.runTaskLater(QAMain.getInstance(), sClone, 10);

					} else {
						s.getWorld().playEffect(s, Effect.valueOf("CLOUD"), 0);
						player.getWorld().playSound(s, Sound.valueOf("EXPLODE"), 8, 0.7f);
					}
					ExplosionHandler.handleAOEExplosion(player, s, g.getDamage(), g.getExplosionRadius(), g.getExplosionKnockback());
					cancel();
					return;
				}
				}
				
			}
		}.runTaskTimer(QAMain.getInstance(), player, 0, 1);
	}
	@Override
	public String getName() {
		return ProjectileManager.MINI_NUKE;
	}
}
