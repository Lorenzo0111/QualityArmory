package me.zombie_striker.qg.handlers;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import me.zombie_striker.customitemmanager.CustomItemManager;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.guns.utils.GunUtil;
import me.zombie_striker.qg.listener.QAListener;

public class Update19Events implements Listener {

    @EventHandler
    @SuppressWarnings("deprecation")
    public void onAnvil(final PrepareAnvilEvent e) {
        if (CustomItemManager.isUsingCustomData())
            return;
        if (QualityArmory.isCustomItem(e.getResult())) {
            final ItemStack newi = e.getResult();
            newi.setDurability((short) QualityArmory.findSafeSpot(e.getResult(), false, QAMain.overrideURL));
            e.setResult(newi);
        }
        for (final ItemStack is : e.getInventory().getContents()) {
            if (is != null && QualityArmory.isCustomItem(is)) {
                e.setResult(new ItemStack(Material.AIR));
                return;
            }
        }
    }

    @EventHandler
    public void onItemHandSwap(final PlayerSwapHandItemsEvent e) {
        if (QAMain.reloadOnF || QAMain.reloadOnFOnly) {
            if (QualityArmory.isIronSights(e.getOffHandItem())) {
                e.setCancelled(true);
                IronsightsHandler.unAim(e.getPlayer());
                final BukkitTask task = GunUtil.rapidfireshooters.get(e.getPlayer().getUniqueId());
                if (task != null)
                    task.cancel();
                return;
            } else if (QualityArmory.isGun(e.getMainHandItem())) {
                e.setCancelled(true);
                final BukkitTask task = GunUtil.rapidfireshooters.get(e.getPlayer().getUniqueId());
                if (task != null)
                    task.cancel();
                return;
            }
            Gun g = null;
            if (QualityArmory.isIronSights(e.getOffHandItem())) {
                if (QualityArmory.isGun(e.getMainHandItem()))
                    g = QualityArmory.getGun(e.getMainHandItem());
            } else {
                if (QualityArmory.isGun(e.getOffHandItem()))
                    g = QualityArmory.getGun(e.getOffHandItem());

            }
            if (g != null) {
                e.setCancelled(true);
                QAListener.reload(e.getPlayer(), g);
                final BukkitTask task = GunUtil.rapidfireshooters.get(e.getPlayer().getUniqueId());
                if (task != null)
                    task.cancel();
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

    @EventHandler
    public void onSwig(final PlayerAnimationEvent e) {
        if (e.getAnimationType() == PlayerAnimationType.ARM_SWING) {
            try {
                if (e.getPlayer().getInventory().getItemInMainHand() != null
                        && QualityArmory.isGun(e.getPlayer().getInventory().getItemInMainHand()))
                    e.setCancelled(true);
                if (e.getPlayer().getInventory().getItemInOffHand() != null
                        && QualityArmory.isGun(e.getPlayer().getInventory().getItemInOffHand()))
                    e.setCancelled(true);
            } catch (Error | Exception re) {
            }
        }
    }
}
