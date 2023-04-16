package me.zombie_striker.qualityarmory.handlers;

import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.qualityarmory.QAMain;
import me.zombie_striker.qualityarmory.interfaces.IEconomy;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

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

    public boolean hasEnough(double amount, Player player) {
        return (econ.getBalance(player) >= amount);
    }

    public void pay(double amount, Player player) {
        econ.withdrawPlayer(player, amount);
    }

    public void deposit(double cost, Player player) {
        econ.depositPlayer(player, cost);
    }

    @Override
    public void init(QAMain main) {
        setupEconomy();
    }
}
