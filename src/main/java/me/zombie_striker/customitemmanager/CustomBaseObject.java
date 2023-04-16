package me.zombie_striker.customitemmanager;

import java.util.HashMap;
import java.util.UUID;

public class CustomBaseObject {

	private final UUID uuid = UUID.randomUUID();
	private final String name;
	private final MaterialStorage base;

	private HashMap<String, Object> data = new HashMap<>();

	public CustomBaseObject(String name, MaterialStorage storage) {
		this.name = name;
		this.base = storage;
	}

	public String getName() {
		return name;
	}

	public MaterialStorage getItemData() {
		return base;
	}

	public HashMap<String, Object> getAllData() {
		return data;
	}

	public void setData(String key, Object data) {
		this.data.put(key,data);
	}

	public UUID getUuid() {
		return uuid;
	}
}
