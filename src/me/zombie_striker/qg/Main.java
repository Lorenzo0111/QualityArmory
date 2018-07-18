package me.zombie_striker.qg;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;

import me.zombie_striker.pluginconstructor.HotbarMessager;
import me.zombie_striker.qg.ammo.*;
import me.zombie_striker.qg.armor.*;
import me.zombie_striker.qg.armor.angles.AngledArmor;
import me.zombie_striker.qg.attachments.AttachmentBase;
import me.zombie_striker.qg.config.ArmoryYML;
import me.zombie_striker.qg.config.CommentYamlConfiguration;
import me.zombie_striker.qg.config.MessagesYML;
import me.zombie_striker.qg.guns.*;
import me.zombie_striker.qg.guns.utils.*;
import me.zombie_striker.qg.handlers.*;
import me.zombie_striker.qg.handlers.gunvalues.*;
import me.zombie_striker.qg.miscitems.*;
import me.zombie_striker.qg.miscitems.ThrowableItems.ThrowableHolder;
import me.zombie_striker.qg.npcs.Gunner;
import me.zombie_striker.qg.npcs.GunnerTrait;

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
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.*;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.google.common.base.Charsets;

public class Main extends JavaPlugin implements Listener {

	public static HashMap<MaterialStorage, Gun> gunRegister = new HashMap<>();
	public static HashMap<MaterialStorage, Ammo> ammoRegister = new HashMap<>();
	public static HashMap<MaterialStorage, ArmoryBaseObject> miscRegister = new HashMap<>();
	public static HashMap<MaterialStorage, ArmorObject> armorRegister = new HashMap<>();
	public static HashMap<MaterialStorage, AttachmentBase> attachmentRegister = new HashMap<>();

	public static HashMap<MaterialStorage, AngledArmor> angledArmor = new HashMap<>();
	public static ArrayList<MaterialStorage> expansionPacks = new ArrayList<>();

	public static HashMap<UUID, List<BukkitTask>> reloadingTasks = new HashMap<UUID, List<BukkitTask>>();

	public static HashMap<UUID, Long> sentResourcepack = new HashMap<>();

	public static ArrayList<UUID> resourcepackReq = new ArrayList<>();

	public static List<Gunner> gunners = new ArrayList<>();

	public List<String> namesToBypass = new ArrayList<String>();

	private static Main main;

	private List<Material> interactableBlocks = new ArrayList<>();
	public static List<Material> destructableBlocks = new ArrayList<Material>();
	private static boolean enableInteractChests = false;

	private TreeFellerHandler tfh = null;

	public static Main getInstance() {
		return main;
	}

	public static boolean DEBUG = false;

	public static Object bulletTrail;

	private boolean shouldSend = true;
	public static boolean sendOnJoin = false;
	public static boolean sendTitleOnJoin = false;
	public static double secondsTilSend = 0.0;

	public static boolean orderShopByPrice = false;

	public static boolean enableDurability = false;/*
													 * public static boolean UnlimitedAmmoPistol = false; public static
													 * boolean UnlimitedAmmoRifle = false; public static boolean
													 * UnlimitedAmmoShotgun = false; public static boolean
													 * UnlimitedAmmoSMG = false; public static boolean UnlimitedAmmoRPG
													 * = false; public static boolean UnlimitedAmmoSniper = false;
													 * public static boolean UnlimitedAmmoLazer = false;
													 */

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

	public static boolean ignoreArmorStands = false;

	public static boolean enableBleeding = false;
	public static double bulletWound_initialbloodamount = 1500;
	public static double bulletWound_BloodIncreasePerSecond = 0.01;
	public static double bulletWound_MedkitBloodlossHealRate = 0.05;

	public static boolean HeadshotOneHit = true;
	public static boolean headshotPling = true;
	public static boolean headshotGoreSounds = true;

	public static boolean overrideURL = false;
	public static boolean kickIfDeniedRequest = false;
	// public static String url19plus =
	// "https://www.dropbox.com/s/faufrgo7w2zpi3d/QualityArmoryv1.0.10.zip?dl=1";
	public static String url_newest = "https://www.dropbox.com/s/2r367jump1ugtus/QualityArmoryv1.0.21.zip?dl=1";
	public static String url18 = "https://www.dropbox.com/s/gx6dhahq6onob4g/QualityArmory1.8v1.0.1.zip?dl=1";
	public static String url = url_newest;

	public static String S_NOPERM = "&c You do not have permission to do that";
	public static String S_NORES1 = " &c&l Downloading Resourcepack...";
	public static String S_NORES2 = " &f Accept the resourcepack to see correct textures";
	public static String S_ANVIL = " &aYou do not have permission to use this armory bench. ShiftClick to access anvil.";
	public static String S_ITEM_BULLETS = "&aBullets";
	public static String S_ITEM_DURIB = "Durability";
	public static String S_ITEM_DAMAGE = "&aDamage";
	public static String S_ITEM_AMMO = "&aAmmo";
	public static String S_ITEM_ING = "Ingredients";
	public static String S_ITEM_VARIENTS = "&7Varient:";
	public static String S_ITEM_DPS = "&2DPS";
	public static String S_ITEM_COST = "&" + ChatColor.GOLD.getChar() + "Price: ";

	public static String S_KICKED_FOR_RESOURCEPACK = "&c You have been kicked because you did not accept the resourcepack. \n&f If you want to rejoin the server, edit the server entry and set \"Resourcepack Prompts\" to \"Accept\" or \"Prompt\"'";

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
	public static double S_MEDKIT_HEALDELAY = 6;
	public static double S_MEDKIT_HEAL_AMOUNT = 1;
	public static String S_MEDKIT_LORE_INFO = "&f[RMB] to heal yourself!";

	public static String S_BULLETPROOFSTOPPEDBLEEDING = "&aYour Kevlar vest protected you from that bullet!";
	public static String S_BLEEDOUT_STARTBLEEDING = "&cYOU ARE BLEEDING! FIND A MEDKIT TO STOP THE BLEEDING!";
	public static String S_BLEEDOUT_LOSINGconscious = "&cYOU ARE LOSING CONSCIOUSNESS DUE TO BLOODLOSS!";

	public static String S_OUT_OF_AMMO = "[Out of Ammo]";
	public static String S_RELOADING_MESSAGE = "[Reloading...]";
	public static String S_MAX_FOUND = "[%total%]";
	public static String S_HOTBAR_FORMAT = "%name% = %amount%/%max% %state%";

	public static String S_RESOURCEPACK_HELP = "In case the resourcepack crashes your client, reject the request and use /qa getResourcepack to get the resourcepack.";
	public static String S_RESOURCEPACK_DOWNLOAD = "Download this resourcepack and enable it to see gun models (Note that it may take some time to load)";
	public static String S_RESOURCEPACK_BYPASS = "By issuing this command, you are now added to a whitelist for the resourcepack. You should no longer see the request";
	public static String S_RESOURCEPACK_OPTIN = "By issuing this command, you have been removed from the whitelist. You will now recieve resourcepack requests";

	public static String S_GRENADE_PULLPIN = " Pull the pin first...";
	public static String S_GRENADE_PALREADYPULLPIN = "You already pulled the pin!";

	public static Material usedIronSightsMaterial = Material.DIAMOND_AXE;
	public static int usedIronSightsData = 21;

	public static boolean enableCrafting = true;
	public static boolean enableShop = true;

	public static double smokeSpacing = 0.5;

	public static boolean enablePrimaryWeaponHandler = false;
	public static int primaryWeaponLimit = 2;
	public static int secondaryWeaponLimit = 2;

	public static String prefix = ChatColor.GRAY + "[" + ChatColor.DARK_GREEN + "QualityArmory" + ChatColor.GRAY + "]"
			+ ChatColor.WHITE;

	// public Inventory craftingMenu;
	public static String S_craftingBenchName = "Armory Bench Page:";
	public static String S_missingIngredients = "You do not have all the materials needed to craft this";

	// public Inventory shopMenu;
	public static String S_shopName = "Weapons Shop Page:";
	public static String S_noMoney = "You do not have enough money to buy this";
	public static String S_noEcon = "ECONOMY NOT ENABLED. REPORT THIS TO THE OWNER!";

	public static ItemStack prevButton;
	public static ItemStack nextButton;

	// public static Material guntype = Material.DIAMOND_AXE;

	public static MessagesYML m;
	public static MessagesYML resourcepackwhitelist;

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

	public static boolean unknownTranslationKeyFixer = false;

	public static boolean enableCreationOfFiles = true;

	public static List<Scoreboard> coloredGunScoreboard = new ArrayList<Scoreboard>();
	public static boolean blockBreakTexture = false;

	// public static List<MaterialStorage> reservedForExps = Arrays.asList(m(53),
	// m(54), m(55), m(56), m(57), m(58), m(59), m(60));

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

		AngledArmorHandler.stopTasks();
		for (Scoreboard s : coloredGunScoreboard)
			for (Team t : s.getTeams())
				t.unregister();

