package me.zombie_striker.qg;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.zombie_striker.qg.ammo.Ammo;
import me.zombie_striker.qg.guns.Gun;

public class ItemFact {

	public static List<String> getGunLore(Gun g, ItemStack current, int amount) {
		List<String> lore = new ArrayList<>();
		if (g.getCustomLore() != null)
			lore.addAll(g.getCustomLore());
		lore.add(ChatColor.GREEN + Main.S_ITEM_BULLETS + ": " + (amount - 1) + "/" + (g.getMaxBullets() - 1));
		lore.add(ChatColor.GREEN + Main.S_ITEM_DAMAGE + ": " + g.getDamage());
		if (Main.enableDurability)
			if (current == null) {
				lore.add(ChatColor.DARK_GREEN + Main.S_ITEM_DURIB + ":" + g.getDurability() + "/" + g.getDurability());
			} else {
				lore = setDamage(g, lore, getDamage(current));
			}

		lore.add(ChatColor.GRAY + Main.S_ITEM_AMMO + ": " + g.getAmmoType().getName());

		if (g.isAutomatic()) {
			lore.add(ChatColor.DARK_GRAY + "[LMB] to use Single-fire");
			lore.add(ChatColor.DARK_GRAY + "[RMB] to reload");
			lore.add(ChatColor.DARK_GRAY + "[Sneak]+[RMB] to use Automatic-firing");
		} else {
			lore.add(ChatColor.DARK_GRAY + "[LMB] to use Single-fire");
			lore.add(ChatColor.DARK_GRAY + (Main.enableIronSightsON_RIGHT_CLICK ? "[DropItem]" : "[RMB]")
					+ " to reload");
			if (g.hasIronSights())
				lore.add(ChatColor.DARK_GRAY + (Main.enableIronSightsON_RIGHT_CLICK ? "[RMB]" : "[Sneak]")
						+ " to open ironsights");
		}

		if (current != null && current.getItemMeta().hasLore())
			for (String s : current.getItemMeta().getLore()) {
				if (ChatColor.stripColor(s).contains("UUID")) {
					lore.add(s);
					break;
				}
			}
		return lore;
	}

	public static List<String> getCraftingGunLore(Gun g) {
		List<String> lore = getGunLore(g, null, 1);
		lore.addAll(getCraftingLore(g));
		return lore;
	}

