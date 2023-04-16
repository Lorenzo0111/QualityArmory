package me.zombie_striker.qualityarmory.utils;

import me.zombie_striker.qualityarmory.hooks.protection.ProtectionHandler;
import org.bukkit.Location;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;


public class ExplosionUtil {
	public static boolean handleExplosion(Location origin, int radius, int power) {
		try{
			if(!ProtectionHandler.canExplode(origin)) {
				origin.getWorld().createExplosion(origin, 0);
				return false;
			}

			origin.getWorld().createExplosion(origin, Math.max(radius,power));
		}catch(NoClassDefFoundError e4){
			origin.getWorld().createExplosion(origin, Math.max(radius,power));
		}

		return true;
	}
	
	public static void handleAOEExplosion(Entity shooter, Location loc, double damage, double radius) {
		for(Entity e : loc.getWorld().getNearbyEntities(loc, radius, radius, radius)) {
			if(e instanceof Damageable) {
				Damageable d = (Damageable) e;
				d.damage(damage/e.getLocation().distance(loc), shooter);
			}
		}
	}
}
