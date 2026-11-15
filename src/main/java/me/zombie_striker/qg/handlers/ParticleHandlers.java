package me.zombie_striker.qg.handlers;

import com.cryptomorin.xseries.particles.XParticle;
import com.cryptomorin.xseries.reflection.XReflection;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.utils.Compat;
import me.zombie_striker.qg.utils.ErrorReporter;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.beykerykt.minecraft.lightapi.common.LightAPI;

public class ParticleHandlers {

	public static boolean is13 = true;

	public static void initValues() {
		is13 = XReflection.supports(13);
	}

	public static void spawnExplosion(Location loc) {
		if (Compat.PARTICLES)
			loc.getWorld().spawnParticle(XParticle.EXPLOSION_EMITTER.get(), loc, 1);
		flashLight(loc, 10);
	}

	public static void spawnMushroomCloud(Location loc) {
		if (Compat.PARTICLES) {
			for (double d = 0; d < 2 * Math.PI; d += Math.PI / 48) {
				double radius = 2;

				spawnParticle(1.0, 1.0, 1.0, new Location(loc.getWorld(), loc.getX() + (Math.sin(d) * radius),
						loc.getY(), loc.getZ() + (Math.cos(d) * radius)));
				radius = 1.8;
				spawnParticle(1.0, 0.0, 0.0, new Location(loc.getWorld(), loc.getX() + (Math.sin(d) * radius),
						loc.getY() + 0.5, loc.getZ() + (Math.cos(d) * radius)));
				radius = 1.6;
				spawnParticle(1.0, 0.2, 0.0, new Location(loc.getWorld(), loc.getX() + (Math.sin(d) * radius),
						loc.getY() + 1, loc.getZ() + (Math.cos(d) * radius)));
				radius = 1.3;
				spawnParticle(1.0, 0.2, 0.0, new Location(loc.getWorld(), loc.getX() + (Math.sin(d) * radius),
						loc.getY() + 1.5, loc.getZ() + (Math.cos(d) * radius)));
				radius = 1.1;
				spawnParticle(1.0, 0.5, 0.0, new Location(loc.getWorld(), loc.getX() + (Math.sin(d) * radius),
						loc.getY() + 2, loc.getZ() + (Math.cos(d) * radius)));
				radius = 1;
				spawnParticle(1.0, 0.5, 0.0, new Location(loc.getWorld(), loc.getX() + (Math.sin(d) * radius),
						loc.getY() + 2.5, loc.getZ() + (Math.cos(d) * radius)));
				radius = 3;
				spawnParticle(1.0, 0.5, 0.0, new Location(loc.getWorld(), loc.getX() + (Math.sin(d) * radius),
						loc.getY() + 3, loc.getZ() + (Math.cos(d) * radius)));
				radius = 2.8;
				spawnParticle(1.0, 0.5, 0.0, new Location(loc.getWorld(), loc.getX() + (Math.sin(d) * radius),
						loc.getY() + 3.5, loc.getZ() + (Math.cos(d) * radius)));
				radius = 2.5;
				spawnParticle(1.0, 1.0, 1.0, new Location(loc.getWorld(), loc.getX() + (Math.sin(d) * radius),
						loc.getY() + 4, loc.getZ() + (Math.cos(d) * radius)));
				radius = 2;
				spawnParticle(1.0, 1.0, 1.0, new Location(loc.getWorld(), loc.getX() + (Math.sin(d) * radius),
						loc.getY() + 4.5, loc.getZ() + (Math.cos(d) * radius)));
				radius = 1.5;
				spawnParticle(1.0, 0.2, 0.0, new Location(loc.getWorld(), loc.getX() + (Math.sin(d) * radius),
						loc.getY() + 5, loc.getZ() + (Math.cos(d) * radius)));
				radius = 0.8;
				spawnParticle(1.0, 0.5, 0.0, new Location(loc.getWorld(), loc.getX() + (Math.sin(d) * radius),
						loc.getY() + 5.5, loc.getZ() + (Math.cos(d) * radius)));
			}
		}
		flashLight(loc, 20);
	}

	/**
	 * Lights up the location for a few ticks when LightAPI is installed.
	 */
	private static void flashLight(Location loc, long ticks) {
		if (Bukkit.getPluginManager().getPlugin("LightAPI") == null) return;
		try {
			final String world = loc.getWorld().getName();
			final int x = loc.getBlockX(), y = loc.getBlockY(), z = loc.getBlockZ();
			LightAPI.get().setLightLevel(world, x, y, z, 15);
			new BukkitRunnable() {
				@Override
				public void run() {
					LightAPI.get().setLightLevel(world, x, y, z, 0);
				}
			}.runTaskLater(QAMain.getInstance(), ticks);
		} catch (RuntimeException | LinkageError e) {
			ErrorReporter.report("lighting an explosion with LightAPI", e);
		}
	}

	public static void spawnGunParticles(Gun g, Location loc) {
		Particle particle = g.getParticle();
		if (particle == null || !Compat.PARTICLES) return;
		try {
			if (particle == XParticle.DUST.get()) {
				spawnParticle(g.getParticleR(), g.getParticleG(), g.getParticleB(), loc);
			} else if (particle == XParticle.BLOCK.get() || particle == XParticle.FALLING_DUST.get()) {
				// Block particles need block data, which exists from 1.13
				if (Compat.FLATTENED)
					loc.getWorld().spawnParticle(particle, loc, 1, g.getParticleMaterial().createBlockData());
			} else {
				loc.getWorld().spawnParticle(particle, loc, g.getParticleData());
			}
		} catch (RuntimeException e) {
			ErrorReporter.report("spawning the particles of " + g.getName(), e);
		}
	}

	public static void spawnParticle(double r, double g, double b, Location loc) {
		if (!Compat.PARTICLES) return;
		try {
			if (is13) {
				Particle.DustOptions dust = new Particle.DustOptions(
						Color.fromRGB((int) (r * 255), (int) (g * 255), (int) (b * 255)), 1);
				for (Player player : loc.getWorld().getPlayers()) {
					if (player.getLocation().distanceSquared(loc) < 60 * 60)
						player.spawnParticle(XParticle.DUST.get(), loc.getX(), loc.getY(), loc.getZ(), 0, 0, 0, 0, dust);
				}
			} else {
				for (Player player : loc.getWorld().getPlayers()) {
					if (player.getLocation().distanceSquared(loc) < 60 * 60)
						player.spawnParticle(XParticle.DUST.get(), loc.getX(), loc.getY(), loc.getZ(), 0, r, g, b, 1);
				}
			}
		} catch (RuntimeException e) {
			ErrorReporter.report("spawning dust particles", e);
		}
	}

	public static void spawnMuzzleSmoke(Player shooter, Location loc) {
		if (!Compat.PARTICLES) return;
		double theta = Math.atan2(shooter.getLocation().getDirection().getX(),
				shooter.getLocation().getDirection().getZ());

		theta -= (Math.PI / 8);

		Location l = loc.clone().add(Math.sin(theta), 0, Math.cos(theta));

		for (int i = 0; i < 2; i++)
			loc.getWorld().spawnParticle(XParticle.EFFECT.get(), l, 0);
	}
}
