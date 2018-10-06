package me.zombie_striker.qg.miscitems;

import org.bukkit.Material;

import me.zombie_striker.qg.QAMain;

public class IronSightsToggleItem {

	public static int getData(){
		return QAMain.usedIronSightsData;
	}
	public static Material getMat() {
		return QAMain.usedIronSightsMaterial;
	}
	public static String getItemName(){
		return "Iron Sights Enabled";
	}
}
