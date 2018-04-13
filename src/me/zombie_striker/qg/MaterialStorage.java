package me.zombie_striker.qg;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class MaterialStorage {

	private static List<MaterialStorage> store = new ArrayList<MaterialStorage>();

	public static MaterialStorage getMS(Material m, int d, int var) {
		return getMS(m, d, var, null);
	}

	public static MaterialStorage getMS(Material m, int d, int var, String extraValue) {
		for (MaterialStorage k : store) {
			if (k.m == m && (k.d == d || k.d == -1) && (k.varient == var || var == -1)
					&& ((extraValue != null && (extraValue.equals("-1")||(k.hasSpecialValue()&&extraValue.equals(k.getSpecialValue()))) || (extraValue == null && !k.hasSpecialValue()))))
				return k;
		}
		MaterialStorage mm = new MaterialStorage(m, d, var,extraValue);
		store.add(mm);
		return mm;
	}

	@SuppressWarnings("deprecation")
	public static MaterialStorage getMS(ItemStack is) {
		return getMS(is.getType(), is.getDurability(), getVarient(is),
				is.getType() == Material.SKULL_ITEM ? ((SkullMeta) is.getItemMeta()).getOwner() : null);
	}

	private int d;
	private Material m;
	private int varient = 0;
	private String specialValues = null;

	public int getData() {
		return d;
	}

	public boolean hasSpecialValue() {
		return specialValues != null;
	}

	public String getSpecialValue() {
		return specialValues;
	}

	public void setSpecialValue(String s) {
		this.specialValues = s;
	}

	public Material getMat() {
		return m;
	}

	private MaterialStorage(Material m, int d) {
		this.m = m;
		this.d = d;
	}
	private MaterialStorage(Material m, int d, int var) {
		this(m, d, var, null);
	}

	private MaterialStorage(Material m, int d, int var, String extraData) {
		this.m = m;
		this.d = d;
		this.varient = var;
		this.specialValues =extraData;
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
				if (lore.startsWith(Main.S_ITEM_VARIENTS)) {
					try {
						int id = Integer.parseInt(lore.split(":")[1].trim());
						return id;
					} catch (Error | Exception e4) {
						e4.printStackTrace();
						return 0;
					}
				}
			}
		}
		return 0;
	}
}
