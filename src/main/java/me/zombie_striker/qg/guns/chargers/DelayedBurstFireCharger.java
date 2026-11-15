package me.zombie_striker.qg.guns.chargers;


import org.bukkit.scheduler.BukkitTask;
import me.zombie_striker.qg.utils.FoliaRunnable;
import me.zombie_striker.qg.utils.Compat;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.guns.utils.GunUtil;

public class DelayedBurstFireCharger implements ChargingHandler {


	public DelayedBurstFireCharger() {
		ChargingManager.add(this);
	}

	@Override
	public boolean isCharging(Player player) {
		return QAMain.getPlayerState().getDelayedBurstFireTasks().containsKey(player.getUniqueId());
	}

	@Override
	public boolean shoot(final Gun g, final Player player, final ItemStack stack) {
		GunUtil.shootHandler(g, player);
		GunUtil.playShoot(g, player);

		BukkitTask task = new FoliaRunnable() {
			int slotUsed = player.getInventory().getHeldItemSlot();
			@SuppressWarnings("deprecation")
			boolean offhand = QualityArmory.isIronSights(player.getItemInHand());
			int shotCurrently = 1;

			int currentRate = (int) (10 / g.getFireRate() / Math.pow(2, g.getBulletsPerShot()));
			int skippedTicks = 0;

			@Override
			@SuppressWarnings("deprecation")
			public void run() {
				if (skippedTicks >= currentRate) {
					skippedTicks = 0;
					currentRate *= 2;
				} else {
					skippedTicks++;
					return;
				}
				int amount = Gun.getAmount(player);
				if (shotCurrently >= g.getBulletsPerShot() || slotUsed != player.getInventory().getHeldItemSlot()
						|| amount <= 0) {
					if (QAMain.getPlayerState().getDelayedBurstFireTasks().containsKey(player.getUniqueId()))
						QAMain.getPlayerState().getDelayedBurstFireTasks().remove(player.getUniqueId()).cancel();
					return;
				}

				GunUtil.shootHandler(g, player);
				GunUtil.playShoot(g, player);
				if (QAMain.getConfiguration().weapons.enableRecoil && g.getRecoil() > 0) {
					GunUtil.addRecoil(player, g);
				}
				shotCurrently++;
				amount--;

				if (amount < 0)
					amount = 0;

				// if (QAMain.enableVisibleAmounts) {
				// stack.setAmount(amount > 64 ? 64 : amount == 0 ? 1 : amount);
				// }
				ItemMeta im = stack.getItemMeta();
				int slot;
				if (offhand) {
					slot = -1;
				} else {
					slot = player.getInventory().getHeldItemSlot();
				}
				stack.setItemMeta(im);
				Gun.updateAmmo(g, stack, amount);
				if (slot == -1) {
					if (Compat.OFFHAND && QualityArmory.isIronSights(player.getItemInHand())) {
						player.getInventory().setItemInOffHand(stack);
					} else if (Compat.OFFHAND) {
						player.getInventory().setItemInHand(stack);
					}
				} else {
					player.getInventory().setItem(slot, stack);
				}
				QualityArmory.sendHotbarGunAmmoCount(player, g, stack, false);
			}
		}.runTaskTimer(QAMain.getInstance(), player, 1, 1);
        // Folia returns no task when the player is already gone: storing it would leave them charging forever.
        if (!task.isCancelled())
            QAMain.getPlayerState().getDelayedBurstFireTasks().put(player.getUniqueId(), task);
		return false;
	}

	@Override
	public String getName() {

		return ChargingManager.DelayedBURSTFIRE;
	}
	@Override
	public String getDefaultChargingSound() {
		return WeaponSounds.RELOAD_BULLET.getSoundName();
		//g.getChargingSound()
	}

}
