package me.zombie_striker.qg.hooks.protection;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.hooks.protection.implementation.GriefPreventionHook;
import me.zombie_striker.qg.hooks.protection.implementation.ResidenceHook;
import me.zombie_striker.qg.hooks.protection.implementation.TownyHook;
import me.zombie_striker.qg.hooks.protection.implementation.WorldGuardHook;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ProtectionHandler {
    private final static Set<ProtectionHook> compatibilities = new HashSet<>();

    public static void init() {
        Map<String,Class<? extends ProtectionHook>> classes = new HashMap<>();
        classes.put("WorldGuard", WorldGuardHook.class);
        classes.put("Towny", TownyHook.class);
        classes.put("Residence", ResidenceHook.class);
        classes.put("GriefPrevention", GriefPreventionHook.class);

        for (Map.Entry<String,Class<? extends ProtectionHook>> entry : classes.entrySet()) {
            try {
                Constructor<? extends ProtectionHook> constructor = entry.getValue().getConstructor();

                hook(entry.getKey(), constructor::newInstance);
            } catch (Throwable ignored) {}
        }
    }

    public static boolean canPvp(Location target) {
        return compatibilities.stream().allMatch(compatibility -> compatibility.canPvp(target));
    }

    public static boolean canExplode(Location target) {
        return compatibilities.stream().allMatch(compatibility -> compatibility.canExplode(target));
    }

    public static boolean canBreak(Location target) {
        return compatibilities.stream().allMatch(compatibility -> compatibility.canBreak(target));
    }

    public static void hook(String plugin, CompatibilityConstructor constructor) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if (Bukkit.getPluginManager().isPluginEnabled(plugin) && (boolean) QAMain.getInstance().a("hooks." + plugin, !plugin.equalsIgnoreCase("WorldGuard"))) {
            compatibilities.add(constructor.create());

            QAMain.getInstance().getLogger().info("Hooked with " + plugin + "!");
        }
    }

    @FunctionalInterface
    private interface CompatibilityConstructor {
        ProtectionHook create() throws InvocationTargetException, InstantiationException, IllegalAccessException;
    }
}
