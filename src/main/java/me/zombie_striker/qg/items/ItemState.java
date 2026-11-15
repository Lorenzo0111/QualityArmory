package me.zombie_striker.qg.items;

import de.tr7zw.changeme.nbtapi.NBT;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Item state stored in NBT. Name and lore only display it, so translating or editing them no longer
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
        return NBT.get(item, nbt -> nbt.hasTag(RELOADING) ? nbt.getBoolean(RELOADING) : null);
    }

    public static void setReloading(ItemStack item, boolean reloading) {
        if (!hasMeta(item)) return;
        NBT.modify(item, nbt -> {
            if (reloading)
                nbt.setBoolean(RELOADING, true);
            else
                nbt.removeKey(RELOADING);
        });
    }

    /**
     * @return The remaining durability of a gun, or null if the item predates the tag.
     */
    public static Integer getDurability(ItemStack item) {
        if (!hasMeta(item)) return null;
        return NBT.get(item, nbt -> nbt.hasTag(DURABILITY) ? nbt.getInteger(DURABILITY) : null);
    }

    public static void setDurability(ItemStack item, int durability) {
        if (!hasMeta(item)) return;
        NBT.modify(item, nbt -> {
            nbt.setInteger(DURABILITY, durability);
        });
    }

    /**
     * @return The ammo stored in an ammo bag, or null if the item predates the tag.
     */
    public static Integer getBagAmmo(ItemStack item) {
        if (!hasMeta(item)) return null;
        return NBT.get(item, nbt -> nbt.hasTag(BAG_AMMO) ? nbt.getInteger(BAG_AMMO) : null);
    }

    public static void setBagAmmo(ItemStack item, int ammo) {
        if (!hasMeta(item)) return;
        NBT.modify(item, nbt -> {
            nbt.setInteger(BAG_AMMO, ammo);
        });
    }

    /**
     * @return The name of the ammo type stored in an ammo bag, or null if the item predates the tag.
     */
    public static String getBagType(ItemStack item) {
        if (!hasMeta(item)) return null;
        return NBT.get(item, nbt -> nbt.hasTag(BAG_TYPE) ? nbt.getString(BAG_TYPE) : null);
    }

    public static void setBagType(ItemStack item, String ammoName) {
        if (!hasMeta(item)) return;
        NBT.modify(item, nbt -> {
            nbt.setString(BAG_TYPE, ammoName);
        });
    }

    /**
     * @return The variant that tells items with the same material and data apart, or null if the item predates the tag.
     */
    public static Integer getVariant(ItemStack item) {
        if (!hasMeta(item)) return null;
        return NBT.get(item, nbt -> nbt.hasTag(VARIANT) ? nbt.getInteger(VARIANT) : null);
    }

    public static void setVariant(ItemStack item, int variant) {
        if (item == null || item.getType() == Material.AIR) return;
        // Most items have no variant; only touch NBT when there is one to set or an old one to clear
        if (variant == 0 && getVariant(item) == null) return;
        NBT.modify(item, nbt -> {
            if (variant != 0)
                nbt.setInteger(VARIANT, variant);
            else
                nbt.removeKey(VARIANT);
        });
    }

    private static boolean hasMeta(ItemStack item) {
        return item != null && item.getType() != Material.AIR && item.hasItemMeta();
    }
}
