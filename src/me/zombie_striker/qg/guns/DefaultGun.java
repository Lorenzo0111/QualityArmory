
package me.zombie_striker.qg.guns;

import me.zombie_striker.qg.ItemFact;
import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.MaterialStorage;
import me.zombie_striker.qg.ammo.*;
import me.zombie_striker.qg.guns.utils.GunUtil;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.guns.utils.WeaponType;
import me.zombie_striker.qg.handlers.IronSightsToggleItem;
import me.zombie_striker.qg.handlers.Update19OffhandChecker;
import me.zombie_striker.qg.handlers.gunvalues.ChargingHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DefaultGun implements Gun {

	private String name;
	private MaterialStorage id;
	private ItemStack[] ing;
	private WeaponType type;
	private boolean hasIronSights;
	private Ammo ammotype;
	private double acc;
	private double swaymultiplier;
	private int maxbull;
	private float damage;
	private int durib = 1000;
	private boolean isAutomatic;
	boolean supports18 = false;

	private List<String> extralore = null;
	private String displayname = null;

	private WeaponSounds weaponSounds;

	double cost = 100;

	private double delayBetweenShots = 0.25;

	private int shotsPerBullet = 1;

	private double reloadTime = 1.5;

	private ChargingHandler ch = null;

	private int maxDistance = 150;

	// This refers to the last time a gun was shot by a player, on a per-gun basis.
	// Doing this should prevent players from fast-switching to get around
	// bullet-delays
	public HashMap<UUID, Long> lastshot = new HashMap<>();

	public DefaultGun(String name, MaterialStorage id, WeaponType type, boolean h, Ammo am, double acc, double swaymult,
			int maxBullets, float damage, boolean isAutomatic, int durib, WeaponSounds ws, double cost) {
		this(name, id, type, h, am, acc, swaymult, maxBullets, damage, isAutomatic, durib, ws, null,
				ChatColor.GOLD + name, cost, null);
		this.ing = Main.getInstance().getIngredients(name);

	}

	public DefaultGun(String name, WeaponType type, boolean h, Ammo am, double acc, double swaymult, int maxBullets,
			float damage, boolean isAutomatic, int durib, WeaponSounds ws, double cost) {
		this(name, type, h, am, acc, swaymult, maxBullets, damage, isAutomatic, durib, ws, cost,
				Main.getInstance().getIngredients(name));
	}

	public DefaultGun(String name, WeaponType type, boolean h, Ammo am, double acc, double swaymult, int maxBullets,
			float damage, boolean isAutomatic, int durib, WeaponSounds ws, double cost, ItemStack[] ing) {
		this.name = name;
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

		this.cost = cost;
		this.displayname = ChatColor.GOLD + name;
	}

	public DefaultGun(String name, MaterialStorage id, WeaponType type, boolean h, Ammo am, double acc, double swaymult,
			int maxBullets, float damage, boolean isAutomatic, int durib, WeaponSounds ws, double cost,
			ItemStack[] ing) {
		this(name, id, type, h, am, acc, swaymult, maxBullets, damage, isAutomatic, durib, ws, null,
				ChatColor.GOLD + name, cost, ing);
	}

	public DefaultGun(String name, MaterialStorage id, WeaponType type, boolean h, Ammo am, double acc, double swaymult,
			int maxBullets, float damage, boolean isAutomatic, int durib, WeaponSounds ws, List<String> extralore,
			String displayname, double cost, ItemStack[] ing) {
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

		this.cost = cost;

		this.extralore = extralore;
		this.displayname = ChatColor.translateAlternateColorCodes('&', displayname);
	}

	public double getReloadTime() {
		return reloadTime;
	}

	public double getReloadingTimeInSeconds() {
		return reloadTime;
	}

	public void setReloadingTimeInSeconds(double time) {
		this.reloadTime = time;
	}

	@Override
	public void setBulletsPerShot(int i) {
		this.shotsPerBullet = i;
	}

	@Override
	public int getBulletsPerShot() {
		return shotsPerBullet;
	}

	@Override
	public WeaponType getWeaponType() {
		return type;
	}

	@Override
	public double cost() {
		return cost;
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

	@Override
	public boolean shoot(Player player) {
		return DefaultGun.USE_THIS_INSTEAD_OF_INDEVIDUAL_SHOOT_METHODS(this, player, getSway());
	}

	@SuppressWarnings("deprecation")
	public static boolean USE_THIS_INSTEAD_OF_INDEVIDUAL_SHOOT_METHODS(Gun g, Player player, double acc) {
		boolean offhand = player.getInventory().getItemInHand().getDurability() == IronSightsToggleItem.getData();
		if ((!offhand && ItemFact.getAmount(player.getInventory().getItemInHand()) > 0)
				|| (offhand && Update19OffhandChecker.hasAmountOFfhandGreaterthan(player, 0))) {
			GunUtil.basicShoot(offhand, g, player, acc);
			return true;
		}
		return false;
	}

	@Override
	public int getMaxBullets() {
		return maxbull;
	}

	@Override
	public boolean playerHasAmmo(Player player) {
		if (hasUnlimitedAmmo())
			return true;
		if (getAmmoType()==null)
			return true;
		return GunUtil.hasAmmo(player, this);
	}

	@Override
	public void reload(Player player) {
		if (getChargingVal() == null || (!getChargingVal().isReloading(player)))
			GunUtil.basicReload(this, player, WeaponType.isUnlimited(type), reloadTime);
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
		return ammotype;
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
		if (id == null) {
			for (Entry<MaterialStorage, Gun> e : Main.gunRegister.entrySet()) {
				if (e.getValue() == this)
					return id = e.getKey();
			}
		}
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

	@Override
	public double getDelayBetweenShotsInSeconds() {
		return delayBetweenShots;
	}

	public void setDelayBetweenShots(double seconds) {
		this.delayBetweenShots = seconds;
	}

	@Override
	public HashMap<UUID, Long> getLastShotForGun() {
		return lastshot;
	}

	@Override
	public ChargingHandler getChargingVal() {
		return ch;
	}

	@Override
	public void setChargingHandler(ChargingHandler ch) {
		this.ch = ch;
	}

	@Override
	public int getMaxDistance() {
		return maxDistance;
	}

	public void setMaxDistance(int a) {
		this.maxDistance = a;
	}

	@Override
	public boolean is18Support() {
		return supports18;
	}

	@Override
	public void set18Supported(boolean b) {
		supports18=b;
	}

}
