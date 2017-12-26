package me.zombie_striker.qg.guns;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.MaterialStorage;
import me.zombie_striker.qg.ammo.AmmoType;
import me.zombie_striker.qg.handlers.IronSightsToggleItem;
import me.zombie_striker.qg.handlers.Update19OffhandChecker;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class MP5K extends DefaultGun {

	//List<UUID> time = new ArrayList<>();

	@SuppressWarnings("deprecation")
	@Override
	public void shoot(final Player player) {
		final boolean offhand = player.getInventory().getItemInHand().getDurability()==IronSightsToggleItem.getData();
		if ((!offhand&&player.getInventory().getItemInHand().getAmount()>3)||(offhand&&Update19OffhandChecker.hasAmountOFfhandGreaterthan(player,3))) {
			final Gun g = this;
			GunUtil.basicShoot(offhand,g, player, getSway());
			/**
			if (time.contains(player.getUniqueId()))
				return;
			
			time.add(player.getUniqueId());
			final Gun g = this;
			GunUtil.basicShoot(offhand,g, player, getSway());
			new BukkitRunnable() {
				@Override
				public void run() {
					GunUtil.basicShoot(offhand,g, player, getSway());
				}
			}.runTaskLater(Main.getInstance(), 2);
			new BukkitRunnable() {
				@Override
				public void run() {
					GunUtil.basicShoot(offhand,g, player, getSway());
				}
			}.runTaskLater(Main.getInstance(), 4);
			new BukkitRunnable() {
				@Override
				public void run() {
					time.remove(player.getUniqueId());
				}
			}.runTaskLater(Main.getInstance(), 5);*/
		}
	}
	public MP5K(int d,ItemStack[] ing, float damage) {
		super("MP5K", MaterialStorage.getMS(Main.guntype,4), WeaponType.SMG,false, AmmoType.Ammo9mm,  0.3,2, 34, damage,true,d,WeaponSounds.GUN_SMALL,ing);
	}
}
