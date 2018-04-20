package me.zombie_striker.qg.ammo;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import me.zombie_striker.qg.ArmoryBaseObject;
import me.zombie_striker.qg.MaterialStorage;

public class Ammo implements ArmoryBaseObject{


	String name;
	String disName;
	int maxAmount;
	boolean indiDrop;

	MaterialStorage ms;

	int returnAmount;

	ItemStack[] ingredients;

	List<String> lore;

	private double cost;

	private double piercingDamage = 1;

	public static final String dontuseskin = "Dont Use Skin";
	private String skullowner = null;
	private String base64SkinURL = dontuseskin;

	public Ammo(String name, MaterialStorage ms, int maxAmount, boolean indiDrop, int returnamount,
			double cost, ItemStack[] ingredients, double piercing) {
		this(name, ChatColor.GOLD + name, null, ms, maxAmount, indiDrop, returnamount, cost, ingredients,
				piercing);
	}

	public Ammo(String name, List<String> lore, MaterialStorage ms, int maxAmount, boolean indiDrop,
			int returnamount, double cost, ItemStack[] ingredients, double piercing) {
		this(name, ChatColor.GOLD + name, lore, ms, maxAmount, indiDrop, returnamount, cost, ingredients,
				piercing);
	}

	public Ammo(String name, String displayName,MaterialStorage ms, int maxAmount, boolean indiDrop,
			int returnamount, double cost, ItemStack[] ingredients, double piercing) {
		this(name, displayName, null, ms, maxAmount, indiDrop, returnamount, cost, ingredients, piercing);
	}

	public Ammo(String name, String displayName, List<String> lore, MaterialStorage ms, int maxAmount,
			boolean indiDrop, int returnamount, double cost, ItemStack[] ingredients, double piercing) {
		this.name = name;
		this.disName = displayName;
		this.ms = ms;
		this.maxAmount = maxAmount;
		this.indiDrop = indiDrop;
		this.ingredients = ingredients;
		this.returnAmount = returnamount;
		this.lore = lore;

		this.cost = cost;

		this.piercingDamage = piercing;

		AmmoType.addAmmo(this, name);
	}
	
	public void setCraftingReturn(int i) {
		returnAmount = i;
	}
	
	public boolean hasCustomSkin() {
		return ! dontuseskin.equals(base64SkinURL);
	}
	public String getCustomSkin() {
		return base64SkinURL;
	}
	public void setCustomSkin(String skin) {
		this.base64SkinURL = skin;
	}
	
	public void setSkullOwner(String s) {
		skullowner = s;
	}
	
	public boolean isSkull() {
		return skullowner!=null;
	}
	public String getSkullOwner() {
		return skullowner;
	}

	public double getPiercingDamage() {
		return piercingDamage;
	}

	public double cost() {
		return cost;
	}

	public String getName() {
		return name;
	}

	public int getMaxAmount() {
		return maxAmount;
	}

	public boolean individualDrop() {
		return indiDrop;
	}

	public MaterialStorage getItemData() {
		return ms;
	}

	public int getCraftingReturn() {
		return returnAmount;
	}

	public ItemStack[] getIngredients() {
		return ingredients;
	}

	public List<String> getCustomLore() {
		return lore;
	}

	public String getDisplayName() {
		return disName;
	}
}
