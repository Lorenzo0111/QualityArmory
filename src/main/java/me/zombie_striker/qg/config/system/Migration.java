package me.zombie_striker.qg.config.system;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * A migration step, run once when a file is upgraded past the version it is registered for.
 *
 * @see BaseConfiguration#migration(int, Migration)
 */
@FunctionalInterface
public interface Migration {
    void migrate(FileConfiguration config);
}
