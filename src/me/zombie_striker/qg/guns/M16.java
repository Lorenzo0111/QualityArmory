package me.zombie_striker.qg.guns;

import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.MaterialStorage;
import me.zombie_striker.qg.ammo.AmmoType;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.guns.utils.WeaponType;

import org.bukkit.inventory.ItemStack;

public class M16 extends DefaultGun {

	public M16(int d,ItemStack[] ing, float damage, double cost) {
		super("M16", MaterialStorage.getMS(Main.guntype,7), WeaponType.RIFLE, true, AmmoType.getAmmo("556"),  0.2,2, 51, damage,true,d,WeaponSounds.GUN_AUTO,cost,ing);
		this.setDelayBetweenShots(0.1);
	}
}
