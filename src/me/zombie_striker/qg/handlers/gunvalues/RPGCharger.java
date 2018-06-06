package me.zombie_striker.qg.handlers.gunvalues;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.guns.utils.GunUtil;
import me.zombie_striker.qg.guns.utils.RocketProjectile;
import me.zombie_striker.qg.guns.utils.WeaponSounds;

public class RPGCharger implements ChargingHandler {

	List<UUID> timeC = new ArrayList<>();
	List<UUID> timeR = new ArrayList<>();
	public RPGCharger() {
		ChargingManager.add(this);
	}

	@Override
	public boolean isCharging(Player player) {
		return timeC.contains(player.getUniqueId());
	}

	@Override
	public boolean isReloading(Player player) {
		return timeR.contains(player.getUniqueId());
	}

	@Override
	public boolean shoot(Gun g, final Player player, ItemStack stack) {
		timeC.add(player.getUniqueId());
		double sway = g.getSway();
		Location start = player.getEyeLocation();
		Vector go = player.getLocation().getDirection().normalize();
		go.add(new Vector((Math.random() * 2 * sway) - sway, (Math.random() * 2 * sway) - sway,
				(Math.random() * 2 * sway) - sway));
		Vector two = go.clone().multiply(2);

		for (int j = 0; j < g.getBulletsPerShot(); j++)
			new RocketProjectile(start, player, two);


		GunUtil.playShoot(g,null, player);
		
		new BukkitRunnable() {

			@Override
			public void run() {
				try {
				player.getWorld().playSound(player.getLocation(), WeaponSounds.RELOAD_BULLET.getName(), 1, 2f);
				}catch(Error|Exception e43) {}
			}
		}.runTaskLater(Main.getInstance(), 10);
		new BukkitRunnable() {

			@Override
			public void run() {
				timeC.remove(player.getUniqueId());
			}
		}.runTaskLater(Main.getInstance(), 20);

		return false;
	}

	@Override
	public double reload(final Player player, Gun g, int amountReloading) {
		double time = ((double)g.getReloadTime()) / g.getMaxBullets();
		double time2 = time*amountReloading;
		for (int i = 0; i < amountReloading; i++) {
			new BukkitRunnable() {
				@Override
				public void run() {
					try {/*
							 * player.getWorld().playSound(player.getLocation(), Sound.BLOCK_PISTON_EXTEND,
							 * 5, 4f); player.getWorld().playSound(player.getLocation(),
							 * Sound.BLOCK_SAND_BREAK, 8, 1.4f);
							 */

						player.getWorld().playSound(player.getLocation(), WeaponSounds.RELOAD_BULLET.getName(), 1, 1f);
					} catch (Error e) {
						try {
						player.getWorld().playSound(player.getLocation(), Sound.valueOf("PISTON_EXTEND"), 5, 4f);
						player.getWorld().playSound(player.getLocation(), Sound.valueOf("DIG_SAND"), 8, 1.4f);
						}catch(Error|Exception e43) {}
					}

				}
			}.runTaskLater(Main.getInstance(), (int) (time * i*20));
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				timeR.remove(player.getUniqueId());

			}
		}.runTaskLater(Main.getInstance(), (int) (time2*20) + 5);
		return time2;
	}

	@Override
	public String getName() {
		return ChargingManager.RPG;
	}

}
