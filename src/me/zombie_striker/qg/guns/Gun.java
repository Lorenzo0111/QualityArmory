
package me.zombie_striker.qg.guns;

import me.zombie_striker.qg.ArmoryBaseObject;
import me.zombie_striker.qg.ItemFact;
import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.MaterialStorage;
import me.zombie_striker.qg.ammo.*;
import me.zombie_striker.qg.attachments.AttachmentBase;
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

public class Gun implements ArmoryBaseObject, Comparable<Gun> {

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

	private String weaponSounds;

	double cost = 100;

	private double delayBetweenShots = 0.25;

	private int shotsPerBullet = 1;

	private double reloadTime = 1.5;

	private ChargingHandler ch = null;

	private int maxDistance = 150;

	private Particle particle = null;
	private double particle_r = 1;
	private double particle_g = 1;
	private double particle_b = 1;
	
	private int lightl = 20;

	private boolean enableMuzzleSmoke = false;

	public ChatColor glowEffect = null;

	public boolean unlimitedAmmo = false;

	// This refers to the last time a gun was shot by a player, on a per-gun basis.
	// Doing this should prevent players from fast-switching to get around
	// bullet-delays
	public HashMap<UUID, Long> lastshot = new HashMap<>();

	public Gun(String name, MaterialStorage id, WeaponType type, boolean h, Ammo am, double acc, double swaymult,
			int maxBullets, float damage, boolean isAutomatic, int durib, WeaponSounds ws, double cost) {
		this(name, id, type, h, am, acc, swaymult, maxBullets, damage, isAutomatic, durib, ws, null,
				ChatColor.GOLD + name, cost, null);
		this.ing = Main.getInstance().getIngredients(name);

	}

	public Gun(String name, WeaponType type, boolean h, Ammo am, double acc, double swaymult, int maxBullets,
			float damage, boolean isAutomatic, int durib, WeaponSounds ws, double cost) {
		this(name, type, h, am, acc, swaymult, maxBullets, damage, isAutomatic, durib, ws, cost,
				Main.getInstance().getIngredients(name));
	}

	public Gun(String name, WeaponType type, boolean h, Ammo am, double acc, double swaymult, int maxBullets,
			float damage, boolean isAutomatic, int durib, WeaponSounds ws, double cost, ItemStack[] ing) {
		this(name, type, h, am, acc, swaymult, maxBullets, damage, isAutomatic, durib, ws.getName(), cost, ing);
	}

