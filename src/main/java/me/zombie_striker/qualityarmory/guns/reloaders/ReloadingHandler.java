package me.zombie_striker.qualityarmory.guns.reloaders;

import org.bukkit.entity.Player;

import me.zombie_striker.qualityarmory.guns.Gun;

public interface ReloadingHandler {

	public boolean isReloading(Player player);
	public double reload(Player player, Gun g, int amountReloading);
	
	public String getName();

	String getDefaultReloadingSound();
}
