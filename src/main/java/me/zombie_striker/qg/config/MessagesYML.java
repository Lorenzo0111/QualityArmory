package me.zombie_striker.qg.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import me.zombie_striker.qg.QAMain;
import org.bukkit.configuration.file.FileConfiguration;

public class MessagesYML {

	private FileConfiguration c;
	private File s;

	public MessagesYML(File f) {
		this(null, f);
	}

	public MessagesYML(String id, File f) {
		s = f;
		if (!f.getParentFile().exists()) {
			f.getParentFile().mkdirs();
		}

		if(!s.exists()) {
			try (InputStream stream = QAMain.getInstance().getResource("lang/messages_" + id + ".yml")) {
				if (stream == null) {
					createFile();
				} else {
					Files.copy(stream, s.toPath());
				}
			} catch (Throwable ignored) {
				createFile();
			}
		}
		c = CommentYamlConfiguration.loadConfiguration(s);
	}

	private void createFile() {
		try {
			s.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Object a(String path, Object val){
		if(!c.contains(path)){
			c.set(path, val);
			save();
			return val;
		}
		return c.get(path);
	}
	public void set(String path, Object val) {
		c.set(path, val);
		save();
	}
	public void save(){
		try {
			c.save(s);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public FileConfiguration getConfig() {
		return c;
	}
}
