package me.zombie_striker.qg;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.Map.Entry;

import me.zombie_striker.pluginconstructor.HotbarMessager;
import me.zombie_striker.qg.ammo.*;
import me.zombie_striker.qg.armor.ArmorObject;
import me.zombie_striker.qg.armor.Kevlar;
import me.zombie_striker.qg.attachments.AttachmentBase;
import me.zombie_striker.qg.guns.*;
import me.zombie_striker.qg.guns.utils.GunUtil;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.guns.utils.WeaponType;
import me.zombie_striker.qg.handlers.*;
import me.zombie_striker.qg.handlers.gunvalues.ChargingHandlerEnum;
import me.zombie_striker.qg.handlers.gunvalues.RapidFireCharger;
import me.zombie_striker.qg.miscitems.*;
import me.zombie_striker.qg.miscitems.ThrowableItems.ThrowableHolder;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.*;

import com.google.common.base.Charsets;

public class Main extends JavaPlugin implements Listener {

	public static HashMap<MaterialStorage, Gun> gunRegister = new HashMap<>();
	public static HashMap<MaterialStorage, Ammo> ammoRegister = new HashMap<>();
	public static HashMap<MaterialStorage, ArmoryBaseObject> miscRegister = new HashMap<>();
	public static HashMap<MaterialStorage, ArmorObject> armorRegister = new HashMap<>();
	public static HashMap<MaterialStorage, AttachmentBase> attachmentRegister = new HashMap<>();

	public static HashMap<UUID, List<BukkitTask>> reloadingTasks = new HashMap<UUID, List<BukkitTask>>();

	public static ArrayList<UUID> resourcepackReq = new ArrayList<>();

	private static Main main;

	private List<Material> interactableBlocks = new ArrayList<>();
	private static boolean enableInteractChests = false;

	public static Main getInstance() {
		return main;
	}

	public static boolean DEBUG = false;

	public static Object bulletTrail;

	private boolean shouldSend = true;
	public static boolean sendOnJoin = false;
	public static boolean sendTitleOnJoin = false;
	public static double secondsTilSend = 0.0;

	public static boolean enableDurability = false;
	public static boolean UnlimitedAmmoPistol = false;
	public static boolean UnlimitedAmmoRifle = false;
	public static boolean UnlimitedAmmoShotgun = false;
	public static boolean UnlimitedAmmoSMG = false;
	public static boolean UnlimitedAmmoRPG = false;
	public static boolean UnlimitedAmmoSniper = false;
	public static boolean UnlimitedAmmoLazer = false;

	public static double bulletStep = 0.10;

	public static boolean blockbullet_leaves = false;
	public static boolean blockbullet_halfslabs = false;
	public static boolean blockbullet_door = false;
	public static boolean blockbullet_water = false;
	public static boolean blockbullet_glass = false;

	public static boolean overrideAnvil = false;
	public static boolean supportWorldGuard = false;
	public static boolean enableIronSightsON_RIGHT_CLICK = false;
	public static boolean enableBulletTrails = true;
	public static boolean enableVisibleAmounts = false;
	public static boolean reloadOnF = true;

	public static boolean enableExplosionDamage = false;
	public static boolean enableExplosionDamageDrop = false;

	public static boolean hideTextureWarnings = false;

	public static boolean enableEconomy = false;

	public static boolean allowGunReload = true;

	public static boolean enableBleeding = false;
	public static double bulletWound_initialbloodamount = 1500;
	public static double bulletWound_BloodIncreasePerSecond = 0.01;
	public static double bulletWound_MedkitBloodlossHealRate = 0.05;

	public static boolean HeadshotOneHit = true;

	public static boolean overrideURL = false;
	public static String url19plus = "https://www.dropbox.com/s/faufrgo7w2zpi3d/QualityArmoryv1.0.10.zip?dl=1";
	public static String url19plusAXE = "https://www.dropbox.com/s/76a6isemw6f4j9d/QualityArmoryv1.0.13d.zip?dl=1";
	public static String url18 = "https://www.dropbox.com/s/gx6dhahq6onob4g/QualityArmory1.8v1.0.1.zip?dl=1";
	public static String url = url19plusAXE;

	public static String FalloutExpName = "QualityArmoryEXPFallout";

	public static String S_NOPERM = "&c You do not have permission to do that";
	public static String S_NORES1 = " &c&l Downloading Resourcepack...";
	public static String S_NORES2 = " &f Accept the resoucepack to see correct textures";
	public static String S_ANVIL = " &aYou do not have permission to use this armory bench. ShiftClick to access anvil.";
	public static String S_ITEM_BULLETS = "&aBullets";
	public static String S_ITEM_DURIB = "Durability";
	public static String S_ITEM_DAMAGE = "&aDamage";
	public static String S_ITEM_AMMO = "&aAmmo";
	public static String S_ITEM_ING = "Ingredients";
	public static String S_ITEM_VARIENTS = "&7Varient:";

	public static String S_LMB_SINGLE = ChatColor.DARK_GRAY + "[LMB] to use Single-fire mode";
	public static String S_LMB_FULLAUTO = ChatColor.DARK_GRAY + "[Sneak]+[LMB] to use Automatic-fire";
	public static String S_RMB_RELOAD = ChatColor.DARK_GRAY + "[RMB] to reload";
	public static String S_RMB_R1 = ChatColor.DARK_GRAY + "[DropItem] to reload";
	public static String S_RMB_R2 = ChatColor.DARK_GRAY + "[RMB] to reload";

	public static String S_RMB_A1 = ChatColor.DARK_GRAY + "[RMB] to open ironsights";
	public static String S_RMB_A2 = ChatColor.DARK_GRAY + "[Sneak] to open ironsights";

	public static String S_FULLYHEALED = "&fYou are fully healed. No need for this right now!";
	public static String S_MEDKIT_HEALING = "Healing";
	public static String S_MEDKIT_BLEEDING = "Blood-Loss Rate:";
	// public static String S_MEDKIT_HEALINGHEARTS = "&f Healed 1HP.";
	// public static String S_MEDKIT_WAIT = "&f Too soon! Wait another %seconds%
	// seconds.";
	public static double S_MEDKIT_HEALDELAY = 6;
	// public static double S_MEDKIT_HEARTDELAY = 10;
	public static double S_MEDKIT_HEAL_AMOUNT = 1;

	public static String S_MEDKIT_LORE_INFO = "&f[RMB] to heal yourself!";

	public static String S_BULLETPROOFSTOPPEDBLEEDING = "&aYour Kevlar vest protected you from that bullet!";
	public static String S_BLEEDOUT_STARTBLEEDING = "&cYOU ARE BLEEDING! FIND A MEDKIT TO STOP THE BLEEDING!";
	public static String S_BLEEDOUT_LOSINGconscious = "&cYOU ARE LOSING CONSCIOUSNESS DUE TO BLOODLOSS!";

	public static boolean enableCrafting = true;
	public static boolean enableShop = true;

	public static double smokeSpacing = 0.5;

	public static String prefix = ChatColor.GRAY + "[" + ChatColor.DARK_GREEN + "QualityArmory" + ChatColor.GRAY + "]"
			+ ChatColor.WHITE;

	// public Inventory craftingMenu;
	public static String S_craftingBenchName = "Armory Bench Page:";
	public static String S_missingIngredients = "You do not have all the materials needed to craft this";

	// public Inventory shopMenu;
	public static String S_shopName = "Weapons Shop Page:";
	public static String S_noMoney = "You do not have enough money to buy this";
	public static String S_noEcon = "ECONOMY NOT ENABLED. REPORT THIS TO THE OWNER!";

