package me.zombie_striker.qg.guns;

import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.MaterialStorage;
import me.zombie_striker.qg.ammo.AmmoType;

import org.bukkit.inventory.ItemStack;

public class AK47 extends DefaultGun {

	public AK47(int d,ItemStack[] ing, float damage) {
		super("AK-47", MaterialStorage.getMS(Main.guntype, 5), WeaponType.RIFLE,false, AmmoType.Ammo556, 0.2,2, 51, damage,true,d,WeaponSounds.GUN_MEDIUM,ing);
	}
}
