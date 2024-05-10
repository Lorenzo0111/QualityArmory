package me.zombie_striker.qg.config;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class AttachmentYML extends GunYML {
    public AttachmentYML(final File file) { super(file); }

    public YamlConfiguration getBaseGunFile() {
        if (this.file == null)
            return null;
        final File newGunFolder = new File(this.file.getParentFile().getParentFile(), "newGuns");
        for (final File f : newGunFolder.listFiles()) {
            final YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
            if (!config.contains("invalid") || !config.getBoolean("invalid"))
                if (config.contains("name")) {
                    if (config.getString("name").equalsIgnoreCase(this.getBaseGun())) {
                        return config;
                    }
                }
        }
        return null;
    }

    public String getBaseGun() { return (String) this.get("baseGun"); }

    @Override
    public AttachmentYML setKilledByMessage(final String message) {
        this.set(false, "KilledByMessage", message);
        return this;
    }

    @Override
    public AttachmentYML verify(final String name, final Object v) {
        if (!this.fileConfig.contains(name)) {
            final FileConfiguration otherConfig = this.getBaseGunFile();
            if (otherConfig != null) {
                if (otherConfig.contains(name))
                    this.fileConfig.set(name, otherConfig.get(name));
                return this;
            }
        }
        return (AttachmentYML) super.verify(name, v);
    }
}