	public Gun(String name, WeaponType type, boolean h, Ammo am, double acc, double swaymult, int maxBullets,
			float damage, boolean isAutomatic, int durib, String ws, double cost, ItemStack[] ing) {
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

	public Gun(String name, MaterialStorage id, WeaponType type, boolean h, Ammo am, double acc, double swaymult,
			int maxBullets, float damage, boolean isAutomatic, int durib, WeaponSounds ws, double cost,
			ItemStack[] ing) {
		this(name, id, type, h, am, acc, swaymult, maxBullets, damage, isAutomatic, durib, ws, null,
				ChatColor.GOLD + name, cost, ing);
	}

	public Gun(String name, MaterialStorage id, WeaponType type, boolean h, Ammo am, double acc, double swaymult,
			int maxBullets, float damage, boolean isAutomatic, int durib, WeaponSounds ws, List<String> extralore,
			String displayname, double cost, ItemStack[] ing) {
		this(displayname, id, type, h, am, acc, swaymult, maxBullets, damage, isAutomatic, durib, ws.getName(),
				extralore, displayname, cost, ing);
	}

	public Gun(String name, MaterialStorage id, WeaponType type, boolean h, Ammo am, double acc, double swaymult,
			int maxBullets, float damage, boolean isAutomatic, int durib, String ws, List<String> extralore,
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

	public ChatColor getGlow() {
		return glowEffect;
	}

	public void setLightOnShoot(int level) {
	lightl = level;
}
	public int getLightOnShoot() {
		return lightl;
	}
	/**
	 * Sets the glow for the item. Null to disable the glow.
	 */
	public void setGlow(ChatColor glow) {
		this.glowEffect = glow;
	}

	public double getReloadTime() {
		return reloadTime;
	}

	public void setReloadingTimeInSeconds(double time) {
		this.reloadTime = time;
	}

	public void setBulletsPerShot(int i) {
		this.shotsPerBullet = i;
	}

	public int getBulletsPerShot() {
		return shotsPerBullet;
	}

	public WeaponType getWeaponType() {
		return type;
	}

	public double cost() {
		return cost;
	}

	public boolean isAutomatic() {
		return isAutomatic;
	}

	public String getName() {
		return name;
	}

	public ItemStack[] getIngredients() {
		return ing;
	}

	public int getCraftingReturn() {
		return 1;
	}

	public void setUnlimitedAmmo(boolean b) {
		this.unlimitedAmmo = b;
	}

	public boolean shoot(Player player, AttachmentBase attachmentBase) {
		return Gun.USE_THIS_INSTEAD_OF_INDEVIDUAL_SHOOT_METHODS(this, attachmentBase, player, getSway());
	}

	@SuppressWarnings("deprecation")
	public static boolean USE_THIS_INSTEAD_OF_INDEVIDUAL_SHOOT_METHODS(Gun g, AttachmentBase attachmentBase,
			Player player, double acc) {
		boolean offhand = player.getInventory().getItemInHand().getDurability() == IronSightsToggleItem.getData();
		if ((!offhand && ItemFact.getAmount(player.getInventory().getItemInHand()) > 0)
				|| (offhand && Update19OffhandChecker.hasAmountOFfhandGreaterthan(player, 0))) {
			GunUtil.basicShoot(offhand, g, attachmentBase, player, acc);
			return true;
		}
		return false;
	}

	public int getMaxBullets() {
		return maxbull;
	}

	public boolean playerHasAmmo(Player player) {
		if (hasUnlimitedAmmo())
			return true;
		if (getAmmoType() == null)
			return true;
		return GunUtil.hasAmmo(player, this);
	}

	public void reload(Player player, AttachmentBase attachmentBase) {
		if (getChargingVal() == null || (!getChargingVal().isReloading(player))) 
			GunUtil.basicReload(this, attachmentBase, player, WeaponType.isUnlimited(type), reloadTime);
	}

	public double getDamage() {
		return damage;
	}

	public int getDurability() {
		return this.durib;
	}

	public Ammo getAmmoType() {
		return ammotype;
	}

	public boolean hasIronSights() {
		return hasIronSights;
	}

	public boolean hasUnlimitedAmmo() {
		if(unlimitedAmmo)
			return true;
		return WeaponType.isUnlimited(type);
	}

	public double getSway() {
		// TODO Auto-generated method stub
		return acc;
	}

	public double getMovementMultiplier() {
		// TODO Auto-generated method stub
		return swaymultiplier;
	}

	public MaterialStorage getItemData() {
		if (id == null) {
			for (Entry<MaterialStorage, Gun> e : Main.gunRegister.entrySet()) {
				if (e.getValue() == this)
					return id = e.getKey();
			}
		}
		return id;
	}

	public String getWeaponSound() {
		return weaponSounds;
	}

	public List<String> getCustomLore() {
		return extralore;
	}

	public String getDisplayName() {
		return displayname;
	}

	public double getDelayBetweenShotsInSeconds() {
		return delayBetweenShots;
	}

	public void setDelayBetweenShots(double seconds) {
		this.delayBetweenShots = seconds;
	}

	public HashMap<UUID, Long> getLastShotForGun() {
		return lastshot;
	}

	public ChargingHandler getChargingVal() {
		return ch;
	}

	public void setChargingHandler(ChargingHandler ch) {
		this.ch = ch;
	}

	public int getMaxDistance() {
		return maxDistance;
	}

	public void setMaxDistance(int a) {
		this.maxDistance = a;
	}

	public boolean is18Support() {
		return supports18;
	}

	public void set18Supported(boolean b) {
		supports18 = b;
	}

	public Particle getParticle() {
		return particle;
	}

	public double getParticleR() {
		return particle_r;
	}

	public double getParticleG() {
		return particle_g;
	}

	public double getParticleB() {
		return particle_b;
	}

	public void setParticles(Particle p) {
		this.setParticles(p, 1, 1, 1);
	}

	public void setParticles(Particle p, double r, double g, double b) {
		particle = p;
		this.particle_r = r;
		this.particle_g = g;
		this.particle_b = b;
	}

	public boolean useMuzzleSmoke() {
		return enableMuzzleSmoke;
	}

	public void setUseMuzzleSmoke(boolean b) {
		this.enableMuzzleSmoke = b;
	}

	@Override
	public int compareTo(Gun arg0) {
		if(Main.orderShopByPrice) {
			return (int) (this.cost-arg0.cost);
		}
		return this.displayname.compareTo(arg0.displayname);
	}

}
