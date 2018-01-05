package me.zombie_striker.qg;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class CustomYml {

	private FileConfiguration c;
	private File s;
	public CustomYml(File f) {
		s = f;
		c = YamlConfiguration.loadConfiguration(s);
	}
	public Object a(String path, Object val){
		if(!c.contains(path)){
			c.set(path, val);
			save();
			return val;
		}
		return c.get(path);
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
