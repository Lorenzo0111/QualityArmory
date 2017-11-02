package me.zombie_striker.qg.ammo;

import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.MaterialStorage;

import org.bukkit.inventory.ItemStack;

public class AmmoShotGun implements Ammo {

	@Override
	public String getName() {
		return "Buckshot";
	}

	@Override
	public int getMaxAmount() {
		return 12;
	}

	@Override
	public boolean individualDrop() {
		return true;
	}

	@Override
	public MaterialStorage getItemData() {
		return MaterialStorage.getMS(Main.guntype, 16);
	}

	public AmmoShotGun(ItemStack[] ing, int a) {
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
