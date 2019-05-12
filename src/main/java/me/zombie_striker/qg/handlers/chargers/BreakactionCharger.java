package me.zombie_striker.qg.handlers.chargers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

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
		new BukkitRunnable() {
			@Override
			public void run() {
				player.getWorld().playSound(player.getLocation(), WeaponSounds.RELOAD_BULLET.getSoundName(), 1, 1f);
			}
		}.runTaskLater(QAMain.getInstance(), 10);
		new BukkitRunnable() {

			@Override
			public void run() {
				player.getWorld().playSound(player.getLocation(), WeaponSounds.RELOAD_BULLET.getSoundName(), 1, 1f);
			}
		}.runTaskLater(QAMain.getInstance(), 15);
		new BukkitRunnable() {

			@Override
			public void run() {
				timeC.remove(player.getUniqueId());
			}
		}.runTaskLater(QAMain.getInstance(), 20);
		return true;
	}


	@Override
	public String getName() {
		return ChargingManager.BREAKACTION;
	}

}
