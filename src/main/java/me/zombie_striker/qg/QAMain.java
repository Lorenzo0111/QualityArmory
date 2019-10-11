package me.zombie_striker.qg;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;

import me.zombie_striker.customitemmanager.CustomItemManager;
import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.customitemmanager.OLD_ItemFact;
import me.zombie_striker.qg.ammo.*;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.armor.*;
import me.zombie_striker.qg.attachments.AttachmentBase;
import me.zombie_striker.qg.config.*;
import me.zombie_striker.qg.guns.*;
import me.zombie_striker.qg.guns.projectiles.ExplodingRoundProjectile;
import me.zombie_striker.qg.guns.projectiles.FireProjectile;
import me.zombie_striker.qg.guns.projectiles.HomingRocketProjectile;
import me.zombie_striker.qg.guns.projectiles.MiniNukeProjectile;
import me.zombie_striker.qg.guns.projectiles.RocketProjectile;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.guns.utils.WeaponType;
import me.zombie_striker.qg.handlers.*;
import me.zombie_striker.qg.handlers.chargers.*;
import me.zombie_striker.qg.handlers.reloaders.*;
import me.zombie_striker.qg.listener.QAListener;
import me.zombie_striker.qg.miscitems.*;
import me.zombie_striker.qg.miscitems.ThrowableItems.ThrowableHolder;
import me.zombie_striker.qg.npcs.Gunner;
import me.zombie_striker.qg.npcs.GunnerTrait;
import me.zombie_striker.qg.npcs_sentinel.SentinelQAHandler;

import org.bukkit.*;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
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

public class QAMain extends JavaPlugin {

	public static final int ViaVersionIdfor_1_8 = 106;
	private static final String SERVER_VERSION;
	// Chris: change to LinkedHashMap let the Items can sort by FileName.
	public static HashMap<MaterialStorage, Gun> gunRegister = new LinkedHashMap<>();
	public static HashMap<MaterialStorage, Ammo> ammoRegister = new LinkedHashMap<>();
	public static HashMap<MaterialStorage, ArmoryBaseObject> miscRegister = new LinkedHashMap<>();
	public static HashMap<MaterialStorage, ArmorObject> armorRegister = new LinkedHashMap<>();


	public static HashMap<String, String> craftingEntityNames = new HashMap<>();

	public static Set<EntityType> avoidTypes = new HashSet<>();
	public static HashMap<UUID, Location> recoilHelperMovedLocation = new HashMap<>();
	public static ArrayList<MaterialStorage> expansionPacks = new ArrayList<>();
	public static HashMap<UUID, List<BukkitTask>> reloadingTasks = new HashMap<>();
	public static HashMap<UUID, Long> sentResourcepack = new HashMap<>();
	public static ArrayList<UUID> resourcepackReq = new ArrayList<>();
	public static List<Gunner> gunners = new ArrayList<>();
	public static List<String> namesToBypass = new ArrayList<>();
	public static List<Material> interactableBlocks = new ArrayList<>();
	public static List<Material> destructableBlocks = new ArrayList<Material>();
	public static boolean enableInteractChests = false;
	public static boolean DEBUG = false;
	public static Object bulletTrail;

	public static boolean shouldSend = true;
	public static boolean sendOnJoin = false;
	public static boolean sendTitleOnJoin = false;
	public static double secondsTilSend = 0.0;

	public static boolean orderShopByPrice = false;
	public static boolean ignoreUnbreaking = false;
	public static boolean ignoreSkipping = false;
	public static boolean verboseLoadingLogging = false;
	public static boolean enableDurability = false;
	public static boolean enableArmorIgnore = false;
	public static boolean showCrashMessage = true;
	public static boolean enableRecoil = true;
	public static double bulletStep = 0.10;
	public static double gravity = 0.05;
	public static double swayModifier_Ironsights = 0.8;
	public static double swayModifier_Sneak = 0.7;
	public static double swayModifier_Walk = 1.5;
	public static double swayModifier_Run = 2;
	public static boolean blockbullet_leaves = false;
	public static boolean blockbullet_halfslabs = false;
	public static boolean blockbullet_door = false;
	public static boolean blockbullet_water = false;
	public static boolean blockbullet_glass = false;
	public static boolean overrideAnvil = false;
	public static boolean supportWorldGuard = false;
	public static boolean enableIronSightsON_RIGHT_CLICK = false;
	public static boolean enableSwapSingleShotOnAim = false;
	public static boolean enableBulletTrails = true;
	public static boolean reloadOnF = true;
	public static boolean reloadOnFOnly = true;
	public static boolean disableHotBarMessageOnShoot = false;
	public static boolean disableHotBarMessageOnReload = false;
	public static boolean disableHotBarMessageOnOutOfAmmo = false;
	public static boolean enableExplosionDamage = false;
	public static boolean enableExplosionDamageDrop = false;
	public static boolean hideTextureWarnings = false;
	public static boolean enableEconomy = false;
	public static boolean allowGunReload = true;
	public static boolean enableBleeding = false;
	public static double bulletWound_initialbloodamount = 1500;
	public static double bulletWound_BloodIncreasePerSecond = 0.01;
	public static double bulletWound_MedkitBloodlossHealRate = 0.05;
	public static String headshot_sound = WeaponSounds.XP_ORG_PICKUP.getSoundName();
	public static boolean HeadshotOneHit = true;
	public static boolean headshotPling = true;
	public static boolean headshotGoreSounds = true;
	public static boolean showOutOfAmmoOnItem = false;
	public static boolean showOutOfAmmoOnTitle = false;
	public static boolean showReloadOnTitle = false;
	public static boolean addGlowEffects = false;
	public static boolean enableReloadWhenOutOfAmmo = false;
	public static boolean overrideURL = false;
	public static boolean kickIfDeniedRequest = false;
	public static String S_NOPERM = "&c You do not have permission to do that.";

	public static String S_NORES1 = " &c&l Downloading Resourcepack...";
	public static String S_NORES2 = " &f Accept the resourcepack to see the custom items";
	public static String S_ANVIL = " &a You do not have permission to use this armory bench. Shift+Click to access anvil.";
	public static String S_ITEM_BULLETS = "&aBullets";
	public static String S_ITEM_DURIB = "Durability";
	public static String S_ITEM_DAMAGE = "&aDamage";
	public static String S_ITEM_AMMO = "&aAmmo";
	public static String S_ITEM_ING = "Ingredients";
	public static String S_ITEM_VARIANTS_LEGACY = "&7Varient:";
	public static String S_ITEM_VARIANTS_NEW = "&7Varient:";
	public static String S_ITEM_DPS = "&2DPS";
	public static String S_ITEM_COST = "&" + ChatColor.GOLD.getChar() + "Price: ";

