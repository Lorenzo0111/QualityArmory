package me.zombie_striker.qg;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;

public class ArmoryYML {

	FileConfiguration fileConfig;
	File save;
	boolean saveNow = false;

	public ArmoryYML(File file) {
		save = file;
		if (!file.getParentFile().exists())
			file.getParentFile().mkdirs();
		if (!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		fileConfig = CommentYamlConfiguration.loadConfiguration(file);
	}

	protected void setNoSave(boolean force, String name, Object v) {
		if (!fileConfig.contains(name) || (force && !fileConfig.get(name).equals(v))) {
			fileConfig.set(name, v);
			saveNow = true;
		}
	}

	public void set(boolean force, String name, Object v) {
		if (!fileConfig.contains(name) || (force && !fileConfig.get(name).equals(v))) {
			fileConfig.set(name, v);
			save();
		}
	}

	public void save() {
		try {
			fileConfig.save(save);
			saveNow = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
