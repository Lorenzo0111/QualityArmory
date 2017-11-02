package me.zombie_striker.qg.ammo;

import me.zombie_striker.qg.Main;

public enum AmmoType {
	Ammo556(Ammo556.class),Ammo9mm(Ammo9mm.class),AmmoRPG(AmmoRPG.class),AmmoBuckshot(AmmoShotGun.class);
	
	public Ammo type;
	
	private AmmoType(Class<? extends Ammo> k) {
		type = getAmmoInstance(k);
	}
	public Ammo getType(){
		return type;
	}
	
	private static Ammo getAmmoInstance(Class<? extends Ammo> clas){
		for(Ammo a : Main.ammoRegister.values()){
			if(a.getClass().equals(clas))
				return a;
		}
		return null;
	}
}
