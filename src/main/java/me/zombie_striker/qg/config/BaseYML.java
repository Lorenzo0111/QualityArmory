package me.zombie_striker.qg.config;

import me.zombie_striker.qg.QAMain;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConstructor;
import org.bukkit.configuration.file.YamlRepresenter;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BaseYML extends YamlConfiguration {
    private File configFile;

    public BaseYML(File configFile) {
        init(configFile);
    }

    public BaseYML(File dataFolder, String fileName) {
        init(dataFolder, fileName);
    }

    protected void init(File dataFolder, String fileName) {
        File file = new File(dataFolder, fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                getLogger().log(Level.SEVERE, "Cannot create " + file, e);
            }
        }
        init(file);
    }

    protected void init(File configFile) {
        if (null != configFile) {
            this.configFile = configFile;
            try {
//                getLogger().log(Level.SEVERE, "BaseYML init load config file: " + this.configFile);
                load(this.configFile);
//                getLogger().log(Level.SEVERE, "BaseYML load config file " + this.configFile + " done.");
            } catch (Exception e) {
                getLogger().log(Level.SEVERE, "Cannot load " + this.configFile, e);
            }
        }
    }

    public Logger getLogger() {
        return QAMain.getInstance().getLogger();
    }

    public Object a(String path, Object def){
        if(!contains(path)){
            set(path, def);
            return def;
        }
        return super.get(path);
    }

    @Override
    public void set(String path, Object val) {
        super.set(path, val);
        saveConfig();
    }

//    private final DumperOptions yamlOptions = new DumperOptions();
//    private final Representer yamlRepresenter = new YamlRepresenter();
//    private Yaml yaml = new Yaml(new YamlConstructor(), yamlRepresenter, yamlOptions);
//
//    @Override
//    public void loadFromString(@NotNull String contents) throws InvalidConfigurationException {
//        super.loadFromString(contents);
//        try {
//            yaml.load(contents);
//            getLogger().log(Level.SEVERE, "新建yaml载入内容：\n" + contents);
//        } catch (Exception e) {
//        }
//    }

    private void saveConfig(){
        try {
            super.save(configFile);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not save config to " + configFile);
        }
    }


//    @Override
//    protected @NotNull String buildHeader() {
//        String header = options().header();
//        if (null == header) {
//            header = "";
//            getLogger().log(Level.SEVERE, "Can not find the header");
//        }
//        return header;
//    }

    @Override
    protected @NotNull String parseHeader(@NotNull String input) {
        String[] lines = input.split("\r?\n", -1);
        StringBuilder result = new StringBuilder();
        for (String line : lines) {
            if (line.startsWith(COMMENT_PREFIX)) {
                result.append(line.substring(COMMENT_PREFIX.length())).append("\n");
            } else {
                break;
            }
        }
        return result.toString();
    }

    public static BaseYML loadConfiguration(@NotNull File file) {
        Validate.notNull(file, "File cannot be null");
        return new BaseYML(file);
    }
}
