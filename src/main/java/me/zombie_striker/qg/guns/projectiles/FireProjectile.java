package me.zombie_striker.qg.guns.projectiles;

import java.util.ArrayList;
import java.util.Collection;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.boundingbox.AbstractBoundingBox;
import me.zombie_striker.qg.boundingbox.BoundingBoxManager;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.guns.utils.GunUtil;

import me.zombie_striker.qg.hooks.protection.ProtectionHandler;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class FireProjectile implements RealtimeCalculationProjectile {
	public FireProjectile() {
		ProjectileManager.add(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void spawn(final Gun g, final Location s, final Player player, final Vector dir) {
		Location test = player.getEyeLocation();
		double maxDist = GunUtil.getTargetedSolidMaxDistance(dir, test, g.getMaxDistance());

		Vector dir2 = dir.clone().multiply(QAMain.bulletStep);

		Collection<Entity> nearby = test.getWorld().getNearbyEntities(
				test.clone().add(dir.clone().multiply(maxDist / 2)), maxDist / 2, maxDist / 2, maxDist / 2);

		// test.add(dir);
		for (double distance = 0; distance < maxDist; distance += QAMain.bulletStep) {
			test.add(dir2);
			if (test.getBlock().getType().name().equals("WATER")
					|| test.getBlock().getType().name().equals("STATIONARY_WATER"))
				break;
			for (Entity e : new ArrayList<>(nearby)) {
				if (e != player && e != player.getVehicle() && e != player.getPassenger()) {
					AbstractBoundingBox box = BoundingBoxManager.getBoundingBox(e);
					if (box.intersects(player,test, e)) {
						if (e instanceof Damageable) {
							((Damageable) e).damage(g.getDurabilityDamage(), player);
							if (e instanceof LivingEntity) {
								((LivingEntity) e).setNoDamageTicks(0);
								try {
									if (ProtectionHandler.canPvp(e.getLocation())) {
										e.setFireTicks(20*3);
									}
								}catch (Error error){
									QAMain.DEBUG("Cannot use protection hook: " + error.getMessage());
									e.setFireTicks(20*3);
								}
							}
						}
						nearby.remove(e);
					} // else if (distance * distance > e.getLocation().distance(start)) {
						// nearby.remove(e);
						// }
				}
			}

			try {
				test.getWorld().spawnParticle(Particle.FLAME, test, 0);
			} catch (Error | Exception e) {
			}
		}
	}

	@Override
	public String getName() {
		return ProjectileManager.FIRE;
	}
}
