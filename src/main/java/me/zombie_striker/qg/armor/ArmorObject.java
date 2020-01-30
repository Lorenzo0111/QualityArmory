package me.zombie_striker.qg.armor;

import java.util.List;

import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.customitemmanager.CustomItemManager;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.zombie_striker.customitemmanager.ArmoryBaseObject;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.customitemmanager.MaterialStorage;

public class ArmorObject extends CustomBaseObject implements ArmoryBaseObject {

	// TODO: Refine max heights
	private double heightMin = 1;
	private double heightMax = 1.5;
	private double shiftingHeightOffset = -0.1;

	private boolean negateHeadshots = false;

	public ArmorObject(String name, String displayname, List<String> lore, ItemStack[] ing, MaterialStorage st,
			double cost) {
		super(name,st,displayname,lore,false);
		super.setIngredients(ing);
		this.setPrice(cost);
	}

	public void setNegateHeadshots(boolean b) {
		this.negateHeadshots = b;
	}

	public boolean getNegateHeadshots() {
		return negateHeadshots;
	}

	public double getMinH() {
		return heightMin;
	}

	public double getMaxH() {
		return heightMax;
	}

	public double getShifitngHeightOffset() {
		return shiftingHeightOffset;
	}

	@Override
	public boolean is18Support() {
		return false;
	}

	@Override
	public void set18Supported(boolean b) {
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onRMB(Player e, ItemStack usedItem) {
			QAMain.DEBUG("A Player is about to put on armor!");
			ItemStack helm = e.getInventory().getHelmet();
			e.setItemInHand(helm);
			e.getInventory().setHelmet(usedItem);
			try {
				e.getPlayer().playSound(e.getLocation(), Sound.ITEM_ARMOR_EQUIP_IRON, 2, 1);
			} catch (Error | Exception e3) {
			}
			return true;
		
	}

	@Override
	public boolean onLMB(Player e, ItemStack usedItem) {
		// TODO Auto-generated method stub
		return false;
		
	}
	@Override
	public ItemStack getItemStack() {
		return CustomItemManager.getItemFact("gun").getItem(this.getItemData(),1);
	}

}
