package me.zombie_striker.qg.guns;

import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.MaterialStorage;
import me.zombie_striker.qg.ammo.AmmoType;

import org.bukkit.inventory.ItemStack;

public class SW1911 extends DefaultGun {	
	public SW1911(int d,ItemStack[] ing, float damage) {
		super("SW-1911", MaterialStorage.getMS(Main.guntype,12), GunType.PISTOL, true, AmmoType.Ammo9mm,  0.2,2, 13,damage,d,ing);
	}
}
