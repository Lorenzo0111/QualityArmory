package me.zombie_striker.qg.handlers.gunvalues;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.zombie_striker.qg.guns.Gun;

public interface ChargingHandler {

	public boolean isCharging(Player player);
	public boolean isReloading(Player player);
	public boolean shoot(Gun g, Player player, ItemStack stack);
	public double reload(Player player, Gun g, int amountReloading);
	
	public String getName();
}
