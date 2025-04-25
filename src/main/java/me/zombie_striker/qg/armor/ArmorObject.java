package me.zombie_striker.qg.armor;

import java.util.List;

import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.customitemmanager.CustomItemManager;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.zombie_striker.customitemmanager.ArmoryBaseObject;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.customitemmanager.MaterialStorage;
import org.bukkit.inventory.meta.ItemMeta;

public class ArmorObject extends CustomBaseObject implements ArmoryBaseObject {
    private static final double shiftingHeightOffset = -0.1;

    private int protection = 0;
    private double heightMin = 1;
    private double heightMax = 1.5;

    private boolean negateHeadshots = false;

    public ArmorObject(String name, String displayname, List<String> lore, ItemStack[] ingredients, MaterialStorage ms,
                       double cost) {
        super(name, ms, displayname, lore, false);

        setIngredients(ingredients);
        setPrice(cost);
    }

    public void setNegateHeadshots(boolean b) {
        this.negateHeadshots = b;
    }

    public boolean getNegateHeadshots() {
        return negateHeadshots;
    }

    public double getMinH() {
        return heightMin;
    }

    public double getMaxH() {
        return heightMax;
    }

    public void setHeightMax(double heightMax) {
        this.heightMax = heightMax;
    }

    public void setHeightMin(double heightMin) {
        this.heightMin = heightMin;
    }

    public int getProtection() {
        return protection;
    }

    public void setProtection(int protection) {
        this.protection = protection;
    }

    public double getShiftingHeightOffset() {
        return shiftingHeightOffset;
    }

    @Override
    public boolean is18Support() {
        return false;
    }

    @Override
    public void set18Supported(boolean b) {
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onRMB(Player e, ItemStack usedItem) {
        QAMain.DEBUG("A Player is about to put on armor!");
        ItemStack helm = e.getInventory().getHelmet();
        e.setItemInHand(helm);
        e.getInventory().setHelmet(usedItem);
        try {
            e.getPlayer().playSound(e.getLocation(), Sound.ITEM_ARMOR_EQUIP_IRON, 2, 1);
        } catch (Error | Exception e3) {
        }
        return true;

    }

    @Override
    public boolean onShift(Player shooter, ItemStack usedItem, boolean toggle) {
        return false;
    }

    @Override
    public boolean onLMB(Player e, ItemStack usedItem) {
        // TODO Auto-generated method stub
        return false;

    }

    @Override
    public ItemStack getItemStack() {
        ItemStack item = CustomItemManager.getItemType("gun").getItem(this.getItemData().getMat(), this.getItemData().getData(), this.getItemData().getVariant());
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null && protection != 0) {
            itemMeta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier("generic.armor", protection, AttributeModifier.Operation.ADD_NUMBER));
            item.setItemMeta(itemMeta);
        }

        return item;
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