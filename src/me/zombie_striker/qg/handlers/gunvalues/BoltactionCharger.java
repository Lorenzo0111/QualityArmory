package me.zombie_striker.qg.handlers.gunvalues;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.guns.utils.WeaponSounds;

public class BoltactionCharger implements ChargingHandler {

	List<UUID> timeC = new ArrayList<>();
	List<UUID> timeR = new ArrayList<>();
	
	public BoltactionCharger() {
		ChargingManager.add(this);
	}

	@Override
	public boolean isCharging(Player player) {
		return timeC.contains(player.getUniqueId());
	}

	@Override
	public boolean isReloading(Player player) {
		return false;
	}

	@Override
	public boolean shoot(Gun g, final Player player, ItemStack stack) {
		timeR.add(player.getUniqueId());
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					if (Main.isVersionHigherThan(1, 9)) {
						player.getWorld().playSound(player.getLocation(), WeaponSounds.RELOAD_BOLT.getName(), 1, 1f);
					} else
						player.getWorld().playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 5, 1);
				} catch (Error e) {
					try {
						player.getWorld().playSound(player.getLocation(), Sound.valueOf("CLICK"), 5, 1);
					} catch (Error | Exception e43) {
					}
				}
			}
		}.runTaskLater(Main.getInstance(), 10);
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					if (Main.isVersionHigherThan(1, 9)) {
						player.getWorld().playSound(player.getLocation(), WeaponSounds.RELOAD_BOLT.getName(), 1, 1f);
					} else
						player.getWorld().playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 5, 1);
				} catch (Error e) {
					try {
						player.getWorld().playSound(player.getLocation(), Sound.valueOf("CLICK"), 5, 1);
					} catch (Error | Exception e43) {
					}
				}
				timeR.remove(player.getUniqueId());
			}
		}.runTaskLater(Main.getInstance(), 16);
		return true;
	}

	@Override
	public double reload(final Player player, Gun g, int amountReloading) {
		return g.getReloadTime();
	}

	@Override
	public String getName() {
		return ChargingManager.BOLT;
	}

}
