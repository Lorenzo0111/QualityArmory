package me.zombie_striker.qg.handlers.reloaders;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.guns.Gun;

public class PumpactionReloader implements ReloadingHandler {

	List<UUID> timeR = new ArrayList<>();

	public PumpactionReloader() {
		ReloadingManager.add(this);
	}

	@Override
	public boolean isReloading(Player player) {
		return false;
	}

	@Override
	public double reload(final Player player, Gun g, int amountReloading) {
		double time = (g.getReloadTime()) / g.getMaxBullets();
		double time2 = time * amountReloading;
		for (int i = 0; i < amountReloading; i++) {
			final boolean k = (i + 1 == amountReloading);
			new BukkitRunnable() {
				@Override
				public void run() {
					try {
						if (k) {
							player.getWorld().playSound(player.getLocation(), Sound.BLOCK_PISTON_EXTEND, 5, 4f);
							player.getWorld().playSound(player.getLocation(), Sound.BLOCK_SAND_BREAK, 8, 1.4f);
						}
						player.getWorld().playSound(player.getLocation(), Sound.BLOCK_PISTON_EXTEND, 5, 4f);
					} catch (Error e) {
						try {
							if (k) {
								player.getWorld().playSound(player.getLocation(), Sound.valueOf("PISTON_EXTEND"), 5,
										4f);
								player.getWorld().playSound(player.getLocation(), Sound.valueOf("DIG_SAND"), 8, 1.4f);
							}
							player.getWorld().playSound(player.getLocation(), Sound.valueOf("PISTON_EXTEND"), 5, 4f);
						} catch (Error | Exception e43) {
						}
					}
				}
			}.runTaskLater(Main.getInstance(), (int) (time * i * 20));
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				timeR.remove(player.getUniqueId());

			}
		}.runTaskLater(Main.getInstance(), (int) (time2 * 20) + 5);
		return time2;
	}

	@Override
	public String getName() {

		return ReloadingManager.PUMPACTIONRELOAD;
	}

}
