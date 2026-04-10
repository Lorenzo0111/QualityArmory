package me.zombie_striker.qg.gui.menus;

import de.themoep.inventorygui.GuiElementGroup;
import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.qg.gui.ItemCategory;
import me.zombie_striker.qg.gui.MenuManager;
import me.zombie_striker.qg.gui.items.BackItem;
import me.zombie_striker.qg.gui.items.NavigationItem;
import me.zombie_striker.qg.gui.items.ShopItem;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class ShopItemsMenu extends BaseMenu {
    private final ItemCategory category;

    public ShopItemsMenu(JavaPlugin plugin, MenuManager menuManager, ItemCategory category) {
        super(plugin, menuManager, "shop_items");
        this.category = category;
    }

    @Override
    protected void addElements() {
        List<CustomBaseObject> items = menuManager.getItemsForShopCategory(category);

        GuiElementGroup group = new GuiElementGroup('.');

        for (CustomBaseObject item : items) {
            ShopItem shopItem = new ShopItem(item);
            group.addElement(shopItem.createElement('i'));
        }

        gui.addElement(group);

        NavigationItem prevItem = new NavigationItem(menuManager, false);
        gui.addElement(prevItem.createElement());

        NavigationItem nextItem = new NavigationItem(menuManager, true);
        gui.addElement(nextItem.createElement());

        BackItem backItem = new BackItem(menuManager, true);
        gui.addElement(backItem.createElement());
    }

    @Override
    protected String getTitle(Player player) {
        String raw = menuManager.getMenuConfig().getString("menus.shop_items.title", "&6&lWeapons Shop");
        return raw.replace("%category%", category.getName());
    }
}
