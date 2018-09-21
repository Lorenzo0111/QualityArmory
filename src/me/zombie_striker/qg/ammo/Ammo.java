package me.zombie_striker.qg.ammo;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.zombie_striker.qg.ArmoryBaseObject;
import me.zombie_striker.qg.ItemFact;
import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.MaterialStorage;
import me.zombie_striker.qg.handlers.MultiVersionLookup;

public class Ammo implements ArmoryBaseObject{


	String name;
	String disName;
	int maxAmount;
	boolean indiDrop;

	MaterialStorage ms;

	int returnAmount;

	ItemStack[] ingredients;

	List<String> lore;

	private double cost;

	private double piercingDamage = 1;

	public static final String NO_SKIN_STRING = "Dont Use Skin";
	private String skullowner = null;
	private String base64SkinURL = NO_SKIN_STRING;

	public Ammo(String name, MaterialStorage ms, int maxAmount, boolean indiDrop, int returnamount,
			double cost, ItemStack[] ingredients, double piercing) {
		this(name, ChatColor.GOLD + name, null, ms, maxAmount, indiDrop, returnamount, cost, ingredients,
				piercing);
	}

	public Ammo(String name, List<String> lore, MaterialStorage ms, int maxAmount, boolean indiDrop,
			int returnamount, double cost, ItemStack[] ingredients, double piercing) {
		this(name, ChatColor.GOLD + name, lore, ms, maxAmount, indiDrop, returnamount, cost, ingredients,
				piercing);
	}

	public Ammo(String name, String displayName,MaterialStorage ms, int maxAmount, boolean indiDrop,
			int returnamount, double cost, ItemStack[] ingredients, double piercing) {
		this(name, displayName, null, ms, maxAmount, indiDrop, returnamount, cost, ingredients, piercing);
	}

	public Ammo(String name, String displayName, List<String> lore, MaterialStorage ms, int maxAmount,
			boolean indiDrop, int returnamount, double cost, ItemStack[] ingredients, double piercing) {
		this.name = name;
		this.disName = displayName;
		this.ms = ms;
		this.maxAmount = maxAmount;
		this.indiDrop = indiDrop;
		this.ingredients = ingredients;
		this.returnAmount = returnamount;
		this.lore = lore;

		this.cost = cost;

		this.piercingDamage = piercing;

		AmmoType.addAmmo(this, name);
	}
	
	public void setCraftingReturn(int i) {
		returnAmount = i;
	}
	
	public boolean hasCustomSkin() {
		return ! NO_SKIN_STRING.equals(base64SkinURL);
	}
	public String getCustomSkin() {
		return base64SkinURL;
	}
	public void setCustomSkin(String skin) {
		this.base64SkinURL = skin;
	}
	
	public void setSkullOwner(String s) {
		skullowner = s;
	}
	
	public boolean isSkull() {
		return skullowner!=null;
	}
	public String getSkullOwner() {
		return skullowner;
	}

	public double getPiercingDamage() {
		return piercingDamage;
	}

	@Override
	public double cost() {
		return cost;
	}

	@Override
	public String getName() {
		return name;
	}

	public int getMaxAmount() {
		return maxAmount;
	}

	public boolean individualDrop() {
		return indiDrop;
	}

	@Override
	public MaterialStorage getItemData() {
		return ms;
	}

	@Override
	public int getCraftingReturn() {
		return returnAmount;
	}

	@Override
	public ItemStack[] getIngredients() {
		return ingredients;
	}

	@Override
	public List<String> getCustomLore() {
		return lore;
	}

	@Override
	public String getDisplayName() {
		return disName;
	}
	@Override
	public boolean is18Support() {
		return false;
	}

	@Override
	public void set18Supported(boolean b) {		
	}
	

	@Override
	public void onRMB(PlayerInteractEvent e, ItemStack usedItem) {
		Main.DEBUG("The item being click is ammo!");
		if (usedItem.getType() == Material.DIAMOND_HOE && e.getAction() == Action.RIGHT_CLICK_BLOCK
				&& (e.getClickedBlock().getType() == Material.DIRT
						|| e.getClickedBlock().getType() == Material.GRASS
						|| e.getClickedBlock().getType() == Material.GRASS_PATH
						|| e.getClickedBlock().getType() == MultiVersionLookup.getMycil()))
			e.setCancelled(true);
	}

	@Override
	public void onLMB(PlayerInteractEvent e, ItemStack usedItem) {
		
	}
	@Override
	public ItemStack getItemStack() {
		return ItemFact.getAmmo(this,1);
	}
}
