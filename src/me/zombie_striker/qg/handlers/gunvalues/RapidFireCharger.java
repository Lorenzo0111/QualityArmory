package me.zombie_striker.qg.handlers.gunvalues;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.zombie_striker.qg.ItemFact;
import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.guns.utils.GunUtil;
import me.zombie_striker.qg.handlers.AimManager;

public class RapidFireCharger implements ChargingHandler {

	@Override
	public boolean isCharging(Player player) {
		return false;
	}

	@Override
	public boolean isReloading(Player player) {
		return false;
	}

	@Override
	public boolean shoot(final Gun g, final Player player, ItemStack stack) {
		GunUtil.shoot(g,player, g.getSway() * AimManager.getSway(g, player.getUniqueId()), g.getDamage(), 1, 200);
		GunUtil.playShoot(g, player);
		for (int j = 1; j < Math.min(ItemFact.getAmount(stack), g.getBulletsPerShot()); j++)
			new BukkitRunnable() {
				@Override
				public void run() {
					GunUtil.shoot(g,player, g.getSway() * AimManager.getSway(g, player.getUniqueId()), g.getDamage(), 1,
							200);
					GunUtil.playShoot(g, player);

				}
			}.runTaskLater(Main.getInstance(), (long) ((int) ((4.0 / g.getBulletsPerShot()) * (j + 1))));

		return false;
	}

	@Override
	public double reload(final Player player, Gun g, int amountReloading) {
		return g.getReloadTime();
	}

}
