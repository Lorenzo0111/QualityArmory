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

}
