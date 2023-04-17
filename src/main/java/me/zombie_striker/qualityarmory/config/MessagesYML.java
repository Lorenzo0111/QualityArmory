package me.zombie_striker.qualityarmory.config;

import me.zombie_striker.qualityarmory.QAMain;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class MessagesYML {

	private FileConfiguration c;
	private File s;

	public MessagesYML(QAMain main,String id, File f) {
		s = f;
		if (!f.getParentFile().exists()) {
			f.getParentFile().mkdirs();
		}

		if(!s.exists()) {
			try (InputStream stream = main.getResource("lang/messages_" + id + ".yml")) {
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
	public String addPlaceHolders(String message, Object... replaceWithArray){
		String result = message;
		for(int i = 0; i < replaceWithArray.length;i+=2){
			result=result.replaceAll((String) replaceWithArray[i], (String) replaceWithArray[i+1]);
		}
		return result;
	}

	public Object getOrSet(String path, Object val){
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
