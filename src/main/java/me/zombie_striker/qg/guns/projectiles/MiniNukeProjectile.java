package me.zombie_striker.qg.guns.projectiles;

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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class MiniNukeProjectile implements RealtimeCalculationProjectile{
public MiniNukeProjectile() {
	ProjectileManager.add(this);
}
	@Override
	public void spawn(final Gun g, final Location s, final Player player, final Vector dir) {
		new BukkitRunnable() {
			int distance = g.getMaxDistance();

			@Override
			public void run() {
				dir.setY(dir.getY() - QAMain.gravity);
				for (int tick = 0; tick < Math.round(0.99+g.getVelocityForRealtimeCalculations()); tick++) {
				distance--;
				s.add(dir);
				ParticleHandlers.spawnGunParticles(g, s);
				try {
					player.getWorld().playSound(s, MultiVersionLookup.getDragonGrowl(), 1, 2f);

				} catch (Error e2) {
					player.getWorld().playSound(s, Sound.valueOf("ENDERDRAGON_GROWL"), 1, 2f);
				}
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
					if (QAMain.enableExplosionDamage) {
						QAProjectileExplodeEvent event = new QAProjectileExplodeEvent(MiniNukeProjectile.this, s);
						Bukkit.getPluginManager().callEvent(event);
						if (!event.isCancelled()) ExplosionHandler.handleExplosion(s, Math.toIntExact(Math.round(g.getExplosionRadius())), 2);
					}
					try {
						player.getWorld().playSound(s, WeaponSounds.WARHEAD_EXPLODE.getSoundName(), 10, 0.9f);
						player.getWorld().playSound(s, Sound.ENTITY_GENERIC_EXPLODE, 8, 0.7f);
						ParticleHandlers.spawnMushroomCloud(s);
						new BukkitRunnable() {

							@Override
							public void run() {
								ParticleHandlers.spawnMushroomCloud(s);
							}
						}.runTaskLater(QAMain.getInstance(), 10);

					} catch (Error e3) {
						s.getWorld().playEffect(s, Effect.valueOf("CLOUD"), 0);
						player.getWorld().playSound(s, Sound.valueOf("EXPLODE"), 8, 0.7f);
					}
					ExplosionHandler.handleAOEExplosion(player, s, g.getDurabilityDamage(), g.getExplosionRadius());
					cancel();
					return;
				}
				}
				
			}
		}.runTaskTimer(QAMain.getInstance(), 0, 1);
	}
	@Override
	public String getName() {
		return ProjectileManager.MINI_NUKE;
	}
}
