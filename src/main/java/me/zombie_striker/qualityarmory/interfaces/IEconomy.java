package me.zombie_striker.qualityarmory.interfaces;

import org.bukkit.entity.Player;

public interface IEconomy extends IHandler {
    boolean setupEconomy();

    boolean hasEnough(double amount, Player player);
    void pay(double amount, Player player);
    void deposit(double amount, Player player);
}
