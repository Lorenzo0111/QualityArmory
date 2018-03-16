package me.zombie_striker.qg.armor;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import me.zombie_striker.qg.MaterialStorage;

public class Kevlar extends ArmorObject{
	public Kevlar(MaterialStorage ms,ItemStack[] ing, double dt, double cost) {
		super("KevlarMk1",ChatColor.AQUA+"Kevlar Vest Mk.1", null, ing,ms,cost,dt);
	}
}
