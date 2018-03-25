package me.zombie_striker.qg.guns;

import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.MaterialStorage;
import me.zombie_striker.qg.ammo.AmmoType;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.guns.utils.WeaponType;

import org.bukkit.inventory.ItemStack;

public class P30 extends DefaultGun{
	
public P30(int d, ItemStack[] ing, float damage, double cost) {
	super("P30",  WeaponType.PISTOL, true, AmmoType.getAmmo("9mm"),  0.2,2, 13, damage,false,d,WeaponSounds.GUN_SMALL,cost,ing);
}

}
