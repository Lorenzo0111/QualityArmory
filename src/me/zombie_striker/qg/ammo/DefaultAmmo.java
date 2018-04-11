package me.zombie_striker.qg.ammo;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import me.zombie_striker.qg.MaterialStorage;

public class DefaultAmmo implements Ammo {

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

	public DefaultAmmo(String name, MaterialStorage ms, int maxAmount, boolean indiDrop, int returnamount,
			double cost, ItemStack[] ingredients, double piercing) {
		this(name, ChatColor.GOLD + name, null, ms, maxAmount, indiDrop, returnamount, cost, ingredients,
				piercing);
	}

	public DefaultAmmo(String name, List<String> lore, MaterialStorage ms, int maxAmount, boolean indiDrop,
			int returnamount, double cost, ItemStack[] ingredients, double piercing) {
		this(name, ChatColor.GOLD + name, lore, ms, maxAmount, indiDrop, returnamount, cost, ingredients,
				piercing);
	}

	public DefaultAmmo(String name, String displayName,MaterialStorage ms, int maxAmount, boolean indiDrop,
			int returnamount, double cost, ItemStack[] ingredients, double piercing) {
		this(name, displayName, null, ms, maxAmount, indiDrop, returnamount, cost, ingredients, piercing);
	}

	public DefaultAmmo(String name, String displayName, List<String> lore, MaterialStorage ms, int maxAmount,
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

	public double getPiercingDamage() {
		return piercingDamage;
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
		return ms;
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
