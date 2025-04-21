package me.zombie_striker.qg.miscitems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.customitemmanager.CustomItemManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.zombie_striker.customitemmanager.ArmoryBaseObject;
import me.zombie_striker.customitemmanager.MaterialStorage;

public class MeleeItems extends CustomBaseObject implements ArmoryBaseObject {

    List<UUID> medkitHeartUsage = new ArrayList<>();
    HashMap<UUID, Long> lastTimeHealed = new HashMap<>();
    HashMap<UUID, Double> PercentTimeHealed = new HashMap<>();

    int damage = 1;

    public MeleeItems(final MaterialStorage ms, final String name, final String displayname, final ItemStack[] ings, final int cost,
            final int damage) {
        super(name, ms, displayname, null, false);
        this.setPrice(cost);
        super.setIngredients(ings);
        this.damage = damage;
    }

    @Override
    public int getCraftingReturn() { return 1; }

    public int getDamage() { return this.damage; }

    @Override
    public boolean is18Support() { return false; }

    @Override
    public void set18Supported(final boolean b) {}

    @Override
    public boolean onRMB(final Player e, final ItemStack usedItem) { return true; }

    @Override
    public boolean onShift(final Player shooter, final ItemStack usedItem, final boolean toggle) { return false; }

    @Override
    public boolean onLMB(final Player e, final ItemStack usedItem) {
        // TODO Auto-generated method stub
        return false;
    }

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
