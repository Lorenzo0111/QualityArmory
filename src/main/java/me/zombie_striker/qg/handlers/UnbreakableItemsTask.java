package me.zombie_striker.qg.handlers;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.config.MainConfig;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.utils.Compat;
import me.zombie_striker.qg.utils.ErrorReporter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import me.zombie_striker.qg.utils.FoliaRunnable;

/**
 * With durability based items (before 1.14), a custom item that lost its unbreakable flag would wear down
 * into a different item. This task moves such items back onto their item slot every 15 ticks.
 */
public final class UnbreakableItemsTask extends FoliaRunnable {

    /**
     * @return Whether the task is needed: durability based items, and a server that has the unbreakable flag (1.11+).
     */
    public static boolean isNeeded() {
        return !me.zombie_striker.customitemmanager.CustomItemManager.isUsingCustomData() && Compat.UNBREAKABLE_META;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void run() {
        MainConfig config = QAMain.getConfiguration();
        if (!config.items.enableUnbreaking || config.features.enableIgnoreUnbreakingChecks)
            return;
        // Each player is handled on its own thread on Folia
        for (Player player : Bukkit.getOnlinePlayers())
            FoliaRunnable.runEntityTask(QAMain.getInstance(), player, () -> fixHeldItems(player));
    }

    @SuppressWarnings("deprecation")
    private static void fixHeldItems(Player p) {
        try {
            ItemStack mainHand = p.getItemInHand();
            if (needsFix(mainHand))
                p.setItemInHand(fix(mainHand));

            if (Compat.OFFHAND) {
                ItemStack offHand = p.getInventory().getItemInOffHand();
                if (needsFix(offHand))
                    p.getInventory().setItemInOffHand(fix(offHand));
            }
        } catch (RuntimeException e) {
            ErrorReporter.report("making held custom items unbreakable", e);
        }
    }

    private static boolean needsFix(ItemStack item) {
        return item != null && item.hasItemMeta() && QualityArmory.isCustomItem(item) && !item.getItemMeta().isUnbreakable();
    }

    @SuppressWarnings("deprecation")
    private static ItemStack fix(ItemStack item) {
        int slot = QualityArmory.findSafeSpot(item, false, QAMain.getConfiguration().resourcepack.overrideDefault);
        item.setDurability((short) Math.max(0, slot - 1));
        return Gun.removeCalculatedExtra(item);
    }
}
