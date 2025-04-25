package me.zombie_striker.qg.ammo;

import java.util.List;

import com.cryptomorin.xseries.XMaterial;
import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.customitemmanager.CustomItemManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.qg.handlers.MultiVersionLookup;

public class Ammo extends CustomBaseObject {
    public static final String NO_SKIN_STRING = "Dont Use Skin";

    private final boolean individualDrop;
    private double piercingDamage = 1;

    private String skullOwner = null;
    private String base64SkinURL = NO_SKIN_STRING;

    public Ammo(String name, MaterialStorage ms, int maxAmount, boolean indiDrop, int returnamount,
                double cost, ItemStack[] ingredients, double piercing) {
        this(name, ChatColor.GOLD + name, null, ms, maxAmount, indiDrop, returnamount, cost, ingredients,
                piercing);
    }

    public Ammo(String name, List<String> lore, MaterialStorage ms, int maxAmount, boolean indiDrop,
                int returnamount, double cost, ItemStack[] ingredients, double piercing) {
        this(name, ChatColor.GOLD + name, lore, ms, maxAmount, indiDrop, returnamount, cost, ingredients,
                piercing);
    }

    public Ammo(String name, String displayName, MaterialStorage ms, int maxAmount, boolean indiDrop,
                int returnamount, double cost, ItemStack[] ingredients, double piercing) {
        this(name, displayName, null, ms, maxAmount, indiDrop, returnamount, cost, ingredients, piercing);
    }

    public Ammo(String name, String displayName, List<String> lore, MaterialStorage ms, int maxAmount,
                boolean indiDrop, int returnamount, double cost, ItemStack[] ingredients, double piercing) {
        super(name, ms, displayName, lore, false);

        setMaxItemStack(maxAmount);
        setIngredients(ingredients);
        setPrice(cost);
        setCraftingReturn(returnamount);

        this.individualDrop = indiDrop;
        this.piercingDamage = piercing;

        AmmoType.addAmmo(this, name);
    }

    public boolean hasCustomSkin() {
        return !NO_SKIN_STRING.equals(base64SkinURL);
    }

    public String getCustomSkin() {
        return base64SkinURL;
    }

    public void setCustomSkin(String skin) {
        this.base64SkinURL = skin;
    }

    public void setSkullOwner(String s) {
        skullOwner = s;
    }

    public boolean isSkull() {
        return skullOwner != null;
    }

    public String getSkullOwner() {
        return skullOwner;
    }

    public double getPiercingDamage() {
        return piercingDamage;
    }

    public boolean individualDrop() {
        return individualDrop;
    }

    @Override
    public boolean is18Support() {
        return false;
    }

    @Override
    public void set18Supported(boolean b) {
    }

    @Override
    public boolean onRMB(Player e, ItemStack usedItem) {
        QAMain.DEBUG("The item being click is ammo!");
        Block b = e.getTargetBlock(null, 6);
        return usedItem.getType() == Material.DIAMOND_HOE
                && (b.getType() == Material.DIRT
                || b.getType() == XMaterial.GRASS_BLOCK.parseMaterial()
                || b.getType() == MultiVersionLookup.getGrassPath()
                || b.getType() == MultiVersionLookup.getMycil());
    }

    @Override
    public boolean onShift(Player shooter, ItemStack usedItem, boolean toggle) {
        return false;
    }

    @Override
    public boolean onLMB(Player e, ItemStack usedItem) {
        return false;
    }

    @Override
    public ItemStack getItemStack() {
        return CustomItemManager.getItemType("gun").getItem(this.getItemData().getMat(), this.getItemData().getData(), this.getItemData().getVariant());
    }

    @Override
    public boolean onSwapTo(Player shooter, ItemStack usedItem) {
        if (getSoundOnEquip() != null)
            shooter.getWorld().playSound(shooter.getLocation(), getSoundOnEquip(), 1, 1);
        return false;
    }

    @Override
    public boolean onSwapAway(Player shooter, ItemStack usedItem) {
        return false;
    }
}