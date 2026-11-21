package me.zombie_striker.qg.items;

import me.zombie_striker.qg.hooks.NBTHook;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Item state stored in NBT, through {@link NBTHook} so the NBTAPI plugin is used when it is installed. Name and lore only display it, so translating or editing them no longer
 * breaks existing items.
 * <p>
 * Items created before this stored their state only in the name and lore, so every getter returns null
 * when the tag is missing and callers fall back to reading the text. Setters must run after any
 * {@code setItemMeta} with an older meta, which would overwrite the tag.
 */
public final class ItemState {
    private static final String RELOADING = "qa_reloading";
    private static final String DURABILITY = "qa_durability";
    private static final String BAG_AMMO = "qa_bag_ammo";
    private static final String BAG_TYPE = "qa_bag_type";
    private static final String VARIANT = "qa_variant";

    private ItemState() {
    }

    /**
     * @return Whether the gun is reloading, or null if the item predates the tag.
     */
    public static Boolean isReloading(ItemStack item) {
        if (!hasMeta(item)) return null;
        return NBTHook.getBooleanOrNull(item, RELOADING);
    }

    public static void setReloading(ItemStack item, boolean reloading) {
        if (!hasMeta(item)) return;
        if (reloading)
            NBTHook.setBoolean(item, RELOADING, true);
        else
            NBTHook.remove(item, RELOADING);
    }

    /**
     * @return The remaining durability of a gun, or null if the item predates the tag.
     */
    public static Integer getDurability(ItemStack item) {
        if (!hasMeta(item)) return null;
        return NBTHook.getIntOrNull(item, DURABILITY);
    }

    public static void setDurability(ItemStack item, int durability) {
        if (!hasMeta(item)) return;
        NBTHook.setInt(item, DURABILITY, durability);
    }

    /**
     * @return The ammo stored in an ammo bag, or null if the item predates the tag.
     */
    public static Integer getBagAmmo(ItemStack item) {
        if (!hasMeta(item)) return null;
        return NBTHook.getIntOrNull(item, BAG_AMMO);
    }

    public static void setBagAmmo(ItemStack item, int ammo) {
        if (!hasMeta(item)) return;
        NBTHook.setInt(item, BAG_AMMO, ammo);
    }

    /**
     * @return The name of the ammo type stored in an ammo bag, or null if the item predates the tag.
     */
    public static String getBagType(ItemStack item) {
        if (!hasMeta(item)) return null;
        return NBTHook.getStringOrNull(item, BAG_TYPE);
    }

    public static void setBagType(ItemStack item, String ammoName) {
        if (!hasMeta(item)) return;
        NBTHook.setString(item, BAG_TYPE, ammoName);
    }

    /**
     * @return The variant that tells items with the same material and data apart, or null if the item predates the tag.
     */
    public static Integer getVariant(ItemStack item) {
        if (!hasMeta(item)) return null;
        return NBTHook.getIntOrNull(item, VARIANT);
    }

    public static void setVariant(ItemStack item, int variant) {
        if (item == null || item.getType() == Material.AIR) return;
        // Most items have no variant; only touch NBT when there is one to set or an old one to clear
        if (variant == 0 && getVariant(item) == null) return;
        if (variant != 0)
            NBTHook.setInt(item, VARIANT, variant);
        else
            NBTHook.remove(item, VARIANT);
    }

    private static boolean hasMeta(ItemStack item) {
        return item != null && item.getType() != Material.AIR && item.hasItemMeta();
    }
}
