package me.zombie_striker.qg.ammo;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.zombie_striker.qg.MaterialStorage;

public class DefaultAmmo implements Ammo{

	String name;
	String disName;
	int maxAmount;
	boolean indiDrop;
	
	Material baseType;
	short data;
	
	int returnAmount;
	
	ItemStack[] ingredients;	
	
	List<String> lore;
	
	private double cost;
	
	
	public DefaultAmmo(String name, Material m, int data, int maxAmount, boolean indiDrop, int returnamount,double cost,  ItemStack[] ingredients) {
		this(name,ChatColor.GOLD+name,null,m,data,maxAmount,indiDrop,returnamount,cost,ingredients);
	}
	public DefaultAmmo(String name, List<String> lore,Material m, int data, int maxAmount, boolean indiDrop, int returnamount, double cost, ItemStack[] ingredients) {
		this(name,ChatColor.GOLD+name,lore,m,data,maxAmount,indiDrop,returnamount,cost,ingredients);
	}
	public DefaultAmmo(String name, String displayName, Material m, int data, int maxAmount, boolean indiDrop,int returnamount,double cost,  ItemStack[] ingredients) {
		this(name,displayName,null,m,data,maxAmount,indiDrop,returnamount,cost,ingredients);		
	}
	public DefaultAmmo(String name, String displayName, List<String> lore, Material m, int data, int maxAmount, boolean indiDrop,int returnamount, double cost, ItemStack[] ingredients) {
		this.name = name;
		this.disName = displayName;
		this.baseType = m;
		this.data = (short) data;
		this.maxAmount = maxAmount;
		this.indiDrop = indiDrop;
		this.ingredients=ingredients;
		this.returnAmount = returnamount;
		this.lore = lore;
		
		AmmoType.addAmmo(this, name);
	}
	
@Override
public double cost() {
	return cost;
}	

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getMaxAmount() {
		return maxAmount;
	}

	@Override
	public boolean individualDrop() {
		return indiDrop;
	}

	@Override
	public MaterialStorage getItemData() {
		return MaterialStorage.getMS(baseType, data);
	}


	@Override
	public int getCraftingReturn() {
		return returnAmount;
	}

	@Override
	public ItemStack[] getIngredients() {
		return ingredients;
	}

	@Override
	public List<String> getCustomLore() {
		return lore;
	}

	@Override
	public String getDisplayName() {
		return disName;
	}
}
