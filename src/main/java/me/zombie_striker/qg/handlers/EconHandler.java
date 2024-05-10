package me.zombie_striker.qg.handlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import me.zombie_striker.customitemmanager.CustomBaseObject;
import net.milkbowl.vault.economy.Economy;

public class EconHandler {

    public static Economy econ;

    public static boolean setupEconomy() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        final RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        EconHandler.econ = rsp.getProvider();
        return EconHandler.econ != null;
    }

    public static boolean hasEnough(final CustomBaseObject base, final Player player) {
        return (EconHandler.econ.getBalance(player) >= base.getPrice());
    }

    public static void pay(final CustomBaseObject base, final Player player) { EconHandler.econ.withdrawPlayer(player, base.getPrice()); }

    public static boolean hasEnough(final int cost, final Player player) { return (EconHandler.econ.getBalance(player) >= cost); }

    public static void pay(final int cost, final Player player) { EconHandler.econ.withdrawPlayer(player, cost); }

    public static void deposit(final int cost, final Player player) { EconHandler.econ.depositPlayer(player, cost); }
}
