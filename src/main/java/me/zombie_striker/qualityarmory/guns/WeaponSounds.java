package me.zombie_striker.qualityarmory.guns;

public enum WeaponSounds {
	GUN_SMALL("bulletsmall"),
	GUN_MEDIUM("bulletmedium"),
	GUN_BIG("bulletbig"),
	GUN_AUTO("bulletauto"),
	GUN_SMALL_AUTO("bulletsmallauto"),

	RELOAD_BULLET("reloadbullet"),
	RELOAD_MAG_IN("reloadmagin"),
	RELOAD_MAG_OUT("reloadmagout"),
	RELOAD_BOLT("reloadbolt"),

	WARHEAD_EXPLODE("warheadexplode"),
	WARHEAD_LAUNCH("warheadlaunch"),
	HONK("honk"),
	LAZERSHOOT("bulletlazer"),

	FLASHBANG("flashbang"),
	METALHIT("metalhit"),
	CHAINS("chainsmall"),
	DRIVING("driving"),
	DRIVING2("driving2"),
	DRIVING3("driving3"),
	CARSKID("carskid"),
	SHOCKWAVE("shockwave"),
	LAZERFIRE("lazerfire"),
	GUN_STARWARS("bulletswblaster"),
	GUN_NEEDLER("bulletneedler"),
	GUN_HALOLAZER("bullethalolazer"),
	GUN_DEAGLE("bulletdeagle"),
	SILENCEDSHOT("bulletsilence"),
	SIREN("siren"),
	ENGINE("engine"),
	HISS("block.lava.extinguish"),
	XP_ORG_PICKUP("entity.experience_orb.pickup"),
	LIGHTSABER_SITH_START("sithsaberstart"),
	LIGHTSABER_LIGHT_START("lightsaberstart"),
	LIGHTSABER_HIT("saberhit"),
	RELOAD_MAG_CLICK("reloadmagclick"),
	OUT_OF_AMMO_CLICK("outofammoclick"),
	RELOAD_CLICK("reloadclick"),
	OPENBAG("openbag"),
	THUMPER("thumper"),
	PARTY_SHOT("partyshot"),
	RELOAD_AK47("reloadak47"),
	RELOAD_SLIDE("reloadslide"),
	GUN_MAUSER("bulletmauser"),
	GUN_AK47("bulletak47"),
	RELOAD_FN("reloadfn"),
	RELOAD_M16("reloadm16"),
	RELOAD_SHOTGUN("reloadshotgun"),
	GUN_SHOTGUN("bulletshotgun"),
	RELOAD_SHELL("reloadshell"),

	DEFAULT("hurt");

	private final String soundname;

	WeaponSounds(String s) {
		this.soundname = s;
	}

	@Deprecated
	public String getName() {
		return soundname;
	}

	public String getSoundName() {
		return soundname;
	}

	public static WeaponSounds getByName(String name) {
		for (WeaponSounds ws : WeaponSounds.values()) {
			if (ws.getName().equals(name))
				return ws;
		}
		return WeaponSounds.DEFAULT;
	}
}
