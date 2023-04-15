package me.zombie_striker.qualityarmory.interfaces;

import me.zombie_striker.customitemmanager.CustomBaseObject;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public interface IEconomy {
    boolean setupEconomy();

    boolean hasEnough(CustomBaseObject base, Player player);
    void pay(CustomBaseObject base, Player player);
    boolean hasEnough(int cost, Player player);
    void pay(int cost, Player player);
    void deposit(int cost, Player player);
}
