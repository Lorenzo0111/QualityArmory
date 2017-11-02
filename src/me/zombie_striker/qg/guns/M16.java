package me.zombie_striker.qg.guns;

import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.MaterialStorage;
import me.zombie_striker.qg.ammo.AmmoType;

import org.bukkit.inventory.ItemStack;

public class M16 extends DefaultGun {

	public M16(int d,ItemStack[] ing, float damage) {
		super("M16", MaterialStorage.getMS(Main.guntype,7), GunType.RIFLE, true, AmmoType.Ammo556,  0.2,2, 51, damage,d,ing);
	}
}