	public static List<String> getCraftingLore(ArmoryBaseObject a) {
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.RED + Main.S_ITEM_ING + ": ");
		for (ItemStack is : a.getIngredients()) {
			StringBuilder sb = new StringBuilder();
			sb.append(ChatColor.RED + "-" + is.getAmount() + " " + is.getType().name());
			if (is.getDurability() != 0)
				sb.append(":" + is.getDurability());
			lore.add(sb.toString());
		}
		if (a.getCraftingReturn() > 1) {
			lore.add(ChatColor.DARK_RED + "Crafts " + a.getCraftingReturn());
		}
		return lore;
	}

	public static ItemStack getGun(int durib) {
		Gun g = Main.gunRegister.get(MaterialStorage.getMS(Main.guntype, durib));
		return getGun(g);
	}

	public static ItemStack getGun(MaterialStorage durib) {
		Gun g = Main.gunRegister.get(durib);
		return getGun(g);
	}

	public static ItemStack getGun(Gun g) {
		ItemStack is = new ItemStack(g.getItemData().getMat(), 0, (short) g.getItemData().getData());
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(g.getDisplayName());
		List<String> lore = getGunLore(g, null, g.getMaxBullets());
		im.setLore(lore);
		try {
			im.setUnbreakable(true);
			im.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_UNBREAKABLE);
		} catch (Error e) {
		}

		is.setItemMeta(im);
		is.setAmount(g.getMaxBullets());

		is = addGunRegister(is);
		return is;
	}

	public static ItemStack getAmmo(Material m, int data) {
		if (Main.ammoRegister.containsKey(MaterialStorage.getMS(m, data)))
			return getAmmo(Main.ammoRegister.get(MaterialStorage.getMS(m, data)));
		return null;
	}

	public static ItemStack getAmmo(Ammo a) {
		ItemStack is = new ItemStack(a.getItemData().getMat(), 0, (short) a.getItemData().getData());
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(a.getDisplayName());
		try {
			im.setUnbreakable(true);
			im.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_UNBREAKABLE);
		} catch (Error e) {
		}

		is.setItemMeta(im);
		is.setAmount(a.getMaxAmount());

		return is;
	}

	public static ItemStack getObject(ArmoryBaseObject a) {
		ItemStack is = new ItemStack(a.getItemData().getMat(), 0, (short) a.getItemData().getData());
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(a.getDisplayName());
		try {
			im.setUnbreakable(true);
			im.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_UNBREAKABLE);
		} catch (Error e) {
		}
		im.setLore(a.getCustomLore());

		is.setItemMeta(im);
		is.setAmount(a.getCraftingReturn());

		return is;
	}

	public static int getDamage(ItemStack is) {
		for (String lore1 : is.getItemMeta().getLore()) {
			String lore = ChatColor.stripColor(lore1);
			if (ChatColor.stripColor(lore).startsWith(Main.S_ITEM_DURIB + ":")) {
				return Integer.parseInt(lore.split(":")[1].split("/")[0]);
			}
		}
		return -1;
	}

	public static ItemStack damage(Gun g, ItemStack is) {
		return setDamage(g, is, getDamage(is) - 1);
	}

	public static ItemStack setDamage(Gun g, ItemStack is, int damage) {
		ItemMeta im = is.getItemMeta();
		im.setLore(setDamage(g, im.getLore(), damage));
		is.setItemMeta(im);
		return is;
	}

	public static List<String> setDamage(Gun g, List<String> lore, int damage) {
		boolean foundLine = false;
		double k = ((double) damage) / g.getDurability();
		ChatColor c = k > 0.5 ? ChatColor.DARK_GREEN : k > 0.25 ? ChatColor.GOLD : ChatColor.DARK_RED;
		for (int j = 0; j < lore.size(); j++) {
			if (ChatColor.stripColor(lore.get(j)).startsWith(Main.S_ITEM_DURIB)) {
				lore.set(j, c + Main.S_ITEM_DURIB + ":" + damage + "/" + g.getDurability());
				foundLine = true;
				break;
			}
		}
		if (!foundLine) {
			lore.add(c + Main.S_ITEM_DURIB + ":" + damage + "/" + g.getDurability());
		}
		return lore;
	}

	public static ItemStack addGunRegister(ItemStack base) {
		ItemMeta im = base.getItemMeta();
		try {
			im.setLocalizedName("" + UUID.randomUUID());
		} catch (Exception | Error e) {
			List<String> lore = im.getLore();
			lore.add(ChatColor.DARK_GRAY + "UUID" + UUID.randomUUID().toString());
			im.setLore(lore);
		}
		base.setItemMeta(im);
		return base;
	}

	public static boolean sameGun(ItemStack is1, ItemStack is2) {
		try {
			if (is1.hasItemMeta() && is1.getItemMeta().hasLocalizedName())
				if (is2.hasItemMeta() && is2.getItemMeta().hasLocalizedName())
					return is1.getItemMeta().getLocalizedName().equals(is2.getItemMeta().getLocalizedName());
		} catch (Exception | Error e1) {
			if (is1.hasItemMeta() && is1.getItemMeta().hasLore())
				if (is2.hasItemMeta() && is2.getItemMeta().hasLore())
					for (String s : is1.getItemMeta().getLore())
						if (s.contains("UUID"))
							return is2.getItemMeta().getLore().contains(s);
		}
		return false;
	}

	public static int getAmount(ItemStack is) {
		if (is != null) {
			if (Main.enableVisibleAmounts) {
				return is.getAmount() - 1;
			} else {
				for (String lore : is.getItemMeta().getLore()) {
					if (lore.contains(Main.S_ITEM_BULLETS)) {
						return Integer.parseInt(lore.split(":")[1].split("/")[0].trim());
					}
				}
				return 0;
			}
		}
		return 0;
	}

}
