package me.zombie_striker.qg.handlers;

import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.guns.Gun;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;

import java.util.ArrayList;

public class IronsightsHandler {

	public static Material ironsightsMaterial = Material.DIAMOND_AXE;
	public static int ironsightsData = 21;
	public static String ironsightsDisplay = "Iron Sights Enabled";


	public static void aim(Player player){
		boolean swap = true;
		try {
			if(player.getItemInHand()!=null && player.getItemInHand().getType().name().equals("CROSSBOW")){
				Gun g = QualityArmory.getGun(player.getItemInHand());
				if(!g.isOffhandOverride()) {

					ItemStack is = player.getItemInHand();
					CrossbowMeta im = (CrossbowMeta) is.getItemMeta();
					im.addChargedProjectile(new ItemStack(Material.ARROW));
					is.setItemMeta(im);
					if(is!=null)
					player.setItemInHand(is);
					swap = false;
					return;
				}
			}
		}catch (Error|Exception e4){

		}finally {if(swap)
			if(!QualityArmory.isIronSights(player.getItemInHand())){
				player.getInventory().setItemInOffHand(player.getItemInHand());
				player.setItemInHand(QualityArmory.getIronSightsItemStack());
			}
		}
	}
	public static void unAim(Player player){
		boolean swap = true;
		try {
			if(player.getItemInHand()!=null && player.getItemInHand().getType().name().equals("CROSSBOW")){
				Gun g = QualityArmory.getGun(player.getItemInHand());
				if(!g.isOffhandOverride()) {
					ItemStack is = player.getItemInHand();
					CrossbowMeta im = (CrossbowMeta) is.getItemMeta();
					im.setChargedProjectiles(null);
					is.setItemMeta(im);
					player.setItemInHand(is);
					swap = false;
					return;
				}
			}
		}catch (Error|Exception e4){

		}finally {
			if(swap)
			if(QualityArmory.isIronSights(player.getItemInHand())){
				player.getInventory().setItemInMainHand(player.getInventory().getItemInOffHand());
				player.getInventory().setItemInOffHand(null);
			}
		}
	}

	public static boolean isAiming(Player player){
		try {
			if(player.getItemInHand()!=null && player.getItemInHand().getType().name().equals("CROSSBOW")){
				Gun g = QualityArmory.getGun(player.getItemInHand());
				if(!g.isOffhandOverride()) {
					ItemStack is = player.getItemInHand();
					CrossbowMeta im = (CrossbowMeta) is.getItemMeta();
					return im.hasChargedProjectiles();
				}
			}
		}catch (Error|Exception e4){

		}finally {
			if(QualityArmory.isIronSights(player.getItemInHand())){
				return true;
			}
		}
		return false;
	}
	public static ItemStack getItemAiming(Player player) {
		try {
			if (player.getItemInHand() != null && player.getItemInHand().getType().name().equals("CROSSBOW")) {
				Gun g = QualityArmory.getGun(player.getItemInHand());
				if(!g.isOffhandOverride()) {
					return player.getItemInHand();
				}
			}
		} catch (Error | Exception e4) {

		} finally {
			if (!QualityArmory.isIronSights(player.getItemInHand())) {
				return player.getItemInHand();
			} else {
				return player.getInventory().getItemInOffHand();
			}
		}
	}
	public static Gun getGunUsed(Player player){
		return QualityArmory.getGun(getItemAiming(player));
	}
	public static void setItemAiming(Player player){}
}
