package me.zombie_striker.customitemmanager.pack;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.hooks.ViaVersionHook;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
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

        String[] version = ViaVersionHook.getVersion(player).split("\\.");
        int major = Integer.parseInt(version[0]);
        int minor = version.length > 1 ? Integer.parseInt(version[1]) : 0;
        int patch = version.length > 2 ? Integer.parseInt(version[2]) : 0;

        QAMain.DEBUG(player.getName() + " is using version " + major + "." + minor + "." + patch);

        String highest = findPack(major, minor, patch);

        QAMain.DEBUG("Fond pack with version " + highest);
        return versions.get(highest);
    }

    private @NotNull String findPack(int major, int minor, int patch) {
        String highest = "0";

        for (String key : versions.keySet()) {
            if (key.equals("0")) continue;

            String[] keyVersion = key.split("-");

            int keyMajor = Integer.parseInt(keyVersion[0]);
            int keyMinor = keyVersion.length > 1 ? Integer.parseInt(keyVersion[1]) : 0;
            int keyPatch = keyVersion.length > 2 ? Integer.parseInt(keyVersion[2]) : 0;

            if (
                    keyMajor < major ||
                            (keyMajor == major && keyMinor < minor) ||
                            (keyMajor == major && keyMinor == minor && keyPatch <= patch)
            ) {
                if (
                        highest.equals("0") ||
                                keyMajor > Integer.parseInt(highest.split("\\.")[0]) ||
                                (keyMajor == Integer.parseInt(highest.split("\\.")[0]) &&
                                        keyMinor > Integer.parseInt(highest.split("\\.")[1]))
                ) {
                    highest = key;
                }
            }
        }

        return highest;
    }

    @Override
    public Object serialize() {
        return new HashMap<>(versions);
    }
}
