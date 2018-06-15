package me.zombie_striker.qg.armor;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import me.zombie_striker.qg.ArmoryBaseObject;
import me.zombie_striker.qg.MaterialStorage;

public class ArmorObject implements ArmoryBaseObject {

	private String displayname;
	private String name;
	private ItemStack[] ing;
	private MaterialStorage storage;
	private List<String> lore;
	private double cost;
	private double dt;

	// TODO: Refine max heights
	private double heightMin = 1;
	private double heightMax = 1.5;
	private double shiftingHeightOffset = -0.1;

	public ArmorObject(String name, String displayname, List<String> lore, ItemStack[] ing, MaterialStorage st,
			double cost, double dt) {
		this.name = name;
		this.displayname = displayname;
		this.lore = lore;
		this.ing = ing;
		this.storage = st;
		this.cost = cost;
		this.dt = dt;
	}

	public double getMinH() {
		return heightMin;
	}

	public double getMaxH() {
		return heightMax;
	}
	public double getShifitngHeightOffset() {
		return shiftingHeightOffset;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ItemStack[] getIngredients() {
		return ing;
	}

	@Override
	public int getCraftingReturn() {
		return 1;
	}

	@Override
	public MaterialStorage getItemData() {
		return storage;
	}

	@Override
	public List<String> getCustomLore() {
		return lore;
	}

	@Override
	public String getDisplayName() {
		return displayname;
	}

	@Override
	public double cost() {
		return cost;
	}

	public double getDT() {
		return dt;
	}
	@Override
	public boolean is18Support() {
		return false;
	}

	@Override
	public void set18Supported(boolean b) {		
	}

}
