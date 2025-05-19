package me.zombie_striker.qg.hooks.protection;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.hooks.protection.implementation.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ProtectionHandler {
    private final static Set<ProtectionHook> compatibilities = new HashSet<>();

    public static void init() {
        Map<String, CompatibilityConstructor> classes = new HashMap<>();
        classes.put("WorldGuard", WorldGuardHook::new);
        classes.put("Towny", TownyHook::new);
        classes.put("Residence", ResidenceHook::new);
        classes.put("GriefPrevention", GriefPreventionHook::new);
        classes.put("Kingdoms", KingdomsHook::new);

        for (Map.Entry<String, CompatibilityConstructor> entry : classes.entrySet()) {
            try {
                hook(entry.getKey(), entry.getValue());
            } catch (Throwable ignored) {
            }
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
    public interface CompatibilityConstructor {
        ProtectionHook create() throws InvocationTargetException, InstantiationException, IllegalAccessException;
    }
}
