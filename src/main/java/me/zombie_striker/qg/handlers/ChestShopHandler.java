package me.zombie_striker.qg.handlers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.Acrobot.ChestShop.Events.ItemParseEvent;
import com.Acrobot.ChestShop.Events.ItemStringQueryEvent;

import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.qg.api.QualityArmory;

public class ChestShopHandler implements Listener {

    @EventHandler
    public void onParseShop(final ItemParseEvent e) {
        CustomBaseObject base;
        if ((base = QualityArmory.getCustomItemByName(e.getItemString())) != null) {
            e.setItem(QualityArmory.getCustomItemAsItemStack(base));
        }
    }

    @EventHandler
    public void onString(final ItemStringQueryEvent e) {
        final CustomBaseObject customItem = QualityArmory.getCustomItem(e.getItem());
        if (customItem != null && customItem.getName() != null) {
            e.setItemString(customItem.getName());
        }
    }
}
