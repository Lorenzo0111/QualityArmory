package me.zombie_striker.customitemmanager.pack;

import me.zombie_striker.qg.hooks.ViaVersionHook;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * A {@link ResourcepackProvider} that provides different resourcepacks based on the player's version.
 * Make sure to put a default version at index 0.
 */
public class MultiVersionPackProvider implements ResourcepackProvider {
    private final Map<Integer, String> versions;

    public MultiVersionPackProvider(Map<Integer, String> versions) {
        this.versions = versions;
    }

    public MultiVersionPackProvider(ConfigurationSection config) {
        Map<Integer, String> versions = new HashMap<>();
        config.getKeys(false).forEach(key -> versions.put(Integer.parseInt(key), config.getString(key)));
        this.versions = versions;
    }

    @Override
    public String getFor(@Nullable Player player) {
        int version = ViaVersionHook.getVersion(player);
        return versions.getOrDefault(version, versions.get(0));
    }

    @Override
    public Object serialize() {
        Map<String, String> map = new HashMap<>();
        versions.forEach((k, v) -> map.put(k.toString(), v));

        return map;
    }
}
