package me.zombie_striker.qg.armor;

import java.util.List;

import me.zombie_striker.customitemmanager.CustomItemManager;
import org.bukkit.Sound;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.zombie_striker.qg.ArmoryBaseObject;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.customitemmanager.MaterialStorage;

public class ArmorObject implements ArmoryBaseObject {

	private String displayname;
	private String name;
	private ItemStack[] ing;
	private MaterialStorage storage;
	private List<String> lore;
	private double cost;

	// TODO: Refine max heights
	private double heightMin = 1;
	private double heightMax = 1.5;
	private double shiftingHeightOffset = -0.1;

	private boolean negateHeadshots = false;

	public ArmorObject(String name, String displayname, List<String> lore, ItemStack[] ing, MaterialStorage st,
			double cost) {
		this.name = name;
		this.displayname = displayname;
		this.lore = lore;
		this.ing = ing;
		this.storage = st;
		this.cost = cost;
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
	public String getName() {
		return name;
	}

	@Override
	public ItemStack[] getIngredients() {
		return ing;
	}

	@Override
	public int getCraftingReturn() {
		return 1;
	}

	@Override
	public MaterialStorage getItemData() {
		return storage;
	}

	@Override
	public List<String> getCustomLore() {
		return lore;
	}

	@Override
	public String getDisplayName() {
		return displayname;
	}

	@Override
	public double cost() {
		return cost;
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
	public void onRMB(PlayerInteractEvent e, ItemStack usedItem) {
			QAMain.DEBUG("A Player is about to put on armor!");
			ItemStack helm = e.getPlayer().getInventory().getHelmet();
			e.getPlayer().setItemInHand(helm);
			e.getPlayer().getInventory().setHelmet(usedItem);
			try {
				e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ITEM_ARMOR_EQUIP_IRON, 2, 1);
			} catch (Error | Exception e3) {
			}
			e.setCancelled(true);
			return;
		
	}

	@Override
	public void onLMB(PlayerInteractEvent e, ItemStack usedItem) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public ItemStack getItemStack() {
		return CustomItemManager.getItemFact("gun").getItem(this.getItemData(),1);
	}

}
