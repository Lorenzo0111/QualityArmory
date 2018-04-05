package me.zombie_striker.qg.guns;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.ammo.AmmoType;
import me.zombie_striker.qg.guns.utils.GunUtil;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.guns.utils.WeaponType;

public class HenryRifle extends DefaultGun {


	List<UUID> time = new ArrayList<>();
	@Override
	public boolean shoot(final Player player) {
		if (!time.contains(player.getUniqueId())) {
			boolean shoot = super.shoot(player);
			time.add(player.getUniqueId());
			new BukkitRunnable() {
				
				@Override
				public void run() {
					player.getWorld().playSound(player.getLocation(), WeaponSounds.RELOAD_BULLET.getName(), 1,
							1f);
				}
			}.runTaskLater(Main.getInstance(), 10);
			new BukkitRunnable() {
				
				@Override
				public void run() {
					player.getWorld().playSound(player.getLocation(), WeaponSounds.RELOAD_BULLET.getName(), 1,
							1f);
				}
			}.runTaskLater(Main.getInstance(), 15);
			new BukkitRunnable() {
				
				@Override
				public void run() {
					time.remove(player.getUniqueId());
				}
			}.runTaskLater(Main.getInstance(), 20);
			
		}
		return false;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void reload(final Player player) {
		if (player.getItemInHand().getAmount() != getMaxBullets()) {
			double amount = (player.getItemInHand().getAmount()+0d);
			double amountReload = (8.0-amount)/8.0;
			
			double time = (4*(amountReload));
			GunUtil.basicReload(this, player,WeaponType.isUnlimited(WeaponType.RIFLE),time);
			for (int i = 0; i < 8-(amount); i++) {
				new BukkitRunnable() {
					@Override
					public void run() {
						try{
							/*
							player.getWorld().playSound(player.getLocation(),
									Sound.BLOCK_PISTON_EXTEND, 5, 4f);
							player.getWorld().playSound(player.getLocation(),
									Sound.BLOCK_SAND_BREAK, 8, 1.4f);*/
							player.getWorld().playSound(player.getLocation(),
									WeaponSounds.RELOAD_BULLET.getName(), 1, 1f);
						}catch(Error e){
							player.getWorld().playSound(player.getLocation(),
									Sound.valueOf("PISTON_EXTEND"), 5, 4f);
							player.getWorld().playSound(player.getLocation(),
									Sound.valueOf("DIG_SAND"), 8, 1.4f);							
						}
					}
				}.runTaskLater(Main.getInstance(),(int)(20*time/6*i));
			}	
		}
	}
	
	public HenryRifle(int d,ItemStack[] ing, float damage, double cost) {
		super("HenryRifle",  WeaponType.RIFLE, true,AmmoType.getAmmo("556"),  0.2,2, 9, damage,false,d,WeaponSounds.GUN_MEDIUM,cost,ing);
	}
}
