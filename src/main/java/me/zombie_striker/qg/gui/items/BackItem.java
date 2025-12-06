package me.zombie_striker.qg.gui.items;

import com.cryptomorin.xseries.XMaterial;
import de.themoep.inventorygui.StaticGuiElement;
import me.zombie_striker.qg.gui.MenuManager;
import me.zombie_striker.qg.utils.LocalUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class BackItem {
    private final MenuManager menuManager;
    private final boolean isShop;

    public BackItem(MenuManager menuManager, boolean isShop) {
        this.menuManager = menuManager;
        this.isShop = isShop;
    }

    public StaticGuiElement createElement() {
        String typeStr = menuManager.getMenuConfig().getString("menus.items.back.type", "BARRIER");
        Material type = XMaterial.matchXMaterial(typeStr).orElse(XMaterial.BARRIER).get();

        ItemStack item = new ItemStack(type);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            String name = menuManager.getMenuConfig().getString("menus.items.back.name", "&cBack to Categories");
            meta.setDisplayName(LocalUtils.colorize(name));

            List<String> lore = menuManager.getMenuConfig().getStringList("menus.items.back.lore");
            if (!lore.isEmpty()) meta.setLore(LocalUtils.colorize(lore));

            item.setItemMeta(meta);
        }

        return new StaticGuiElement('B', item, click -> {
            Player player = (Player) click.getWhoClicked();

            player.closeInventory();
            if (isShop) menuManager.openShopMenu(player);
            else menuManager.openCraftMenu(player);

            return true;
        });
    }
}
