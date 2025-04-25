package me.zombie_striker.qg.miscitems;

import java.util.*;

import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.customitemmanager.CustomItemManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.zombie_striker.customitemmanager.MaterialStorage;

public class MeleeItems extends CustomBaseObject {

    private final List<UUID> medkitHeartUsage = new ArrayList<>();
    private final Map<UUID, Long> lastTimeHealed = new HashMap<>();
    private final Map<UUID, Double> PercentTimeHealed = new HashMap<>();

    private int damage = 1;

    public MeleeItems(MaterialStorage ms, String name, String displayname, ItemStack[] ingredients, int cost, int damage) {
        super(name, ms, displayname, null, false);

        setPrice(cost);
        setIngredients(ingredients);

        this.damage = damage;
    }

    @Override
    public int getCraftingReturn() {
        return 1;
    }

    public int getDamage() {
        return damage;
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