package me.zombie_striker.qg.miscitems;

import org.bukkit.Material;

import me.zombie_striker.qg.Main;

public class IronSightsToggleItem {

	public static int getData(){
		return Main.usedIronSightsData;
	}
	public static Material getMat() {
		return Main.usedIronSightsMaterial;
	}
	public static String getItemName(){
		return "Iron Sights Enabled";
	}
}
