package me.zombie_striker.qg.handlers.gunvalues;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.zombie_striker.qg.ItemFact;
import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.attachments.AttachmentBase;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.guns.utils.GunUtil;
import me.zombie_striker.qg.handlers.AimManager;

public class RapidFireCharger implements ChargingHandler {
	
	public static HashMap<UUID, BukkitTask> shooters = new HashMap<>();

	@Override
	public boolean isCharging(Player player) {
		return false;
	}

	@Override
	public boolean isReloading(Player player) {
		return false;
	}

	@Override
	public boolean shoot(final Gun g,final Player player, final ItemStack stack) {
		GunUtil.shoot(g, player, g.getSway() * AimManager.getSway(g, player.getUniqueId()), g.getDamage(), 1, 200);
		final AttachmentBase attach = Main.getGunWithAttchments(stack);
		GunUtil.playShoot(g, attach, player);
		/*
		 * for (int j = 1; j < Math.min(ItemFact.getAmount(stack),
		 * g.getBulletsPerShot()); j++) new BukkitRunnable() {
		 * 
		 * @Override public void run() { GunUtil.shoot(g,player, g.getSway() *
		 * AimManager.getSway(g, player.getUniqueId()), g.getDamage(), 1, 200);
		 * GunUtil.playShoot(g, player);
		 * 
		 * } }.runTaskLater(Main.getInstance(), (long) ((int) ((4.0 /
		 * g.getBulletsPerShot()) * (j + 1))));
		 */
		shooters.put(player.getUniqueId(),new BukkitRunnable() {			
			int slotUsed = player.getInventory().getHeldItemSlot();
			@SuppressWarnings("deprecation")
			boolean offhand = Main.isIS(player.getItemInHand());
			@SuppressWarnings("deprecation")
			public void run() {

				int amount = ItemFact.getAmount(stack) - /*(g.getChargingVal() != null
						&& ChargingHandlerEnum.getEnumV(g.getChargingVal()) == ChargingHandlerEnum.RAPIDFIRE
								? g.getBulletsPerShot()
								: 1)*/1;
				if(!player.isSneaking() || slotUsed != player.getInventory().getHeldItemSlot() || amount <= 0) {
					shooters.remove(player.getUniqueId()).cancel();
				}
				
				
				GunUtil.shoot(g, player, g.getSway() * AimManager.getSway(g, player.getUniqueId()), g.getDamage(), 1,
						200);
				GunUtil.playShoot(g,attach, player);


				if (amount < 0)
					amount = 0;

				if (Main.enableVisibleAmounts) {
					stack.setAmount(amount > 64 ? 64 : amount == 0 ? 1 : amount);
				}
				ItemMeta im = stack.getItemMeta();
				int slot;
				if (offhand) {
					slot = -1;
				} else {
					slot = player.getInventory().getHeldItemSlot();
				}
				im.setLore(ItemFact.getGunLore(g,attach, stack, amount));
				stack.setItemMeta(im);
				if (slot == -1) {
					try {
						if(Main.isIS(player.getItemInHand())) {
						player.getInventory().setItemInOffHand(stack);
						}else {
							player.getInventory().setItemInHand(stack);
						}
						
					} catch (Error e) {
					}
				} else {
					player.getInventory().setItem(slot, stack);
				}
				Main.sendHotbarGunAmmoCount(player, g, attach, stack);
			}
		}.runTaskTimer(Main.getInstance(), 10 / g.getBulletsPerShot(), 10 / g.getBulletsPerShot()));
		return false;
	}

	@Override
	public double reload(final Player player, Gun g, int amountReloading) {
		return g.getReloadTime();
	}

}
