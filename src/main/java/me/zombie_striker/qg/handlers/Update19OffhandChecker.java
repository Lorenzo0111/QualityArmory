package me.zombie_striker.qg.handlers;

import me.zombie_striker.qg.utils.Compat;

import me.zombie_striker.qg.guns.Gun;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Update19OffhandChecker {

	public static boolean hasAmountOFfhandGreaterthan(Player p, int amount) {
		return Gun.getAmount(p) > amount;
	}

	public static boolean supportOffhand(Player p) {
		return Compat.OFFHAND;
	}
	public static boolean hasTypeOFfhand(Player p, Material m) {
		return Compat.OFFHAND && p.getInventory().getItemInOffHand().getType() == m;
	}
	public static void setOffhand(Player p, ItemStack is){
		if (Compat.OFFHAND)
			p.getInventory().setItemInOffHand(is);
	}
	public static ItemStack getItemStackOFfhand(Player p) {
		return Compat.OFFHAND ? p.getInventory().getItemInOffHand() : null;
	}
}
