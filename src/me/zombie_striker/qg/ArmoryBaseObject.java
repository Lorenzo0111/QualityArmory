package me.zombie_striker.qg;

import org.bukkit.inventory.ItemStack;

public interface ArmoryBaseObject {

	public String getName();
	public ItemStack[] getIngredients();
	public int getCraftingReturn();
	public MaterialStorage getItemData();
}
