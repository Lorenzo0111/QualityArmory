package me.zombie_striker.qg.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.api.WeaponInteractEvent;
import me.zombie_striker.qg.guns.Gun;

public class IronsightsHandler {

    public static Material ironsightsMaterial = Material.DIAMOND_AXE;
    public static int ironsightsData = 21;
    public static String ironsightsDisplay = "Iron Sights Enabled";


	public static void aim(Player player){
			if(!QualityArmory.isIronSights(player.getItemInHand())){
				//offHandStorage.put(player, player.getInventory().getItemInOffHand());
				if(player.getInventory().getItemInOffHand() != null && !player.getInventory().getItemInOffHand().getType().equals(Material.AIR)){
					if(player.getInventory().firstEmpty()==-1){
						player.getWorld().dropItem(player.getLocation(),player.getInventory().getItemInOffHand());
					}else {
						player.getInventory().addItem(player.getInventory().getItemInOffHand());
					}
				}
				if (player.getItemInHand() != null && QualityArmory.isGun(player.getItemInHand())) {
					Gun gun = QualityArmory.getGun(player.getItemInHand());
					QAMain.toggleNightvision(player, gun, true);
					Bukkit.getPluginManager().callEvent(new WeaponInteractEvent(player, gun, WeaponInteractEvent.InteractType.AIM));
				}
				final int ammo = Gun.getAmount(player);

            player.getInventory().setItemInOffHand(player.getInventory().getItemInMainHand());
            player.getInventory().setItemInMainHand(QualityArmory.getIronSightsItemStack());
            Gun.updateAmmo(null, player, ammo);
        }
    }

    public static void unAim(final Player player) {
        if (QualityArmory.isIronSights(player.getInventory().getItemInMainHand())) {
            if (player.getInventory().getItemInOffHand() != null && QualityArmory.isGun(player.getInventory().getItemInOffHand())) {
                final Gun gun = QualityArmory.getGun(player.getInventory().getItemInOffHand());
                QAMain.toggleNightvision(player, null, false);
                Bukkit.getPluginManager().callEvent(new WeaponInteractEvent(player, gun, WeaponInteractEvent.InteractType.UNAIM));
            }

            final int ammo = Gun.getAmount(player);

            player.getInventory().setItemInMainHand(player.getInventory().getItemInOffHand());
            player.getInventory().setItemInOffHand(null);
            // offHandStorage.remove(player);

            Gun.updateAmmo(null, player, ammo);
        }
    }

    public static boolean isAiming(final Player player) {
        if (QualityArmory.isIronSights(player.getInventory().getItemInMainHand())) {
            return true;
        }
        return false;
    }

    public static ItemStack getItemAiming(final Player player) {
        if (!QualityArmory.isIronSights(player.getInventory().getItemInMainHand())) {
            return player.getInventory().getItemInMainHand();
        } else {
            return player.getInventory().getItemInOffHand();
        }
    }

    public static Gun getGunUsed(final Player player) { return QualityArmory.getGun(IronsightsHandler.getItemAiming(player)); }

    public static void setItemAiming(final Player player) {}
}
