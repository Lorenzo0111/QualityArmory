package me.zombie_striker.qg.guns.utils;

public enum WeaponSounds {
	GUN_SMALL("bulletsmall"), GUN_MEDIUM("bulletmedium"), GUN_BIG("bulletbig"), GUN_AUTO("bulletauto"), GUN_SMALL_AUTO("bulletsmallauto"), 
	
	RELOAD_BULLET("reloadbullet"), RELOAD_MAG_IN("reloadmagin"), RELOAD_MAG_OUT("reloadmagout"), RELOAD_BOLT("reloadbolt"),

	WARHEAD_EXPLODE("warheadexplode"), WARHEAD_LAUNCH("warheadlaunch"), 
	
	LAZERSHOOT("bulletlazer"),

	FLASHBANG("flashbang"), METALHIT("metalhit"), CHAINS("chainsmall"),
	
	
	
	SHOCKWAVE("shockwave"),LAZERFIRE("lazerfire"),GUN_STARWARS("bulletswblaster"),GUN_NEEDLER("bulletneedler"),GUN_HALOLAZER("bullethalolazer"),GUN_DEAGLE("bulletdeagle"),	
	
	SILENCEDSHOT("bulletsilence"),
	
	HISS("block.lava.extinguish"),

	DEFAULT("hurt");

	private String soundname;

	private WeaponSounds(String s) {
		this.soundname = s;
	}

	public String getName() {
		return soundname;
	}

	public static WeaponSounds getByName(String name) {
		for (WeaponSounds ws : WeaponSounds.values()) {
			if (ws.getName().equals(name))
				return ws;
		}
		return WeaponSounds.DEFAULT;
	}
	
	public static String getSoundByType(WeaponType type) {

		if (type == WeaponType.PISTOL || type == WeaponType.SMG)
			return WeaponSounds.GUN_SMALL.getName();
		if (type == WeaponType.SHOTGUN || type == WeaponType.SNIPER)
			return WeaponSounds.GUN_BIG.getName();
		if (type == WeaponType.RPG)
			return WeaponSounds.WARHEAD_LAUNCH.getName();
		if (type == WeaponType.LAZER)
			return WeaponSounds.LAZERSHOOT.getName();
		return WeaponSounds.GUN_MEDIUM.getName();
	}
}
