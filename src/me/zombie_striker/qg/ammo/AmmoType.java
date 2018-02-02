package me.zombie_striker.qg.ammo;

import java.util.HashMap;
import java.util.Set;

public class AmmoType {

	private static HashMap<String, Ammo> ammoHolder = new HashMap<>();
	
	public static void addAmmo(Ammo ammo, String key) {
		ammoHolder.put(key, ammo);
	}
	public static Ammo getAmmo(String key) {
		return ammoHolder.get(key);
	}
	public static Set<String> getKeys(){
		return ammoHolder.keySet();
	}
}
