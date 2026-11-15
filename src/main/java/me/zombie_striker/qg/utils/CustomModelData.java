package me.zombie_striker.qg.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Reads and writes the number that identifies a custom item: custom model data on 1.14+,
 * durability before that, or when the item has no custom model data.
 */
public final class CustomModelData {
    /** {@code ItemMeta#setCustomModelData} (1.14+). */
    public static final boolean SUPPORTED = Compat.hasMethod("org.bukkit.inventory.meta.ItemMeta", "setCustomModelData", "java.lang.Integer");

    private CustomModelData() {
    }

    /**
     * @return Whether the item has custom model data. Always false before 1.14.
     */
    public static boolean has(ItemStack item) {
        if (!SUPPORTED || item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().hasCustomModelData();
    }

    /**
     * @return The custom model data, or the durability if the item has none.
     */
    @SuppressWarnings("deprecation")
    public static int getOrDurability(ItemStack item) {
        return has(item) ? item.getItemMeta().getCustomModelData() : item.getDurability();
    }

    /**
     * Sets the custom model data, or the durability before 1.14 and on items without meta (air).
     */
    @SuppressWarnings("deprecation")
    public static void setOrDurability(ItemStack item, int data) {
        ItemMeta meta = SUPPORTED ? item.getItemMeta() : null;
        if (meta == null) {
            item.setDurability((short) data);
            return;
        }
        meta.setCustomModelData(data);
        item.setItemMeta(meta);
    }
}
