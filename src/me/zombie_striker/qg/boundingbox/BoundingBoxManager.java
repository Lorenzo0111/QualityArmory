package me.zombie_striker.qg.boundingbox;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class BoundingBoxManager {

	private static HashMap<String, AbstractBoundingBox> boundboxTypes = new HashMap<>();
	private static HashMap<UUID, AbstractBoundingBox> entityBoundbox = new HashMap<>();
	private static HashMap<EntityType, AbstractBoundingBox> entityTypeBoundingBox = new HashMap<>();

	private static DefaultBoundingBox DEFUALT = new DefaultBoundingBox();

	public static void addBoundingBox(String name, AbstractBoundingBox box) {
		boundboxTypes.put(name, box);
	}

	public static AbstractBoundingBox getBoxByName(String name) {
		return boundboxTypes.get(name);
	}

	public static AbstractBoundingBox getBoundingBox(Entity base) {
		if (entityBoundbox.containsKey(base.getUniqueId()))
			return entityBoundbox.get(base.getUniqueId());
		if (entityTypeBoundingBox.containsKey(base.getType()))
			return entityTypeBoundingBox.get(base.getType());
		return DEFUALT;
	}

	public static void setEntityTypeBoundingBox(EntityType type, String boundingBoxName) {
		entityTypeBoundingBox.put(type, getBoxByName(boundingBoxName));
	}

	public static void setEntityBoundingBox(Entity base, String boundingBoxName) {
		entityBoundbox.put(base.getUniqueId(), getBoxByName(boundingBoxName));
	}
}
