package me.zombie_striker.qg.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import org.bukkit.configuration.file.FileConfiguration;

import me.zombie_striker.qg.QAMain;

public class MessagesYML {

    private final FileConfiguration c;
    private final File s;

    public MessagesYML(final File f) { this(null, f); }

    public MessagesYML(final String id, final File f) {
        this.s = f;
        if (!f.getParentFile().exists()) {
            f.getParentFile().mkdirs();
        }

        if (!this.s.exists()) {
            try (InputStream stream = QAMain.getInstance().getResource("lang/messages_" + id + ".yml")) {
                if (stream == null) {
                    this.createFile();
                } else {
                    Files.copy(stream, this.s.toPath());
                }
            } catch (final Throwable ignored) {
                this.createFile();
            }
        }
        this.c = CommentYamlConfiguration.loadConfiguration(this.s);
    }

    private void createFile() {
        try {
            this.s.createNewFile();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public Object a(final String path, final Object val) {
        if (!this.c.contains(path)) {
            this.c.set(path, val);
            this.save();
            return val;
        }
        return this.c.get(path);
    }

    public void set(final String path, final Object val) {
        this.c.set(path, val);
        this.save();
    }

    public void save() {
        try {
            this.c.save(this.s);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfig() { return this.c; }
}
