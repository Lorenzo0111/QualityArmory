package me.zombie_striker.qualityarmory.config;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
 
public class CommentYamlConfiguration extends YamlConfiguration {
    private File file;
	
    private Map<Integer, String> comments = Maps.newHashMap();
    @Override
    public void load(Reader reader) throws IOException, InvalidConfigurationException {
        StringBuilder builder = new StringBuilder();
 
        String line;
        try (BufferedReader input = reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader)) {
            int index = 0;
            while ((line = input.readLine()) != null) {
                if (line.startsWith("#") || line.trim().isEmpty()) {
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
        Objects.requireNonNull(file, "File cannot be null");
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

    public Object getOrSet(String path, Object val) {
        if(!this.contains(path)){
            this.set(path, val);
            if (file != null) {
                try {
                    this.save(file);
                } catch (IOException ignored) {}
            }
            return val;
        }
        return this.get(path);
    }
 
    @Override
    @Deprecated
    protected @NotNull String buildHeader() {
        return "";
    }

    protected String parseHeader(String input) {
        return "";
    }

    public void setFile(File file) {
        this.file = file;
    }

    public static CommentYamlConfiguration loadConfiguration(File file) {
        Objects.requireNonNull(file, "File cannot be null");
        CommentYamlConfiguration config = new CommentYamlConfiguration();
        config.setFile(file);
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