package me.zombie_striker.qualityarmory.guns.chargers;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.zombie_striker.qualityarmory.guns.Gun;

public interface ChargingHandler {

	public boolean isCharging(Player player);
	public boolean shoot(Gun g, Player player, ItemStack stack);
	
	public String getName();

	String getDefaultChargingSound();
}
