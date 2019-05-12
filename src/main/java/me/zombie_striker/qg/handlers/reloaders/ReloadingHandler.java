package me.zombie_striker.qg.handlers.reloaders;

import org.bukkit.entity.Player;

import me.zombie_striker.qg.guns.Gun;

public interface ReloadingHandler {

	public boolean isReloading(Player player);
	public double reload(Player player, Gun g, int amountReloading);
	
	public String getName();
}
