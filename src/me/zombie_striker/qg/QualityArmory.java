package me.zombie_striker.qg;

import me.zombie_striker.qg.ammo.Ammo;
import me.zombie_striker.qg.ammo.AmmoType;
import me.zombie_striker.qg.guns.*;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.guns.utils.WeaponType;
import me.zombie_striker.qg.handlers.IronSightsToggleItem;

import java.util.*;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class QualityArmory {
	/**
	 * Returns the itemstack instance of a gun by using the material and data.
	 * @param mat
	 * @param data
	 * @return
	 */
	public static ItemStack getGunItemStack(Material mat, int data) {
		if (Main.gunRegister.containsKey(MaterialStorage.getMS(mat, data)))
			return ItemFact.getGun(MaterialStorage.getMS(mat, data));
		return null;
	}
	/**
	 * Returns the itemstack instance of the gun
	 * @param gun
	 * @return
	 */
	public static ItemStack getGunItemStack(Gun gun) {
		return ItemFact.getGun(gun);
	}
	/**
	 * Retrusn the gun instance by its material and data
	 * @param mat
	 * @param data
	 * @return
	 */
	public static Gun getGun(Material mat, int data) {
		if (Main.gunRegister.containsKey(MaterialStorage.getMS(mat, data)))
			return Main.gunRegister.get(MaterialStorage.getMS(mat, data));
		return null;
	}
	/**
	 * Returns the gun instance by its name
	 * @param name
	 * @return
	 */
	public static Gun getGun(String name){
		for(Gun g : getAllGunTypes()) {
			if(g.getName().equals(name))
				return g;
		}
		return null;
	}
	/**
	 * Returns an itemstack instance of an ammo type by its material and data.
	 * @param mat
	 * @param data
	 * @return
	 */
	public static ItemStack getAmmoItemStack(Material mat, int data) {
		if (Main.ammoRegister.containsKey(MaterialStorage.getMS(mat, data)))
			return ItemFact.getAmmo(mat, data);
		return null;
	}
	/**
	 * Returns an itemstack instance of an ammo type by its name
	 * @param name
	 * @return
	 */
	public static ItemStack getAmmoItemStackByName(String name) {
		return ItemFact.getAmmo(AmmoType.getAmmo(name));
	}
	/**
	 * Retrurns the itemstack instance of an ammo type
	 * @param ammo
	 * @return
	 */
	public static ItemStack getAmmoItemStack(Ammo ammo) {
		return ItemFact.getAmmo(ammo);
	}
	/**
	 * Returns all ammo registered
	 * @return
	 */
	public static Collection<Ammo> getAllAmmoTypes(){
		return Main.ammoRegister.values();
	}
	/**
	 * Returns all guns regisered
	 * @return
	 */
	public static Collection<Gun> getAllGunTypes(){
		return Main.gunRegister.values();
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
			WeaponType type, boolean hasIronSights, Ammo ammotype, double acc,
			int swayMultiplier, int maxBullets, float damage, int gunDurability,double cost) {
	return createSimpleGun(name, mat, data, type, hasIronSights, ammotype, acc, swayMultiplier, maxBullets, damage,false, gunDurability,cost,WeaponSounds.GUN_MEDIUM);
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
			WeaponType type, boolean hasIronSights, Ammo ammotype, double acc,
			int swayMultiplier, int maxBullets, float damage,boolean isAutomatic, int gunDurability, double cost, WeaponSounds sound) {
		MaterialStorage mm = MaterialStorage.getMS(mat, data);
		Gun g = new DefaultGun(name, mm, type, hasIronSights, ammotype, acc,
				swayMultiplier, maxBullets, damage,isAutomatic, gunDurability,sound,cost);
		Main.gunRegister.put(mm, g);
		return g;
	}
	

	/**
	 * Returns if the item is a misc itemstack.
	 * @param is
	 * @return
	 */
	public boolean isMiscItem(ItemStack is) {
		return (is != null && Main.miscRegister.containsKey(MaterialStorage.getMS(is.getType(), (int) is.getDurability())));
	}

	/**
	 * Returns the base object for the item.
	 * @param is
	 * @return
	 */
	public ArmoryBaseObject getMiscItem(ItemStack is) {
		return Main.miscRegister.get(MaterialStorage.getMS(is.getType(), (int) is.getDurability()));
	}

	/**
	 * Returns the gun instance for the item.
	 * @param is
	 * @return
	 */
	public Gun getGun(ItemStack is) {
		return Main.gunRegister.get(MaterialStorage.getMS(is.getType(), (int) is.getDurability()));
	}

	/**
	 * Returns the ammo instance for the item.
	 * @param is
	 * @return
	 */
	public Ammo getAmmo(ItemStack is) {
		return Main.ammoRegister.get(MaterialStorage.getMS(is.getType(), (int) is.getDurability()));
	}

	/**
	 * Returns if the item is a gun itemstack.
	 * @param is
	 * @return
	 */
	public boolean isGun(ItemStack is) {
		return (is != null && Main.gunRegister.containsKey(MaterialStorage.getMS(is.getType(), (int) is.getDurability())));
	}

	/**
	 * Returns if the item is a ammo itemstack.
	 * @param is
	 * @return
	 */
	public boolean isAmmo(ItemStack is) {
		return (is != null &&Main. ammoRegister.containsKey(MaterialStorage.getMS(is.getType(), (int) is.getDurability())));
	}


	/**
	 * Returns if the item is a IronSight itemstack. Useful for checking if the player is aiming.
	 * 
	 * If this returns true, the gun is in the player's OFF hand.
	 * @param is
	 * @return
	 */
	public boolean isIronSight(ItemStack is) {
		if (is != null && is.getType() == Main.guntype && is.getDurability() == (int) IronSightsToggleItem.getData())
			return true;
		return false;
	}

}
