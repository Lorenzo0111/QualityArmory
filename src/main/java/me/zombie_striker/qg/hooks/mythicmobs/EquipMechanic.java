package me.zombie_striker.qg.hooks.mythicmobs;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.qg.api.QualityArmory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class EquipMechanic implements INoTargetSkill {

	private final String itemName;
	private final String slot;
	private final float dropChance;

	public EquipMechanic(MythicLineConfig config) {
		this.itemName = config.getString(new String[]{"item", "i", "gun", "g"}, null);
		this.slot = config.getString(new String[]{"slot", "s"}, "HAND").toUpperCase();
		this.dropChance = (float) config.getDouble("dropchance", 0);
	}

	@Override
	public ThreadSafetyLevel getThreadSafetyLevel() {
		return ThreadSafetyLevel.SYNC_ONLY;
	}

	@Override
	public SkillResult cast(SkillMetadata data) {
		Entity caster = data.getCaster().getEntity().getBukkitEntity();
		if (!(caster instanceof LivingEntity))
			return SkillResult.INVALID_TARGET;
		EntityEquipment equipment = ((LivingEntity) caster).getEquipment();
		if (equipment == null)
			return SkillResult.INVALID_TARGET;

		CustomBaseObject object = itemName == null ? null : QualityArmory.getCustomItemByName(itemName);
		if (object == null)
			return SkillResult.INVALID_CONFIG;
		ItemStack item = QualityArmory.getCustomItemAsItemStack(object);

		switch (slot) {
			case "OFFHAND":
			case "OFF_HAND":
				equipment.setItemInOffHand(item);
				equipment.setItemInOffHandDropChance(dropChance);
				break;
			case "HEAD":
			case "HELMET":
				equipment.setHelmet(item);
				equipment.setHelmetDropChance(dropChance);
				break;
			default:
				equipment.setItemInMainHand(item);
				equipment.setItemInMainHandDropChance(dropChance);
				break;
		}
		return SkillResult.SUCCESS;
	}
}
