package me.zombie_striker.qg.gui;

import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.ammo.Ammo;
import me.zombie_striker.qg.armor.ArmorObject;
import me.zombie_striker.qg.gui.menus.CraftingCategoryMenu;
import me.zombie_striker.qg.gui.menus.ShopCategoryMenu;
import me.zombie_striker.qg.guns.Gun;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MenuManager {
    private final JavaPlugin plugin;
    private FileConfiguration menuConfig;
    private final Map<String, ItemCategory> shopCategories = new LinkedHashMap<>();
    private final Map<String, ItemCategory> craftingCategories = new LinkedHashMap<>();

    public MenuManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadMenuConfig();
        loadCategories();
    }

    private void loadMenuConfig() {
        File menuFile = new File(plugin.getDataFolder(), "menu.yml");

        if (!menuFile.exists()) {
            plugin.saveResource("menu.yml", false);
        }

        menuConfig = YamlConfiguration.loadConfiguration(menuFile);

        InputStream defConfigStream = plugin.getResource("menu.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream));
            menuConfig.setDefaults(defConfig);
        }
    }

    private void loadCategories() {
        shopCategories.clear();
        craftingCategories.clear();

        ConfigurationSection shopCategoriesSection = menuConfig.getConfigurationSection("categories.shop");
        if (shopCategoriesSection != null) {
            for (String key : shopCategoriesSection.getKeys(false)) {
                ConfigurationSection categorySection = shopCategoriesSection.getConfigurationSection(key);
                if (categorySection != null) {
                    shopCategories.put(key, new ItemCategory(key, categorySection));
                }
            }
        }

        ConfigurationSection craftingCategoriesSection = menuConfig.getConfigurationSection("categories.crafting");
        if (craftingCategoriesSection != null) {
            for (String key : craftingCategoriesSection.getKeys(false)) {
                ConfigurationSection categorySection = craftingCategoriesSection.getConfigurationSection(key);
                if (categorySection != null) {
                    craftingCategories.put(key, new ItemCategory(key, categorySection));
                }
            }
        }
    }

    public List<ItemCategory> getShopCategories() {
        return new ArrayList<>(shopCategories.values());
    }

    public List<ItemCategory> getCraftingCategories() {
        return new ArrayList<>(craftingCategories.values());
    }

    public List<CustomBaseObject> getItemsForShopCategory(ItemCategory category) {
        return getItemsForCategory(category, true);
    }

    public List<CustomBaseObject> getItemsForCraftingCategory(ItemCategory category) {
        return getItemsForCategory(category, false);
    }

    private List<CustomBaseObject> getItemsForCategory(ItemCategory category, boolean isShop) {
        List<CustomBaseObject> allItems = new ArrayList<>();

        allItems.addAll(QAMain.gunRegister.values());
        allItems.addAll(QAMain.ammoRegister.values());
        allItems.addAll(QAMain.miscRegister.values());
        allItems.addAll(QAMain.armorRegister.values());

        String filter = category.getFilter();
        List<CustomBaseObject> filtered = allItems.stream().filter(item -> {
            switch (filter.toUpperCase()) {
                case "GUN":
                    return item instanceof Gun;
                case "AMMO":
                    return item instanceof Ammo;
                case "ARMOR":
                    return item instanceof ArmorObject;
                case "MISC":
                    return !(item instanceof Gun || item instanceof Ammo || item instanceof ArmorObject);
                case "ALL":
                default:
                    return true;
            }
        }).collect(Collectors.toList());

        return filtered.stream()
                .filter(item -> isShop
                        ? item.getPrice() >= 0 && item.isEnableShop()
                        : item.getIngredientsRaw() != null)
                .collect(Collectors.toList());
    }

    public void openShopMenu(Player player) {
        ShopCategoryMenu menu = new ShopCategoryMenu(plugin, this);
        menu.open(player);
    }

    public void openCraftMenu(Player player) {
        CraftingCategoryMenu menu = new CraftingCategoryMenu(plugin, this);
        menu.open(player);
    }

    public FileConfiguration getMenuConfig() {
        return menuConfig;
    }

    public void reload() {
        loadMenuConfig();
        loadCategories();
    }
}
