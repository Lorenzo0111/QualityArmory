package me.zombie_striker.customitemmanager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.inventory.ItemStack;

public class CustomBaseObject {

    private final UUID uuid = UUID.randomUUID();
    private final String name;
    private final MaterialStorage base;
    private List<String> lore;
    private String displayname;
    private boolean customAnimations;

    private String soundOnEquip;
    private String soundOnHit;

    private double price;
    private boolean enableShop = true;
    private boolean enableCrafting = true;

    Object[] ing = null;
    int craftingReturn = 1;

    public int maxItemStack = 1;

    public CustomBaseObject(final String name, final MaterialStorage storage, final String displayname, final List<String> lore,
            final boolean hasAimAnimations) {
        this.name = name;
        this.base = storage;
        this.displayname = displayname;
        this.lore = lore;
        this.customAnimations = hasAimAnimations;
    }

    public String getName() { return this.name; }

    public MaterialStorage getItemData() { return this.base; }

    public List<String> getCustomLore() {
        if (this.lore == null)
            return new ArrayList<>();
        return new ArrayList<>(this.lore);
    }

    public void setCustomLore(final List<String> lore) { this.lore = lore; }

    public String getDisplayName() { return this.displayname; }

    public void setDisplayname(final String displayname) { this.displayname = displayname; }

    public void enableBetterAimingAnimations(final boolean b) { this.customAnimations = b; }

    public boolean hasBetterAimingAnimations() { return this.customAnimations; }

    public String getSoundOnEquip() { return this.soundOnEquip; }

    public String getSoundOnHit() { return this.soundOnHit; }

    public void setSoundOnHit(final String sound) { this.soundOnHit = sound; }

    public void setSoundOnEquip(final String sound) { this.soundOnEquip = sound; }

    public double getPrice() { return this.price; }

    public void setPrice(final double price) { this.price = price; }

    public boolean isEnableShop() { return this.enableShop; }

    public boolean isEnableCrafting() { return this.enableCrafting; }

    public void setEnableShop(final boolean enableShop) { this.enableShop = enableShop; }

    public void setEnableCrafting(final boolean enableCrafting) { this.enableCrafting = enableCrafting; }

    public void setIngredients(final ItemStack[] ing) { this.ing = ing; }

    public void setIngredientsRaw(final Object[] ing) { this.ing = ing; }

    @Deprecated
    public ItemStack[] getIngredients() { return (ItemStack[]) this.ing; }

    public Object[] getIngredientsRaw() { return this.ing; }

    public int getCraftingReturn() { return this.craftingReturn; }

    public void setCraftingReturn(final int amount) { this.craftingReturn = amount; }

    public int getMaxItemStack() { return this.maxItemStack; }

    public void setMaxItemStack(final int amount) { this.maxItemStack = amount; }

    public UUID getUuid() { return this.uuid; }
}
