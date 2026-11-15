package me.zombie_striker.qg.guns.chargers;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import me.zombie_striker.qg.utils.FoliaRunnable;
import me.zombie_striker.qg.utils.Compat;
import java.util.UUID;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.guns.utils.WeaponSounds;

public class PumpactionCharger implements ChargingHandler {

	Set<UUID> timeC = ConcurrentHashMap.newKeySet();
	Set<UUID> timeR = ConcurrentHashMap.newKeySet();
public PumpactionCharger() {
	ChargingManager.add(this);
}
	@Override
	public boolean isCharging(Player player) {
		return timeC.contains(player.getUniqueId());
	}

	@Override
	public boolean shoot(Gun g, final Player player, ItemStack stack) {
		timeC.add(player.getUniqueId());

		new FoliaRunnable() {
			@Override
			public void run() {
				if (Compat.WORLD_SOUND_BY_NAME) {
					/*
					 * player.getWorld().playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 8,
					 * 1.4f); player.getWorld().playSound(player.getLocation(),
					 * Sound.BLOCK_SAND_BREAK, 8, 1.4f);
					 */
					player.getWorld().playSound(player.getLocation(), g.getChargingSound(), 1, 1f);
				} else {
					player.getWorld().playSound(player.getLocation(), Sound.valueOf("CLICK"), 8, 1.4f);
				player.getWorld().playSound(player.getLocation(), Sound.valueOf("DIG_SAND"), 8, 1.4f);
				}
			}
		}.runTaskLater(QAMain.getInstance(), player, 12);
		new FoliaRunnable() {
			@Override
			public void run() {
				if (Compat.WORLD_SOUND_BY_NAME) {
					player.getWorld().playSound(player.getLocation(), g.getChargingSound(), 1,
							1f);/*
								 * player.getWorld().playSound(player.getLocation(), Sound.BLOCK_SAND_BREAK, 8,
								 * 1.4f); player.getWorld().playSound(player.getLocation(),
								 * Sound.BLOCK_LEVER_CLICK, 8, 1);
								 */
				} else {try {
					player.getWorld().playSound(player.getLocation(), Sound.valueOf("DIG_SAND"), 8, 1.4f);
					player.getWorld().playSound(player.getLocation(), Sound.valueOf("CLICK"), 8, 1);
				}catch(Error|Exception e43) {}

				}
			}
		}.runTaskLater(QAMain.getInstance(), player, (long) (g.getDelayBetweenShotsInSeconds()*20));
		new FoliaRunnable() {
			@Override
			public void run() {
				timeC.remove(player.getUniqueId());
			}
		}.runTaskLater(QAMain.getInstance(), player, 20);
		return true;
	}


	@Override
	public String getName() {

		return ChargingManager.PUMPACTION;
	}
	@Override
	public String getDefaultChargingSound() {
		return WeaponSounds.RELOAD_BOLT.getSoundName();
		//g.getChargingSound()
	}

}
