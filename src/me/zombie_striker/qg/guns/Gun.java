package me.zombie_striker.qg.guns;

import me.zombie_striker.qg.ArmoryBaseObject;
import me.zombie_striker.qg.ItemFact;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.MaterialStorage;
import me.zombie_striker.qg.ammo.*;
import me.zombie_striker.qg.api.QAWeaponPrepareShootEvent;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.guns.projectiles.ProjectileManager;
import me.zombie_striker.qg.guns.projectiles.RealtimeCalculationProjectile;
import me.zombie_striker.qg.guns.utils.GunUtil;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.guns.utils.WeaponType;
import me.zombie_striker.qg.handlers.Update19OffhandChecker;
import me.zombie_striker.qg.handlers.chargers.ChargingHandler;
import me.zombie_striker.qg.handlers.reloaders.ReloadingHandler;
import me.zombie_striker.qg.miscitems.IronSightsToggleItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Gun implements ArmoryBaseObject, Comparable<Gun> {

	private String name;
	private MaterialStorage id;
	private ItemStack[] ing;
	private WeaponType type;
	private boolean hasIronSights;
	private int zoomLevel = 0;
	private Ammo ammotype;
	private double acc;
	private double swaymultiplier = 2;
	private int maxbull;
	private float damage;
	private int durib = 1000;
	private boolean isAutomatic;
	boolean supports18 = false;
	boolean nightVisionOnScope = false;

	private double headshotMultiplier = 2;

	private boolean isPrimaryWeapon = true;

	private List<String> extralore = null;
	private String displayname = null;

	private String weaponSounds;

	double cost = 100;

	private double delayBetweenShots = 0.25;

	private int shotsPerBullet = 1;
	private int firerate = 1;

	private double reloadTime = 1.5;

	private ChargingHandler ch = null;
	private ReloadingHandler rh = null;

	private int maxDistance = 150;

	private Particle particle = null;
	private double particle_r = 1;
	private double particle_g = 1;
	private double particle_b = 1;

	private int lightl = 20;

	private boolean enableMuzzleSmoke = false;

	public ChatColor glowEffect = null;

	public boolean unlimitedAmmo = false;

	RealtimeCalculationProjectile customProjectile = null;
	double velocity = 2;
	double explosionRadius = 10;
	double recoil = 1;

	// This refers to the last time a gun was shot by a player, on a per-gun basis.
	// Doing this should prevent players from fast-switching to get around
	// bullet-delays
	public HashMap<UUID, Long> lastshot = new HashMap<>();

	public void copyFrom(Gun g) {
		this.ing = g.ing;
		this.type = g.type;
		this.hasIronSights = g.hasIronSights;
		this.zoomLevel = g.zoomLevel;
		this.ammotype = g.ammotype;
		this.acc = g.acc;
		this.swaymultiplier = g.swaymultiplier;
		this.maxbull = g.maxbull;
		this.damage = g.damage;
		this.durib = g.durib;
		this.isAutomatic = g.isAutomatic;
		this.supports18 = g.supports18;
		this.nightVisionOnScope = g.nightVisionOnScope;
		this.headshotMultiplier = g.headshotMultiplier;
		this.isPrimaryWeapon = g.isPrimaryWeapon;
		this.explosionRadius = g.explosionRadius;
		this.extralore = g.extralore;
		// this.displayname = displayname;
		this.weaponSounds = g.weaponSounds;
		this.cost = g.cost;
		this.delayBetweenShots = g.delayBetweenShots;
		this.shotsPerBullet = g.shotsPerBullet;
		this.firerate = g.firerate;
		this.ch = g.ch;
		this.rh = g.rh;
		this.maxDistance = g.maxbull;
		this.particle = g.particle;
		this.particle_r = g.particle_r;
		this.particle_g = g.particle_g;
		this.particle_b = g.particle_b;
		this.lightl = g.lightl;
		this.enableMuzzleSmoke = g.enableMuzzleSmoke;
		this.glowEffect = g.glowEffect;
		this.unlimitedAmmo = g.unlimitedAmmo;
		this.customProjectile = g.customProjectile;
		this.velocity = g.velocity;
		this.recoil = g.recoil;

	}

	/*
	 * @Deprecated public Gun(String name, MaterialStorage id, WeaponType type,
	 * boolean h, Ammo am, double acc, double swaymult, int maxBullets, float
	 * damage, boolean isAutomatic, int durib, WeaponSounds ws, double cost) {
	 * this(name, id, type, h, am, acc, swaymult, maxBullets, damage, isAutomatic,
	 * durib, ws, null, ChatColor.GOLD + name, cost, null); this.ing =
	 * Main.getInstance().getIngredients(name);
	 * 
	 * }
	 * 
	 * @Deprecated public Gun(String name, WeaponType type, boolean h, Ammo am,
	 * double acc, double swaymult, int maxBullets, float damage, boolean
	 * isAutomatic, int durib, WeaponSounds ws, double cost) { this(name, type, h,
	 * am, acc, swaymult, maxBullets, damage, isAutomatic, durib, ws, cost,
	 * Main.getInstance().getIngredients(name)); }
	 */

	@Deprecated
	public Gun(String name, WeaponType type, boolean h, Ammo am, double acc, double swaymult, int maxBullets,
			float damage, boolean isAutomatic, int durib, WeaponSounds ws, double cost, ItemStack[] ing) {
		this(name, type, h, am, acc, swaymult, maxBullets, damage, isAutomatic, durib, ws.getSoundName(), cost, ing);
	}

	@Deprecated
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

	@Deprecated
	public Gun(String name, MaterialStorage id, WeaponType type, boolean h, Ammo am, double acc, double swaymult,
			int maxBullets, float damage, boolean isAutomatic, int durib, WeaponSounds ws, double cost,
			ItemStack[] ing) {
		this(name, id, type, h, am, acc, swaymult, maxBullets, damage, isAutomatic, durib, ws, null,
				ChatColor.GOLD + name, cost, ing);
	}

	@Deprecated
	public Gun(String name, MaterialStorage id, WeaponType type, boolean h, Ammo am, double acc, double swaymult,
			int maxBullets, float damage, boolean isAutomatic, int durib, WeaponSounds ws, List<String> extralore,
			String displayname, double cost, ItemStack[] ing) {
		this(displayname, id, type, h, am, acc, swaymult, maxBullets, damage, isAutomatic, durib, ws.getSoundName(),
				extralore, displayname, cost, ing);
	}

	@Deprecated
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

	public Gun(String name, MaterialStorage id) {
		this.name = name;
		this.id = id;
	}

	public void setIngredients(ItemStack[] ing) {
		this.ing = ing;
	}

	public void setAutomatic(boolean automatic) {
		this.isAutomatic = automatic;
	}

	public void setHasIronsights(boolean b) {
		this.hasIronSights = b;
	}

	public void setDamage(float damage) {
		this.damage = damage;
	}

	public void setPrice(double price) {
		this.cost = price;
	}

	public void setDuribility(int durib) {
		this.durib = durib;
	}

	public void setMaxBullets(int amount) {
		this.maxbull = amount;
	}

	public void setSway(double sway) {
		this.acc = sway;
	}

	public void setSwayMultiplier(double multiplier) {
		this.swaymultiplier = multiplier;
	}

	public void setHeadshotMultiplier(double dam) {
		headshotMultiplier = dam;
	}

	public double getHeadshotMultiplier() {
		return headshotMultiplier;
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

	public double getRecoil() {
		return recoil;
	}

	public void setRecoil(double d) {
		this.recoil = d;
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

	public void setNightVision(boolean nightVisionOnScope) {
		this.nightVisionOnScope = nightVisionOnScope;
	}

	public void setAmmo(Ammo ammo) {
		this.ammotype = ammo;
	}

	public boolean hasnightVision() {
		return nightVisionOnScope;
	}

	public boolean usesCustomProjctiles() {
		return customProjectile != null;
	}

	public void setCustomProjectile(String key) {
		this.customProjectile = ProjectileManager.getHandler(key);
	}

	public RealtimeCalculationProjectile getCustomProjectile() {
		return customProjectile;
	}

	public void setRealtimeVelocity(double velocity) {
		this.velocity = velocity;
	}

	public double getVelocityForRealtimeCalculations() {
		return velocity;
	}

	public double getExplosionRadius() {
		return explosionRadius;
	}

	public void setExplosionRadius(double d) {
		this.explosionRadius = d;
	}

	public int getBulletsPerShot() {
		return shotsPerBullet;
	}

	public void setZoomLevel(int zoom) {
		this.zoomLevel = zoom;
	}

	public int getZoomWhenIronSights() {
		return zoomLevel;
	}

	public void setFireRate(int firerate) {
		this.firerate = firerate;
	}

	public int getFireRate() {
		return firerate;
	}

	public void setDisplayName(String displayname) {
		this.displayname = displayname;
	}

	public WeaponType getWeaponType() {
		return type;
	}

	public boolean isPrimaryWeapon() {
		return isPrimaryWeapon;
	}

	public void setIsPrimary(boolean isPrimary) {
		this.isPrimaryWeapon = isPrimary;
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

	public void setUnlimitedAmmo(boolean b) {
		this.unlimitedAmmo = b;
	}

	public boolean shoot(Player player) {
		return Gun.USE_THIS_INSTEAD_OF_INDEVIDUAL_SHOOT_METHODS(this, player, getSway());
	}

	@SuppressWarnings("deprecation")
	public static boolean USE_THIS_INSTEAD_OF_INDEVIDUAL_SHOOT_METHODS(Gun g, Player player, double acc) {
		boolean offhand = player.getInventory().getItemInHand().getDurability() == IronSightsToggleItem.getData();
		if ((!offhand && ItemFact.getAmount(player.getInventory().getItemInHand()) > 0)
				|| (offhand && Update19OffhandChecker.hasAmountOFfhandGreaterthan(player, 0))) {
			QAWeaponPrepareShootEvent shootevent = new QAWeaponPrepareShootEvent(player, g);
			Bukkit.getPluginManager().callEvent(shootevent);
			if (shootevent.isCanceled())
				return false;
			GunUtil.basicShoot(offhand, g, player, acc);
			return true;
		}
		return false;
	}

	public int getMaxBullets() {
		return maxbull;
	}

	public boolean playerHasAmmo(Player player) {
		if (player.getGameMode() == GameMode.CREATIVE)
			return true;
		if (hasUnlimitedAmmo())
			return true;
		if (getAmmoType() == null)
			return true;
		return GunUtil.hasAmmo(player, this);
	}

	public void setSound(WeaponSounds sound) {
		setSound(sound.getSoundName());
	}

	public void setSound(String sound) {
		this.weaponSounds = sound;
	}

	public void reload(Player player) {
		if (getChargingVal() == null || (getReloadingingVal() == null || !getReloadingingVal().isReloading(player)))
			GunUtil.basicReload(this, player, unlimitedAmmo, reloadTime);
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
		if (unlimitedAmmo)
			return true;
		return ammotype == null;
	}

	public double getSway() {
		// TODO Auto-generated method stub
		return acc;
	}

	public double getMovementMultiplier() {
		// TODO Auto-generated method stub
		return swaymultiplier;
	}

	@Override
	public MaterialStorage getItemData() {
		if (id == null) {
			for (Entry<MaterialStorage, Gun> e : QAMain.gunRegister.entrySet()) {
				if (e.getValue() == this)
					return id = e.getKey();
			}
		}
		return id;
	}

	public String getWeaponSound() {
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

	public ReloadingHandler getReloadingingVal() {
		return rh;
	}

	public void setReloadingHandler(ReloadingHandler rh) {
		this.rh = rh;
	}

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
		if (QAMain.orderShopByPrice) {
			return (int) (this.cost - arg0.cost);
		}
		return this.displayname.compareTo(arg0.displayname);
	}

	@Override
	public void onRMB(PlayerInteractEvent e, ItemStack usedItem) {
		onClick(e, usedItem, (QAMain.reloadOnFOnly || !QAMain.SWAP_RMB_WITH_LMB));
	}

	@Override
	public void onLMB(PlayerInteractEvent e, ItemStack usedItem) {
		// if (((e.getAction() == Action.LEFT_CLICK_AIR
		// || e.getAction() == Action.LEFT_CLICK_BLOCK) == SWAP_RMB_WITH_LMB)
		// || (SWAP_RMB_WITH_LMB && g != null && g.isAutomatic() &&
		// e.getPlayer().isSneaking())) {
		/*
		 * if(e.getAction().name().contains("RIGHT") && e.getClickedBlock() != null &&
		 * interactableBlocks.contains(e.getClickedBlock().getType())) {
		 * e.setCancelled(false); return; }
		 */
		// TODO: Verify If the player is shifting and rightclick, the gun will still
		// fire. The player has to be standing (non-sneak) in order to interact with
		// interactable blocks.
		onClick(e, usedItem, QAMain.SWAP_RMB_WITH_LMB);
	}

	@SuppressWarnings("deprecation")
	public void onClick(final PlayerInteractEvent e, ItemStack usedItem, boolean fire) {
		QAMain.DEBUG("CLICKED GUN " + getName());

		if (!e.getPlayer().hasPermission("qualityarmory.usegun")) {
			e.getPlayer().sendMessage(QAMain.S_NOPERM);
			return;
		}

		if (QAMain.enableVisibleAmounts) {
			QAMain.DEBUG("UNSUPPORTED - Enable visable ammo amount ID check");
			boolean validcheck2 = false;
			try {
				if (QAMain.isVersionHigherThan(1, 9)) {
					UUID.fromString(usedItem.getItemMeta().getLocalizedName());
					QAMain.DEBUG("Gun-Validation check - 1");
				} else {
					validcheck2 = true;
				}
			} catch (Error | Exception e34) {
				validcheck2 = true;
			}
			if (validcheck2) {
				if (QAMain.isVersionHigherThan(1, 9)) {
					if (!usedItem.getItemMeta().hasDisplayName() || !usedItem.getItemMeta().hasLore()) {
						ItemStack is = ItemFact.getGun(MaterialStorage.getMS(usedItem));
						e.setCancelled(true);
						e.getPlayer().setItemInHand(is);
						QAMain.DEBUG("Gun-Validation check - 2");
						return;
					}
				}
			}
		}

		QAMain.DEBUG("Dups check");
		QAMain.getInstance().checkforDups(e.getPlayer(), usedItem);

		
		ItemStack offhandItem = Update19OffhandChecker.getItemStackOFfhand(e.getPlayer());
		boolean offhand = offhandItem!=null && offhandItem.equals(usedItem);

		// AttachmentBase attachment =
		// me.zombie_striker.qg.api.QualityArmory.getGunWithAttchments(usedItem);
		/*
		 * Gun g = QualityArmory.getGun(usedItem); if (g == null) g =
		 * attachment.getBaseGun();
		 */
		QAMain.DEBUG("Made it to gun/attachment check : " + getName());
		if (QAMain.enableInteractChests) {
			if (e.getClickedBlock() != null && (e.getClickedBlock().getType() == Material.CHEST
					|| e.getClickedBlock().getType() == Material.TRAPPED_CHEST)) {
				QAMain.DEBUG("Chest interactable check has return true!");
				return;
			}
			// Return with no shots if EIC is enabled for chests.
		}

		e.setCancelled(true);
		if (fire) {
			QAMain.DEBUG("Fire mode called");
			if (!QAMain.enableDurability || ItemFact.getDamage(usedItem) > 0) {
				// if (allowGunsInRegion(e.getPlayer().getLocation())) {
				try {
					if (e.getHand() == EquipmentSlot.OFF_HAND) {
						QAMain.DEBUG("OffHandChecker was disabled for shooting!");
						return;
					}
				} catch (Error | Exception e4) {
				}

				if (isAutomatic() && GunUtil.rapidfireshooters.containsKey(e.getPlayer().getUniqueId())) {
					GunUtil.rapidfireshooters.remove(e.getPlayer().getUniqueId()).cancel();
					if (QAMain.enableReloadWhenOutOfAmmo)
						if (ItemFact.getAmount(usedItem) <= 0) {
							if (offhand) {
								e.getPlayer().setItemInHand(e.getPlayer().getInventory().getItemInOffHand());
								e.getPlayer().getInventory().setItemInOffHand(null);
								usedItem = e.getPlayer().getItemInHand();
								offhand = false;
							}
							if (QAMain.allowGunReload) {
								QualityArmory.sendHotbarGunAmmoCount(e.getPlayer(), this, usedItem,
										((getMaxBullets() != ItemFact.getAmount(usedItem))
												&& GunUtil.hasAmmo(e.getPlayer(), this)));
								if (playerHasAmmo(e.getPlayer())) {
									QAMain.DEBUG("Trying to reload WITH AUTORELOAD. player has ammo");
									reload(e.getPlayer());

								} else {
									if (QAMain.showOutOfAmmoOnItem) {
										// ItemFact.addOutOfAmmoToDisplayname(g, e.getPlayer(), usedItem, slot);
										QAMain.DEBUG("UNSUPPORTED: Out of ammo displayed on item");
									}
									QAMain.DEBUG("Trying to reload WITH AUTORELOAD. player DOES NOT have ammo");
								}
							}
							return;
						}
				} else {
					if (QAMain.enableReloadWhenOutOfAmmo)
						if (ItemFact.getAmount(usedItem) <= 0) {
							if (offhand) {
								e.getPlayer().setItemInHand(e.getPlayer().getInventory().getItemInOffHand());
								e.getPlayer().getInventory().setItemInOffHand(null);
								usedItem = e.getPlayer().getItemInHand();
								offhand = false;
							}
							if (QAMain.allowGunReload) {
								QualityArmory.sendHotbarGunAmmoCount(e.getPlayer(), this, usedItem,
										((getMaxBullets() != ItemFact.getAmount(usedItem))
												&& GunUtil.hasAmmo(e.getPlayer(), this)));
								if (playerHasAmmo(e.getPlayer())) {
									QAMain.DEBUG("Trying to reload WITH AUTORELOAD. player has ammo");
									reload(e.getPlayer());

								} else {
									QAMain.DEBUG("Trying to reload WITH AUTORELOAD. player DOES NOT have ammo");
								}
							}
							return;
						}
					shoot(e.getPlayer());
					if (QAMain.enableDurability) {
						if (QualityArmory.isIronSights(e.getPlayer().getItemInHand())) {
							Update19OffhandChecker.setOffhand(e.getPlayer(), ItemFact.damage(this, usedItem));
						} else {
							e.getPlayer().setItemInHand(ItemFact.damage(this, usedItem));
						}
					}

				}

				QualityArmory.sendHotbarGunAmmoCount(e.getPlayer(), this, usedItem, false);
				return;
				/*
				 * } else { Main.DEBUG("Worldguard region canceled the event"); }
				 */
				// sendHotbarGunAmmoCount(e.getPlayer(), g, attachment, usedItem, false);
				// TODO: Verify that the gun is in the main hand.
				// Shouldn't work for offhand, but it should still
				// be checked later.
			}
			QAMain.DEBUG("End of fire mode check called");

		} else {
			QAMain.DEBUG("Non-Fire mode activated");

			if (QAMain.enableIronSightsON_RIGHT_CLICK) {
				if (!Update19OffhandChecker.supportOffhand(e.getPlayer())) {
					QAMain.enableIronSightsON_RIGHT_CLICK = false;
					QAMain.DEBUG("Offhand checker returned false for the player. Disabling ironsights");
					return;
				}
				// Rest should be okay
				if (hasIronSights()) {
					try {

						if (e.getPlayer().getItemInHand().getItemMeta().getDisplayName()
								.contains(QAMain.S_RELOADING_MESSAGE)) {
							QAMain.DEBUG("Reloading message 1!");
							return;
						}
						//ItemStack offhandItem = Update19OffhandChecker.getItemStackOFfhand(e.getPlayer());
						if (offhandItem!= null) {
							e.getPlayer().getInventory()
									.addItem(offhandItem);
							Update19OffhandChecker.setOffhand(e.getPlayer(), null);
						}

						ItemStack tempremove = null;
						if (e.getPlayer().getInventory().getItemInOffHand() != null)
							tempremove = e.getPlayer().getInventory().getItemInOffHand();
						e.getPlayer().getInventory().setItemInOffHand(e.getPlayer().getInventory().getItemInMainHand());
						if (tempremove != null) {
							e.getPlayer().getInventory().setItemInMainHand(QualityArmory.getIronSightsItemStack());

							QAMain.toggleNightvision(e.getPlayer(), this, true);
							QAMain.DEBUG("Toggle Night vision on right click");
							final Gun checkTo = QualityArmory
									.getGun(Update19OffhandChecker.getItemStackOFfhand(e.getPlayer()));
							new BukkitRunnable() {

								@Override
								public void run() {
									if (!e.getPlayer().isOnline()) {
										QAMain.DEBUG("Canceling since player is offline");
										cancel();
										return;
									}
									Gun g = null;
									if (!QualityArmory.isIronSights(e.getPlayer().getItemInHand())
											|| (g = QualityArmory.getGun(
													Update19OffhandChecker.getItemStackOFfhand(e.getPlayer()))) == null
											|| g != checkTo) {
										QAMain.toggleNightvision(e.getPlayer(), checkTo, false);
										QAMain.DEBUG(
												"Removing nightvision since either the main hand is not ironsights/ offhand gun is null. : "
														+ (!QualityArmory.isIronSights(e.getPlayer().getItemInHand()))
														+ " "
														+ ((g = QualityArmory.getGun(Update19OffhandChecker
																.getItemStackOFfhand(e.getPlayer()))) == null)
														+ " " + (g != checkTo));
										cancel();
										return;
									}

								}
							}.runTaskTimer(QAMain.getInstance(), 20, 20);
						}

						QualityArmory.sendHotbarGunAmmoCount(e.getPlayer(), this, usedItem, false);
					} catch (Error e2) {
						Bukkit.broadcastMessage(QAMain.prefix
								+ "Ironsights not compatible for versions lower than 1.8. The server owner should set EnableIronSights to false in the plugin's config");
					}
				} else {
					/*
					 * if (!Main.enableDurability || ItemFact.getDamage(usedItem) > 0) { // if
					 * (allowGunsInRegion(e.getPlayer().getLocation())) { g.shoot(e.getPlayer(),
					 * attachment); if (Main.enableDurability) if (offhand) {
					 * e.getPlayer().getInventory().setItemInOffHand(ItemFact.damage(g, usedItem));
					 * } else { e.getPlayer().setItemInHand(ItemFact.damage(g, usedItem)); } // }
					 * QualityArmory.sendHotbarGunAmmoCount(e.getPlayer(), g, attachment, usedItem,
					 * false); // TODO: Verify that the gun is in the main // hand. // Shouldn't
					 * work for offhand, but it should // still // be checked later. }
					 */
				}

				QAMain.DEBUG("Ironsights on RMB finished");
			} else {
				QAMain.DEBUG("Reload called");
				if (e.getClickedBlock() != null && QAMain.interactableBlocks.contains(e.getClickedBlock().getType())) {
					e.setCancelled(false);
				} else {
					if (QAMain.allowGunReload) {
						QualityArmory.sendHotbarGunAmmoCount(e.getPlayer(), this, usedItem,
								((getMaxBullets() != ItemFact.getAmount(usedItem))
										&& GunUtil.hasAmmo(e.getPlayer(), this)));
						if (playerHasAmmo(e.getPlayer())) {
							QAMain.DEBUG("Trying to reload. player has ammo");
							reload(e.getPlayer());
						} else {
							QAMain.DEBUG("Trying to reload. player DOES NOT have ammo");
						}
					}
				}
			}

		}
		QAMain.DEBUG("Reached end for gun-check!");
	}

	@Override
	public ItemStack getItemStack() {
		return ItemFact.getGun(this);
	}

}
