package me.zombie_striker.qg.handlers;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.guns.Gun;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class IronsightsHandler {

	public static Material ironsightsMaterial = Material.DIAMOND_AXE;
	public static int ironsightsData = 21;
	public static String ironsightsDisplay = "Iron Sights Enabled";


	public static void aim(Player player){
			if(!QualityArmory.isIronSights(player.getItemInHand())){
				//offHandStorage.put(player, player.getInventory().getItemInOffHand());
				if(player.getInventory().getItemInOffHand()!=null){
					if(player.getInventory().firstEmpty()==-1){
						player.getWorld().dropItem(player.getLocation(),player.getInventory().getItemInOffHand());
					}else {
						player.getInventory().addItem(player.getInventory().getItemInOffHand());
					}
				}
				if (player.getItemInHand() != null && QualityArmory.isGun(player.getItemInHand())) {
					Gun gun = QualityArmory.getGun(player.getItemInHand());
					QAMain.toggleNightvision(player, gun, true);
				}
				player.getInventory().setItemInOffHand(player.getItemInHand());
				player.setItemInHand(QualityArmory.getIronSightsItemStack());
			}
	}
	public static void unAim(Player player){
			if(QualityArmory.isIronSights(player.getItemInHand())){
				if (player.getInventory().getItemInOffHand() != null && QualityArmory.isGun(player.getInventory().getItemInOffHand())) {
					Gun gun = QualityArmory.getGun(player.getInventory().getItemInOffHand());
					QAMain.toggleNightvision(player, null, false);
				}
				player.getInventory().setItemInMainHand(player.getInventory().getItemInOffHand());
				player.getInventory().setItemInOffHand(null);
				//offHandStorage.remove(player);
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