	// Chris: add message
	public static String S_ITEM_CRAFTS = "Crafts";
	public static String S_ITEM_RETURNS = "Returns";

	public static String S_KICKED_FOR_RESOURCEPACK = "&c You have been kicked because you did not accept the resourcepack. \n&f If you want to rejoin the server, edit the server entry and set \"Resourcepack Prompts\" to \"Accept\" or \"Prompt\"'";
	public static String S_LMB_SINGLE = ChatColor.DARK_GRAY + "[LMB] to use Single-fire mode";
	public static String S_LMB_FULLAUTO = ChatColor.DARK_GRAY + "[Sneak]+[LMB] to use Automatic-fire";
	public static String S_RMB_RELOAD = ChatColor.DARK_GRAY + "[RMB] to reload";
	public static String S_RMB_R1 = ChatColor.DARK_GRAY + "[DropItem] to reload";
	public static String S_RMB_R2 = ChatColor.DARK_GRAY + "[RMB] to reload";
	public static String S_RMB_A1 = ChatColor.DARK_GRAY + "[RMB] to open ironsights";
	public static String S_RMB_A2 = ChatColor.DARK_GRAY + "[Sneak] to open ironsights";
	public static String S_HELMET_RMB = ChatColor.DARK_GRAY + "[RMB] to equip helmet.";
	public static String S_FULLYHEALED = "&fYou are fully healed. No need for this right now!";
	public static String S_MEDKIT_HEALING = "Healing";
	public static String S_MEDKIT_BLEEDING = "Blood-Loss Rate:";
	public static double S_MEDKIT_HEALDELAY = 6;
	public static double S_MEDKIT_HEAL_AMOUNT = 1;
	public static String S_MEDKIT_LORE_INFO = "&f[RMB] to heal yourself!";
	public static String S_BULLETPROOFSTOPPEDBLEEDING = "&aYour Kevlar vest protected you from that bullet!";
	public static String S_BLEEDOUT_STARTBLEEDING = "&cYOU ARE BLEEDING! FIND A MEDKIT TO STOP THE BLEEDING!";
	public static String S_BLEEDOUT_LOSINGconscious = "&cYOU ARE LOSING CONSCIOUSNESS DUE TO BLOODLOSS!";
	public static String S_BUYCONFIRM = "&aYour have purchased %item% for $%cost%!";
	public static String S_OUT_OF_AMMO = "[Out of Ammo]";
	public static String S_RELOADING_MESSAGE = "[Reloading...]";
	public static String S_MAX_FOUND = "[%total%]";
	public static String S_HOTBAR_FORMAT = "%name% = %amount%/%max% %state%";
	public static String S_RESOURCEPACK_HELP = "In case the resourcepack crashes your client, reject the request and use /qa getResourcepack to get the resourcepack.";
	public static String S_RESOURCEPACK_DOWNLOAD = "Download this resourcepack and enable it to see the custom items (Note that it may take some time to load)";
	public static String S_RESOURCEPACK_BYPASS = "By issuing this command, you are now added to a whitelist for the resourcepack. You should no longer see the prompt";
	public static String S_RESOURCEPACK_OPTIN = "By issuing this command, you have been removed from the whitelist. You will now recieve the resourcepack prompt";
	public static String S_GRENADE_PULLPIN = " Pull the pin first...";
	public static String S_GRENADE_PALREADYPULLPIN = "You already pulled the pin!";
	public static boolean enableCrafting = true;
	public static boolean enableShop = true;
	public static double smokeSpacing = 0.5;
	public static boolean enablePrimaryWeaponHandler = false;
	public static int primaryWeaponLimit = 2;
	public static int secondaryWeaponLimit = 2;
	public static String prefix = ChatColor.GRAY + "[" + ChatColor.DARK_GREEN + "QualityArmory" + ChatColor.GRAY + "]"
			+ ChatColor.WHITE;
	// public Inventory craftingMenu;
	public static String S_craftingBenchName = "Armory Crafting-Bench Page:";
	public static String S_missingIngredients = "You do not have all the materials needed to craft this.";
	// public Inventory shopMenu;
	public static String S_shopName = "Weapons Shop Page:";
	public static String S_noMoney = "You do not have enough money to buy this.";
	public static String S_noEcon = "ECONOMY NOT ENABLED. REPORT THIS TO THE OWNER!";
	public static ItemStack prevButton;
	public static ItemStack nextButton;
	public static MessagesYML m;
	public static MessagesYML resourcepackwhitelist;
	public static String language = "en";
	public static boolean hasParties = false;
	public static boolean friendlyFire = false;
	public static boolean hasProtocolLib = false;
	public static boolean hasViaVersion = false;
	public static boolean hasViaRewind = false;
	public static boolean AUTOUPDATE = true;
	public static boolean SWAP_RMB_WITH_LMB = true;
	public static boolean ENABLE_LORE_INFO = true;
	public static boolean ENABLE_LORE_HELP = true;
	public static boolean AutoDetectResourcepackVersion = true;
	public static boolean ITEM_enableUnbreakable = true;// TODO :stuufff
	public static boolean MANUALLYSELECT18 = false;
	public static boolean MANUALLYSELECT113 = false;
	public static boolean unknownTranslationKeyFixer = false;
	public static boolean enableCreationOfFiles = true;
	public static List<Scoreboard> coloredGunScoreboard = new ArrayList<Scoreboard>();
	public static boolean blockBreakTexture = false;
	public static List<UUID> currentlyScoping = new ArrayList<>();
	private static QAMain main;

	static {
		String name = Bukkit.getServer().getClass().getName();
		name = name.substring(name.indexOf("craftbukkit.") + "craftbukkit.".length());
		name = name.substring(0, name.indexOf("."));
		SERVER_VERSION = name;
	}

	private TreeFellerHandler tfh = null;
	private FileConfiguration config;
	private File configFile;
	private boolean saveTheConfig = false;

	public static QAMain getInstance() {
		return main;
	}

