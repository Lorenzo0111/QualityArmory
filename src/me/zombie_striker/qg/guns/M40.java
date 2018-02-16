package me.zombie_striker.qg.guns;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.MaterialStorage;
import me.zombie_striker.qg.ammo.AmmoType;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.guns.utils.WeaponType;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class M40 extends DefaultGun {

	List<UUID> time = new ArrayList<>();

	@Override
	public boolean shoot(final Player player) {
		if (time.contains(player.getUniqueId()))
			return false;
		if (super.shoot(player)) {
			time.add(player.getUniqueId());
			new BukkitRunnable() {
				@Override
				public void run() {
					try {
						player.getWorld().playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 5, 1);
					} catch (Error e) {
						player.getWorld().playSound(player.getLocation(), Sound.valueOf("CLICK"), 5, 1);
					}
				}
			}.runTaskLater(Main.getInstance(), 10);
			new BukkitRunnable() {
				@Override
				public void run() {
					try {
						player.getWorld().playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 5, 1);
					} catch (Error e) {
						player.getWorld().playSound(player.getLocation(), Sound.valueOf("CLICK"), 5, 1);
					}
					time.remove(player.getUniqueId());
				}
			}.runTaskLater(Main.getInstance(), 16);
			return true;
		}
		return false;
	}

	public M40(int d, ItemStack[] ing, float damage, double cost) {
		super("M40", MaterialStorage.getMS(Main.guntype, 13), WeaponType.SNIPER, true, AmmoType.getAmmo("556"), 0.1, 4, 8,
				damage, false, d, WeaponSounds.GUN_BIG, cost,ing);
		this.setDelayBetweenShots(0.8);
	}
}
