package me.zombie_striker.qg.guns.utils;

import me.zombie_striker.qg.Main;

public enum WeaponType {
	PISTOL(true),SMG(true),RPG(true),RIFLE(true),SHOTGUN(true),SNIPER(true),GRENADES(false),MINES(false),MEELEE(false),MISC(false),AMMO(false);
	
	private boolean isGun;
	
	public boolean isGun() {
		return isGun;
	}
	private WeaponType(boolean isGun) {
		this.isGun = isGun;
	}
	
	public static boolean isUnlimited(WeaponType g){
		switch(g){
			case PISTOL:
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
		default:
			break;
		}
		return false;
	}
}
