package me.zombie_striker.qg.guns;

import java.util.ArrayList;
import java.util.List;

import me.zombie_striker.qg.Main;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class RocketProjectile {

	public RocketProjectile(final Location s, final Player player,
			final Vector dir) {
		new BukkitRunnable() {
			int distance = 220;

			@Override
			public void run() {
				distance--;
				s.add(dir);
				try {
					s.getWorld().spawnParticle(org.bukkit.Particle.SMOKE_LARGE,
							s, 0);
					s.getWorld().spawnParticle(
							org.bukkit.Particle.FIREWORKS_SPARK, s, 0);
					player.getWorld().playSound(s,
							Sound.ENTITY_ENDERDRAGON_GROWL, 1, 2f);

				} catch (Error e2) {
					s.getWorld().playEffect(s, Effect.valueOf("CLOUD"), 0);
					player.getWorld().playSound(s, Sound.valueOf("ENDERDRAGON_GROWL"), 1,
							2f);
				}
				boolean entityNear = false;
				try {
					List<Entity> e2 = new ArrayList<>(s.getWorld()
							.getNearbyEntities(s, 1, 1, 1));
					if (!e2.isEmpty())
						if (e2.size() > 1 || e2.get(0) != player)
							entityNear = true;
				} catch (Error e) {
				}

				if (GunUtil.isSolid(s.getBlock(), s) || entityNear
						|| distance < 0) {
					try {
						s.getWorld().spawnParticle(
								org.bukkit.Particle.EXPLOSION_HUGE, s, 0);
						player.getWorld().playSound(s,
								Sound.ENTITY_GENERIC_EXPLODE, 8, 0.7f);
					} catch (Error e3) {
						s.getWorld().playEffect(s, Effect.valueOf("CLOUD"), 0);
						player.getWorld().playSound(s, Sound.valueOf("EXPLODE"), 8, 0.7f);
					}
					try {
						for (Entity e : s.getWorld().getNearbyEntities(s, 7, 7,
								7)) {
							if (e instanceof LivingEntity) {
								((LivingEntity) e).damage((20 * 4 / e
										.getLocation().distance(s)));
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
