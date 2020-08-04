package me.zombie_striker.qg.handlers;

import org.bukkit.Location;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;


public class ExplosionHandler {

	// private static List<Material> indestruct = Arrays.asList(Material.OBSIDIAN,
	// Material.BEDROCK, Material.OBSERVER,
	// Material.FURNACE, Material.WATER, Material.STATIONARY_LAVA, Material.LAVA,
	// Material.STATIONARY_WATER,
	// Material.COMMAND, Material.COMMAND_CHAIN, Material.COMMAND_MINECART,
	// Material.COMMAND_REPEATING);

	public static void handleExplosion(Location origin, int radius, int power) {
		try{
			if(WorldGuardSupport.canExplode(origin)) {
				origin.getWorld().createExplosion(origin, power);
			}else{
				origin.getWorld().createExplosion(origin, 0);
			}
			if(false)
				throw new ClassNotFoundException();
		}catch(ClassNotFoundException e4){
			origin.getWorld().createExplosion(origin, power);
		}
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
