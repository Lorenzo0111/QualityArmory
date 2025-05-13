package me.zombie_striker.customitemmanager;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
		if(lore==null) return new ArrayList<>();
		return new ArrayList<>(lore);
	}

	public void setCustomLore(List<String> lore) {
		this.lore = lore;
	}

	public String getDisplayName() {
		return displayname;
	}

	public void setDisplayname(String displayname) {this.displayname = displayname;
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
		this.ing = ing;
	}
	public void setIngredientsRaw(Object[] ing) {
		this.ing = ing;
	}
	@Deprecated
	public ItemStack[] getIngredients(){
		return (ItemStack[]) ing;
	}
	public Object[] getIngredientsRaw(){
		return ing;
	}
	public int getCraftingReturn(){
		return craftingReturn;
	}
	public void setCraftingReturn(int amount){
		this.craftingReturn = amount;
	}
	public int getMaxItemStack(){
		return maxItemStack;
	}
	public void setMaxItemStack(int amount){
		maxItemStack = amount;
	}

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
	 * @param shooter the player using the item.
	 * @param usedItem the {@link ItemStack} being used.
	 * @return {@code true} if the action was successfully handled; {@code false} otherwise.
	 */
	public abstract boolean onRMB(Player shooter, ItemStack usedItem);

	/**
	 * Called when the player toggles shift.
	 *
	 * @param shooter the player using the item.
	 * @param usedItem the {@link ItemStack} being used.
	 * @param toggle a boolean that can be used to enable or disable a toggleable action/state.
	 * @return {@code true} if the action was successfully handled; {@code false} otherwise.
	 */
	public abstract boolean onShift(Player shooter, ItemStack usedItem, boolean toggle);

	/**
	 * Called when the player left-clicks (LMB).
	 *
	 * @param shooter the player using the item.
	 * @param usedItem the {@link ItemStack} being used.
	 * @return {@code true} if the action was successfully handled; {@code false} otherwise.
	 */
	public abstract boolean onLMB(Player shooter, ItemStack usedItem);

	/**
	 * Called when the player swaps to this item (e.g., changes to this item in hand).
	 *
	 * @param shooter the player equipping the item.
	 * @param usedItem the {@link ItemStack} being equipped.
	 * @return {@code true} if the action was successfully handled; {@code false} otherwise.
	 */
	public abstract boolean onSwapTo(Player shooter, ItemStack usedItem);

	/**
	 * Called when the player swaps away from this item (e.g., changes to a different item).
	 *
	 * @param shooter the player unequipping the item.
	 * @param usedItem the {@link ItemStack} being unequipped.
	 * @return {@code true} if the action was successfully handled; {@code false} otherwise.
	 */
	public abstract boolean onSwapAway(Player shooter, ItemStack usedItem);
}
