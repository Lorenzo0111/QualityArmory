package me.zombie_striker.customitemmanager;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CustomBaseObject {

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

	public void setEnableShop(boolean enableShop) {
		this.enableShop = enableShop;
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
}
