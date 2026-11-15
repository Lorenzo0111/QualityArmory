package me.zombie_striker.qg.hooks;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.utils.MinecraftVersion;
import me.zombie_striker.qg.QAMain;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public final class NBTHook {
    private static boolean external = false;

    public static boolean init(QAMain plugin) {
        if (Bukkit.getPluginManager().isPluginEnabled("NBTAPI")) {
            plugin.getLogger().info("Found NBTAPI plugin installed. Using that instead of the internal version");
            external = true;
            return true;
        }

        if (!NBT.preloadApi())
            return false;

        MinecraftVersion.replaceLogger(plugin.getLogger());
        MinecraftVersion.disableUpdateCheck();

        return true;
    }

    public static int getInt(ItemStack item, String tag) {
        try {
            if (external) {
                return de.tr7zw.nbtapi.NBT.get(item, nbt -> nbt.hasTag(tag) ? nbt.getInteger(tag) : 0);
            }
        } catch (Exception | Error ignored) {
        }

        return NBT.get(item, nbt -> nbt.hasTag(tag) ? nbt.getInteger(tag) : 0);
    }

    public static void setInt(ItemStack item, String tag, int value) {
        try {
            if (external) {
                de.tr7zw.nbtapi.NBT.modify(item, nbt -> {
                    nbt.setInteger(tag, value);
                });
                return;
            }
        } catch (Exception | Error ignored) {
        }

        NBT.modify(item, nbt -> {
            nbt.setInteger(tag, value);
        });
    }

    /**
     * @return The integer at the tag, or null if the item doesn't have it.
     */
    public static Integer getIntOrNull(ItemStack item, String tag) {
        try {
            if (external)
                return de.tr7zw.nbtapi.NBT.get(item, nbt -> nbt.hasTag(tag) ? nbt.getInteger(tag) : null);
        } catch (Exception | Error ignored) {
            // Fall back to the bundled NBT-API, as getInt does
        }
        return NBT.get(item, nbt -> nbt.hasTag(tag) ? nbt.getInteger(tag) : null);
    }

    /**
     * @return The boolean at the tag, or null if the item doesn't have it.
     */
    public static Boolean getBooleanOrNull(ItemStack item, String tag) {
        try {
            if (external)
                return de.tr7zw.nbtapi.NBT.get(item, nbt -> nbt.hasTag(tag) ? nbt.getBoolean(tag) : null);
        } catch (Exception | Error ignored) {
            // Fall back to the bundled NBT-API, as getInt does
        }
        return NBT.get(item, nbt -> nbt.hasTag(tag) ? nbt.getBoolean(tag) : null);
    }

    /**
     * @return The string at the tag, or null if the item doesn't have it.
     */
    public static String getStringOrNull(ItemStack item, String tag) {
        try {
            if (external)
                return de.tr7zw.nbtapi.NBT.get(item, nbt -> nbt.hasTag(tag) ? nbt.getString(tag) : null);
        } catch (Exception | Error ignored) {
            // Fall back to the bundled NBT-API, as getInt does
        }
        return NBT.get(item, nbt -> nbt.hasTag(tag) ? nbt.getString(tag) : null);
    }

    public static void setBoolean(ItemStack item, String tag, boolean value) {
        try {
            if (external) {
                de.tr7zw.nbtapi.NBT.modify(item, nbt -> {
                    nbt.setBoolean(tag, value);
                });
                return;
            }
        } catch (Exception | Error ignored) {
            // Fall back to the bundled NBT-API, as setInt does
        }
        NBT.modify(item, nbt -> {
            nbt.setBoolean(tag, value);
        });
    }

    public static void setString(ItemStack item, String tag, String value) {
        try {
            if (external) {
                de.tr7zw.nbtapi.NBT.modify(item, nbt -> {
                    nbt.setString(tag, value);
                });
                return;
            }
        } catch (Exception | Error ignored) {
            // Fall back to the bundled NBT-API, as setInt does
        }
        NBT.modify(item, nbt -> {
            nbt.setString(tag, value);
        });
    }

    public static void remove(ItemStack item, String tag) {
        try {
            if (external) {
                de.tr7zw.nbtapi.NBT.modify(item, nbt -> {
                    nbt.removeKey(tag);
                });
                return;
            }
        } catch (Exception | Error ignored) {
            // Fall back to the bundled NBT-API, as setInt does
        }
        NBT.modify(item, nbt -> {
            nbt.removeKey(tag);
        });
    }
}
