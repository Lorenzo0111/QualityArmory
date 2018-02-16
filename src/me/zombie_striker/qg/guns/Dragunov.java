package me.zombie_striker.qg.guns;

import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.MaterialStorage;
import me.zombie_striker.qg.ammo.AmmoType;
import me.zombie_striker.qg.guns.utils.GunUtil;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.guns.utils.WeaponType;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Dragunov extends DefaultGun {
	
	public Dragunov(int d,ItemStack[] ing, float damage, double cost) {
		super("Dragunov", MaterialStorage.getMS(Main.guntype,23), WeaponType.SNIPER, true,AmmoType.getAmmo("556"),  0.2,2, 8, damage,false,d,WeaponSounds.GUN_MEDIUM,cost,ing);
		this.setDelayBetweenShots(0.2);
	}
}
