package me.zombie_striker.qg.guns;

public enum WeaponSounds {
	GUN_SMALL("bulletsmall"),GUN_MEDIUM("bulletmedium"),GUN_BIG("bulletbig"),GUN_AUTO("bulletauto"),
	RELOAD_BULLET("reloadbullet"),RELOAD_MAG_IN("reloadmagin"),RELOAD_MAG_OUT("reloadmagout"),RELOAD_BOLT("reloadbolt"),
	
	DEFAULT("hurt");
	
	private String soundname;
	
	private WeaponSounds(String s) {
		this.soundname= s;
	}
	public String getName() {
		return soundname;
	}
}
