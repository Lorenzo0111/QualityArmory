package me.zombie_striker.qg.handlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import me.zombie_striker.qg.ArmoryBaseObject;
import net.milkbowl.vault.economy.Economy;

public class EconHandler {
	
	public static Economy econ;

	public static boolean setupEconomy() {
		if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}
	
	public static boolean hasEnough(ArmoryBaseObject base, Player player) {
		return (econ.getBalance(player) >=base.cost());
	}
	public static void pay(ArmoryBaseObject base, Player player) {
		econ.withdrawPlayer(player, base.cost());
	}
}
