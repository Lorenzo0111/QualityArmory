package me.zombie_striker.qg.ammo;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import me.zombie_striker.customitemmanager.ArmoryBaseObject;
import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.customitemmanager.CustomItemManager;
import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.handlers.MultiVersionLookup;

public class Ammo extends CustomBaseObject implements ArmoryBaseObject {

    boolean indiDrop;

    int returnAmount;

    private double piercingDamage = 1;

    public static final String NO_SKIN_STRING = "Dont Use Skin";
    private String skullowner = null;
    private String base64SkinURL = Ammo.NO_SKIN_STRING;

    public Ammo(final String name, final MaterialStorage ms, final int maxAmount, final boolean indiDrop, final int returnamount,
            final double cost, final ItemStack[] ingredients, final double piercing) {
        this(name, ChatColor.GOLD + name, null, ms, maxAmount, indiDrop, returnamount, cost, ingredients, piercing);
    }

    public Ammo(final String name, final List<String> lore, final MaterialStorage ms, final int maxAmount, final boolean indiDrop,
            final int returnamount, final double cost, final ItemStack[] ingredients, final double piercing) {
        this(name, ChatColor.GOLD + name, lore, ms, maxAmount, indiDrop, returnamount, cost, ingredients, piercing);
    }

    public Ammo(final String name, final String displayName, final MaterialStorage ms, final int maxAmount, final boolean indiDrop,
            final int returnamount, final double cost, final ItemStack[] ingredients, final double piercing) {
        this(name, displayName, null, ms, maxAmount, indiDrop, returnamount, cost, ingredients, piercing);
    }

    public Ammo(final String name, final String displayName, final List<String> lore, final MaterialStorage ms, final int maxAmount,
            final boolean indiDrop, final int returnamount, final double cost, final ItemStack[] ingredients, final double piercing) {
        super(name, ms, displayName, lore, false);
        this.setMaxItemStack(maxAmount);
        this.indiDrop = indiDrop;
        super.setIngredients(ingredients);
        this.returnAmount = returnamount;

        this.setPrice(cost);

        this.piercingDamage = piercing;

        AmmoType.addAmmo(this, name);
    }

    public boolean hasCustomSkin() { return !Ammo.NO_SKIN_STRING.equals(this.base64SkinURL); }

    public String getCustomSkin() { return this.base64SkinURL; }

    public void setCustomSkin(final String skin) { this.base64SkinURL = skin; }

    public void setSkullOwner(final String s) { this.skullowner = s; }

    public boolean isSkull() { return this.skullowner != null; }

    public String getSkullOwner() { return this.skullowner; }

    @SuppressWarnings("deprecation")
    public OfflinePlayer getSkullPlayer() { return Bukkit.getServer().getOfflinePlayer(this.skullowner); }

    public double getPiercingDamage() { return this.piercingDamage; }

    public boolean individualDrop() { return this.indiDrop; }

    @Override
    public boolean is18Support() { return false; }

    @Override
    public void set18Supported(final boolean b) {}

    @Override
    public boolean onRMB(final Player e, final ItemStack usedItem) {
        QAMain.DEBUG("The item being click is ammo!");
        final Block b = e.getTargetBlock(null, 6);
        if (usedItem.getType() == Material.DIAMOND_HOE
                && (b.getType() == Material.DIRT || b.getType() == XMaterial.GRASS_BLOCK.parseMaterial()
                        || b.getType() == MultiVersionLookup.getGrassPath() || b.getType() == MultiVersionLookup.getMycil()))
            return true;
        return false;
    }

    @Override
    public boolean onShift(final Player shooter, final ItemStack usedItem, final boolean toggle) { return false; }

    @Override
    public boolean onLMB(final Player e, final ItemStack usedItem) { return false; }

    @Override
    public ItemStack getItemStack() {
        return CustomItemManager.getItemType("gun").getItem(this.getItemData().getMat(), this.getItemData().getData(),
                this.getItemData().getVariant());
    }

    @Override
    public boolean onSwapTo(final Player shooter, final ItemStack usedItem) {
        if (this.getSoundOnEquip() != null)
            shooter.getWorld().playSound(shooter.getLocation(), this.getSoundOnEquip(), 1, 1);
        return false;
    }

    @Override
    public boolean onSwapAway(final Player shooter, final ItemStack usedItem) { return false; }

}
