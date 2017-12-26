package me.zombie_striker.qg.guns;

import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.MaterialStorage;
import me.zombie_striker.qg.ammo.AmmoType;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class HenryRifle extends DefaultGun {

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
	
	public HenryRifle(int d,ItemStack[] ing, float damage) {
		super("HenryRifle", MaterialStorage.getMS(Main.guntype,19), WeaponType.RIFLE, true, AmmoType.Ammo556,  0.2,2, 9, damage,false,d,WeaponSounds.GUN_MEDIUM,ing);
	}
}
