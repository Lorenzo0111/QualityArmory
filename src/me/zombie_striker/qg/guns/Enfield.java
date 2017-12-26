package me.zombie_striker.qg.guns;

import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.MaterialStorage;
import me.zombie_striker.qg.ammo.AmmoType;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Enfield extends DefaultGun {

	@SuppressWarnings("deprecation")
	@Override
	public void reload(final Player player) {
		if (player.getItemInHand().getAmount() != getMaxBullets()) {
			double amount = (player.getItemInHand().getAmount() + 0d);
			double amountReload = (6.0 - amount) / 6.0;

			double time = (5 * (amountReload));
			GunUtil.basicReload(this, player,
					WeaponType.isUnlimited(WeaponType.PISTOL), time);
			for (int i = 0; i < 7 - (amount) + 1; i++) {
				new BukkitRunnable() {
					@Override
					public void run() {
						try{/*
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
				}.runTaskLater(Main.getInstance(), (int) (20*time/5 * i));
			}
		}
	}


	public Enfield(int d,ItemStack[] ing, float damage) {
		super("Enfield-1853", MaterialStorage.getMS(Main.guntype,18), WeaponType.PISTOL, true, AmmoType.Ammo9mm,  0.2,2, 7, damage,false,d,WeaponSounds.GUN_SMALL,ing);
	}
}
