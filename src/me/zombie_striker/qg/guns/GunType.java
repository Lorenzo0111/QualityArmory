package me.zombie_striker.qg.guns;

import me.zombie_striker.qg.Main;

public enum GunType {
	PISTOL,SMG,RPG,RIFLE,SHOTGUN,SNIPER;
	public static boolean isUnlimited(GunType g){
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
		}
		return false;
	}
}
