package me.zombie_striker.qg.guns.chargers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import me.zombie_striker.qg.util.FoliaRunnable;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.guns.utils.WeaponSounds;

public class BreakactionCharger implements ChargingHandler {

	List<UUID> timeC = new ArrayList<>();
	List<UUID> timeR = new ArrayList<>();

	public BreakactionCharger() {
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
				player.getWorld().playSound(player.getLocation(), g.getChargingSound(), 1, 1f);
			}
		}.runTaskLater(QAMain.getInstance(), player, 10);
		new FoliaRunnable() {

			@Override
			public void run() {
				player.getWorld().playSound(player.getLocation(), g.getChargingSound(), 1, 1f);
			}
		}.runTaskLater(QAMain.getInstance(), player, 15);
		new FoliaRunnable() {

			@Override
			public void run() {
				timeC.remove(player.getUniqueId());
			}
		}.runTaskLater(QAMain.getInstance(), player, (long) (g.getDelayBetweenShotsInSeconds()*20));
		return true;
	}


	@Override
	public String getName() {
		return ChargingManager.BREAKACTION;
	}

	@Override
	public String getDefaultChargingSound() {
		return WeaponSounds.RELOAD_BULLET.getSoundName();
		//g.getChargingSound()
	}
}
