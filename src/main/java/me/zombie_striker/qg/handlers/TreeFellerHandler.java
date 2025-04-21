package me.zombie_striker.qg.handlers;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.zombie_striker.customitemmanager.CustomItemManager;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.api.QualityArmory;

public class TreeFellerHandler implements Listener {

    public HashMap<UUID, Long> lastClicked = new HashMap<>();

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onClickFirst(final PlayerInteractEvent e) {
        if (CustomItemManager.isUsingCustomData())
            return;
        if (!QualityArmory.isCustomItem(e.getPlayer().getInventory().getItemInMainHand())) {
            this.lastClicked.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlock(final BlockBreakEvent e) {
        if (CustomItemManager.isUsingCustomData())
            return;
        if (QualityArmory.isCustomItem(e.getPlayer().getInventory().getItemInMainHand())
                && e.getPlayer().getItemOnCursor().getType().name().endsWith("AXE")
                && System.currentTimeMillis() - this.lastClicked.get(e.getPlayer().getUniqueId()) < 1000) {
            this.lastClicked.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
            final int durib = QualityArmory.findSafeSpot(e.getPlayer().getInventory().getItemInMainHand(), true, QAMain.overrideURL);
            final ItemStack temp = e.getPlayer().getInventory().getItemInMainHand();
            temp.setDurability((short) (durib + (QAMain.overrideURL ? 0 : 4)));
            e.getPlayer().setItemInHand(temp);
        }
    }
}
