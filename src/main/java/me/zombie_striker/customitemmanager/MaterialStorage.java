package me.zombie_striker.customitemmanager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.SkullMeta;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.handlers.MultiVersionLookup;
import me.zombie_striker.qg.handlers.SkullHandler;

public class MaterialStorage {

    private static final MaterialStorage EMPTY = new MaterialStorage(null, 0, 0);

    private static List<MaterialStorage> store = new ArrayList<MaterialStorage>();
    private final int d;
    private final Material m;
    private int variant = 0;
    private String specialValues = null;
    private String specialValues2 = null;

    private MaterialStorage(final Material m, final int d) {
        this.m = m;
        this.d = d;
    }

    private MaterialStorage(final Material m, final int d, final int var) { this(m, d, var, null); }

    private MaterialStorage(final Material m, final int d, final int var, final String extraData) { this(m, d, var, extraData, null); }

    private MaterialStorage(final Material m, final int d, final int var, final String extraData, final String ed2) {
        this.m = m;
        this.d = d;
        this.variant = var;
        this.specialValues = extraData;
        this.specialValues2 = ed2;
    }

    public static MaterialStorage getMS(final Material m, final int d, final int var) { return MaterialStorage.getMS(m, d, var, null); }

    public static MaterialStorage getMS(final Material m, final int d, final int var, final String extraValue) {
        return MaterialStorage.getMS(m, d, var, extraValue, null);
    }

    public static MaterialStorage getMS(final Material m, final int d, final int var, final String extraValue, final String ev2) {
        for (final MaterialStorage k : MaterialStorage.store) {
            if (MaterialStorage.matchesMaterials(k, m, d))
                if (MaterialStorage.matchVariants(k, var))
                    if (MaterialStorage.matchHeads(k, extraValue, ev2))
                        return k;
        }
        final MaterialStorage mm = new MaterialStorage(m, d, var, extraValue, ev2);
        MaterialStorage.store.add(mm);
        return mm;
    }

    private static boolean matchesMaterials(final MaterialStorage k, final Material m, final int d) {
        return (k.m == m && (k.d == d || k.d == -1));
    }

    public static boolean matchVariants(final MaterialStorage k, final int var) {
        return (!k.hasVariant() && var == 0) || (k.variant == var);
    }

    public static boolean matchHeads(final MaterialStorage k, final String ex1, final String ex2) {
        final boolean exb1 = (!k.hasSpecialValue() || k.hasSpecialValue2()
                || (ex1 != null && (ex1.equals("-1") || k.getSpecialValue().equals(ex1))));
        final boolean exb2 = (!k.hasSpecialValue2() || (ex2 != null && (ex2.equals("-1") || k.getSpecialValue2().equals(ex2))));
        return exb1 && exb2;
    }

    public static MaterialStorage getMS(final ItemStack is) { return MaterialStorage.getMS(is, MaterialStorage.getVariant(is)); }

    public static MaterialStorage getMS(final ItemStack is, final int variant) {

        if (is == null) {
            return MaterialStorage.EMPTY;
        }

        final String extraData = is.getType() == MultiVersionLookup.getSkull() ? ((SkullMeta) is.getItemMeta()).getOwningPlayer().getName()
                : null;
        String temp = null;
        if (extraData != null)
            temp = SkullHandler.getURL64(is);
        try {
            return MaterialStorage.getMS(is.getType(), is.getItemMeta().hasCustomModelData() ? is.getItemMeta().getCustomModelData() : 0,
                    variant,
                    is.getType() == MultiVersionLookup.getSkull() ? ((SkullMeta) is.getItemMeta()).getOwningPlayer().getName() : null,
                    temp);

        } catch (Error | Exception e4) {
        }
        return MaterialStorage.getMS(is.getType(), ((Damageable) is.getItemMeta()).getDamage(), variant,
                is.getType() == MultiVersionLookup.getSkull() ? ((SkullMeta) is.getItemMeta()).getOwningPlayer().getName() : null, temp);
    }

    public static int getVariant(final ItemStack is) {
        if (is != null)
            if (is.hasItemMeta() && is.getItemMeta().hasLore()) {
                for (final String lore : is.getItemMeta().getLore()) {
                    if (lore.startsWith(QAMain.S_ITEM_VARIANTS_NEW)) {
                        try {
                            final int id = Integer.parseInt(lore.split(QAMain.S_ITEM_VARIANTS_NEW)[1].trim());
                            return id;
                        } catch (Error | Exception e4) {
                            e4.printStackTrace();
                            return 0;
                        }
                    } else if (lore.startsWith(QAMain.S_ITEM_VARIANTS_LEGACY)) {
                        try {
                            final int id = Integer.parseInt(lore.split(QAMain.S_ITEM_VARIANTS_LEGACY)[1].trim());
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

    public int getData() { return this.d; }

    public boolean hasSpecialValue() { return this.specialValues != null; }

    public String getSpecialValue() { return this.specialValues; }

    public void setSpecialValue(final String s) { this.specialValues = s; }

    public boolean hasSpecialValue2() { return this.specialValues2 != null; }

    public String getSpecialValue2() { return this.specialValues2; }

    public void setSpecialValue2(final String s) { this.specialValues2 = s; }

    public Material getMat() { return this.m; }

    public boolean hasVariant() { return this.variant > 0; }

    public int getVariant() { return this.variant; }

    @Override
    public String toString() {
        return "MaterialStorage{" + "d=" + this.d + ", m=" + this.m + ", variant=" + this.variant + ", specialValues='" + this.specialValues
                + '\'' + ", specialValues2='" + this.specialValues2 + '\'' + '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || this.getClass() != o.getClass())
            return false;
        final MaterialStorage that = (MaterialStorage) o;
        return this.d == that.d && this.variant == that.variant && this.m == that.m
                && Objects.equals(this.specialValues, that.specialValues) && Objects.equals(this.specialValues2, that.specialValues2);
    }

    @Override
    public int hashCode() { return Objects.hash(this.d, this.m, this.variant, this.specialValues, this.specialValues2); }
}
