package me.zombie_striker.qg.hooks.protection;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.hooks.protection.implementation.ResidenceHook;
import me.zombie_striker.qg.hooks.protection.implementation.TownyHook;
import me.zombie_striker.qg.hooks.protection.implementation.WorldGuardHook;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;

public class ProtectionHandler {
    private final static Set<ProtectionHook> compatibilities = new HashSet<>();

    public static void init() {
        hook("WorldGuard", WorldGuardHook::new);
        hook("Towny", TownyHook::new);
        hook("Residence", ResidenceHook::new);
    }

    public static boolean canPvp(Location target) {
        return compatibilities.stream().allMatch(compatibility -> compatibility.canPvp(target));
    }

    public static boolean canExplode(Location target) {
        return compatibilities.stream().allMatch(compatibility -> compatibility.canExplode(target));
    }

    public static void hook(String plugin, CompatibilityConstructor constructor) {
        if (Bukkit.getPluginManager().isPluginEnabled(plugin) && (boolean) QAMain.getInstance().a("hooks." + plugin, true)) {
            compatibilities.add(constructor.create());
        }
    }

    @FunctionalInterface
    private interface CompatibilityConstructor {
        ProtectionHook create();
    }
}
