package me.zombie_striker.qg.guns.utils;

import me.zombie_striker.qg.Main;

public enum WeaponType {
	PISTOL(true), SMG(true), RPG(true), RIFLE(true), SHOTGUN(true), SNIPER(true), GRENADES(false), SMOKE_GRENADES(
			false), FLASHBANGS(false), MINES(
					false), MEELEE(false), MISC(false), AMMO(false), KEVLAR(false), MEDKIT(false), LAZER(true);

	private boolean isGun;

	public boolean isGun() {
		return isGun;
	}

	private WeaponType(boolean isGun) {
		this.isGun = isGun;
	}

	public static WeaponType getByName(String name) {
		for (WeaponType w : WeaponType.values()) {
			if (w.name().equals(name))
				return w;
		}
		return MISC;
	}

	public static boolean isUnlimited(WeaponType g) {
		if (!Main.isVersionHigherThan(1, 9))
			return true;
		switch (g) {
		/*case PISTOL:
			return Main.UnlimitedAmmoPistol;
		case RIFLE:
			return Main.UnlimitedAmmoRifle;
		case SMG:
			return Main.UnlimitedAmmoSMG;
		case RPG:
			return Main.UnlimitedAmmoRPG;
		case SNIPER:
			return Main.UnlimitedAmmoSniper;
		case SHOTGUN:
			return Main.UnlimitedAmmoShotgun;
		case LAZER:
			return Main.UnlimitedAmmoLazer;*/
		default:
			break;
		}
		return false;
	}
}
