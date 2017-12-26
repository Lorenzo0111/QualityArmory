package me.zombie_striker.qg.ammo;

import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.MaterialStorage;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public class Ammo9mm implements Ammo {

	@Override
	public String getName() {
		return "9mm.ACP";
	}

	@Override
	public int getMaxAmount() {
		return 50;
	}

	@Override
	public boolean individualDrop() {
		return true;
	}

	@Override
	public MaterialStorage getItemData() {
		return MaterialStorage.getMS(Main.guntype, 15);
	}

	public Ammo9mm(ItemStack[] ing, int a) {
		this.ing = ing;
		this.a = a;
	}

	int a;

	@Override
	public int getCraftingReturn() {
		return a;
	}

	ItemStack[] ing;

	@Override
	public ItemStack[] getIngredients() {
		return ing;
	}

	@Override
	public List<String> getCustomLore() {
		return null;
	}

	@Override
	public String getDisplayName() {
		return ChatColor.GOLD + getName();
	}
}
