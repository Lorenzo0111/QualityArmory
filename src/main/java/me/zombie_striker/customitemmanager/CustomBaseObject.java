package me.zombie_striker.customitemmanager;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Abstract base class for custom items/objects in the CustomItemManager system.
 * Provides core functionality for custom items including identification,
 * display properties,
 * sounds, shop/crafting settings, and interaction handlers.
 */
public abstract class CustomBaseObject {

    private final UUID uuid = UUID.randomUUID();
    private String name;
    private MaterialStorage base;
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

    /**
     * Constructs a new CustomBaseObject with the specified properties.
     *
     * @param name             The internal name/ID of the custom object
     * @param storage          The material storage data
     * @param displayname      The display name shown in-game
     * @param lore             The item's lore text
     * @param hasAimAnimations Whether this item uses custom aim animations
     */
    public CustomBaseObject(String name, MaterialStorage storage, String displayname, List<String> lore,
                            boolean hasAimAnimations) {
        this.name = name;
        this.base = storage;
        this.displayname = displayname;
        this.lore = lore;
        this.customAnimations = hasAimAnimations;
    }

    /**
     * Gets the internal name/ID of this custom object.
     *
     * @return The name of this custom object
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the material storage data for this item.
     *
     * @return The MaterialStorage object containing item data
     */
    public MaterialStorage getItemData() {
        return base;
    }

    /**
     * Gets a copy of this item's lore text.
     *
     * @return A new ArrayList containing the lore text
     */
    public List<String> getCustomLore() {
        if (lore == null)
            return new ArrayList<>();
        return new ArrayList<>(lore);
    }

    /**
     * Sets the lore text for this item.
     *
     * @param lore The new lore text
     */
    public void setCustomLore(List<String> lore) {
        this.lore = lore;
    }

    /**
     * Gets the display name shown in-game.
     *
     * @return The item's display name
     */
    public String getDisplayName() {
        return displayname;
    }

    /**
     * Sets the display name shown in-game.
     *
     * @param displayName The new display name
     */
    public void setDisplayname(String displayName) {
        this.displayname = displayName;
    }

    /**
     * Enables or disables custom aiming animations.
     *
     * @param enable True to enable, false to disable
     */
    public void enableBetterAimingAnimations(boolean enable) {
        this.customAnimations = enable;
    }

    /**
     * Checks if custom aiming animations are enabled.
     *
     * @return True if enabled, false if disabled
     */
    boolean hasBetterAimingAnimations() {
        return this.customAnimations;
    }

    /**
     * Gets the sound played when equipping this item.
     *
     * @return The equip sound string
     */
    public String getSoundOnEquip() {
        return soundOnEquip;
    }

    /**
     * Gets the sound played when hitting with this item.
     *
     * @return The hit sound string
     */
    public String getSoundOnHit() {
        return soundOnHit;
    }

    /**
     * Sets the sound played when hitting with this item.
     *
     * @param sound The sound string to play
     */
    public void setSoundOnHit(String sound) {
        this.soundOnHit = sound;
    }

    /**
     * Sets the sound played when equipping this item.
     *
     * @param sound The sound string to play
     */
    public void setSoundOnEquip(String sound) {
        this.soundOnEquip = sound;
    }

    /**
     * Gets the shop price of this item.
     *
     * @return The item's price
     */
    public double getPrice() {
        return price;
    }

    /**
     * Sets the shop price of this item.
     *
     * @param price The new price
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Checks if this item can be purchased in the shop.
     *
     * @return True if purchasable, false if not
     */
    public boolean isEnableShop() {
        return enableShop;
    }

    /**
     * Checks if this item can be crafted.
     *
     * @return True if craftable, false if not
     */
    public boolean isEnableCrafting() {
        return enableCrafting;
    }

    /**
     * Sets whether this item can be purchased in the shop.
     *
     * @param enableShop True to enable shop purchases, false to disable
     */
    public void setEnableShop(boolean enableShop) {
        this.enableShop = enableShop;
    }

    /**
     * Sets whether this item can be crafted.
     *
     * @param enableCrafting True to enable crafting, false to disable
     */
    public void setEnableCrafting(boolean enableCrafting) {
        this.enableCrafting = enableCrafting;
    }

    /**
     * Sets the crafting ingredients for this item.
     *
     * @param ing Array of ItemStacks used as ingredients
     */
    public void setIngredients(ItemStack[] ing) {
        this.ing = ing;
    }

