package me.zombie_striker.qg.handlers;

import org.bukkit.Location;
import org.bukkit.Particle;

import me.zombie_striker.qg.guns.Gun;

public class ParticleHandlers {

	public static void spawnMushroomCloud(Location loc) {
		for (double d = 0; d < 2 * Math.PI; d += Math.PI / 48) {
			double radius = 2;
			loc.getWorld().spawnParticle(Particle.REDSTONE, loc.getX() + (Math.sin(d)* radius),
					loc.getY() , loc.getZ()+ (Math.cos(d)* radius), 0, 1.0, 1.0, 1.0, 1);
			radius = 1.8;
			loc.getWorld().spawnParticle(Particle.REDSTONE, loc.getX() + (Math.sin(d)* radius),
					loc.getY()+0.5 , loc.getZ()+ (Math.cos(d)* radius), 0, 1.0, 0.0, 0.0, 1);
			radius = 1.6;
			loc.getWorld().spawnParticle(Particle.REDSTONE, loc.getX() + (Math.sin(d)* radius),
					loc.getY()+1 , loc.getZ()+ (Math.cos(d)* radius), 0, 1.0, 0.2, 0.0, 1);
			radius = 1.3;
			loc.getWorld().spawnParticle(Particle.REDSTONE, loc.getX() + (Math.sin(d)* radius),
					loc.getY()+1.5 , loc.getZ()+ (Math.cos(d)* radius), 0, 1.0, 0.2, 0.0, 1);
			radius = 1.1;
			loc.getWorld().spawnParticle(Particle.REDSTONE, loc.getX() + (Math.sin(d)* radius),
					loc.getY()+2 , loc.getZ()+ (Math.cos(d)* radius), 0, 1.0, 0.5, 0.0, 1);
			radius = 1.1;
			loc.getWorld().spawnParticle(Particle.REDSTONE, loc.getX() + (Math.sin(d)* radius),
					loc.getY()+2.5 , loc.getZ()+ (Math.cos(d)* radius), 0, 1.0, 0.5, 0.0, 1);
			radius = 1;
			loc.getWorld().spawnParticle(Particle.REDSTONE, loc.getX() + (Math.sin(d)* radius),
					loc.getY()+3  , loc.getZ()+ (Math.cos(d)* radius), 0, 1.0, 0.5, 0.0, 1);
			radius = 1;
			loc.getWorld().spawnParticle(Particle.REDSTONE, loc.getX() + (Math.sin(d)* radius),
					loc.getY()+3.5  , loc.getZ()+ (Math.cos(d)* radius), 0, 1.0, 0.5, 0.0, 1);
			radius = 3;
			loc.getWorld().spawnParticle(Particle.REDSTONE, loc.getX() + (Math.sin(d)* radius),
					loc.getY()+4  , loc.getZ()+ (Math.cos(d)* radius), 0, 1.0, 1.0, 1.0, 1);
			radius = 2.8;
			loc.getWorld().spawnParticle(Particle.REDSTONE, loc.getX() + (Math.sin(d)* radius),
					loc.getY()+4.5  , loc.getZ()+ (Math.cos(d)* radius), 0, 1.0, 1.0, 1.0, 1);
			radius = 2.5;
			loc.getWorld().spawnParticle(Particle.REDSTONE, loc.getX() + (Math.sin(d)* radius),
					loc.getY()+5  , loc.getZ()+ (Math.cos(d)* radius), 0, 1.0, 0.2, 0.0, 1);
			radius = 2;
			loc.getWorld().spawnParticle(Particle.REDSTONE, loc.getX() + (Math.sin(d)* radius),
					loc.getY()+5.5  , loc.getZ()+ (Math.cos(d)* radius), 0, 1.0, 0.5, 0.0, 1);
			radius = 1.5;
			loc.getWorld().spawnParticle(Particle.REDSTONE, loc.getX() + (Math.sin(d)* radius),
					loc.getY()+6  , loc.getZ()+ (Math.cos(d)* radius), 0, 1.0, 0.5, 0.0, 1);
		}
	}
	
	public static void spawnGunParticles(Gun g, Location loc) {
		if(g.getParticle()==Particle.REDSTONE) {
		loc.getWorld().spawnParticle(Particle.REDSTONE, loc.getX() ,
				loc.getY() , loc.getZ(), 0, g.getParticleR(), g.getParticleG(), g.getParticleB(), 1);
		}else {
			loc.getWorld().spawnParticle(g.getParticle(), loc, 0);
		}
	}
}
