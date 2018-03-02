package me.zombie_striker.qg.guns;

import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.MaterialStorage;
import me.zombie_striker.qg.ammo.AmmoType;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.guns.utils.WeaponType;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public class Spas12 extends DefaultGun{
	
public Spas12(int d, ItemStack[] ing, float damage, double cost) {
	super("Spas12", MaterialStorage.getMS(Main.guntype,24), WeaponType.SHOTGUN, false, AmmoType.getAmmo("shell"),  0.2,2, 7, damage,false,d,WeaponSounds.GUN_MEDIUM,null,ChatColor.GOLD+"Spas-12",cost,ing);
	this.setBulletsPerShot(15);
}

}
