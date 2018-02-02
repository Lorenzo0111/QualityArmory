package me.zombie_striker.qg;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import me.zombie_striker.pluginconstructor.HotbarMessager;
import me.zombie_striker.qg.ammo.*;
import me.zombie_striker.qg.guns.*;
import me.zombie_striker.qg.guns.utils.GunUtil;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.guns.utils.WeaponType;
import me.zombie_striker.qg.handlers.*;
import me.zombie_striker.qg.miscitems.Grenades;
import me.zombie_striker.qg.miscitems.ThrowableItems;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.*;

public class Main extends JavaPlugin implements Listener {

	public static HashMap<MaterialStorage, Gun> gunRegister = new HashMap<>();
	public static HashMap<MaterialStorage, Ammo> ammoRegister = new HashMap<>();
	public static HashMap<MaterialStorage, ArmoryBaseObject> miscRegister = new HashMap<>();

	public static HashMap<UUID, List<BukkitTask>> reloadingTasks = new HashMap<UUID, List<BukkitTask>>();

	public static ArrayList<UUID> resourcepackReq = new ArrayList<>();

	private static Main main;

	private List<Material> interactableBlocks = new ArrayList<>();

	public static Main getInstance() {
		return main;
	}

	public static Object bulletTrail;

	private boolean shouldSend = true;
	public static boolean sendOnJoin = false;

	public static boolean enableDurability = false;
	public static boolean UnlimitedAmmoPistol = false;
	public static boolean UnlimitedAmmoRifle = false;
	public static boolean UnlimitedAmmoShotgun = false;
	public static boolean UnlimitedAmmoSMG = false;
	public static boolean UnlimitedAmmoRPG = false;
	public static boolean UnlimitedAmmoSniper = false;

	public static double bulletStep = 0.25;

	public static boolean blockbullet_leaves = false;
	public static boolean blockbullet_halfslabs = false;
	public static boolean blockbullet_door = false;
	public static boolean blockbullet_water = false;

	public static boolean overrideAnvil = false;
	public static boolean supportWorldGuard = false;
	public static boolean enableIronSightsON_RIGHT_CLICK = false;
	public static boolean enableBulletTrails = true;
	public static boolean enableVisibleAmounts = false;
	public static boolean reloadOnF = true;

	public static boolean enableExplosionDamage = false;

	public static boolean enableEconomy = false;

	public static boolean overrideURL = false;
	public static String url = "https://www.dropbox.com/s/uztx0depegg2hat/QualityArmory%201.5.zip?dl=1";

	public static String S_NOPERM = " You do not have permission to do that";
	public static String S_NORES1 = " You do not have the resoucepack";
	public static String S_NORES2 = " Accept the resoucepack to see correct textures";
	public static String S_ANVIL = " You do not have permission to use this armory bench. ShiftClick to access anvil.";
	public static String S_ITEM_BULLETS = "Bullets";
	public static String S_ITEM_DURIB = "Durability";
	public static String S_ITEM_DAMAGE = "Damage";
	public static String S_ITEM_AMMO = "Ammo";
	public static String S_ITEM_ING = "Ingredients";

	public static double smokeSpacing = 0.5;

	public static String prefix = ChatColor.GRAY + "[" + ChatColor.DARK_GREEN + "QualityArmory" + ChatColor.GRAY + "]"
			+ ChatColor.WHITE;

	public Inventory craftingMenu;
	public static String S_craftingBenchName = "Armory Bench";
	public static String S_missingIngredients = "You do not have all the materials needed to craft this";

	public Inventory shopMenu;
	public static String S_shopName = "Weapons Shop";
	public static String S_noMoney = "You do not have enough money to buy this";
	public static String S_noEcon = "ECONOMY NOT ENABLED. REPORT THIS TO THE OWNER!";

	public static Material guntype = Material.DIAMOND_HOE;

	public static CustomYml m;

	/**
	 * GUNLIST:
	 * 
	 * 2: P30 3 PKP 4 MP5K 5 AK47 6: AK 7 M16 8 Remmington 9 FNFal 10 RPG 11 UMP 12
	 * SW1911 13 M40 14 Ammo 556 15 9mm 16 buckshot 17 rocketRPG 18 Enfield 19 Henry
	 * 20 MouserC96
	 * 
	 * 22 Grenades
	 */

	@Override
	public void onDisable() {
		gunRegister.clear();
	}

	private boolean saveTheConfig = false;

	public Object a(String path, Object def) {
		if (getConfig().contains(path)) {
			return getConfig().get(path);
		}
		saveTheConfig = true;
		getConfig().set(path, def);
		return def;
	}

