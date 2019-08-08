package me.zombie_striker.customitemmanager;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.Set;

public class CustomItemManager {

	private static String resourcepack = null;
	private static HashMap<String, AbstractItem> customItemTypes = new HashMap<>();

	public static String getResourcepack() {
		return resourcepack;
	}
	public static void setResourcepack(String url){
		resourcepack = url;
	}

	public static Set<String> getCustomItemTypes(){return customItemTypes.keySet();}

	public static void registerItemType(File dataFolder, String key, AbstractItem item){
		customItemTypes.put(key,item);
		// item.initItems(dataFolder);
	}
	public static AbstractItem getItemType(String key){
		return customItemTypes.get(key);
	}
	public static AbstractItemFact getItemFact(String type){
		return customItemTypes.get(type).getItemFactory();
	}
	public static AbstractItemFact getItemFact(AbstractItem type){
		return type.getItemFactory();
	}

	public static boolean isUsingCustomData(){
		try{
			new ItemStack(Material.AIR).getItemMeta().getCustomModelData();
			return true;
		}catch (Error|Exception e4){

		}
		return false;
	}


}
