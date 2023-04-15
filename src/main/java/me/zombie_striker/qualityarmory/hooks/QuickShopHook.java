package me.zombie_striker.qualityarmory.hooks;

import me.zombie_striker.qualityarmory.api.QualityArmory;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.maxgamer.quickshop.api.event.ProtectionCheckStatus;
import org.maxgamer.quickshop.api.event.ShopProtectionCheckEvent;

public class QuickShopHook implements Listener {
    private volatile Location protectionCheckingLocation = null;

    @EventHandler
    public void onQuickShopProtectionChecking(ShopProtectionCheckEvent event){
        if(event.getStatus().equals(ProtectionCheckStatus.BEGIN)) {
            protectionCheckingLocation = event.getLocation();
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event){
        if(protectionCheckingLocation != null && event.getBlock().getLocation().equals(protectionCheckingLocation)) {
            protectionCheckingLocation = null;

            if (QualityArmory.isCustomItem(event.getPlayer().getInventory().getItemInMainHand()))
                event.setCancelled(false);
        }
    }

}
