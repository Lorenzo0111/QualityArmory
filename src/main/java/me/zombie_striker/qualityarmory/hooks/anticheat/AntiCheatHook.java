package me.zombie_striker.qualityarmory.hooks.anticheat;

import me.zombie_striker.qualityarmory.QAMain;
import me.zombie_striker.qualityarmory.api.QualityArmory;
import me.zombie_striker.qualityarmory.guns.Gun;
import me.zombie_striker.qualityarmory.guns.utils.GunUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public abstract class AntiCheatHook implements Listener {

    public AntiCheatHook() {
        Bukkit.getPluginManager().registerEvents(this, QAMain.getInstance());
    }

    public abstract String getName();

    public void onViolation(Player player, Cancellable cancellable) {
        Gun gun = QualityArmory.getGunInHand(player);
        if (gun == null) return;

        if (GunUtil.isDelay(gun,player)) {
            cancellable.setCancelled(true);
            QAMain.DEBUG("Cancelled " + this.getName() + " violation because player is using gun.");
        }
    }

    public static void registerHook(String name, @NotNull Class<? extends AntiCheatHook> hook) {
        if (Bukkit.getPluginManager().isPluginEnabled(name)) {
            try {
                AntiCheatHook aHook = hook.getConstructor().newInstance();
                QAMain.getInstance().getLogger().info("Found " + aHook.getName() + " AntiCheat. Loaded support");
            } catch (Throwable ignored) {}
        }
    }
}
