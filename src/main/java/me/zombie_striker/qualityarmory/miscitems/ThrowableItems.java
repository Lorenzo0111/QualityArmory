package me.zombie_striker.qualityarmory.miscitems;

import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.customitemmanager.MaterialStorage;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public class ThrowableItems extends CustomBaseObject {

	HashMap<Entity, ThrowableHolder> throwItems = new HashMap<>();

	public ThrowableItems(String name, MaterialStorage storage) {
		super(name, storage);
	}

}
