package me.zombie_striker.qg.ammo;

import me.zombie_striker.qg.Main;
import org.bukkit.inventory.ItemStack;

public class AmmoRPG extends DefaultAmmo {

	public AmmoRPG(ItemStack[] ing, int a,double cost) {
		super("rocket","Rocket",Main.guntype,17,1,true,a,cost,ing);
	}
}
