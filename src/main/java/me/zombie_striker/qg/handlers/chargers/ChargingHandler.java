package me.zombie_striker.qg.handlers.chargers;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.zombie_striker.qg.guns.Gun;

public interface ChargingHandler {

	public boolean isCharging(Player player);

	public boolean isReadyToFire(final Gun g, final Player player, ItemStack stack);

	public boolean useChargingShoot();

	public void shoot(final Gun g, final Player player, ItemStack stack);
	
	public String getName();
}
