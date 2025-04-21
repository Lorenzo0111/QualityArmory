package me.zombie_striker.qg.config;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import me.zombie_striker.qg.ammo.Ammo;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.guns.utils.WeaponType;
import me.zombie_striker.qg.handlers.MultiVersionLookup;

public class ArmoryYML {

    FileConfiguration fileConfig;
    File file;
    public boolean saveNow = false;

    public ArmoryYML(final File file) {
        this.file = file;
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        if (!file.exists())
            try {
                file.createNewFile();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        this.fileConfig = CommentYamlConfiguration.loadConfiguration(file);
    }

    public Object get(final String path) { return this.fileConfig.get(path); }

    public long getLong(final String path) { return this.fileConfig.getLong(path); }

    public boolean contains(final String path) { return this.fileConfig.contains(path); }

    public ArmoryYML set(final String name, final Object v) { return this.set(false, name, v); }

    public ArmoryYML set(final boolean force, final String name, final Object v) {
        final long lastmodifiedFile = this.file.lastModified();
        final long lastmodifiedInternal = this.contains("lastModifiedByQA") ? this.getLong("lastModifiedByQA")
                : (this.contains("AllowUserModifications") && this.fileConfig.getBoolean("AllowUserModifications") ? 0
                        : System.currentTimeMillis());

        if (!force && lastmodifiedFile - lastmodifiedInternal > 5000) {
            return this; // The file has been changed sometime after QA made any changes.
        }
        if (!this.contains(name) || !this.get(name).equals(v)) {
            this.fileConfig.set(name, v);
            this.saveNow = true;
        }
        return this;
    }

    public ArmoryYML verify(final String name, final Object v) {
        if (!this.contains(name)) {
            this.fileConfig.set(name, v);
            this.saveNow = true;
        }
        return this;
    }

    public void done() {
        this.verifyAllTagsExist();
        if (this.saveNow) {
            this.save();
        }
    }

    public void save() {
        try {
            this.putTimeStamp();
            this.fileConfig.save(this.file);
            this.saveNow = false;
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    public ArmoryYML setSkullType(final String skullowner) {
        this.set("id", org.bukkit.SkullType.PLAYER.ordinal());
        this.set("material", MultiVersionLookup.getSkull().name());
        this.set("skull_owner", skullowner);
        this.set("skull_owner_custom_url", Ammo.NO_SKIN_STRING);
        return this;
    }

    public ArmoryYML setInvalid(final boolean invalid) {
        this.set("invalid", invalid);
        return this;
    }

    public ArmoryYML setLore(final List<String> lore) {
        this.set("lore", lore);
        return this;
    }

    public ArmoryYML setLore(final String... lore) {
        this.set("lore", lore);
        return this;
    }

    public ArmoryYML setVariant(final int var) {
        this.set("variant", var);
        return this;
    }

    public ArmoryYML setDurability(final int durib) {
        this.set("durability", durib);
        return this;
    }

    public ArmoryYML setPrice(final int cost) {
        this.set("price", cost);
        return this;
    }

    public ArmoryYML setSoundOnEquip(final String sound) {
        this.set("sound_equip", sound);
        return this;
    }

    public ArmoryYML setSoundOnHit(final String sound) {
        this.set("sound_meleehit", sound);
        return this;
    }

    public ArmoryYML setMaterial(final Material mat) {
        this.set("material", mat.name());
        return this;
    }

    public ArmoryYML setWeaponSound(final WeaponSounds sound) {
        this.set("weaponsounds", sound.getSoundName());
        return this;
    }

    public ArmoryYML setMaxItemStack(final int amount) {
        this.set("maxItemStack", amount);
        return this;
    }

    public void verifyAllTagsExist() {
        if (this.contains("AllowUserModifications")) {
            final boolean allowChanges = (boolean) this.get("AllowUserModifications");
            this.set(true, "AllowUserModifications", null);
            if (!allowChanges)
                this.putTimeStamp();
        }
        this.verify("invalid", false);
        this.verify("weaponsounds", WeaponSounds.getSoundByType(WeaponType.RIFLE));
        this.verify("damage", 3);

        this.verify("durability", 1000);

        this.verify("price", 1000);
        this.verify("maxItemStack", 1);
    }

    public ArmoryYML putTimeStamp() {
        this.fileConfig.set("lastModifiedByQA", System.currentTimeMillis());
        this.saveNow = true;
        return this;
    }

    public long getTimestamp() { return this.contains("lastModifiedByQA") ? (long) this.get("lastModifiedByQA") : 0; }

}
