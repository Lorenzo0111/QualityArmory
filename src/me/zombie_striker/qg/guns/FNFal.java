package me.zombie_striker.qg.guns;

import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.MaterialStorage;
import me.zombie_striker.qg.ammo.AmmoType;

import org.bukkit.inventory.ItemStack;

public class FNFal extends DefaultGun {

	public FNFal(int d,ItemStack[] ing, float damage) {
		super("FN-FAL", MaterialStorage.getMS(Main.guntype, 9), WeaponType.RIFLE,false, AmmoType.Ammo556, 0.2,2, 51, damage,true,d,WeaponSounds.GUN_MEDIUM,ing);
	}
}
