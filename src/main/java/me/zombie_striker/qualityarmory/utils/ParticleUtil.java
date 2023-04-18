package me.zombie_striker.qualityarmory.utils;

import com.cryptomorin.xseries.ReflectionUtils;
import me.zombie_striker.qualityarmory.QAMain;
import me.zombie_striker.qualityarmory.guns.Gun;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.beykerykt.minecraft.lightapi.common.LightAPI;

public class ParticleUtil {

	private static boolean is13 = true;

	public static void initValues() {
		is13 = ReflectionUtils.supports(13);
	}

	public static void spawnParticle(double r, double g, double b, Location loc) {
		try {
			if (is13) {
				Particle.DustOptions dust = new Particle.DustOptions(
						Color.fromRGB((int) (r * 255), (int) (g * 255), (int) (b * 255)), 1);
				for (Player player : loc.getWorld().getPlayers()) {
					if (player.getLocation().distanceSquared(loc) < 60 * 60)
						player.spawnParticle(Particle.REDSTONE, loc.getX(), loc.getY(), loc.getZ(), 0, 0, 0, 0, dust);
				}
			} else {

				for (Player player : loc.getWorld().getPlayers()) {
					if (player.getLocation().distanceSquared(loc) < 60 * 60)
						player.spawnParticle(Particle.REDSTONE, loc.getX(), loc.getY(), loc.getZ(), 0, r, g, b, 1);
				}
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
				loc.getWorld().spawnParticle(Particle.SPELL, l, 0);
		} catch (Error | Exception e4) {
		}
	}
}
