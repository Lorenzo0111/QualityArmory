package me.zombie_striker.qg.gui.items;

import com.cryptomorin.xseries.XMaterial;
import de.themoep.inventorygui.GuiPageElement;
import me.zombie_striker.qg.gui.MenuManager;
import me.zombie_striker.qg.utils.LocalUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class NavigationItem {
    private final MenuManager menuManager;
    private final boolean forward;

    public NavigationItem(MenuManager menuManager, boolean forward) {
        this.menuManager = menuManager;
        this.forward = forward;
    }

    public GuiPageElement createElement() {
        GuiPageElement.PageAction action = forward ? GuiPageElement.PageAction.NEXT : GuiPageElement.PageAction.PREVIOUS;

        List<String> text = new ArrayList<>();

        ItemStack item = createItem();

        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();

            if (meta != null && meta.hasDisplayName())
                text.add(meta.getDisplayName());
            if (meta != null && meta.hasLore() && meta.getLore() != null)
                text.addAll(meta.getLore());
        }

        return new GuiPageElement(forward ? '>' : '<', item, action, text.toArray(new String[0]));
    }

    private ItemStack createItem() {
        String itemId = forward ? "next" : "previous";
        String typeStr = menuManager.getMenuConfig().getString("menus.items." + itemId + ".type", "ARROW");
        Material type = XMaterial.matchXMaterial(typeStr).orElse(XMaterial.ARROW).get();

        ItemStack item = new ItemStack(type);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            String name = menuManager.getMenuConfig().getString("menus.items." + itemId + ".name",
                    forward ? "&aNext Page" : "&cPrevious Page");
            meta.setDisplayName(LocalUtils.colorize(name));

            String lorePath = "menus.items." + itemId + ".lore";

            List<String> lore = menuManager.getMenuConfig().getStringList(lorePath);
            if (!lore.isEmpty()) meta.setLore(LocalUtils.colorize(lore));

            item.setItemMeta(meta);
        }

        return item;
    }

}
