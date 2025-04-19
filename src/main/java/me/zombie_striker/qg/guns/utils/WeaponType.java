package me.zombie_striker.qg.guns.utils;

public enum WeaponType {
	PISTOL(true), SMG(true), RPG(true), RIFLE(true), SHOTGUN(true), FLAMER(true), SNIPER(true), BIG_GUN(true), GRENADES(false), SMOKE_GRENADES(
			false), FLASHBANGS(false), INCENDARY_GRENADES(false),MOLOTOV(false),PROXYMINES(false), STICKYGRENADE(false), MINES(
					false), MELEE(false),  MISC(false), AMMO(false), HELMET(false), MEDKIT(false), AMMO_BAG(false), AMMO_BOX(false), LAZER(true),BACKPACK(false),PARACHUTE(false),CUSTOM(false);

	private boolean isGun;

	public boolean isGun() {
		return isGun;
	}

	WeaponType(boolean isGun) {
		this.isGun = isGun;
	}

	public static WeaponType getByName(String name) {
		for (WeaponType w : WeaponType.values()) {
			if (w.name().equals(name))
				return w;
		}
		return MISC;
	}
}
