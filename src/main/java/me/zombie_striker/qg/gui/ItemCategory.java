package me.zombie_striker.qg.gui;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class ItemCategory {
    private final String id;
    private final String name;
    private final Material iconType;
    private final String iconName;
    private final List<String> iconLore;
    private final String filter;

    public ItemCategory(String id, ConfigurationSection config) {
        this.id = id;
        this.name = config.getString("name", "&eCategory");

        ConfigurationSection iconSection = config.getConfigurationSection("icon");
        if (iconSection != null) {
            String typeStr = iconSection.getString("type", "CHEST");
            this.iconType = XMaterial.matchXMaterial(typeStr).orElse(XMaterial.CHEST).get();
            this.iconName = iconSection.getString("name", this.name);
            this.iconLore = iconSection.getStringList("lore");
        } else {
            this.iconType = XMaterial.CHEST.get();
            this.iconName = this.name;
            this.iconLore = null;
        }

        this.filter = config.getString("filter", "ALL").toUpperCase();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Material getIconType() {
        return iconType;
    }

    public String getIconName() {
        return iconName;
    }

    public List<String> getIconLore() {
        return iconLore;
    }

    public String getFilter() {
        return filter;
    }

}
