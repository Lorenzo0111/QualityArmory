package me.zombie_striker.qg.config;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;

public class MessagesYML {

	private FileConfiguration c;
	private File s;
	public MessagesYML(File f) {
		s = f;
		c = CommentYamlConfiguration.loadConfiguration(s);
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
