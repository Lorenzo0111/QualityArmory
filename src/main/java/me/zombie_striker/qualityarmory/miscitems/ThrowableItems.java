package me.zombie_striker.qualityarmory.miscitems;

import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.qualityarmory.guns.ThrownObject;
import org.bukkit.entity.Entity;

import java.util.HashMap;

public class ThrowableItems extends CustomBaseObject {

	HashMap<Entity, ThrownObject> throwItems = new HashMap<>();

	public ThrowableItems(String name, MaterialStorage storage) {
		super(name, storage);
	}

}
