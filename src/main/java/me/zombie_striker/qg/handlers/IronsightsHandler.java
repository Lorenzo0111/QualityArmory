package me.zombie_striker.qg.handlers;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.api.WeaponInteractEvent;
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
	public static Map<Player, Integer> beforeSwitch = new HashMap<>();


	public static void aim(Player player){
			if(!QualityArmory.isIronSights(player.getItemInHand())){
				//offHandStorage.put(player, player.getInventory().getItemInOffHand());
				if(player.getInventory().getItemInOffHand() != null && !player.getInventory().getItemInOffHand().getType().equals(Material.AIR)){
					int slot = player.getInventory().firstEmpty();
					if (slot == -1) {
						player.getWorld().dropItem(player.getLocation(),player.getInventory().getItemInOffHand());
					} else {
						player.getInventory().setItem(slot, player.getInventory().getItemInOffHand());
						if (QAMain.restoreOffHand) beforeSwitch.put(player, slot);
					}
				}
				if (player.getItemInHand() != null && QualityArmory.isGun(player.getItemInHand())) {
					Gun gun = QualityArmory.getGun(player.getItemInHand());
					QAMain.toggleNightvision(player, gun, true);
					Bukkit.getPluginManager().callEvent(new WeaponInteractEvent(player, gun, WeaponInteractEvent.InteractType.AIM));
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
					QAMain.toggleNightvision(player, null, false);
					Bukkit.getPluginManager().callEvent(new WeaponInteractEvent(player, gun, WeaponInteractEvent.InteractType.UNAIM));
				}

				final int ammo = Gun.getAmount(player);

				player.getInventory().setItemInMainHand(player.getInventory().getItemInOffHand());
				player.getInventory().setItemInOffHand(null);
				if(beforeSwitch.containsKey(player)){
					int slot = beforeSwitch.get(player);
					if(slot != -1){
						ItemStack item = player.getInventory().getItem(slot);
						if(item != null && !item.getType().equals(Material.AIR)){
							player.getInventory().setItem(slot, null);
							player.getInventory().setItemInOffHand(item);
						}
					}
					beforeSwitch.remove(player);
				}

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
	public static void setItemAiming(Player player){}
}
