package me.zombie_striker.customitemmanager;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class CustomBaseObject {

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
    private Object[] craftingIngredients;
    private int craftingReturn = 1;

    private int maxItemStack = 1;

    public CustomBaseObject(String name, MaterialStorage storage, String displayname, List<String> lore, boolean hasAimAnimations) {
        this.name = name;
        this.base = storage;
        this.displayname = displayname;
        this.lore = lore;
        this.customAnimations = hasAimAnimations;
    }

    public String getName() {
        return name;
    }

    public MaterialStorage getItemData() {
        return base;
    }

    public List<String> getCustomLore() {
        if (lore == null) return new ArrayList<>();
        return new ArrayList<>(lore);
    }

    public void setCustomLore(List<String> lore) {
        this.lore = lore;
    }

    public String getDisplayName() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public void enableBetterAimingAnimations(boolean b) {
        this.customAnimations = b;
    }

    boolean hasBetterAimingAnimations() {
        return this.customAnimations;
    }

    public String getSoundOnEquip() {
        return soundOnEquip;
    }

    public String getSoundOnHit() {
        return soundOnHit;
    }

    public void setSoundOnHit(String sound) {
        this.soundOnHit = sound;
    }

    public void setSoundOnEquip(String sound) {
        this.soundOnEquip = sound;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isEnableShop() {
        return enableShop;
    }

    public boolean isEnableCrafting() {
        return enableCrafting;
    }

    public void setEnableShop(boolean enableShop) {
        this.enableShop = enableShop;
    }

    public void setEnableCrafting(boolean enableCrafting) {
        this.enableCrafting = enableCrafting;
    }

    public void setIngredients(ItemStack[] ing) {
        this.craftingIngredients = ing;
    }

    public void setIngredientsRaw(Object[] ing) {
        this.craftingIngredients = ing;
    }

    @Deprecated
    public ItemStack[] getIngredients() {
        return (ItemStack[]) craftingIngredients;
    }

    public Object[] getIngredientsRaw() {
        return craftingIngredients;
    }

    public int getCraftingReturn() {
        return craftingReturn;
    }

    public void setCraftingReturn(int amount) {
        this.craftingReturn = amount;
    }

    public int getMaxItemStack() {
        return maxItemStack;
    }

    public void setMaxItemStack(int amount) {
        maxItemStack = amount;
    }

    public UUID getUuid() {
        return uuid;
    }


    public abstract ItemStack getItemStack();

    public abstract boolean is18Support();

    public abstract void set18Supported(boolean b);

    public abstract boolean onRMB(Player shooter, ItemStack usedItem);

    public abstract boolean onShift(Player shooter, ItemStack usedItem, boolean toggle);

    public abstract boolean onLMB(Player shooter, ItemStack usedItem);

    public abstract boolean onSwapTo(Player shooter, ItemStack usedItem);

    public abstract boolean onSwapAway(Player shooter, ItemStack usedItem);
}