package me.zombie_striker.qg.guns.utils;

import java.util.ArrayList;
import java.util.List;

import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.handlers.ExplosionHandler;
import me.zombie_striker.qg.handlers.MultiVersionLookup;
import me.zombie_striker.qg.handlers.ParticleHandlers;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class MiniNukeProjectile {

	public static final double gravity = 0.05;

	public MiniNukeProjectile(final Location s, final Player player, final Vector dir) {
		new BukkitRunnable() {
			int distance = 220;

			@Override
			public void run() {
				distance--;
				s.add(dir);
				dir.setY(dir.getY() - gravity);
				try {
					s.getWorld().spawnParticle(org.bukkit.Particle.SMOKE_LARGE, s, 0);
					s.getWorld().spawnParticle(org.bukkit.Particle.FIREWORKS_SPARK, s, 0);
					player.getWorld().playSound(s, MultiVersionLookup.getDragonGrowl(), 1, 2f);

				} catch (Error e2) {
					s.getWorld().playEffect(s, Effect.valueOf("CLOUD"), 0);
					player.getWorld().playSound(s, Sound.valueOf("ENDERDRAGON_GROWL"), 1, 2f);
				}
				boolean entityNear = false;
				try {
					List<Entity> e2 = new ArrayList<>(s.getWorld().getNearbyEntities(s, 1, 1, 1));
					if (!e2.isEmpty())
						if (e2.size() > 1 || e2.get(0) != player)
							entityNear = true;
				} catch (Error e) {
				}

				if (GunUtil.isSolid(s.getBlock(), s) || entityNear || distance < 0) {
					if (Main.enableExplosionDamage) {
						ExplosionHandler.handleExplosion(s, 4, 2);
					}
					try {
						player.getWorld().playSound(s, WeaponSounds.WARHEAD_EXPLODE.getName(), 10, 0.9f);
						player.getWorld().playSound(s, Sound.ENTITY_GENERIC_EXPLODE, 8, 0.7f);
						ParticleHandlers.spawnMushroomCloud(s);
						new BukkitRunnable() {

							@Override
							public void run() {
								ParticleHandlers.spawnMushroomCloud(s);
							}
						}.runTaskLater(Main.getInstance(), 10);

					} catch (Error e3) {
						s.getWorld().playEffect(s, Effect.valueOf("CLOUD"), 0);
						player.getWorld().playSound(s, Sound.valueOf("EXPLODE"), 8, 0.7f);
					}
					try {
						for (Entity e : s.getWorld().getNearbyEntities(s, 10, 10, 10)) {
							if (e instanceof LivingEntity) {
								((LivingEntity) e).damage((20 * 4 / e.getLocation().distance(s)), player);
							}
						}
					} catch (Error e) {
						s.getWorld().createExplosion(s, 1);
					}
					cancel();
				}
			}
		}.runTaskTimer(Main.getInstance(), 0, 1);
	}
}
