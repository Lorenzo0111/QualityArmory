package me.zombie_striker.qg;

import me.zombie_striker.qg.ammo.AmmoType;
import me.zombie_striker.qg.guns.*;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class QualityArmory {

	public static ItemStack getGunItemStack(Material mat, int data) {
		if (Main.gunRegister.containsKey(MaterialStorage.getMS(mat, data)))
			return ItemFact.getGun(MaterialStorage.getMS(mat, data));
		return null;
	}

	public static ItemStack getGunItemStack(Gun gun) {
		return ItemFact.getGun(gun);
	}
	public static ItemStack getGunItemStack(BasegameGuns gun) {
		return ItemFact.getGun(getGun(gun));
	}

	public static Gun getGun(Material mat, int data) {
		if (Main.gunRegister.containsKey(MaterialStorage.getMS(mat, data)))
			return Main.gunRegister.get(MaterialStorage.getMS(mat, data));
		return null;
	}
	public static Gun getGun(BasegameGuns gun){
		return getGun(Main.guntype, gun.getData());
	}

	public static ItemStack getAmmoItemStack(Material mat, int data) {
		if (Main.ammoRegister.containsKey(MaterialStorage.getMS(mat, data)))
			return ItemFact.getAmmo(mat, data);
		return null;
	}

	public static ItemStack getAmmoItemStack(AmmoType type) {
		return ItemFact.getAmmo(type.type);
	}

	/**
	 * Creates and registers a new gun.
	 * @param name of gun
	 * @param m Material type
	 * @param data durability int
	 * @param type gun type
	 * @param hasIronSights if the gun has ironsights
	 * @param ammotype ammo type
	 * @param acc default accuracy (normally 0.2)
	 * @param swayMultiplier (normally 1)
	 * @param maxBullets Maximum amount of bullets per reload
	 * @param damage damage per bullet
	 * @param gunDurability durability of gun (does not matter unless you have durability enabled)
	 * @return The instance of the gun you created
	 */
	public static Gun createSimpleGun(String name, Material mat, int data,
			WeaponType type, boolean hasIronSights, AmmoType ammotype, double acc,
			int swayMultiplier, int maxBullets, float damage, int gunDurability) {
	return createSimpleGun(name, mat, data, type, hasIronSights, ammotype, acc, swayMultiplier, maxBullets, damage,false, gunDurability,WeaponSounds.GUN_MEDIUM);
	}

	/**
	 * Creates and registers a new gun.
	 * @param name of gun
	 * @param m Material type
	 * @param data durability int
	 * @param type gun type
	 * @param hasIronSights if the gun has ironsights
	 * @param ammotype ammo type
	 * @param acc default accuracy (normally 0.2)
	 * @param swayMultiplier (normally 1)
	 * @param maxBullets Maximum amount of bullets per reload
	 * @param damage damage per bullet
	 * @param isAutomatic if the gun is automatic
	 * @param gunDurability durability of gun (does not matter unless you have durability enabled)
	 * @return The instance of the gun you created
	 */
	public static Gun createSimpleGun(String name, Material mat, int data,
			WeaponType type, boolean hasIronSights, AmmoType ammotype, double acc,
			int swayMultiplier, int maxBullets, float damage,boolean isAutomatic, int gunDurability, WeaponSounds sound) {
		MaterialStorage mm = MaterialStorage.getMS(mat, data);
		Gun g = new DefaultGun(name, mm, type, hasIronSights, ammotype, acc,
				swayMultiplier, maxBullets, damage,isAutomatic, gunDurability,sound);
		Main.gunRegister.put(mm, g);
		return g;
	}

}
