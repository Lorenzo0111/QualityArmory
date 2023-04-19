package me.zombie_striker.qualityarmory;

import me.zombie_striker.customitemmanager.MaterialStorage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ResourceBuilder {

    private File configFile;
    private FileConfiguration config;

    public ResourceBuilder(File configfile, ResourceType type, String name){
        this.configFile = configfile;
        this.config = YamlConfiguration.loadConfiguration(configfile);
        this.config.set("type",type.name());
        this.config.set("name",name);
    }
    public ResourceBuilder setMaterialData(MaterialStorage ms){
        this.config.set("material",ms.getMat().name());
        this.config.set("custommodeldata",ms.getData());
        return this;
    }

    public ResourceBuilder setDefaultGunValues(String displayname, int ammoMax, String ammotype, boolean automaticFiring, float damage, float bulletspeed, String sound){
        setData(ConfigKey.CUSTOMITEM_AMMOTYPE,ammotype);
        setData(ConfigKey.CUSTOMITEM_DISPLAYNAME,displayname);
        setData(ConfigKey.CUSTOMITEM_MAXBULLETS,ammoMax);
        setData(ConfigKey.CUSTOMITEM_AUTOMATIC_FIRING,automaticFiring);
        setData(ConfigKey.CUSTOMITEM_DAMAGE,damage);
        setData(ConfigKey.CUSTOMITEM_SPEED,bulletspeed);
        setData(ConfigKey.CUSTOMITEM_FIRING_SOUND,sound);
        return this;
    }

    public ResourceBuilder setValue(String path, Object value){
        this.config.set(path,value);
        return this;
    }
    public ResourceBuilder setData(ConfigKey datapath, Object data){
        this.config.set("data."+datapath.getKey(),data);
        return this;
    }
    public void build(){
        try {
            config.save(configFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
