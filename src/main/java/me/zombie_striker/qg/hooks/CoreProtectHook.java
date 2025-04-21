package me.zombie_striker.qg.hooks;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import me.zombie_striker.qg.QAMain;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;

public class CoreProtectHook {
    private static Object api = null;

    public static void logBreak(final Block block, final Player player) {
        try {
            if (!CoreProtectHook.logBreak0(block, player)) {
                QAMain.DEBUG("CoreProtect failed to log break: Returned false");
            }
        } catch (final Throwable e) {
            QAMain.DEBUG("Error while logging break to CoreProtect: " + e.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    private static boolean logBreak0(final Block block, final Player player) {
        if (CoreProtectHook.getCoreProtect() == null)
            return false;

        QAMain.DEBUG("Logging break to CoreProtect");

        try {
            return CoreProtectHook.getCoreProtect().logRemoval(player.getName(), block.getLocation(), block.getType(),
                    block.getBlockData());
        } catch (final Exception ignored) {
            return CoreProtectHook.getCoreProtect().logRemoval(player.getName(), block.getLocation(), block.getType(), block.getData());
        }
    }

    public static void logPlace(final Block block, final Player player) {
        try {
            if (!CoreProtectHook.logPlace0(block, player)) {
                QAMain.DEBUG("CoreProtect failed to log place: Returned false");
            }
        } catch (final Throwable e) {
            QAMain.DEBUG("Error while logging break to CoreProtect: " + e.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    private static boolean logPlace0(final Block block, final Player player) {
        if (CoreProtectHook.getCoreProtect() == null)
            return false;

        QAMain.DEBUG("Logging break to CoreProtect");

        try {
            return CoreProtectHook.getCoreProtect().logPlacement(player.getName(), block.getLocation(), block.getType(),
                    block.getBlockData());
        } catch (final Exception ignored) {
            return CoreProtectHook.getCoreProtect().logPlacement(player.getName(), block.getLocation(), block.getType(), block.getData());
        }
    }

    private static @Nullable CoreProtectAPI getCoreProtect() {
        if (CoreProtectHook.api == null) {

            final Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CoreProtect");

            // Check that CoreProtect is loaded
            if (!(plugin instanceof CoreProtect)) {
                return null;
            }

            // Check that the API is enabled
            final CoreProtectAPI CoreProtect = ((CoreProtect) plugin).getAPI();
            if (!CoreProtect.isEnabled()) {
                return null;
            }

            CoreProtectHook.api = CoreProtect;
        }

        return (CoreProtectAPI) CoreProtectHook.api;
    }
}
