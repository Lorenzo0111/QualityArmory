package me.zombie_striker.qualityarmory.armor;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import me.zombie_striker.customitemmanager.MaterialStorage;

public class Helmet extends ArmorObject {

	public Helmet(String name, String displayname, List<String> lore, ItemStack[] ing, MaterialStorage st,
				  double cost) {
		this(name, displayname, lore, ing, st, cost, cost > 0);
	}

	public Helmet(String name, String displayname, List<String> lore, ItemStack[] ing, MaterialStorage st,
			double cost, boolean allowInShop) {
		super(name, displayname, lore, ing, st, cost);
		this.setEnableShop(allowInShop);
	}

}
