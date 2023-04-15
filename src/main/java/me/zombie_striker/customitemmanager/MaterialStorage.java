package me.zombie_striker.customitemmanager;

import me.zombie_striker.qualityarmory.QAMain;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MaterialStorage {

	private static final MaterialStorage EMPTY = new MaterialStorage(null, 0, 0);

	private static List<MaterialStorage> store = new ArrayList<MaterialStorage>();
	private int data;
	private Material material;
	private int variant = 0;
	private String specialValues = null;
	private String specialValues2 = null;

	private MaterialStorage(Material material, int data) {
		this.material = material;
		this.data = data;
	}

	private MaterialStorage(Material material, int data, int var) {
		this(material, data, var, null);
	}

	private MaterialStorage(Material material, int data, int var, String extraData) {
		this(material, data, var, extraData, null);
	}

	private MaterialStorage(Material material, int data, int var, String extraData, String ed2) {
		this.material = material;
		this.data = data;
		this.variant = var;
		this.specialValues = extraData;
		this.specialValues2 = ed2;
	}

	public static MaterialStorage getMS(Material m, int d, int var) {
		return getMS(m, d, var, null);
	}

	public static MaterialStorage getMS(Material m, int d, int var, String extraValue) {
		return getMS(m, d, var, extraValue, null);
	}

	public static MaterialStorage getMS(Material m, int d, int var, String extraValue, String ev2) {
		for (MaterialStorage k : store) {
			if (matchesMaterials(k, m, d)) if (matchVariants(k, var)) if (matchHeads(k, extraValue, ev2))
				return k;
		}
		MaterialStorage mm = new MaterialStorage(m, d, var, extraValue, ev2);
		store.add(mm);
		return mm;
	}

	private static boolean matchesMaterials(MaterialStorage k, Material m, int d) {
		return (k.material == m && (k.data == d || k.data == -1));
	}

	public static boolean matchVariants(MaterialStorage k, int var) {
		return (!k.hasVariant() && var == 0) || (k.variant == var);
	}

	public static boolean matchHeads(MaterialStorage k, String ex1, String ex2) {
		boolean exb1 = (!k.hasSpecialValue() || k.hasSpecialValue2()
				|| (ex1 != null && (ex1.equals("-1") || k.getSpecialValue().equals(ex1))));
		boolean exb2 = (!k.hasSpecialValue2()
				|| (ex2 != null && (ex2.equals("-1") || k.getSpecialValue2().equals(ex2))));
		return exb1 && exb2;
	}

	public static MaterialStorage getMS(ItemStack is) {
		return getMS(is, getVariant(is));
	}

	public static MaterialStorage getMS(ItemStack is, int variant) {

		if (is == null) {
			return EMPTY;
		}

		String extraData = is.getType() == MultiVersionLookup.getSkull() ? ((SkullMeta) is.getItemMeta()).getOwner()
				: null;
		String temp = null;
		if (extraData != null)
			temp = SkullHandler.getURL64(is);
		try {
			return getMS(is.getType(), is.getItemMeta().hasCustomModelData() ? is.getItemMeta().getCustomModelData() : 0, variant,
					is.getType() == MultiVersionLookup.getSkull() ? ((SkullMeta) is.getItemMeta()).getOwner() : null, temp);

		} catch (Error | Exception e4) {
		}
		return getMS(is.getType(), is.getDurability(), variant,
				is.getType() == MultiVersionLookup.getSkull() ? ((SkullMeta) is.getItemMeta()).getOwner() : null, temp);
	}

	public static int getVariant(ItemStack is) {
		if (is != null)
			if (is.hasItemMeta() && is.getItemMeta().hasLore()) {
				for (String lore : is.getItemMeta().getLore()) {
					if (lore.startsWith(QAMain.S_ITEM_VARIANTS_NEW)) {
						try {
							int id = Integer.parseInt(lore.split(QAMain.S_ITEM_VARIANTS_NEW)[1].trim());
							return id;
						} catch (Error | Exception e4) {
							e4.printStackTrace();
							return 0;
						}
					} else if (lore.startsWith(QAMain.S_ITEM_VARIANTS_LEGACY)) {
						try {
							int id = Integer.parseInt(lore.split(QAMain.S_ITEM_VARIANTS_LEGACY)[1].trim());
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

	public int getData() {
		return data;
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

	public boolean hasSpecialValue2() {
		return specialValues2 != null;
	}

	public String getSpecialValue2() {
		return specialValues2;
	}

	public void setSpecialValue2(String s) {
		this.specialValues2 = s;
	}

	public Material getMat() {
		return material;
	}

	public boolean hasVariant() {
		return variant > 0;
	}

	public int getVariant() {
		return variant;
	}

	@Override
	public String toString() {
		return "MaterialStorage{" +
				"d=" + data +
				", m=" + material +
				", variant=" + variant +
				", specialValues='" + specialValues + '\'' +
				", specialValues2='" + specialValues2 + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MaterialStorage that = (MaterialStorage) o;
		return data == that.data && variant == that.variant && material == that.material && Objects.equals(specialValues, that.specialValues) && Objects.equals(specialValues2, that.specialValues2);
	}

	@Override
	public int hashCode() {
		return Objects.hash(data, material, variant, specialValues, specialValues2);
	}
}
