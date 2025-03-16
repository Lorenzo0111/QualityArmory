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
    private final Map<String, String> versions;

    public MultiVersionPackProvider(Map<String, String> versions) {
        this.versions = versions;
    }

    public MultiVersionPackProvider(ConfigurationSection config) {
        Map<String, String> versions = new HashMap<>();
        config.getKeys(false).forEach(key -> versions.put(key, config.getString(key)));
        this.versions = versions;
    }

    @Override
    public String getFor(@Nullable Player player) {
        if (player == null) return versions.get("0");

        String version = ViaVersionHook.getVersion(player);
        return versions.getOrDefault(version.replace(".", "-"), versions.get("0"));
    }

    @Override
    public Object serialize() {
        return new HashMap<>(versions);
    }
}
