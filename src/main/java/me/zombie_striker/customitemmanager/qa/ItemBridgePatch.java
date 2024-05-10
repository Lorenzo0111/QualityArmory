package me.zombie_striker.customitemmanager.qa;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.jojodmo.itembridge.ItemBridge;
import com.jojodmo.itembridge.ItemBridgeListener;

import me.zombie_striker.qg.api.QualityArmory;

public class ItemBridgePatch implements ItemBridgeListener {

    private static ItemBridge bridge;

    public static void setup(final Plugin plugin) { // call setup where plugin is your plugin's instance
        ItemBridgePatch.bridge = new ItemBridge(plugin, "qualityarmory", "qa");
        ItemBridgePatch.bridge.registerListener(new ItemBridgePatch());
    }

    @Override
    public ItemStack fetchItemStack(final String item) { return QualityArmory.getCustomItemAsItemStack(item); }
}
