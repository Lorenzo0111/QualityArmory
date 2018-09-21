package me.zombie_striker.qg.armor;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import me.zombie_striker.qg.MaterialStorage;

public class Helmet extends ArmorObject {

	public Helmet(String name, String displayname, List<String> lore, ItemStack[] ing, MaterialStorage st,
			double cost) {
		super(name, displayname, lore, ing, st, cost);
	}

}
