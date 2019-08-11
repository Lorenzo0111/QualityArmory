package me.zombie_striker.qg.config;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import me.zombie_striker.qg.QAMain;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class MessagesYML extends BaseYML {

	public MessagesYML(File dataFolder, String language) {
		super(null);
		String fileName = "lang/message_" + language + ".yml";
		File file = new File(dataFolder, fileName);
		if (!file.exists()) {
			// save default file.
			try {
				QAMain.getInstance().saveResource(fileName, true);
			} catch (IllegalArgumentException e) {
				// if the language can't find, copy an default config and change file name to that language.
				QAMain.getInstance().getLogger().log(Level.SEVERE, e.getMessage());
				String resourcePath = "lang/message_en.yml";
				QAMain.getInstance().saveResource(resourcePath, true);
				File newFile = new File(dataFolder, resourcePath);
				newFile.renameTo(file);
			}
		}
		super.init(file);
	}

}
