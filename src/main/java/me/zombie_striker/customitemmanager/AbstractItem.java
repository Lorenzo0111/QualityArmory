package me.zombie_striker.customitemmanager;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public abstract class AbstractItem {

	public abstract ItemStack getItem(Material material, int data, int variant);

	public abstract boolean isCustomItem(ItemStack is);

	public abstract void initItems(File dataFolder);

	public abstract void initIronSights(File dataFolder);

	public abstract AbstractItemFact getItemFactory();


}
