package me.zombie_striker.qg.handlers;

import com.cryptomorin.xseries.particles.XParticle;
import com.cryptomorin.xseries.reflection.XReflection;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.guns.Gun;
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
		try {
			loc.getWorld().spawnParticle(XParticle.EXPLOSION_EMITTER.get(), loc, 1);
		} catch (Error | Exception e4) {
		}
		// TODO: Do lights n stuff
		try {
			if (Bukkit.getPluginManager().getPlugin("LightAPI") != null) {
				final Location loc2 = loc;
				LightAPI.get().setLightLevel(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), 15);
				new BukkitRunnable() {
					@Override
					public void run() {
						LightAPI.get().setLightLevel(loc2.getWorld().getName(), loc2.getBlockX(), loc2.getBlockY(), loc2.getBlockZ(), 0);
					}
				}.runTaskLater(QAMain.getInstance(), 10);
			}
		} catch (Error | Exception e5) {
		}
	}

	public static void spawnMushroomCloud(Location loc) {
		try {
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
		} catch (Error | Exception e4) {
		}

		try {
			if (Bukkit.getPluginManager().getPlugin("LightAPI") != null) {
				final Location loc2 = loc;
				LightAPI.get().setLightLevel(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), 15);
				new BukkitRunnable() {

					@Override
					public void run() {
						LightAPI.get().setLightLevel(loc2.getWorld().getName(), loc2.getBlockX(), loc2.getBlockY(), loc2.getBlockZ(), 0);
					}
				}.runTaskLater(QAMain.getInstance(), 20);
			}
		} catch (Error | Exception e5) {
		}
	}

	public static void spawnGunParticles(Gun g, Location loc) {
		try {
			if (g.getParticle() != null)
				if (g.getParticle() == XParticle.DUST.get()) {
					spawnParticle(g.getParticleR(), g.getParticleG(), g.getParticleB(), loc);
				} else if (g.getParticle() == XParticle.BLOCK.get() || g.getParticle() == Particle.BLOCK || g.getParticle() == Particle.FALLING_DUST) {
					loc.getWorld().spawnParticle(g.getParticle(), loc, 1, g.getParticleMaterial().createBlockData());
				} else {
					loc.getWorld().spawnParticle(g.getParticle(), loc, g.getParticleData());
				}
		} catch (Error | Exception e4) {
		}
	}

	public static void spawnParticle(double r, double g, double b, Location loc) {
		try {
			if (is13) {
				Particle.DustOptions dust = new Particle.DustOptions(
						Color.fromRGB((int) (r * 255), (int) (g * 255), (int) (b * 255)), 1);
				for (Player player : loc.getWorld().getPlayers()) {
					if (player.getLocation().distanceSquared(loc) < 60 * 60)
						player.spawnParticle(XParticle.DUST.get(), loc.getX(), loc.getY(), loc.getZ(), 0, 0, 0, 0, dust);
				}
				/*Particle.DustOptions dust = new Particle.DustOptions(
						Color.fromRGB((int) (r * 255), (int) (g * 255), (int) (b * 255)), 1);
				loc.getWorld().spawnParticle(Particle.REDSTONE, loc.getX(), loc.getY(), loc.getZ(), 0, 0, 0, 0, dust);*/
			} else {

				for (Player player : loc.getWorld().getPlayers()) {
					if (player.getLocation().distanceSquared(loc) < 60 * 60)
						player.spawnParticle(XParticle.DUST.get(), loc.getX(), loc.getY(), loc.getZ(), 0, r, g, b, 1);
				}
				//loc.getWorld().spawnParticle(Particle.REDSTONE, loc.getX(), loc.getY(), loc.getZ(), 0, r, g, b, 1);
			}
		} catch (Error | Exception e45) {
			e45.printStackTrace();
		}
	}

	public static void spawnMuzzleSmoke(Player shooter, Location loc) {
		try {
			double theta = Math.atan2(shooter.getLocation().getDirection().getX(),
					shooter.getLocation().getDirection().getZ());

			theta -= (Math.PI / 8);

			double x = Math.sin(theta);
			double z = Math.cos(theta);

			Location l = loc.clone().add(x, 0, z);

			for (int i = 0; i < 2; i++)
				loc.getWorld().spawnParticle(XParticle.EFFECT.get(), l, 0);
		} catch (Error | Exception e4) {
		}
	}
}
