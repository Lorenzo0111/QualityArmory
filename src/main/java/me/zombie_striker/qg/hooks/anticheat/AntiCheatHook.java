package me.zombie_striker.qg.hooks.anticheat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.guns.utils.GunUtil;

public abstract class AntiCheatHook implements Listener {

    public AntiCheatHook() { Bukkit.getPluginManager().registerEvents(this, QAMain.getInstance()); }

    public abstract String getName();

    public void onViolation(final Player player, final Cancellable cancellable) {
        final Gun gun = QualityArmory.getGunInHand(player);
        if (gun == null)
            return;

        if (GunUtil.isDelay(gun, player)) {
            cancellable.setCancelled(true);
            QAMain.DEBUG("Cancelled " + this.getName() + " violation because player is using gun.");
        }
    }

    public static void registerHook(final String name, @NotNull final Class<? extends AntiCheatHook> hook) {
        if (Bukkit.getPluginManager().isPluginEnabled(name)) {
            try {
                final AntiCheatHook aHook = hook.getConstructor().newInstance();
                QAMain.getInstance().getLogger().info("Found " + aHook.getName() + " AntiCheat. Loaded support");
            } catch (final Throwable ignored) {
            }
        }
    }
}
