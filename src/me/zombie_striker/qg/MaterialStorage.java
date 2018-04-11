package me.zombie_striker.qg;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MaterialStorage {

	private static List<MaterialStorage> store = new ArrayList<MaterialStorage>();

	public static MaterialStorage getMS(Material m, int d, int var) {
		for (MaterialStorage k : store) {
			if (k.m == m && (k.d == d || k.d == -1) && (k.varient == var || var == -1))
				return k;
		}
		MaterialStorage mm = new MaterialStorage(m, d, var);
		store.add(mm);
		return mm;
	}

	public static MaterialStorage getMS(ItemStack is) {
		return getMS(is.getType(), is.getDurability(), getVarient(is));
	}

	private int d;
	private Material m;
	private int varient = 0;

	public int getData() {
		return d;
	}

	public Material getMat() {
		return m;
	}

	private MaterialStorage(Material m, int d) {
		this.m = m;
		this.d = d;
	}

	private MaterialStorage(Material m, int d, int var) {
		this.m = m;
		this.d = d;
		this.varient = var;
	}

	public boolean isVarient() {
		return varient > 0;
	}

	public int getVarient() {
		return varient;
	}

	public static int getVarient(ItemStack is) {
		if (is.hasItemMeta() && is.getItemMeta().hasLore()) {
			for (String lore : is.getItemMeta().getLore()) {
				if (lore.startsWith(Main.S_ITEM_VARIENTS))
					try {
						int id = Integer.parseInt(lore.split(":")[1]);
						return id;
					} catch (Error | Exception e4) {
						return 0;
					}
			}
		}
		return 0;
	}
}
