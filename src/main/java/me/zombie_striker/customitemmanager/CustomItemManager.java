package me.zombie_striker.customitemmanager;

import java.io.File;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.zombie_striker.customitemmanager.pack.ResourcepackProvider;
import me.zombie_striker.customitemmanager.pack.StaticPackProvider;

public class CustomItemManager {

    private static ResourcepackProvider resourcepackProvider = null;
    private static HashMap<String, AbstractItem> customItemTypes = new HashMap<>();

    /**
     * @deprecated Use {@link #getResourcepack(Player)} instead
     * @return the resourcepack URL
     */
    @Deprecated
    public static String getResourcepack() { return CustomItemManager.resourcepackProvider.getFor(null); }

    /**
     * @deprecated Use {@link #setResourcepack(ResourcepackProvider)} instead
     * @param url the resourcepack URL
     */
    @Deprecated
    public static void setResourcepack(final String url) { CustomItemManager.resourcepackProvider = new StaticPackProvider(url); }

    public static String getResourcepack(final Player player) { return CustomItemManager.resourcepackProvider.getFor(player); }

    public static void setResourcepack(final ResourcepackProvider provider) { CustomItemManager.resourcepackProvider = provider; }

    public static ResourcepackProvider getResourcepackProvider() { return CustomItemManager.resourcepackProvider; }

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
