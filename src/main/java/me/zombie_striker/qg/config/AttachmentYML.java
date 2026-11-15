package me.zombie_striker.qg.config;

import me.zombie_striker.qg.config.items.ItemKeys;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class AttachmentYML extends GunYML {
	public AttachmentYML(File file) {
		super(file);
	}


	public YamlConfiguration getBaseGunFile() {
		if(this.file==null)
			return null;
		File newGunFolder = new File(this.file.getParentFile().getParentFile(), "newGuns");
		for (File f : newGunFolder.listFiles()) {
			YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
			if (!config.contains("invalid") || !config.getBoolean("invalid"))
				if (config.contains("name")) {
					if (config.getString("name").equalsIgnoreCase(getBaseGun())) {
						return config;
					}
				}
		}
		return null;
	}

	public String getBaseGun() {
		return (String) get("base-gun");
	}

	public AttachmentYML setKilledByMessage(String message) {
		set(false, "killed-by-message", message);
		return this;
	}
	@Override
	public AttachmentYML verify(String name, Object v) {
		name = ItemKeys.translate(name);
		if (!fileConfig.contains(name)) {
			FileConfiguration otherConfig = getBaseGunFile();
			if (otherConfig != null) {
				if (otherConfig.contains(name))
					fileConfig.set(name, otherConfig.get(name));
				return this;
			}
		}
		return (AttachmentYML) super.verify(name, v);
	}
}
