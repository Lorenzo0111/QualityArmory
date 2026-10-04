package me.zombie_striker.qg.hooks.mythicmobs;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.*;
import io.lumine.mythic.bukkit.BukkitAdapter;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.guns.utils.EntityGunUtil;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;

public class ShootMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {

	private final String gunName;
	private final double sway;

	public ShootMechanic(MythicLineConfig config) {
		this.gunName = config.getString(new String[]{"gun", "g", "weapon"}, null);
		this.sway = config.getDouble("sway", 1.0);
	}

	@Override
	public ThreadSafetyLevel getThreadSafetyLevel() {
		return ThreadSafetyLevel.SYNC_ONLY;
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		Entity entity = target.getBukkitEntity();
		Location location;
		if (entity instanceof LivingEntity) {
			// Aim at the chest instead of the eyes
			location = entity.getLocation().add(0, ((LivingEntity) entity).getEyeHeight() * 0.75, 0);
		} else {
			location = entity.getLocation();
		}
		return shoot(data, location);
	}

	@Override
	public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
		return shoot(data, BukkitAdapter.adapt(target));
	}

	private SkillResult shoot(SkillMetadata data, Location target) {
		Entity caster = data.getCaster().getEntity().getBukkitEntity();
		if (!(caster instanceof LivingEntity))
			return SkillResult.INVALID_TARGET;
		LivingEntity shooter = (LivingEntity) caster;

		Gun gun = getGun(shooter);
		if (gun == null)
			return SkillResult.INVALID_CONFIG;
		if (target.getWorld() != shooter.getWorld())
			return SkillResult.INVALID_TARGET;

		EntityGunUtil.shoot(gun, shooter, target.toVector().subtract(shooter.getEyeLocation().toVector()), sway);
		return SkillResult.SUCCESS;
	}

	private Gun getGun(LivingEntity shooter) {
		if (gunName != null)
			return QualityArmory.getGunByName(gunName);

		// No gun specified: use the one the mob is holding
		EntityEquipment equipment = shooter.getEquipment();
		if (equipment == null)
			return null;
		return QualityArmory.getGun(equipment.getItemInMainHand());
	}
}
