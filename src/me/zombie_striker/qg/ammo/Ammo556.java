package me.zombie_striker.qg.ammo;

import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.MaterialStorage;

import org.bukkit.inventory.ItemStack;

public class Ammo556 implements Ammo {

	@Override
	public String getName() {
		return "5.56x45mm";
	}

	@Override
	public int getMaxAmount() {
		return 50;
	}

	@Override
	public boolean individualDrop() {
		return false;
	}

	@Override
	public MaterialStorage getItemData() {
		return MaterialStorage.getMS(Main.guntype, 14);
	}

	public Ammo556(ItemStack[] ing, int a) {
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
