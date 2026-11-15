package me.zombie_striker.qg.hooks;

import me.zombie_striker.qg.utils.ErrorReporter;
import me.zombie_striker.qg.utils.Compat;
import me.zombie_striker.qg.QAMain;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

public class CoreProtectHook {
    private static Object api = null;

    public static void logBreak(Block block, Player player) {
        try {
            if (!logBreak0(block, player)) {
                QAMain.DEBUG("CoreProtect failed to log break: Returned false");
            }
        } catch (RuntimeException | LinkageError e) {
            ErrorReporter.report("logging a block change to CoreProtect", e);
        }
    }

    @SuppressWarnings("deprecation")
    private static boolean logBreak0(Block block, Player player) {
        if (getCoreProtect() == null) return false;

        QAMain.DEBUG("Logging break to CoreProtect");

        if (Compat.FLATTENED)
            return getCoreProtect().logRemoval(player.getName(), block.getLocation(), block.getType(), block.getBlockData());
        return getCoreProtect().logRemoval(player.getName(), block.getLocation(), block.getType(), block.getData());
    }

    public static void logPlace(Block block, Player player) {
        try {
            if (!logPlace0(block, player)) {
                QAMain.DEBUG("CoreProtect failed to log place: Returned false");
            }
        } catch (RuntimeException | LinkageError e) {
            ErrorReporter.report("logging a block change to CoreProtect", e);
        }
    }

    @SuppressWarnings("deprecation")
    private static boolean logPlace0(Block block, Player player) {
        if (getCoreProtect() == null) return false;

        QAMain.DEBUG("Logging break to CoreProtect");

        if (Compat.FLATTENED)
            return getCoreProtect().logPlacement(player.getName(), block.getLocation(), block.getType(), block.getBlockData());
        return getCoreProtect().logPlacement(player.getName(), block.getLocation(), block.getType(), block.getData());
    }

    private static @Nullable CoreProtectAPI getCoreProtect() {
        if (api == null) {

            Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CoreProtect");

            // Check that CoreProtect is loaded
            if (!(plugin instanceof CoreProtect)) {
                return null;
            }

            // Check that the API is enabled
            CoreProtectAPI CoreProtect = ((CoreProtect) plugin).getAPI();
            if (!CoreProtect.isEnabled()) {
                return null;
            }

            api = CoreProtect;
        }

        return (CoreProtectAPI) api;
    }
}
