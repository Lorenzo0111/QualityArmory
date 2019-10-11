package me.zombie_striker.qg.handlers;

import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.guns.Gun;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class IronsightsHandler {

	public static Material ironsightsMaterial = Material.DIAMOND_AXE;
	public static int ironsightsData = 21;
	public static String ironsightsDisplay = "Iron Sights Enabled";


	public static void aim(Player player){
			if(!QualityArmory.isIronSights(player.getItemInHand())){
				player.getInventory().setItemInOffHand(player.getItemInHand());
				player.setItemInHand(QualityArmory.getIronSightsItemStack());
			}
	}
	public static void unAim(Player player){
			if(QualityArmory.isIronSights(player.getItemInHand())){
				player.getInventory().setItemInMainHand(player.getInventory().getItemInOffHand());
				player.getInventory().setItemInOffHand(null);
			}
	}

	public static boolean isAiming(Player player){
			if(QualityArmory.isIronSights(player.getItemInHand())){
				return true;
			}
		return false;
	}
	public static ItemStack getItemAiming(Player player) {
			if (!QualityArmory.isIronSights(player.getItemInHand())) {
				return player.getItemInHand();
			} else {
				return player.getInventory().getItemInOffHand();
			}
	}
	public static Gun getGunUsed(Player player){
		return QualityArmory.getGun(getItemAiming(player));
	}
	public static void setItemAiming(Player player){}
}
