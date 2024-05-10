package me.zombie_striker.qg.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Files;

public class CommentYamlConfiguration extends YamlConfiguration {
    private File file;

    private final Map<Integer, String> comments = Maps.newHashMap();

    @Override
    public void load(final Reader reader) throws IOException, InvalidConfigurationException {
        final StringBuilder builder = new StringBuilder();

        String line;
        try (BufferedReader input = reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader)) {
            int index = 0;
            while ((line = input.readLine()) != null) {
                if (line.startsWith("#") || line.trim().isEmpty()) {
                    this.comments.put(index, line);
                }
                builder.append(line);
                builder.append(System.lineSeparator());
                index++;
            }
        }
        this.loadFromString(builder.toString());
    }

    @Override
    public void save(final File file) throws IOException {
        Objects.requireNonNull(file, "File cannot be null");
        Files.createParentDirs(file);
        String data = this.saveToString();
        if (this.comments.size() != 0) {
            final String[] stringArray = data.split(System.lineSeparator());
            final StringBuilder stringBuilder = new StringBuilder();
            int arrayIndex = 0;
            for (int i = 0; i < stringArray.length + this.comments.size(); i++) {
                if (this.comments.containsKey(i)) {
                    stringBuilder.append(System.lineSeparator()).append(this.comments.get(i));
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

    public Object getOrSet(final String path, final Object val) {
        if (!this.contains(path)) {
            this.set(path, val);
            if (this.file != null) {
                try {
                    this.save(this.file);
                } catch (final IOException ignored) {
                }
            }
            return val;
        }
        return this.get(path);
    }

    @Override
    @Deprecated
    protected @NotNull String buildHeader() { return ""; }

    protected String parseHeader(final String input) { return ""; }

    public void setFile(final File file) { this.file = file; }

    public static CommentYamlConfiguration loadConfiguration(final File file) {
        Objects.requireNonNull(file, "File cannot be null");
        final CommentYamlConfiguration config = new CommentYamlConfiguration();
        config.setFile(file);
        if (!file.exists())
            try {
                file.createNewFile();
            } catch (final IOException e1) {
                e1.printStackTrace();
            }
        try {
            config.load(file);
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException | InvalidConfigurationException var4) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, var4);
        }

        return config;
    }
}