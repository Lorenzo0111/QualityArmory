package me.zombie_striker.customitemmanager;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class AbstractItemFact {

	public abstract ItemStack getItem(MaterialStorage materialStorage, int amount);
}
