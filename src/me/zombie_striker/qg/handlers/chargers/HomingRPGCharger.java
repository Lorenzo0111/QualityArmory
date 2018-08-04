package me.zombie_striker.qg.handlers.chargers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.guns.utils.GunUtil;
import me.zombie_striker.qg.guns.utils.HomingRocketProjectile;
import me.zombie_striker.qg.guns.utils.WeaponSounds;

public class HomingRPGCharger implements ChargingHandler {

	List<UUID> timeC = new ArrayList<>();
	public HomingRPGCharger() {
		ChargingManager.add(this);
	}

	@Override
	public boolean isCharging(Player player) {
		return timeC.contains(player.getUniqueId());
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
			new HomingRocketProjectile(start, player, two);


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
	public String getName() {
		return ChargingManager.HOMINGRPG;
	}

}
