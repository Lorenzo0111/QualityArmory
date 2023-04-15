package me.zombie_striker.qualityarmory.miscitems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.customitemmanager.CustomItemManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.zombie_striker.customitemmanager.MaterialStorage;

public class MeleeItems extends CustomBaseObject implements ArmoryBaseObject{

	List<UUID> medkitHeartUsage = new ArrayList<>();
	HashMap<UUID, Long> lastTimeHealed = new HashMap<>();
	HashMap<UUID, Double> PercentTimeHealed = new HashMap<>();

	int damage = 1;

	public MeleeItems(MaterialStorage ms, String name, String displayname, ItemStack[] ings, int cost, int damage) {
		super(name,ms,displayname,null,false);
		this.setPrice(cost);
		super.setIngredients(ings);
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
		return CustomItemManager.getItemType("gun").getItem(this.getItemData().getMat(),this.getItemData().getData(),this.getItemData().getVariant());
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
