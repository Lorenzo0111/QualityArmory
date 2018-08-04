package me.zombie_striker.qg.handlers.chargers;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.zombie_striker.qg.guns.Gun;

public interface ChargingHandler {

	public boolean isCharging(Player player);
	public boolean shoot(Gun g, Player player, ItemStack stack);
	
	public String getName();
}
