package me.zombie_striker.qg.gui.menus;

import de.themoep.inventorygui.GuiElementGroup;
import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.qg.gui.ItemCategory;
import me.zombie_striker.qg.gui.MenuManager;
import me.zombie_striker.qg.gui.items.BackItem;
import me.zombie_striker.qg.gui.items.CraftingItem;
import me.zombie_striker.qg.gui.items.NavigationItem;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class CraftingItemsMenu extends BaseMenu {
    private final ItemCategory category;

    public CraftingItemsMenu(JavaPlugin plugin, MenuManager menuManager, ItemCategory category) {
        super(plugin, menuManager, "crafting_items");
        this.category = category;
    }

    @Override
    protected void addElements() {
        List<CustomBaseObject> items = menuManager.getItemsForCraftingCategory(category);

        GuiElementGroup group = new GuiElementGroup('.');

        for (CustomBaseObject item : items) {
            CraftingItem craftingItem = new CraftingItem(item);
            group.addElement(craftingItem.createElement('i'));
        }

        gui.addElement(group);

        NavigationItem prevItem = new NavigationItem(menuManager, false);
        gui.addElement(prevItem.createElement());

        NavigationItem nextItem = new NavigationItem(menuManager, true);
        gui.addElement(nextItem.createElement());

        BackItem backItem = new BackItem(menuManager, false);
        gui.addElement(backItem.createElement());
    }

    @Override
    protected String getTitle(Player player) {
        String raw = menuManager.getMenuConfig().getString("menus.crafting_items.title", "&e&lCrafting Bench");
        return raw.replace("%category%", category.getName());
    }
}
