package me.zombie_striker.qg.armor;

import java.util.List;

import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.zombie_striker.customitemmanager.ArmoryBaseObject;
import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.customitemmanager.CustomItemManager;
import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.qg.QAMain;

public class ArmorObject extends CustomBaseObject implements ArmoryBaseObject {

    private int protection = 0;
    private double heightMin = 1;
    private double heightMax = 1.5;
    private final double shiftingHeightOffset = -0.1;

    private boolean negateHeadshots = false;

    public ArmorObject(final String name, final String displayname, final List<String> lore, final ItemStack[] ing,
            final MaterialStorage st, final double cost) {
        super(name, st, displayname, lore, false);
        super.setIngredients(ing);
        this.setPrice(cost);
    }

    public void setNegateHeadshots(final boolean b) { this.negateHeadshots = b; }

    public boolean getNegateHeadshots() { return this.negateHeadshots; }

    public double getMinH() { return this.heightMin; }

    public double getMaxH() { return this.heightMax; }

    public void setHeightMax(final double heightMax) { this.heightMax = heightMax; }

    public void setHeightMin(final double heightMin) { this.heightMin = heightMin; }

    public int getProtection() { return this.protection; }

    public void setProtection(final int protection) { this.protection = protection; }

    public double getShifitngHeightOffset() { return this.shiftingHeightOffset; }

    @Override
    public boolean is18Support() { return false; }

    @Override
    public void set18Supported(final boolean b) {}

    @SuppressWarnings("deprecation")
    @Override
    public boolean onRMB(final Player e, final ItemStack usedItem) {
        QAMain.DEBUG("A Player is about to put on armor!");
        final ItemStack helm = e.getInventory().getHelmet();
        e.setItemInHand(helm);
        e.getInventory().setHelmet(usedItem);
        try {
            e.getPlayer().playSound(e.getLocation(), Sound.ITEM_ARMOR_EQUIP_IRON, 2, 1);
        } catch (Error | Exception e3) {
        }
        return true;

    }

    @Override
    public boolean onShift(final Player shooter, final ItemStack usedItem, final boolean toggle) { return false; }

    @Override
    public boolean onLMB(final Player e, final ItemStack usedItem) {
        // TODO Auto-generated method stub
        return false;

    }

    @Override
    public ItemStack getItemStack() {
        final ItemStack item = CustomItemManager.getItemType("gun").getItem(this.getItemData().getMat(), this.getItemData().getData(),
                this.getItemData().getVariant());
        final ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null && this.protection != 0) {
            itemMeta.addAttributeModifier(Attribute.GENERIC_ARMOR,
                    new AttributeModifier("generic.armor", this.protection, AttributeModifier.Operation.ADD_NUMBER));
            item.setItemMeta(itemMeta);
        }

        return item;
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