	@Override
	public void onEnable() {
		main = this;
		supportWorldGuard = Bukkit.getPluginManager().isPluginEnabled("WorldGuard");
		reloadVals();
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers())
					resourcepackReq.add(player.getUniqueId());
			}
		}.runTaskLater(this, 1);
		if (Bukkit.getPluginManager().getPlugin("PluginConstructorAPI") == null)
			// new DependencyDownloader(this, 276723);
			GithubDependDownloader.autoUpdate(this,
					new File(getDataFolder().getParentFile(), "PluginConstructorAPI.jar"), "ZombieStriker",
					"PluginConstructorAPI", "PluginConstructorAPI.jar");

		Bukkit.getPluginManager().registerEvents(this, this);
		Bukkit.getPluginManager().registerEvents(new AimManager(), this);

		try {
			Bukkit.getPluginManager().registerEvents(new Update19Events(), this);
		} catch (Exception | Error e) {
		}

		try {
			/* final Updater updater = */// new Updater(this, 278412, true);
			/*
			 * new BukkitRunnable() { public void run() { // TODO: Works well. Make changes
			 * for the updaters of // PixelPrinter and Music later. if
			 * (updater.updaterActive) updater.download(false); }
			 * }.runTaskTimerAsynchronously(this, 20 /* * 60 *, 20 * 60 * 5);
			 */
			GithubUpdater.autoUpdate(this, "ZombieStriker", "QualityArmory", "QualityArmory.jar");
		} catch (Exception e) {
		}

		Metrics metrics = new Metrics(this);

		// Optional: Add custom charts
		metrics.addCustomChart(new Metrics.SimplePie("GunCount", new java.util.concurrent.Callable<String>() {
			@Override
			public String call() throws Exception {
				return gunRegister.size() + "";
			}
		}));
	}

	public void reloadVals() {

		gunRegister.clear();
		ammoRegister.clear();
		miscRegister.clear();
		interactableBlocks.clear();

		m = new CustomYml(new File(getDataFolder(), "messages.yml"));
		S_ANVIL = (String) m.a("NoPermAnvilMessage", S_ANVIL);
		S_NOPERM = (String) m.a("NoPerm", S_NOPERM);
		S_craftingBenchName = (String) m.a("CraftingBenchName", S_craftingBenchName);
		S_missingIngredients = (String) m.a("Missing_Ingredients", S_missingIngredients);
		S_NORES1 = (String) m.a("NoResoucepackMessage1", S_NORES1);
		S_NORES2 = (String) m.a("NoResourcepackMessage2", S_NORES2);
		S_ITEM_AMMO = (String) m.a("Lore_Ammo", S_ITEM_AMMO);
		S_ITEM_BULLETS = (String) m.a("lore_bullets", S_ITEM_BULLETS);
		S_ITEM_DAMAGE = (String) m.a("Lore_Damage", S_ITEM_DAMAGE);
		S_ITEM_DURIB = (String) m.a("Lore_Durib", S_ITEM_DURIB);
		S_ITEM_ING = (String) m.a("Lore_ingredients", S_ITEM_ING);

		if (!new File(getDataFolder(), "config.yml").exists())
			saveDefaultConfig();

		shouldSend = (boolean) a("useDefaultResourcepack", true);
		UnlimitedAmmoPistol = (boolean) a("UnlimitedPistolAmmo", false);
		UnlimitedAmmoShotgun = (boolean) a("UnlimitedShotgunAmmo", false);
		UnlimitedAmmoRifle = (boolean) a("UnlimitedRifleAmmo", false);
		UnlimitedAmmoSMG = (boolean) a("UnlimitedSMGAmmo", false);
		UnlimitedAmmoSniper = (boolean) a("UnlimitedSniperAmmo", false);
		UnlimitedAmmoRPG = (boolean) a("UnlimitedRocketAmmo", false);
		enableDurability = (boolean) a("EnableWeaponDurability", false);

		bulletStep = (double) a("BulletDetection.step", 0.10);

		blockbullet_door = (boolean) a("BlockBullets.door", false);
		blockbullet_halfslabs = (boolean) a("BlockBullets.halfslabs", false);
		blockbullet_leaves = (boolean) a("BlockBullets.leaves", false);
		blockbullet_water = (boolean) a("BlockBullets.water", false);

		overrideAnvil = (boolean) a("overrideAnvil", false);

		sendOnJoin = (boolean) a("sendOnJoin", false);

		enableBulletTrails = (boolean) a("enableBulletTrails", true);
		smokeSpacing = (double) a("BulletTrailsSpacing", 0.5);

		enableVisibleAmounts = (boolean) a("enableVisableBulletCounts", false);
		reloadOnF = (boolean) a("enableReloadingWhenSwapToOffhand", true);

		enableExplosionDamage = (boolean) a("enableExplosionDamage", false);

		try {
			enableEconomy = EconHandler.setupEconomy();
		} catch (Exception | Error e) {
		}

		try {
			bulletTrail = Particle.valueOf((String) a("Bullet-Particle-Type", "CLOUD"));
			a("ACCEPTED-BULLET-PARTICLE-VALUES", "https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Particle.html");
		} catch (Exception | Error e) {
		}

		try {
			guntype = Material.matchMaterial((String) a("gunMaterialType", guntype.toString()));
		} catch (Exception e) {
			guntype = Material.DIAMOND_HOE;
		}
		overrideURL = (boolean) a("DefaultResoucepackOverride", false);
		if (overrideURL) {
			url = (String) a("DefaultResoucepack", url);
		} else {
			// Make sure the user is always up to date if they don't override
			// the resoucepack
			if (!getConfig().contains("DefaultResoucepack")
					|| !url.equals(getConfig().getString("DefaultResoucepack"))) {
				getConfig().set("DefaultResoucepack", url);
				saveTheConfig = true;
			}
		}
		enableIronSightsON_RIGHT_CLICK = (boolean) a("IronSightsOnRightCLick", false);

		for (Material m : Material.values())
			if (m.isBlock())
				if (m.name().contains("DOOR") || m.name().contains("TRAPDOOR") || m.name().contains("BUTTON")
						|| m.name().contains("LEVER"))
					interactableBlocks.add(m);

		// ,(float)a("Weapon.RPG.Damage", 10)

		List<String> stringsWoodRif = Arrays.asList(new String[] { getIngString(Material.IRON_INGOT, 0, 12),
				getIngString(Material.WOOD, 0, 2), getIngString(Material.REDSTONE, 0, 5) });
		List<String> stringsGoldRif = Arrays.asList(new String[] { getIngString(Material.IRON_INGOT, 0, 12),
				getIngString(Material.GOLD_INGOT, 0, 2), getIngString(Material.REDSTONE, 0, 5) });
		List<String> stringsMetalRif = Arrays.asList(
				new String[] { getIngString(Material.IRON_INGOT, 0, 15), getIngString(Material.REDSTONE, 0, 5) });
		List<String> stringsPistol = Arrays.asList(
				new String[] { getIngString(Material.IRON_INGOT, 0, 5), getIngString(Material.REDSTONE, 0, 2) });
		List<String> stringsRPG = Arrays.asList(
				new String[] { getIngString(Material.IRON_INGOT, 0, 32), getIngString(Material.REDSTONE, 0, 10) });

		List<String> stringsGrenades = Arrays.asList(
				new String[] { getIngString(Material.IRON_INGOT, 0, 6), getIngString(Material.SULPHUR, 0, 10) });

		List<String> stringsAmmo = Arrays.asList(new String[] { getIngString(Material.IRON_INGOT, 0, 1),
				getIngString(Material.SULPHUR, 0, 1), getIngString(Material.REDSTONE, 0, 1) });
		List<String> stringsAmmoRPG = Arrays.asList(new String[] { getIngString(Material.IRON_INGOT, 0, 4),
				getIngString(Material.SULPHUR, 0, 6), getIngString(Material.REDSTONE, 0, 1) });

		Ammo a1 = new Ammo556(getIngredients("Ammo556", stringsAmmo), 4,(double) a("Ammo.556.Price", 5.0));
		Ammo a2 = new Ammo9mm(getIngredients("Ammo9mm", stringsAmmo), 4,(double) a("Ammo.9mm.Price", 2.0));
		Ammo a3 = new AmmoShotGun(getIngredients("AmmoBuckshot", stringsAmmo), 2,(double) a("Ammo.Buckshot.Price", 50.0));
		Ammo a4 = new AmmoRPG(getIngredients("AmmoRPG", stringsAmmoRPG), 1,(double) a("Ammo.RPG.Price", 100.0));

		ammoRegister.put(m(14), a1);
		ammoRegister.put(m(15), a2);
		ammoRegister.put(m(16), a3);
		ammoRegister.put(m(17), a4);

		gunRegister.put(m(2), new P30((int) a("Weapon.P30.Durability", 500), getIngredients("P30", stringsPistol),
				(int) a("Weapon.P30.Damage", 3),(double) a("Weapon.P30.Price", 500.5)));
		gunRegister.put(m(3), new PKP((int) a("Weapon.PKP.Durability", 500), getIngredients("PKP", stringsPistol),
				(int) a("Weapon.PKP.Damage", 3),(double) a("Weapon.PKP.Price", 2000.0)));
		gunRegister.put(m(4), new MP5K((int) a("Weapon.MP5K.Durability", 1000), getIngredients("MP5K", stringsPistol),
				(int) a("Weapon.MP5K.Damage", 1),(double) a("Weapon.MP5K.Price", 800.0)));
		gunRegister.put(m(5), new AK47((int) a("Weapon.AK47.Durability", 1000), getIngredients("AK47", stringsWoodRif),
				(int) a("Weapon.AK47.Damage", 3),(double) a("Weapon.AK47.Price", 1000.0)));
		gunRegister.put(m(7), new M16((int) a("Weapon.M16.Durability", 1000), getIngredients("M16", stringsMetalRif),
				(int) a("Weapon.M16.Damage", 3),(double) a("Weapon.M16.Price", 1000.0)));
		gunRegister.put(m(8), new Remmington((int) a("Weapon.Remmington.Durability", 500),
				getIngredients("Remmington", stringsMetalRif), (int) a("Weapon.Remmington.Damage", 1),(double) a("Weapon.Remmington.Price", 1000.0)));
		gunRegister.put(m(9), new FNFal((int) a("Weapon.FNFal.Durability", 1000),
				getIngredients("FNFal", stringsMetalRif), (int) a("Weapon.FNFal.Damage", 1),(double) a("Weapon.FNFal.Price", 1000.0)));
		gunRegister.put(m(10), new RPG((int) a("Weapon.RPG.Durability", 100), getIngredients("RPG", stringsRPG),(double) a("Weapon.rPG.Price", 5000.0)));
		gunRegister.put(m(11), new UMP((int) a("Weapon.UMP.Durability", 1000), getIngredients("UMP", stringsPistol),
				(int) a("Weapon.UMP.Damage", 2),(double) a("Weapon.UMP.Price", 1000.0)));
		gunRegister.put(m(12), new SW1911((int) a("Weapon.SW1911.Durability", 500),
				getIngredients("SW1911", stringsPistol), (int) a("Weapon.SW1911.Damage", 2),(double) a("Weapon.SW1911.Price", 600.0)));
		gunRegister.put(m(13), new M40((int) a("Weapon.M40.Durability", 500), getIngredients("M40", stringsWoodRif),
				(int) a("Weapon.M40.Damage", 5),(double) a("Weapon.M40.Price", 1000.0)));

		gunRegister.put(m(18), new Enfield((int) a("Weapon.Enfield.Durability", 500),
				getIngredients("Enfield", stringsGoldRif), (int) a("Weapon.Enfield.Damage", 2),(double) a("Weapon.Enfield.Price", 500.0)));
		gunRegister.put(m(19), new HenryRifle((int) a("Weapon.Henry.Durability", 500),
				getIngredients("Henry", stringsGoldRif), (int) a("Weapon.Henry.Damage", 3),(double) a("Weapon.Henry.Price", 1000.0)));
		gunRegister.put(m(20), new MouserC96((int) a("Weapon.MouserC96.Durability", 500),
				getIngredients("MouserC96", stringsPistol), (int) a("Weapon.MouserC96.Damage", 2),(double) a("Weapon.MouserC96.Price", 400.0)));

		miscRegister.put(m(22), new Grenades(getIngredients("Grenades", stringsGrenades),(double) a("Weapon.Grenade.Price", 800.0)));

		if (saveTheConfig)
			saveConfig();

		if (!new File(getDataFolder(), "newGuns/examplegun.yml").exists()) {
			if (!new File(getDataFolder(), "newGuns").exists()) {
				new File(getDataFolder(), "newGuns").mkdirs();
			}
			FileConfiguration f = YamlConfiguration
					.loadConfiguration(new File(getDataFolder(), "newGuns/examplegun.yml"));
			f.set("invalid", true);
			f.set("name", "ExampleGun");
			f.set("displayname", "Example Gun");
			f.set("lore", Arrays.asList("loreline 1", "loreline 2", "more lines"));
			f.set("id", 21);
			f.set("craftingRequirements", stringsMetalRif);
			f.set("weapontype", WeaponType.RIFLE.name());
			StringBuilder validGuns = new StringBuilder();
			for (WeaponType g : WeaponType.values()) {
				validGuns.append(g.name() + ", ");
			}
			f.set("_VALID_WEAPON_TYPES", validGuns.toString());
			f.set("enableIronSights", false);
			f.set("ammotype", a1.getName());
			f.set("_VALID_AMMO_TYPES", a1.toString() + ", " + a2.getName() + ", " + a3.getName() + ", " + a4.getName());
			f.set("damage", 5);
			f.set("sway", 0.2);
			f.set("material", guntype.name());
			f.set("maxbullets", 12);
			f.set("durability", 1000);
			f.set("isAutomatic", false);
			try {
				f.save(new File(getDataFolder(), "newGuns/examplegun.yml"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (!new File(getDataFolder(), "newGuns/exampleKnife.yml").exists()) {
			if (!new File(getDataFolder(), "newGuns").exists()) {
				new File(getDataFolder(), "newGuns").mkdirs();
			}
			FileConfiguration f = YamlConfiguration
					.loadConfiguration(new File(getDataFolder(), "newGuns/exampleKnife.yml"));
			f.set("invalid", true);
			f.set("name", "ExampleKnife");
			f.set("displayname", "Example Gun");
			f.set("lore", Arrays.asList("loreline 1", "loreline 2", "more lines"));
			f.set("id", 0);
			f.set("craftingRequirements", stringsMetalRif);
			f.set("weapontype", WeaponType.MEELEE.name());
			StringBuilder validGuns = new StringBuilder();
			for (WeaponType g : WeaponType.values()) {
				validGuns.append(g.name() + ", ");
			}
			f.set("_VALID_WEAPON_TYPES", validGuns.toString());
			f.set("damage", 5);
			f.set("material", Material.IRON_SWORD.name());
			f.set("durability", 1000);
			try {
				f.save(new File(getDataFolder(), "newGuns/exampleKnife.yml"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (!new File(getDataFolder(), "newGuns/exampleAmmo.yml").exists()) {
			if (!new File(getDataFolder(), "newGuns").exists()) {
				new File(getDataFolder(), "newGuns").mkdirs();
			}
			FileConfiguration f = YamlConfiguration
					.loadConfiguration(new File(getDataFolder(), "newGuns/exampleAmmo.yml"));
			f.set("invalid", true);
			f.set("name", "ExampleAmmo");
			f.set("USE_THE_NAME_OF_AMMO_TO_CHANGE_A_GUN'S_AMMO_TYPE", "ExampleAmmo");
			f.set("displayname", "&cExample Ammo");
			f.set("id", 0);
			f.set("craftingRequirements", stringsMetalRif);
			f.set("weapontype", WeaponType.AMMO.name());
			StringBuilder validGuns = new StringBuilder();
			for (WeaponType g : WeaponType.values()) {
				validGuns.append(g.name() + ", ");
			}
			f.set("_VALID_WEAPON_TYPES", validGuns.toString());
			f.set("material", Material.DIAMOND_HOE.name());
			try {
				f.save(new File(getDataFolder(), "newGuns/exampleAmmo.yml"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		for (File f : new File(getDataFolder(), "newGuns").listFiles()) {
			try {
				if (f.getName().contains("yml")) {
					FileConfiguration f2 = YamlConfiguration.loadConfiguration(f);
					if ((!f2.contains("invalid")) || !f2.getBoolean("invalid")) {
						Material m = (Material) (f2.contains("material")
								? Material.matchMaterial(f2.getString("material"))
								: guntype);
						final MaterialStorage ms = MaterialStorage.getMS(m, f2.getInt("id"));
						WeaponType weatype = f2.contains("guntype") ? WeaponType.valueOf(f2.getString("guntype"))
								: WeaponType.valueOf(f2.getString("weapontype"));
						final ItemStack[] materails = convertIngredients(f2.getStringList("craftingRequirements"));
						final String name = f2.getString("name");

						final String displayname = f2.contains("displayname")
								? ChatColor.translateAlternateColorCodes('&', f2.getString("displayname"))
								: (ChatColor.GOLD + name);
						final List<String> extraLore = f2.contains("lore") ? f2.getStringList("lore") : null;
						
						
						final double price = f2.contains("price")?f2.getDouble("price"):100;
						
						
						if (weatype.isGun()) {
							boolean isAutomatic = f2.contains("isAutomatic") ? f2.getBoolean("isAutomatic") : false;

							WeaponSounds sound = WeaponSounds.GUN_MEDIUM;
							if (weatype == WeaponType.PISTOL || weatype == WeaponType.SMG)
								sound = WeaponSounds.GUN_SMALL;
							if (weatype == WeaponType.SHOTGUN || weatype == WeaponType.SNIPER)
								sound = WeaponSounds.GUN_BIG;

							gunRegister.put(ms,
									new DefaultGun(name, ms, weatype, f2.getBoolean("enableIronSights"),
											AmmoType.getAmmo(f2.getString("ammotype")), f2.getDouble("sway"), 2,
											f2.getInt("maxbullets"), f2.getInt("damage"), isAutomatic,
											f2.getInt("durability"), sound, extraLore, displayname, price,materails));

						} else {
							if (weatype == WeaponType.AMMO) {
								ammoRegister.put(ms, new DefaultAmmo(name, displayname, extraLore, m, ms.getData(), 64,
										false, 1,price, materails));
							} else {
								miscRegister.put(ms, new ArmoryBaseObject() {
									@Override
									public String getName() {
										return name;
									}

									@Override
									public MaterialStorage getItemData() {
										return ms;
									}

									@Override
									public ItemStack[] getIngredients() {
										return materails;
									}

									@Override
									public int getCraftingReturn() {
										return 1;
									}

									@Override
									public List<String> getCustomLore() {
										return extraLore;
									}

									@Override
									public String getDisplayName() {
										return displayname;
									}

									@Override
									public double cost() {
										return price;
									}
								});
							}
						}
					}
				}
			} catch (Exception e) {
			}
		}

		craftingMenu = Bukkit.createInventory(null, 27, S_craftingBenchName);
		shopMenu = Bukkit.createInventory(null, 27, S_shopName);
		for (Gun g : gunRegister.values()) {
			try {
				getLogger().info("-Loading Gun:" + g.getName());
				ItemStack is = ItemFact.getGun(g);
				ItemMeta im = is.getItemMeta();
				List<String> lore = ItemFact.getCraftingGunLore(g);
				im.setLore(lore);
				is.setItemMeta(im);
				if (enableVisibleAmounts)
					is.setAmount(g.getCraftingReturn());
				craftingMenu.addItem(is);
				
				ItemStack shopVers = ItemFact.addShopLore(g,is.clone());
				shopMenu.addItem(shopVers);				
			} catch (Exception e) {
				getLogger().warning("-Failed to load Gun:" + g.getName());
				e.printStackTrace();
			}
		}
		for (ArmoryBaseObject abo : miscRegister.values()) {
			try {
				getLogger().info("-Loading MISC:" + abo.getName());
				ItemStack is = ItemFact.getObject(abo);
				ItemMeta im = is.getItemMeta();
				List<String> lore = ItemFact.getCraftingLore(abo);
				im.setLore(lore);
				is.setItemMeta(im);
				if (enableVisibleAmounts)
					is.setAmount(abo.getCraftingReturn());
				craftingMenu.addItem(is);

				ItemStack shopVers = ItemFact.addShopLore(abo,is.clone());
				shopMenu.addItem(shopVers);		
			} catch (Exception e) {
				getLogger().warning("-Failed to load MISC:" + abo.getName());
				e.printStackTrace();
			}
		}
		for (Ammo ammo : ammoRegister.values()) {
			try {
				getLogger().info("-Loading Ammo:" + ammo.getName());
				ItemStack is = ItemFact.getAmmo(ammo);
				ItemMeta im = is.getItemMeta();
				List<String> lore = ItemFact.getCraftingLore(ammo);
				im.setLore(lore);
				is.setItemMeta(im);
				is.setAmount(ammo.getCraftingReturn());
				craftingMenu.addItem(is);
				
				ItemStack shopVers = ItemFact.addShopLore(ammo,is.clone());
				shopMenu.addItem(shopVers);		
			} catch (Exception e) {
				getLogger().warning("-Failed to load ammo:" + ammo.getName());
				e.printStackTrace();
			}
		}
	}

	@EventHandler
	public void onResourcepackStatusEvent(PlayerResourcePackStatusEvent event) {
		if (event.getStatus() == PlayerResourcePackStatusEvent.Status.ACCEPTED
				|| event.getStatus() == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED)
			Main.resourcepackReq.add(event.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onHopperpickup(InventoryPickupItemEvent e) {
		if (e.getInventory().getType() == InventoryType.HOPPER)
			if (isGun(e.getItem().getItemStack()))
				e.setCancelled(true);
	}

	@EventHandler
	public void onHopper(InventoryMoveItemEvent e) {
		if (e.getSource().getType() == InventoryType.HOPPER || e.getSource().getType() == InventoryType.DISPENSER
				|| e.getSource().getType() == InventoryType.DROPPER)
			if (isGun(e.getItem()))
				e.setCancelled(true);
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onShift(PlayerToggleSneakEvent e) {
		if (!Main.enableIronSightsON_RIGHT_CLICK) {
			if (e.isSneaking()) {
				if (isGun(e.getPlayer().getItemInHand())) {
					Gun g = getGun(e.getPlayer().getItemInHand());
					if (g != null) {
						if (g.hasIronSights()) {
							try {

								if (!e.getPlayer().getItemInHand().hasItemMeta()
										|| !e.getPlayer().getItemInHand().getItemMeta().hasDisplayName()
										|| e.getPlayer().getItemInHand().getItemMeta().getDisplayName()
												.contains("Reloading."))
									return;
								if (Update19OffhandChecker.supportOffhand(e.getPlayer())) {
									try {
										ItemStack tempremove = null;
										if (e.getPlayer().getInventory().getItemInOffHand() != null)
											tempremove = e.getPlayer().getInventory().getItemInOffHand();
										e.getPlayer().getInventory()
												.setItemInOffHand(e.getPlayer().getInventory().getItemInMainHand());
										ItemStack ironsights = new ItemStack(guntype, 1,
												(short) IronSightsToggleItem.getData());
										ItemMeta im = ironsights.getItemMeta();
										im.setDisplayName(IronSightsToggleItem.getItemName());
										im.setUnbreakable(true);
										im.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_UNBREAKABLE);
										ironsights.setItemMeta(im);
										e.getPlayer().getInventory().setItemInMainHand(ironsights);
										if (tempremove != null) {
											e.getPlayer().getInventory().addItem(tempremove);
										}

									} catch (Error e2) {
									}
								}
							} catch (Error e2) {
								Bukkit.broadcastMessage(prefix
										+ "Ironsights not compatible for versions lower than 1.8. The server owner should set EnableIronSights to false in the plugin's config");
							}
						}
					}
				}
			} else {
				if (isIS(e.getPlayer().getItemInHand())) {
					try {
						e.getPlayer().getInventory().setItemInMainHand(e.getPlayer().getInventory().getItemInOffHand());
						e.getPlayer().getInventory().setItemInOffHand(null);
					} catch (Error e2) {
					}
				}
			}
		}
	}

	public ItemStack[] getIngredients(String name, List<String> ing) {
		if (!getConfig().contains("Crafting." + name)) {
			getConfig().set("Crafting." + name, ing);
			saveConfig();
		}
		List<String> e = getConfig().getStringList("Crafting." + name);
		return convertIngredients(e);
	}

	public ItemStack[] getIngredients(String name) {
		if (!getConfig().contains("Crafting." + name)) {
			getConfig().set("Crafting." + name,
					Arrays.asList(new String[] { getIngString(Material.IRON_INGOT, 0, 10) }));
			saveConfig();
		}
		List<String> e = getConfig().getStringList("Crafting." + name);
		return convertIngredients(e);
	}

	@SuppressWarnings("deprecation")
	public ItemStack[] convertIngredients(List<String> e) {
		ItemStack[] list = new ItemStack[e.size()];
		for (int i = 0; i < e.size(); i++) {
			String[] k = e.get(i).split(",");
			ItemStack temp = null;
			try {
				temp = new ItemStack(Material.matchMaterial(k[0]));
			} catch (Exception e2) {
				temp = new ItemStack(Integer.parseInt(k[0]));
			}
			if (k.length > 1)
				temp.setDurability(Short.parseShort(k[1]));
			if (k.length > 2)
				temp.setAmount(Integer.parseInt(k[2]));
			list[i] = temp;
		}
		return list;
	}

	public String getIngString(Material m, int durability, int amount) {
		return m.toString() + "," + durability + "," + amount;
	}

	public boolean b(String arg, String startsWith) {
		return arg.toLowerCase().startsWith(startsWith.toLowerCase());
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length == 1) {
			List<String> s = new ArrayList<String>();
			if (b("give", args[0]))
				s.add("give");
			if (b("shop", args[0]))
				s.add("shop");
			if (b("craft", args[0]))
				s.add("craft");
			if (b("getOpenGunSlot", args[0]))
				s.add("getOpenGunSlot");
			if (b("reload", args[0]))
				s.add("reload");

			return s;
		}
		if (args.length == 2) {
			List<String> s = new ArrayList<String>();
			if (args[0].equalsIgnoreCase("give")) {
				for (Entry<MaterialStorage, Gun> e : gunRegister.entrySet()) 
					if (b(e.getValue().getName(), args[1])) 
						s.add(e.getValue().getName());
				for (Entry<MaterialStorage, Ammo> e : ammoRegister.entrySet()) 
					if (b(e.getValue().getName(), args[1])) 
						s.add(e.getValue().getName());
				for (Entry<MaterialStorage, ArmoryBaseObject> e : miscRegister.entrySet()) 
					if (b(e.getValue().getName(), args[1])) 
						s.add(e.getValue().getName());			
				
			}
			return s;
		}
		return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("QualityArmory")) {
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("getOpenGunSlot")) {
					if (sender.hasPermission("qualityarmory.getopengunslot")) {
						List<MaterialStorage> getAllKeys = new ArrayList<>();
						getAllKeys.addAll(gunRegister.keySet());
						getAllKeys.addAll(ammoRegister.keySet());
						getAllKeys.addAll(miscRegister.keySet());
						int openID = 1;
						for (MaterialStorage i : getAllKeys) {
							if (i.getMat() == guntype)
								if (i.getData() > openID)
									openID = i.getData();
						}
						sender.sendMessage(prefix + " The next open slot for \"" + guntype.name() + "\"-base-guns is "
								+ (openID + 1));
						return true;
					} else {
						sender.sendMessage(prefix + ChatColor.RED + S_NOPERM);
						return true;
					}
				}
				if(args[0].equalsIgnoreCase("listItemIds")) {
					if(sender.hasPermission("qualityarmory.getmaterialsused")){
						for (ArmoryBaseObject g : miscRegister.values()) 
							sender.sendMessage(ChatColor.GREEN+g.getName()+": "+ChatColor.WHITE+g.getItemData().getMat().name()+" : "+g.getItemData().getData());
						for (Gun g : gunRegister.values()) 
							sender.sendMessage(ChatColor.GOLD+g.getName()+": "+ChatColor.WHITE+g.getItemData().getMat().name()+" : "+g.getItemData().getData());
						for (Ammo g : ammoRegister.values()) 
							sender.sendMessage(ChatColor.AQUA+g.getName()+": "+ChatColor.WHITE+g.getItemData().getMat().name()+" : "+g.getItemData().getData());
					} else {
						sender.sendMessage(prefix + ChatColor.RED + S_NOPERM);
						return true;
					}
				}
				if (args[0].equalsIgnoreCase("reload")) {
					if (sender.hasPermission("qualityarmory.reload")) {
						reloadVals();
						sender.sendMessage(prefix + " Guns and configs have been reloaded.");
						return true;
					} else {
						sender.sendMessage(prefix + ChatColor.RED + S_NOPERM);
						return true;
					}
				}
				if (args[0].equalsIgnoreCase("give")) {
					if (!sender.hasPermission("qualityarmory.give")) {
						sender.sendMessage(prefix + ChatColor.RED + S_NOPERM);
						return true;
					}
					if (args.length == 1) {
						sender.sendMessage(prefix + " The item name is required");
						StringBuilder sb = new StringBuilder();
						sb.append("Valid items: ");
						for (Gun g : gunRegister.values()) {
							sb.append(g.getName() + ",");
						}
						sb.append(ChatColor.GRAY);
						for (Ammo g : ammoRegister.values()) {
							sb.append(g.getName() + ",");
						}
						sb.append(ChatColor.WHITE);
						for (ArmoryBaseObject g : miscRegister.values()) {
							sb.append(g.getName() + ",");
						}
						sender.sendMessage(prefix + sb.toString());
						return true;
					}
					ArmoryBaseObject g = null;
					StringBuilder gunName = new StringBuilder();
					// for (int j = 1; j < args.length; j++) {
					// gunName.append(args[j]);
					// if (j != args.length - 1)
					// gunName.append(" ");
					// }
					gunName.append(args[1]);
					// Check if it is a gun, then if it is ammo, then if it is misc
					for (Entry<MaterialStorage, Gun> e : gunRegister.entrySet())
						if (e.getValue().getName().equalsIgnoreCase(gunName.toString())) {
							g = e.getValue();
							break;
						}
					if (g == null)
						for (Entry<MaterialStorage, Ammo> e : ammoRegister.entrySet())
							if (e.getValue().getName().equalsIgnoreCase(gunName.toString())) {
								g = e.getValue();
								break;
							}
					if (g == null)
						for (Entry<MaterialStorage, ArmoryBaseObject> e : miscRegister.entrySet())
							if (e.getValue().getName().equalsIgnoreCase(gunName.toString())) {
								g = e.getValue();
								break;
							}

					if (g != null) {
						Player who = sender instanceof Player ? ((Player) sender) : null;
						if (args.length > 2)
							who = Bukkit.getPlayer(args[2]);
						if (who == null) {
							sender.sendMessage("That player is not online");
							return true;
						}

						ItemStack temp;
						if (g instanceof Gun)
							temp = ItemFact.getGun((Gun) g);
						else if (g instanceof Ammo)
							temp = ItemFact.getAmmo((Ammo) g);
						else
							temp = ItemFact.getObject(g);

						who.getInventory().addItem(temp);
						sender.sendMessage(prefix + " Adding " + g.getName() + " to your inventory");
					} else {
						sender.sendMessage(prefix + " Could not find item \"" + args[1] + "\"");
					}
				}
			}
			if (sender instanceof Player) {
				final Player player = (Player) sender;
				if (args.length >= 1 && args[0].equalsIgnoreCase("override")) {
					resourcepackReq.add(player.getUniqueId());
					sender.sendMessage(prefix + " Overriding resoucepack requirement.");
					return true;
				}
				if (shouldSend && !resourcepackReq.contains(player.getUniqueId())) {
					sendPacket(((Player) sender), true);
					return true;
				}
				if (args.length == 0) {
					sendHelp(player);
					return true;
				}
				if (args[0].equalsIgnoreCase("craft")) {
					if (!sender.hasPermission("qualityarmory.craft")) {
						sender.sendMessage(prefix + ChatColor.RED + S_NOPERM);
						return true;
					}
					player.openInventory(craftingMenu);
					return true;

				}
				if (args[0].equalsIgnoreCase("shop")) {
					if (!sender.hasPermission("qualityarmory.shop")) {
						sender.sendMessage(prefix + ChatColor.RED + S_NOPERM);
						return true;
					}
					player.openInventory(shopMenu);
					return true;

				}
				// The user sent an invalid command/no args.
				sendHelp(player);
				return true;
			}
		}
		return true;
	}

	public void sendHelp(CommandSender sender) {
		sender.sendMessage(prefix + " Commands:");
		sender.sendMessage(ChatColor.GRAY
				+ "/QA give <Item>: Gives the sender the item specified (guns, ammo, misc.)");
		sender.sendMessage(ChatColor.GRAY + "/QA craft: Opens the crafting menu.");
		sender.sendMessage(ChatColor.GRAY + "/QA shop: Opens the shop menu");
		sender.sendMessage(ChatColor.GRAY + "/QA listItemIds: Lists the materials and data for all items.");
	}

	public boolean isDuplicateGun(ItemStack is1, Player player) {
		for (ItemStack is : player.getInventory().getContents()) {
			if (is != null && is1 != null)
				if (ItemFact.sameGun(is, is1)) {
					return true;
				}
		}
		return false;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void oninvClick(final InventoryClickEvent e) {
		
		String name = null;
		try {
			if(e.getClickedInventory() != null)		
				name = e.getClickedInventory().getTitle();
		}catch(Error|Exception e4) {
			if(e.getInventory() != null)		
				name = e.getInventory().getTitle();
		}
		
		
		if (name != null && name.equals(S_craftingBenchName)) {
			e.setCancelled(true);
			if (e.getCurrentItem() != null) {

				if (isGun(e.getCurrentItem())) {
					Gun g = getGun(e.getCurrentItem());
					if (lookForIngre((Player) e.getWhoClicked(), g)
							|| e.getWhoClicked().getGameMode() == GameMode.CREATIVE) {
						removeForIngre((Player) e.getWhoClicked(), g);
						ItemStack s = ItemFact.getGun(g);
						s.setAmount(g.getCraftingReturn());
						e.getWhoClicked().getInventory().addItem(s);
						try {
							((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(),
									Sound.BLOCK_ANVIL_USE, 0.7f, 1);
						} catch (Error e2) {
							((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(),
									Sound.valueOf("ANVIL_USE"), 0.7f, 1);
						}
					} else {
						e.getWhoClicked().closeInventory();
						e.getWhoClicked().sendMessage(prefix + S_missingIngredients);
						try {
							((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(),
									Sound.BLOCK_ANVIL_BREAK, 1, 1);
						} catch (Error e2) {
							((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(),
									Sound.valueOf("ANVIL_BREAK"), 1, 1);
						}
					}
				} else if (isAmmo(e.getCurrentItem())) {
					Ammo g = getAmmo(e.getCurrentItem());
					if (lookForIngre((Player) e.getWhoClicked(), g)
							|| e.getWhoClicked().getGameMode() == GameMode.CREATIVE) {
						removeForIngre((Player) e.getWhoClicked(), g);
						AmmoUtil.addAmmo((Player) e.getWhoClicked(), g, g.getCraftingReturn());
						try {
							((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(),
									Sound.BLOCK_ANVIL_USE, 0.7f, 1);
						} catch (Error e2) {
							((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(),
									Sound.valueOf("ANVIL_USE"), 0.7f, 1);
						}
					} else {
						e.getWhoClicked().closeInventory();
						e.getWhoClicked().sendMessage(prefix + S_missingIngredients);
						try {
							((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(),
									Sound.BLOCK_ANVIL_BREAK, 1, 1);
						} catch (Error e2) {
							((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(),
									Sound.valueOf("ANVIL_BREAK"), 1, 1);
						}
					}
				} else if (isMisc(e.getCurrentItem())) {
					ArmoryBaseObject g = getMisc(e.getCurrentItem());
					if (lookForIngre((Player) e.getWhoClicked(), g)
							|| e.getWhoClicked().getGameMode() == GameMode.CREATIVE) {
						removeForIngre((Player) e.getWhoClicked(), g);
						ItemStack s = ItemFact.getObject(g);
						s.setAmount(g.getCraftingReturn());
						e.getWhoClicked().getInventory().addItem(s);
						try {
							((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(),
									Sound.BLOCK_ANVIL_USE, 0.7f, 1);
						} catch (Error e2) {
							((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(),
									Sound.valueOf("ANVIL_USE"), 0.7f, 1);
						}
					} else {
						e.getWhoClicked().closeInventory();
						e.getWhoClicked().sendMessage(prefix + S_missingIngredients);
						try {
							((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(),
									Sound.BLOCK_ANVIL_BREAK, 1, 1);
						} catch (Error e2) {
							((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(),
									Sound.valueOf("ANVIL_BREAK"), 1, 1);
						}
					}
				} else {
					e.setCancelled(true);
				}
			}
			return;
		}else if(name.equals(S_shopName)){
			e.setCancelled(true);
			if(!enableEconomy) {
				e.getWhoClicked().closeInventory();
				e.getWhoClicked().sendMessage(prefix+S_noEcon);
				return;
			}
			if (e.getCurrentItem() != null) {
				if (isGun(e.getCurrentItem())) {
					Gun g = getGun(e.getCurrentItem());
					if (EconHandler.hasEnough(g, (Player) e.getWhoClicked())
							|| e.getWhoClicked().getGameMode() == GameMode.CREATIVE) {
						EconHandler.pay(g, (Player) e.getWhoClicked());
						ItemStack s = ItemFact.getGun(g);
						s.setAmount(g.getCraftingReturn());
						e.getWhoClicked().getInventory().addItem(s);
						try {
							((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(),
									Sound.BLOCK_NOTE_HARP, 0.7f, 1);
						} catch (Error e2) {
							((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(),
									Sound.valueOf("NOTE_PIANO"), 0.7f, 1);
						}
					} else {
						e.getWhoClicked().closeInventory();
						e.getWhoClicked().sendMessage(prefix + S_noMoney);
						try {
							((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(),
									Sound.BLOCK_ANVIL_FALL, 1, 1);
						} catch (Error e2) {
							((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(),
									Sound.valueOf("ANVIL_LAND"), 1, 1);
						}
					}
				} else if (isAmmo(e.getCurrentItem())) {
					Ammo g = getAmmo(e.getCurrentItem());
					if (EconHandler.hasEnough(g, (Player) e.getWhoClicked())
							|| e.getWhoClicked().getGameMode() == GameMode.CREATIVE) {
						EconHandler.pay(g, (Player) e.getWhoClicked());
						AmmoUtil.addAmmo((Player) e.getWhoClicked(), g, g.getCraftingReturn());
						try {
							((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(),
									Sound.BLOCK_NOTE_HARP, 0.7f, 1);
						} catch (Error e2) {
							((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(),
									Sound.valueOf("NOTE_PIANO"), 0.7f, 1);
						}
					} else {
						e.getWhoClicked().closeInventory();
						e.getWhoClicked().sendMessage(prefix + S_noMoney);
						try {
							((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(),
									Sound.BLOCK_ANVIL_FALL, 1, 1);
						} catch (Error e2) {
							((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(),
									Sound.valueOf("ANVIL_LAND"), 1, 1);
						}
					}
				} else if (isMisc(e.getCurrentItem())) {
					ArmoryBaseObject g = getMisc(e.getCurrentItem());
					if (EconHandler.hasEnough(g, (Player) e.getWhoClicked())
							|| e.getWhoClicked().getGameMode() == GameMode.CREATIVE) {
						EconHandler.pay(g, (Player) e.getWhoClicked());
						ItemStack s = ItemFact.getObject(g);
						s.setAmount(g.getCraftingReturn());
						e.getWhoClicked().getInventory().addItem(s);
						try {
							((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(),
									Sound.BLOCK_NOTE_HARP, 0.7f, 1);
						} catch (Error e2) {
							((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(),
									Sound.valueOf("NOTE_PIANO"), 0.7f, 1);
						}
					} else {
						e.getWhoClicked().closeInventory();
						e.getWhoClicked().sendMessage(prefix + S_noMoney);
						try {
							((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(),
									Sound.BLOCK_ANVIL_FALL, 1, 1);
						} catch (Error e2) {
							((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(),
									Sound.valueOf("ANVIL_LAND"), 1, 1);
						}
					}
				} else {
					e.setCancelled(true);
				}
			}
			return;
		}

		// player inv

		if ((e.getCurrentItem() != null && e.getCurrentItem().getDurability() == IronSightsToggleItem.getData())
				|| (e.getCursor() != null && e.getCursor().getDurability() == IronSightsToggleItem.getData())) {
			e.setCancelled(true);
			return;
		}

		if (e.getCurrentItem() == null) {
			if (e.getClickedInventory() == null) {
				checkforDups((Player) e.getWhoClicked(), e.getCursor());
			}
			return;
		}
		final ItemStack curr = e.getCurrentItem().clone();
		final ItemStack curs = e.getCursor().clone();

		new BukkitRunnable() {
			@Override
			public void run() {
				checkforDups((Player) e.getWhoClicked(), curr, curs);
			}

		}.runTaskLater(this, 1);

		if ((e.getCursor() != null && isGun(e.getCursor())/*
															 * e.getCursor().getType( ) == guntype
															 */) && (e.getCurrentItem() != null
				&& isGun(e.getCurrentItem())/*
											 * e. getCurrentItem ( ) . getType ( ) == guntype
											 */)) {
			e.setCancelled(true);
			if ((e.getCurrentItem().getItemMeta().hasDisplayName()
					&& e.getCurrentItem().getItemMeta().getDisplayName().contains("Reloading"))
					|| (e.getCursor().getItemMeta().hasDisplayName()
							&& e.getCursor().getItemMeta().getDisplayName().contains("Reloading"))) {
				return;
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPickup(PlayerPickupItemEvent e) {
		if (isAmmo(e.getItem().getItemStack())) {
			if (shouldSend && !resourcepackReq.contains(e.getPlayer().getUniqueId())) {
				sendPacket(e.getPlayer(), true);
			}
			AmmoUtil.addAmmo(e.getPlayer(), ammoRegister.get(MaterialStorage.getMS(e.getItem().getItemStack())),
					e.getItem().getItemStack().getAmount());
			e.setCancelled(true);
			e.getItem().remove();
			try {
				e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_LEVER_CLICK, 0.2f, 1);
			} catch (Error e2) {
				e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.valueOf("CLICK"), 0.2f, 1);
			}
		}

		if (isGun(e.getItem().getItemStack())) {
			if (shouldSend && !resourcepackReq.contains(e.getPlayer().getUniqueId())) {
				sendPacket(e.getPlayer(), true);
			}
		}
		// }
	}

	public boolean lookForIngre(Player player, ArmoryBaseObject a) {
		ItemStack[] ings = a.getIngredients();
		boolean[] bb = new boolean[ings.length];
		for (ItemStack is : player.getInventory().getContents()) {
			if (is != null) {
				for (int i = 0; i < ings.length; i++) {
					if (bb[i])
						continue;
					if (is.getType() == ings[i].getType()
							&& (is.getDurability() == 0 || is.getDurability() == ings[i].getDurability())) {
						// TODO: Make sure this is clear: If there is no damage value (0), then accept
						// all damage types.
						if (is.getAmount() >= ings[i].getAmount())
							bb[i] = true;
						break;
					}
				}
			}
		}
		for (boolean b : bb) {
			if (!b)
				return false;
		}
		return true;
	}

	public boolean removeForIngre(Player player, ArmoryBaseObject a) {
		ItemStack[] ings = a.getIngredients();
		boolean[] bb = new boolean[ings.length];
		for (ItemStack is : player.getInventory().getContents()) {
			if (is != null) {
				for (int i = 0; i < ings.length; i++) {
					if (bb[i])
						continue;
					if (is.getType() == ings[i].getType() && is.getDurability() == ings[i].getDurability()) {
						if (is.getAmount() > ings[i].getAmount()) {
							bb[i] = true;
							int slot = player.getInventory().first(is);
							is.setAmount(is.getAmount() - ings[i].getAmount());
							player.getInventory().setItem(slot, is);
						} else if (is.getAmount() == ings[i].getAmount()) {
							bb[i] = true;
							int slot = player.getInventory().first(is);
							player.getInventory().setItem(slot, new ItemStack(Material.AIR));
						}
						break;
					}
				}
			}
		}
		for (boolean b : bb) {
			if (!b)
				return false;
		}
		return true;
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		if (reloadingTasks.containsKey(e.getEntity().getUniqueId())) {
			for (BukkitTask r : reloadingTasks.get(e.getEntity().getUniqueId())) {
				r.cancel();
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType() == Material.ANVIL
				&& overrideAnvil && !e.getPlayer().isSneaking()) {
			if (shouldSend && !resourcepackReq.contains(e.getPlayer().getUniqueId())) {
				sendPacket(e.getPlayer(), true);
				e.setCancelled(true);
				return;
			}
			if (!e.getPlayer().hasPermission("qualityarmory.craft")) {
				e.getPlayer().sendMessage(prefix + ChatColor.RED + S_ANVIL);
				return;
			}
			e.getPlayer().openInventory(craftingMenu);
			e.setCancelled(true);
			return;
		}

		if (e.getItem() != null) {
			// Quick bugfix for specifically this item.
			if ((!isGun(e.getItem())) && !isAmmo(e.getItem()) && !isMisc(e.getItem()) && (!isIS(e.getItem()))) {
				if ((gunRegister.containsKey(
						MaterialStorage.getMS(e.getItem().getType(), (int) e.getItem().getDurability() + 1))
						|| ammoRegister.containsKey(
								MaterialStorage.getMS(e.getItem().getType(), (int) e.getItem().getDurability() + 1)))) {
					// If the item is not a gun, but the item below it is
					int safeDurib = e.getItem().getDurability();
					for (MaterialStorage j : ammoRegister.keySet())
						if (j.getMat() == e.getItem().getType() && j.getData() > safeDurib)
							safeDurib = j.getData();
					for (MaterialStorage j : gunRegister.keySet())
						if (j.getMat() == e.getItem().getType() && j.getData() > safeDurib)
							safeDurib = j.getData();
					for (MaterialStorage j : miscRegister.keySet())
						if (j.getMat() == e.getItem().getType() && j.getData() > safeDurib)
							safeDurib = j.getData();

					// if (e.getItem().getDurability() == 1) {
					ItemStack is = e.getItem();
					is.setDurability((short) (safeDurib + 2));
					e.getPlayer().getInventory().setItem(e.getPlayer().getInventory().getHeldItemSlot(), is);
					// }
				}
				return;
			}

			ItemStack usedItem = e.getPlayer().getItemInHand();
			boolean offhand = false;

			if (isMisc(usedItem)) {
				ArmoryBaseObject base = getMisc(usedItem);
				if (base instanceof ThrowableItems) {
					e.setCancelled(true);
					if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
						((ThrowableItems) base).onRightClick(e.getPlayer());
					} else {
						((ThrowableItems) base).onLeftClick(e.getPlayer());
					}
				}
				return;
			}

			if (Update19OffhandChecker.supportOffhand(e.getPlayer())) {
				try {
					if (e.getPlayer().getInventory().getItemInMainHand().getDurability() == IronSightsToggleItem
							.getData()) {
						usedItem = e.getPlayer().getInventory().getItemInOffHand();
						offhand = true;
						if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
							if (!e.getPlayer().isSneaking() || !getGun(usedItem).isAutomatic()) {
								e.setCancelled(true);
								e.getPlayer().getInventory()
										.setItemInMainHand(e.getPlayer().getInventory().getItemInOffHand());
								e.getPlayer().getInventory().setItemInOffHand(null);
								return;
							}
						}
					}
				} catch (Error e2) {
				}
			}

			Gun g = getGun(usedItem);
			e.setCancelled(true);
			if (shouldSend && !resourcepackReq.contains(e.getPlayer().getUniqueId())) {
				sendPacket(e.getPlayer(), true);
			}
			if ((e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK)
					|| (g != null && g.isAutomatic() && e.getPlayer().isSneaking())) {
				/*
				 * if(e.getAction().name().contains("RIGHT") && e.getClickedBlock() != null &&
				 * interactableBlocks.contains(e.getClickedBlock().getType())) {
				 * e.setCancelled(false); return; }
				 */
				// TODO: Verify If the player is shifting and rightclick, the gun will still
				// fire. The player has to be standing (non-sneak) in order to interact with
				// interactable blocks.
				if (g != null) {
					if (!enableDurability || ItemFact.getDamage(usedItem) > 0) {
						if (allowGunsInRegion(e.getPlayer().getLocation())) {
							try {
								if (e.getHand() == EquipmentSlot.OFF_HAND) {
									return;
								}
							} catch (Error | Exception e4) {
							}
							g.shoot(e.getPlayer());
							if (enableDurability)
								if (offhand) {
									try {
										e.getPlayer().getInventory().setItemInOffHand(ItemFact.damage(g, usedItem));
									} catch (Error e2) {
									}
								} else {
									e.getPlayer().setItemInHand(ItemFact.damage(g, usedItem));
								}
							try {
								HotbarMessager.sendHotBarMessage(e.getPlayer(), g.getDisplayName() + " = "
										+ ItemFact.getAmount(usedItem) + "/" + g.getMaxBullets());
							} catch (Error | Exception e5) {
							}
							return;
						}
						try {
							HotbarMessager.sendHotBarMessage(e.getPlayer(), g.getDisplayName() + " = "
									+ ItemFact.getAmount(usedItem) + "/" + g.getMaxBullets());
						} catch (Error | Exception e5) {
						}
						// TODO: Verify that the gun is in the main hand.
						// Shouldn't work for offhand, but it should still
						// be checked later.
					}
				}
			} else {
				if (enableIronSightsON_RIGHT_CLICK) {
					if (!Update19OffhandChecker.supportOffhand(e.getPlayer())) {
						enableIronSightsON_RIGHT_CLICK = false;
						return;
					}
					// Rest should be okay
					if (g != null) {
						if (g.hasIronSights()) {
							try {

								if (e.getPlayer().getItemInHand().getItemMeta().getDisplayName().contains("Reloading."))
									return;

								if (Update19OffhandChecker.getItemStackOFfhand(e.getPlayer()) != null) {
									e.getPlayer().getInventory()
											.addItem(Update19OffhandChecker.getItemStackOFfhand(e.getPlayer()));
									Update19OffhandChecker.setOffhand(e.getPlayer(), null);
								}

								ItemStack tempremove = null;
								if (e.getPlayer().getInventory().getItemInOffHand() != null)
									tempremove = e.getPlayer().getInventory().getItemInOffHand();
								e.getPlayer().getInventory()
										.setItemInOffHand(e.getPlayer().getInventory().getItemInMainHand());
								if (tempremove != null) {
									ItemStack ironsights = new ItemStack(guntype, 1,
											(short) IronSightsToggleItem.getData());
									ItemMeta im = ironsights.getItemMeta();
									im.setDisplayName(IronSightsToggleItem.getItemName());
									im.setUnbreakable(true);
									im.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_UNBREAKABLE);
									ironsights.setItemMeta(im);
									e.getPlayer().getInventory().setItemInMainHand(ironsights);
								}

								try {
									HotbarMessager.sendHotBarMessage(e.getPlayer(), g.getDisplayName() + " = "
											+ ItemFact.getAmount(usedItem) + "/" + g.getMaxBullets());
								} catch (Error | Exception e5) {
								}
							} catch (Error e2) {
								Bukkit.broadcastMessage(prefix
										+ "Ironsights not compatible for versions lower than 1.8. The server owner should set EnableIronSights to false in the plugin's config");
							}
						} else {
							if (!enableDurability || ItemFact.getDamage(usedItem) > 0) {
								if (allowGunsInRegion(e.getPlayer().getLocation())) {
									g.shoot(e.getPlayer());
									if (enableDurability)
										if (offhand) {
											e.getPlayer().getInventory().setItemInOffHand(ItemFact.damage(g, usedItem));
										} else {
											e.getPlayer().setItemInHand(ItemFact.damage(g, usedItem));
										}
								}
								try {
									HotbarMessager.sendHotBarMessage(e.getPlayer(), g.getDisplayName() + " = "
											+ ItemFact.getAmount(usedItem) + "/" + g.getMaxBullets());
								} catch (Error | Exception e5) {
								}
								// TODO: Verify that the gun is in the main
								// hand.
								// Shouldn't work for offhand, but it should
								// still
								// be checked later.
							}
						}
					}
				} else {
					if (e.getClickedBlock() != null && interactableBlocks.contains(e.getClickedBlock().getType())) {
						e.setCancelled(false);
					} else {
						if (g != null) {
							if (g.playerHasAmmo(e.getPlayer())) {
								g.reload(e.getPlayer());
							}

							try {
								HotbarMessager.sendHotBarMessage(e.getPlayer(), g.getDisplayName() + " = "
										+ ItemFact.getAmount(usedItem) + "/" + g.getMaxBullets());
							} catch (Error | Exception e5) {
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void swap(PlayerItemHeldEvent e) {
		ItemStack prev = e.getPlayer().getInventory().getItem(e.getPreviousSlot());
		if (isIS(prev)) {
			try {
				e.getPlayer().getInventory().setItem(e.getPreviousSlot(),
						e.getPlayer().getInventory().getItemInOffHand());
				e.getPlayer().getInventory().setItemInOffHand(null);
			} catch (Error e2) {
			}
		}
	}

	@EventHandler
	public void onQuit(final PlayerQuitEvent e) {
		resourcepackReq.remove(e.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onJoin(final PlayerJoinEvent e) {
		if (/* Bukkit.getVersion().contains("1.8") || */ Bukkit.getVersion().contains("1.7")) {
			Bukkit.broadcastMessage(
					prefix + " QualityArmory does not support versions older than 1.9, and may crash clients");
			Bukkit.broadcastMessage(
					"Since there is no reason to stay on outdated updates, (1.7 and 1.8 has quite a number of exploits) update your server.");
			if (shouldSend) {
				shouldSend = false;
				Bukkit.broadcastMessage(prefix + ChatColor.RED + " Disabling resoucepack.");
			}
		}
		if (sendOnJoin) {
			sendPacket(e.getPlayer(), false);
		} else {
			for (ItemStack i : e.getPlayer().getInventory().getContents()) {
				if (i != null && (isGun(i)||isAmmo(i)||isMisc(i) )) {
					if (shouldSend && !resourcepackReq.contains(e.getPlayer().getUniqueId())) {
						new BukkitRunnable() {
							@Override
							public void run() {
								sendPacket(e.getPlayer(), false);
							}
						}.runTaskLater(this, 40);
					}
					break;
				}
			}
		}
	}

	@SuppressWarnings({ "deprecation", "unlikely-arg-type" })
	@EventHandler
	public void onDrop(final PlayerDropItemEvent e) {
		if (e.getPlayer().isDead()) {
			ItemStack newone = e.getItemDrop().getItemStack();
			if (isGun(newone)/* newone.getType() == guntype */
					&& newone.getItemMeta().hasDisplayName()
					&& newone.getItemMeta().getDisplayName().contains("Reloading")) {
				ItemMeta im = newone.getItemMeta();
				im.setDisplayName(gunRegister.get((int) newone.getDurability()).getDisplayName());
				newone.setItemMeta(im);
				e.getItemDrop().setItemStack(newone);
			}
			return;
		}

		if (isGun(e.getItemDrop().getItemStack()) && isDuplicateGun(e.getItemDrop().getItemStack(), e.getPlayer())) {
			e.setCancelled(true);
			return;
		}

		if (isGun(e.getItemDrop().getItemStack())) {
			if ((e.getItemDrop().getItemStack().getItemMeta().hasDisplayName()
					&& e.getItemDrop().getItemStack().getItemMeta().getDisplayName().contains("Reloading"))) {
				e.setCancelled(true);
				return;
			}
		}

		if (enableIronSightsON_RIGHT_CLICK) {
			if (e.getPlayer().getItemInHand() != null
					&& (isGun(e.getItemDrop().getItemStack()) || isIS(e.getItemDrop().getItemStack()))) {
				if (e.getItemDrop().getItemStack().getAmount() == 1) {
					try {
						boolean dealtWithDrop = false;
						if (e.getItemDrop().getItemStack().getDurability() == IronSightsToggleItem.getData()) {
							e.getItemDrop().setItemStack(e.getPlayer().getInventory().getItemInOffHand());
							e.getPlayer().setItemInHand(e.getPlayer().getInventory().getItemInOffHand());
							e.getPlayer().getInventory().setItemInOffHand(null);
							dealtWithDrop = true;
						}
						if (e.getPlayer().getItemInHand().getType() != Material.AIR && e.getPlayer().getItemInHand()
								.getDurability() != e.getItemDrop().getItemStack().getDurability()) {
							return;
						}
						final Gun g = getGun(e.getItemDrop().getItemStack());
						if (g != null) {
							e.setCancelled(true);
							if (GunUtil.hasAmmo(e.getPlayer(), g)) {
								if (!dealtWithDrop) {
									if (e.getPlayer().getItemInHand().getType() == Material.AIR) {
										e.getPlayer().setItemInHand(e.getItemDrop().getItemStack());
									} else {
										if ((g.getMaxBullets() - 1) == e.getPlayer().getItemInHand().getAmount())
											return;
									}
								}
								new BukkitRunnable() {
									@Override
									public void run() {
										GunUtil.basicReload(g, e.getPlayer(), g.hasUnlimitedAmmo());
									}
								}.runTaskLater(this, 1);
							}
						}

					} catch (Error e2) {
						e2.printStackTrace();
					}
				}
			}
		} else {
			/*
			 * if (isGun(e.getPlayer().getItemInHand())) {
			 * e.getPlayer().getItemInHand().setAmount(
			 * e.getPlayer().getItemInHand().getAmount() +
			 * e.getItemDrop().getItemStack().getAmount());
			 * e.getItemDrop().setItemStack(e.getPlayer().getItemInHand());
			 * e.getPlayer().getInventory().setItem(e.getPlayer().getInventory().
			 * getHeldItemSlot(), new ItemStack(Material.AIR)); }
			 */
			// TODO: Check if correct: The first two if statements should be enough to make
			// sure no dupes go by.
			// If it must reach this, then the player is not throwing half a gun.
		}
	}

	@SuppressWarnings("deprecation")
	public void sendPacket(final Player player, boolean warning) {
		if (warning)
			try {
				player.sendTitle(ChatColor.RED + S_NORES1, S_NORES2);
			} catch (Error e2) {
				player.sendMessage(ChatColor.RED + S_NORES1);
				player.sendMessage(ChatColor.RED + S_NORES2);
			}
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					player.setResourcePack(url);
				} catch (Exception e) {

				}
			}
		}.runTaskLater(this, 20 * (warning ? 1 : 5));
	}

	public static boolean allowGunsInRegion(Location loc) {
		if (!supportWorldGuard)
			return true;
		try {
			return WorldGuardSupport.a(loc);
		} catch (Error e) {
		}
		return true;
	}

	private void checkforDups(Player p, ItemStack... curr) {
		for (ItemStack curs : curr)
			for (int i = 0; i < p.getInventory().getSize(); i++) {
				ItemStack cont = p.getInventory().getItem(i);
				if (cont != null)
					if (curs != null && ItemFact.sameGun(cont, curs))
						if (!cont.equals(curs))
							p.getInventory().setItem(i, null);

			}
	}

	public MaterialStorage m(int d) {
		return MaterialStorage.getMS(guntype, d);
	}

	public boolean isMisc(ItemStack is) {
		return (is != null && miscRegister.containsKey(MaterialStorage.getMS(is.getType(), (int) is.getDurability())));
	}

	public ArmoryBaseObject getMisc(ItemStack is) {
		return miscRegister.get(MaterialStorage.getMS(is.getType(), (int) is.getDurability()));
	}

	public Gun getGun(ItemStack is) {
		return gunRegister.get(MaterialStorage.getMS(is.getType(), (int) is.getDurability()));
	}

	public Ammo getAmmo(ItemStack is) {
		return ammoRegister.get(MaterialStorage.getMS(is.getType(), (int) is.getDurability()));
	}

	public boolean isGun(ItemStack is) {
		return (is != null && gunRegister.containsKey(MaterialStorage.getMS(is.getType(), (int) is.getDurability())));
	}

	public boolean isAmmo(ItemStack is) {
		return (is != null && ammoRegister.containsKey(MaterialStorage.getMS(is.getType(), (int) is.getDurability())));
	}

	public boolean isIS(ItemStack is) {
		if (is != null && is.getType() == guntype && is.getDurability() == (int) IronSightsToggleItem.getData())
			return true;
		return false;
	}
}
