package me.zombie_striker.qg.guns;

import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.MaterialStorage;
import me.zombie_striker.qg.ammo.AmmoType;

import org.bukkit.inventory.ItemStack;

public class P30 extends DefaultGun{
	
public P30(int d, ItemStack[] ing, float damage) {
	super("P30", MaterialStorage.getMS(Main.guntype,2), WeaponType.PISTOL, true, AmmoType.Ammo9mm,  0.2,2, 13, damage,false,d,WeaponSounds.GUN_SMALL,ing);
}

}
