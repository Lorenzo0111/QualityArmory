package me.zombie_striker.qg.config;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
 
import java.io.*;
import java.util.*;
import java.util.logging.Level;
 
public class CommentYamlConfiguration extends YamlConfiguration {
	
    private Map<Integer, String> comments = Maps.newHashMap();
    @Override
    public void load(Reader reader) throws IOException, InvalidConfigurationException {
        StringBuilder builder = new StringBuilder();
 
        String line;
        try (BufferedReader input = reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader)) {
            int index = 0;
            while ((line = input.readLine()) != null) {
                if (line.startsWith("#") || line.isEmpty()) {
                    comments.put(index, line);
                }
                builder.append(line);
                builder.append(System.lineSeparator());
                index++;
            }
        }
        this.loadFromString(builder.toString());
    }
 
    @Override
    public void save(File file) throws IOException {
        Validate.notNull(file, "File cannot be null");
        Files.createParentDirs(file);
        String data = this.saveToString();
        if (comments.size() != 0) {
            String[] stringArray = data.split(System.lineSeparator());
            StringBuilder stringBuilder = new StringBuilder();
            int arrayIndex = 0;
            for (int i = 0; i < stringArray.length + comments.size(); i++) {
                if (comments.containsKey(i)) {
                    stringBuilder.append(System.lineSeparator()).append(comments.get(i));
                } else {
                    if (arrayIndex >= stringArray.length) {
                        stringBuilder.append(System.lineSeparator());
                    } else {
                        stringBuilder.append(System.lineSeparator()).append(stringArray[arrayIndex++]);
                    }
                }
            }
            data = stringBuilder.toString().substring(1);
        }
 
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), Charsets.UTF_8)) {
            writer.write(data);
        }
    }
 
    @Override
    protected String buildHeader() {
        return "";
    }
 
    @Override
    protected String parseHeader(String input) {
        return "";
    }
 
    public static YamlConfiguration loadConfiguration(File file) {
        Validate.notNull(file, "File cannot be null");
        YamlConfiguration config = new CommentYamlConfiguration();
        if(!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
        try {
            config.load(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException | InvalidConfigurationException var4) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, var4);
        }
 
        return config;
    }
}