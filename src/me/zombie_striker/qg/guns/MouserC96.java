package me.zombie_striker.qg.guns;

import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.MaterialStorage;
import me.zombie_striker.qg.ammo.AmmoType;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.guns.utils.WeaponType;

import org.bukkit.inventory.ItemStack;

public class MouserC96 extends DefaultGun {
	public MouserC96(int d,ItemStack[] ing, float damage, double cost) {
		super("Mouser-C96", MaterialStorage.getMS(Main.guntype,20), WeaponType.PISTOL, true, AmmoType.getAmmo("9mm"),  0.2,2, 9, damage,false,d,WeaponSounds.GUN_SMALL,cost,ing);
		this.setDelayBetweenShots(0.1);
	}

}
