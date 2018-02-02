package me.zombie_striker.qg.guns;

import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.MaterialStorage;
import me.zombie_striker.qg.ammo.AmmoType;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.guns.utils.WeaponType;

import org.bukkit.inventory.ItemStack;

public class UMP extends DefaultGun {
	
	public UMP(int d,ItemStack[] ing, float damage, double cost) {
		super("UMP", MaterialStorage.getMS(Main.guntype,11), WeaponType.SMG,false, AmmoType.getAmmo("9mm"),  0.3,2, 40, damage,true,d,WeaponSounds.GUN_SMALL,cost,ing);
	}
}
