package me.zombie_striker.customitemmanager;

import me.zombie_striker.customitemmanager.pack.ResourcepackProvider;
import me.zombie_striker.customitemmanager.pack.StaticPackProvider;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.Set;

public class CustomItemManager {

	private static ResourcepackProvider resourcepackProvider = null;
	private static HashMap<String, AbstractItem> customItemTypes = new HashMap<>();

    /**
     * @deprecated Use {@link #getResourcepack(Player)} instead
     * @return the resourcepack URL
     */
    @Deprecated
	public static String getResourcepack() {
		return resourcepackProvider.getFor(null);
	}

    /**
     * @deprecated Use {@link #setResourcepack(ResourcepackProvider)} instead
     * @param url the resourcepack URL
     */
    @Deprecated
	public static void setResourcepack(String url) {
		resourcepackProvider = new StaticPackProvider(url);
	}

    public static String getResourcepack(Player player) {
        return resourcepackProvider.getFor(player);
    }

    public static void setResourcepack(ResourcepackProvider provider) {
        resourcepackProvider = provider;
    }

	public static ResourcepackProvider getResourcepackProvider() {
		return resourcepackProvider;
	}

	public static Set<String> getCustomItemTypes(){return customItemTypes.keySet();}

	public static void registerItemType(File dataFolder, String key, AbstractItem item){
		customItemTypes.put(key,item);
	}
	public static AbstractItem getItemType(String key){
		return customItemTypes.get(key);
	}

	public static boolean isUsingCustomData(){
		try{
			new ItemStack(Material.DIAMOND_BLOCK).getItemMeta().hasCustomModelData();
			return true;
		}catch (Error | Exception e4){

		}
		return false;
	}


}
