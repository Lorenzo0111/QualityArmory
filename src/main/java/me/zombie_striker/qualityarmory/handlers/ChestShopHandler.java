package me.zombie_striker.qualityarmory.handlers;

import com.Acrobot.ChestShop.Events.ItemParseEvent;
import com.Acrobot.ChestShop.Events.ItemStringQueryEvent;
import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.qualityarmory.QAMain;
import me.zombie_striker.qualityarmory.api.QualityArmory;
import me.zombie_striker.qualityarmory.interfaces.IHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChestShopHandler implements Listener, IHandler {

	@EventHandler
	public void onParseShop(ItemParseEvent e){
		CustomBaseObject base;
		if((base =QualityArmory.getCustomItemByName(e.getItemString()))!=null){
			e.setItem(QualityArmory.getCustomItemAsItemStack(base));
		}
	}

	@EventHandler
	public void onString(ItemStringQueryEvent e) {
		CustomBaseObject customItem = QualityArmory.getCustomItem(e.getItem());
		if (customItem != null && customItem.getName() != null) {
			e.setItemString(customItem.getName());
		}
	}

	@Override
	public void init(QAMain main) {
		Bukkit.getPluginManager().registerEvents(this, main);
	}
}
