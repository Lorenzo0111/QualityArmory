package me.zombie_striker.qg.hooks;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.maxgamer.quickshop.api.event.ProtectionCheckStatus;
import org.maxgamer.quickshop.api.event.ShopProtectionCheckEvent;

import me.zombie_striker.qg.api.QualityArmory;

public class QuickShopHook implements Listener {
    private volatile Location protectionCheckingLocation = null;

    @EventHandler
    public void onQuickShopProtectionChecking(final ShopProtectionCheckEvent event) {
        if (event.getStatus().equals(ProtectionCheckStatus.BEGIN)) {
            this.protectionCheckingLocation = event.getLocation();
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(final BlockBreakEvent event) {
        if (this.protectionCheckingLocation != null && event.getBlock().getLocation().equals(this.protectionCheckingLocation)) {
            this.protectionCheckingLocation = null;

            if (QualityArmory.isCustomItem(event.getPlayer().getInventory().getItemInMainHand()))
                event.setCancelled(false);
        }
    }

}
