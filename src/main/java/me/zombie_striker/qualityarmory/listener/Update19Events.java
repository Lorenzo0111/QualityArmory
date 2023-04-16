package me.zombie_striker.qualityarmory.listener;

import me.zombie_striker.customitemmanager.CustomItemManager;
import me.zombie_striker.qualityarmory.QAMain;
import me.zombie_striker.qualityarmory.api.QualityArmory;
import me.zombie_striker.qualityarmory.guns.Gun;
import me.zombie_striker.qualityarmory.guns.utils.GunUtil;

import me.zombie_striker.qualityarmory.listener.QAListener;
import org.bukkit.*;
import org.bukkit.event.*;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

public class Update19Events implements Listener {

    @EventHandler
    @SuppressWarnings("deprecation")
    public void onAnvil(PrepareAnvilEvent e) {
        if (CustomItemManager.isUsingCustomData()) return;
        if (QualityArmory.isCustomItem(e.getResult())) {
            ItemStack newi = e.getResult();
            newi.setDurability((short) QualityArmory.findSafeSpot(e.getResult(), false, QAMain.overrideURL));
            e.setResult(newi);
        }
        for (ItemStack is : e.getInventory().getContents()) {
            if (is != null && QualityArmory.isCustomItem(is)) {
                e.setResult(new ItemStack(Material.AIR));
                return;
            }
        }
    }

    @EventHandler
    public void onItemHandSwap(PlayerSwapHandItemsEvent e) {
        if (QAMain.reloadOnF || QAMain.reloadOnFOnly) {
            if (QualityArmory.isIronSights(e.getOffHandItem())) {
                e.setCancelled(true);
                e.getPlayer().getInventory().setItemInMainHand(e.getMainHandItem());
                e.getPlayer().getInventory().setItemInOffHand(null);
                BukkitTask task = GunUtil.rapidfireshooters.get(e.getPlayer().getUniqueId());
                if (task != null) task.cancel();
                return;
            } else if (QualityArmory.isGun(e.getMainHandItem())) {
                e.setCancelled(true);
                e.getPlayer().getInventory().setItemInMainHand(e.getMainHandItem());
                e.getPlayer().getInventory().setItemInOffHand(null);
                BukkitTask task = GunUtil.rapidfireshooters.get(e.getPlayer().getUniqueId());
                if (task != null) task.cancel();
                return;
            }
            Gun g = null;
            if (QualityArmory.isIronSights(e.getOffHandItem())) {
                if (QualityArmory.isGun(e.getMainHandItem())) g = QualityArmory.getGun(e.getMainHandItem());
            } else {
                if (QualityArmory.isGun(e.getOffHandItem())) g = QualityArmory.getGun(e.getOffHandItem());

            }
            if (g != null) {
                e.setCancelled(true);
                QAListener.reload(e.getPlayer(), g);
                BukkitTask task = GunUtil.rapidfireshooters.get(e.getPlayer().getUniqueId());
                if (task != null) task.cancel();
            }

        } else {
            Gun g = null;
            if (((g = QualityArmory.getGun(e.getOffHandItem())) != null) || (g = QualityArmory.getGun(e.getMainHandItem())) != null) {
                if (g.hasIronSights() && e.getPlayer().isSneaking()) {
                    e.setCancelled(true);
                    return;
                }
            }
            if (QualityArmory.isGun(e.getOffHandItem())) {
                e.setCancelled(true);
                return;
            }
            if (QualityArmory.isIronSights(e.getOffHandItem())) {
                e.setCancelled(true);
                return;
            }
            /*
             * if (e.getOffHandItem() != null && QualityArmory.isGun(e.getOffHandItem())) {
             * e.setCancelled(true); return; }
             */
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onSwig(PlayerAnimationEvent e) {
        if (e.getAnimationType() == PlayerAnimationType.ARM_SWING) {
            if (e.getPlayer().getItemInHand() != null && QualityArmory.isGun(e.getPlayer().getInventory().getItemInMainHand()))
                e.setCancelled(true);
            if (e.getPlayer().getInventory().getItemInOffHand() != null && QualityArmory.isGun(e.getPlayer().getInventory().getItemInOffHand()))
                e.setCancelled(true);
        }
    }
}
