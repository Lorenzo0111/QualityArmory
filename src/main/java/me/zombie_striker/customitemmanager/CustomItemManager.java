package me.zombie_striker.customitemmanager;

import java.io.File;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CustomItemManager {

    private static String resourcepack = null;
    private static HashMap<String, AbstractItem> customItemTypes = new HashMap<>();

    public static String getResourcepack() { return CustomItemManager.resourcepack; }

    public static void setResourcepack(final String url) { CustomItemManager.resourcepack = url; }

    public static Set<String> getCustomItemTypes() { return CustomItemManager.customItemTypes.keySet(); }

    public static void registerItemType(final File dataFolder, final String key, final AbstractItem item) {
        CustomItemManager.customItemTypes.put(key, item);
    }

    public static AbstractItem getItemType(final String key) { return CustomItemManager.customItemTypes.get(key); }

    public static boolean isUsingCustomData() {
        try {
            new ItemStack(Material.DIAMOND_BLOCK).getItemMeta().hasCustomModelData();
            return true;
        } catch (Error | Exception e4) {

        }
        return false;
    }

}