    /**
     * Sets the raw crafting ingredients for this item.
     *
     * @param ing Array of Objects used as ingredients
     * @see CustomBaseObject#getIngredientsRaw()
     */
    public void setIngredientsRaw(Object[] ing) {
        this.ing = ing;
    }

    /**
     * Gets the crafting ingredients.
     *
     * @return Array of ItemStacks used as ingredients
     * @deprecated Use {@link #getIngredientsRaw()} instead.
     */
    @Deprecated
    public ItemStack[] getIngredients() {
        if (ing == null)
            return new ItemStack[0];

        ItemStack[] stacks = new ItemStack[ing.length];

        for (int i = 0; i < ing.length; i++) {
            if (ing[i] instanceof ItemStack) {
                stacks[i] = (ItemStack) ing[i];
            } else {
                stacks[i] = null;
            }
        }

        return stacks;
    }

    /**
     * Gets the raw crafting ingredients.
     *
     * @return Array of Object ingredients. This can be either an {@link ItemStack} or a {@link String} for custom items.
     */
    public Object[] getIngredientsRaw() {
        return ing;
    }

    /**
     * Gets the number of items returned from crafting.
     *
     * @return The crafting return amount
     */
    public int getCraftingReturn() {
        return craftingReturn;
    }

    /**
     * Sets the number of items returned from crafting.
     *
     * @param amount The new crafting return amount
     */
    public void setCraftingReturn(int amount) {
        this.craftingReturn = amount;
    }

    /**
     * Gets the maximum stack size for this item.
     *
     * @return The maximum stack size
     */
    public int getMaxItemStack() {
        return maxItemStack;
    }

    /**
     * Sets the maximum stack size for this item.
     *
     * @param amount The new maximum stack size
     */
    public void setMaxItemStack(int amount) {
        maxItemStack = amount;
    }

    /**
     * Returns the UUID of this custom object.
     *
     * @return the UUID of this object.
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Returns the {@link ItemStack} associated with this custom object.
     *
     * @return the {@code ItemStack} representing this object.
     */
    public abstract ItemStack getItemStack();

    /**
     * Checks whether this object supports Minecraft 1.8 mechanics or compatibility.
     *
     * @return {@code true} if 1.8 support is enabled; {@code false} otherwise.
     */
    public abstract boolean is18Support();

    /**
     * Sets whether this object supports Minecraft 1.8 mechanics or compatibility.
     *
     * @param b {@code true} to enable 1.8 support; {@code false} to disable it.
     */
    public abstract void set18Supported(boolean b);

    /**
     * Called when the player right-clicks (RMB).
     *
     * @param shooter  the player using the item.
     * @param usedItem the {@link ItemStack} being used.
     * @return {@code true} if the action was successfully handled; {@code false}
     *         otherwise.
     */
    public abstract boolean onRMB(Player shooter, ItemStack usedItem);

    /**
     * Called when the player toggles shift.
     *
     * @param shooter  the player using the item.
     * @param usedItem the {@link ItemStack} being used.
     * @param toggle   a boolean that can be used to enable or disable a toggleable
     *                 action/state.
     * @return {@code true} if the action was successfully handled; {@code false}
     *         otherwise.
     */
    public abstract boolean onShift(Player shooter, ItemStack usedItem, boolean toggle);

    /**
     * Called when the player left-clicks (LMB).
     *
     * @param shooter  the player using the item.
     * @param usedItem the {@link ItemStack} being used.
     * @return {@code true} if the action was successfully handled; {@code false}
     *         otherwise.
     */
    public abstract boolean onLMB(Player shooter, ItemStack usedItem);

    /**
     * Called when the player swaps to this item (e.g., changes to this item in
     * hand).
     *
     * @param shooter  the player equipping the item.
     * @param usedItem the {@link ItemStack} being equipped.
     * @return {@code true} if the action was successfully handled; {@code false}
     *         otherwise.
     */
    public abstract boolean onSwapTo(Player shooter, ItemStack usedItem);

    /**
     * Called when the player swaps away from this item (e.g., changes to a
     * different item).
     *
     * @param shooter  the player unequipping the item.
     * @param usedItem the {@link ItemStack} being unequipped.
     * @return {@code true} if the action was successfully handled; {@code false}
     *         otherwise.
     */
    public abstract boolean onSwapAway(Player shooter, ItemStack usedItem);
}
