package me.zombie_striker.qg.guns;

import me.zombie_striker.qg.ItemFact;
import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.MaterialStorage;
import me.zombie_striker.qg.ammo.Ammo;
import me.zombie_striker.qg.ammo.AmmoType;
import me.zombie_striker.qg.handlers.IronSightsToggleItem;
import me.zombie_striker.qg.handlers.Update19OffhandChecker;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DefaultGun implements Gun {

	private String name;
	private MaterialStorage id;
	private ItemStack[] ing;
	private WeaponType type;
	private boolean hasIronSights;
	private AmmoType ammotype;
	private double acc;
	private double swaymultiplier;
	private int maxbull;
	private float damage;
	private int durib = 1000;
	private boolean isAutomatic;;
	
	private List<String> extralore=null;
	private String displayname=null;
	
	private WeaponSounds weaponSounds;

	public DefaultGun(String name, MaterialStorage id, WeaponType type, boolean h, AmmoType am, double acc,
			double swaymult, int maxBullets, float damage, boolean isAutomatic, int durib, WeaponSounds ws) {
		this.name = name;
		this.id = id;
		this.type = type;
		this.hasIronSights = h;
		this.ammotype = am;
		this.ing = Main.getInstance().getIngredients(name);
		this.acc = acc;
		this.maxbull = maxBullets;
		this.damage = damage;
		this.durib = durib;
		this.swaymultiplier = swaymult;
		this.isAutomatic = isAutomatic;
		this.weaponSounds = ws;
		
		this.displayname = ChatColor.GOLD + name;
	}

	public DefaultGun(String name, MaterialStorage id, WeaponType type, boolean h, AmmoType am, double acc,
			double swaymult, int maxBullets, float damage, boolean isAutomatic, int durib, WeaponSounds ws,  ItemStack[] ing) {
		this.name = name;
		this.id = id;
		this.type = type;
		this.hasIronSights = h;
		this.ammotype = am;
		this.ing = ing;
		this.acc = acc;
		this.maxbull = maxBullets;
		this.damage = damage;
		this.durib = durib;
		this.swaymultiplier = swaymult;
		this.isAutomatic = isAutomatic;
		this.weaponSounds = ws;
		
		
		this.displayname = ChatColor.GOLD + name;
	}

	public DefaultGun(String name, MaterialStorage id, WeaponType type, boolean h, AmmoType am, double acc,
			double swaymult, int maxBullets, float damage, boolean isAutomatic, int durib, WeaponSounds ws,List<String> extralore,String displayname, ItemStack[] ing) {
		this.name = name;
		this.id = id;
		this.type = type;
		this.hasIronSights = h;
		this.ammotype = am;
		this.ing = ing;
		this.acc = acc;
		this.maxbull = maxBullets;
		this.damage = damage;
		this.durib = durib;
		this.swaymultiplier = swaymult;
		this.isAutomatic = isAutomatic;
		this.weaponSounds = ws;
		
		this.extralore = extralore;
		this.displayname = ChatColor.translateAlternateColorCodes('&',displayname);
	}

	public boolean isAutomatic() {
		return isAutomatic;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ItemStack[] getIngredients() {
		return ing;
	}

	@Override
	public int getCraftingReturn() {
		return 1;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void shoot(Player player) {
		boolean offhand = player.getInventory().getItemInHand().getDurability() == IronSightsToggleItem.getData();
		if ((!offhand && ItemFact.getAmount(player.getInventory().getItemInHand()) > 0)
				|| (offhand && Update19OffhandChecker.hasAmountOFfhandGreaterthan(player, 0))) {
			GunUtil.basicShoot(offhand, this, player, acc);
		}
	}

	@Override
	public int getMaxBullets() {
		return maxbull;
	}

	@Override
	public boolean playerHasAmmo(Player player) {
		if (hasUnlimitedAmmo())
			return true;
		return GunUtil.hasAmmo(player, this);
	}

	@Override
	public void reload(Player player) {
		GunUtil.basicReload(this, player, WeaponType.isUnlimited(type));
	}

	@Override
	public double getDamage() {
		return damage;
	}

	@Override
	public int getDurability() {
		return this.durib;
	}

	@Override
	public Ammo getAmmoType() {
		return ammotype.getType();
	}

	@Override
	public boolean hasIronSights() {
		return hasIronSights;
	}

	@Override
	public boolean hasUnlimitedAmmo() {
		return WeaponType.isUnlimited(type);
	}

	@Override
	public double getSway() {
		// TODO Auto-generated method stub
		return acc;
	}

	@Override
	public double getMovementMultiplier() {
		// TODO Auto-generated method stub
		return swaymultiplier;
	}

	@Override
	public MaterialStorage getItemData() {
		return id;
	}

	@Override
	public WeaponSounds getWeaponSound() {
		return weaponSounds;
	}

	@Override
	public List<String> getCustomLore() {
		return extralore;
	}

	@Override
	public String getDisplayName() {
		return displayname;
	}

}
