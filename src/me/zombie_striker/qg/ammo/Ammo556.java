package me.zombie_striker.qg.ammo;

import me.zombie_striker.qg.Main;
import org.bukkit.inventory.ItemStack;

public class Ammo556 extends DefaultAmmo {

	public Ammo556(ItemStack[] ing, int a, double cost) {
		super("556","5.56x45mm",Main.guntype,14,50,true,a,cost,ing);
	}

}
