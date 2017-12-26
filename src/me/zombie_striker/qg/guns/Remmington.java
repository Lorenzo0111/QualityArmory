package me.zombie_striker.qg.guns;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.zombie_striker.qg.ItemFact;
import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.MaterialStorage;
import me.zombie_striker.qg.ammo.AmmoType;
import me.zombie_striker.qg.handlers.IronSightsToggleItem;
import me.zombie_striker.qg.handlers.Update19OffhandChecker;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Remmington extends DefaultGun {
	List<UUID> time = new ArrayList<>();

	@SuppressWarnings("deprecation")
	@Override
	public void shoot(final Player player) {
		final ItemStack temp = player.getInventory().getItemInHand();
		if (temp.getAmount() > 1||(!Main.enableVisibleAmounts)) {
			if (time.contains(player.getUniqueId()))
				return;
			time.add(player.getUniqueId());
			

			boolean offhand = player.getInventory().getItemInHand().getDurability()==IronSightsToggleItem.getData();
			if ((!offhand&&ItemFact.getAmount(player.getInventory().getItemInHand())>0)||(offhand&&Update19OffhandChecker.hasAmountOFfhandGreaterthan(player,0))) {
				GunUtil.basicShoot(offhand,this, player, 0.1, 10);
			}
			
			new BukkitRunnable() {
				@Override
				public void run() {
					try{
						/*
					player.getWorld().playSound(player.getLocation(),
							Sound.BLOCK_LEVER_CLICK, 8, 1.4f);
					player.getWorld().playSound(player.getLocation(),
							Sound.BLOCK_SAND_BREAK, 8, 1.4f);*/
						player.getWorld().playSound(player.getLocation(),
								WeaponSounds.RELOAD_BULLET.getName(), 1, 1f);
					}catch(Error e){

						player.getWorld().playSound(player.getLocation(),
								Sound.valueOf("CLICK"), 8, 1.4f);
						player.getWorld().playSound(player.getLocation(),
								Sound.valueOf("DIG_SAND"), 8, 1.4f);
					}
				}
			}.runTaskLater(Main.getInstance(), 12);
			new BukkitRunnable() {
				@Override
				public void run() {
					try{
						player.getWorld().playSound(player.getLocation(),
								WeaponSounds.RELOAD_BOLT.getName(), 1, 1f);/*
					player.getWorld().playSound(player.getLocation(),
							Sound.BLOCK_SAND_BREAK, 8, 1.4f);
					player.getWorld().playSound(player.getLocation(),
							Sound.BLOCK_LEVER_CLICK, 8, 1);*/
					}catch(Error e){
						player.getWorld().playSound(player.getLocation(),
								Sound.valueOf("DIG_SAND"), 8, 1.4f);
						player.getWorld().playSound(player.getLocation(),
								Sound.valueOf("CLICK"), 8, 1);
						
					}
					time.remove(player.getUniqueId());
				}
			}.runTaskLater(Main.getInstance(), 16);
		}
	}
	@SuppressWarnings("deprecation")
	@Override
	public void reload(final Player player) {
		if (ItemFact.getAmount(player.getItemInHand()) != getMaxBullets()) {
			double amount = ItemFact.getAmount(player.getItemInHand());
			double amountReload = (7.0-amount)/7.0;
			
			double time = (2.5*(amountReload));
			GunUtil.basicReload(this, player,WeaponType.isUnlimited(WeaponType.SHOTGUN),time);
			for (int i = 0; i < 7-(amount); i++) {
				final boolean k = i==7;
				new BukkitRunnable() {
					@Override
					public void run() {
						try{
						if(k){
							player.getWorld().playSound(player.getLocation(),
									Sound.BLOCK_PISTON_EXTEND, 5, 4f);
							player.getWorld().playSound(player.getLocation(),
									Sound.BLOCK_SAND_BREAK, 8, 1.4f);
						}
						player.getWorld().playSound(player.getLocation(),
								Sound.BLOCK_PISTON_EXTEND, 5, 4f);
						}catch(Error e){

							if(k){
								player.getWorld().playSound(player.getLocation(),
										Sound.valueOf("PISTON_EXTEND"), 5, 4f);
								player.getWorld().playSound(player.getLocation(),
										Sound.valueOf("DIG_SAND"), 8, 1.4f);
							}
							player.getWorld().playSound(player.getLocation(),
									Sound.valueOf("PISTON_EXTEND"), 5, 4f);
						}
					}
				}.runTaskLater(Main.getInstance(),(int)(time/(8-amount)*20*i));
			}	
		}
	}

	public Remmington(int d,ItemStack[] ing, float damage) {
		super("Remmington", MaterialStorage.getMS(Main.guntype,8), WeaponType.SHOTGUN, false, AmmoType.AmmoBuckshot,  0.2,2.6, 8, damage,false,d,WeaponSounds.GUN_BIG,ing);
	}
}
