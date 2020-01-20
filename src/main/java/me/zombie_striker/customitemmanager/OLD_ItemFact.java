package me.zombie_striker.customitemmanager;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.armor.ArmorObject;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.handlers.IronsightsHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class OLD_ItemFact {

	public static List<String> getArmorLore(ArmorObject a) {
		List<String> lore = new ArrayList<>();
		lore.add(QAMain.S_HELMET_RMB);
		return lore;
	}

	public static void addOutOfAmmoToDisplayname(final Gun g, final Player player, ItemStack is, final int slot) {
		final ItemStack k = is;
		ItemMeta im = k.getItemMeta();
		im.setDisplayName(g.getDisplayName() + QAMain.S_OUT_OF_AMMO);
		k.setItemMeta(im);
		player.getInventory().setItem(slot, k);
		new BukkitRunnable() {
			public void run() {
				removeOutOfAmmoToDisplayname(g, player, k, slot);
			}
		}.runTaskLater(QAMain.getInstance(), 20 * 3);
	}

	public static void removeOutOfAmmoToDisplayname(final Gun g, final Player player, ItemStack is, final int slot) {
		ItemStack temp = player.getInventory().getItem(slot);
		if (temp != null && temp.isSimilar(is)) {
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(g.getDisplayName());
			is.setItemMeta(im);
			player.getInventory().setItem(slot, is);
		}
	}


	public static List<String> addShopLore(CustomBaseObject obj) {
		List<String> lore = new ArrayList<>();
		if ((obj).getCraftingReturn() > 1)
			lore.add(ChatColor.DARK_RED + QAMain.S_ITEM_RETURNS + " " + (obj).getCraftingReturn());
		lore.add(QAMain.S_ITEM_COST + (obj.getPrice()));
		return lore;
	}

	@SuppressWarnings("deprecation")
	public static List<String> getCraftingLore(CustomBaseObject a) {
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.RED + QAMain.S_ITEM_ING + ": ");
		for (ItemStack is : (a).getIngredients()) {
			StringBuilder sb = new StringBuilder();
			// Chris: itemName from message.yml
			String itemName = is.getType().name();
			sb.append(ChatColor.RED + "- " + QAMain.findCraftEntityName(itemName, itemName) + " x " + is.getAmount());
			if (is.getDurability() != 0)
				sb.append(":" + is.getDurability());
			lore.add(sb.toString());
		}
		if ((a).getCraftingReturn() > 1) {
			lore.add(ChatColor.DARK_RED + QAMain.S_ITEM_CRAFTS + " " + (a).getCraftingReturn());

		}
		return lore;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getIronSights() {

		ItemStack ironsights = null;
		ItemMeta im = null;
		try {
			ironsights = new ItemStack(IronsightsHandler.ironsightsMaterial);
			im = ironsights.getItemMeta();
			im.setCustomModelData(IronsightsHandler.ironsightsData);
		} catch (Error | Exception e4) {
			ironsights = new ItemStack(IronsightsHandler.ironsightsMaterial, 1, (short) IronsightsHandler.ironsightsData);
			im = ironsights.getItemMeta();
		}
		im.setDisplayName(IronsightsHandler.ironsightsDisplay);
		if (QAMain.ITEM_enableUnbreakable) {
			try {
				im.setUnbreakable(true);
			} catch (Error | Exception e3423) {			}
		}
		try {
			if (QAMain.ITEM_enableUnbreakable) {
				im.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_UNBREAKABLE);
			}
			im.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES);
			im.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_DESTROYS);
		} catch (Error | Exception e34) {
		}
		ironsights.setItemMeta(im);
		return ironsights;
	}

	public static boolean sameGun(ItemStack is1, ItemStack is2) {
		return false;
	}

	public static void addVariantData(ItemMeta im, List<String> lore, CustomBaseObject object) {
		if (object.getItemData().hasVariant())
			addVariantData(im, lore, object.getItemData().getVariant());
	}

	public static void addVariantData(ItemMeta im, List<String> lore, int var) {
		/*try {
			if(im!=null)
				im.setCustomModelData(var);
		} catch (Error | Exception e4) {*/
			boolean b = false;
			if(lore == null){
				b=  true;
				lore = new ArrayList<>();
			}
			lore.add(QAMain.S_ITEM_VARIANTS_NEW + " " + var);
			if(b)
				if(im!=null)
				im.setLore(lore);
		}
	//}


}
