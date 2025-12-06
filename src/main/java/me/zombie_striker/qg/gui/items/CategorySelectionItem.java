package me.zombie_striker.qg.gui.items;

import de.themoep.inventorygui.StaticGuiElement;
import me.zombie_striker.qg.gui.ItemCategory;
import me.zombie_striker.qg.gui.MenuManager;
import me.zombie_striker.qg.gui.menus.CraftingItemsMenu;
import me.zombie_striker.qg.gui.menus.ShopItemsMenu;
import me.zombie_striker.qg.utils.LocalUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class CategorySelectionItem {
    private final JavaPlugin plugin;
    private final MenuManager menuManager;
    private final ItemCategory category;
    private final boolean isShop;

    public CategorySelectionItem(JavaPlugin plugin, MenuManager menuManager, ItemCategory category, boolean isShop) {
        this.plugin = plugin;
        this.menuManager = menuManager;
        this.category = category;
        this.isShop = isShop;
    }

    public StaticGuiElement createElement(char slot) {
        Material type = category.getIconType();
        ItemStack item = new ItemStack(type);

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            String name = category.getIconName();
            meta.setDisplayName(LocalUtils.colorize(name));

            List<String> lore = category.getIconLore();
            if (lore != null && !lore.isEmpty())
                meta.setLore(LocalUtils.colorize(lore));

            item.setItemMeta(meta);
        }

        return new StaticGuiElement(slot, item, click -> {
            Player player = (Player) click.getWhoClicked();

            player.closeInventory();

            if (isShop) {
                ShopItemsMenu menu = new ShopItemsMenu(plugin, menuManager, category);
                menu.open(player);
            } else {
                CraftingItemsMenu menu = new CraftingItemsMenu(plugin, menuManager, category);
                menu.open(player);
            }

            return true;
        });
    }
}
