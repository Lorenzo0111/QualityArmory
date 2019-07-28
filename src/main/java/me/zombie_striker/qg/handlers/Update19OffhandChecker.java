package me.zombie_striker.qg.handlers;

import me.zombie_striker.customitemmanager.OLD_ItemFact;

import me.zombie_striker.qg.guns.Gun;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Update19OffhandChecker {

	public static boolean hasAmountOFfhandGreaterthan(Player p, int amount) {
		try {
			return Gun.getAmount(p.getInventory().getItemInOffHand()) > amount;
		} catch (Error e) {
			return false;
		}
	}

	public static boolean supportOffhand(Player p) {
		try {
			p.getInventory().getItemInOffHand();
			return true;
		} catch (Error e) {
			return false;
		}
	}
	public static boolean hasTypeOFfhand(Player p, Material m) {
		try {
			return p.getInventory().getItemInOffHand().getType() == m;
		} catch (Error e) {
			return false;
		}
	}
	public static void setOffhand(Player p, ItemStack is){
		try{
			p.getInventory().setItemInOffHand(is);
		}catch(Error e){
			
		}
	}
	public static ItemStack getItemStackOFfhand(Player p) {
		try {
			return p.getInventory().getItemInOffHand();
		} catch (Error e) {
			return  null;
		}
	}
}
