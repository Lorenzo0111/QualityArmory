package me.zombie_striker.qg.handlers;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import me.zombie_striker.qg.guns.Gun;

public class ParticleHandlers {

	public static void spawnMushroomCloud(Location loc) {
		try {
			for (double d = 0; d < 2 * Math.PI; d += Math.PI / 48) {
				double radius = 2;
				loc.getWorld().spawnParticle(Particle.REDSTONE, loc.getX() + (Math.sin(d) * radius), loc.getY(),
						loc.getZ() + (Math.cos(d) * radius), 0, 1.0, 1.0, 1.0, 1);
				radius = 1.8;
				loc.getWorld().spawnParticle(Particle.REDSTONE, loc.getX() + (Math.sin(d) * radius), loc.getY() + 0.5,
						loc.getZ() + (Math.cos(d) * radius), 0, 1.0, 0.0, 0.0, 1);
				radius = 1.6;
				loc.getWorld().spawnParticle(Particle.REDSTONE, loc.getX() + (Math.sin(d) * radius), loc.getY() + 1,
						loc.getZ() + (Math.cos(d) * radius), 0, 1.0, 0.2, 0.0, 1);
				radius = 1.3;
				loc.getWorld().spawnParticle(Particle.REDSTONE, loc.getX() + (Math.sin(d) * radius), loc.getY() + 1.5,
						loc.getZ() + (Math.cos(d) * radius), 0, 1.0, 0.2, 0.0, 1);
				radius = 1.1;
				loc.getWorld().spawnParticle(Particle.REDSTONE, loc.getX() + (Math.sin(d) * radius), loc.getY() + 2,
						loc.getZ() + (Math.cos(d) * radius), 0, 1.0, 0.5, 0.0, 1);
				radius = 1.1;
				loc.getWorld().spawnParticle(Particle.REDSTONE, loc.getX() + (Math.sin(d) * radius), loc.getY() + 2.5,
						loc.getZ() + (Math.cos(d) * radius), 0, 1.0, 0.5, 0.0, 1);
				radius = 1;
				loc.getWorld().spawnParticle(Particle.REDSTONE, loc.getX() + (Math.sin(d) * radius), loc.getY() + 3,
						loc.getZ() + (Math.cos(d) * radius), 0, 1.0, 0.5, 0.0, 1);
				radius = 1;
				loc.getWorld().spawnParticle(Particle.REDSTONE, loc.getX() + (Math.sin(d) * radius), loc.getY() + 3.5,
						loc.getZ() + (Math.cos(d) * radius), 0, 1.0, 0.5, 0.0, 1);
				radius = 3;
				loc.getWorld().spawnParticle(Particle.REDSTONE, loc.getX() + (Math.sin(d) * radius), loc.getY() + 4,
						loc.getZ() + (Math.cos(d) * radius), 0, 1.0, 1.0, 1.0, 1);
				radius = 2.8;
				loc.getWorld().spawnParticle(Particle.REDSTONE, loc.getX() + (Math.sin(d) * radius), loc.getY() + 4.5,
						loc.getZ() + (Math.cos(d) * radius), 0, 1.0, 1.0, 1.0, 1);
				radius = 2.5;
				loc.getWorld().spawnParticle(Particle.REDSTONE, loc.getX() + (Math.sin(d) * radius), loc.getY() + 5,
						loc.getZ() + (Math.cos(d) * radius), 0, 1.0, 0.2, 0.0, 1);
				radius = 2;
				loc.getWorld().spawnParticle(Particle.REDSTONE, loc.getX() + (Math.sin(d) * radius), loc.getY() + 5.5,
						loc.getZ() + (Math.cos(d) * radius), 0, 1.0, 0.5, 0.0, 1);
				radius = 1.5;
				loc.getWorld().spawnParticle(Particle.REDSTONE, loc.getX() + (Math.sin(d) * radius), loc.getY() + 6,
						loc.getZ() + (Math.cos(d) * radius), 0, 1.0, 0.5, 0.0, 1);
			}
		} catch (Error | Exception e4) {
		}
	}

	public static void spawnGunParticles(Gun g, Location loc) {
		try {
			if (g.getParticle() == Particle.REDSTONE) {
				loc.getWorld().spawnParticle(Particle.REDSTONE, loc.getX(), loc.getY(), loc.getZ(), 0, g.getParticleR(),
						g.getParticleG(), g.getParticleB(), 1);
			} else {
				loc.getWorld().spawnParticle(g.getParticle(), loc, 0);
			}
		} catch (Error | Exception e4) {
		}
	}

	public static void spawnMuzzleSmoke(Player shooter, Location loc) {
		try {
			double theta = Math.atan2(shooter.getLocation().getDirection().getX(), shooter.getLocation().getDirection().getZ());
			
			theta-= (Math.PI/8);
			
			double x = Math.sin(theta);
			double z = Math.cos(theta);
			
			Location l = loc.clone().add(x, 0, z);
			
			for(int i = 0; i < 2;i++)
			loc.getWorld().spawnParticle(Particle.SPELL, l, 0);
		} catch (Error | Exception e4) {
		}
	}
}
