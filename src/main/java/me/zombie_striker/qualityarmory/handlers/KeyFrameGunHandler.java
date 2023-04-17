package me.zombie_striker.qualityarmory.handlers;

import me.zombie_striker.qualityarmory.QAMain;
import me.zombie_striker.qualityarmory.data.AnimationGunAction;
import me.zombie_striker.qualityarmory.data.AnimationKeyFrame;
import me.zombie_striker.qualityarmory.guns.WeaponSounds;
import me.zombie_striker.qualityarmory.interfaces.IHandler;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class KeyFrameGunHandler implements IHandler {

    private QAMain main;
    private HashMap<String, List<AnimationKeyFrame>> keyFrames = new HashMap<>();
    private HashMap<String, Integer> maxKeyFrame = new HashMap<>();

    /**
     * Tries to return the animation keyframe at the specified keyframe
     *
     * @param name
     * @param keyframe
     * @return May return null
     */
    public AnimationKeyFrame getAnimationKeyFrame(String name, int keyframe) {
        return keyFrames.get(name).get(keyframe);
    }

    public int getMaxKeyFrame(String name) {
        return maxKeyFrame.get(name);
    }

    @Override
    public void init(QAMain main) {
        this.main = main;
        File keyframeFolder = new File(main.getDataFolder(), "animations");
        if (!keyframeFolder.exists()) {
            keyframeFolder.mkdirs();
            generateAnimations(keyframeFolder);
        }
        for (File file : keyframeFolder.listFiles()) {
            if (file.getName().toLowerCase().endsWith(".yml")) {
                FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
                List<AnimationKeyFrame> keyframes = new LinkedList<>();
                if (fileConfiguration.contains("keyframes"))
                    for (String keyframe : fileConfiguration.getConfigurationSection("keyframes").getKeys(false)) {
                        int keyframeint = Integer.parseInt(keyframe);
                        String sound = null;
                        if (fileConfiguration.contains("keyframes." + keyframe + ".sound"))
                            sound = fileConfiguration.getString("keyframes." + keyframe + ".sound");
                        AnimationGunAction action = null;
                        if (fileConfiguration.contains("keyframes." + keyframe + ".action"))
                            action = AnimationGunAction.valueOf(fileConfiguration.getString("keyframes." + keyframe + ".action"));
                        AnimationKeyFrame akf = new AnimationKeyFrame(sound, action, keyframeint);
                        keyframes.add(akf);
                    }
                maxKeyFrame.put(file.getName().substring(0, file.getName().length() - 4), fileConfiguration.getInt("ticks"));
                keyFrames.put(file.getName().substring(0, file.getName().length() - 4), keyframes);
            }
        }
    }

    private void generateAnimations(File keyframeFolder) {
        new AnimationBuilder(keyframeFolder, "reload_single", 5).setAction(0,AnimationGunAction.CANCEL_IF_AMMO_FULL).setSound(1, WeaponSounds.RELOAD_BULLET.getSoundName()).setAction(5, AnimationGunAction.INCREMENT_BULLET).save();
        new AnimationBuilder(keyframeFolder, "reload_ak", 60).setAction(0,AnimationGunAction.CANCEL_IF_AMMO_FULL).setAction(1,AnimationGunAction.DROP_ALL_AMMO).setSound(1, WeaponSounds.RELOAD_AK47.getSoundName()).setSound(40, WeaponSounds.RELOAD_MAG_IN.getSoundName()).setSound(60, WeaponSounds.RELOAD_MAG_CLICK.getSoundName()).setAction(60, AnimationGunAction.RELOAD_ALL).save();
        new AnimationBuilder(keyframeFolder, "reload_default", 60).setAction(0,AnimationGunAction.CANCEL_IF_AMMO_FULL).setAction(1,AnimationGunAction.DROP_ALL_AMMO).setSound(1, WeaponSounds.RELOAD_MAG_OUT.getSoundName()).setSound(40, WeaponSounds.RELOAD_MAG_IN.getSoundName()).setSound(60, WeaponSounds.RELOAD_MAG_CLICK.getSoundName()).setAction(60, AnimationGunAction.RELOAD_ALL).save();

    }

    public class AnimationBuilder {
        private File config;
        private FileConfiguration fileConfiguration;

        public AnimationBuilder(File keyframeFolder, String name, int maxticks) {
            config = new File(keyframeFolder, name + ".yml");
            if (!config.exists()) try {
                config.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            fileConfiguration = YamlConfiguration.loadConfiguration(config);
            fileConfiguration.set("ticks", maxticks);
        }

        public AnimationBuilder set(String path, Object object) {
            fileConfiguration.set(path, object);
            return this;
        }

        public AnimationBuilder setSound(int keyframe, String sound) {
            fileConfiguration.set("keyframes." + keyframe + ".sound", sound);
            return this;
        }

        public AnimationBuilder setAction(int keyframe, AnimationGunAction action) {
            fileConfiguration.set("keyframes." + keyframe + ".action", action.name());
            return this;
        }

        public void save() {
            try {
                fileConfiguration.save(config);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
