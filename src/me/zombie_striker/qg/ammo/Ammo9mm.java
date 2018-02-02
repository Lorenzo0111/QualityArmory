package me.zombie_striker.qg.ammo;

import me.zombie_striker.qg.Main;
import org.bukkit.inventory.ItemStack;

public class Ammo9mm extends DefaultAmmo {

	public Ammo9mm(ItemStack[] ing, int a,double cost) {
		super("9mm","9mm.ACP",Main.guntype,15,50,true,a,cost,ing);
	}
}
