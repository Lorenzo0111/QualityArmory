package me.zombie_striker.qualityarmory.handlers;

import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.qualityarmory.QAMain;
import me.zombie_striker.qualityarmory.interfaces.IEconomy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;

public class EconHandler implements IEconomy {
	
	public Economy econ;

	public boolean setupEconomy() {
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
	
	public boolean hasEnough(CustomBaseObject base, Player player) {
		return (econ.getBalance(player) >= base.getPrice());
	}
	public void pay(CustomBaseObject base, Player player) {
		econ.withdrawPlayer(player, base.getPrice());
	}
	public boolean hasEnough(int cost, Player player) {
		return (econ.getBalance(player) >= cost);
	}
	public void pay(int cost, Player player) {
		econ.withdrawPlayer(player, cost);
	}
	public void deposit(int cost, Player player) {
		econ.depositPlayer(player, cost);
	}

	@Override
	public void init(QAMain main) {
		setupEconomy();
	}
}
