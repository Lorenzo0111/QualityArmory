package me.zombie_striker.qg.hooks.protection.implementation;


import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import me.zombie_striker.qg.hooks.protection.ProtectionHook;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class WorldGuardHook implements ProtectionHook {
    private final RegionContainer regionContainer;
    private final WorldGuard worldGuard;
    private final WorldGuardPlugin plugin;

    public WorldGuardHook() {
        worldGuard = WorldGuard.getInstance();
        regionContainer = worldGuard.getPlatform().getRegionContainer();
        plugin = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
    }

    @Override
    public boolean canPvp(@NotNull Location location) {
        for (ProtectedRegion k : regionContainer.get(BukkitAdapter.adapt(location.getWorld())).getRegions().values()) {
            if (k.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ())) {
                if (k.getFlag(Flags.PVP) == com.sk89q.worldguard.protection.flags.StateFlag.State.DENY)
                    return false;
            }
        }
        return true;    }

    @Override
    public boolean canExplode(@NotNull Location location) {
        for (ProtectedRegion k : regionContainer.get(BukkitAdapter.adapt(location.getWorld())).getRegions().values()) {
            if (k.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ())) {
                if (k.getFlag(Flags.OTHER_EXPLOSION) == com.sk89q.worldguard.protection.flags.StateFlag.State.DENY)
                    return false;
            }
        }

        return true;
    }

    private LocalPlayer wrapPlayer(Player player) {
        return plugin.wrapPlayer(player);
    }
}
