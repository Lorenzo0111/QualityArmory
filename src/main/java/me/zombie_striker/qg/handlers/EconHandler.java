package me.zombie_striker.qg.handlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import me.zombie_striker.customitemmanager.CustomBaseObject;
import net.milkbowl.vault.economy.Economy;

public class EconHandler {

    public static Economy econ;

	public static boolean setupEconomy() {
		if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) return false;

		RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) return false;

		econ = rsp.getProvider();
		return true;
	}
	
	public static boolean hasEnough(CustomBaseObject base, Player player) {
		return econ.has(player, base.getPrice());
	}

	public static void pay(CustomBaseObject base, Player player) {
		econ.withdrawPlayer(player, base.getPrice());
	}

	public static boolean hasEnough(int cost, Player player) {
		return econ.has(player, cost);
	}

	public static void pay(int cost, Player player) {
		econ.withdrawPlayer(player, cost);
	}

	public static void deposit(int cost, Player player) {
		econ.depositPlayer(player, cost);
	}
}
