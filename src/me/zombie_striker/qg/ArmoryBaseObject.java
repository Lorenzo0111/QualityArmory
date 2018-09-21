package me.zombie_striker.qg;

import java.util.List;

import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public interface ArmoryBaseObject {

	public String getName();
	public ItemStack[] getIngredients();
	public int getCraftingReturn();
	public MaterialStorage getItemData();
	
	public List<String> getCustomLore();
	public String getDisplayName();
	
	public double cost();
	
	public ItemStack getItemStack();
	
	public boolean is18Support();

	public void set18Supported(boolean b);
	
	public void onRMB(PlayerInteractEvent e, ItemStack usedItem);
	
	public void onLMB(PlayerInteractEvent e, ItemStack usedItem);
}
