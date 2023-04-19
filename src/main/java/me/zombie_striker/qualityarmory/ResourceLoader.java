package me.zombie_striker.qualityarmory;

import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.qualityarmory.guns.Ammo;
import me.zombie_striker.qualityarmory.guns.Gun;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ResourceLoader {

    private File resourceFolder;
    private QAMain main;

    public ResourceLoader(File folder, QAMain main) {
        this.resourceFolder = folder;
        this.main = main;
    }

    public void loadResources() {
        loadResources(resourceFolder);
    }

    private void loadResources(File resourceFolder) {
        for (File file : resourceFolder.listFiles()) {
            if (file.isDirectory()) {
                loadResources(file);
            } else if (file.getName().endsWith(".yml")) {
                loadYML(file);
            }
        }
    }

    private void loadYML(File file) {
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
        ResourceType type = ResourceType.valueOf(fileConfiguration.getString("type"));
        if (type == null) {
            main.getLogger().info("Failed to load resource \"" + file.getName() + "\" as the type is not valid.");
            return;
        }
        String internalName = fileConfiguration.getString("name");
        Material material = Material.matchMaterial(fileConfiguration.getString("material"));
        int id = fileConfiguration.getInt("custommodeldata");
        if (material == null) {
            main.getLogger().info("Failed to load resource \"" + file.getName() + "\" as the material is not valid.");
            return;
        }
        CustomBaseObject customBaseObject = null;
        switch (type) {
            case GUN:
                customBaseObject = new Gun(internalName, MaterialStorage.getMS(material, id, 0));
                break;
            case AMMO:
                customBaseObject = new Ammo(internalName, MaterialStorage.getMS(material, id, 0));
                break;
            case MELEE:
                //TODO: Write Melee
                break;
            case MISC:
                customBaseObject = new CustomBaseObject(internalName, MaterialStorage.getMS(material, id, 0));
                break;
            default:
        }
        if (fileConfiguration.contains("data")) {
            for (String key : fileConfiguration.getConfigurationSection("data").getKeys(false)) {
                if (!fileConfiguration.isConfigurationSection("data." + key)) {
                    customBaseObject.setData(key, fileConfiguration.get("data." + key));
                }
            }
        }
        if (customBaseObject != null)
            main.registerCustomItem(customBaseObject);
    }
}