	public static ItemStack prevButton = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
	public static ItemStack nextButton = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5);

	public static Material guntype = Material.DIAMOND_AXE;

	public static CustomYml m;

	public static boolean hasParties = false;
	public static boolean friendlyFire = false;

	private static final String SERVER_VERSION;

	public static boolean AUTOUPDATE = true;

	public static boolean USE_DEFAULT_CONTROLS = true;

	public static boolean ENABLE_LORE_INFO = true;
	public static boolean ENABLE_LORE_HELP = true;

	public static boolean AutoDetectResourcepackVersion = false;
	public static final int ID18 = 106;

	private FileConfiguration config;
	private File configFile;

	public static List<MaterialStorage> reservedForExps = Arrays.asList(m(53), m(54), m(55), m(56), m(57), m(58), m(59),
			m(60));

	static {
		String name = Bukkit.getServer().getClass().getName();
		name = name.substring(name.indexOf("craftbukkit.") + "craftbukkit.".length());
		name = name.substring(0, name.indexOf("."));
		SERVER_VERSION = name;
	}

	public static boolean isVersionHigherThan(int mainVersion, int secondVersion) {
		if (secondVersion >= 9 && Bukkit.getPluginManager().isPluginEnabled("ViaRewind"))
			return false;
		String firstChar = SERVER_VERSION.substring(1, 2);
		int fInt = Integer.parseInt(firstChar);
		if (fInt < mainVersion)
			return false;
		StringBuilder secondChar = new StringBuilder();
		for (int i = 3; i < 10; i++) {
			if (SERVER_VERSION.charAt(i) == '_' || SERVER_VERSION.charAt(i) == '.')
				break;
			secondChar.append(SERVER_VERSION.charAt(i));
		}

		int sInt = Integer.parseInt(secondChar.toString());
		if (sInt < secondVersion)
			return false;
		return true;
	}

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
		for (Entry<MaterialStorage, ArmoryBaseObject> misc : miscRegister.entrySet()) {
			if (misc instanceof GrenadeBase) {
				for (Entry<Entity, ThrowableHolder> e : ((GrenadeBase) misc).grenadeHolder.entrySet()) {
					if (e.getKey() instanceof Item)
						e.getKey().remove();
				}
			}
		}
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
		DEBUG(ChatColor.RED + "NOTICE ME");
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
			Bukkit.getPluginManager().registerEvents(new Update19resourcepackhandler(), this);
		} catch (Exception | Error e) {
			getLogger().info(prefix + " Resourcepack handler has been disabled due to the update being used.");
		}
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
			if (AUTOUPDATE)
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

	@EventHandler
	public void roggleshift(PlayerToggleSneakEvent e) {
		try {
			ItemStack item = e.getPlayer().getInventory().getItemInOffHand();
			try {
				if (item != null && isGun(item)) {

					Gun gun = getGun(item);
					if (gun.getWeaponType() == WeaponType.SNIPER)
						if (e.isSneaking()) {
							e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 12000, 6));
						}
				}
				if (!e.isSneaking())
					e.getPlayer().removePotionEffect(PotionEffectType.SLOW);
			} catch (Error e2) {
			}
		} catch (Error | Exception e4) {
		}
	}

	public static void DEBUG(String message) {
		if (DEBUG)
			Bukkit.broadcast(message, "qualityarmory.debugmessages");
	}

	public void reloadVals() {

		gunRegister.clear();
		ammoRegister.clear();
		miscRegister.clear();
		armorRegister.clear();
		interactableBlocks.clear();

		attachmentRegister.clear();

		m = new CustomYml(new File(getDataFolder(), "messages.yml"));
		S_ANVIL = ChatColor.translateAlternateColorCodes('&', (String) m.a("NoPermAnvilMessage", S_ANVIL));
		S_NOPERM = ChatColor.translateAlternateColorCodes('&', (String) m.a("NoPerm", S_NOPERM));
		S_craftingBenchName = (String) m.a("CraftingBenchName", S_craftingBenchName);
		S_missingIngredients = (String) m.a("Missing_Ingredients", S_missingIngredients);
		S_NORES1 = ChatColor.translateAlternateColorCodes('&', (String) m.a("NoResoucepackMessage1", S_NORES1));
		S_NORES2 = ChatColor.translateAlternateColorCodes('&', (String) m.a("NoResourcepackMessage2", S_NORES2));
		S_ITEM_AMMO = ChatColor.translateAlternateColorCodes('&', (String) m.a("Lore_Ammo", S_ITEM_AMMO));
		S_ITEM_BULLETS = ChatColor.translateAlternateColorCodes('&', (String) m.a("lore_bullets", S_ITEM_BULLETS));
		S_ITEM_DAMAGE = ChatColor.translateAlternateColorCodes('&', (String) m.a("Lore_Damage", S_ITEM_DAMAGE));
		S_ITEM_DURIB = ChatColor.translateAlternateColorCodes('&', (String) m.a("Lore_Durib", S_ITEM_DURIB));
		S_ITEM_ING = ChatColor.translateAlternateColorCodes('&', (String) m.a("Lore_ingredients", S_ITEM_ING));
		S_ITEM_VARIENTS = ChatColor.translateAlternateColorCodes('&', (String) m.a("Lore_Varients", S_ITEM_VARIENTS));

		S_LMB_SINGLE = (String) m.a("Lore-LMB-Single", S_LMB_SINGLE);
		S_LMB_FULLAUTO = (String) m.a("Lore-LMB-FullAuto", S_LMB_FULLAUTO);
		S_RMB_RELOAD = (String) m.a("Lore-RMB-Reload", S_RMB_RELOAD);
		S_RMB_A1 = (String) m.a("Lore-Ironsights-RMB", S_RMB_A1);
		S_RMB_A2 = (String) m.a("Lore-Ironsights-Sneak", S_RMB_A2);
		S_RMB_R1 = (String) m.a("Lore-Reload-Dropitem", S_RMB_R1);
		S_RMB_R2 = (String) m.a("Lore-Reload-RMB", S_RMB_R2);

		S_FULLYHEALED = ChatColor.translateAlternateColorCodes('&', (String) m.a("Medkit-FullyHealed", S_FULLYHEALED));
		S_MEDKIT_HEALING = ChatColor.translateAlternateColorCodes('&',
				(String) m.a("Medkit-Healing", S_MEDKIT_HEALING));
		S_MEDKIT_BLEEDING = ChatColor.translateAlternateColorCodes('&',
				(String) m.a("Medkit-Bleeding", S_MEDKIT_BLEEDING));
		// S_MEDKIT_HEALINGHEARTS =ChatColor.translateAlternateColorCodes('&',(String)
		// m.a("Medkit-HEALING_HEARTS", S_MEDKIT_HEALINGHEARTS));
		// S_MEDKIT_WAIT=ChatColor.translateAlternateColorCodes('&',(String)
		// m.a("Medkit-HEALING_WAIT", S_MEDKIT_WAIT));
		S_MEDKIT_HEAL_AMOUNT = (double) m.a("Medkit-HEALING_HEARTS_AMOUNT", S_MEDKIT_HEAL_AMOUNT);
		// S_MEDKIT_HEARTDELAY =(double) m.a("Medkit-HEALING_DELAY_IN_SECONDS",
		// S_MEDKIT_HEARTDELAY);
		S_MEDKIT_HEALDELAY = (double) m.a("Medkit-HEALING_WEAPPING_DELAY_IN_SECONDS", S_MEDKIT_HEALDELAY);

		S_MEDKIT_LORE_INFO = ChatColor.translateAlternateColorCodes('&',
				(String) m.a("Medkit-Lore_RMB", S_MEDKIT_LORE_INFO));

		S_BLEEDOUT_LOSINGconscious = ChatColor.translateAlternateColorCodes('&',
				(String) m.a("Bleeding.Losingconsciousness", S_BLEEDOUT_LOSINGconscious));
		S_BLEEDOUT_STARTBLEEDING = ChatColor.translateAlternateColorCodes('&',
				(String) m.a("Bleeding.StartBleeding", S_BLEEDOUT_STARTBLEEDING));
		S_BULLETPROOFSTOPPEDBLEEDING = ChatColor.translateAlternateColorCodes('&',
				(String) m.a("Bleeding.ProtectedByKevlar", S_BULLETPROOFSTOPPEDBLEEDING));
		if (!new File(getDataFolder(), "config.yml").exists())
			saveDefaultConfig();
		reloadConfig();

		if (getServer().getPluginManager().isPluginEnabled("Parties"))
			hasParties = true;
		DEBUG = (boolean) a("ENABLE-DEBUG", false);

		friendlyFire = (boolean) a("FriendlyFireEnabled", false);

		shouldSend = (boolean) a("useDefaultResourcepack", true);
		UnlimitedAmmoPistol = (boolean) a("UnlimitedPistolAmmo", false);
		UnlimitedAmmoShotgun = (boolean) a("UnlimitedShotgunAmmo", false);
		UnlimitedAmmoRifle = (boolean) a("UnlimitedRifleAmmo", false);
		UnlimitedAmmoSMG = (boolean) a("UnlimitedSMGAmmo", false);
		UnlimitedAmmoSniper = (boolean) a("UnlimitedSniperAmmo", false);
		UnlimitedAmmoRPG = (boolean) a("UnlimitedRocketAmmo", false);
		UnlimitedAmmoLazer = (boolean) a("UnlimitedLazerAmmo", false);
		enableDurability = (boolean) a("EnableWeaponDurability", false);

		bulletStep = (double) a("BulletDetection.step", 0.10);

		blockbullet_door = (boolean) a("BlockBullets.door", false);
		blockbullet_halfslabs = (boolean) a("BlockBullets.halfslabs", false);
		blockbullet_leaves = (boolean) a("BlockBullets.leaves", false);
		blockbullet_water = (boolean) a("BlockBullets.water", false);
		blockbullet_glass = (boolean) a("BlockBullets.glass", false);

		enableInteractChests = (boolean) a("enableInteract.Chests", false);

		overrideAnvil = (boolean) a("overrideAnvil", false);

		sendOnJoin = (boolean) a("sendOnJoin", false);
		sendTitleOnJoin = (boolean) a("sendTitleOnJoin", false);
		secondsTilSend = Double.valueOf(a("SecondsTillRPIsSent", 5.0) + "");

		enableBulletTrails = (boolean) a("enableBulletTrails", true);
		smokeSpacing = Double.valueOf(a("BulletTrailsSpacing", 0.5) + "");

		enableVisibleAmounts = (boolean) a("enableVisableBulletCounts", false);
		reloadOnF = (boolean) a("enableReloadingWhenSwapToOffhand", true);

		enableExplosionDamage = (boolean) a("enableExplosionDamage", false);
		enableExplosionDamageDrop = (boolean) a("enableExplosionDamageDrop", false);

		enableCrafting = (boolean) a("enableCrafting", true);
		enableShop = (boolean) a("enableShop", true);

		AUTOUPDATE = (boolean) a("AUTO-UPDATE", true);
		USE_DEFAULT_CONTROLS = !(boolean) a("Swap-Reload-and-Shooting-Controls", false);

		ENABLE_LORE_INFO = (boolean) a("enable_lore_gun-info_messages", true);
		ENABLE_LORE_HELP = (boolean) a("enable_lore_control-help_messages", true);

		bulletWound_initialbloodamount = (double) a("BulletWounds.InitialBloodLevel", bulletWound_initialbloodamount);
		bulletWound_BloodIncreasePerSecond = (double) a("BulletWounds.BloodIncreasePerSecond",
				bulletWound_BloodIncreasePerSecond);
		bulletWound_MedkitBloodlossHealRate = (double) a("BulletWounds.Medkit_Heal_Bloodloss_Rate",
				bulletWound_MedkitBloodlossHealRate);

		enableBleeding = (boolean) a("BulletWounds.enableBleeding", enableBleeding);
		HeadshotOneHit = (boolean) a("Enable_Headshot_Instantkill", HeadshotOneHit);

		hideTextureWarnings = (boolean) a("hideTextureWarnings", false);

		allowGunReload = (boolean) a("allowGunReload", allowGunReload);
		AutoDetectResourcepackVersion = (boolean) a("Auto-Detect-Resourcepack", false);

		// Force inversion due to naming
		if (saveTheConfig)
			saveConfig();
		try {
			enableEconomy = EconHandler.setupEconomy();
		} catch (Exception | Error e) {
		}

		try {
			bulletTrail = Particle.valueOf((String) a("Bullet-Particle-Type", "FIREWORKS_SPARK"));
			a("ACCEPTED-BULLET-PARTICLE-VALUES", "https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Particle.html");
		} catch (Exception | Error e) {
		}

		try {
			guntype = Material.matchMaterial((String) a("gunMaterialType", guntype.toString()));
		} catch (Exception e) {
			guntype = Material.DIAMOND_HOE;
		}
		overrideURL = (boolean) a("DefaultResoucepackOverride", false);

		if (isVersionHigherThan(1, 9) || AutoDetectResourcepackVersion) {
			if (guntype == Material.DIAMOND_HOE) {
				url = url19plus;
			} else {
				url = url19plusAXE;
			}
		} else {
			// Use 1.8 resourcepack.
			url = url18;
		}

		if (overrideURL) {
			url = (String) a("DefaultResoucepack", url);
			url18 = (String) a("DefaultResoucepack_18", url18);
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
		if (saveTheConfig)
			saveConfig();

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

		List<String> stringsHealer = Arrays
				.asList(new String[] { getIngString(Material.WOOL, 0, 6), getIngString(Material.GOLDEN_APPLE, 0, 1) });
		if (!isVersionHigherThan(1, 9) || AutoDetectResourcepackVersion) {
			boolean forceUpdate = false;

			String additive = AutoDetectResourcepackVersion ? "_18" : "";

			GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), false, "default18_P30", "P30" + additive,
					ChatColor.GOLD + "P30", null, 0, stringsPistol, WeaponType.PISTOL, false, "556ammo", 3, 0.25,
					Material.IRON_HOE, 12, 1000, 1.5, 0.25, 1, false, 700, null, 80, true, null);
			GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), false, "default18_AK47", "AK47" + additive,
					ChatColor.GOLD + "AK-47", null, -1, stringsWoodRif, WeaponType.RIFLE, true, "556ammo", 3, 0.25,
					Material.GOLD_SPADE, 50, 1000, 1.5, 0.25, 2, true, 1400, ChargingHandlerEnum.RAPIDFIRE, 140, true,
					null);
			GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), false, "default18_MP5K", "MP5K" + additive,
					ChatColor.GOLD + "MP5K", null, 0, stringsMetalRif, WeaponType.SMG, true, "556ammo", 3, 0.25,
					Material.GOLD_PICKAXE, 32, 1000, 1.5, 0.25, 1, false, 1200, null, 100, true,
					WeaponSounds.GUN_SMALL);
			GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), false, "default18_FNFal", "FNFal" + additive,
					ChatColor.GOLD + "FN-Fal", null, 0, stringsMetalRif, WeaponType.RIFLE, true, "556ammo", 3, 0.25,
					Material.GOLD_HOE, 32, 1000, 1.5, 0.25, 1, false, 1000, null, 140, true, null);

			// The the type is not the same, or if it is, if there is no auto detection
			if (guntype != Material.DIAMOND_HOE || !AutoDetectResourcepackVersion)
				GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), false, "default18_RPG", "RPG" + additive,
						ChatColor.GOLD + "RPG", null, 0, stringsRPG, WeaponType.RPG, false, "RPGammo", 100, 0.1,
						Material.DIAMOND_HOE, 1, 200, 3, 3, 2, false, 5000, ChargingHandlerEnum.RPG, 220, true, null);
			if (guntype != Material.DIAMOND_AXE || !AutoDetectResourcepackVersion)
				GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), false, "default18_PKP", "PKP" + additive,
						ChatColor.GOLD + "PKP", null, 0, stringsMetalRif, WeaponType.RIFLE, false, "556ammo", 2, 0.3,
						Material.DIAMOND_AXE, 100, 1000, 3, 0.27, 3, true, 3000, ChargingHandlerEnum.RAPIDFIRE, 170,
						true, WeaponSounds.GUN_BIG);
			GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), false, "default18_M16", "M16" + additive,
					ChatColor.GOLD + "M16", null, 0, stringsMetalRif, WeaponType.RIFLE, false, "556ammo", 4, 0.3,
					Material.IRON_SPADE, 50, 1000, 0.11, 1.5, 2, true, 1200, ChargingHandlerEnum.RAPIDFIRE, 140, true,
					null);

			ArmoryYML skullammo = GunYMLCreator.createSkullAmmo(false, getDataFolder(), false, "default18_ammo556",
					"556ammo", "&7 5.56x45mm NATO", null, Material.SKULL_ITEM, 3, "cactus", null, 4, 1, 50);
			skullammo.set(false, "skull_owner_custom_url_COMMENT",
					"Only specify the custom URL if the head does not use a player's skin, and instead sets the skin to a base64 value. If you need to get the head using a command, the URL should be set to the string of letters after \"Properties:{textures:[{Value:\"");
			skullammo.set(false, "skull_owner_custom_url",
					"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTg3ZmRmNDU4N2E2NDQ5YmZjOGJlMzNhYjJlOTM4ZTM2YmYwNWU0MGY2ZmFhMjc3ZDcxYjUwYmNiMGVhNjgzOCJ9fX0=");
			ArmoryYML skullammo2 = GunYMLCreator.createSkullAmmo(false, getDataFolder(), false, "default18_ammoRPG",
					"RPGammo", "&7 Rocket", null, Material.SKULL_ITEM, 3, "cactus", null, 4, 1, 50);
			skullammo2.set(false, "skull_owner_custom_url_COMMENT",
					"Only specify the custom URL if the head does not use a player's skin, and instead sets the skin to a base64 value. If you need to get the head using a command, the URL should be set to the string of letters after \"Properties:{textures:[{Value:\"");
			skullammo2.set(false, "skull_owner_custom_url",
					"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTg3ZmRmNDU4N2E2NDQ5YmZjOGJlMzNhYjJlOTM4ZTM2YmYwNWU0MGY2ZmFhMjc3ZDcxYjUwYmNiMGVhNjgzOCJ9fX0=");

		}
		if (isVersionHigherThan(1, 9)) {

			boolean forceUpdate = false;

			GunYMLCreator.createAmmo(false, getDataFolder(), false, "9mm", "&f9mm", 15, stringsAmmo, 2, 0.7, 50, 10);
			GunYMLCreator.createAmmo(false, getDataFolder(), false, "556", "&f5x56.NATO", 14, stringsAmmo, 5, 1, 50, 5);
			GunYMLCreator.createAmmo(false, getDataFolder(), false, "shell", "&fBuckshot", 16, stringsAmmo, 10, 0.5, 8,
					4);
			GunYMLCreator.createAmmo(false, getDataFolder(), false, "rocket", "&fRocket", 17, stringsAmmoRPG, 100, 1000,
					1);

			GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), "P30", 2, stringsPistol, WeaponType.PISTOL, true,
					"9mm", 3, 0.3, 12, 1000, false, 800, null, 100, null);
			ArmoryYML pkp = GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), "PKP", 3, stringsMetalRif,
					WeaponType.RIFLE, true, "556", 2, 0.3, 100, 1000, 3, 0.27, 3, true, 3000,
					ChargingHandlerEnum.RAPIDFIRE, 170, WeaponSounds.GUN_BIG);
			pkp.set(false, "addMuzzleSmoke", true);
			GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), "MP5K", 4, stringsMetalRif, WeaponType.SMG, false,
					"9mm", 3, 0.3, 32, 1000, 3, true, 1000, ChargingHandlerEnum.RAPIDFIRE, 100, WeaponSounds.GUN_SMALL);
			ArmoryYML ak47 = GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), "AK47", 5, stringsWoodRif,
					WeaponType.RIFLE, false, "556", 4, 0.3, 50, 1000, 2, true, 1200, ChargingHandlerEnum.RAPIDFIRE, 140,
					null);
			ak47.set(false, "addMuzzleSmoke", true);
			ArmoryYML m16 = GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), "M16", 7, stringsMetalRif,
					WeaponType.RIFLE, true, "556", 4, 0.3, 50, 1000, 2, true, 1200, ChargingHandlerEnum.RAPIDFIRE, 140,
					null);
			m16.set(false, "addMuzzleSmoke", true);
			GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), "Remmington", 8, stringsMetalRif,
					WeaponType.SHOTGUN, false, "shell", 3, 0.15, 8, 1000, 5, 0.4, 10, false, 1400,
					ChargingHandlerEnum.PUMPACTION, 70, null);
			GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), "FNFal", 9, stringsWoodRif, WeaponType.RIFLE, true,
					"556", 3, 0.3, 32, 1000, 2, true, 1000, ChargingHandlerEnum.RAPIDFIRE, 140, null);

			ArmoryYML rpg = GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), "RPG", 10, stringsRPG,
					WeaponType.RPG, false, "rocket", 100, 0.1, 1, 200, false, 5000, ChargingHandlerEnum.RPG, 220, null);
			rpg.set(false, "addMuzzleSmoke", true);
			GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), "UMP", 11, stringsPistol, WeaponType.SMG, true,
					"9mm", 2, 0.3, 32, 1000, true, 1300, null, 100, null);
			GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), "SW1911", 12, stringsPistol, WeaponType.PISTOL,
					true, "9mm", 3, 0.3, 12, 1000, false, 800, null, 100, null);
			GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), "M40", 13, stringsWoodRif, WeaponType.SNIPER, true,
					"556", 3, 0.2, 6, 1000, false, 2000, ChargingHandlerEnum.BOLTACTION, 200, null);

			GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), "Enfield", 18, stringsPistol, WeaponType.PISTOL,
					true, "9mm", 3, 0.3, 6, 1000, 3, 0.25, 1, false, 500, ChargingHandlerEnum.REVOLVER, 80, null);
			GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), "HenryRifle", 19, stringsGoldRif, WeaponType.RIFLE,
					true, "556", 8, 0.3, 6, 1000, false, 1000, ChargingHandlerEnum.BREAKACTION, 100, null);
			GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), "Mouserc96", 20, stringsPistol, WeaponType.PISTOL,
					true, "9mm", 3, 0.3, 12, 1000, false, 800, null, 80, null);

			GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), "Dragunov", 23, stringsMetalRif, WeaponType.SNIPER,
					true, "556", 10, 0.2, 12, 1000, false, 2400, null, 140, null);
			GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), "Spas12", 24, stringsMetalRif, WeaponType.SHOTGUN,
					false, "shell", 2, 0.15, 8, 1000, 2, 0.5, 10, true, 2000, null, 80, null);
			ArmoryYML aa12 = GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), "AA12", 26, stringsMetalRif,
					WeaponType.SHOTGUN, false, "shell", 2, 0.15, 32, 1000, 10, true, 3300, null, 80, null);
			aa12.set(false, "addMuzzleSmoke", true);

			ArmoryYML magnum = GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), "Magnum", 38, stringsPistol,
					WeaponType.PISTOL, true, "9mm", 6, 0.3, 6, 1000, 2, 0.5, 1, false, 500,
					ChargingHandlerEnum.REVOLVER, 140, WeaponSounds.GUN_BIG);
			magnum.set(false, "addMuzzleSmoke", true);
			ArmoryYML awp = GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), "AWP", 39, stringsMetalRif,
					WeaponType.SNIPER, true, "556", 16, 0.3, 12, 1000, 2, 0.5, 1, false, 500,
					ChargingHandlerEnum.BOLTACTION, 260, WeaponSounds.GUN_BIG);
			awp.set(false, "addMuzzleSmoke", true);

			/*
			 * gunRegister.put(m(2), new P30((int) a("Weapon.P30.Durability", 500),
			 * getIngredients("P30", stringsPistol), (int) a("Weapon.P30.Damage", 3),
			 * (double) a("Weapon.P30.Price", 500.5))); gunRegister.put(m(3), new PKP((int)
			 * a("Weapon.PKP.Durability", 500), getIngredients("PKP", stringsPistol), (int)
			 * a("Weapon.PKP.Damage", 3), (double) a("Weapon.PKP.Price", 2000.0)));
			 * gunRegister.put(m(4), new MP5K((int) a("Weapon.MP5K.Durability", 1000),
			 * getIngredients("MP5K", stringsPistol), (int) a("Weapon.MP5K.Damage", 1),
			 * (double) a("Weapon.MP5K.Price", 800.0))); gunRegister.put(m(5), new
			 * AK47((int) a("Weapon.AK47.Durability", 1000), getIngredients("AK47",
			 * stringsWoodRif), (int) a("Weapon.AK47.Damage", 3), (double)
			 * a("Weapon.AK47.Price", 1000.0))); gunRegister.put(m(7), new M16((int)
			 * a("Weapon.M16.Durability", 1000), getIngredients("M16", stringsMetalRif),
			 * (int) a("Weapon.M16.Damage", 3), (double) a("Weapon.M16.Price", 1000.0)));
			 * gunRegister.put(m(8), new Remmington((int) a("Weapon.Remmington.Durability",
			 * 500), getIngredients("Remmington", stringsMetalRif), (int)
			 * a("Weapon.Remmington.Damage", 1), (double) a("Weapon.Remmington.Price",
			 * 1000.0))); gunRegister.put(m(9), new FNFal((int) a("Weapon.FNFal.Durability",
			 * 1000), getIngredients("FNFal", stringsMetalRif), (int)
			 * a("Weapon.FNFal.Damage", 1), (double) a("Weapon.FNFal.Price", 1000.0)));
			 * gunRegister.put(m(10), new RPG((int) a("Weapon.RPG.Durability", 100),
			 * getIngredients("RPG", stringsRPG), (double) a("Weapon.RPG.Price", 5000.0)));
			 * gunRegister.put(m(11), new UMP((int) a("Weapon.UMP.Durability", 1000),
			 * getIngredients("UMP", stringsPistol), (int) a("Weapon.UMP.Damage", 2),
			 * (double) a("Weapon.UMP.Price", 1000.0))); gunRegister.put(m(12), new
			 * SW1911((int) a("Weapon.SW1911.Durability", 500), getIngredients("SW1911",
			 * stringsPistol), (int) a("Weapon.SW1911.Damage", 2), (double)
			 * a("Weapon.SW1911.Price", 600.0))); gunRegister.put(m(13), new M40((int)
			 * a("Weapon.M40.Durability", 500), getIngredients("M40", stringsWoodRif), (int)
			 * a("Weapon.M40.Damage", 5), (double) a("Weapon.M40.Price", 1000.0)));
			 * 
			 * gunRegister.put(m(18), new Enfield((int) a("Weapon.Enfield.Durability", 500),
			 * getIngredients("Enfield", stringsGoldRif), (int) a("Weapon.Enfield.Damage",
			 * 2), (double) a("Weapon.Enfield.Price", 500.0))); gunRegister.put(m(19), new
			 * HenryRifle((int) a("Weapon.Henry.Durability", 500), getIngredients("Henry",
			 * stringsGoldRif), (int) a("Weapon.Henry.Damage", 3), (double)
			 * a("Weapon.Henry.Price", 1000.0))); gunRegister.put(m(20), new MouserC96((int)
			 * a("Weapon.MouserC96.Durability", 500), getIngredients("MouserC96",
			 * stringsPistol), (int) a("Weapon.MouserC96.Damage", 2), (double)
			 * a("Weapon.MouserC96.Price", 400.0)));
			 */
			ArmoryYML grenade = GunYMLCreator.createMisc(false, getDataFolder(), false, "default_grenade", "grenade",
					"&7Grenade",
					Arrays.asList(ChatColor.DARK_GRAY + "[LMB] to pull pin", ChatColor.DARK_GRAY + "[RMB] to throw",
							ChatColor.DARK_GRAY + "Grenades wait " + ChatColor.GRAY + "FIVE seconds"
									+ ChatColor.DARK_GRAY + " before exploding.",
							ChatColor.DARK_RED + "<!>Will Explode Even If Not Thrown<!>"),
					m(22), stringsGrenades, 100, WeaponType.GRENADES, 100, 1);
			grenade.set(false, "radius", 10);

			ArmoryYML smokegrenade = GunYMLCreator.createMisc(false, getDataFolder(), false, "default_smokegrenade",
					"smokegrenade", "&7Smoke Grenade",
					Arrays.asList(ChatColor.DARK_GRAY + "[LMB] to pull pin", ChatColor.DARK_GRAY + "[RMB] to throw",
							ChatColor.DARK_GRAY + "Smoke Grenades wait " + ChatColor.GRAY + "FIVE seconds"
									+ ChatColor.DARK_GRAY + " before exploding.",
							ChatColor.DARK_RED + "<!>Will Explode Even If Not Thrown<!>"),
					m(40), stringsGrenades, 100, WeaponType.SMOKE_GRENADES, 100, 1);
			smokegrenade.set(false, "radius", 5);
			ArmoryYML flashbanggrenade = GunYMLCreator.createMisc(false, getDataFolder(), false, "default_flashbang",
					"flashbang", "&7FlashBang",
					Arrays.asList(ChatColor.DARK_GRAY + "[LMB] to pull pin", ChatColor.DARK_GRAY + "[RMB] to throw",
							ChatColor.DARK_GRAY + "Flashbangs wait " + ChatColor.GRAY + "FIVE seconds"
									+ ChatColor.DARK_GRAY + " before exploding.",
							ChatColor.DARK_RED + "<!>Will Explode Even If Not Thrown<!>"),
					m(41), stringsGrenades, 100, WeaponType.FLASHBANGS, 100, 1);
			flashbanggrenade.set(false, "radius", 5);

			ArmoryYML p30sil = GunYMLCreator.createAttachment(false, getDataFolder(), false, "default_p30_silencer",
					"p30silenced", ChatColor.GOLD + "P30[Silenced]", null, m(42), stringsPistol, 1000, "p30");
			p30sil.set(false, "weaponsounds", WeaponSounds.SILENCEDSHOT.getName());
			@SuppressWarnings("unused")
			ArmoryYML awp2 = GunYMLCreator.createAttachment(false, getDataFolder(), false, "default_awp_asiimov",
					"awpasiimov", ChatColor.GOLD + "AWP[Asiimov-skin]", null, m(43), stringsMetalRif, 1000, "awp");
			// miscRegister.put(m(22),
			// new Grenades(getIngredients("Grenades", stringsGrenades), (double)
			// a("Weapon.Grenade.Price", 800.0),
			// (double) a("Weapon.Grenade.radiusdamage", 10.0), (double)
			// a("Weapon.Grenade.radius", 5.0)));

			/*
			 * gunRegister.put(m(23), new Dragunov((int) a("Weapon.Dragunov.Durability",
			 * 1000), getIngredients("Dragunov", stringsWoodRif), (int)
			 * a("Weapon.Dragunov.Damage", 6), (double) a("Weapon.Dragunov.Price",
			 * 1200.0))); gunRegister.put(m(24), new Spas12((int)
			 * a("Weapon.Spas.Durability", 1000), getIngredients("Spas", stringsMetalRif),
			 * (int) a("Weapon.Spas.Damage", 1), (double) a("Weapon.Spas.Price", 1200.0)));
			 */

			armorRegister.put(m(25), new Kevlar(m(25), getIngredients("Kevlarnk1", stringsMetalRif),
					(int) a("Weapon.Kevlarmk1.DamageThreshold", 1), (double) a("Weapon.Kevlarmk1.Price", 1200.0)));
			/*
			 * gunRegister.put(m(26), new AA12((int) a("Weapon.AA12.Durability", 1000),
			 * getIngredients("AA12", stringsMetalRif), (int) a("Weapon.AA12.Damage", 1),
			 * (double) a("Weapon.AA12.Price", 2000.0)));
			 */
			GunYMLCreator.createMisc(false, getDataFolder(), false, "default_Medkit_camo", "medkitcamo", "&5Medkit",
					null, m(37), stringsHealer, 300, WeaponType.MEDKIT, 1, 1000);
			// miscRegister.put(m(37), new MedKit(m(37),"MedkitCamo", ChatColor.WHITE + "
			// Medkit", 200));

		}
		if (saveTheConfig)
			saveConfig();

		GunYMLCreator.createNewGun(false, getDataFolder(), true, "ExampleGun", "Examplegun", ChatColor.GOLD + "Example",
				Arrays.asList("Hello", "more lines"), 28, stringsGoldRif, WeaponType.PISTOL, false, "556", 3, 0.3,
				Material.DIAMOND_AXE, 100, 1000, 1.5, 0.25, 1, true, 1000000, null, 120, false, WeaponSounds.GUN_AUTO);
		GunYMLCreator.createNewGun(false, getDataFolder(), true, "ExampleGun", "Examplegun", ChatColor.GOLD + "Example",
				Arrays.asList("Hello", "more lines"), 28, stringsGoldRif, WeaponType.PISTOL, false, "556", 3, 0.3,
				Material.DIAMOND_AXE, 100, 1000, 1.5, 0.25, 1, true, 1000000, null, 120, false, WeaponSounds.GUN_SMALL);
		GunYMLCreator.createAmmo(false, getDataFolder(), true, "example_ammo", "example", "7fDisplayname",
				Arrays.asList("Example", "Lore"), Material.DIAMOND_AXE, 27, stringsAmmo, 1, 1.0, 16);

		GunYMLCreator.createMisc(false, getDataFolder(), true, "example_knife", "ExampleKnife", "&7Example Knife",
				Arrays.asList("Now, this is a knife!"), Material.IRON_SWORD, 0, stringsMetalRif, 100, WeaponType.MEELEE,
				12, 100);
		GunYMLCreator.createAttachment(false, getDataFolder(), true, "example_attachment", "example_attachment",
				"Attachment For AK47", null, m(28), stringsMetalRif, 100, "AK47");

		ArmoryYML skullammo = GunYMLCreator.createSkullAmmo(false, getDataFolder(), true, "example_skullammo",
				"exampleSkullAmmo", "&7 Example Ammo", null, Material.SKULL_ITEM, 3, "cactus", null, 4, 1, 64);
		skullammo.set(false, "skull_owner_custom_url_COMMENT",
				"Only specify the custom URL if the head does not use a player's skin, and instead sets the skin to a base64 value. If you need to get the head using a command, the URL should be set to the string of letters after \"Properties:{textures:[{Value:\"");
		skullammo.set(false, "skull_owner_custom_url",
				"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTg3ZmRmNDU4N2E2NDQ5YmZjOGJlMzNhYjJlOTM4ZTM2YmYwNWU0MGY2ZmFhMjc3ZDcxYjUwYmNiMGVhNjgzOCJ9fX0=");
		// Skull texture
		GunYMLLoader.loadAmmo(this);
		GunYMLLoader.loadMisc(this);
		GunYMLLoader.loadGuns(this);
		GunYMLLoader.loadAttachments(this);
		if (Main.enableBleeding)
			BulletWoundHandler.startTimer();
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onHit(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			Player d = (Player) e.getDamager();
			if ((e.getCause() == DamageCause.ENTITY_ATTACK || e.getCause() == DamageCause.ENTITY_SWEEP_ATTACK)
					&& isMisc(d.getItemInHand())) {
				ArmoryBaseObject aa = getMisc(d.getItemInHand());
				if (aa instanceof MeleeItems) {
					e.setDamage(((MeleeItems) aa).getDamage());
				}
			}
		}
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
				if (isGun(e.getPlayer().getItemInHand()) || isGunWithAttchments(e.getPlayer().getItemInHand())) {
					Gun g = getGun(e.getPlayer().getItemInHand());
					AttachmentBase attach = getGunWithAttchments(e.getPlayer().getItemInHand());
					if (g == null && attach != null)
						g = gunRegister.get(attach.getBase());
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
										ItemStack ironsights = ItemFact.getIronSights();
										e.getPlayer().getInventory().setItemInMainHand(ironsights);
										if (tempremove != null) {
											e.getPlayer().getInventory().addItem(tempremove);
										}

									} catch (Error e2) {
										e2.printStackTrace();
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
			if (b("version", args[0]))
				s.add("version");
			if (enableShop)
				if (b("shop", args[0]))
					s.add("shop");
			if (enableCrafting)
				if (b("craft", args[0]))
					s.add("craft");
			if (b("getOpenGunSlot", args[0]))
				s.add("getOpenGunSlot");
			if (sender.hasPermission("qualityarmory.reload"))
				if (b("reload", args[0]))
					s.add("reload");
			if (sender.hasPermission("qualityarmory.createnewitem")) {
				if (b("createNewAmmoGun", args[0]))
					s.add("createNewGun");
				if (b("createNewAmmo", args[0]))
					s.add("createNewAmmo");
			}

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
				for (Entry<MaterialStorage, ArmorObject> e : armorRegister.entrySet())
					if (b(e.getValue().getName(), args[1]))
						s.add(e.getValue().getName());
				for (Entry<MaterialStorage, AttachmentBase> e : attachmentRegister.entrySet())
					if (b(e.getValue().getAttachmentName(), args[1]))
						s.add(e.getValue().getAttachmentName());

			}
			return s;
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if (e.getPlayer().getItemInHand() != null
				&& (isGun(e.getPlayer().getItemInHand()) || isAmmo(e.getPlayer().getItemInHand())
						|| isIS(e.getPlayer().getItemInHand()) || isArmor(e.getPlayer().getItemInHand()))) {
			e.setCancelled(true);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("QualityArmory")) {
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("version")) {
					sender.sendMessage(prefix+ChatColor.WHITE+" This server is using version "+ChatColor.GREEN+this.getDescription().getVersion()+ChatColor.WHITE+" of QualityArmory");
					sender.sendMessage("--==Changelog==--");
					InputStream in = getClass().getResourceAsStream("/changelog.txt"); 
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					for(int i = 0; i <7;i++) {
						try {
							String s = reader.readLine();
							if(s.length() <= 1)
								break;
							if(i==6) {
								sender.sendMessage("......");
								break;
							}
							sender.sendMessage(s);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					return true;
				}
				if (args[0].equalsIgnoreCase("getOpenGunSlot")) {
					if (sender.hasPermission("qualityarmory.getopengunslot")) {
						List<MaterialStorage> getAllKeys = new ArrayList<>();
						getAllKeys.addAll(gunRegister.keySet());
						getAllKeys.addAll(ammoRegister.keySet());
						getAllKeys.addAll(miscRegister.keySet());
						getAllKeys.addAll(armorRegister.keySet());
						getAllKeys.addAll(reservedForExps);
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
				if (args[0].equalsIgnoreCase("createNewAmmo")) {
					if (sender.hasPermission("qualityarmory.createnewitem")) {
						if (args.length >= 2) {
							ItemStack itemInHand = ((Player) sender).getItemInHand();
							if (itemInHand == null) {
								sender.sendMessage(prefix + " You need to hold an item that will be used as ammo.");

								return true;
							}
							GunYMLCreator.createSkullAmmo(true, getDataFolder(), false, "custom_" + args[1], args[1],
									args[1], Arrays.asList("Custom_item"), itemInHand.getType(),
									itemInHand.getDurability(),
									(itemInHand.getType() == Material.SKULL_ITEM
											? ((SkullMeta) itemInHand.getItemMeta()).getOwner()
											: null),
									null, 100, 1, 64);
							sender.sendMessage(prefix + " A new ammo type has been created.");
							sender.sendMessage(
									prefix + " You will need to use /qa reload for the new ammo type to appear.");
						} else {
							sendHelp(sender);
						}
					} else {
						sender.sendMessage(prefix + ChatColor.RED + S_NOPERM);
						return true;
					}
					return true;
				}

				if (args[0].equalsIgnoreCase("createNewGun")) {
					if (sender.hasPermission("qualityarmory.createnewitem")) {
						if (args.length >= 2) {
							ItemStack itemInHand = ((Player) sender).getItemInHand();
							if (itemInHand == null) {
								sender.sendMessage(prefix + " You need to hold an item that will be used as gun");
								return true;
							}
							GunYMLCreator.createNewGun(false, getDataFolder(), false, "custom_" + args[1], args[1],
									args[1], Arrays.asList("Custom_item"), itemInHand.getDurability(), null,
									WeaponType.RIFLE, false, "9mm", 1, 0.2, itemInHand.getType(), 64, 100, 1.5, 0.2, 1,
									false, 200, null, 120, 0, (AutoDetectResourcepackVersion), WeaponSounds.GUN_MEDIUM);
							GunYMLCreator.createSkullAmmo(true, getDataFolder(), true, "custom_" + args[1], args[1],
									args[1], Arrays.asList("Custom_item"), itemInHand.getType(),
									itemInHand.getDurability(),
									(itemInHand.getType() == Material.SKULL_ITEM
											? ((SkullMeta) itemInHand.getItemMeta()).getOwner()
											: null),
									null, 100, 1, 64);
							sender.sendMessage(prefix + " A new gun has been created.");
							sender.sendMessage(prefix + " You will need to use /qa reload for the new gun to appear.");
						} else {
							sendHelp(sender);
						}
					} else {
						sender.sendMessage(prefix + ChatColor.RED + S_NOPERM);
						return true;
					}
					return true;
				}

				if (args[0].equalsIgnoreCase("listItemIds")) {
					if (sender.hasPermission("qualityarmory.getmaterialsused")) {
						for (ArmoryBaseObject g : miscRegister.values())
							sender.sendMessage(ChatColor.GREEN + g.getName() + ": " + ChatColor.WHITE
									+ g.getItemData().getMat().name() + " : " + g.getItemData().getData());
						for (Gun g : gunRegister.values())
							sender.sendMessage(ChatColor.GOLD + g.getName() + ": " + ChatColor.WHITE
									+ g.getItemData().getMat().name() + " : " + g.getItemData().getData());
						for (Ammo g : ammoRegister.values())
							sender.sendMessage(ChatColor.AQUA + g.getName() + ": " + ChatColor.WHITE
									+ g.getItemData().getMat().name() + " : " + g.getItemData().getData());
					} else {
						sender.sendMessage(prefix + ChatColor.RED + S_NOPERM);
						return true;
					}
				}
				if (args[0].equalsIgnoreCase("debug")) {
					BulletWoundHandler.bulletHit((Player) sender, 1);
					return true;
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
						sb.append(ChatColor.GRAY);
						for (ArmorObject g : armorRegister.values()) {
							sb.append(g.getName() + ",");
						}
						sb.append(ChatColor.WHITE);
						for (AttachmentBase g : attachmentRegister.values()) {
							sb.append(g.getAttachmentName() + ",");
						}
						sender.sendMessage(prefix + sb.toString());
						return true;
					}

					ArmoryBaseObject g = null;
					AttachmentBase attachment = null;
					// for (int j = 1; j < args.length; j++) {
					// gunName.append(args[j]);
					// if (j != args.length - 1)
					// gunName.append(" ");
					// }
					// Check if it is a gun, then if it is ammo, then if it is misc
					for (Entry<MaterialStorage, Gun> e : gunRegister.entrySet())
						if (e.getValue().getName().equalsIgnoreCase(args[1])) {
							g = e.getValue();
							break;
						}
					if (g == null)
						for (Entry<MaterialStorage, Ammo> e : ammoRegister.entrySet())
							if (e.getValue().getName().equalsIgnoreCase(args[1])) {
								g = e.getValue();
								break;
							}
					if (g == null)
						for (Entry<MaterialStorage, ArmoryBaseObject> e : miscRegister.entrySet())
							if (e.getValue().getName().equalsIgnoreCase(args[1])) {
								g = e.getValue();
								break;
							}
					if (g == null)
						for (Entry<MaterialStorage, ArmorObject> e : armorRegister.entrySet())
							if (e.getValue().getName().equalsIgnoreCase(args[1])) {
								g = e.getValue();
								break;
							}
					if (g == null)
						for (Entry<MaterialStorage, AttachmentBase> e : attachmentRegister.entrySet())
							if (e.getValue().getAttachmentName().equalsIgnoreCase(args[1])) {
								// g = e.getValue().getBase();
								attachment = e.getValue();
								g = gunRegister.get(attachment.getBase());
								break;
							}
					if (g != null || attachment != null) {
						Player who = null;
						if (args.length > 2)
							who = Bukkit.getPlayer(args[2]);
						else if (sender instanceof Player)
							who = ((Player) sender);
						else {
							sender.sendMessage("A USER IS REQUIRED FOR CONSOLE. /QA give <gun> <player>");
						}
						if (who == null) {
							sender.sendMessage("That player is not online");
							return true;
						}

						ItemStack temp;

						if (g instanceof Gun) {
							temp = ItemFact.getGun((Gun) g, attachment);
							who.getInventory().addItem(temp);
						} else if (g instanceof Ammo) {
							// temp = ItemFact.getAmmo((Ammo) g);
							AmmoUtil.addAmmo(who, (Ammo) g, ((Ammo) g).getMaxAmount());
						} else {
							temp = ItemFact.getObject(g);
							temp.setAmount(g.getCraftingReturn());
							who.getInventory().addItem(temp);
						}

						sender.sendMessage(prefix + " Adding " + g.getName() + " to your inventory");
					} else {
						sender.sendMessage(prefix + " Could not find item \"" + args[1] + "\"");
					}
					return true;
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
				if (enableCrafting)
					if (args[0].equalsIgnoreCase("craft")) {
						if (!sender.hasPermission("qualityarmory.craft")) {
							sender.sendMessage(prefix + ChatColor.RED + S_NOPERM);
							return true;
						}
						player.openInventory(createCraft(0));
						return true;

					}
				if (enableShop)
					if (args[0].equalsIgnoreCase("shop")) {
						if (!sender.hasPermission("qualityarmory.shop")) {
							sender.sendMessage(prefix + ChatColor.RED + S_NOPERM);
							return true;
						}
						player.openInventory(createShop(0));
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
		sender.sendMessage(ChatColor.GOLD + "/QA give <Item>:" + ChatColor.GRAY
				+ " Gives the sender the item specified (guns, ammo, misc.)");
		sender.sendMessage(ChatColor.GOLD + "/QA craft:" + ChatColor.GRAY + " Opens the crafting menu.");
		sender.sendMessage(ChatColor.GOLD + "/QA shop: " + ChatColor.GRAY + "Opens the shop menu");
		sender.sendMessage(
				ChatColor.GOLD + "/QA listItemIds: " + ChatColor.GRAY + "Lists the materials and data for all items.");
		if (sender.hasPermission("qualityarmory.reload"))
			sender.sendMessage(ChatColor.GOLD + "/QA reload: " + ChatColor.GRAY + "reloads all values in QA.");
		if (sender.hasPermission("qualityarmory.createnewitem")) {
			sender.sendMessage(ChatColor.GOLD + "/QA createNewGun <name>: " + ChatColor.GRAY
					+ "Creats a new gun using the item in your hand as a template.");
			sender.sendMessage(ChatColor.GOLD + "/QA createNewAmmo <name>: " + ChatColor.GRAY
					+ "Creats a new ammo type using the item in your hand as a template");
		}
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
			if (e.getClickedInventory() != null)
				name = e.getClickedInventory().getTitle();
		} catch (Error | Exception e4) {
			if (e.getInventory() != null)
				name = e.getInventory().getTitle();
		}

		if (name != null && name.startsWith(S_craftingBenchName)) {
			e.setCancelled(true);
			if (e.getCurrentItem() != null) {

				if (e.getCurrentItem().equals(prevButton)) {
					int page = Integer.parseInt(e.getInventory().getTitle().split(S_craftingBenchName)[1]) - 1 - 1;
					e.getWhoClicked().openInventory(createCraft(Math.max(0, page)));
					return;
				}
				if (e.getCurrentItem().equals(nextButton)) {
					int page = Integer.parseInt(e.getInventory().getTitle().split(S_craftingBenchName)[1]) - 1 + 1;
					e.getWhoClicked().openInventory(createCraft(Math.min(getMaxPages(), page)));
					return;
				}

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
				} else if (isGunWithAttchments(e.getCurrentItem())) {
					AttachmentBase g = getGunWithAttchments(e.getCurrentItem());
					Gun g2 = gunRegister.get(g.getBase());
					if (lookForIngre((Player) e.getWhoClicked(), g)
							|| e.getWhoClicked().getGameMode() == GameMode.CREATIVE) {
						removeForIngre((Player) e.getWhoClicked(), g);
						ItemStack s = ItemFact.getGun(g);
						s.setAmount(g2.getCraftingReturn());
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
				} else if (isArmor(e.getCurrentItem())) {
					ArmorObject g = getArmor(e.getCurrentItem());
					if (lookForIngre((Player) e.getWhoClicked(), g)
							|| e.getWhoClicked().getGameMode() == GameMode.CREATIVE) {
						removeForIngre((Player) e.getWhoClicked(), g);
						ItemStack s = ItemFact.getArmor(g);
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
		} else if (name != null && S_shopName != null && name.startsWith(S_shopName)) {
			e.setCancelled(true);
			if (!enableEconomy) {
				e.getWhoClicked().closeInventory();
				e.getWhoClicked().sendMessage(prefix + S_noEcon);
				return;
			}
			if (e.getCurrentItem() != null) {

				if (e.getCurrentItem().equals(prevButton)) {
					int page = Integer.parseInt(e.getInventory().getTitle().split(S_shopName)[1]) - 1 - 1;
					e.getWhoClicked().openInventory(createShop(Math.max(0, page)));
					return;
				}
				if (e.getCurrentItem().equals(nextButton)) {
					int page = Integer.parseInt(e.getInventory().getTitle().split(S_shopName)[1]) - 1 + 1;
					e.getWhoClicked().openInventory(createShop(Math.min(getMaxPages(), page)));
					return;
				}

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

	public boolean lookForIngre(Player player, AttachmentBase a) {
		return lookForIngre(player, a.getCraftingRequirements());
	}

	public boolean lookForIngre(Player player, ArmoryBaseObject a) {
		return lookForIngre(player, a.getIngredients());
	}

	public boolean lookForIngre(Player player, ItemStack[] ings) {
		if (ings == null)
			return true;
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

	public boolean removeForIngre(Player player, AttachmentBase a) {
		return removeForIngre(player, a.getCraftingRequirements());
	}

	public boolean removeForIngre(Player player, ArmoryBaseObject a) {
		return removeForIngre(player, a.getIngredients());
	}

	public boolean removeForIngre(Player player, ItemStack[] ings) {
		if (ings == null)
			return true;
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
	public void onHeadPlace(BlockPlaceEvent e) {
		if (isAmmo(e.getItemInHand()) || isGun(e.getItemInHand()))
			e.setCancelled(true);
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		if (reloadingTasks.containsKey(e.getEntity().getUniqueId())) {
			for (BukkitTask r : reloadingTasks.get(e.getEntity().getUniqueId())) {
				r.cancel();
			}
		}
		reloadingTasks.remove(e.getEntity().getUniqueId());

		for (ItemStack is : new ArrayList<>(e.getDrops())) {
			if (isIS(is))
				e.getDrops().remove(is);
		}

		if (e.getDeathMessage().contains(IronSightsToggleItem.getItemName())) {
			try {
				e.setDeathMessage(e.getDeathMessage().replaceAll(IronSightsToggleItem.getItemName(),
						e.getEntity().getKiller().getInventory().getItemInOffHand().getItemMeta().getDisplayName()));
			} catch (Error | Exception e34) {
			}
		}

		BulletWoundHandler.bleedoutMultiplier.remove(e.getEntity().getUniqueId());
		BulletWoundHandler.bloodLevel.put(e.getEntity().getUniqueId(), bulletWound_initialbloodamount);
	}

	@EventHandler
	public void onDeath(PlayerRespawnEvent e) {
		BulletWoundHandler.bleedoutMultiplier.remove(e.getPlayer().getUniqueId());
		BulletWoundHandler.bloodLevel.put(e.getPlayer().getUniqueId(), bulletWound_initialbloodamount);
	}

	@SuppressWarnings({ "deprecation", "null" })
	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		Main.DEBUG("InteractEvent Called");
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType() == Material.ANVIL
				&& overrideAnvil && !e.getPlayer().isSneaking()) {
			if (shouldSend && !resourcepackReq.contains(e.getPlayer().getUniqueId())) {
				sendPacket(e.getPlayer(), true);
				e.setCancelled(true);
				Main.DEBUG("Resourcepack message being sent!");
				return;
			}
			if (!e.getPlayer().hasPermission("qualityarmory.craft")) {
				e.getPlayer().sendMessage(prefix + ChatColor.RED + S_ANVIL);
				return;
			}
			e.getPlayer().openInventory(createCraft(0));
			e.setCancelled(true);
			Main.DEBUG("Opening crafting menu");
			return;
		}

		if (e.getItem() != null) {
			// Quick bugfix for specifically this item.
			if (!isCustomItem(e.getItem())) {
				Main.DEBUG("Item is not any valid item");
				if (gunRegister
						.containsKey(MaterialStorage.getMS(e.getItem().getType(),
								(int) (e.getItem().getDurability() + 1), -1, "-1", "-1"))
						|| ammoRegister.containsKey(MaterialStorage.getMS(e.getItem().getType(),
								(int) (e.getItem().getDurability() + 1), -1, "-1", "-1"))
						|| miscRegister.containsKey(MaterialStorage.getMS(e.getItem().getType(),
								(int) (e.getItem().getDurability() + 1), -1, "-1", "-1"))
						|| armorRegister.containsKey(MaterialStorage.getMS(e.getItem().getType(),
								(int) (e.getItem().getDurability() + 1), -1, "-1", "-1"))
						|| attachmentRegister.containsKey(MaterialStorage.getMS(e.getItem().getType(),
								(int) (e.getItem().getDurability() + 1), -1, "-1", "-1"))
						|| reservedForExps.contains(MaterialStorage.getMS(e.getItem().getType(),
								(int) (e.getItem().getDurability() + 1), -1, "-1", "-1"))) {
					Main.DEBUG("A player is using a non-gun item, but may reach the textures of one!");
					// If the item is not a gun, but the item below it is
					int safeDurib = findSafeSpot(e.getItem(), true);

					// if (e.getItem().getDurability() == 1) {
					Main.DEBUG("Safe Durib " + (safeDurib + 4) + "! ORG " + e.getItem().getDurability());
					ItemStack is = e.getItem();
					is.setDurability((short) (safeDurib + 4));
					e.getPlayer().getInventory().setItem(e.getPlayer().getInventory().getHeldItemSlot(), is);
					// }
				}
				return;
			}

			ItemStack usedItem = e.getPlayer().getItemInHand();

			try {
				if (usedItem.getEnchantments().containsKey(Enchantment.MENDING))
					if (!isCustomItem(usedItem, 0) && isCustomItem(usedItem, -2)) {
						ItemStack temp2 = usedItem;
						temp2.setDurability((short) Math.max((findSafeSpot(usedItem, false) - 1), 0));
						e.getPlayer().setItemInHand(temp2);
						return;
					}
			} catch (Error | Exception e4532) {

			}

			// Sedn the resourcepack if the player does not have it.
			if (shouldSend && !resourcepackReq.contains(e.getPlayer().getUniqueId())) {
				Main.DEBUG("Player does not have resourcepack!");
				sendPacket(e.getPlayer(), true);
			}

			if (isArmor(usedItem)) {
				if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
					Main.DEBUG("A Player is about to put on armor!");
					ItemStack helm = e.getPlayer().getInventory().getHelmet();
					e.getPlayer().setItemInHand(helm);
					e.getPlayer().getInventory().setHelmet(usedItem);
					try {
						e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ITEM_ARMOR_EQUIP_IRON, 2, 1);
					} catch (Error | Exception e3) {
					}
					e.setCancelled(true);
					return;
				}
			}

			if (isGun(usedItem) || isGunWithAttchments(usedItem)) {
				try {
					if (AutoDetectResourcepackVersion) {
						if (us.myles.ViaVersion.bukkit.util.ProtocolSupportUtil
								.getProtocolVersion(e.getPlayer()) < ID18) {
							Gun g = getGun(usedItem);
							if (g == null)
								g = gunRegister.get(getGunWithAttchments(usedItem).getBase());

							if (!g.is18Support()) {
								for (Gun g2 : gunRegister.values()) {
									if (g2.is18Support()) {
										if (g2.getDisplayName().equals(g.getDisplayName())) {
											e.getPlayer().setItemInHand(ItemFact.getGun(g2));
											Main.DEBUG("Custom-validation check 1");
											return;
										}
									}
								}
								// If there is no exact match for 1.8, get the closest gun that uses the same
								// ammo type.
								for (Gun g2 : gunRegister.values()) {
									if (g2.is18Support()) {
										if (g2.getAmmoType().equals(g.getAmmoType())) {
											e.getPlayer().setItemInHand(ItemFact.getGun(g2));
											Main.DEBUG("Custom-validation check 2");
											return;
										}
									}
								}
							}
						} else {
							if (us.myles.ViaVersion.bukkit.util.ProtocolSupportUtil
									.getProtocolVersion(e.getPlayer()) >= ID18) {
								Gun g = getGun(usedItem);
								if (g == null)
									g = gunRegister.get(getGunWithAttchments(usedItem).getBase());
								if (g.is18Support()) {
									for (Gun g2 : gunRegister.values()) {
										if (!g2.is18Support()) {
											if (g2.getDisplayName().equals(g.getDisplayName())) {
												e.getPlayer().setItemInHand(ItemFact.getGun(g2));
												Main.DEBUG("Custom-validation check 3");
												return;
											}
										}
									}
									// If there is no exact match for 1.8, get the closest gun that uses the same
									// ammo type.
									for (Gun g2 : gunRegister.values()) {
										if (!g2.is18Support()) {
											if (g2.getAmmoType().equals(g.getAmmoType())) {
												e.getPlayer().setItemInHand(ItemFact.getGun(g2));
												Main.DEBUG("Custom-validation check 4");
												return;
											}
										}
									}
								}
							}
						}
					}
				} catch (Error | Exception e4) {
				}

				try {
					if (isVersionHigherThan(1, 9)) {
						UUID.fromString(usedItem.getItemMeta().getLocalizedName());
						Main.DEBUG("Gun-Validation check - 1");
					} else {
						String k = null;
						k.toString();
						// Just throw the error
					}
				} catch (Error | Exception e34) {
					if (isVersionHigherThan(1, 9)) {
						if (!usedItem.getItemMeta().hasDisplayName() || !usedItem.getItemMeta().hasLore()) {
							ItemStack is = ItemFact.getGun(MaterialStorage.getMS(usedItem));
							e.setCancelled(true);
							e.getPlayer().setItemInHand(is);
							Main.DEBUG("Gun-Validation check - 2");
							return;
						}
					}
				}
			}

			boolean offhand = false;

			if (isMisc(usedItem)) {
				Main.DEBUG("Misc item is being used!");
				ArmoryBaseObject base = getMisc(usedItem);
				if (base instanceof InteractableMisc) {
					e.setCancelled(true);
					if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
						((InteractableMisc) base).onRightClick(e.getPlayer());
					} else {
						((InteractableMisc) base).onLeftClick(e.getPlayer());
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
						if ((e.getAction() == Action.RIGHT_CLICK_AIR
								|| e.getAction() == Action.RIGHT_CLICK_BLOCK) == (USE_DEFAULT_CONTROLS)) {
							if (!e.getPlayer().isSneaking() || !getGun(usedItem).isAutomatic()) {
								e.setCancelled(true);
								e.getPlayer().getInventory()
										.setItemInMainHand(e.getPlayer().getInventory().getItemInOffHand());
								e.getPlayer().getInventory().setItemInOffHand(null);
								Main.DEBUG("Swapping gun from offhand to main hand!");
								return;
							}
						}
					}
				} catch (Error e2) {
				}
			}
			if (isAmmo(usedItem)) {
				Main.DEBUG("The item being click is ammo!");
				if (usedItem.getType() == Material.DIAMOND_HOE && e.getAction() == Action.RIGHT_CLICK_BLOCK
						&& (e.getClickedBlock().getType() == Material.DIRT
								|| e.getClickedBlock().getType() == Material.GRASS
								|| e.getClickedBlock().getType() == Material.GRASS_PATH
								|| e.getClickedBlock().getType() == Material.MYCEL))
					e.setCancelled(true);
				return;
			}

			// Check to make sure the gun is not a dup. This should be fast
			if (isGun(usedItem) || isGunWithAttchments(usedItem)) {
				Main.DEBUG("Dups check");
				checkforDups(e.getPlayer(), usedItem);
			}

			Gun g = getGun(usedItem);
			AttachmentBase attachment = getGunWithAttchments(usedItem);
			if (g == null)
				g = gunRegister.get(attachment.getBase());
			Main.DEBUG("Made it to gun/attachment check : " + g + " - " + attachment);
			if (enableInteractChests) {
				if (e.getClickedBlock() != null && (e.getClickedBlock().getType() == Material.CHEST
						|| e.getClickedBlock().getType() == Material.TRAPPED_CHEST)) {
					Main.DEBUG("Chest interactable check has return true!");
					return;
				}
				// Return with no shots if EIC is enabled for chests.
			}

			e.setCancelled(true);
			if (((e.getAction() == Action.LEFT_CLICK_AIR
					|| e.getAction() == Action.LEFT_CLICK_BLOCK) == USE_DEFAULT_CONTROLS)
					|| (USE_DEFAULT_CONTROLS && g != null && g.isAutomatic() && e.getPlayer().isSneaking())) {
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
									Main.DEBUG("OffHandChecker was disabled for shooting!");
									return;
								}
							} catch (Error | Exception e4) {
							}
							if (g.isAutomatic() && RapidFireCharger.shooters.containsKey(e.getPlayer().getUniqueId())) {
								RapidFireCharger.shooters.remove(e.getPlayer().getUniqueId()).cancel();
							} else {
								g.shoot(e.getPlayer(), attachment);
								if (enableDurability)
									if (offhand) {
										try {
											e.getPlayer().getInventory().setItemInOffHand(ItemFact.damage(g, usedItem));
										} catch (Error e2) {
										}
									} else {
										e.getPlayer().setItemInHand(ItemFact.damage(g, usedItem));
									}
							}
							sendHotbarGunAmmoCount(e.getPlayer(), g, attachment, usedItem);
							return;
						} else {
							Main.DEBUG("Worldguard region canceled the event");
						}
						sendHotbarGunAmmoCount(e.getPlayer(), g, attachment, usedItem);
						// TODO: Verify that the gun is in the main hand.
						// Shouldn't work for offhand, but it should still
						// be checked later.
					}
				}
			} else {
				if (enableIronSightsON_RIGHT_CLICK) {
					if (!Update19OffhandChecker.supportOffhand(e.getPlayer())) {
						enableIronSightsON_RIGHT_CLICK = false;
						Main.DEBUG("Offhand checker returned false for the player. Disabling ironsights");
						return;
					}
					// Rest should be okay
					if (g != null) {
						if (g.hasIronSights()) {
							try {

								if (e.getPlayer().getItemInHand().getItemMeta().getDisplayName()
										.contains("Reloading.")) {
									Main.DEBUG("Reloading message 1!");
									return;
								}

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

								sendHotbarGunAmmoCount(e.getPlayer(), g, attachment, usedItem);
							} catch (Error e2) {
								Bukkit.broadcastMessage(prefix
										+ "Ironsights not compatible for versions lower than 1.8. The server owner should set EnableIronSights to false in the plugin's config");
							}
						} else {
							if (!enableDurability || ItemFact.getDamage(usedItem) > 0) {
								if (allowGunsInRegion(e.getPlayer().getLocation())) {
									g.shoot(e.getPlayer(), attachment);
									if (enableDurability)
										if (offhand) {
											e.getPlayer().getInventory().setItemInOffHand(ItemFact.damage(g, usedItem));
										} else {
											e.getPlayer().setItemInHand(ItemFact.damage(g, usedItem));
										}
								}
								sendHotbarGunAmmoCount(e.getPlayer(), g, attachment, usedItem);
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
							if (allowGunReload)
								if (g.playerHasAmmo(e.getPlayer())) {
									Main.DEBUG("Trying to reload. player has ammo");
									g.reload(e.getPlayer(), attachment);
								} else {
									Main.DEBUG("Trying to reload. player DOES NOT have ammo");
								}

							sendHotbarGunAmmoCount(e.getPlayer(), g, attachment, usedItem);
						}
					}
				}
				Main.DEBUG("Reached end for gun-check!");
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
		if (reloadingTasks.containsKey(e.getPlayer().getUniqueId())) {
			for (BukkitTask r : reloadingTasks.get(e.getPlayer().getUniqueId())) {
				r.cancel();
			}
		}
		reloadingTasks.remove(e.getPlayer().getUniqueId());
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
		if (guntype == Material.DIAMOND_HOE && !hideTextureWarnings) {
			Bukkit.broadcastMessage(prefix
					+ " QA is now moving all items to a new Diamond_Axe system to prevent conflicts with other plugins.");
			Bukkit.broadcastMessage(prefix
					+ " Please delete the \"gunMaterialType\"value in the config or hide these warnings by setting \"hideTextureWarnings\" to true.");
		}
		if (sendOnJoin) {
			sendPacket(e.getPlayer(), sendTitleOnJoin);
		} else {
			for (ItemStack i : e.getPlayer().getInventory().getContents()) {
				if (i != null && (isGun(i) || isAmmo(i) || isMisc(i))) {
					if (shouldSend && !resourcepackReq.contains(e.getPlayer().getUniqueId())) {
						new BukkitRunnable() {
							@Override
							public void run() {
								sendPacket(e.getPlayer(), false);
							}
						}.runTaskLater(this, 0);
					}
					break;
				}
			}
		}
	}

	@SuppressWarnings({ "deprecation", "unlikely-arg-type" })
	@EventHandler
	public void onDrop(final PlayerDropItemEvent e) {

		if (isIS(e.getItemDrop().getItemStack())) {
			e.setCancelled(true);
			return;
		}

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
				if (!reloadingTasks.containsKey(e.getPlayer().getUniqueId())) {
					Gun g = getGun(e.getItemDrop().getItemStack());
					if (g != null) {
						ItemStack fix = e.getItemDrop().getItemStack();
						ItemMeta temp = fix.getItemMeta();
						temp.setDisplayName(g.getDisplayName());
						fix.setItemMeta(temp);
						e.getItemDrop().setItemStack(fix);
						e.setCancelled(false);
						return;
					}
				}
				// If the gun is glitched, allow dropps. If not, cancel it
				e.setCancelled(true);
				return;
			}
		}
		checkforDups(e.getPlayer(), e.getItemDrop().getItemStack());
		// This should prevent the everything below from being called.

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
										GunUtil.basicReload(g, getGunWithAttchments(e.getItemDrop().getItemStack()),
												e.getPlayer(), g.hasUnlimitedAmmo());
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
	public void sendPacket(final Player player, final boolean warning) {
		new BukkitRunnable() {
			public void run() {
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
							try {
								if (AutoDetectResourcepackVersion && us.myles.ViaVersion.bukkit.util.ProtocolSupportUtil
										.getProtocolVersion(player) < ID18) {
									player.setResourcePack(url18);
								} else {
									player.setResourcePack(url);
								}
							} catch (Error | Exception e4) {
								player.setResourcePack(url);
							}

							if (!isVersionHigherThan(1, 9))
								resourcepackReq.add(player.getUniqueId());
							// If the player is on 1.8, manually add them to the resource list.

						} catch (Exception e) {

						}
					}
				}.runTaskLater(Main.getInstance(), 20 * (warning ? 1 : 5));
			}
		}.runTaskLater(this, (long) (20 * secondsTilSend));
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

	public static MaterialStorage m(int d) {
		return MaterialStorage.getMS(guntype, d, 0);
	}

	public boolean isCustomItem(ItemStack is) {
		return isCustomItem(is, 0);
	}

	public boolean isCustomItem(ItemStack is, int dataOffset) {
		ItemStack itemstack = is.clone();
		itemstack.setDurability((short) (is.getDurability() + dataOffset));
		return isArmor(itemstack) || isGunWithAttchments(itemstack) || isAmmo(itemstack) || isMisc(itemstack)
				|| isGun(itemstack) || isIS(itemstack) || isReserved(itemstack);
	}

	public boolean isReserved(ItemStack is) {
		int var = MaterialStorage.getVarient(is);
		return (is != null
				&& (reservedForExps.contains(MaterialStorage.getMS(is.getType(), (int) is.getDurability(), var))
						|| reservedForExps.contains(MaterialStorage.getMS(is.getType(), -1, -1, "-1", "-1"))));
	}

	public boolean isArmor(ItemStack is) {
		int var = MaterialStorage.getVarient(is);
		return (is != null
				&& (armorRegister.containsKey(MaterialStorage.getMS(is.getType(), (int) is.getDurability(), var))
						|| armorRegister.containsKey(MaterialStorage.getMS(is.getType(), -1, var))));
	}

	public ArmorObject getArmor(ItemStack is) {
		int var = MaterialStorage.getVarient(is);
		if (armorRegister.containsKey(MaterialStorage.getMS(is.getType(), (int) is.getDurability(), var)))
			return armorRegister.get(MaterialStorage.getMS(is.getType(), (int) is.getDurability(), var));
		return armorRegister.get(MaterialStorage.getMS(is.getType(), -1, var));
	}

	public boolean isMisc(ItemStack is) {
		int var = MaterialStorage.getVarient(is);
		return (is != null
				&& (miscRegister.containsKey(MaterialStorage.getMS(is.getType(), (int) is.getDurability(), var))
						|| miscRegister.containsKey(MaterialStorage.getMS(is.getType(), -1, var))));
	}

	public ArmoryBaseObject getMisc(ItemStack is) {
		int var = MaterialStorage.getVarient(is);
		if (miscRegister.containsKey(MaterialStorage.getMS(is.getType(), (int) is.getDurability(), var)))
			return miscRegister.get(MaterialStorage.getMS(is.getType(), (int) is.getDurability(), var));
		return miscRegister.get(MaterialStorage.getMS(is.getType(), -1, var));
	}

	public Gun getGun(ItemStack is) {
		int var = MaterialStorage.getVarient(is);
		if (gunRegister.containsKey(MaterialStorage.getMS(is.getType(), (int) is.getDurability(), var)))
			return gunRegister.get(MaterialStorage.getMS(is.getType(), (int) is.getDurability(), var));
		return gunRegister.get(MaterialStorage.getMS(is.getType(), -1, var));
	}

	public static boolean isGun(ItemStack is) {
		int var = MaterialStorage.getVarient(is);
		return (is != null
				&& (gunRegister.containsKey(MaterialStorage.getMS(is.getType(), (int) is.getDurability(), var))
						|| gunRegister.containsKey(MaterialStorage.getMS(is.getType(), -1, var))));
	}

	public AttachmentBase getGunWithAttchments(ItemStack is) {
		int var = MaterialStorage.getVarient(is);
		if (attachmentRegister.containsKey(MaterialStorage.getMS(is.getType(), (int) is.getDurability(), var)))
			return attachmentRegister.get(MaterialStorage.getMS(is.getType(), (int) is.getDurability(), var));
		return attachmentRegister.get(MaterialStorage.getMS(is.getType(), -1, var));
	}

	public static boolean isGunWithAttchments(ItemStack is) {
		int var = MaterialStorage.getVarient(is);
		return (is != null
				&& (attachmentRegister.containsKey(MaterialStorage.getMS(is.getType(), (int) is.getDurability(), var))
						|| attachmentRegister.containsKey(MaterialStorage.getMS(is.getType(), -1, var))));
	}

	public Ammo getAmmo(ItemStack is) {
		int var = MaterialStorage.getVarient(is);
		@SuppressWarnings("deprecation")
		String extraData = is.getType() == Material.SKULL_ITEM ? ((SkullMeta) is.getItemMeta()).getOwner() : null;
		String temp = SkullHandler.getURL64(is);
		if (ammoRegister
				.containsKey(MaterialStorage.getMS(is.getType(), (int) is.getDurability(), var, extraData, temp)))
			return ammoRegister
					.get(MaterialStorage.getMS(is.getType(), (int) is.getDurability(), var, extraData, temp));
		return ammoRegister.get(MaterialStorage.getMS(is.getType(), -1, var, extraData, temp));
	}

	public boolean isAmmo(ItemStack is) {
		int var = MaterialStorage.getVarient(is);
		@SuppressWarnings("deprecation")
		String extraData = is.getType() == Material.SKULL_ITEM ? ((SkullMeta) is.getItemMeta()).getOwner() : null;
		String temp = SkullHandler.getURL64(is);
		boolean k = (is != null && (ammoRegister
				.containsKey(MaterialStorage.getMS(is.getType(), (int) is.getDurability(), var, extraData, temp))
				|| ammoRegister.containsKey(MaterialStorage.getMS(is.getType(), -1, var, extraData, temp))));
		return k;
	}

	public boolean isIS(ItemStack is) {
		if (is != null && is.getType() == guntype && is.getDurability() == (int) IronSightsToggleItem.getData())
			return true;
		return false;

	}

	public static void sendHotbarGunAmmoCount(Player p, Gun g, AttachmentBase attachmentBase, ItemStack usedItem) {
		try {
			HotbarMessager.sendHotBarMessage(p,
					(attachmentBase != null ? attachmentBase.getDisplayName() : g.getDisplayName()) + " = "
							+ ItemFact.getAmount(usedItem) + "/" + (g.getMaxBullets()) + "");
		} catch (Error | Exception e5) {
		}
	}

	@Override
	public void reloadConfig() {
		if (configFile == null) {
			configFile = new File(this.getDataFolder(), "config.yml");
		}
		config = CommentYamlConfiguration.loadConfiguration(configFile);
		InputStream defConfigStream = this.getResource("config.yml");
		if (defConfigStream != null) {
			config.setDefaults(
					CommentYamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
		}
	}

	@Override
	public FileConfiguration getConfig() {
		if (this.config == null) {
			this.reloadConfig();
		}
		return this.config;
	}

	public static Inventory createShop(int page) {
		return createCustomInventory(page, true);
	}

	public static Inventory createCraft(int page) {
		return createCustomInventory(page, false);
	}

	public static Inventory createCustomInventory(int page, boolean shopping) {
		Inventory shopMenu = Bukkit.createInventory(null, 9 * 6, (shopping ? S_shopName : S_craftingBenchName) + page);
		List<Gun> gunslistr = new ArrayList<Gun>(gunRegister.values());
		Collections.sort(gunslistr);
		int basei = 0;
		int index = (page * 9 * 5);
		int maxIndex = (index + (9 * 5));

		shopMenu.setItem((9 * 6) - 1 - 8, prevButton);
		shopMenu.setItem((9 * 6) - 1, nextButton);

		if (basei + gunslistr.size() < index)
			basei += gunslistr.size();
		else
			for (Gun g : gunslistr) {
				if (basei < index) {
					basei++;
					continue;
				}
				basei++;
				if (index >= maxIndex)
					break;
				index++;
				try {
					ItemStack is = ItemFact.getGun(g);
					ItemMeta im = is.getItemMeta();
					List<String> lore = ItemFact.getCraftingGunLore(g, null);
					im.setLore(lore);
					is.setItemMeta(im);
					if (enableVisibleAmounts)
						is.setAmount(g.getCraftingReturn());
					if (shopping)
						is = ItemFact.addShopLore(g, is.clone());
					shopMenu.addItem(is);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		List<AttachmentBase> attlistr = new ArrayList<AttachmentBase>(attachmentRegister.values());
		Collections.sort(attlistr);
		if (basei + gunslistr.size() < index)
			basei += gunslistr.size();
		else
			for (AttachmentBase g : attlistr) {
				if (basei < index) {
					basei++;
					continue;
				}
				basei++;
				if (index >= maxIndex)
					break;
				index++;

				Gun g2 = gunRegister.get(g.getBase());
				try {
					ItemStack is = ItemFact.getGun(g);
					ItemMeta im = is.getItemMeta();
					List<String> lore = ItemFact.getCraftingGunLore(g2, g);
					im.setLore(lore);
					is.setItemMeta(im);
					if (enableVisibleAmounts)
						is.setAmount(g2.getCraftingReturn());
					if (shopping)
						is = ItemFact.addShopLore(g2, g, is.clone());
					shopMenu.addItem(is);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		if (basei + gunslistr.size() < index)
			basei += gunslistr.size();
		else
			for (ArmoryBaseObject abo : miscRegister.values()) {
				if (basei < index) {
					basei++;
					continue;
				}
				basei++;
				if (index >= maxIndex)
					break;
				index++;
				try {
					ItemStack is = ItemFact.getObject(abo);
					ItemMeta im = is.getItemMeta();
					List<String> lore = ItemFact.getCraftingLore(abo);
					im.setLore(lore);
					is.setItemMeta(im);
					if (enableVisibleAmounts)
						is.setAmount(abo.getCraftingReturn());
					if (shopping)
						is = ItemFact.addShopLore(abo, is.clone());
					shopMenu.addItem(is);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		if (basei + gunslistr.size() < index)
			basei += gunslistr.size();
		else
			for (Ammo ammo : ammoRegister.values()) {
				if (basei < index) {
					basei++;
					continue;
				}
				basei++;
				if (index >= maxIndex)
					break;
				index++;
				try {
					ItemStack is = ItemFact.getAmmo(ammo);
					ItemMeta im = is.getItemMeta();
					List<String> lore = ItemFact.getCraftingLore(ammo);
					im.setLore(lore);
					is.setItemMeta(im);
					is.setAmount(ammo.getCraftingReturn());
					if (shopping)
						is = ItemFact.addShopLore(ammo, is.clone());
					shopMenu.addItem(is);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		if (basei + gunslistr.size() < index)
			basei += gunslistr.size();
		else
			for (ArmorObject armor : armorRegister.values()) {
				if (basei < index) {
					basei++;
					continue;
				}
				basei++;
				if (index >= maxIndex)
					break;
				index++;
				try {
					ItemStack is = ItemFact.getArmor(armor);
					ItemMeta im = is.getItemMeta();
					List<String> lore = ItemFact.getCraftingLore(armor);
					im.setLore(lore);
					is.setItemMeta(im);
					is.setAmount(armor.getCraftingReturn());
					if (shopping)
						is = ItemFact.addShopLore(armor, is.clone());
					shopMenu.addItem(is);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		return shopMenu;
	}

	public int findSafeSpot(ItemStack newItem, boolean findHighest) {
		int safeDurib = newItem.getDurability();
		for (MaterialStorage j : ammoRegister.keySet())
			if (j.getMat() == newItem.getType() && (j.getData() > safeDurib) == findHighest)
				safeDurib = j.getData();
		for (MaterialStorage j : gunRegister.keySet())
			if (j.getMat() == newItem.getType() && (j.getData() > safeDurib) == findHighest)
				safeDurib = j.getData();
		for (MaterialStorage j : miscRegister.keySet())
			if (j.getMat() == newItem.getType() && (j.getData() > safeDurib) == findHighest)
				safeDurib = j.getData();
		for (MaterialStorage j : armorRegister.keySet())
			if (j.getMat() == newItem.getType() && (j.getData() > safeDurib) == findHighest)
				safeDurib = j.getData();
		for (MaterialStorage j : reservedForExps)
			if (j.getMat() == newItem.getType() && (j.getData() > safeDurib) == findHighest)
				safeDurib = j.getData();
		for (MaterialStorage j : attachmentRegister.keySet())
			if (j.getMat() == newItem.getType() && (j.getData() > safeDurib) == findHighest)
				safeDurib = j.getData();
		return safeDurib;
	}

	public static int getMaxPages() {
		return (armorRegister.size() + ammoRegister.size() + miscRegister.size() + gunRegister.size()) / (9 * 5);
	}
}
