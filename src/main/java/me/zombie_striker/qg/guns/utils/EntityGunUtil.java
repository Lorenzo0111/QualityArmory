package me.zombie_striker.qg.guns.utils;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.armor.BulletProtectionUtil;
import me.zombie_striker.qg.boundingbox.AbstractBoundingBox;
import me.zombie_striker.qg.boundingbox.BoundingBoxManager;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.handlers.GunDamageHandler;
import me.zombie_striker.qg.handlers.ParticleHandlers;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Lets entities that are not players (for example mobs) shoot guns.
 */
public class EntityGunUtil {

	/**
	 * Shoots a gun from the eyes of an entity. The entity does not need to hold the
	 * gun and no ammo is used.
	 *
	 * @param direction      the direction of the shot.
	 * @param swayMultiplier multiplier for the sway of the gun: 0 is a perfect aim.
	 */
	public static void shoot(Gun g, LivingEntity shooter, Vector direction, double swayMultiplier) {
		if (direction.lengthSquared() == 0)
			return;
		if (!QualityArmory.allowGunsInRegion(shooter.getLocation()))
			return;

		double sway = g.getSway() * swayMultiplier;
		for (int i = 0; i < g.getBulletsPerShot(); i++) {
			shootBullet(g, shooter, direction, sway);
		}

		String sound = g.getWeaponSound();
		if (g.getWeaponSounds() != null && g.getWeaponSounds().size() > 1)
			sound = g.getWeaponSounds().get(ThreadLocalRandom.current().nextInt(g.getWeaponSounds().size()));
		if (sound != null)
			shooter.getWorld().playSound(shooter.getLocation(), sound, (float) g.getVolume(), 1);
	}

	private static void shootBullet(Gun g, LivingEntity shooter, Vector direction, double sway) {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		Location start = shooter.getEyeLocation();

		Vector normalizedDirection = direction.clone().normalize();
		if (sway > 0) {
			normalizedDirection.add(new Vector(random.nextDouble(-sway, sway), random.nextDouble(-sway, sway),
					random.nextDouble(-sway, sway))).normalize();
		}
		Vector step = normalizedDirection.clone().multiply(QAMain.bulletStep);

		double maxDistance = GunUtil.getTargetedSolidMaxDistance(step, start, g.getMaxDistance());

		Entity hitTarget = null;
		AbstractBoundingBox hitBox = null;
		Location bulletHitLoc = null;
		double hitDistance = maxDistance;

		Location center = start.clone().add(normalizedDirection.clone().multiply(maxDistance / 2));
		for (Entity e : start.getWorld().getNearbyEntities(center, maxDistance / 2, maxDistance / 2, maxDistance / 2)) {
			if (!(e instanceof Damageable) || e == shooter || e == shooter.getVehicle() || e.getVehicle() == shooter)
				continue;
			if (QAMain.avoidTypes.contains(e.getType()))
				continue;
			if (e instanceof Player && ((Player) e).getGameMode() == GameMode.SPECTATOR)
				continue;

			double entityDistance = e.getLocation().distance(start);
			if (entityDistance >= hitDistance)
				continue;

			AbstractBoundingBox box = BoundingBoxManager.getBoundingBox(e);
			double checkDistanceMax = box.maximumCheckingDistance(e);
			double startDistance = Math.max(entityDistance - checkDistanceMax, 0);

			// Only check the part of the bullet path that is close to the entity
			Location test = start.clone().add(normalizedDirection.clone().multiply(startDistance));
			for (double distance = startDistance; distance < entityDistance + checkDistanceMax; distance += step.length()) {
				test.add(step);
				if (box.intersects(shooter, test, e)) {
					hitTarget = e;
					hitBox = box;
					bulletHitLoc = test;
					hitDistance = entityDistance;
					break;
				}
			}
		}

		if (hitTarget != null && QualityArmory.allowGunsInRegion(hitTarget.getLocation())) {
			boolean headshot = hitBox.allowsHeadshots() && hitBox.intersectsHead(bulletHitLoc, hitTarget);
			boolean bulletProtection = false;
			if (hitTarget instanceof Player) {
				bulletProtection = BulletProtectionUtil.stoppedBullet((Player) hitTarget, bulletHitLoc, normalizedDirection);
				if (headshot && BulletProtectionUtil.negatesHeadshot((Player) hitTarget))
					headshot = false;
			}

			double damage = g.getDamage() * (bulletProtection ? 0.1 : 1) * (headshot ? g.getHeadshotMultiplier() : 1);
			if (hitTarget instanceof LivingEntity)
				((LivingEntity) hitTarget).setNoDamageTicks(0);
			GunDamageHandler.damage((Damageable) hitTarget, damage, shooter, true);
		}

		if (QAMain.enableBulletTrails) {
			Location trail = start.clone();
			Vector stepSmoke = normalizedDirection.clone().multiply(QAMain.smokeSpacing);
			for (double distance = 0; distance < hitDistance; distance += QAMain.smokeSpacing) {
				trail.add(stepSmoke);
				if (trail.getBlock().getType() != Material.AIR && GunUtil.isSolid(trail.getBlock(), trail))
					break;
				ParticleHandlers.spawnGunParticles(g, trail);
			}
		}
	}
}
