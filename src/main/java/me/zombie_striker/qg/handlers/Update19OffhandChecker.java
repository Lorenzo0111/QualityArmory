package me.zombie_striker.qg.handlers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.zombie_striker.qg.guns.Gun;

public class Update19OffhandChecker {

    public static boolean hasAmountOFfhandGreaterthan(final Player p, final int amount) {
        try {
            return Gun.getAmount(p) > amount;
        } catch (final Error e) {
            return false;
        }
    }

    public static boolean supportOffhand(final Player p) {
        try {
            p.getInventory().getItemInOffHand();
            return true;
        } catch (final Error e) {
            return false;
        }
    }

    public static boolean hasTypeOFfhand(final Player p, final Material m) {
        try {
            return p.getInventory().getItemInOffHand().getType() == m;
        } catch (final Error e) {
            return false;
        }
    }

    public static void setOffhand(final Player p, final ItemStack is) {
        try {
            p.getInventory().setItemInOffHand(is);
        } catch (final Error e) {

        }
    }

    public static ItemStack getItemStackOFfhand(final Player p) {
        try {
            return p.getInventory().getItemInOffHand();
        } catch (final Error e) {
            return null;
        }
    }
}
