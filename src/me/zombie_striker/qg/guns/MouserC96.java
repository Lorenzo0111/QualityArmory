package me.zombie_striker.qg.guns;

import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.MaterialStorage;
import me.zombie_striker.qg.ammo.AmmoType;

import org.bukkit.inventory.ItemStack;

public class MouserC96 extends DefaultGun {
	public MouserC96(int d,ItemStack[] ing, float damage) {
		super("Mouser-C96", MaterialStorage.getMS(Main.guntype,20), WeaponType.PISTOL, true, AmmoType.Ammo9mm,  0.2,2, 9, damage,false,d,WeaponSounds.GUN_SMALL,ing);
	}

}
