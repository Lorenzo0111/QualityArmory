package me.zombie_striker.qg.ammo;

import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.MaterialStorage;

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
}
