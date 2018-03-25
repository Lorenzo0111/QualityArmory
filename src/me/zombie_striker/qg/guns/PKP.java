package me.zombie_striker.qg.guns;

import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.MaterialStorage;
import me.zombie_striker.qg.ammo.AmmoType;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.guns.utils.WeaponType;

import org.bukkit.inventory.ItemStack;

public class PKP extends DefaultGun {

	//List<UUID> time = new ArrayList<>();=
	public PKP(int d,ItemStack[] ing, float damage, double cost) {
		super("PKP",  WeaponType.RIFLE,false, AmmoType.getAmmo("556"),  0.3,2, 60, damage,true,d,WeaponSounds.GUN_BIG,cost,ing);
		this.setDelayBetweenShots(0.005);
	}
}
