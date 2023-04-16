package me.zombie_striker.qualityarmory.utils;

import me.zombie_striker.qualityarmory.QAMain;
import me.zombie_striker.qualityarmory.api.QualityArmory;
import me.zombie_striker.qualityarmory.guns.Gun;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class IronsightsUtil {

	public static void aim(Player player){
			if(!QualityArmory.isIronSights(player.getItemInHand())){
				if(player.getInventory().getItemInOffHand()!=null){
					if(player.getInventory().firstEmpty()==-1){
						player.getWorld().dropItem(player.getLocation(),player.getInventory().getItemInOffHand());
					}else {
						player.getInventory().addItem(player.getInventory().getItemInOffHand());
					}
				}
				if (player.getItemInHand() != null && QualityArmory.isGun(player.getItemInHand())) {
					Gun gun = QualityArmory.getGun(player.getItemInHand());
					QAMain.toggleNightVision(player, gun, true);
				}
				final int ammo = Gun.getAmount(player);

				player.getInventory().setItemInOffHand(player.getItemInHand());
				player.setItemInHand(QualityArmory.getIronSightsItemStack());
				Gun.updateAmmo(null, player, ammo);
			}
	}
	public static void unAim(Player player){
			if(QualityArmory.isIronSights(player.getItemInHand())){
				if (player.getInventory().getItemInOffHand() != null && QualityArmory.isGun(player.getInventory().getItemInOffHand())) {
					Gun gun = QualityArmory.getGun(player.getInventory().getItemInOffHand());
					QAMain.toggleNightVision(player, null, false);
				}

				final int ammo = Gun.getAmount(player);

				player.getInventory().setItemInMainHand(player.getInventory().getItemInOffHand());
				player.getInventory().setItemInOffHand(null);
				//offHandStorage.remove(player);

				Gun.updateAmmo(null, player, ammo);
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
}
