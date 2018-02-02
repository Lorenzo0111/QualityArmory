package me.zombie_striker.qg.ammo;

import me.zombie_striker.qg.Main;
import org.bukkit.inventory.ItemStack;

public class AmmoShotGun extends DefaultAmmo{

	public AmmoShotGun(ItemStack[] ing, int a,double cost) {
		super("shell","Buckshot",Main.guntype,16,12,true,a,cost,ing);
	}
}
