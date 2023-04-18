package me.zombie_striker.qualityarmory.hooks.anticheat;

import me.zombie_striker.qualityarmory.QAMain;
import me.zombie_striker.qualityarmory.api.QualityArmory;
import me.zombie_striker.qualityarmory.guns.Gun;
import me.zombie_striker.qualityarmory.interfaces.IHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public abstract class AntiCheatHook implements Listener, IHandler {

    private QAMain main;

    @Override
    public void init(QAMain main) {
        this.main = main;
        Bukkit.getPluginManager().registerEvents(this, main);
    }

    public abstract String getName();

    public void onViolation(Player player, Cancellable cancellable) {
        Gun gun = QualityArmory.getInstance().getGunInHand(player);
        if (gun == null) return;

        cancellable.setCancelled(true);
        QualityArmory.getInstance().DEBUG("Cancelled " + this.getName() + " violation because player is using gun.");
    }

    public void registerHook(String name, @NotNull Class<? extends AntiCheatHook> hook) {
        if (Bukkit.getPluginManager().isPluginEnabled(name)) {
            try {
                AntiCheatHook aHook = hook.getConstructor().newInstance();
                main.getLogger().info("Found " + aHook.getName() + " AntiCheat. Loaded support");
            } catch (Throwable ignored) {
            }
        }
    }
}