	public static boolean isVersionHigherThan(int mainVersion, int secondVersion) {
		if (secondVersion >= 9 && hasViaRewind)
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

	public static void toggleNightvision(Player player, Gun g, boolean add) {
		if (add) {
			if (g.getZoomWhenIronSights() > 0) {
				currentlyScoping.add(player.getUniqueId());
				player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1200, g.getZoomWhenIronSights()));
			}
			if (g.hasnightVision()) {
				currentlyScoping.add(player.getUniqueId());
				player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1200, 3));
			}
		} else {
			if (currentlyScoping.contains(player.getUniqueId())) {
				if (player.hasPotionEffect(PotionEffectType.SLOW) && (g == null || g.getZoomWhenIronSights() > 0))
					player.removePotionEffect(PotionEffectType.SLOW);
				boolean potionEff = false;
				try {
					potionEff = player.hasPotionEffect(PotionEffectType.NIGHT_VISION)
							&& (g == null || g.hasnightVision())
							&& player.getPotionEffect(PotionEffectType.NIGHT_VISION).getAmplifier() == 3;
				} catch (Error | Exception e3452) {
					for (PotionEffect pe : player.getActivePotionEffects())
						if (pe.getType() == PotionEffectType.NIGHT_VISION)
							potionEff = (g == null || g.hasnightVision()) && pe.getAmplifier() == 3;
				}
				if (potionEff)
					player.removePotionEffect(PotionEffectType.NIGHT_VISION);
				currentlyScoping.remove(player.getUniqueId());
			}
		}

	}

	public static void DEBUG(String message) {
		if (DEBUG)
			Bukkit.broadcast(message, "qualityarmory.debugmessages");
	}

	public static Scoreboard registerGlowTeams(Scoreboard sb) {
		if (sb.getTeam("QA_RED") == null) {
			for (ChatColor c : ChatColor.values()) {
				if (sb.getTeam("QA_" + c.name() + "") == null)
					sb.registerNewTeam("QA_" + c.name() + "").setPrefix(c + "");
			}
		}
		return sb;
	}

	public static void shopsSounds(InventoryClickEvent e, boolean shop) {

		if (shop) {
			try {
				((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(), Sound.BLOCK_ANVIL_USE, 0.7f, 1);
			} catch (Error e2) {
				((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(), Sound.valueOf("ANVIL_USE"),
						0.7f, 1);
			}
		} else {
			try {
				((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(), MultiVersionLookup.getHarp(),
						0.7f, 1);
			} catch (Error e2) {
				((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(), Sound.valueOf("NOTE_PIANO"),
						0.7f, 1);
			}
		}
	}

	public static boolean lookForIngre(Player player, ArmoryBaseObject a) {
		return lookForIngre(player, a.getIngredients());
	}

	@SuppressWarnings("deprecation")
	public static boolean lookForIngre(Player player, ItemStack[] ings) {
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

	public static boolean removeForIngre(Player player, ArmoryBaseObject a) {
		return removeForIngre(player, a.getIngredients());
	}

	@SuppressWarnings("deprecation")
	public static boolean removeForIngre(Player player, ItemStack[] ings) {
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

	public static void checkforDups(Player p, ItemStack... curr) {
		for (ItemStack curs : curr)
			for (int i = 0; i < p.getInventory().getSize(); i++) {
				ItemStack cont = p.getInventory().getItem(i);
				if (cont != null)
					if (curs != null && OLD_ItemFact.sameGun(cont, curs))
						if (!cont.equals(curs))
							p.getInventory().setItem(i, null);

			}
	}

	public static MaterialStorage m(int d) {
		return MaterialStorage.getMS(Material.DIAMOND_AXE, d, 0);
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
				if (shopping && g.cost() < 0)
					continue;
				if (basei < index) {
					basei++;
					continue;
				}
				basei++;
				if (index >= maxIndex)
					break;
				index++;
				try {
					ItemStack is = CustomItemManager.getItemFact("gun").getItem(g.getItemData(), 1);
					ItemMeta im = is.getItemMeta();
					List<String> lore = OLD_ItemFact.getCraftingGunLore(g);
					im.setLore(lore);
					is.setItemMeta(im);
					// if (enableVisibleAmounts)
					// is.setAmount(g.getCraftingReturn());
					if (shopping)
						is = OLD_ItemFact.addShopLore(g, is.clone());
					shopMenu.addItem(is);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		if (basei + gunslistr.size() < index)
			basei += gunslistr.size();
		else
			for (ArmoryBaseObject abo : miscRegister.values()) {
				if (shopping && abo.cost() < 0)
					continue;
				if (basei < index) {
					basei++;
					continue;
				}
				basei++;
				if (index >= maxIndex)
					break;
				index++;
				try {
					ItemStack is = CustomItemManager.getItemFact("gun").getItem(abo.getItemData(), 1);
					ItemMeta im = is.getItemMeta();
					List<String> lore = OLD_ItemFact.getCraftingLore(abo);
					im.setLore(lore);
					is.setItemMeta(im);
					if (shopping)
						is = OLD_ItemFact.addShopLore(abo, is.clone());
					shopMenu.addItem(is);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		if (basei + gunslistr.size() < index)
			basei += gunslistr.size();
		else
			for (Ammo ammo : ammoRegister.values()) {
				if (shopping && ammo.cost() < 0)
					continue;
				if (basei < index) {
					basei++;
					continue;
				}
				basei++;
				if (index >= maxIndex)
					break;
				index++;
				try {
					ItemStack is = CustomItemManager.getItemFact("gun").getItem(ammo.getItemData(), ammo.getCraftingReturn());
					ItemMeta im = is.getItemMeta();
					List<String> lore = OLD_ItemFact.getCraftingLore(ammo);
					im.setLore(lore);
					is.setItemMeta(im);
					is.setAmount(ammo.getCraftingReturn());
					if (shopping)
						is = OLD_ItemFact.addShopLore(ammo, is.clone());
					shopMenu.addItem(is);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		if (basei + gunslistr.size() < index)
			basei += gunslistr.size();
		else
			for (ArmorObject armor : armorRegister.values()) {
				if (shopping && armor.cost() < 0)
					continue;
				if (basei < index) {
					basei++;
					continue;
				}
				basei++;
				if (index >= maxIndex)
					break;
				index++;
				try {
					ItemStack is = CustomItemManager.getItemFact("gun").getItem(armor.getItemData(), 1);
					ItemMeta im = is.getItemMeta();
					List<String> lore = OLD_ItemFact.getCraftingLore(armor);
					im.setLore(lore);
					is.setItemMeta(im);
					is.setAmount(armor.getCraftingReturn());
					if (shopping)
						is = OLD_ItemFact.addShopLore(armor, is.clone());
					shopMenu.addItem(is);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		return shopMenu;
	}

	/**
	 * GUNLIST:
	 * <p>
	 * 2: P30 3 PKP 4 MP5K 5 AK47 6: AK 7 M16 8 Remmington 9 FNFal 10 RPG 11 UMP 12
	 * SW1911 13 M40 14 Ammo 556 15 9mm 16 buckshot 17 rocketRPG 18 Enfield 19 Henry
	 * 20 MouserC96
	 * <p>
	 * 22 Grenades
	 */

	@Override
	public void onDisable() {
		for (Entry<MaterialStorage, ArmoryBaseObject> misc : miscRegister.entrySet()) {
			if (misc instanceof ThrowableItems) {
				for (Entry<Entity, ThrowableHolder> e : ThrowableItems.throwItems.entrySet()) {
					if (e.getKey() instanceof Item)
						e.getKey().remove();
				}
			}
		}

		for (Scoreboard s : coloredGunScoreboard)
			for (Team t : s.getTeams())
				t.unregister();

		for (Gunner g : gunners) {
			g.dispose();
		}
	}

	public Object a(String path, Object def) {
		if (getConfig().contains(path)) {
			return getConfig().get(path);
		}
		getConfig().set(path, def);
		saveConfig();
		return def;
	}

	@Override
	public void onEnable() {
		main = this;
		supportWorldGuard = Bukkit.getPluginManager().isPluginEnabled("WorldGuard");
		if (Bukkit.getPluginManager().getPlugin("PluginConstructorAPI") == null)
			// new DependencyDownloader(this, 276723);
			GithubDependDownloader.autoUpdate(this,
					new File(getDataFolder().getParentFile(), "PluginConstructorAPI.jar"), "ZombieStriker",
					"PluginConstructorAPI", "PluginConstructorAPI.jar");
		try {
			ParticleHandlers.initValues();
		} catch (Error | Exception e5) {
		}
		DEBUG(ChatColor.RED + "NOTICE ME");
		reloadVals();
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers())
					resourcepackReq.add(player.getUniqueId());
			}
		}.runTaskLater(this, 1);

		// check if Citizens is present and enabled.

		if (getServer().getPluginManager().getPlugin("Citizens") == null) {
			getLogger().log(Level.SEVERE, "Citizens 2.0 not found or not enabled (Ignore this.)");
		} else {
			try {
				// Register your trait with Citizens.
				net.citizensnpcs.api.CitizensAPI.getTraitFactory()
						.registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(GunnerTrait.class));
			} catch (Error | Exception e4) {
				getLogger().log(Level.SEVERE, "Citizens 2.0 failed to register gunner trait (Ignore this.)");
			}
		}

		Bukkit.getPluginManager().registerEvents(new QAListener(), this);
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
		metrics.addCustomChart(
				new Metrics.SimplePie("uses_default_resourcepack", new java.util.concurrent.Callable<String>() {
					@Override
					public String call() throws Exception {
						return overrideURL + "";
					}
				}));
		metrics.addCustomChart(
				new Metrics.SimplePie("has_an_expansion_pack", new java.util.concurrent.Callable<String>() {
					@Override
					public String call() throws Exception {
						return (expansionPacks.size() > 0) + "";
					}
				}));
	if(!CustomItemManager.isUsingCustomData()) {
		new BukkitRunnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				try {
					// Cheaty, hacky fix
					for (Player p : Bukkit.getOnlinePlayers()) {
						// if (p.getItemInHand().containsEnchantment(Enchantment.MENDING)) {
						if (p.getItemInHand() != null && p.getItemInHand().hasItemMeta())
							if (QualityArmory.isCustomItem(p.getItemInHand())) {
								if (ITEM_enableUnbreakable && (!p.getItemInHand().getItemMeta().spigot().isUnbreakable()
										&& !ignoreUnbreaking)) {
									ItemStack temp = p.getItemInHand();
									int j = QualityArmory.findSafeSpot(temp, false, overrideURL);
									temp.setDurability((short) Math.max(0, j - 1));
									temp = Gun.removeCalculatedExtra(temp);
									p.setItemInHand(temp);
								}
							}
						try {

							// if
							// (p.getInventory().getItemInOffHand().containsEnchantment(Enchantment.MENDING))
							// {
							if (p.getInventory().getItemInOffHand() != null
									&& p.getInventory().getItemInOffHand().hasItemMeta())
								if (QualityArmory.isCustomItem(p.getInventory().getItemInOffHand())) {
									if (ITEM_enableUnbreakable && (!p.getInventory().getItemInOffHand().getItemMeta()
											.spigot().isUnbreakable() && !ignoreUnbreaking)) {
										ItemStack temp = p.getInventory().getItemInOffHand();
										int j = QualityArmory.findSafeSpot(temp, false, overrideURL);
										temp.setDurability((short) Math.max(0, j - 1));
										temp = Gun.removeCalculatedExtra(temp);
										p.getInventory().setItemInOffHand(temp);
										return;
									}
								}
						} catch (Error | Exception e45) {
						}
					}
				} catch (Error | Exception catchy) {

				}
			}
		}.runTaskTimer(this, 20, 15);
	}
	}

	@SuppressWarnings({"unchecked", "deprecation"})
	public void reloadVals() {

		DEBUG = (boolean) a("ENABLE-DEBUG", false);

		Material glass = null;
		try {
			glass = Material.matchMaterial("STAINED_GLASS_PANE");
		} catch (Error | Exception e45) {
		}
		if (glass == null)
			glass = Material.matchMaterial("BLUE_STAINED_GLASS_PANE");

		prevButton = new ItemStack(glass, 1, (short) 14);
		nextButton = new ItemStack(glass, 1, (short) 5);

		new BoltactionCharger();
		new BreakactionCharger();
		new PumpactionCharger();
		new RevolverCharger();
		new BurstFireCharger();
		new DelayedBurstFireCharger();
		new PushbackCharger();
		new RequireAimCharger();

		new PumpactionReloader();
		new SingleBulletReloader();

		new MiniNukeProjectile();
		new ExplodingRoundProjectile();
		new HomingRocketProjectile();
		new RocketProjectile();
		new FireProjectile();

		gunRegister.clear();
		ammoRegister.clear();
		miscRegister.clear();
		armorRegister.clear();
		interactableBlocks.clear();
		craftingEntityNames.clear();

		// attachmentRegister.clear();
//Chris: Support more language file lang/message_xx.yml
		language = (String) a("language", "en");
		File langFolder = new File(getDataFolder(), "lang");
		if (null == langFolder) {
			if (langFolder.exists() && !langFolder.isDirectory()) {
				langFolder.delete();
			}
			langFolder.mkdir();
		}
		m = new MessagesYML(new File(langFolder, "message_" + language + ".yml"));
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
		if (m.getConfig().contains("Lore_Varients"))
			S_ITEM_VARIANTS_LEGACY = ChatColor.translateAlternateColorCodes('&',
					(String) m.a("Lore_Varients", S_ITEM_VARIANTS_LEGACY));
		S_ITEM_VARIANTS_NEW = ChatColor.translateAlternateColorCodes('&',
				(String) m.a("Lore_Variants", S_ITEM_VARIANTS_NEW));
		S_ITEM_COST = ChatColor.translateAlternateColorCodes('&', (String) m.a("Lore_Price", S_ITEM_COST));
		S_ITEM_DPS = ChatColor.translateAlternateColorCodes('&', (String) m.a("Lore_DamagePerSecond", S_ITEM_DPS));

		// Chris: add message Crafts
		S_ITEM_CRAFTS = ChatColor.translateAlternateColorCodes('&', (String) m.a("Lore_Crafts", S_ITEM_CRAFTS));
		S_ITEM_RETURNS = ChatColor.translateAlternateColorCodes('&', (String) m.a("Lore_Returns", S_ITEM_RETURNS));



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
		S_HELMET_RMB = ChatColor.translateAlternateColorCodes('&', (String) m.a("Lore-Helmet-RMB", S_HELMET_RMB));

		S_BUYCONFIRM = ChatColor.translateAlternateColorCodes('&', (String) m.a("Shop_Confirm", S_BUYCONFIRM));

		S_RESOURCEPACK_HELP = (String) m.a("Resourcepack_InCaseOfCrash", S_RESOURCEPACK_HELP);
		S_RESOURCEPACK_DOWNLOAD = (String) m.a("Resourcepack_Download", S_RESOURCEPACK_DOWNLOAD);
		S_RESOURCEPACK_BYPASS = (String) m.a("Resourcepack_NowBypass", S_RESOURCEPACK_BYPASS);
		S_RESOURCEPACK_OPTIN = (String) m.a("Resourcepack_NowOptIn", S_RESOURCEPACK_OPTIN);

		S_FULLYHEALED = ChatColor.translateAlternateColorCodes('&', (String) m.a("Medkit-FullyHealed", S_FULLYHEALED));
		S_MEDKIT_HEALING = ChatColor.translateAlternateColorCodes('&',
				(String) m.a("Medkit-Healing", S_MEDKIT_HEALING));
		S_MEDKIT_BLEEDING = ChatColor.translateAlternateColorCodes('&',
				(String) m.a("Medkit-Bleeding", S_MEDKIT_BLEEDING));

		S_MEDKIT_HEAL_AMOUNT = (double) m.a("Medkit-HEALING_HEARTS_AMOUNT", S_MEDKIT_HEAL_AMOUNT);

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
		if (Bukkit.getPluginManager().isPluginEnabled("ViaRewind"))
			hasViaRewind = true;
		if (Bukkit.getPluginManager().isPluginEnabled("ViaVersion"))
			hasViaVersion = true;
		if (getServer().getPluginManager().isPluginEnabled("ProtocolLib")) {
			hasProtocolLib = true;
			ProtocolLibHandler.initRemoveArmswing();
			ProtocolLibHandler.initAimBow();
		}

		if (getServer().getPluginManager().isPluginEnabled("Sentinel"))
			try {
				org.mcmonkey.sentinel.SentinelPlugin.integrations.add(new SentinelQAHandler());
			} catch (Error | Exception e4) {
			}


		friendlyFire = (boolean) a("FriendlyFireEnabled", false);

		kickIfDeniedRequest = (boolean) a("KickPlayerIfDeniedResourcepack", false);
		shouldSend = (boolean) a("useDefaultResourcepack", true);

		enableDurability = (boolean) a("EnableWeaponDurability", false);

		bulletStep = (double) a("BulletDetection.step", 0.10);

		blockbullet_door = (boolean) a("BlockBullets.door", false);
		blockbullet_halfslabs = (boolean) a("BlockBullets.halfslabs", false);
		blockbullet_leaves = (boolean) a("BlockBullets.leaves", false);
		blockbullet_water = (boolean) a("BlockBullets.water", false);
		blockbullet_glass = (boolean) a("BlockBullets.glass", false);

		enableInteractChests = (boolean) a("enableInteract.Chests", false);

		overrideAnvil = (boolean) a("overrideAnvil", false);

		showCrashMessage = (boolean) a("showPossibleCrashHelpMessage", showCrashMessage);

		verboseLoadingLogging = (boolean) a("verboseItemLogging", verboseLoadingLogging);

		sendOnJoin = (boolean) a("sendOnJoin", true);
		sendTitleOnJoin = (boolean) a("sendTitleOnJoin", false);
		secondsTilSend = Double.valueOf(a("SecondsTillRPIsSent", 5.0) + "");

		enableBulletTrails = (boolean) a("enableBulletTrails", true);
		smokeSpacing = Double.valueOf(a("BulletTrailsSpacing", 0.5) + "");

		enableArmorIgnore = (boolean) a("enableIgnoreArmorProtection", enableArmorIgnore);
		ignoreUnbreaking = (boolean) a("enableIgnoreUnbreakingChecks", ignoreUnbreaking);
		ignoreSkipping = (boolean) a("enableIgnoreSkipForBasegameItems", ignoreSkipping);

		ITEM_enableUnbreakable = (boolean) a("Items.enable_Unbreaking", ITEM_enableUnbreakable);

		// enableVisibleAmounts = (boolean) a("enableVisibleBulletCounts", false);
		reloadOnF = (boolean) a("enableReloadingWhenSwapToOffhand", true);
		reloadOnFOnly = (boolean) a("enableReloadOnlyWhenSwapToOffhand", false);

		// showOutOfAmmoOnItem = (boolean) a("showOutOfAmmoOnItem", false);
		showOutOfAmmoOnTitle = (boolean) a("showOutOfAmmoOnTitle", false);
		showReloadOnTitle = (boolean) a("showReloadingTitle", false);

		enableExplosionDamage = (boolean) a("enableExplosionDamage", false);
		enableExplosionDamageDrop = (boolean) a("enableExplosionDamageDrop", false);

		enablePrimaryWeaponHandler = (boolean) a("enablePrimaryWeaponLimiter", false);
		primaryWeaponLimit = (int) a("weaponlimiter_primaries", primaryWeaponLimit);
		secondaryWeaponLimit = (int) a("weaponlimiter_secondaries", secondaryWeaponLimit);

		enableCrafting = (boolean) a("enableCrafting", true);
		enableShop = (boolean) a("enableShop", true);

		AUTOUPDATE = (boolean) a("AUTO-UPDATE", true);
		SWAP_RMB_WITH_LMB = !(boolean) a("Swap-Reload-and-Shooting-Controls", false);

		orderShopByPrice = (boolean) a("Order-Shop-By-Price", orderShopByPrice);

		ENABLE_LORE_INFO = (boolean) a("enable_lore_gun-info_messages", true);
		ENABLE_LORE_HELP = (boolean) a("enable_lore_control-help_messages", true);

		HeadshotOneHit = (boolean) a("Enable_Headshot_Instantkill", HeadshotOneHit);
		headshotPling = (boolean) a("Enable_Headshot_Notification_Sound", headshotPling);
		headshot_sound = (String) a("Headshot_Notification_Sound", headshot_sound);
		headshotGoreSounds = (boolean) a("Enable_Headshot_Sounds", headshotGoreSounds);

		// ignoreArmorStands = (boolean) a("ignoreArmorStands", false);

		gravity = (double) a("gravityConstantForDropoffCalculations", gravity);

		allowGunReload = (boolean) a("allowGunReload", allowGunReload);
		AutoDetectResourcepackVersion = (boolean) a("Auto-Detect-Resourcepack", AutoDetectResourcepackVersion);
		MANUALLYSELECT18 = (boolean) a("ManuallyOverrideTo_1_8_systems",
				Bukkit.getPluginManager().isPluginEnabled("WetSponge") ? true : MANUALLYSELECT18);
		MANUALLYSELECT113 = (boolean) a("ManuallyOverrideTo_1_13_systems", MANUALLYSELECT113);

		unknownTranslationKeyFixer = (boolean) a("unknownTranslationKeyFixer", false);

		enableCreationOfFiles = (boolean) a("Enable_Creation_Of_Default_Files", true);

		addGlowEffects = (boolean) a("EnableGlowEffects", false);

		blockBreakTexture = (boolean) a("Break-Block-Texture-If-Shot", true);

		enableRecoil = (boolean) a("enableRecoil", true);

		bulletWound_initialbloodamount = (double) a("experimental.BulletWounds.InitialBloodLevel",
				bulletWound_initialbloodamount);
		bulletWound_BloodIncreasePerSecond = (double) a("experimental.BulletWounds.BloodIncreasePerSecond",
				bulletWound_BloodIncreasePerSecond);
		bulletWound_MedkitBloodlossHealRate = (double) a("experimental.BulletWounds.Medkit_Heal_Bloodloss_Rate",
				bulletWound_MedkitBloodlossHealRate);
		enableBleeding = (boolean) a("experimental.BulletWounds.enableBleeding", enableBleeding);

		disableHotBarMessageOnOutOfAmmo = (boolean) a("disableHotbarMessages.OutOfAmmo", false);
		disableHotBarMessageOnShoot = (boolean) a("disableHotbarMessages.Shoot", false);
		disableHotBarMessageOnReload = (boolean) a("disableHotbarMessages.Reload", false);

		enableReloadWhenOutOfAmmo = (boolean) a("automaticallyReloadGunWhenOutOfAmmo", false);

		swayModifier_Run = (double) a("generalModifiers.sway.Run", swayModifier_Run);
		swayModifier_Walk = (double) a("generalModifiers.sway.Walk", swayModifier_Walk);
		swayModifier_Ironsights = (double) a("generalModifiers.sway.Ironsights", swayModifier_Ironsights);
		swayModifier_Sneak = (double) a("generalModifiers.sway.Sneak", swayModifier_Sneak);

		List<String> avoidTypes = (List<String>) a("impenetrableEntityTypes",
				Collections.singleton(EntityType.ARROW.name()));
		if (getConfig().getBoolean("ignoreArmorStands"))
			QAMain.avoidTypes.add(EntityType.ARMOR_STAND);
		for (String s : avoidTypes) {
			try {
				QAMain.avoidTypes.add(EntityType.valueOf(s));
			} catch (Error | Exception e4) {
			}
		}

		try {
			enableEconomy = EconHandler.setupEconomy();
		} catch (Exception | Error e) {
		}

		overrideURL = (boolean) a("DefaultResourcepackOverride", false);

		enableIronSightsON_RIGHT_CLICK = (boolean) a("IronSightsOnRightClick", false);
		enableSwapSingleShotOnAim = (boolean) a("SwapSneakToSingleFile", enableSwapSingleShotOnAim);

		List<String> destarray = (List<String>) a("DestructableMaterials",
				Collections.singletonList("MATERIAL_NAME_HERE"));
		for (String s : destarray) {
			try {
				destructableBlocks.add(Material.getMaterial(s));
			} catch (Error | Exception e54) {
				try {
					// destructableBlocks.add(Material.getMaterial(Integer.parseInt(s.split(":")[0])));
				} catch (Error | Exception e5) {
				}
			}
		}

		for (Material m : Material.values())
			if (m.isBlock())
				if (m.name().contains("DOOR") || m.name().contains("TRAPDOOR") || m.name().contains("BUTTON")
						|| m.name().contains("LEVER"))
					interactableBlocks.add(m);
// Chris: default has 1.14 ItemType
		if (MANUALLYSELECT18 || !isVersionHigherThan(1, 9)) {
			//1.8
			CustomItemManager.registerItemType(getDataFolder(), "gun", new me.zombie_striker.customitemmanager.versions.V1_8.CustomGunItem());
		} else if (!isVersionHigherThan(1, 14) || MANUALLYSELECT113) {
			//1.9 to 1.13
			CustomItemManager.registerItemType(getDataFolder(), "gun", new me.zombie_striker.customitemmanager.versions.V1_13.CustomGunItem());
			//Make sure vehicles are safe
			if (!overrideURL) {
				expansionPacks.add(MaterialStorage.getMS(Material.DIAMOND_AXE, 80, 0));
				expansionPacks.add(MaterialStorage.getMS(Material.DIAMOND_AXE, 81, 0));
				expansionPacks.add(MaterialStorage.getMS(Material.DIAMOND_AXE, 82, 0));
				expansionPacks.add(MaterialStorage.getMS(Material.DIAMOND_AXE, 83, 0));
				expansionPacks.add(MaterialStorage.getMS(Material.DIAMOND_AXE, 84, 0));
				expansionPacks.add(MaterialStorage.getMS(Material.DIAMOND_AXE, 85, 0));
				expansionPacks.add(MaterialStorage.getMS(Material.DIAMOND_AXE, 86, 0));
				expansionPacks.add(MaterialStorage.getMS(Material.DIAMOND_AXE, 87, 0));
				expansionPacks.add(MaterialStorage.getMS(Material.DIAMOND_AXE, 88, 0));
				expansionPacks.add(MaterialStorage.getMS(Material.DIAMOND_AXE, 89, 0));
				expansionPacks.add(MaterialStorage.getMS(Material.DIAMOND_AXE, 92, 0));
				expansionPacks.add(MaterialStorage.getMS(Material.DIAMOND_AXE, 94, 0));
				expansionPacks.add(MaterialStorage.getMS(Material.DIAMOND_AXE, 95, 0));
				expansionPacks.add(MaterialStorage.getMS(Material.DIAMOND_AXE, 96, 0));
				expansionPacks.add(MaterialStorage.getMS(Material.DIAMOND_AXE, 97, 0));
				expansionPacks.add(MaterialStorage.getMS(Material.DIAMOND_AXE, 104, 0));
				expansionPacks.add(MaterialStorage.getMS(Material.DIAMOND_AXE, 111, 0));
				expansionPacks.add(MaterialStorage.getMS(Material.DIAMOND_AXE, 112, 0));
				expansionPacks.add(MaterialStorage.getMS(Material.DIAMOND_AXE, 114, 0));
				expansionPacks.add(MaterialStorage.getMS(Material.DIAMOND_AXE, 115, 0));
				expansionPacks.add(MaterialStorage.getMS(Material.DIAMOND_AXE, 116, 0));
			}
		} else {
			//1.14. Use crossbows
			CustomItemManager.registerItemType(getDataFolder(), "gun", new me.zombie_striker.customitemmanager.versions.V1_14.CustomGunItem());
		}

		// Chris: if switch on, create default items.
		if (enableCreationOfFiles) {
			CustomItemManager.getItemType("gun").initItems(getDataFolder());
		}
		if(CustomItemManager.getItemType("gun") !=null){
			CustomItemManager.getItemType("gun").initIronSights(getDataFolder());
		}


// Chirs: fix bug
		if (overrideURL) {
			CustomItemManager.setResourcepack((String) a("DefaultResourcepack", CustomItemManager.getResourcepack()));
		} else {
			if (!getConfig().contains("DefaultResourcepack")
					|| !getConfig().getString("DefaultResourcepack").equals(CustomItemManager.getResourcepack())) {
				getConfig().set("DefaultResourcepack", CustomItemManager.getResourcepack());
				saveTheConfig = true;
			}
		}
		if (saveTheConfig) {
			DEBUG(prefix + " Needed to save config: code=2");
			saveConfig();
		}


		// Skull texture
		GunYMLLoader.loadAmmo(this);
		GunYMLLoader.loadMisc(this);
		GunYMLLoader.loadGuns(this);
		GunYMLLoader.loadAttachments(this);
		GunYMLLoader.loadArmor(this);
		if (QAMain.enableBleeding)
			BulletWoundHandler.startTimer();

		if (tfh != null) {
			tfh = new TreeFellerHandler();
			Bukkit.getPluginManager().registerEvents(tfh, this);
		}
		if (addGlowEffects) {
			coloredGunScoreboard = new ArrayList<>();
			coloredGunScoreboard.add(registerGlowTeams(Bukkit.getScoreboardManager().getMainScoreboard()));
		}

		//Chris: Find and reg craft entity name
		registerCraftEntityNames(gunRegister);
		registerCraftEntityNames(ammoRegister);
		registerCraftEntityNames(miscRegister);
		registerCraftEntityNames(armorRegister);
	}


	public static void registerCraftEntityNames(HashMap<MaterialStorage, ?> regMaps) {
		if (null != regMaps && !regMaps.isEmpty()) {
			for (Object item: regMaps.values()) {
				try {
					ItemStack[] itemStacks = ((ArmoryBaseObject) item).getIngredients();
					if (null != itemStacks && itemStacks.length > 0) {
						for (ItemStack itemStack: itemStacks) {
							String itemName = itemStack.getType().name();
							String showName = ChatColor.translateAlternateColorCodes('&', (String) QAMain.m.a("EntityType." + itemName, itemName));
							craftingEntityNames.put(itemName, showName);
						}
					}
				} catch (Exception e) {
					//
				}
			}
		}
	}

	public static String findCraftEntityName(String itemName, String defaultName) {
		String value = craftingEntityNames.get(itemName);
		if (null == value || value.trim().length() <= 0) {
			value = craftingEntityNames.put(itemName, defaultName);
		}
		return value;
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
				// temp = new ItemStack(Integer.parseInt(k[0]));
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
		if (arg == null || startsWith == null)
			return false;
		return arg.toLowerCase().startsWith(startsWith.toLowerCase());
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length == 1) {
			List<String> s = new ArrayList<String>();
			if (b("give", args[0]))
				s.add("give");
			if (b("drop", args[0]))
				s.add("drop");
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
			if (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("drop")) {
				for (Entry<MaterialStorage, Gun> e : gunRegister.entrySet()) {
					if (e.getValue() instanceof AttachmentBase) {
						if (b(e.getValue().getName(), args[1]))
							s.add(e.getValue().getName());

					} else if (b(e.getValue().getName(), args[1]))
						s.add(e.getValue().getName());
				}
				for (Entry<MaterialStorage, Ammo> e : ammoRegister.entrySet())
					if (b(e.getValue().getName(), args[1]))
						s.add(e.getValue().getName());
				for (Entry<MaterialStorage, ArmoryBaseObject> e : miscRegister.entrySet())
					if (b(e.getValue().getName(), args[1]))
						s.add(e.getValue().getName());
				for (Entry<MaterialStorage, ArmorObject> e : armorRegister.entrySet())
					if (b(e.getValue().getName(), args[1]))
						s.add(e.getValue().getName());
			}
			return s;
		}
		if (args[0].equalsIgnoreCase("give")) {
			if (args.length > 2) {
				List<String> s = new ArrayList<String>();
				if (b("~", args[0]))
					s.add("~");
				return s;
			}
		}
		return null;
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
									args[1], Collections.singletonList("Custom_item"), itemInHand.getType(),
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
					Player player = null;
					if (args.length > 1) {
						player = Bukkit.getPlayer(args[1]);
						if (player == null) {
							sender.sendMessage(prefix + " This player does not exist.");
							return true;
						}
					} else {
						player = (Player) sender;
					}
					namesToBypass.remove(player.getName());
					resourcepackwhitelist.set("Names_Of_players_to_bypass", namesToBypass);
					sender.sendMessage(prefix + S_RESOURCEPACK_OPTIN);
					QualityArmory.sendResourcepack(player, true);
					return true;
				}
				if (args[0].equalsIgnoreCase("getResourcepack")) {
					Player player = null;
					if (args.length > 1) {
						player = Bukkit.getPlayer(args[1]);
						if (player == null) {
							sender.sendMessage(prefix + " This player does not exist.");
							return true;
						}
					} else {
						player = (Player) sender;
					}
					namesToBypass.add(player.getName());
					resourcepackwhitelist.set("Names_Of_players_to_bypass", namesToBypass);
					player.sendMessage(prefix + S_RESOURCEPACK_DOWNLOAD);
					/*if (AutoDetectResourcepackVersion && Bukkit.getPluginManager().isPluginEnabled("ViaRewind")) {
						if (MANUALLYSELECT18) {
							player.sendMessage("For 1.9+ : " + url18New);
							player.sendMessage("For 1.8  : " + url18);
						} else {
							player.sendMessage("For 1.9+ : " + url);
							player.sendMessage("For 1.8  : " + url18);
						}
					} else {*/
					player.sendMessage(CustomItemManager.getResourcepack());
					//}
					player.sendMessage(prefix + S_RESOURCEPACK_BYPASS);

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
									args[1], Collections.singletonList("Custom_item"), itemInHand.getDurability(), null,
									WeaponType.RIFLE, false, "9mm", 1, 0.2, itemInHand.getType(), 64, 100, 1.5, 0.2, 1,
									false, 200, null, 120, 0, (AutoDetectResourcepackVersion), WeaponSounds.GUN_MEDIUM);
							GunYMLCreator.createSkullAmmo(true, getDataFolder(), true, "custom_" + args[1], args[1],
									args[1], Collections.singletonList("Custom_item"), itemInHand.getType(),
									itemInHand.getDurability(),
									(itemInHand.getType() == MultiVersionLookup.getSkull()
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
						reloadConfig();
						reloadVals();
						sender.sendMessage(prefix + " Guns and configs have been reloaded.");
						return true;
					} else {
						sender.sendMessage(prefix + ChatColor.RED + S_NOPERM);
						return true;
					}
				}

				if (args[0].equalsIgnoreCase("drop")) {
					if (!sender.hasPermission("qualityarmory.drop")) {
						sender.sendMessage(prefix + ChatColor.RED + S_NOPERM);
						return true;
					}
					if (args.length == 1) {
						sender.sendMessage(prefix + " The item name is required");
						StringBuilder sb = new StringBuilder();
						sb.append("Valid items: ");
						for (Gun g : gunRegister.values()) {
							sb.append(g.getName() + ", ");
						}
						sb.append(ChatColor.GRAY);
						for (Ammo g : ammoRegister.values()) {
							sb.append(g.getName() + ", ");
						}
						sb.append(ChatColor.WHITE);
						for (ArmoryBaseObject g : miscRegister.values()) {
							sb.append(g.getName() + ", ");
						}
						sb.append(ChatColor.GRAY);
						for (ArmorObject g : armorRegister.values()) {
							sb.append(g.getName() + ", ");
						}
						sb.append(ChatColor.WHITE);
						sender.sendMessage(prefix + sb.toString());
						return true;
					}

					ArmoryBaseObject g = QualityArmory.getCustomItemByName(args[1]);
					if (g != null) {
						Location loc = null;
						Location relLoc = null;
						if (args.length >= 5) {
							World w = null;
							if (sender instanceof Player) {
								relLoc = (loc = ((Player) sender).getLocation());
							} else if (sender instanceof BlockCommandSender) {
								relLoc = (loc = ((BlockCommandSender) sender).getBlock().getLocation());
							}

							if (args.length >= 6) {
								if (args[2].equals("~"))
									w = relLoc.getWorld();
								else
									w = Bukkit.getWorld(args[5]);
							} else {
								w = relLoc.getWorld();
							}

							double x = 0;
							double y = 0;
							double z = 0;
							if (args[2].equals("~"))
								x = relLoc.getX();
							else
								x = Double.parseDouble(args[2]);
							if (args[3].equals("~"))
								y = relLoc.getY();
							else
								y = Double.parseDouble(args[3]);
							if (args[4].equals("~"))
								z = relLoc.getZ();
							else
								z = Double.parseDouble(args[4]);
							loc = new Location(w, x, y, z);
						}
						if (loc == null) {
							sender.sendMessage(prefix + " A valid location is required");
							return true;
						}
						ItemStack temp = null;

						if (g instanceof Gun) {
							temp = CustomItemManager.getItemFact("gun").getItem(g.getItemData(), 1);
						} else if (g instanceof Ammo) {
							temp = CustomItemManager.getItemFact("gun").getItem(g.getItemData(), 1);
						} else {
							temp = CustomItemManager.getItemFact("gun").getItem(g.getItemData(), g.getCraftingReturn());
							temp.setAmount(g.getCraftingReturn());
						}
						if (temp != null) {
							loc.getWorld().dropItem(loc, temp);
							sender.sendMessage(prefix + " Dropping item " + g.getName() + " at that location");
						} else {
							sender.sendMessage(prefix + " Failed to drop item " + g.getName() + " at that location");
						}
					} else {
						sender.sendMessage(prefix + " Could not find item \"" + args[1] + "\"");
					}
					return true;
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
							sb.append(g.getName() + ", ");
						}
						sb.append(ChatColor.GRAY);
						for (Ammo g : ammoRegister.values()) {
							sb.append(g.getName() + ", ");
						}
						sb.append(ChatColor.WHITE);
						for (ArmoryBaseObject g : miscRegister.values()) {
							sb.append(g.getName() + ", ");
						}
						sb.append(ChatColor.GRAY);
						for (ArmorObject g : armorRegister.values()) {
							sb.append(g.getName() + ", ");
						}
						sb.append(ChatColor.WHITE);
						// for (AttachmentBase g : attachmentRegister.values()) {
						// sb.append(g.getAttachmentName() + ",");
						// }
						sender.sendMessage(prefix + sb.toString());
						return true;
					}

					ArmoryBaseObject g = QualityArmory.getCustomItemByName(args[1]);
					if (g != null) {
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
							temp = CustomItemManager.getItemFact("gun").getItem(g.getItemData(), 1);
							who.getInventory().addItem(temp);
						} else if (g instanceof Ammo) {
							QualityArmory.addAmmoToInventory(who, (Ammo) g, ((Ammo) g).getMaxAmount());
						} else {
							temp = CustomItemManager.getItemFact("gun").getItem(g.getItemData(), g.getCraftingReturn());
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
					QualityArmory.sendResourcepack(((Player) sender), true);
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

		sender.sendMessage(ChatColor.GOLD + "/QA getResourcepack: " + ChatColor.GRAY
				+ "Sends the link to the resourcepack. Disables the resourcepack prompts until /qa sendResourcepack is used");
		sender.sendMessage(ChatColor.GOLD + "/QA sendResourcepack: " + ChatColor.GRAY
				+ "Sends the resourcepack prompt. Enables the resourcepack prompt. Enabled by default");

		// sender.sendMessage(
		// ChatColor.GOLD + "/QA listItemIds: " + ChatColor.GRAY + "Lists the materials
		// and data for all items.");
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
				if (OLD_ItemFact.sameGun(is, is1)) {
					return true;
				}
		}
		return false;
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
					YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
		}
	}

	@Override
	public FileConfiguration getConfig() {
		if (this.config == null) {
			this.reloadConfig();
		}
		return this.config;
	}
}
