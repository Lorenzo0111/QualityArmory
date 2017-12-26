package me.zombie_striker.qg.ammo;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.zombie_striker.qg.ItemFact;

public class AmmoUtil {

	public static int getAmmoAmount(Player player, Ammo a) {
		int amount = 0;
		for (ItemStack is : player.getInventory().getContents()) {
			if (is != null && is.getType() == a.getItemData().getMat()) {
				if (is.getDurability() == a.getItemData().getData()) {
					amount += is.getAmount();
				}
			}
		}
		return amount;
	}

	public static boolean addAmmo(Player player, Ammo a, int amount) {
		int remaining = amount;
		for (int i = 0; i < player.getInventory().getSize(); i++) {
			ItemStack is = player.getInventory().getItem(i);
			if (is != null && is.getType() == a.getItemData().getMat()) {
				if (is.getDurability() == a.getItemData().getData()) {
					if (is.getAmount() + remaining <= a.getMaxAmount()) {
						is.setAmount(is.getAmount() + remaining);
						remaining = 0;
					} else {
						remaining -= a.getMaxAmount() - is.getAmount();
						is.setAmount(a.getMaxAmount());
					}
					player.getInventory().setItem(i, is);
					if (remaining <= 0)
						break;
				}
			}
		}
		if (remaining > 0) {
			if (player.getInventory().firstEmpty() >= 0) {
				ItemStack is = ItemFact.getAmmo(a);
				is.setAmount(remaining);
				player.getInventory().addItem(is);
				remaining = 0;
			}
		}
		return remaining <= 0;
	}

	public static boolean removeAmmo(Player player, Ammo a, int amount) {
		int remaining = amount;
		for (int i = 0; i < player.getInventory().getSize(); i++) {
			ItemStack is = player.getInventory().getItem(i);
			if (is != null && is.getType() == a.getItemData().getMat()) {
				if (is.getDurability() == a.getItemData().getData()) {
					int temp = is.getAmount();
					if (remaining < is.getAmount()) {
						is.setAmount(is.getAmount() - remaining);
					} else {
						is.setType(Material.AIR);
					}
					remaining -= temp;
					player.getInventory().setItem(i, is);
					if (remaining <= 0)
						break;
				}
			}
		}
		return remaining <= 0;
	}
}
