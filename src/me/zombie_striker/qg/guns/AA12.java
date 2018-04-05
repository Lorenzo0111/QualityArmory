package me.zombie_striker.qg.guns;

import org.bukkit.inventory.ItemStack;

import me.zombie_striker.qg.ammo.AmmoType;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.guns.utils.WeaponType;

public class AA12 extends DefaultGun {

	public AA12(int d, ItemStack[] ing, float damage, double cost) {
		super("AA12", WeaponType.SHOTGUN, false, AmmoType.getAmmo("shell"), 0.2, 2.0, 32, damage, true, d,
				WeaponSounds.GUN_BIG, cost, ing);
		this.setBulletsPerShot(10);
		this.setDelayBetweenShots(0.29);
	}
}
