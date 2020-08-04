package me.zombie_striker.customitemmanager;

import java.util.List;

import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public interface ArmoryBaseObject {
	
	 ItemStack getItemStack();
	
	 boolean is18Support();

	 void set18Supported(boolean b);
	
	 boolean onRMB(Player shooter, ItemStack usedItem);

	 boolean onShift(Player shooter, ItemStack usedItem, boolean toggle);

	 boolean onLMB(Player shooter, ItemStack usedItem);

	boolean onSwapTo(Player shooter, ItemStack usedItem);

	boolean onSwapAway(Player shooter, ItemStack usedItem);

}
