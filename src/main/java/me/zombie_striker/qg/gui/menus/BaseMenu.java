package me.zombie_striker.qg.gui.menus;

import com.cryptomorin.xseries.XMaterial;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import me.zombie_striker.qg.gui.MenuManager;
import me.zombie_striker.qg.utils.LocalUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseMenu {
    protected final JavaPlugin plugin;
    protected final MenuManager menuManager;
    private final String id;
    protected InventoryGui gui;

    public BaseMenu(JavaPlugin plugin, MenuManager menuManager, String id) {
        this.plugin = plugin;
        this.menuManager = menuManager;
        this.id = id;
    }

    public void build(Player player) {
        String title = LocalUtils.colorize(getTitle(player));
        String[] structure = getStructure();

        gui = new InventoryGui(plugin, player, title, structure);
        gui.setFiller(new ItemStack(Material.AIR));

        addDecoratorItems();
        addElements();
    }

    protected void addDecoratorItems() {
        ConfigurationSection custom = menuManager.getMenuConfig().getConfigurationSection("menus.items.custom");
        if (custom == null) return;

        for (String key : custom.getKeys(false)) {
            ConfigurationSection section = custom.getConfigurationSection(key);
            if (section == null) continue;

            Material type = XMaterial.matchXMaterial(section.getString("type", "BARRIER"))
                    .orElse(XMaterial.BARRIER)
                    .get();

            ItemStack item = new ItemStack(type);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(LocalUtils.colorize(section.getString("name", "&r")));

                List<String> lore = section.getStringList("lore");
                if (!lore.isEmpty()) meta.setLore(LocalUtils.colorize(lore));

                item.setItemMeta(meta);
            }

            char marker = key.charAt(0);
            gui.addElement(new StaticGuiElement(marker, item, click -> {
                click.getRawEvent().setCancelled(true);
                return true;
            }));
        }
    }

    protected abstract void addElements();

    public void open(Player player) {
        if (gui == null) {
            build(player);
        }
        gui.show(player);
    }

    public String getId() {
        return id;
    }

    protected String getTitle(Player player) {
        return menuManager.getMenuConfig().getString("menus." + id + ".title", "Menu");
    }

    protected String[] getStructure() {
        List<String> structureList = menuManager.getMenuConfig()
                .getStringList("menus." + id + ".structure")
                .stream()
                .map(s -> s.replace(" ", ""))
                .collect(Collectors.toList());

        if (structureList.isEmpty()) {
            return new String[]{
                    "#########",
                    "#.......#",
                    "#.......#",
                    "#.......#",
                    "#########"
            };
        }
        return structureList.toArray(new String[0]);
    }

    public InventoryGui getGui() {
        return gui;
    }
}
