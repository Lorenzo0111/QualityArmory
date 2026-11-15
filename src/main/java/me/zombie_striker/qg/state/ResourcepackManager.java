package me.zombie_striker.qg.state;

import me.zombie_striker.qg.config.CommentYamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Tracks which players received, are downloading or accepted the resourcepack, and the whitelist of
 * players who never get the prompt ({@code resourcepackwhitelist.yml}).
 */
public final class ResourcepackManager {
    private static final String WHITELIST_PATH = "Names_Of_players_to_bypass";

    private final File whitelistFile;
    private final Logger logger;
    private final Map<UUID, Long> sentAt = new HashMap<>();
    private final Set<UUID> accepted = new HashSet<>();
    private final Set<UUID> loading = new HashSet<>();
    private final List<String> bypassNames = new ArrayList<>();
    private CommentYamlConfiguration whitelist;

    public ResourcepackManager(File dataFolder, Logger logger) {
        this.whitelistFile = new File(dataFolder, "resourcepackwhitelist.yml");
        this.logger = logger;
    }

    /**
     * Loads the whitelist file. Keeps the players tracked in memory.
     */
    @SuppressWarnings("unchecked")
    public void load() {
        this.whitelist = CommentYamlConfiguration.loadConfiguration(this.whitelistFile);
        Object names = this.whitelist.getOrSet(WHITELIST_PATH, new ArrayList<String>());
        this.bypassNames.clear();
        if (names instanceof List)
            for (Object name : (List<Object>) names)
                this.bypassNames.add(String.valueOf(name));
    }

    public void save() {
        if (this.whitelist == null) return;
        try {
            this.whitelist.save(this.whitelistFile);
        } catch (IOException e) {
            this.logger.log(Level.SEVERE, "Could not save " + this.whitelistFile.getName(), e);
        }
    }

    /**
     * @return Whether the player is on the whitelist and never gets the resourcepack prompt.
     */
    public boolean isBypassing(String playerName) {
        return this.bypassNames.contains(playerName);
    }

    public void setBypassing(String playerName, boolean bypassing) {
        if (bypassing == this.isBypassing(playerName)) return;
        if (bypassing)
            this.bypassNames.add(playerName);
        else
            this.bypassNames.remove(playerName);
        this.whitelist.set(WHITELIST_PATH, new ArrayList<>(this.bypassNames));
    }

    /**
     * @return Whether the player accepted the resourcepack, bypassed it or doesn't need it.
     * Guns only work for these players while the resourcepack is enabled.
     */
    public boolean hasAccepted(UUID player) {
        return this.accepted.contains(player);
    }

    public void setAccepted(UUID player) {
        this.accepted.add(player);
    }

    /**
     * Records that the resourcepack was just sent to the player.
     */
    public void markSent(UUID player) {
        this.sentAt.put(player, System.currentTimeMillis());
    }

    /**
     * @return When the resourcepack was sent to the player (millis), or null if it wasn't.
     */
    public Long getSentAt(UUID player) {
        return this.sentAt.get(player);
    }

    public void clearSent(UUID player) {
        this.sentAt.remove(player);
    }

    public boolean isLoading(UUID player) {
        return this.loading.contains(player);
    }

    public void setLoading(UUID player, boolean loading) {
        if (loading)
            this.loading.add(player);
        else
            this.loading.remove(player);
    }

    public void onQuit(UUID player) {
        this.sentAt.remove(player);
        this.loading.remove(player);
        this.accepted.remove(player);
    }
}
