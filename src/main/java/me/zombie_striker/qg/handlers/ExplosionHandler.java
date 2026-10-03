package me.zombie_striker.qg.handlers;

import me.zombie_striker.qg.hooks.protection.ProtectionHandler;
import org.bukkit.Location;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;


public class ExplosionHandler {

	// private static List<Material> indestruct = Arrays.asList(Material.OBSIDIAN,
	// Material.BEDROCK, Material.OBSERVER,
	// Material.FURNACE, Material.WATER, Material.STATIONARY_LAVA, Material.LAVA,
	// Material.STATIONARY_WATER,
	// Material.COMMAND, Material.COMMAND_CHAIN, Material.COMMAND_MINECART,
	// Material.COMMAND_REPEATING);

	public static boolean handleExplosion(Location origin, int radius, int power) {
		GunDamageHandler.begin();
		try{
			if(!ProtectionHandler.canExplode(origin)) {
				origin.getWorld().createExplosion(origin, 0);
				return false;
			}

			origin.getWorld().createExplosion(origin, Math.max(radius,power));
		}catch(NoClassDefFoundError e4){
			origin.getWorld().createExplosion(origin, Math.max(radius,power));
		}finally{
			GunDamageHandler.end();
		}

		return true;
	}
	
	public static void handleAOEExplosion(Entity shooter, Location loc, double damage, double radius) {
		handleAOEExplosion(shooter, loc, damage, radius, 0);
	}

	public static void handleAOEExplosion(Entity shooter, Location loc, double damage, double radius, double knockback) {
		for(Entity e : loc.getWorld().getNearbyEntities(loc, radius, radius, radius)) {
			if(e instanceof Damageable) {
				Damageable d = (Damageable) e;
				double distance = e.getLocation().distance(loc);
				GunDamageHandler.damage(d, damage/distance, shooter, false);

				if (knockback != 0 && e instanceof LivingEntity && distance < radius) {
					// Push the entity away from the explosion: the closer it is, the stronger the push
					Vector push = e.getLocation().add(0, 0.5, 0).toVector().subtract(loc.toVector());
					if (push.lengthSquared() < 0.01)
						push = new Vector(0, 1, 0);
					e.setVelocity(e.getVelocity().add(push.normalize().multiply(knockback * (1 - distance / radius))));
				}
			}
		}
	}
}
