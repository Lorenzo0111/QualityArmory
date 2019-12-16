package me.zombie_striker.qg;

import java.util.List;

import me.zombie_striker.customitemmanager.MaterialStorage;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public interface ArmoryBaseObject {

	 String getName();
	 ItemStack[] getIngredients();
	 int getCraftingReturn();
	 MaterialStorage getItemData();
	
	 List<String> getCustomLore();
	 void setCustomLore(List<String> lore);
	 String getDisplayName();
	
	 double cost();
	
	 ItemStack getItemStack();
	
	 boolean is18Support();

	 void set18Supported(boolean b);
	
	 boolean onRMB(Player shooter, ItemStack usedItem);
	
	 boolean onLMB(Player shooter, ItemStack usedItem);
}