		for (Gunner g : gunners) {
			g.dispose();
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

		// check if Citizens is present and enabled.

		if (getServer().getPluginManager().getPlugin("Citizens") == null
				|| getServer().getPluginManager().getPlugin("Citizens").isEnabled() == false) {
			getLogger().log(Level.SEVERE, "Citizens 2.0 not found or not enabled");
			// getServer().getPluginManager().disablePlugin(this);
		} else {
			// Register your trait with Citizens.
			net.citizensnpcs.api.CitizensAPI.getTraitFactory()
					.registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(GunnerTrait.class));
		}

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
					if (gun.getZoomWhenIronSights() > 0)
						if (e.isSneaking()) {
							e.getPlayer().addPotionEffect(
									new PotionEffect(PotionEffectType.SLOW, 12000, gun.getZoomWhenIronSights()));
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

	@SuppressWarnings({ "unchecked" })
	public void reloadVals() {

		Material glass = Material.valueOf("STAINED_GLASS_PANE");
		if(glass==null)
			glass = Material.matchMaterial("YELLOW_STAINED_GLASS_PANE");
		
		prevButton = new ItemStack(glass, 1, (short) 14);
		nextButton = new ItemStack(glass, 1, (short) 5);
		
		new BoltactionCharger();
		new BreakactionCharger();
		new HomingRPGCharger();
		new MininukeCharger();
		new PumpactionCharger();
		new RapidFireCharger();
		new RevolverCharger();
		new RPGCharger();

		gunRegister.clear();
		ammoRegister.clear();
		miscRegister.clear();
		armorRegister.clear();
		interactableBlocks.clear();

		attachmentRegister.clear();

		m = new MessagesYML(new File(getDataFolder(), "messages.yml"));
		S_ANVIL = ChatColor.translateAlternateColorCodes('&', (String) m.a("NoPermAnvilMessage", S_ANVIL));
		S_NOPERM = ChatColor.translateAlternateColorCodes('&', (String) m.a("NoPerm", S_NOPERM));
		S_shopName = (String) m.a("ShopName", S_shopName);
		S_craftingBenchName = (String) m.a("CraftingBenchName", S_craftingBenchName);
		S_missingIngredients = (String) m.a("Missing_Ingredients", S_missingIngredients);
		S_NORES1 = ChatColor.translateAlternateColorCodes('&', (String) m.a("NoResourcepackMessage1", S_NORES1));
		S_NORES2 = ChatColor.translateAlternateColorCodes('&', (String) m.a("NoResourcepackMessage2", S_NORES2));
		S_ITEM_AMMO = ChatColor.translateAlternateColorCodes('&', (String) m.a("Lore_Ammo", S_ITEM_AMMO));
		S_ITEM_BULLETS = ChatColor.translateAlternateColorCodes('&', (String) m.a("lore_bullets", S_ITEM_BULLETS));
		S_ITEM_DAMAGE = ChatColor.translateAlternateColorCodes('&', (String) m.a("Lore_Damage", S_ITEM_DAMAGE));
		S_ITEM_DURIB = ChatColor.translateAlternateColorCodes('&', (String) m.a("Lore_Durib", S_ITEM_DURIB));
		S_ITEM_ING = ChatColor.translateAlternateColorCodes('&', (String) m.a("Lore_ingredients", S_ITEM_ING));
		S_ITEM_VARIENTS = ChatColor.translateAlternateColorCodes('&', (String) m.a("Lore_Varients", S_ITEM_VARIENTS));
		S_ITEM_COST = ChatColor.translateAlternateColorCodes('&', (String) m.a("Lore_Price", S_ITEM_COST));
		S_ITEM_DPS = ChatColor.translateAlternateColorCodes('&', (String) m.a("Lore_DamagePerSecond", S_ITEM_DPS));

		S_RELOADING_MESSAGE = ChatColor.translateAlternateColorCodes('&',
				(String) m.a("Reloading_Message", S_RELOADING_MESSAGE));
		S_MAX_FOUND = ChatColor.translateAlternateColorCodes('&', (String) m.a("State_AmmoCount", S_MAX_FOUND));
		S_OUT_OF_AMMO = ChatColor.translateAlternateColorCodes('&', (String) m.a("State_OutOfAmmo", S_OUT_OF_AMMO));
		S_HOTBAR_FORMAT = ChatColor.translateAlternateColorCodes('&', (String) m.a("HotbarMessage", S_HOTBAR_FORMAT));

		S_KICKED_FOR_RESOURCEPACK = ChatColor.translateAlternateColorCodes('&',
				(String) m.a("Kick_message_if_player_denied_request", S_KICKED_FOR_RESOURCEPACK));

		S_LMB_SINGLE = (String) m.a("Lore-LMB-Single", S_LMB_SINGLE);
		S_LMB_FULLAUTO = (String) m.a("Lore-LMB-FullAuto", S_LMB_FULLAUTO);
		S_RMB_RELOAD = (String) m.a("Lore-RMB-Reload", S_RMB_RELOAD);
		S_RMB_A1 = (String) m.a("Lore-Ironsights-RMB", S_RMB_A1);
		S_RMB_A2 = (String) m.a("Lore-Ironsights-Sneak", S_RMB_A2);
		S_RMB_R1 = (String) m.a("Lore-Reload-Dropitem", S_RMB_R1);
		S_RMB_R2 = (String) m.a("Lore-Reload-RMB", S_RMB_R2);

		S_RESOURCEPACK_HELP = (String) m.a("Resourcepack_InCaseOfCrash", S_RESOURCEPACK_HELP);
		S_RESOURCEPACK_DOWNLOAD = (String) m.a("Resourcepack_Download", S_RESOURCEPACK_DOWNLOAD);
		S_RESOURCEPACK_BYPASS = (String) m.a("Resourcepack_NowBypass", S_RESOURCEPACK_BYPASS);
		S_RESOURCEPACK_OPTIN = (String) m.a("Resourcepack_NowOptIn", S_RESOURCEPACK_OPTIN);

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

		resourcepackwhitelist = new MessagesYML(new File(getDataFolder(), "resourcepackwhitelist.yml"));
		namesToBypass = (List<String>) resourcepackwhitelist.a("Names_Of_players_to_bypass", namesToBypass);

		if (!new File(getDataFolder(), "config.yml").exists())
			saveDefaultConfig();
		reloadConfig();

		if (getServer().getPluginManager().isPluginEnabled("Parties"))
			hasParties = true;
		DEBUG = (boolean) a("ENABLE-DEBUG", false);

		friendlyFire = (boolean) a("FriendlyFireEnabled", false);

		kickIfDeniedRequest = (boolean) a("KickPlayerIfDeniedResourcepack", false);
		shouldSend = (boolean) a("useDefaultResourcepack", true);
		/*
		 * UnlimitedAmmoPistol = (boolean) a("UnlimitedPistolAmmo", false);
		 * UnlimitedAmmoShotgun = (boolean) a("UnlimitedShotgunAmmo", false);
		 * UnlimitedAmmoRifle = (boolean) a("UnlimitedRifleAmmo", false);
		 * UnlimitedAmmoSMG = (boolean) a("UnlimitedSMGAmmo", false);
		 * UnlimitedAmmoSniper = (boolean) a("UnlimitedSniperAmmo", false);
		 * UnlimitedAmmoRPG = (boolean) a("UnlimitedRocketAmmo", false);
		 * UnlimitedAmmoLazer = (boolean) a("UnlimitedLazerAmmo", false);
		 */
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

		enableVisibleAmounts = (boolean) a("enableVisibleBulletCounts", false);
		reloadOnF = (boolean) a("enableReloadingWhenSwapToOffhand", true);

		enableExplosionDamage = (boolean) a("enableExplosionDamage", false);
		enableExplosionDamageDrop = (boolean) a("enableExplosionDamageDrop", false);

		enablePrimaryWeaponHandler = (boolean) a("enablePrimaryWeaponLimiter", false);
		primaryWeaponLimit = (int) a("weaponlimiter_primaries", primaryWeaponLimit);
		secondaryWeaponLimit = (int) a("weaponlimiter_secondaries", secondaryWeaponLimit);

		enableCrafting = (boolean) a("enableCrafting", true);
		enableShop = (boolean) a("enableShop", true);

		AUTOUPDATE = (boolean) a("AUTO-UPDATE", true);
		USE_DEFAULT_CONTROLS = !(boolean) a("Swap-Reload-and-Shooting-Controls", false);

		orderShopByPrice = (boolean) a("Order-Shop-By-Price", orderShopByPrice);

		ENABLE_LORE_INFO = (boolean) a("enable_lore_gun-info_messages", true);
		ENABLE_LORE_HELP = (boolean) a("enable_lore_control-help_messages", true);

		usedIronSightsMaterial = Material
				.matchMaterial((String) a("Material_For_IronSightToggle_Item", usedIronSightsMaterial.name()));
		usedIronSightsData = (int) a("Data_For_IronSightToggle_Item", usedIronSightsData);

		HeadshotOneHit = (boolean) a("Enable_Headshot_Instantkill", HeadshotOneHit);
		headshotPling = (boolean) a("Enable_Headshot_Notification_Sound", headshotPling);
		headshotGoreSounds = (boolean) a("Enable_Headshot_Sounds", headshotGoreSounds);

		hideTextureWarnings = (boolean) a("hideTextureWarnings", false);
		ignoreArmorStands = (boolean) a("ignoreArmorStands", false);

		allowGunReload = (boolean) a("allowGunReload", allowGunReload);
		AutoDetectResourcepackVersion = (boolean) a("Auto-Detect-Resourcepack", false);

		unknownTranslationKeyFixer = (boolean) a("unknownTranslationKeyFixer", false);

		enableCreationOfFiles = (boolean) a("Enable_Creation_Of_Default_Files", true);

		blockBreakTexture = (boolean) a("Break-Block-Texture-If-Shot", true);

		bulletWound_initialbloodamount = (double) a("experimental.BulletWounds.InitialBloodLevel",
				bulletWound_initialbloodamount);
		bulletWound_BloodIncreasePerSecond = (double) a("experimental.BulletWounds.BloodIncreasePerSecond",
				bulletWound_BloodIncreasePerSecond);
		bulletWound_MedkitBloodlossHealRate = (double) a("experimental.BulletWounds.Medkit_Heal_Bloodloss_Rate",
				bulletWound_MedkitBloodlossHealRate);
		enableBleeding = (boolean) a("experimental.BulletWounds.enableBleeding", enableBleeding);

		// Force inversion due to naming
		if (saveTheConfig) {
			Bukkit.getConsoleSender().sendMessage(prefix + " Needed to save config: code=1");
			saveConfig();
		}
		try {
			enableEconomy = EconHandler.setupEconomy();
		} catch (Exception | Error e) {
		}

		/*
		 * try { bulletTrail = Particle.valueOf((String) a("Bullet-Particle-Type",
		 * "FIREWORKS_SPARK")); a("ACCEPTED-BULLET-PARTICLE-VALUES",
		 * "https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Particle.html"); } catch
		 * (Exception | Error e) { }
		 */

		/*
		 * try { guntype = Material.matchMaterial((String) a("gunMaterialType",
		 * guntype.toString())); } catch (Exception e) { guntype = Material.DIAMOND_AXE;
		 * }
		 */
		overrideURL = (boolean) a("DefaultResourcepackOverride", false);

		if (isVersionHigherThan(1, 9) || AutoDetectResourcepackVersion) {
			url = url_newest;

		} else {
			// Use 1.8 resourcepack.
			url = url18;
		}

		if (overrideURL) {
			url = (String) a("DefaultResourcepack", url);
			url18 = (String) a("DefaultResourcepack_18", url18);
		} else {
			// Make sure the user is always up to date if they don't override
			// the resoucepack
			if (!getConfig().contains("DefaultResourcepack")
					|| !url.equals(getConfig().getString("DefaultResourcepack"))) {
				getConfig().set("DefaultResourcepack", url);
				saveTheConfig = true;
			}
		}
		enableIronSightsON_RIGHT_CLICK = (boolean) a("IronSightsOnRightClick", false);

		List<String> destarray = (List<String>) a("DestructableMaterials", Arrays.asList("MATERIAL_NAME_HERE"));
		for (String s : destarray) {
			try {
				destructableBlocks.add(Material.getMaterial(s));
			} catch (Error | Exception e54) {
				try {
					//destructableBlocks.add(Material.getMaterial(Integer.parseInt(s.split(":")[0])));
				} catch (Error | Exception e5) {
				}
			}
		}

		for (Material m : Material.values())
			if (m.isBlock())
				if (m.name().contains("DOOR") || m.name().contains("TRAPDOOR") || m.name().contains("BUTTON")
						|| m.name().contains("LEVER"))
					interactableBlocks.add(m);
		if (saveTheConfig) {
			Bukkit.getConsoleSender().sendMessage(prefix + " Needed to save config: code=2");
			saveConfig();
		}

		// ,(float)a("Weapon.RPG.Damage", 10)

		if (enableCreationOfFiles) {

			List<String> stringsWoodRif = Arrays.asList(new String[] { getIngString(Material.IRON_INGOT, 0, 12),
					getIngString(MultiVersionLookup.getWood(), 0, 2), getIngString(Material.REDSTONE, 0, 5) });
			List<String> stringsGoldRif = Arrays.asList(new String[] { getIngString(Material.IRON_INGOT, 0, 12),
					getIngString(Material.GOLD_INGOT, 0, 2), getIngString(Material.REDSTONE, 0, 5) });
			List<String> stringsMetalRif = Arrays.asList(
					new String[] { getIngString(Material.IRON_INGOT, 0, 15), getIngString(Material.REDSTONE, 0, 5) });
			List<String> stringsPistol = Arrays.asList(
					new String[] { getIngString(Material.IRON_INGOT, 0, 5), getIngString(Material.REDSTONE, 0, 2) });
			List<String> stringsRPG = Arrays.asList(
					new String[] { getIngString(Material.IRON_INGOT, 0, 32), getIngString(Material.REDSTONE, 0, 10) });

			List<String> stringsGrenades = Arrays.asList(
					new String[] { getIngString(Material.IRON_INGOT, 0, 6), getIngString(MultiVersionLookup.getGunpowder(), 0, 10) });

			List<String> stringsAmmo = Arrays.asList(new String[] { getIngString(Material.IRON_INGOT, 0, 1),
					getIngString(MultiVersionLookup.getGunpowder(), 0, 1), getIngString(Material.REDSTONE, 0, 1) });
			List<String> stringsAmmoMusket = Arrays.asList(
					new String[] { getIngString(Material.IRON_INGOT, 0, 4), getIngString(MultiVersionLookup.getGunpowder(), 0, 3), });
			List<String> stringsAmmoRPG = Arrays.asList(new String[] { getIngString(Material.IRON_INGOT, 0, 4),
					getIngString(MultiVersionLookup.getGunpowder(), 0, 6), getIngString(Material.REDSTONE, 0, 1) });

			List<String> stringsHealer = Arrays.asList(
					new String[] { getIngString(MultiVersionLookup.getWool(), 0, 6), getIngString(Material.GOLDEN_APPLE, 0, 1) });
			if (!isVersionHigherThan(1, 9)
					|| (AutoDetectResourcepackVersion && Bukkit.getPluginManager().isPluginEnabled("ViaRewind"))) {
				String additive = AutoDetectResourcepackVersion ? "_18" : "";
				{
					// GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), false,
					// "default18_P30", "P30" + additive,
					// ChatColor.GOLD + "P30", null, 0, stringsPistol, WeaponType.PISTOL, false,
					// "556ammo", 3, 0.25,
					// Material.IRON_HOE, 12, 1000, 1.5, 0.25, 1, false, 700, null, 80, true, null);
					// GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), false,
					// "default18_AK47", "AK47" + additive,
					// ChatColor.GOLD + "AK-47", null, -1, stringsWoodRif, WeaponType.RIFLE, true,
					// "556ammo", 3, 0.25,
					// Material.GOLD_SPADE, 40, 1000, 1.5, 0.25, 2, true, 1400,
					// ChargingManager.RAPIDFIRE, 140, true,
					// null);
					// GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), false,
					// "default18_MP5K", "MP5K" + additive,
					// ChatColor.GOLD + "MP5K", null, 0, stringsMetalRif, WeaponType.SMG, true,
					// "556ammo", 3, 0.25,
					// Material.GOLD_PICKAXE, 32, 1000, 1.5, 0.25, 1, false, 1200, null, 100, true,
					// WeaponSounds.GUN_SMALL);
					// GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), false,
					// "default18_FNFal", "FNFal" + additive,
					// ChatColor.GOLD + "FN-Fal", null, 0, stringsMetalRif, WeaponType.RIFLE, true,
					// "556ammo", 3, 0.25,
					// Material.GOLD_HOE, 32, 1000, 1.5, 0.25, 1, false, 1000, null, 140, true,
					// null);

					// The the type is not the same, or if it is, if there is no auto detection
					// if (guntype != Material.DIAMOND_HOE || !AutoDetectResourcepackVersion)
					// GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), false,
					// "default18_RPG", "RPG" + additive,
					// ChatColor.GOLD + "RPG", null, 0, stringsRPG, WeaponType.RPG, false,
					// "RPGammo", 100, 0.1,
					// Material.DIAMOND_HOE, 1, 200, 3, 3, 2, false, 5000, ChargingManager.RPG, 220,
					// true, null);
					// if (/* guntype != Material.DIAMOND_AXE || */ !AutoDetectResourcepackVersion)
					// GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), false,
					// "default18_PKP", "PKP" + additive,
					// ChatColor.GOLD + "PKP", null, 0, stringsMetalRif, WeaponType.RIFLE, false,
					// "556ammo", 2,
					// 0.3, Material.DIAMOND_AXE, 100, 1000, 3, 0.27, 3, true, 3000,
					// ChargingManager.RAPIDFIRE,
					// 170, true, WeaponSounds.GUN_BIG);
					// GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), false,
					// "default18_M16", "M16" + additive,
					// ChatColor.GOLD + "M16", null, 0, stringsMetalRif, WeaponType.RIFLE, false,
					// "556ammo", 4, 0.3,
					/// Material.IRON_SPADE, 30, 1000, 0.11, 1.5, 2, true, 1200,
					// ChargingManager.RAPIDFIRE, 140, true,
					// null);

					GunYMLCreator
							.createNewCustomGun(getDataFolder(), "default_1_8_p30", "p30" + additive, "P30", 1,
									stringsPistol, WeaponType.PISTOL, null, true, "9mm", 3, 12, 100)
							.setMaterial(Material.IRON_HOE).setOn18(true).setIsSecondaryWeapon(true).done();
					if (!AutoDetectResourcepackVersion)
						GunYMLCreator.createNewCustomGun(getDataFolder(), "default_1_8_pkp", "pkp" + additive, "PKP", 1,
								stringsMetalRif, WeaponType.RIFLE, WeaponSounds.GUN_BIG, true, "556", 4, 100, 3000)
								.setMaterial(Material.DIAMOND_AXE).setOn18(true).setFullyAutomatic(3).done();
					GunYMLCreator
							.createNewCustomGun(getDataFolder(), "default_1_8_mp5k", "mp5k" + additive, "MP5K", 1,
									stringsMetalRif, WeaponType.SMG, null, false, "9mm", 3, 32, 1000)
							.setMaterial(MultiVersionLookup.getGoldPick()).setOn18(true).setFullyAutomatic(3).done();
					GunYMLCreator
							.createNewCustomGun(getDataFolder(), "default_1_8_ak47", "ak47" + additive, "AK47", 1,
									stringsMetalRif, WeaponType.RIFLE, null, false, "556", 3, 40, 1500)
							.setMaterial(MultiVersionLookup.getGoldShovel()).setOn18(true).setFullyAutomatic(2).done();
					GunYMLCreator
							.createNewCustomGun(getDataFolder(), "default_1_8_m16", "m16", "M16" + additive, 1,
									stringsMetalRif, WeaponType.RIFLE, null, true, "556", 3, 30, 2000)
							.setMaterial(MultiVersionLookup.getIronShovel()).setOn18(true).setFullyAutomatic(2).done();
					GunYMLCreator
							.createNewCustomGun(getDataFolder(), "default_1_8_fnfal", "fnfal" + additive, "FNFal", 1,
									stringsMetalRif, WeaponType.RIFLE, null, false, "556", 3, 32, 2000)
							.setMaterial(MultiVersionLookup.getGoldHoe()).setOn18(true).setFullyAutomatic(2).done();
					GunYMLCreator
							.createNewCustomGun(getDataFolder(), "default_1_8_rpg", "rpg" + additive, "RPG", 1,
									stringsRPG, WeaponType.RPG, null, false, "rocket", 100, 1, 4000)
							.setMaterial(Material.DIAMOND_HOE).setOn18(true).setChargingHandler(ChargingManager.RPG)
							.setDistance(200).done();

				}

				ArmoryYML skullammo = GunYMLCreator.createSkullAmmo(false, getDataFolder(), false, "default18_ammo556",
						"556ammo", "&7 5.56x45mm NATO", null,MultiVersionLookup.getSkull(), 3, "cactus", null, 4, 1, 50);
				skullammo.set(false, "skull_owner_custom_url_COMMENT",
						"Only specify the custom URL if the head does not use a player's skin, and instead sets the skin to a base64 value. If you need to get the head using a command, the URL should be set to the string of letters after \"Properties:{textures:[{Value:\"");
				skullammo.set(false, "skull_owner_custom_url",
						"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTg3ZmRmNDU4N2E2NDQ5YmZjOGJlMzNhYjJlOTM4ZTM2YmYwNWU0MGY2ZmFhMjc3ZDcxYjUwYmNiMGVhNjgzOCJ9fX0=");
				ArmoryYML skullammo2 = GunYMLCreator.createSkullAmmo(false, getDataFolder(), false, "default18_ammoRPG",
						"RPGammo", "&7 Rocket", null, MultiVersionLookup.getSkull(), 3, "cactus", null, 4, 1, 50);
				skullammo2.set(false, "skull_owner_custom_url_COMMENT",
						"Only specify the custom URL if the head does not use a player's skin, and instead sets the skin to a base64 value. If you need to get the head using a command, the URL should be set to the string of letters after \"Properties:{textures:[{Value:\"");
				skullammo2.set(false, "skull_owner_custom_url",
						"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTg3ZmRmNDU4N2E2NDQ5YmZjOGJlMzNhYjJlOTM4ZTM2YmYwNWU0MGY2ZmFhMjc3ZDcxYjUwYmNiMGVhNjgzOCJ9fX0=");

			}
			if (isVersionHigherThan(1, 9)) {
				GunYMLCreator.createAmmo(false, getDataFolder(), false, "9mm", "&f9mm", 15, stringsAmmo, 2, 0.7, 50,
						10);
				GunYMLCreator.createAmmo(false, getDataFolder(), false, "556", "&f5.56 NATO", 14, stringsAmmo, 4, 1, 50,
						5);
				GunYMLCreator.createAmmo(false, getDataFolder(), false, "762", "&f7.62x39mm", 79, stringsAmmo, 5, 1.2,
						50, 5);
				GunYMLCreator.createAmmo(false, getDataFolder(), false, "shell", "&fBuckshot", 16, stringsAmmo, 10, 0.5,
						8, 4);
				GunYMLCreator.createAmmo(false, getDataFolder(), false, "rocket", "&fRocket", 17, stringsAmmoRPG, 100,
						1000, 1);
				GunYMLCreator.createAmmo(false, getDataFolder(), false, "musketball", "&fMusket Ball", 51,
						stringsAmmoMusket, 1, 0.7, 32, 8);
				/**
				 * GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), "P30", 2,
				 * stringsPistol, WeaponType.PISTOL, true, "9mm", 3, 0.3, 12, 1000, false, 800,
				 * null, 100, null); ArmoryYML pkp = GunYMLCreator.createNewGun(forceUpdate,
				 * getDataFolder(), "PKP", 3, stringsMetalRif, WeaponType.RIFLE, true, "556", 2,
				 * 0.3, 100, 1000, 3, 0.27, 3, true, 3000, ChargingManager.RAPIDFIRE, 170,
				 * WeaponSounds.GUN_BIG); pkp.set(false, "addMuzzleSmoke", true);
				 * GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), "MP5K", 4,
				 * stringsMetalRif, WeaponType.SMG, false, "9mm", 3, 0.3, 32, 1000, 3, true,
				 * 1000, ChargingManager.RAPIDFIRE, 100, WeaponSounds.GUN_SMALL); ArmoryYML ak47
				 * = GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), "AK47", 5,
				 * stringsWoodRif, WeaponType.RIFLE, false, "556", 4, 0.3, 40, 1000, 2, true,
				 * 1200, ChargingManager.RAPIDFIRE, 140, null); ak47.set(false,
				 * "addMuzzleSmoke", true); ArmoryYML ak47u =
				 * GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), "AK47U", 6,
				 * stringsWoodRif, WeaponType.RIFLE, false, "556", 4, 0.3, 30, 1000, 2, true,
				 * 1200, ChargingManager.RAPIDFIRE, 140, null); ak47u.set(false,
				 * "addMuzzleSmoke", true); ArmoryYML m16 =
				 * GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), "M16", 7,
				 * stringsMetalRif, WeaponType.RIFLE, true, "556", 4, 0.3, 30, 1000, 2, true,
				 * 1200, ChargingManager.RAPIDFIRE, 140, null); m16.set(false, "addMuzzleSmoke",
				 * true); GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), "Remmington",
				 * 8, stringsMetalRif, WeaponType.SHOTGUN, false, "shell", 3, 0.15, 8, 1000, 5,
				 * 0.4, 10, false, 1400, ChargingManager.PUMPACTION, 70, null);
				 * GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), "FNFal", 9,
				 * stringsWoodRif, WeaponType.RIFLE, true, "556", 3, 0.3, 32, 1000, 2, true,
				 * 1000, ChargingManager.RAPIDFIRE, 140, null);
				 * 
				 * ArmoryYML rpg = GunYMLCreator.createNewGun(forceUpdate, getDataFolder(),
				 * "RPG", 10, stringsRPG, WeaponType.RPG, false, "rocket", 100, 0.1, 1, 200,
				 * false, 5000, ChargingManager.RPG, 220, null); rpg.set(false,
				 * "addMuzzleSmoke", true); GunYMLCreator.createNewGun(forceUpdate,
				 * getDataFolder(), "UMP", 11, stringsPistol, WeaponType.SMG, true, "9mm", 2,
				 * 0.3, 32, 1000, true, 1300, null, 100, null);
				 * GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), "SW1911", 12,
				 * stringsPistol, WeaponType.PISTOL, true, "9mm", 3, 0.3, 12, 1000, false, 800,
				 * null, 100, null); GunYMLCreator.createNewGun(forceUpdate, getDataFolder(),
				 * "Enfield", 18, stringsPistol, WeaponType.PISTOL, true, "9mm", 3, 0.3, 6,
				 * 1000, 3, 0.25, 1, false, 500, ChargingManager.REVOLVER, 80, null);
				 * GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), "HenryRifle", 19,
				 * stringsGoldRif, WeaponType.RIFLE, true, "556", 8, 0.3, 6, 1000, false, 1000,
				 * ChargingManager.BREAKACTION, 100, null);
				 * GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), "Mouserc96", 20,
				 * stringsPistol, WeaponType.PISTOL, true, "9mm", 3, 0.3, 12, 1000, false, 800,
				 * null, 80, null); GunYMLCreator.createNewGun(forceUpdate, getDataFolder(),
				 * "Dragunov", 23, stringsMetalRif, WeaponType.SNIPER, true, "556", 10, 0.2, 12,
				 * 1000, false, 2400, null, 140, null); GunYMLCreator.createNewGun(forceUpdate,
				 * getDataFolder(), "Spas12", 24, stringsMetalRif, WeaponType.SHOTGUN, false,
				 * "shell", 2, 0.15, 8, 1000, 2, 0.5, 10, true, 2000, null, 80, null); ArmoryYML
				 * aa12 = GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), "AA12", 26,
				 * stringsMetalRif, WeaponType.SHOTGUN, false, "shell", 2, 0.15, 32, 1000, 10,
				 * true, 3300, null, 80, null); aa12.set(false, "addMuzzleSmoke", true);
				 */

				GunYMLCreator.createNewDefaultGun(getDataFolder(), "p30", "P30", 2, stringsPistol, WeaponType.PISTOL,
						null, true, "9mm", 3, 12, 100).setIsSecondaryWeapon(true).done();
				GunYMLCreator.createNewDefaultGun(getDataFolder(), "pkp", "PKP", 3, stringsMetalRif, WeaponType.RIFLE,
						WeaponSounds.GUN_BIG, true, "762", 3, 100, 3000).setFullyAutomatic(3).done();
				GunYMLCreator.createNewDefaultGun(getDataFolder(), "mp5k", "MP5K", 4, stringsMetalRif, WeaponType.SMG,
						null, false, "9mm", 2, 32, 1000).setFullyAutomatic(3).done();
				GunYMLCreator.createNewDefaultGun(getDataFolder(), "ak47", "AK47", 5, stringsMetalRif, WeaponType.RIFLE,
						null, true, "762", 3, 40, 1500).setSway(0.3).setFullyAutomatic(2).done();
				GunYMLCreator.createNewDefaultGun(getDataFolder(), "ak47u", "AK47-U", 6, stringsMetalRif,
						WeaponType.RIFLE, null, true, "762", 3, 30, 2000).setFullyAutomatic(2).done();
				GunYMLCreator.createNewDefaultGun(getDataFolder(), "m16", "M16", 7, stringsMetalRif, WeaponType.RIFLE,
						null, true, "556", 3, 30, 2000).setFullyAutomatic(2).done();
				GunYMLCreator
						.createNewDefaultGun(getDataFolder(), "remington", "Remington", 8, stringsMetalRif,
								WeaponType.SHOTGUN, null, false, "shell", 3, 8, 1000)
						.setChargingHandler(ChargingManager.PUMPACTION).setBulletsPerShot(20).setDistance(70).done();
				GunYMLCreator.createNewDefaultGun(getDataFolder(), "fnfal", "FNFal", 9, stringsMetalRif,
						WeaponType.RIFLE, null, false, "762", 3, 32, 2000).setFullyAutomatic(2).done();
				GunYMLCreator
						.createNewDefaultGun(getDataFolder(), "rpg", "RPG", 10, stringsRPG, WeaponType.RPG, null, false,
								"rocket", 100, 1, 4000)
						.setDelayShoot(1).setChargingHandler(ChargingManager.RPG).setDistance(200).done();
				GunYMLCreator.createNewDefaultGun(getDataFolder(), "ump", "UMP", 11, stringsMetalRif, WeaponType.SMG,
						null, false, "9mm", 2, 32, 1000).setFullyAutomatic(2).done();
				GunYMLCreator.createNewDefaultGun(getDataFolder(), "sw1911", "sw1911", 12, stringsPistol,
						WeaponType.PISTOL, null, true, "9mm", 3, 12, 100).setIsSecondaryWeapon(true).done();
				GunYMLCreator
						.createNewDefaultGun(getDataFolder(), "m40", "M40", 13, stringsWoodRif, WeaponType.SNIPER, null,
								true, "762", 10, 6, 1200)
						.setZoomLevel(9).setDelayShoot(0.7).setChargingHandler(ChargingManager.BOLT).setDistance(280)
						.done();
				GunYMLCreator
						.createNewDefaultGun(getDataFolder(), "enfield", "Enfield", 18, stringsPistol,
								WeaponType.PISTOL, null, true, "9mm", 3, 6, 50)
						.setIsSecondaryWeapon(true).setChargingHandler(ChargingManager.REVOLVER).done();
				GunYMLCreator
						.createNewDefaultGun(getDataFolder(), "henryrifle", "HenryRifle", 19, stringsGoldRif,
								WeaponType.RIFLE, null, true, "556", 4, 6, 800)
						.setChargingHandler(ChargingManager.BREAKACTION).done();
				GunYMLCreator.createNewDefaultGun(getDataFolder(), "mauser", "Mauser.c96", 20, stringsPistol,
						WeaponType.PISTOL, null, true, "9mm", 3, 12, 100).setIsSecondaryWeapon(true).done();

				ArmoryYML grenade = GunYMLCreator.createMisc(false, getDataFolder(), false, "default_grenade",
						"grenade", "&7Grenade",
						Arrays.asList(ChatColor.DARK_GRAY + "[LMB] to pull pin", ChatColor.DARK_GRAY + "[RMB] to throw",
								ChatColor.DARK_GRAY + "Grenades wait " + ChatColor.GRAY + "FIVE seconds"
										+ ChatColor.DARK_GRAY + " before exploding.",
								ChatColor.DARK_RED + "<!>Will Explode Even If Not Thrown<!>"),
						m(22), stringsGrenades, 100, WeaponType.GRENADES, 100, 1);
				grenade.set(false, "radius", 10);
				GunYMLCreator
						.createNewDefaultGun(getDataFolder(), "dragunov", "Dragunov", 23, stringsMetalRif,
								WeaponType.SNIPER, null, true, "762", 7, 12, 2100)
						.setDelayShoot(0.4).setZoomLevel(9).done();
				GunYMLCreator
						.createNewDefaultGun(getDataFolder(), "spas12", "Spas.12", 24, stringsMetalRif,
								WeaponType.SHOTGUN, null, false, "shell", 2, 8, 2100)
						.setBulletsPerShot(20).setDistance(80).done();
				GunYMLCreator
						.createNewDefaultGun(getDataFolder(), "aa12", "AA-12", 26, stringsMetalRif, WeaponType.SHOTGUN,
								null, false, "shell", 2, 32, 3100)
						.setBulletsPerShot(10).setDistance(80).setAutomatic(true).done();

				/**
				 * 27 - 36 taken for custom weapons
				 */
				GunYMLCreator.createMisc(false, getDataFolder(), false, "default_Medkit_camo", "medkitcamo", "&5Medkit",
						null, m(37), stringsHealer, 300, WeaponType.MEDKIT, 1, 1000);
				// ArmoryYML magnum = GunYMLCreator.createNewGun(forceUpdate, getDataFolder(),
				// "Magnum", 38, stringsPistol,
				// WeaponType.PISTOL, true, "9mm", 6, 0.3, 6, 1000, 2, 0.5, 1, false, 500,
				// ChargingManager.REVOLVER, 140, WeaponSounds.GUN_BIG);
				// magnum.set(false, "addMuzzleSmoke", true);
				GunYMLCreator
						.createNewDefaultGun(getDataFolder(), "magnum", "Magnum", 38, stringsPistol, WeaponType.PISTOL,
								WeaponSounds.GUN_BIG, true, "9mm", 6, 6, 500)
						.setChargingHandler(ChargingManager.REVOLVER).setIsSecondaryWeapon(true).done();
				// ArmoryYML awp = GunYMLCreator.createNewGun(forceUpdate, getDataFolder(),
				// "AWP", 39, stringsMetalRif,
				// WeaponType.SNIPER, true, "556", 16, 0.3, 12, 1000, 2, 0.5, 1, false, 500,
				// ChargingManager.BOLT,
				// 260, WeaponSounds.GUN_BIG);
				// awp.set(false, "addMuzzleSmoke", true);
				GunYMLCreator
						.createNewDefaultGun(getDataFolder(), "awp", "AWP", 39, stringsMetalRif, WeaponType.SNIPER,
								WeaponSounds.GUN_BIG, true, "762", 10, 12, 1500)
						.setDelayShoot(0.8).setZoomLevel(9).done();

				ArmoryYML smokegrenade = GunYMLCreator.createMisc(false, getDataFolder(), false, "default_smokegrenade",
						"smokegrenade", "&7Smoke Grenade",
						Arrays.asList(ChatColor.DARK_GRAY + "[LMB] to pull pin", ChatColor.DARK_GRAY + "[RMB] to throw",
								ChatColor.DARK_GRAY + "Smoke Grenades wait " + ChatColor.GRAY + "FIVE seconds"
										+ ChatColor.DARK_GRAY + " before exploding.",
								ChatColor.DARK_RED + "<!>Will Explode Even If Not Thrown<!>"),
						m(40), stringsGrenades, 100, WeaponType.SMOKE_GRENADES, 100, 1);
				smokegrenade.set(false, "radius", 5);
				ArmoryYML flashbanggrenade = GunYMLCreator.createMisc(false, getDataFolder(), false,
						"default_flashbang", "flashbang", "&7FlashBang",
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

				/// * ArmoryYML m4a1s= */ GunYMLCreator.createNewGun(forceUpdate,
				/// getDataFolder(), "M4A1S", 44,
				// stringsMetalRif, WeaponType.RIFLE, true, "556", 4, 0.3, 30, 1000, 2, true,
				/// 1200,
				// ChargingManager.RAPIDFIRE, 140, WeaponSounds.SILENCEDSHOT);

				GunYMLCreator.createNewDefaultGun(getDataFolder(), "m4a1s", "M4A1S", 44, stringsMetalRif,
						WeaponType.RIFLE, null, true, "556", 3, 30, 1200).setFullyAutomatic(2).done();
				// m4a1s.set(false, "addMuzzleSmoke", true);
				// ArmoryYML rpk = GunYMLCreator.createNewGun(forceUpdate, getDataFolder(),
				// "RPK", 45, stringsWoodRif,
				// WeaponType.RIFLE, false, "556", 4, 0.3, 70, 1000, 3, true, 1200,
				// ChargingManager.RAPIDFIRE, 140,
				// null);
				// rpk.set(false, "addMuzzleSmoke", true);
				GunYMLCreator.createNewDefaultGun(getDataFolder(), "rpk", "RPK", 45, stringsWoodRif, WeaponType.RIFLE,
						null, false, "762", 3, 70, 1600).setFullyAutomatic(3).done();
				// GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), "SG-553", 46,
				// stringsMetalRif,
				// WeaponType.RIFLE, true, "556", 4, 0.3, 40, 1000, 2, true, 1200,
				// ChargingManager.RAPIDFIRE, 140,
				// null);
				GunYMLCreator.createNewDefaultGun(getDataFolder(), "sg553", "SG-553", 46, stringsMetalRif,
						WeaponType.RIFLE, null, true, "556", 3, 40, 1200).setFullyAutomatic(2).done();
				// GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), "FN-Five-Seven", 47,
				// stringsPistol,
				// WeaponType.PISTOL, true, "9mm", 3, 0.3, 12, 1000, false, 800, null, 100,
				// null);
				GunYMLCreator.createNewDefaultGun(getDataFolder(), "fnfiveseven", "FN-Five-Seven", 47, stringsPistol,
						WeaponType.PISTOL, null, true, "9mm", 3, 12, 200).setIsSecondaryWeapon(true).done();
				// GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), "DP27", 48,
				// stringsMetalRif, WeaponType.RIFLE,
				// true, "556", 4, 0.4, 47, 1000, 2, true, 1200, ChargingManager.RAPIDFIRE, 140,
				// WeaponSounds.GUN_BIG);

				GunYMLCreator.createNewDefaultGun(getDataFolder(), "dp27", "DP27", 48, stringsMetalRif,
						WeaponType.RIFLE, WeaponSounds.GUN_BIG, true, "762", 3, 47, 2200).setFullyAutomatic(2).done();

				ArmoryYML incedarygrenade = GunYMLCreator.createMisc(false, getDataFolder(), false,
						"default_incendarygrenade", "incendarygrenade", "&7Incendary Grenade",
						Arrays.asList(ChatColor.DARK_GRAY + "[LMB] to pull pin", ChatColor.DARK_GRAY + "[RMB] to throw",
								ChatColor.DARK_GRAY + "Incendary Grenades wait " + ChatColor.GRAY + "FIVE seconds"
										+ ChatColor.DARK_GRAY + " before exploding.",
								ChatColor.DARK_RED + "<!>Will Explode Even If Not Thrown<!>"),
						m(49), stringsGrenades, 100, WeaponType.INCENDARY_GRENADES, 100, 1);
				incedarygrenade.set(false, "radius", 5);
				// GunYMLCreator.createNewGun(true, getDataFolder(), "default_homingrpg",
				// "&6Homing RPG Launcher", 50,
				// stringsRPG, WeaponType.RPG, false, "rocket", 500, 0.2, 1, 1000, false, 3000,
				// ChargingManager.HOMINGRPG, 300, null);
				GunYMLCreator
						.createNewDefaultGun(getDataFolder(), "homingrpg", "&6Homing RPG Launcher", 50, stringsMetalRif,
								WeaponType.RPG, null, false, "rocket", 100, 1, 4900)
						.setDelayShoot(1).setChargingHandler(ChargingManager.HOMINGRPG).done();

				// GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), "flintlockpistol",
				// "\"Harper's Ferry\" Flintlock Pistol", 52, stringsWoodRif, WeaponType.PISTOL,
				// true,
				// "musketball", 10, 0.5, 1, 1000, 3.7, 1, 1, false, 100, null, 100,
				// WeaponSounds.GUN_AUTO);
				GunYMLCreator.createNewDefaultGun(getDataFolder(), "flintlockpistol",
						"\"Harper's Ferry\" Flintlock Pistol", 52, stringsMetalRif, WeaponType.RIFLE,
						WeaponSounds.GUN_AUTO, true, "musketball", 10, 1, 200).setSway(0.47).setDelayReload(4)
						.setDelayShoot(1).setIsSecondaryWeapon(true).done();

				// Jump for armor

				/**
				 * (boolean forceUpdate, File dataFolder, boolean invalid, String filename,
				 * String name, String displayname, List<String> lore, int id, List<String>
				 * craftingRequirements, WeaponType weapontype, boolean enableIronSights, String
				 * ammotype, int damage, double sway, Material type, int maxBullets, int
				 * duribility, double delayReload, double delayShoot, int bulletspershot,
				 * boolean isAutomatic, int cost, ChargingHandlerEnum ch, int distance, int var,
				 * boolean version18, WeaponSounds ws, String particle, double particleR, double
				 * particleG, double particleB, boolean addMuzzleSmoke)
				 * 
				 */

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

				/*
				 * gunRegister.put(m(26), new AA12((int) a("Weapon.AA12.Durability", 1000),
				 * getIngredients("AA12", stringsMetalRif), (int) a("Weapon.AA12.Damage", 1),
				 * (double) a("Weapon.AA12.Price", 2000.0)));
				 */
				// miscRegister.put(m(37), new MedKit(m(37),"MedkitCamo", ChatColor.WHITE + "
				// Medkit", 200));

				List<String> stringsMini = Arrays.asList(
						new String[] { getIngString(Material.IRON_INGOT, 0, 10), getIngString(Material.TNT, 0, 16) });
				List<String> strings10mm = Arrays.asList(new String[] { getIngString(Material.IRON_INGOT, 0, 10),
						getIngString(Material.REDSTONE, 0, 4) });

				List<String> stringsFatman = Arrays.asList(new String[] { getIngString(Material.IRON_INGOT, 0, 32),
						getIngString(Material.REDSTONE, 0, 16), getIngString(Material.BLAZE_POWDER, 0, 8) });

				GunYMLCreator.createAmmo(true, getDataFolder(), false, "default_fusion_cell", "fusion_cell",
						"Fusion Cell", 53, strings10mm, 60, 0.2, 30);
				// GunYMLCreator.createNewGun(true, getDataFolder(), "lazerrifle", "&6Lazer
				// Rifle", 54, strings10mm,
				// WeaponType.LAZER, false, "fusion_cell", 4, 0.2, 20, 1000, false, 2000, null,
				// 120, null);
				GunYMLCreator
						.createNewDefaultGun(getDataFolder(), "lazerrifle", "&6Lazer Rifle", 54, stringsMetalRif,
								WeaponType.LAZER, WeaponSounds.LAZERSHOOT, false, "fusion_cell", 4, 20, 2800)
						.setAutomatic(true).setParticle(1, 0, 0).setDistance(150).done();
				// GunYMLCreator.createNewGun(true, getDataFolder(), "default_fatman", "fatman",
				// "&6Fatman", 55,
				// stringsFatman, WeaponType.RPG, false, "mininuke", 500, 0.2, 1, 1000, false,
				// 3000,
				// ChargingManager.MININUKELAUNCHER, 300, null);
				GunYMLCreator
						.createNewDefaultGun(getDataFolder(), "fatman", "&6Fatman", 55, stringsFatman, WeaponType.RPG,
								WeaponSounds.WARHEAD_LAUNCH, false, "mininuke", 500, 1, 4800)
						.setDelayShoot(1).setChargingHandler(ChargingManager.MININUKELAUNCHER).setDistance(350).done();
				GunYMLCreator.createAmmo(true, getDataFolder(), false, "default_mininuke", "mininuke", "MiniNuke", 56,
						stringsMini, 3000, 100, 1);

				// GunYMLCreator.createNewGun(true, getDataFolder(), "10mm", "&610mm Pistol",
				// 57, strings10mm,
				// WeaponType.PISTOL, true, "9mm", 3, 0.2, 12, 1000, false, 3000, null, 120,
				// null);
				GunYMLCreator.createNewDefaultGun(getDataFolder(), "10mm", "&610mm Pistol", 57, strings10mm,
						WeaponType.PISTOL, null, false, "9mm", 3, 12, 400).setIsSecondaryWeapon(true).done();

				// GunYMLCreator.createNewGun(false, getDataFolder(), false, "instituterifle",
				// "&6Institute Rifle", null,
				// 58, strings10mm, WeaponType.LAZER, false, "fusion_cell", 4, 0.2,
				// Material.DIAMOND_AXE, 20, 1000,
				// 1.5, 0.3, 1, false, 2000, null, 120, 0, false, WeaponSounds.LAZERSHOOT,
				// "REDSTONE", 0.5, 0.9,
				// 0.9);
				GunYMLCreator.createNewDefaultGun(getDataFolder(), "instituterifle", "&6Institute Rifle", 58,
						stringsMetalRif, WeaponType.LAZER, WeaponSounds.LAZERSHOOT, false, "fusion_cell", 4, 20, 2800)
						.setAutomatic(true).setParticle(0.5, 0.9, 0.9).setDistance(150).done();

				// GunYMLCreator.createNewGun(true, getDataFolder(), "instituterifle",
				// "&6Institute Rifle",null, 58,
				// strings10mm, WeaponType.LAZER, false, "fusion_cell", 4,
				// 0.2,Material.DIAMOND_AXE, 20, 1000, false, 2000, null,
				// 120,0,false,WeaponSounds.LAZERSHOOT,
				// "REDSTONE",0.5,0.9,0.9);

				// GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), "musket", "\"Brown
				// Bess\" Musket", 63,
				// stringsWoodRif, WeaponType.RIFLE, true, "musketball", 10, 0.38, 1, 1000, 5,
				// 1, 1, false, 200,
				// null, 100, WeaponSounds.GUN_AUTO);
				GunYMLCreator
						.createNewDefaultGun(getDataFolder(), "musket", "\"Brown Bess\" Musket", 63, stringsMetalRif,
								WeaponType.RIFLE, WeaponSounds.GUN_AUTO, true, "musketball", 10, 1, 300)
						.setSway(0.38).setDelayReload(5).setDelayShoot(1).done();

				new PushbackCharger();
				List<String> stringsRifle = Arrays.asList(new String[] { getIngString(Material.IRON_INGOT, 0, 8),
						getIngString(Material.REDSTONE, 0, 3) });
				List<String> stringsLight = Arrays.asList(new String[] { getIngString(Material.IRON_INGOT, 0, 8), getIngString(Material.NETHER_STAR, 0, 1) });

				GunYMLCreator.createNewGun(false, getDataFolder(), false, "default_aliensrifle", "M41PulseRifle",
						ChatColor.GOLD + "M41-A Pulse Rifle", Arrays.asList("&fGame over, man. Game over!"), 64,
						stringsRifle, WeaponType.RIFLE, false, "556", 4, 0.3, Material.DIAMOND_AXE, 40, 1000, 1.5, 0.3,
						2, true, 3000, ChargingManager.RAPIDFIRE, 200, false, WeaponSounds.GUN_MEDIUM);
				GunYMLCreator.createNewGun(false, getDataFolder(), false, "default_auto9", "Auto9",
						ChatColor.GOLD + "The Auto-9", Arrays.asList("&fDead or alive, you're coming with me! "), 65,
						stringsRifle, WeaponType.PISTOL, true, "556", 5, 0.3, Material.DIAMOND_AXE, 12, 1000, 1.5, 0.3,
						1, false, 2000, null, 200, false, WeaponSounds.GUN_MEDIUM);
				GunYMLCreator.createNewGun(false, getDataFolder(), false, "default_arcgun9", "ArcGun9",
						ChatColor.GOLD + "The Arc-Gun-9", Arrays.asList("&fPushy!"), 66, stringsRifle, WeaponType.LAZER,
						false, "fusion_cell", 8, 0.3, Material.DIAMOND_AXE, 5, 1000, 1.5, 1, 1, false, 3500,
						PushbackCharger.NAME, 35, false, WeaponSounds.SHOCKWAVE);

				GunYMLCreator.createNewGun(false, getDataFolder(), false, "default_halorifle", "UNSCAssaultRifle",
						ChatColor.GOLD + "UNSC Assault Rifle", Arrays.asList("&fAlso known as the \"MA5B\""), 67,
						stringsRifle, WeaponType.RIFLE, true, "556", 3, 0.3, Material.DIAMOND_AXE, 32, 1000, 1.5, 0.3,
						2, true, 2500, ChargingManager.RAPIDFIRE, 200, false, WeaponSounds.GUN_MEDIUM);
				GunYMLCreator.createNewGun(false, getDataFolder(), false, "default_haloalien", "AlienNeedler",
						ChatColor.GOLD + "\"Needler\"", Arrays.asList("&fWarning: Sharp"), 68, stringsRifle,
						WeaponType.PISTOL, true, "fusion_cell", 1, 0.3, Material.DIAMOND_AXE, 26, 1000, 1.5, 0.3, 3,
						true, 2000, ChargingManager.RAPIDFIRE, 200, 0, false, WeaponSounds.GUN_NEEDLER, "REDSTONE", 1,
						0.1, 1);

				GunYMLCreator.createNewGun(false, getDataFolder(), false, "default_thatgun", "ThatGun",
						ChatColor.GOLD + "\"That Gun\"",
						Arrays.asList("&fAlso known as the \"LAPD 2019 Detective Special\""), 69, stringsRifle,
						WeaponType.PISTOL, true, "556", 9, 0.3, Material.DIAMOND_AXE, 12, 1000, 1.3, 0.6, 1, false,
						5000, null, 200, false, WeaponSounds.GUN_DEAGLE);

				GunYMLCreator.createMisc(false, getDataFolder(), false, "default_lightsaberblue", "LightSaberBlue",
						ChatColor.GOLD + "(Blue)LightSaber", Arrays.asList("&fMay The Force be with you", "&fAlways"),
						Material.DIAMOND_AXE, 70, stringsLight, 10000, WeaponType.MEELEE, 9, 1000);
				GunYMLCreator.createMisc(false, getDataFolder(), false, "default_lightsaberred", "LightSaberRed",
						ChatColor.GOLD + "(Red)LightSaber", Arrays.asList("&fMay The Force be with you", "&fAlways"),
						Material.DIAMOND_AXE, 71, stringsLight, 10000, WeaponType.MEELEE, 9, 1000);

				GunYMLCreator.createNewGun(false, getDataFolder(), false, "default_blaster", "Blaster",
						ChatColor.GOLD + "\"Blaster\" Pistol", Arrays.asList("&fMiss all the shots you want!"), 72,
						stringsRifle, WeaponType.LAZER, true, "fusion_cell", 5, 0.3, Material.DIAMOND_AXE, 20, 1000,
						1.5, 0.3, 1, true, 2200, ChargingManager.RAPIDFIRE, 200, 0, false, WeaponSounds.GUN_STARWARS,
						"REDSTONE", 1, 0, 0);
				GunYMLCreator.createNewGun(false, getDataFolder(), false, "default_hl2pulserifle", "pulserifle",
						ChatColor.GOLD + "Overwatch Pulse Rifle",
						Arrays.asList("&fStardard Issue Rifles for Combie solders."), 73, stringsRifle,
						WeaponType.LAZER, true, "fusion_cell", 2, 0.3, Material.DIAMOND_AXE, 30, 1000, 1.5, 0.3, 3,
						true, 3000, ChargingManager.RAPIDFIRE, 200, 0, false, WeaponSounds.GUN_HALOLAZER, "REDSTONE",
						05, 0.99, 0.99);
				GunYMLCreator.createNewGun(false, getDataFolder(), false, "default_vera", "vera",
						ChatColor.GOLD + "\"Vera\"",
						Arrays.asList("&fThe Callahan Full-bore Auto-lock.", "&7\"Customized trigger, …",
								"&7…double cartridge thorough gauge.", "&7It is my very favorite gun …",
								"&7…This is the best gun made by man.", "&7 It has extreme sentimental value …",
								"&7…I call her Vera.\"-Jayne Cobb"),
						74, stringsRifle, WeaponType.RIFLE, true, "556", 3, 0.3, Material.DIAMOND_AXE, 30, 1000, 1.5,
						0.3, 2, true, 2000, ChargingManager.RAPIDFIRE, 200, 0, false, WeaponSounds.GUN_MEDIUM);

				GunYMLCreator.createNewDefaultGun(getDataFolder(), "mac10", "Mac-10", 75, stringsMetalRif,
						WeaponType.SMG, WeaponSounds.GUN_SMALL_AUTO, true, "9mm", 2, 32, 1100).setFullyAutomatic(3)
						.done();
				GunYMLCreator.createNewDefaultGun(getDataFolder(), "uzi", "UZI", 76, stringsMetalRif, WeaponType.SMG,
						WeaponSounds.GUN_SMALL_AUTO, true, "9mm", 2, 25, 1100).setFullyAutomatic(3).done();
				GunYMLCreator
						.createNewDefaultGun(getDataFolder(), "skorpion", "Skorpion vz.61", 77, stringsMetalRif,
								WeaponType.SMG, WeaponSounds.GUN_SMALL_AUTO, true, "9mm", 2, 20, 1100)
						.setFullyAutomatic(3).done();

				GunYMLCreator
						.createNewDefaultGun(getDataFolder(), "sks", "SKS-45", 78, stringsWoodRif, WeaponType.SNIPER,
								null, true, "762", 7, 10, 1500)
						.setDelayShoot(0.6).setZoomLevel(6).setDistance(290).done();

				expansionPacks.add(m(88));
				// GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), "mac10", "Mac-10",
				// 75, stringsMetalRif,
				// WeaponType.SMG, true, "9mm", 3, 0.4, 32, 1000, 3, true, 1000,
				// ChargingManager.RAPIDFIRE, 100,
				// WeaponSounds.GUN_SMALL_AUTO);
				// GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), "uzi", "UZI", 76,
				// stringsMetalRif,
				// WeaponType.SMG, true, "9mm", 3, 0.35, 25, 1000, 3, true, 1000,
				// ChargingManager.RAPIDFIRE, 100,
				// WeaponSounds.GUN_SMALL_AUTO);
				// GunYMLCreator.createNewGun(forceUpdate, getDataFolder(), "skorpion",
				// "Škorpion vz.61", 77,
				// stringsMetalRif, WeaponType.SMG, true, "9mm", 3, 0.3, 20, 1000, 3, true,
				// 1000,
				// ChargingManager.RAPIDFIRE, 100, WeaponSounds.GUN_SMALL_AUTO);

				// Covers all SciFi weapons

			}

			// GunYMLCreator.createNewGun(false, getDataFolder(), true, "ExampleGun",
			// "Examplegun",
			// ChatColor.GOLD + "Example", Arrays.asList("Hello", "more lines"), 0,
			// stringsGoldRif,
			// WeaponType.PISTOL, false, "556", 3, 0.3, Material.DIAMOND_AXE, 100, 1000,
			// 1.5, 0.25, 1, true,
			// 1000000, null, 120, false, WeaponSounds.GUN_AUTO);

			GunYMLCreator
					.createNewCustomGun(getDataFolder(), "example_gun", "ExampleGun", "&7ExampleGun", 35,
							stringsMetalRif, WeaponType.RIFLE, WeaponSounds.GUN_MEDIUM, true, "556", 4, 100, 69)
					.setInvalid(true).setDropGlow(ChatColor.GREEN).setLore(Arrays.asList("Hello", "more lines")).done();

			GunYMLCreator.createAmmo(false, getDataFolder(), true, "example_ammo", "example", "7fDisplayname",
					Arrays.asList("Example", "Lore"), Material.DIAMOND_AXE, 27, stringsAmmo, 1, 1.0, 16);

			GunYMLCreator.createMisc(false, getDataFolder(), true, "example_knife", "ExampleKnife", "&7Example Knife",
					Arrays.asList("Now, this is a knife!"), Material.IRON_SWORD, 1, stringsMetalRif, 100,
					WeaponType.MEELEE, 12, 100);
			GunYMLCreator.createAttachment(false, getDataFolder(), true, "example_attachment", "example_attachment",
					"Attachment For AK47", null, m(28), stringsMetalRif, 100, "AK47");

			ArmoryYML skullammo = GunYMLCreator.createSkullAmmo(false, getDataFolder(), true, "example_skullammo",
					"exampleSkullAmmo", "&7 Example Ammo", null, MultiVersionLookup.getSkull(), 3, "cactus", null, 4, 1, 64);
			skullammo.set(false, "skull_owner_custom_url_COMMENT",
					"Only specify the custom URL if the head does not use a player's skin, and instead sets the skin to a base64 value. If you need to get the head using a command, the URL should be set to the string of letters after \"Properties:{textures:[{Value:\"");
			skullammo.set(false, "skull_owner_custom_url",
					"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTg3ZmRmNDU4N2E2NDQ5YmZjOGJlMzNhYjJlOTM4ZTM2YmYwNWU0MGY2ZmFhMjc3ZDcxYjUwYmNiMGVhNjgzOCJ9fX0=");

		}

		{
			// Force creation of kevlar
			armorRegister.put(m(25), new Kevlar(m(25), getIngredients("Kevlarnk1", Arrays.asList(
					new String[] { getIngString(Material.IRON_INGOT, 0, 15), getIngString(Material.OBSIDIAN, 0, 1) })),
					(int) a("Weapon.Kevlarmk1.DamageThreshold", 1), (double) a("Weapon.Kevlarmk1.Price", 1200.0)));

			angledArmor.put(m(59), new AngledArmor(m(59), m(25), 135f));
			angledArmor.put(m(60), new AngledArmor(m(60), m(25), 180f));
			angledArmor.put(m(61), new AngledArmor(m(61), m(25), 45f));
			angledArmor.put(m(62), new AngledArmor(m(62), m(25), 0f));

		}
		// Skull texture
		GunYMLLoader.loadAmmo(this);
		GunYMLLoader.loadMisc(this);
		GunYMLLoader.loadGuns(this);
		GunYMLLoader.loadAttachments(this);
		if (Main.enableBleeding)
			BulletWoundHandler.startTimer();

		AngledArmorHandler.stopTasks();
		AngledArmorHandler.init();

		if (tfh != null) {
			tfh = new TreeFellerHandler();
			Bukkit.getPluginManager().registerEvents(tfh, this);
		}
		coloredGunScoreboard = new ArrayList<>();
		coloredGunScoreboard.add(registerGlowTeams(Bukkit.getScoreboardManager().getMainScoreboard()));
	}

	public Scoreboard registerGlowTeams(Scoreboard sb) {
		if (sb.getTeam("QA_RED") == null) {
			for (ChatColor c : ChatColor.values()) {
				if (sb.getTeam("QA_" + c.name() + "") == null)
					sb.registerNewTeam("QA_" + c.name() + "").setPrefix(c + "");
			}
		}
		return sb;
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
					DEBUG("Setting damage for " + aa.getName() + " to be equal to " + ((MeleeItems) aa).getDamage()
							+ ". was " + e.getDamage());
					e.setDamage(((MeleeItems) aa).getDamage());
				}
			}
			if (isGun(d.getItemInHand()) || isGunWithAttchments(d.getItemInHand()) || isIS(d.getItemInHand()))
				DEBUG("The player " + e.getEntity().getName() + " was shot with a gun! Damage=" + e.getDamage());
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
			try {
				if (e.isSneaking()) {
					if (isGun(e.getPlayer().getItemInHand()) || isGunWithAttchments(e.getPlayer().getItemInHand())
							&& (!isCustomItem(e.getPlayer().getInventory().getItemInOffHand()))) {
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
													.contains(S_RELOADING_MESSAGE))
										return;
									if (Update19OffhandChecker.supportOffhand(e.getPlayer())) {
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
						e.getPlayer().getInventory().setItemInMainHand(e.getPlayer().getInventory().getItemInOffHand());
						e.getPlayer().getInventory().setItemInOffHand(null);
					}
				}
			} catch (Error | Exception e2) {
				DEBUG("Failed to sneak and put gun in off hand.");
			}
		}
	}

	public ItemStack[] getIngredients(String name, List<String> ing) {
		/*
		 * if (!getConfig().contains("Crafting." + name)) { getConfig().set("Crafting."
		 * + name, ing); saveConfig(); } List<String> e =
		 * getConfig().getStringList("Crafting." + name);
		 */
		return convertIngredients(ing);
	}

	public ItemStack[] getIngredients(String name) {
		/*
		 * if (!getConfig().contains("Crafting." + name)) { getConfig().set("Crafting."
		 * + name, Arrays.asList(new String[] { getIngString(Material.IRON_INGOT, 0, 10)
		 * })); saveConfig(); } List<String> e = getConfig().getStringList("Crafting." +
		 * name);
		 */
		return convertIngredients(Arrays.asList(new String[] { getIngString(Material.IRON_INGOT, 0, 10) }));
	}

	public ItemStack[] convertIngredients(List<String> e) {
		ItemStack[] list = new ItemStack[e.size()];
		for (int i = 0; i < e.size(); i++) {
			String[] k = e.get(i).split(",");
			ItemStack temp = null;
			try {
				temp = new ItemStack(Material.matchMaterial(k[0]));
			} catch (Exception e2) {
				//temp = new ItemStack(Integer.parseInt(k[0]));
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
			if (b("getResourcepack", args[0]))
				s.add("getResourcepack");
			if (b("sendResourcepack", args[0]))
				s.add("sendResourcepack");
			if (b("version", args[0]))
				s.add("version");
			if (enableShop)
				if (b("shop", args[0]))
					s.add("shop");
			if (enableCrafting)
				if (b("craft", args[0]))
					s.add("craft");
			// if (b("getOpenGunSlot", args[0]))
			// s.add("getOpenGunSlot");
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
		if (e.getPlayer().getItemInHand() != null && (isCustomItem(e.getPlayer().getItemInHand()))) {
			e.setCancelled(true);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("QualityArmory")) {
			if (args.length > 0) {

				if (args[0].equalsIgnoreCase("debug")) {
					Gunner.createGunner(((Entity) sender).getLocation(), "ak47");
				}
				if (args[0].equalsIgnoreCase("debug2")) {
					for (Gunner g : gunners) {
						g.dispose();
					}
				}

				if (args[0].equalsIgnoreCase("version")) {
					sender.sendMessage(prefix + ChatColor.WHITE + " This server is using version " + ChatColor.GREEN
							+ this.getDescription().getVersion() + ChatColor.WHITE + " of QualityArmory");
					sender.sendMessage("--==Changelog==--");
					InputStream in = getClass().getResourceAsStream("/changelog.txt");
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					for (int i = 0; i < 7; i++) {
						try {
							String s = reader.readLine();
							if (s.length() <= 1)
								break;
							if (i == 6) {
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
				/*
				 * if (args[0].equalsIgnoreCase("getOpenGunSlot")) { if
				 * (sender.hasPermission("qualityarmory.getopengunslot")) {
				 * List<MaterialStorage> getAllKeys = new ArrayList<>();
				 * getAllKeys.addAll(gunRegister.keySet());
				 * getAllKeys.addAll(ammoRegister.keySet());
				 * getAllKeys.addAll(miscRegister.keySet());
				 * getAllKeys.addAll(armorRegister.keySet()); int openID = 1; for
				 * (MaterialStorage i : getAllKeys) { if (i.getMat() == guntype) if (i.getData()
				 * > openID) openID = i.getData(); } sender.sendMessage(prefix +
				 * " The next open slot for \"" + guntype.name() + "\"-base-guns is " + (openID
				 * + 1)); return true; } else { sender.sendMessage(prefix + ChatColor.RED +
				 * S_NOPERM); return true; } }
				 */
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
									(itemInHand.getType() == MultiVersionLookup.getSkull()
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
				if (args[0].equalsIgnoreCase("sendResourcepack")) {
					namesToBypass.remove(sender.getName());
					resourcepackwhitelist.set("Names_Of_players_to_bypass", namesToBypass);
					sender.sendMessage(prefix + S_RESOURCEPACK_OPTIN);
					sendResourcepack((Player) sender, true);
					return true;
				}
				if (args[0].equalsIgnoreCase("getResourcepack")) {
					namesToBypass.add(sender.getName());
					resourcepackwhitelist.set("Names_Of_players_to_bypass", namesToBypass);
					sender.sendMessage(prefix + S_RESOURCEPACK_DOWNLOAD);
					sender.sendMessage(url);
					sender.sendMessage(prefix + S_RESOURCEPACK_BYPASS);
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
									(itemInHand.getType() ==MultiVersionLookup.getSkull()
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
					sender.sendMessage(prefix + " Overriding resourcepack requirement.");
					return true;
				}
				if (shouldSend && !resourcepackReq.contains(player.getUniqueId())) {
					sendResourcepack(((Player) sender), true);
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

		if (name != null && (name.startsWith(S_craftingBenchName) || name.startsWith(S_shopName))) {
			DEBUG("ClickedShop");

			boolean shop = (name.startsWith(S_shopName));

			e.setCancelled(true);

			if (shop) {

				if (!enableEconomy) {
					e.getWhoClicked().closeInventory();
					e.getWhoClicked().sendMessage(prefix + S_noEcon);
					return;
				}

			}

			if (e.getCurrentItem() != null) {
				if (shop) {

					if (e.getCurrentItem().isSimilar(prevButton)) {
						int page = Integer.parseInt(e.getInventory().getTitle().split(S_shopName)[1]) - 1;
						e.getWhoClicked().closeInventory();
						e.getWhoClicked().openInventory(createShop(Math.max(0, page)));
						DEBUG("Prev_Shop");
						return;
					}
					if (e.getCurrentItem().isSimilar(nextButton)) {
						int page = Integer.parseInt(e.getInventory().getTitle().split(S_shopName)[1]) + 1;
						e.getWhoClicked().closeInventory();
						e.getWhoClicked().openInventory(createShop(Math.min(getMaxPages(), page)));
						DEBUG("next_Shop");
						return;
					}
				} else {
					if (e.getCurrentItem().isSimilar(prevButton)) {
						int page = Integer.parseInt(e.getInventory().getTitle().split(S_craftingBenchName)[1]) - 1;
						e.getWhoClicked().closeInventory();
						e.getWhoClicked().openInventory(createCraft(Math.max(0, page)));
						DEBUG("Prev_craft");
						return;
					}
					if (e.getCurrentItem().isSimilar(nextButton)) {
						int page = Integer.parseInt(e.getInventory().getTitle().split(S_craftingBenchName)[1]) + 1;
						e.getWhoClicked().closeInventory();
						e.getWhoClicked().openInventory(createCraft(Math.min(getMaxPages(), page)));
						DEBUG("next_craft");
						return;
					}
				}

				if (isGun(e.getCurrentItem())) {
					Gun g = getGun(e.getCurrentItem());
					if ((shop && EconHandler.hasEnough(g, (Player) e.getWhoClicked()))
							|| (!shop && lookForIngre((Player) e.getWhoClicked(), g))
							|| e.getWhoClicked().getGameMode() == GameMode.CREATIVE) {
						if (shop)
							EconHandler.pay(g, (Player) e.getWhoClicked());
						else
							removeForIngre((Player) e.getWhoClicked(), g);
						ItemStack s = ItemFact.getGun(g);
						s.setAmount(g.getCraftingReturn());
						e.getWhoClicked().getInventory().addItem(s);
						shopsSounds(e, shop);
						DEBUG("Buy-gun");
					} else {
						DEBUG("Failed to buy/craft gun");
						e.getWhoClicked().closeInventory();
						if (shop)
							e.getWhoClicked().sendMessage(prefix + S_noMoney);
						else
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
					if ((shop && EconHandler.hasEnough(g2, (Player) e.getWhoClicked()))
							|| (!shop && lookForIngre((Player) e.getWhoClicked(), g))
							|| e.getWhoClicked().getGameMode() == GameMode.CREATIVE) {
						if (shop)
							EconHandler.pay(g2, (Player) e.getWhoClicked());
						else
							removeForIngre((Player) e.getWhoClicked(), g);
						ItemStack s = ItemFact.getGun(g);
						s.setAmount(g2.getCraftingReturn());
						e.getWhoClicked().getInventory().addItem(s);
						shopsSounds(e, shop);
						DEBUG("Buy-attachment");
					} else {
						DEBUG("Failed to buy/craft attachment");
						e.getWhoClicked().closeInventory();
						if (shop)
							e.getWhoClicked().sendMessage(prefix + S_noMoney);
						else
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
					if ((shop && EconHandler.hasEnough(g, (Player) e.getWhoClicked()))
							|| (!shop && lookForIngre((Player) e.getWhoClicked(), g))
							|| e.getWhoClicked().getGameMode() == GameMode.CREATIVE) {
						if (shop)
							EconHandler.pay(g, (Player) e.getWhoClicked());
						else
							removeForIngre((Player) e.getWhoClicked(), g);
						AmmoUtil.addAmmo((Player) e.getWhoClicked(), g, g.getCraftingReturn());
						shopsSounds(e, shop);
						DEBUG("Buy-ammo");
					} else {
						e.getWhoClicked().closeInventory();
						DEBUG("Failed to buy/craft ammo");
						if (shop)
							e.getWhoClicked().sendMessage(prefix + S_noMoney);
						else
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
					if ((shop && EconHandler.hasEnough(g, (Player) e.getWhoClicked()))
							|| (!shop && lookForIngre((Player) e.getWhoClicked(), g))
							|| e.getWhoClicked().getGameMode() == GameMode.CREATIVE) {
						if (shop)
							EconHandler.pay(g, (Player) e.getWhoClicked());
						else
							removeForIngre((Player) e.getWhoClicked(), g);
						ItemStack s = ItemFact.getObject(g);
						s.setAmount(g.getCraftingReturn());
						e.getWhoClicked().getInventory().addItem(s);
						shopsSounds(e, shop);
						DEBUG("Buy-Misc");
					} else {
						DEBUG("Failed to buy/craft misc");
						e.getWhoClicked().closeInventory();
						if (shop)
							e.getWhoClicked().sendMessage(prefix + S_noMoney);
						else
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
					if (isAngledArmor(e.getCurrentItem())) {
						ArmorObject g = armorRegister.get(getAngledArmor(e.getCurrentItem()).getBase());
						if ((shop && EconHandler.hasEnough(g, (Player) e.getWhoClicked()))
								|| (!shop && lookForIngre((Player) e.getWhoClicked(), g))
								|| e.getWhoClicked().getGameMode() == GameMode.CREATIVE) {
							if (shop)
								EconHandler.pay(g, (Player) e.getWhoClicked());
							else
								removeForIngre((Player) e.getWhoClicked(), g);
							ItemStack s = ItemFact.getArmor(g);
							s.setAmount(g.getCraftingReturn());
							e.getWhoClicked().getInventory().addItem(s);
							shopsSounds(e, shop);
							DEBUG("Buy-armor");
						} else {
							DEBUG("Failed to buy/craft armor");
							e.getWhoClicked().closeInventory();
							if (shop)
								e.getWhoClicked().sendMessage(prefix + S_noMoney);
							else
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
						ArmorObject g = getArmor(e.getCurrentItem());
						if ((shop && EconHandler.hasEnough(g, (Player) e.getWhoClicked()))
								|| (!shop && lookForIngre((Player) e.getWhoClicked(), g))
								|| e.getWhoClicked().getGameMode() == GameMode.CREATIVE) {
							if (shop)
								EconHandler.pay(g, (Player) e.getWhoClicked());
							else
								removeForIngre((Player) e.getWhoClicked(), g);
							ItemStack s = ItemFact.getArmor(g);
							s.setAmount(g.getCraftingReturn());
							e.getWhoClicked().getInventory().addItem(s);
							shopsSounds(e, shop);
							DEBUG("Buy-armor");
						} else {
							DEBUG("Failed to buy/craft armor");
							e.getWhoClicked().closeInventory();
							if (shop)
								e.getWhoClicked().sendMessage(prefix + S_noMoney);
							else
								e.getWhoClicked().sendMessage(prefix + S_missingIngredients);
							try {
								((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(),
										Sound.BLOCK_ANVIL_BREAK, 1, 1);
							} catch (Error e2) {
								((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(),
										Sound.valueOf("ANVIL_BREAK"), 1, 1);
							}
						}
					}
				} else {
					e.setCancelled(true);
				}
			}
			return;
		}

		// player inv

		if ((e.getCurrentItem() != null && isIS(e.getCurrentItem()))) {
			e.setCancelled(true);
			return;
		}
		if (isAngledArmor(e.getCurrentItem())) {
			e.setCurrentItem(ItemFact.getArmor(getArmor(e.getCurrentItem())));
		}

		/*
		 * if (e.getCurrentItem() == null) { if (e.getClickedInventory() == null) {
		 * checkforDups((Player) e.getWhoClicked(), e.getCursor()); } return; }
		 */
		// final ItemStack curr =
		// e.getCurrentItem()==null?null:e.getCurrentItem().clone();
		// final ItemStack curs = e.getCursor()==null?null:e.getCursor().clone();

		/*
		 * new BukkitRunnable() {
		 * 
		 * @Override public void run() { checkforDups((Player) e.getWhoClicked(), curr,
		 * curs); }
		 * 
		 * }.runTaskLater(this, 1);
		 */

		/*
		 * if ((e.getCursor() != null && isGun(e.getCursor())/ * e.getCursor().getType(
		 * ) == guntype /) || (e.getCurrentItem() != null && isGun(e.getCurrentItem())/*
		 * e. getCurrentItem ( ) . getType ( ) == guntype /)) { e.setCancelled(true); if
		 * ((e.getCurrentItem().getItemMeta().hasDisplayName() &&
		 * e.getCurrentItem().getItemMeta().getDisplayName().contains(
		 * S_RELOADING_MESSAGE)) || (e.getCursor().getItemMeta().hasDisplayName() &&
		 * e.getCursor().getItemMeta().getDisplayName().contains(S_RELOADING_MESSAGE)))
		 * { return; } }
		 */
	}

	private void shopsSounds(InventoryClickEvent e, boolean shop) {

		if (shop) {
			try {
				((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(), Sound.BLOCK_ANVIL_USE, 0.7f, 1);
			} catch (Error e2) {
				((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(), Sound.valueOf("ANVIL_USE"),
						0.7f, 1);
			}
		} else {
			try {
				((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(), MultiVersionLookup.getHarp(), 0.7f, 1);
			} catch (Error e2) {
				((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(), Sound.valueOf("NOTE_PIANO"),
						0.7f, 1);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPickup(PlayerPickupItemEvent e) {

		if (isCustomItem(e.getItem().getItemStack())) {
			if (shouldSend && !namesToBypass.contains(e.getPlayer().getName())
					&& !resourcepackReq.contains(e.getPlayer().getUniqueId())) {
				sendResourcepack(e.getPlayer(), true);
			}

			if (isGun(e.getItem().getItemStack())) {
				Gun g = getGun(e.getItem().getItemStack());
				try {
					if (AutoDetectResourcepackVersion) {
						if (us.myles.ViaVersion.bukkit.util.ProtocolSupportUtil
								.getProtocolVersion(e.getPlayer()) < ID18) {
							if (g == null)
								g = gunRegister.get(getGunWithAttchments(e.getItem().getItemStack()).getBase());

							if (!g.is18Support()) {
								for (Gun g2 : gunRegister.values()) {
									if (g2.is18Support()) {
										if (g2.getDisplayName().equals(g.getDisplayName())) {
											e.getItem().setItemStack(ItemFact.getGun(g2));
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
											e.getItem().setItemStack(ItemFact.getGun(g2));
											Main.DEBUG("Custom-validation check 2");
											return;
										}
									}
								}
							}
						} else {
							if (us.myles.ViaVersion.bukkit.util.ProtocolSupportUtil
									.getProtocolVersion(e.getPlayer()) >= ID18) {
								if (g == null)
									g = gunRegister.get(getGunWithAttchments(e.getItem().getItemStack()).getBase());
								if (g.is18Support()) {
									for (Gun g2 : gunRegister.values()) {
										if (!g2.is18Support()) {
											if (g2.getDisplayName().equals(g.getDisplayName())) {
												e.getItem().setItemStack(ItemFact.getGun(g2));
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
												e.getItem().setItemStack(ItemFact.getGun(g2));
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
				checkforDups(e.getPlayer(), e.getItem().getItemStack());

				if (enablePrimaryWeaponHandler)
					if (isOverLimitForGun(g, e.getPlayer()))
						e.setCancelled(true);
				return;
			}

			if (isAmmo(e.getItem().getItemStack())) {
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
		}
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
							&& (ings[i].getDurability() == 0 || is.getDurability() == ings[i].getDurability())) {
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

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		if (reloadingTasks.containsKey(e.getEntity().getUniqueId())) {
			for (BukkitTask r : reloadingTasks.get(e.getEntity().getUniqueId())) {
				r.cancel();
				DEBUG("Canceling reload task " + r.getTaskId());
			}
		}
		reloadingTasks.remove(e.getEntity().getUniqueId());

		for (ItemStack is : new ArrayList<>(e.getDrops())) {
			if (isIS(is)) {
				e.getDrops().remove(is);
				DEBUG("Removing IronSights");
			}
		}

		if (e.getDeathMessage() != null && IronSightsToggleItem.getItemName() != null
				&& e.getDeathMessage().contains(IronSightsToggleItem.getItemName())) {
			try {
				e.setDeathMessage(e.getDeathMessage().replace(IronSightsToggleItem.getItemName(),
						e.getEntity().getKiller().getInventory().getItemInOffHand().getItemMeta().getDisplayName()));
				DEBUG("Removing ironsights from death message and replaced with gun's name");
			} catch (Error | Exception e34) {
			}
		}
		BulletWoundHandler.bleedoutMultiplier.remove(e.getEntity().getUniqueId());
		BulletWoundHandler.bloodLevel.put(e.getEntity().getUniqueId(), bulletWound_initialbloodamount);

		if (e.getEntity().getKiller() instanceof Player) {
			Player killer = (Player) e.getEntity().getKiller();
			if (isGun(killer.getItemInHand()) || isGunWithAttchments(killer.getItemInHand())
					|| isIS(killer.getItemInHand())) {
				DEBUG("This player \"" + e.getEntity().getName() + "\" was killed by a player with a gun");
			} else if (isCustomItem(e.getEntity().getItemInHand())) {
				DEBUG("This player \"" + e.getEntity().getName() + "\" was killed by a player, but not with a gun");
			}
		}
	}

	@EventHandler
	public void onDeath(PlayerRespawnEvent e) {
		BulletWoundHandler.bleedoutMultiplier.remove(e.getPlayer().getUniqueId());
		BulletWoundHandler.bloodLevel.put(e.getPlayer().getUniqueId(), bulletWound_initialbloodamount);
	}

	@SuppressWarnings({ "deprecation", "null" })
	@EventHandler
	public void onClick(final PlayerInteractEvent e) {
		Main.DEBUG("InteractEvent Called");

		if (Main.kickIfDeniedRequest && sentResourcepack.containsKey(e.getPlayer().getUniqueId())
				&& System.currentTimeMillis() - sentResourcepack.get(e.getPlayer().getUniqueId()) >= 3000) {
			// the player did not accept resourcepack, and got away with it
			e.setCancelled(true);
			e.getPlayer().kickPlayer(Main.S_KICKED_FOR_RESOURCEPACK);
			return;
		}

		if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType() == Material.ANVIL
				&& overrideAnvil && !e.getPlayer().isSneaking()) {
			if (shouldSend && !resourcepackReq.contains(e.getPlayer().getUniqueId())) {
				sendResourcepack(e.getPlayer(), true);
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

		try {
			if (e.getPlayer().getInventory().getItemInOffHand() != null
					&& isCustomItem(e.getPlayer().getInventory().getItemInOffHand(), -5) && e.getPlayer().getInventory()
							.getItemInOffHand().getEnchantments().containsKey(Enchantment.MENDING)) {
				int safeDurib = findSafeSpot(e.getPlayer().getInventory().getItemInOffHand(), false);
				Main.DEBUG("Safe Durib with mending OFFHAND= " + (safeDurib) + "! ORG "
						+ e.getPlayer().getInventory().getItemInOffHand().getDurability());
				ItemStack is = e.getPlayer().getInventory().getItemInOffHand();
				is.setDurability((short) Math.max(0, safeDurib - 1));
				e.getPlayer().getInventory().setItemInOffHand(is);
				return;
			}

		} catch (Error | Exception e4) {
		}
		try {
			if (e.getPlayer().getItemInHand() != null && isCustomItem(e.getPlayer().getInventory().getItemInHand(), -5)
					&& e.getPlayer().getInventory().getItemInHand().getEnchantments()
							.containsKey(Enchantment.MENDING)) {
				int safeDurib = findSafeSpot(e.getPlayer().getInventory().getItemInHand(), false);
				Main.DEBUG("Safe Durib with mending OFFHAND= " + (safeDurib + 4) + "! ORG "
						+ e.getPlayer().getInventory().getItemInHand().getDurability());
				ItemStack is = e.getPlayer().getInventory().getItemInHand();
				is.setDurability((short) Math.max(0, safeDurib - 1));
				e.getPlayer().getInventory().setItemInHand(is);
				return;
			}

		} catch (Error | Exception e4) {
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
						|| angledArmor.containsKey(MaterialStorage.getMS(e.getItem().getType(),
								(int) (e.getItem().getDurability() + 1), -1, "-1", "-1"))
						|| expansionPacks.contains(MaterialStorage.getMS(e.getItem().getType(),
								(int) (e.getItem().getDurability() + 1), -1, "-1", "-1"))) {
					Main.DEBUG("A player is using a non-gun item, but may reach the textures of one!");
					// If the item is not a gun, but the item below it is
					int safeDurib = findSafeSpot(e.getItem(), true);

					// if (e.getItem().getDurability() == 1) {
					Main.DEBUG("Safe Durib= " + (safeDurib + 6) + "! ORG " + e.getItem().getDurability());
					ItemStack is = e.getItem();
					is.setDurability((short) (safeDurib + 6));
					e.getPlayer().getInventory().setItem(e.getPlayer().getInventory().getHeldItemSlot(), is);
					// }
				}
				return;
			}
			try {
				if (!e.getItem().getItemMeta().isUnbreakable()) {
					Main.DEBUG("A player is using a breakable item that reached being a gun!");
					// If the item is not a gun, but the item below it is
					int safeDurib = findSafeSpot(e.getItem(), true);

					// if (e.getItem().getDurability() == 1) {
					Main.DEBUG("Safe Durib= " + (safeDurib + 6) + "! ORG " + e.getItem().getDurability());
					ItemStack is = e.getItem();
					is.setDurability((short) (safeDurib + 6));
					e.getPlayer().getInventory().setItem(e.getPlayer().getInventory().getHeldItemSlot(), is);
					return;
				}

			} catch (Error | Exception e45) {
			}
			/*
			 * if (e.getItem().getEnchantments().containsKey(Enchantment.MENDING)) { int
			 * safeDurib = findSafeSpot(e.getItem(), false);
			 * Main.DEBUG("Safe Durib with mending= " + (safeDurib + 4) + "! ORG " +
			 * e.getItem().getDurability()); ItemStack is = e.getItem();
			 * is.setDurability((short) Math.max(0, safeDurib - 1));
			 * e.getPlayer().getInventory().setItem(e.getPlayer().getInventory().
			 * getHeldItemSlot(), is); return; }
			 */
			final ItemStack origin = e.getItem();
			final int slot = e.getPlayer().getInventory().getHeldItemSlot();
			if (!isVersionHigherThan(1, 10)) {
				ItemStack temp1 = null;
				try {
					temp1 = e.getPlayer().getInventory().getItemInOffHand();
				} catch (Error | Exception re453) {
				}
				final ItemStack temp2 = temp1;

				new BukkitRunnable() {
					@Override
					public void run() {
						if (origin.getDurability() != e.getPlayer().getItemInHand().getDurability()
								&& slot == e.getPlayer().getInventory().getHeldItemSlot()
								&& (e.getPlayer().getItemInHand() != null
										&& e.getPlayer().getItemInHand().getType() == origin.getType())) {
							try {
								if (isIS(e.getPlayer().getItemInHand()) && origin.getDurability() == e.getPlayer()
										.getInventory().getItemInOffHand().getDurability())
									return;
								if (temp2 != null
										&& temp2.getDurability() == e.getPlayer().getItemInHand().getDurability())
									return;
							} catch (Error | Exception re54) {
							}
							e.getPlayer().setItemInHand(origin);
							DEBUG("The item in the player's hand changed! Origin " + origin.getDurability() + " New "
									+ e.getPlayer().getItemInHand().getDurability());
						}

					}
				}.runTaskLater(this, 1);
			}

			ItemStack usedItem = e.getPlayer().getItemInHand();

			try {
				if (AutoDetectResourcepackVersion) {
					if (us.myles.ViaVersion.bukkit.util.ProtocolSupportUtil.getProtocolVersion(e.getPlayer()) < ID18) {
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

			/*
			 * try {
			 * 
			 * if (us.myles.ViaVersion.bukkit.util.ProtocolSupportUtil.getProtocolVersion(e.
			 * getPlayer()) > ID18) { if (isIS(usedItem)) { try { usedItem =
			 * e.getPlayer().getInventory().getItemInOffHand(); } catch (Error | Exception
			 * e4) { } } if (getCustomItem(usedItem).is18Support()) { return; } } } catch
			 * (Error | Exception e3) { }
			 */

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
				sendResourcepack(e.getPlayer(), true);
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
				if (!e.getPlayer().hasPermission("qualityarmory.usegun")) {
					e.getPlayer().sendMessage(Main.S_NOPERM);
					return;
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

			if (isGun(usedItem) || isIS(usedItem))
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
									if (enableIronSightsON_RIGHT_CLICK) {
										e.getPlayer().getInventory()
												.setItemInMainHand(e.getPlayer().getInventory().getItemInOffHand());
										e.getPlayer().getInventory().setItemInOffHand(null);
										Main.DEBUG("Swapping gun from offhand to main hand!");
									} else {
										Main.DEBUG("Swapping \"usedItem\" to offhand!");
									}
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
								|| e.getClickedBlock().getType() == MultiVersionLookup.getMycil()))
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
						// if (allowGunsInRegion(e.getPlayer().getLocation())) {
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

						sendHotbarGunAmmoCount(e.getPlayer(), g, attachment, usedItem, false);
						return;
						/*
						 * } else { Main.DEBUG("Worldguard region canceled the event"); }
						 */
						// sendHotbarGunAmmoCount(e.getPlayer(), g, attachment, usedItem, false);
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
										.contains(S_RELOADING_MESSAGE)) {
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
									ItemStack ironsights = new ItemStack(IronSightsToggleItem.getMat(), 1,
											(short) IronSightsToggleItem.getData());
									ItemMeta im = ironsights.getItemMeta();
									im.setDisplayName(IronSightsToggleItem.getItemName());
									im.setUnbreakable(true);
									im.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_UNBREAKABLE);
									ironsights.setItemMeta(im);
									e.getPlayer().getInventory().setItemInMainHand(ironsights);
								}

								sendHotbarGunAmmoCount(e.getPlayer(), g, attachment, usedItem, false);
							} catch (Error e2) {
								Bukkit.broadcastMessage(prefix
										+ "Ironsights not compatible for versions lower than 1.8. The server owner should set EnableIronSights to false in the plugin's config");
							}
						} else {
							if (!enableDurability || ItemFact.getDamage(usedItem) > 0) {
								// if (allowGunsInRegion(e.getPlayer().getLocation())) {
								g.shoot(e.getPlayer(), attachment);
								if (enableDurability)
									if (offhand) {
										e.getPlayer().getInventory().setItemInOffHand(ItemFact.damage(g, usedItem));
									} else {
										e.getPlayer().setItemInHand(ItemFact.damage(g, usedItem));
									}
								// }
								sendHotbarGunAmmoCount(e.getPlayer(), g, attachment, usedItem, false);
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
							if (allowGunReload) {
								sendHotbarGunAmmoCount(e.getPlayer(), g, attachment, usedItem,
										((g.getMaxBullets() != ItemFact.getAmount(usedItem))
												&& GunUtil.hasAmmo(e.getPlayer(), g)));
								if (g.playerHasAmmo(e.getPlayer())) {
									Main.DEBUG("Trying to reload. player has ammo");
									g.reload(e.getPlayer(), attachment);

								} else {
									Main.DEBUG("Trying to reload. player DOES NOT have ammo");
								}
							}

						}
					}
				}
				Main.DEBUG("Reached end for gun-check!");
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPickUp(PlayerExpChangeEvent e) {
		if (isCustomItem(e.getPlayer().getItemInHand(), -Math.min(10, e.getAmount()))) {
			if (e.getPlayer().getItemInHand().containsEnchantment(Enchantment.MENDING)) {
				ItemStack temp = e.getPlayer().getItemInHand();
				int j = findSafeSpot(temp, false);
				temp.setDurability((short) Math.max(0, j - 1));
				e.getPlayer().setItemInHand(temp);
			}
		}
		try {

			if (isCustomItem(e.getPlayer().getInventory().getItemInOffHand(), -Math.min(10, e.getAmount()))) {
				if (e.getPlayer().getInventory().getItemInOffHand().containsEnchantment(Enchantment.MENDING)) {
					ItemStack temp = e.getPlayer().getInventory().getItemInOffHand();
					int j = findSafeSpot(temp, false);
					temp.setDurability((short) Math.max(0, j - 1));
					e.getPlayer().getInventory().setItemInOffHand(temp);
				}
			}
		} catch (Error | Exception e45) {
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
				Bukkit.broadcastMessage(prefix + ChatColor.RED + " Disabling resourcepack.");
			}
		}
		/*
		 * if (guntype == Material.DIAMOND_HOE && !hideTextureWarnings) {
		 * Bukkit.broadcastMessage(prefix +
		 * " QA is now moving all items to a new Diamond_Axe system to prevent conflicts with other plugins."
		 * ); Bukkit.broadcastMessage(prefix +
		 * " Please delete the \"gunMaterialType\"value in the config or hide these warnings by setting \"hideTextureWarnings\" to true."
		 * ); }
		 */
		new BukkitRunnable() {

			@Override
			public void run() {
				if (e.getPlayer().getScoreboard() != null
						&& !coloredGunScoreboard.contains(e.getPlayer().getScoreboard())) {
					coloredGunScoreboard.add(registerGlowTeams(e.getPlayer().getScoreboard()));
				}
			}
		}.runTaskLater(this, 20 * 15);

		if (sendOnJoin) {
			sendResourcepack(e.getPlayer(), sendTitleOnJoin);
		} else {
			for (ItemStack i : e.getPlayer().getInventory().getContents()) {
				if (i != null && (isGun(i) || isAmmo(i) || isMisc(i))) {
					if (shouldSend && !resourcepackReq.contains(e.getPlayer().getUniqueId())) {
						new BukkitRunnable() {
							@Override
							public void run() {
								sendResourcepack(e.getPlayer(), false);
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
			if ((isGun(newone) || isGunWithAttchments(newone))/* newone.getType() == guntype */
					&& newone.getItemMeta().hasDisplayName()
					&& newone.getItemMeta().getDisplayName().contains(S_RELOADING_MESSAGE)) {
				ItemMeta im = newone.getItemMeta();
				if (isGun(newone)) {
					im.setDisplayName(gunRegister.get((int) newone.getDurability()).getDisplayName());
				} else if (isGunWithAttchments(newone))
					im.setDisplayName(attachmentRegister.get((int) newone.getDurability()).getDisplayName());
				newone.setItemMeta(im);
				e.getItemDrop().setItemStack(newone);
			}
			return;
		}

		if (isGun(e.getItemDrop().getItemStack()) || isGunWithAttchments(e.getItemDrop().getItemStack())) {
			Gun g = getGun(e.getItemDrop().getItemStack());
			if (g == null)
				g = gunRegister.get(getGunWithAttchments(e.getItemDrop().getItemStack()).getBase());
			if (isDuplicateGun(e.getItemDrop().getItemStack(), e.getPlayer())) {
				e.setCancelled(true);
				return;
			}
			if ((e.getItemDrop().getItemStack().getItemMeta().hasDisplayName()
					&& e.getItemDrop().getItemStack().getItemMeta().getDisplayName().contains(S_RELOADING_MESSAGE))) {
				if (!reloadingTasks.containsKey(e.getPlayer().getUniqueId())) {
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

			if (g.getGlow() != null) {
				for (Scoreboard s : coloredGunScoreboard)
					if (s.getTeam("QA_" + g.getGlow().name() + "") != null)
						s.getTeam("QA_" + g.getGlow().name() + "").addEntry(e.getItemDrop().getUniqueId().toString());
				e.getItemDrop().setGlowing(true);
			}
		}
		checkforDups(e.getPlayer(), e.getItemDrop().getItemStack());
		// This should prevent the everything below from being called.

		if (enableIronSightsON_RIGHT_CLICK) {
			if (e.getPlayer().getItemInHand() != null && (isGun(e.getItemDrop().getItemStack())
					|| isGunWithAttchments(e.getItemDrop().getItemStack()) || isIS(e.getItemDrop().getItemStack()))) {
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
						Gun g = getGun(e.getItemDrop().getItemStack());
						if (g == null) {
							g = gunRegister.get(getGunWithAttchments(e.getItemDrop().getItemStack()).getBase());
						}
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
								final Gun gk = g;
								new BukkitRunnable() {
									@Override
									public void run() {
										GunUtil.basicReload(gk, getGunWithAttchments(e.getItemDrop().getItemStack()),
												e.getPlayer(), gk.hasUnlimitedAmmo());
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
	public void sendResourcepack(final Player player, final boolean warning) {
		new BukkitRunnable() {
			public void run() {
				if (namesToBypass.contains(player.getName())) {
					Main.resourcepackReq.add(player.getUniqueId());
					return;
				}
				if (warning)
					try {
						player.sendTitle(ChatColor.RED + S_NORES1, S_NORES2);
					} catch (Error e2) {
						player.sendMessage(ChatColor.RED + S_NORES1);
						player.sendMessage(ChatColor.RED + S_NORES2);
					}
				player.sendMessage(prefix + S_RESOURCEPACK_HELP);
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

							if (!isVersionHigherThan(1, 9)) {
								resourcepackReq.add(player.getUniqueId());
								sentResourcepack.put(player.getUniqueId(), System.currentTimeMillis());
							}
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
		return MaterialStorage.getMS(Material.DIAMOND_AXE, d, 0);
	}

	public static boolean isCustomItem(ItemStack is) {
		return isCustomItem(is, 0);
	}

	public static ArmoryBaseObject getCustomItem(ItemStack is) {
		if (isGun(is))
			return getGun(is);
		if (isAmmo(is))
			return getAmmo(is);
		if (isArmor(is))
			return getArmor(is);
		if (isMisc(is))
			return getMisc(is);
		if (isAngledArmor(is))
			return armorRegister.get(getAngledArmor(is).getBase());
		if (isGunWithAttchments(is))
			return gunRegister.get(getGunWithAttchments(is).getBase());
		return null;
	}

	public static boolean isCustomItem(ItemStack is, int dataOffset) {
		ItemStack itemstack = is.clone();
		itemstack.setDurability((short) (is.getDurability() + dataOffset));
		return isArmor(itemstack) || isGunWithAttchments(itemstack) || isAmmo(itemstack) || isMisc(itemstack)
				|| isGun(itemstack) || isIS(itemstack) || isAngledArmor(itemstack)
				|| expansionPacks.contains(MaterialStorage.getMS(is));
	}

	public static boolean isArmor(ItemStack is) {
		if (is == null)
			return false;
		int var = MaterialStorage.getVarient(is);
		if (isAngledArmor(is))
			return true;
		return (is != null
				&& (armorRegister.containsKey(MaterialStorage.getMS(is.getType(), (int) is.getDurability(), var))
						|| armorRegister.containsKey(MaterialStorage.getMS(is.getType(), -1, var))));
	}

	public static boolean isAngledArmor(ItemStack is) {
		if (is == null)
			return false;
		int var = MaterialStorage.getVarient(is);
		if (angledArmor.containsKey(MaterialStorage.getMS(is.getType(), (int) is.getDurability(), var)))
			return true;
		return false;
	}

	public static AngledArmor getAngledArmor(ItemStack is) {
		int var = MaterialStorage.getVarient(is);
		if (angledArmor.containsKey(MaterialStorage.getMS(is.getType(), (int) is.getDurability(), var)))
			return angledArmor.get(MaterialStorage.getMS(is.getType(), (int) is.getDurability(), var));
		return angledArmor.get(MaterialStorage.getMS(is.getType(), -1, var));

	}

	public static ArmorObject getArmor(ItemStack is) {
		int var = MaterialStorage.getVarient(is);
		if (isArmor(is)) {
			try {
				AngledArmor ms = getAngledArmor(is);
				return armorRegister.get(ms.getBase());
			} catch (Error | Exception e4) {
			}
		}
		if (armorRegister.containsKey(MaterialStorage.getMS(is.getType(), (int) is.getDurability(), var)))
			return armorRegister.get(MaterialStorage.getMS(is.getType(), (int) is.getDurability(), var));
		return armorRegister.get(MaterialStorage.getMS(is.getType(), -1, var));
	}

	public static boolean isMisc(ItemStack is) {
		if (is == null)
			return false;
		int var = MaterialStorage.getVarient(is);
		return (is != null
				&& (miscRegister.containsKey(MaterialStorage.getMS(is.getType(), (int) is.getDurability(), var))
						|| miscRegister.containsKey(MaterialStorage.getMS(is.getType(), -1, var))));
	}

	public static ArmoryBaseObject getMisc(ItemStack is) {
		int var = MaterialStorage.getVarient(is);
		if (miscRegister.containsKey(MaterialStorage.getMS(is.getType(), (int) is.getDurability(), var)))
			return miscRegister.get(MaterialStorage.getMS(is.getType(), (int) is.getDurability(), var));
		return miscRegister.get(MaterialStorage.getMS(is.getType(), -1, var));
	}

	public static Gun getGun(ItemStack is) {
		int var = MaterialStorage.getVarient(is);
		if (gunRegister.containsKey(MaterialStorage.getMS(is.getType(), (int) is.getDurability(), var)))
			return gunRegister.get(MaterialStorage.getMS(is.getType(), (int) is.getDurability(), var));
		return gunRegister.get(MaterialStorage.getMS(is.getType(), -1, var));
	}

	public static boolean isGun(ItemStack is) {
		if (is == null)
			return false;
		int var = MaterialStorage.getVarient(is);
		return (is != null
				&& (gunRegister.containsKey(MaterialStorage.getMS(is.getType(), (int) is.getDurability(), var))
						|| gunRegister.containsKey(MaterialStorage.getMS(is.getType(), -1, var))));
	}

	public static AttachmentBase getGunWithAttchments(ItemStack is) {
		int var = MaterialStorage.getVarient(is);
		if (attachmentRegister.containsKey(MaterialStorage.getMS(is.getType(), (int) is.getDurability(), var)))
			return attachmentRegister.get(MaterialStorage.getMS(is.getType(), (int) is.getDurability(), var));
		return attachmentRegister.get(MaterialStorage.getMS(is.getType(), -1, var));
	}

	public static boolean isGunWithAttchments(ItemStack is) {
		if (is == null)
			return false;
		int var = MaterialStorage.getVarient(is);
		return (is != null
				&& (attachmentRegister.containsKey(MaterialStorage.getMS(is.getType(), (int) is.getDurability(), var))
						|| attachmentRegister.containsKey(MaterialStorage.getMS(is.getType(), -1, var))));
	}

	public static Ammo getAmmo(ItemStack is) {
		int var = MaterialStorage.getVarient(is);
		@SuppressWarnings("deprecation")
		String extraData = is.getType() == MultiVersionLookup.getSkull()? ((SkullMeta) is.getItemMeta()).getOwner() : null;
		String temp = SkullHandler.getURL64(is);
		if (ammoRegister
				.containsKey(MaterialStorage.getMS(is.getType(), (int) is.getDurability(), var, extraData, temp)))
			return ammoRegister
					.get(MaterialStorage.getMS(is.getType(), (int) is.getDurability(), var, extraData, temp));
		return ammoRegister.get(MaterialStorage.getMS(is.getType(), -1, var, extraData, temp));
	}

	public static boolean isAmmo(ItemStack is) {
		if (is == null)
			return false;
		int var = MaterialStorage.getVarient(is);
		@SuppressWarnings("deprecation")
		String extraData = is.getType() == MultiVersionLookup.getSkull() ? ((SkullMeta) is.getItemMeta()).getOwner() : null;
		String temp = null;
		try {
			temp = SkullHandler.getURL64(is);
		} catch (Error | Exception e4) {
		}
		boolean k = (is != null && (ammoRegister
				.containsKey(MaterialStorage.getMS(is.getType(), (int) is.getDurability(), var, extraData, temp))
				|| ammoRegister.containsKey(MaterialStorage.getMS(is.getType(), -1, var, extraData, temp))));
		return k;
	}

	public static boolean isIS(ItemStack is) {
		if (is == null)
			return false;
		if (is != null && is.getType() == IronSightsToggleItem.getMat()
				&& is.getDurability() == (int) IronSightsToggleItem.getData())
			return true;
		return false;
	}

	public static Gun getGunByName(String name) {
		for (Gun g : gunRegister.values()) {
			if (g.getName().equals(name))
				return g;
		}
		return null;
	}

	public static void sendHotbarGunAmmoCount(Player p, Gun g, AttachmentBase attachmentBase, ItemStack usedItem,
			boolean reloading) {
		try {
			String message = S_HOTBAR_FORMAT;
			if (message.contains("%name%"))
				message = message.replace("%name%",
						(attachmentBase != null ? attachmentBase.getDisplayName() : g.getDisplayName()));
			if (message.contains("%amount%"))
				message = message.replace("%amount%", ItemFact.getAmount(usedItem) + "");
			if (message.contains("%max%"))
				message = message.replace("%max%", g.getMaxBullets() + "");

			if (message.contains("%state%"))
				message = message.replace("%state%", reloading ? S_RELOADING_MESSAGE
						: AmmoUtil.getAmmoAmount(p, g.getAmmoType()) <= 0 ? S_OUT_OF_AMMO : S_MAX_FOUND);
			if (message.contains("%total%"))
				message = message.replace("%total%", "" + AmmoUtil.getAmmoAmount(p, g.getAmmoType()));

			// (attachmentBase != null ? attachmentBase.getDisplayName() :
			// g.getDisplayName()) + " = "
			// + ItemFact.getAmount(usedItem) + "/" + (g.getMaxBullets()) + "";
			if (unknownTranslationKeyFixer) {
				message = ChatColor.stripColor(message);
			} else {
				message = ChatColor.translateAlternateColorCodes('&', message);
			}
			HotbarMessager.sendHotBarMessage(p, message);
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

	public static int findSafeSpot(ItemStack newItem, boolean findHighest) {
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
		for (MaterialStorage j : attachmentRegister.keySet())
			if (j.getMat() == newItem.getType() && (j.getData() > safeDurib) == findHighest)
				safeDurib = j.getData();
		for (MaterialStorage j : angledArmor.keySet())
			if (j.getMat() == newItem.getType() && (j.getData() > safeDurib) == findHighest)
				safeDurib = j.getData();
		for (MaterialStorage j : expansionPacks)
			if (j.getMat() == newItem.getType() && (j.getData() > safeDurib) == findHighest)
				safeDurib = j.getData();
		return safeDurib;
	}

	public static int getMaxPages() {
		return (armorRegister.size() + ammoRegister.size() + miscRegister.size() + gunRegister.size()) / (9 * 5);
	}

	public boolean isOverLimitForGun(Gun g, Player p) {
		int count = 0;
		for (ItemStack is : p.getInventory().getContents()) {
			if (is != null && isGun(is)) {
				Gun g2 = getGun(is);
				if (g2.isPrimaryWeapon() == g.isPrimaryWeapon())
					count++;
			}
		}
		return count >= (g.isPrimaryWeapon() ? primaryWeaponLimit : secondaryWeaponLimit);
	}
}
