package me.zombie_striker.qg.guns.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.zombie_striker.qg.ItemFact;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.handlers.Update19OffhandChecker;

public class GunRefillerRunnable {

	private static List<GunRefillerRunnable> allGunRefillers = new ArrayList<>();

	public static boolean hasItemReloaded(ItemStack is) {
		for (GunRefillerRunnable s : allGunRefillers) {
			if (is.equals(s.reloadedItem))
				return true;
		}
		return false;
	}

	private BukkitTask r;
	private ItemStack reloadedItem;

	public GunRefillerRunnable(final Player player, final ItemStack temp, final Gun g, final int slot,
			final int reloadAmount, double seconds) {
		final GunRefillerRunnable gg = this;

		this.reloadedItem = temp;

		r = new BukkitRunnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				try {

					player.getWorld().playSound(player.getLocation(), WeaponSounds.RELOAD_MAG_IN.getSoundName(), 1, 1f);
					if (!QAMain.isVersionHigherThan(1, 9)) {
						try {
							player.getWorld().playSound(player.getLocation(), Sound.valueOf("CLICK"), 5, 1);
						} catch (Error | Exception e3) {
							player.getWorld().playSound(player.getLocation(), Sound.valueOf("BLOCK_LEVER_CLICK"), 5, 1);
						}
					}
				} catch (Error e2) {
					try {
						player.getWorld().playSound(player.getLocation(), Sound.valueOf("CLICK"), 5, 1);
					} catch (Error | Exception e3) {
						player.getWorld().playSound(player.getLocation(), Sound.valueOf("BLOCK_LEVER_CLICK"), 5, 1);
					}
				}
				ItemMeta newim = temp.getItemMeta();
				newim.setLore(ItemFact.getGunLore(g, temp, reloadAmount));
				newim.setDisplayName(g.getDisplayName());
				temp.setItemMeta(newim);
				if (QAMain.enableVisibleAmounts)
					temp.setAmount(reloadAmount);
				ItemStack current = player.getInventory().getItem(slot);
				int newSlot = slot;
				if (current == null || current.getType() != temp.getType()
						|| current.getDurability() != temp.getDurability()) {
					newSlot = -8;
					for (int i = 0; i < player.getInventory().getSize(); i++) {
						ItemStack check = player.getInventory().getItem(i);
						if (check != null && check.getType() == temp.getType()
								&& check.getDurability() == temp.getDurability()) {
							newSlot = i;
							break;
						}
					}
				}

				if (newSlot > -2) {
					if (player.isSneaking() && !QAMain.enableIronSightsON_RIGHT_CLICK) {
						player.getInventory().setItem(newSlot, ItemFact.getIronSights());
						Update19OffhandChecker.setOffhand(player, temp);
						QAMain.toggleNightvision(player, g, true);
					} else {
						player.getInventory().setItem(newSlot, temp);
					}

					QualityArmory.sendHotbarGunAmmoCount(player, g, temp, false);
				}

				if (!QAMain.reloadingTasks.containsKey(player.getUniqueId())) {
					return;
				}
				List<BukkitTask> rr = QAMain.reloadingTasks.get(player.getUniqueId());
				rr.remove(r);
				reloadedItem = null;
				allGunRefillers.remove(gg);

				if (rr.isEmpty()) {
					QAMain.reloadingTasks.remove(player.getUniqueId());
				} else {
					QAMain.reloadingTasks.put(player.getUniqueId(), rr);
				}
			}
		}.runTaskLater(QAMain.getInstance(), (long) (20 * seconds));

		allGunRefillers.add(gg);

		if (!QAMain.reloadingTasks.containsKey(player.getUniqueId())) {
			QAMain.reloadingTasks.put(player.getUniqueId(), new ArrayList<BukkitTask>());
		}
		List<BukkitTask> rr = QAMain.reloadingTasks.get(player.getUniqueId());
		rr.add(r);
		QAMain.reloadingTasks.put(player.getUniqueId(), rr);
	}

}
