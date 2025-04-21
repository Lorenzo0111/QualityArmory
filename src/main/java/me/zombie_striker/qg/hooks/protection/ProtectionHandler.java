package me.zombie_striker.qg.hooks.protection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.hooks.protection.implementation.GriefPreventionHook;
import me.zombie_striker.qg.hooks.protection.implementation.ResidenceHook;
import me.zombie_striker.qg.hooks.protection.implementation.TownyHook;
import me.zombie_striker.qg.hooks.protection.implementation.WorldGuardHook;

public class ProtectionHandler {
    private final static Set<ProtectionHook> compatibilities = new HashSet<>();

    public static void init() {
        final Map<String, Class<? extends ProtectionHook>> classes = new HashMap<>();
        classes.put("WorldGuard", WorldGuardHook.class);
        classes.put("Towny", TownyHook.class);
        classes.put("Residence", ResidenceHook.class);
        classes.put("GriefPrevention", GriefPreventionHook.class);

        for (final Map.Entry<String, Class<? extends ProtectionHook>> entry : classes.entrySet()) {
            try {
                final Constructor<? extends ProtectionHook> constructor = entry.getValue().getConstructor();

                ProtectionHandler.hook(entry.getKey(), constructor::newInstance);
            } catch (final Throwable ignored) {
            }
        }
    }

    public static boolean canPvp(final Location target) {
        return ProtectionHandler.compatibilities.stream().allMatch(compatibility -> compatibility.canPvp(target));
    }

    public static boolean canExplode(final Location target) {
        return ProtectionHandler.compatibilities.stream().allMatch(compatibility -> compatibility.canExplode(target));
    }

    public static boolean canBreak(final Location target) {
        return ProtectionHandler.compatibilities.stream().allMatch(compatibility -> compatibility.canBreak(target));
    }

    public static void hook(final String plugin, final CompatibilityConstructor constructor)
            throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if (Bukkit.getPluginManager().isPluginEnabled(plugin)
                && (boolean) QAMain.getInstance().a("hooks." + plugin, !plugin.equalsIgnoreCase("WorldGuard"))) {
            ProtectionHandler.compatibilities.add(constructor.create());

            QAMain.getInstance().getLogger().info("Hooked with " + plugin + "!");
        }
    }

    @FunctionalInterface
    private interface CompatibilityConstructor {
        ProtectionHook create() throws InvocationTargetException, InstantiationException, IllegalAccessException;
    }
}
