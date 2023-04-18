package me.zombie_striker.customitemmanager.qa;

import com.jojodmo.itembridge.ItemBridge;
import com.jojodmo.itembridge.ItemBridgeListener;
import me.zombie_striker.qualityarmory.api.QualityArmory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class ItemBridgePatch  implements ItemBridgeListener {

	private static ItemBridge bridge;
	public static void setup(Plugin plugin){ // call setup where plugin is your plugin's instance
		bridge = new ItemBridge(plugin, "qualityarmory", "qa");
		bridge.registerListener(new ItemBridgePatch());
	}

	@Override
	public ItemStack fetchItemStack(String item) {
		return QualityArmory.getInstance().getCustomItemAsItemStack(item);
	}
}
