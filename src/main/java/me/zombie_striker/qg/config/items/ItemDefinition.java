package me.zombie_striker.qg.config.items;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.config.system.BaseConfiguration;
import me.zombie_striker.qg.config.system.Config;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

/**
 * An item file: a gun, ammo, misc item, armor or attachment.
 * <p>
 * Item files are only migrated and read: missing values are not written back, so hand written files and
 * shared packs stay as they are. Optional values are nullable and only applied when present.
 */
public abstract class ItemDefinition extends BaseConfiguration {
    private static final String STAMP = "last-modified-by-qa";
    private static final String ALLOW_USER_CHANGES = "allow-user-modifications";
    /** Same tolerance as {@link me.zombie_striker.qg.config.ArmoryYML}. */
    private static final long USER_EDIT_TOLERANCE = 5000;

    @Config
    public boolean invalid = false;
    @Config
    public String name;
    @Config
    public String material;
    @Config
    public int id = 0;
    @Config
    public int variant = 0;
    @Config
    public String displayName;
    @Config
    public List<String> lore;
    @Config
    public List<String> craftingRequirements = new ArrayList<>();
    @Config
    public Double price;
    @Config
    public boolean allowInShop = true;
    @Config
    public Integer craftingReturnAmount;
    @Config
    public Integer maxItemStack;

    private final long fileModified;
    private Boolean editedByUser;

    protected ItemDefinition(File file) {
        super(file, QAMain.getInstance().getLogger());
        this.fileModified = file.lastModified();
        migration(1, this::renameKeys);
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    protected boolean writesDefaults() {
        return false;
    }

    @Override
    protected boolean logsMigration() {
        return false;
    }

    private void renameKeys(FileConfiguration config) {
        // Decide before anything changes whether the admin edited this default file
        this.editedByUser = isEditedByUser(config);
        Map<String, String> renames = new LinkedHashMap<>(ItemKeys.RENAMED);
        renames.putAll(typeRenames());

        // When several keys map to the same new key, keep the first in the map (or an existing new key)
        Set<String> claimed = new HashSet<>();
        for (Map.Entry<String, String> rename : renames.entrySet()) {
            String from = rename.getKey();
            if (!config.contains(from) || config.isConfigurationSection(from)) continue;
            boolean newKeyPresent = config.contains(rename.getValue()) && !renames.containsKey(rename.getValue());
            if (newKeyPresent || !claimed.add(rename.getValue()))
                config.set(from, null);
        }

        // Rebuild with the new names, keeping every value where it was in the file
        YamlConfiguration renamed = new YamlConfiguration();
        for (String path : config.getKeys(true)) {
            if (!config.isConfigurationSection(path))
                renamed.set(renames.getOrDefault(path, path), config.get(path));
        }
        for (String key : config.getKeys(false))
            config.set(key, null);
        for (String path : renamed.getKeys(true)) {
            if (!renamed.isConfigurationSection(path))
                config.set(path, renamed.get(path));
        }
    }

    /**
     * @return Keys renamed only in this item type, applied after (so with lower priority than) the shared ones.
     */
    protected Map<String, String> typeRenames() {
        return Collections.emptyMap();
    }

    /**
     * Files QualityArmory manages keep a timestamp of its last save; a newer file means the admin edited it.
     */
    private boolean isEditedByUser(FileConfiguration config) {
        Object stamp = config.contains("lastModifiedByQA") ? config.get("lastModifiedByQA") : config.get(STAMP);
        boolean allowChanges = config.getBoolean("AllowUserModifications", config.getBoolean(ALLOW_USER_CHANGES));
        long internal = stamp instanceof Number ? ((Number) stamp).longValue() : (allowChanges ? 0 : System.currentTimeMillis());
        return this.fileModified - internal > USER_EDIT_TOLERANCE;
    }

    /**
     * Keeps managed default files managed after migration: the timestamp is refreshed only for files the admin
     * didn't edit, so edited files keep being left alone.
     */
    @Override
    protected void beforeSave() {
        if (this.editedByUser != null && !this.editedByUser && this.config.contains(STAMP))
            this.config.set(STAMP, System.currentTimeMillis());
    }

    /**
     * @param fallbackColor Color code used when the file has no display name.
     * @return The display name, or the name in the given color.
     */
    public String displayNameOr(String fallbackColor) {
        return displayName != null ? displayName : fallbackColor + name;
    }

    /**
     * @return The raw value at a path, for values that don't have a field.
     */
    public Object getRaw(String path) {
        return this.config.get(path);
    }

    public boolean contains(String path) {
        return this.config.contains(path);
    }
}
