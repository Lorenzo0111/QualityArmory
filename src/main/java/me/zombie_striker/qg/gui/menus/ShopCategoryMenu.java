package me.zombie_striker.qg.gui.menus;

import de.themoep.inventorygui.GuiElementGroup;
import me.zombie_striker.qg.gui.ItemCategory;
import me.zombie_striker.qg.gui.MenuManager;
import me.zombie_striker.qg.gui.items.CategorySelectionItem;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class ShopCategoryMenu extends BaseMenu {

    public ShopCategoryMenu(JavaPlugin plugin, MenuManager menuManager) {
        super(plugin, menuManager, "shop_categories");
    }

    @Override
    protected void addElements() {
        List<ItemCategory> categories = menuManager.getShopCategories();
        
        GuiElementGroup group = new GuiElementGroup('.');

        for (ItemCategory category : categories) {
            CategorySelectionItem categoryItem = new CategorySelectionItem(plugin, menuManager, category, true);
            group.addElement(categoryItem.createElement('c'));
        }

        gui.addElement(group);
    }
}
