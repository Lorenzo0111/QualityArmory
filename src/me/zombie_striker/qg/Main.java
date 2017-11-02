package me.zombie_striker.qg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import me.zombie_striker.qg.ammo.*;
import me.zombie_striker.qg.guns.*;
import me.zombie_striker.qg.handlers.AimManager;
import me.zombie_striker.qg.handlers.IronSightsToggleItem;
import me.zombie_striker.qg.handlers.Update19Events;
import me.zombie_striker.qg.handlers.Update19OffhandChecker;
import me.zombie_striker.qg.handlers.WorldGuardSupport;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Main extends JavaPlugin implements Listener {

	public static HashMap<MaterialStorage, Gun> gunRegister = new HashMap<>();
	public static HashMap<MaterialStorage, Ammo> ammoRegister = new HashMap<>();

	public static HashMap<UUID, List<BukkitTask>> reloadingTasks = new HashMap<UUID, List<BukkitTask>>();

	public static ArrayList<UUID> resourcepackReq = new ArrayList<>();

	private static Main main;

	public static Main getInstance() {
		return main;
	}

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
	public static boolean enableVisibleAmounts = true;
	public static boolean reloadOnF = true;

	public static boolean overrideURL = false;
	public static String url = "https://www.dropbox.com/s/55ton366ci0vt92/QualityArmory%201.4.zip?dl=1";

	public static String S_NOPERM = " You do not have permission to do that";
	public static String S_NORES1 = " You do not have the resoucepack";
	public static String S_NORES2 = " Accept the resoucepack to see correct textures";
	public static String S_ANVIL = " You do not have permission to use this armory bench. ShiftClick to access anvil.";
	public static String S_ITEM_BULLETS = "Bullets";
	public static String S_ITEM_DURIB = "Durability";
	public static String S_ITEM_DAMAGE = "Damage";
	public static String S_ITEM_AMMO = "Ammo";
	public static String S_ITEM_ING = "Ingredients";

	public static String prefix = ChatColor.GRAY + "[" + ChatColor.DARK_GREEN
			+ "QualityArmory" + ChatColor.GRAY + "]" + ChatColor.WHITE;

	public Inventory craftingMenu;
	public static String S_craftingBenchName = "Armory Bench";
	public static String S_missingIngredients = "You do not have all the materials needed to craft this";

	public static Material guntype = Material.DIAMOND_HOE;

	public static CustomYml m;

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
		supportWorldGuard = Bukkit.getPluginManager().isPluginEnabled(
				"WorldGuard");

		m = new CustomYml(new File(getDataFolder(), "messages.yml"));
		S_ANVIL = (String) m.a("NoPermAnvilMessage", S_ANVIL);
		S_NOPERM = (String) m.a("NoPerm", S_NOPERM);
		S_craftingBenchName = (String) m.a("CraftingBenchName",
				S_craftingBenchName);
		S_missingIngredients = (String) m.a("Missing_Ingredients",
				S_missingIngredients);
		S_NORES1 = (String) m.a("NoResoucepackMessage1", S_NORES1);
		S_NORES2 = (String) m.a("NoResourcepackMessage2", S_NORES2);
		S_ITEM_AMMO = (String) m.a("Lore_Ammo", S_ITEM_AMMO);
		S_ITEM_BULLETS = (String) m.a("lore_bullets", S_ITEM_BULLETS);
		S_ITEM_DAMAGE = (String) m.a("Lore_Damage", S_ITEM_DAMAGE);
		S_ITEM_DURIB = (String) m.a("Lore_Durib", S_ITEM_DURIB);
		S_ITEM_ING = (String) m.a("Lore_ingredients", S_ITEM_ING);

		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers())
					resourcepackReq.add(player.getUniqueId());
			}
		}.runTaskLater(this, 1);
		if (Bukkit.getPluginManager().getPlugin("PluginConstructorAPI") == null)
			new DependencyDownloader(this, 276723);
		Bukkit.getPluginManager().registerEvents(this, this);
		Bukkit.getPluginManager().registerEvents(new AimManager(), this);

		shouldSend = (boolean) a("useDefaultResourcepack", true);
		UnlimitedAmmoPistol = (boolean) a("UnlimitedPistolAmmo", false);
		UnlimitedAmmoShotgun = (boolean) a("UnlimitedShotgunAmmo", false);
		UnlimitedAmmoRifle = (boolean) a("UnlimitedRifleAmmo", false);
		UnlimitedAmmoSMG = (boolean) a("UnlimitedSMGAmmo", false);
		UnlimitedAmmoSniper = (boolean) a("UnlimitedSniperAmmo", false);
		UnlimitedAmmoRPG = (boolean) a("UnlimitedRocketAmmo", false);
		enableDurability = (boolean) a("EnableWeaponDurability", false);

		bulletStep = (double) a("BulletDetection.step", 0.25);

		blockbullet_door = (boolean) a("BlockBullets.door", false);
		blockbullet_halfslabs = (boolean) a("BlockBullets.halfslabs", false);
		blockbullet_leaves = (boolean) a("BlockBullets.leaves", false);
		blockbullet_water = (boolean) a("BlockBullets.water", false);

		overrideAnvil = (boolean) a("overrideAnvil", false);

		sendOnJoin = (boolean) a("sendOnJoin", false);

		enableBulletTrails = (boolean) a("enableBulletTrails", true);

		enableVisibleAmounts = (boolean) a("enableVisableBulletCounts", true);
		reloadOnF = (boolean) a("enableReloadingWhenSwapToOffhand", true);

		try {
			guntype = Material.matchMaterial((String) a("gunMaterialType",
					guntype.toString()));
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
		enableIronSightsON_RIGHT_CLICK = (boolean) a("IronSightsOnRightCLick",
				false);

		// ,(float)a("Weapon.RPG.Damage", 10)

		List<String> stringsWoodRif = Arrays.asList(new String[] {
				getIngString(Material.IRON_INGOT, 0, 12),
				getIngString(Material.WOOD, 0, 2),
				getIngString(Material.REDSTONE, 0, 5) });
		List<String> stringsGoldRif = Arrays.asList(new String[] {
				getIngString(Material.IRON_INGOT, 0, 12),
				getIngString(Material.GOLD_INGOT, 0, 2),
				getIngString(Material.REDSTONE, 0, 5) });
		List<String> stringsMetalRif = Arrays.asList(new String[] {
				getIngString(Material.IRON_INGOT, 0, 15),
				getIngString(Material.REDSTONE, 0, 5) });
		List<String> stringsPistol = Arrays.asList(new String[] {
				getIngString(Material.IRON_INGOT, 0, 5),
				getIngString(Material.REDSTONE, 0, 2) });
		List<String> stringsRPG = Arrays.asList(new String[] {
				getIngString(Material.IRON_INGOT, 0, 32),
				getIngString(Material.REDSTONE, 0, 10) });

		List<String> stringsAmmo = Arrays.asList(new String[] {
				getIngString(Material.IRON_INGOT, 0, 1),
				getIngString(Material.SULPHUR, 0, 1),
				getIngString(Material.REDSTONE, 0, 1) });
		List<String> stringsAmmoRPG = Arrays.asList(new String[] {
				getIngString(Material.IRON_INGOT, 0, 4),
				getIngString(Material.SULPHUR, 0, 6),
				getIngString(Material.REDSTONE, 0, 1) });

		ammoRegister.put(m(14), AmmoType.Ammo556.type = new Ammo556(
				getIngredients("Ammo556", stringsAmmo), 4));
		ammoRegister.put(m(15), AmmoType.Ammo9mm.type = new Ammo9mm(
				getIngredients("Ammo9mm", stringsAmmo), 4));
		ammoRegister.put(m(16), AmmoType.AmmoBuckshot.type = new AmmoShotGun(
				getIngredients("AmmoBuckshot", stringsAmmo), 2));
		ammoRegister.put(m(17), AmmoType.AmmoRPG.type = new AmmoRPG(
				getIngredients("AmmoRPG", stringsAmmoRPG), 1));

		gunRegister
				.put(m(2),
						new P30((int) a("Weapon.P30.Durability", 500),
								getIngredients("p30", stringsPistol), (int) a(
										"Weapon.P30.Damage", 3)));
		gunRegister.put(
				m(4),
				new MP5K((int) a("Weapon.MP5K.Durability", 1000),
						getIngredients("MP5K", stringsPistol), (int) a(
								"Weapon.MP5K.Damage", 1)));
		gunRegister.put(
				m(5),
				new AK47((int) a("Weapon.AK47.Durability", 1000),
						getIngredients("AK47", stringsWoodRif), (int) a(
								"Weapon.AK47.Damage", 3)));
		gunRegister.put(
				m(7),
				new M16((int) a("Weapon.M16.Durability", 1000), getIngredients(
						"M16", stringsMetalRif),
						(int) a("Weapon.M16.Damage", 3)));
		gunRegister.put(
				m(8),
				new Remmington((int) a("Weapon.Remmington.Durability", 500),
						getIngredients("Remmington", stringsMetalRif), (int) a(
								"Weapon.Remmington.Damage", 1)));
		gunRegister.put(m(10), new RPG((int) a("Weapon.RPG.Durability", 100),
				getIngredients("RPG", stringsRPG)));
		gunRegister.put(
				m(12),
				new SW1911((int) a("Weapon.SW1911.Durability", 500),
						getIngredients("SW1911", stringsPistol), (int) a(
								"Weapon.SW1911.Damage", 2)));
		gunRegister
				.put(m(13),
						new M40((int) a("Weapon.M40.Durability", 500),
								getIngredients("M40", stringsWoodRif), (int) a(
										"Weapon.M40.Damage", 5)));

		gunRegister.put(
				m(18),
				new Enfield((int) a("Weapon.Enfield.Durability", 500),
						getIngredients("Enfield", stringsGoldRif), (int) a(
								"Weapon.Enfield.Damage", 2)));
		gunRegister.put(
				m(19),
				new HenryRifle((int) a("Weapon.Henry.Durability", 500),
						getIngredients("Henry", stringsGoldRif), (int) a(
								"Weapon.Henry.Damage", 3)));
		gunRegister.put(
				m(20),
				new MouserC96((int) a("Weapon.MouserC96.Durability", 500),
						getIngredients("MouserC96", stringsPistol), (int) a(
								"Weapon.MouserC96.Damage", 2)));
		if (saveTheConfig)
			saveConfig();

		if (!new File(getDataFolder(), "newGuns/examplegun.yml").exists()) {
			if (!new File(getDataFolder(), "newGuns").exists()) {
				new File(getDataFolder(), "newGuns").mkdirs();
			}
			FileConfiguration f = YamlConfiguration.loadConfiguration(new File(
					getDataFolder(), "newGuns/examplegun.yml"));
			f.set("invalid", true);
			f.set("name", "ExampleGun");
			f.set("id", 21);
			f.set("guntype", GunType.RIFLE.name());
			StringBuilder validGuns = new StringBuilder();
			for (GunType g : GunType.values()) {
				validGuns.append(g.name() + ", ");
			}
			f.set("_VALID_GUN_TYPES", validGuns.toString());
			f.set("enableIronSights", false);
			f.set("ammotype", AmmoType.Ammo556.toString());
			f.set("_VALID_AMMO_TYPES", AmmoType.Ammo556.toString() + ", "
					+ AmmoType.Ammo9mm.toString() + ", "
					+ AmmoType.AmmoBuckshot.toString() + ", "
					+ AmmoType.AmmoRPG.toString() + ", ");
			f.set("damage", 5);
			f.set("sway", 0.2);
			f.set("material", guntype.name());
			f.set("maxbullets", 12);
			f.set("durability", 1000);
			try {
				f.save(new File(getDataFolder(), "newGuns/examplegun.yml"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for (File f : new File(getDataFolder(), "newGuns").listFiles()) {
			try {
				if (f.getName().contains("yml")) {
					FileConfiguration f2 = YamlConfiguration
							.loadConfiguration(f);
					if ((!f2.contains("invalid")) || !f2.getBoolean("invalid")) {
						Material m = (Material) (f2.contains("material") ? Material
								.matchMaterial(f2.getString("material"))
								: guntype);
						MaterialStorage ms = MaterialStorage.getMS(m,
								f2.getInt("id"));
						gunRegister
								.put(ms,
										new DefaultGun(
												f2.getString("name"),
												ms,
												GunType.valueOf(f2
														.getString("guntype")),
												f2.getBoolean("enableIronSights"),
												AmmoType.valueOf(f2
														.getString("ammotype")),
												f2.getDouble("sway"), 2, f2
														.getInt("maxbullets"),
												f2.getInt("damage"), f2
														.getInt("durability")));
					}
				}
			} catch (Exception e) {
			}
		}

		craftingMenu = Bukkit.createInventory(null, 27, S_craftingBenchName);
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
			} catch (Exception e) {
				getLogger().warning("-Failed to load Gun:" + g.getName());
				e.printStackTrace();
			}
		}
		for (Ammo g : ammoRegister.values()) {
			try {
				getLogger().info("-Loading Ammo:" + g.getName());
				ItemStack is = ItemFact.getAmmo(g);
				ItemMeta im = is.getItemMeta();
				List<String> lore = ItemFact.getCraftingLore(g);
				im.setLore(lore);
				is.setItemMeta(im);
				is.setAmount(g.getCraftingReturn());
				craftingMenu.addItem(is);
			} catch (Exception e) {
				getLogger().warning("-Failed to load ammo:" + g.getName());
				e.printStackTrace();
			}
		}
		try {
			Bukkit.getPluginManager()
					.registerEvents(new Update19Events(), this);
		} catch (Exception | Error e) {
		}

		try {
			/* final Updater updater = */new Updater(this, 278412, true);
			/*
			 * new BukkitRunnable() { public void run() { // TODO: Works well.
			 * Make changes for the updaters of // PixelPrinter and Music later.
			 * if (updater.updaterActive) updater.download(false); }
			 * }.runTaskTimerAsynchronously(this, 20 /* * 60 *, 20 * 60 * 5);
			 */
		} catch (Exception e) {
		}

		Metrics metrics = new Metrics(this);

		// Optional: Add custom charts
		metrics.addCustomChart(new Metrics.SimplePie("GunCount",
				new java.util.concurrent.Callable<String>() {
					@Override
					public String call() throws Exception {
						return gunRegister.size() + "";
					}
				}));
	}

	@EventHandler
	public void onResourcepackStatusEvent(PlayerResourcePackStatusEvent event) {
		if (event.getStatus() == PlayerResourcePackStatusEvent.Status.ACCEPTED
				|| event.getStatus() == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED)
			Main.resourcepackReq.add(event.getPlayer().getUniqueId());
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

								if (e.getPlayer().getItemInHand().getItemMeta()
										.getDisplayName()
										.contains("Reloading."))
									return;
								if (Update19OffhandChecker.supportOffhand(e
										.getPlayer())) {
									try {
										ItemStack tempremove = null;
										if (e.getPlayer().getInventory()
												.getItemInOffHand() != null)
											tempremove = e.getPlayer()
													.getInventory()
													.getItemInOffHand();
										e.getPlayer()
												.getInventory()
												.setItemInOffHand(
														e.getPlayer()
																.getInventory()
																.getItemInMainHand());
										ItemStack ironsights = new ItemStack(
												guntype, 1,
												(short) IronSightsToggleItem
														.getData());
										ItemMeta im = ironsights.getItemMeta();
										im.setDisplayName(IronSightsToggleItem
												.getItemName());
										im.setUnbreakable(true);
										im.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_UNBREAKABLE);
										ironsights.setItemMeta(im);
										e.getPlayer().getInventory()
												.setItemInMainHand(ironsights);
										if (tempremove != null) {
											e.getPlayer().getInventory()
													.addItem(tempremove);
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
						e.getPlayer()
								.getInventory()
								.setItemInMainHand(
										e.getPlayer().getInventory()
												.getItemInOffHand());
						e.getPlayer().getInventory().setItemInOffHand(null);
					} catch (Error e2) {
					}
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	public ItemStack[] getIngredients(String name, List<String> ing) {
		if (!getConfig().contains("Crafting." + name)) {
			getConfig().set("Crafting." + name, ing);
			saveConfig();
		}
		List<String> e = getConfig().getStringList("Crafting." + name);
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

	@SuppressWarnings("deprecation")
	public ItemStack[] getIngredients(String name) {
		if (!getConfig().contains("Crafting." + name)) {
			getConfig().set(
					"Crafting." + name,
					Arrays.asList(new String[] { getIngString(
							Material.IRON_INGOT, 0, 10) }));
			saveConfig();
		}
		List<String> e = getConfig().getStringList("Crafting." + name);
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
	public List<String> onTabComplete(CommandSender sender, Command command,
			String alias, String[] args) {
		if (args.length == 1) {
			List<String> s = new ArrayList<String>();
			if (b("givegun", args[0]))
				s.add("giveGun");
			if (b("giveammo", args[0]))
				s.add("giveAmmo");

			return s;
		}
		if (args.length == 2) {
			List<String> s = new ArrayList<String>();
			if (args[0].equalsIgnoreCase("giveGun")) {
				for (Entry<MaterialStorage, Gun> e : gunRegister.entrySet()) {
					if (b(e.getValue().getName(), args[1])) {
						s.add(e.getValue().getName());
					}
				}
			}
			if (args[0].equalsIgnoreCase("giveAmmo")) {
				for (Entry<MaterialStorage, Ammo> e : ammoRegister.entrySet()) {
					if (b(e.getValue().getName(), args[1])) {
						s.add(e.getValue().getName());
					}
				}
			}
			return s;
		}
		return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (command.getName().equalsIgnoreCase("QualityArmory")) {

			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("giveGun")) {
					if (!sender.hasPermission("qualityarmory.give")) {
						sender.sendMessage(prefix + ChatColor.RED + S_NOPERM);
						return true;
					}
					if (args.length == 1) {
						sender.sendMessage(prefix + " The gun type is required");
						StringBuilder sb = new StringBuilder();
						sb.append("Valid Guns: ");
						for (Gun g : gunRegister.values()) {
							sb.append(g.getName() + ",");
						}
						sender.sendMessage(prefix + sb.toString());
						return true;
					}
					Gun g = null;
					StringBuilder gunName = new StringBuilder();
					// for (int j = 1; j < args.length; j++) {
					// gunName.append(args[j]);
					// if (j != args.length - 1)
					// gunName.append(" ");
					// }
					gunName.append(args[1]);
					for (Entry<MaterialStorage, Gun> e : gunRegister.entrySet()) {
						if (e.getValue().getName()
								.equalsIgnoreCase(gunName.toString())) {
							g = e.getValue();
							break;
						}
					}
					if (g != null) {
						Player who = sender instanceof Player ? ((Player) sender)
								: null;
						if (args.length > 2)
							who = Bukkit.getPlayer(args[2]);
						if (who == null) {
							sender.sendMessage("That player is not online");
							return true;
						}

						ItemStack temp = ItemFact.getGun(g);
						who.getInventory().addItem(temp);
						sender.sendMessage(prefix + " Adding " + g.getName()
								+ " to your inventory");
					} else {
						sender.sendMessage(prefix + " Could not find gun \""
								+ args[1] + "\"");
					}
					return true;
				}
				if (args[0].equalsIgnoreCase("giveAmmo")) {
					if (!sender.hasPermission("qualityarmory.give")) {
						sender.sendMessage(prefix + ChatColor.RED + S_NOPERM);
						return true;
					}
					if (args.length == 1) {
						sender.sendMessage(prefix
								+ " The ammo type is required");
						StringBuilder sb = new StringBuilder();
						sb.append("Valid Ammo types: ");
						for (Ammo g : ammoRegister.values()) {
							sb.append(g.getName() + ",");
						}
						sender.sendMessage(prefix + sb.toString());
						return true;
					}
					Ammo g = null;
					StringBuilder gunName = new StringBuilder();
					gunName.append(args[1]);
					// for (int j = 1; j < args.length; j++) {
					// gunName.append(args[j]);
					// if (j != args.length - 1)
					// gunName.append(" ");
					// }
					for (Entry<MaterialStorage, Ammo> e : ammoRegister
							.entrySet()) {
						if (e.getValue().getName()
								.equalsIgnoreCase(gunName.toString())) {
							g = e.getValue();
							break;
						}
					}
					if (g != null) {
						Player who = sender instanceof Player ? ((Player) sender)
								: null;
						if (args.length > 2)
							who = Bukkit.getPlayer(args[2]);
						if (who == null) {
							sender.sendMessage("That player is not online");
							return true;
						}

						ItemStack temp = ItemFact.getAmmo(g);
						who.getInventory().addItem(temp);
						sender.sendMessage(prefix + " Adding "
								+ temp.getAmount() + " " + g.getName()
								+ " to your inventory");
					} else {
						sender.sendMessage(prefix + " Could not find ammo \""
								+ args[1] + "\"");
					}
					return true;
				}
			}

			if (sender instanceof Player) {
				final Player player = (Player) sender;
				if (args.length >= 1 && args[0].equalsIgnoreCase("override")) {
					resourcepackReq.add(player.getUniqueId());
					sender.sendMessage(prefix
							+ " Overriding resoucepack requirement.");
					return true;
				}
				if (shouldSend
						&& !resourcepackReq.contains(player.getUniqueId())) {
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
				+ "/QA giveGun <Gun>: Gives the sender a gun");
		sender.sendMessage(ChatColor.GRAY
				+ "/QA giveAmmo <Ammo>: Gives the sender a stack of ammo (stack is defined by ammo type)");
		sender.sendMessage(ChatColor.GRAY
				+ "/QA craft: Opens the crafting inventory.");
	}

	public boolean isDuplicateGun(ItemStack is1, Player player) {
		for (ItemStack is : player.getInventory().getContents()) {
			if (is != null && is1 != null)
				if (is.getType() == is1.getType()
						&& is.getDurability() == is1.getDurability()) {
					if (is.getAmount() == is1.getAmount()
							|| is.getAmount() == 1)
						return true;
				}
		}
		return false;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void oninvClick(final InventoryClickEvent e) {
		if (e.getClickedInventory() != null
				&& e.getClickedInventory().getTitle()
						.equals(S_craftingBenchName)) {
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
							((Player) e.getWhoClicked()).playSound(e
									.getWhoClicked().getLocation(),
									Sound.BLOCK_ANVIL_USE, 0.7f, 1);
						} catch (Error e2) {
							((Player) e.getWhoClicked()).playSound(e
									.getWhoClicked().getLocation(), Sound
									.valueOf("ANVIL_USE"), 0.7f, 1);
						}
					} else {
						e.getWhoClicked().closeInventory();
						e.getWhoClicked().sendMessage(
								prefix + S_missingIngredients);
						try {
							((Player) e.getWhoClicked()).playSound(e
									.getWhoClicked().getLocation(),
									Sound.BLOCK_ANVIL_BREAK, 1, 1);
						} catch (Error e2) {
							((Player) e.getWhoClicked()).playSound(e
									.getWhoClicked().getLocation(), Sound
									.valueOf("ANVIL_BREAK"), 1, 1);
						}
					}
				} else if (isAmmo(e.getCurrentItem())) {
					Ammo g = getAmmo(e.getCurrentItem());
					if (lookForIngre((Player) e.getWhoClicked(), g)
							|| e.getWhoClicked().getGameMode() == GameMode.CREATIVE) {
						removeForIngre((Player) e.getWhoClicked(), g);
						AmmoUtil.addAmmo((Player) e.getWhoClicked(), g,
								g.getCraftingReturn());
						try {
							((Player) e.getWhoClicked()).playSound(e
									.getWhoClicked().getLocation(),
									Sound.BLOCK_ANVIL_USE, 0.7f, 1);
						} catch (Error e2) {
							((Player) e.getWhoClicked()).playSound(e
									.getWhoClicked().getLocation(), Sound
									.valueOf("ANVIL_USE"), 0.7f, 1);
						}
					} else {
						e.getWhoClicked().closeInventory();
						e.getWhoClicked().sendMessage(
								prefix + S_missingIngredients);
						try {
							((Player) e.getWhoClicked()).playSound(e
									.getWhoClicked().getLocation(),
									Sound.BLOCK_ANVIL_BREAK, 1, 1);
						} catch (Error e2) {
							((Player) e.getWhoClicked()).playSound(e
									.getWhoClicked().getLocation(), Sound
									.valueOf("ANVIL_BREAK"), 1, 1);
						}
					}
				}
			}
			return;
		}

		// player inv

		if ((e.getCurrentItem() != null && e.getCurrentItem().getDurability() == IronSightsToggleItem
				.getData())
				|| (e.getCursor() != null && e.getCursor().getDurability() == IronSightsToggleItem
						.getData())) {
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
														 * e.getCursor().getType(
														 * ) == guntype
														 */)
				&& (e.getCurrentItem() != null && isGun(e.getCurrentItem())/*
																			 * e.
																			 * getCurrentItem
																			 * (
																			 * )
																			 * .
																			 * getType
																			 * (
																			 * )
																			 * ==
																			 * guntype
																			 */)) {
			e.setCancelled(true);
			if ((e.getCurrentItem().getItemMeta().hasDisplayName() && e
					.getCurrentItem().getItemMeta().getDisplayName()
					.contains("Reloading"))
					|| (e.getCursor().getItemMeta().hasDisplayName() && e
							.getCursor().getItemMeta().getDisplayName()
							.contains("Reloading"))) {
				return;
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPickup(PlayerPickupItemEvent e) {
		if (isAmmo(e.getItem().getItemStack())) {
			if (shouldSend
					&& !resourcepackReq.contains(e.getPlayer().getUniqueId())) {
				sendPacket(e.getPlayer(), true);
			}
			AmmoUtil.addAmmo(e.getPlayer(), ammoRegister.get(MaterialStorage
					.getMS(e.getItem().getItemStack())), e.getItem()
					.getItemStack().getAmount());
			e.setCancelled(true);
			e.getItem().remove();
			try {
				e.getPlayer().playSound(e.getPlayer().getLocation(),
						Sound.BLOCK_LEVER_CLICK, 0.2f, 1);
			} catch (Error e2) {
				e.getPlayer().playSound(e.getPlayer().getLocation(),
						Sound.valueOf("CLICK"), 0.2f, 1);
			}
		}

		if (isGun(e.getItem().getItemStack())) {
			if (shouldSend
					&& !resourcepackReq.contains(e.getPlayer().getUniqueId())) {
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
							&& is.getDurability() == ings[i].getDurability()) {
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
					if (is.getType() == ings[i].getType()
							&& is.getDurability() == ings[i].getDurability()) {
						if (is.getAmount() > ings[i].getAmount()) {
							bb[i] = true;
							int slot = player.getInventory().first(is);
							is.setAmount(is.getAmount() - ings[i].getAmount());
							player.getInventory().setItem(slot, is);
						} else if (is.getAmount() == ings[i].getAmount()) {
							bb[i] = true;
							int slot = player.getInventory().first(is);
							player.getInventory().setItem(slot,
									new ItemStack(Material.AIR));
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
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK
				&& e.getClickedBlock().getType() == Material.ANVIL
				&& overrideAnvil && !e.getPlayer().isSneaking()) {
			if (shouldSend
					&& !resourcepackReq.contains(e.getPlayer().getUniqueId())) {
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
			if ((!isGun(e.getItem())) && !isAmmo(e.getItem())
					&& (!isIS(e.getItem()))) {
				if ((gunRegister.containsKey(MaterialStorage.getMS(e.getItem()
						.getType(), (int) e.getItem().getDurability() + 1)) || ammoRegister
						.containsKey(MaterialStorage.getMS(e.getItem()
								.getType(),
								(int) e.getItem().getDurability() + 1)))) {
					// If the item is not a gun, but the item below it is
					int goodDurib = e.getItem().getDurability() + 1;
					for (int i = 0; i < e.getItem().getType()
							.getMaxDurability(); i++) {
						if (gunRegister.containsKey(MaterialStorage.getMS(e
								.getItem().getType(), goodDurib))
								|| ammoRegister
										.containsKey(MaterialStorage
												.getMS(e.getItem().getType(),
														goodDurib))) {
							goodDurib++;
							continue;
						}
						break;
					}

					// if (e.getItem().getDurability() == 1) {
					ItemStack is = e.getItem();
					is.setDurability((short) goodDurib);
					e.getPlayer()
							.getInventory()
							.setItem(
									e.getPlayer().getInventory()
											.getHeldItemSlot(), is);
					// }
				}
				return;
			}
			ItemStack usedItem = e.getPlayer().getItemInHand();
			boolean offhand = false;
			if (Update19OffhandChecker.supportOffhand(e.getPlayer())) {
				try {
					if (e.getPlayer().getInventory().getItemInMainHand()
							.getDurability() == IronSightsToggleItem.getData()) {
						if (e.getAction() == Action.RIGHT_CLICK_AIR
								|| e.getAction() == Action.RIGHT_CLICK_BLOCK) {
							e.setCancelled(true);
							e.getPlayer()
									.getInventory()
									.setItemInMainHand(
											e.getPlayer().getInventory()
													.getItemInOffHand());
							e.getPlayer().getInventory().setItemInOffHand(null);
							return;
						} else {
							usedItem = e.getPlayer().getInventory()
									.getItemInOffHand();
							offhand = true;
						}
					}
				} catch (Error e2) {
				}
			}

			Gun g = getGun(usedItem);
			e.setCancelled(true);
			if (shouldSend
					&& !resourcepackReq.contains(e.getPlayer().getUniqueId())) {
				sendPacket(e.getPlayer(), true);
			}
			if (e.getAction() == Action.LEFT_CLICK_AIR
					|| e.getAction() == Action.LEFT_CLICK_BLOCK) {
				if (!enableDurability || ItemFact.getDamage(usedItem) > 0) {
					if (allowGunsInRegion(e.getPlayer().getLocation())) {
						g.shoot(e.getPlayer());
						if (enableDurability)
							if (offhand) {
								try {
									e.getPlayer()
											.getInventory()
											.setItemInOffHand(
													ItemFact.damage(g, usedItem));
								} catch (Error e2) {
								}
							} else {
								e.getPlayer().setItemInHand(
										ItemFact.damage(g, usedItem));
							}
						return;
					}
					// TODO: Verify that the gun is in the main hand.
					// Shouldn't work for offhand, but it should still
					// be checked later.
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

								if (e.getPlayer().getItemInHand().getItemMeta()
										.getDisplayName()
										.contains("Reloading."))
									return;

								if (Update19OffhandChecker
										.getItemStackOFfhand(e.getPlayer()) != null) {
									e.getPlayer()
											.getInventory()
											.addItem(
													Update19OffhandChecker
															.getItemStackOFfhand(e
																	.getPlayer()));
									Update19OffhandChecker.setOffhand(
											e.getPlayer(), null);
								}

								ItemStack tempremove = null;
								if (e.getPlayer().getInventory()
										.getItemInOffHand() != null)
									tempremove = e.getPlayer().getInventory()
											.getItemInOffHand();
								e.getPlayer()
										.getInventory()
										.setItemInOffHand(
												e.getPlayer().getInventory()
														.getItemInMainHand());
								if (tempremove != null) {
									ItemStack ironsights = new ItemStack(
											guntype, 1,
											(short) IronSightsToggleItem
													.getData());
									ItemMeta im = ironsights.getItemMeta();
									im.setDisplayName(IronSightsToggleItem
											.getItemName());
									im.setUnbreakable(true);
									im.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_UNBREAKABLE);
									ironsights.setItemMeta(im);
									e.getPlayer().getInventory()
											.setItemInMainHand(ironsights);
								}
							} catch (Error e2) {
								Bukkit.broadcastMessage(prefix
										+ "Ironsights not compatible for versions lower than 1.8. The server owner should set EnableIronSights to false in the plugin's config");
							}
						} else {

							if (!enableDurability
									|| ItemFact.getDamage(usedItem) > 0) {
								if (allowGunsInRegion(e.getPlayer()
										.getLocation())) {
									g.shoot(e.getPlayer());
									if (enableDurability)
										if (offhand) {
											e.getPlayer()
													.getInventory()
													.setItemInOffHand(
															ItemFact.damage(g,
																	usedItem));
										} else {
											e.getPlayer()
													.setItemInHand(
															ItemFact.damage(g,
																	usedItem));
										}
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
					if (g != null) {
						if (g.playerHasAmmo(e.getPlayer())) {
							g.reload(e.getPlayer());
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void swap(PlayerItemHeldEvent e) {
		ItemStack prev = e.getPlayer().getInventory()
				.getItem(e.getPreviousSlot());
		if (isIS(prev)) {
			try {
				e.getPlayer()
						.getInventory()
						.setItem(e.getPreviousSlot(),
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
		if (Bukkit.getVersion().contains("1.8")
				|| Bukkit.getVersion().contains("1.7")) {
			Bukkit.broadcastMessage(prefix
					+ " QualityArmory does not support versions older than 1.9, and may crash clients");
			Bukkit.broadcastMessage("Since there is no reason to stay on outdated updates, (1.8 has quite a number of exploits) update your server.");
			if (shouldSend) {
				shouldSend = false;
				Bukkit.broadcastMessage(prefix + ChatColor.RED
						+ " Disabling resoucepack.");
			}
		}
		if (sendOnJoin) {
			sendPacket(e.getPlayer(), false);
		} else {
			for (ItemStack i : e.getPlayer().getInventory().getContents()) {
				if (i != null && isGun(i)) {
					if (shouldSend
							&& !resourcepackReq.contains(e.getPlayer()
									.getUniqueId())) {
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

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onDrop(final PlayerDropItemEvent e) {
		if (e.getPlayer().isDead()) {
			ItemStack newone = e.getItemDrop().getItemStack();
			if (isGun(newone)/* newone.getType() == guntype */
					&& newone.getItemMeta().hasDisplayName()
					&& newone.getItemMeta().getDisplayName()
							.contains("Reloading")) {
				ItemMeta im = newone.getItemMeta();
				im.setDisplayName(ItemFact.getGunName(gunRegister
						.get((int) newone.getDurability())));
				newone.setItemMeta(im);
				e.getItemDrop().setItemStack(newone);
			}
			return;
		}

		if (isGun(e.getItemDrop().getItemStack())
				&& isDuplicateGun(e.getItemDrop().getItemStack(), e.getPlayer())) {
			e.setCancelled(true);
			return;
		}

		if (e.getPlayer().getItemInHand() != null
				&& isGun(e.getItemDrop().getItemStack()) /*
														 * e.getItemDrop().
														 * getItemStack
														 * ().getType() ==
														 * guntype
														 */) {
			if ((e.getItemDrop().getItemStack().getItemMeta().hasDisplayName() && e
					.getItemDrop().getItemStack().getItemMeta()
					.getDisplayName().contains("Reloading"))) {
				e.setCancelled(true);
				return;
			}
		}

		if (enableIronSightsON_RIGHT_CLICK) {
			if (e.getPlayer().getItemInHand() != null
					&& (isGun(e.getItemDrop().getItemStack()) || isIS(e
							.getItemDrop().getItemStack()))) {
				if (e.getItemDrop().getItemStack().getAmount() == 1) {
					try {
						boolean dealtWithDrop = false;
						if (e.getItemDrop().getItemStack().getDurability() == IronSightsToggleItem
								.getData()) {
							e.getItemDrop().setItemStack(
									e.getPlayer().getInventory()
											.getItemInOffHand());
							e.getPlayer().setItemInHand(
									e.getPlayer().getInventory()
											.getItemInOffHand());
							e.getPlayer().getInventory().setItemInOffHand(null);
							dealtWithDrop = true;
						}
						if (e.getPlayer().getItemInHand().getType() != Material.AIR
								&& e.getPlayer().getItemInHand()
										.getDurability() != e.getItemDrop()
										.getItemStack().getDurability()) {
							return;
						}
						final Gun g = getGun(e.getItemDrop().getItemStack());
						if (g != null) {
							e.setCancelled(true);
							if (GunUtil.hasAmmo(e.getPlayer(), g)) {
								if (!dealtWithDrop) {
									if (e.getPlayer().getItemInHand().getType() == Material.AIR) {
										e.getPlayer().setItemInHand(
												e.getItemDrop().getItemStack());
									} else {
										if ((g.getMaxBullets() - 1) == e
												.getPlayer().getItemInHand()
												.getAmount())
											return;
									}
								}
								new BukkitRunnable() {
									@Override
									public void run() {
										GunUtil.basicReload(g, e.getPlayer(),
												g.hasUnlimitedAmmo());
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
			if (isGun(e.getPlayer().getItemInHand())) {
				e.getPlayer()
						.getItemInHand()
						.setAmount(
								e.getPlayer().getItemInHand().getAmount()
										+ e.getItemDrop().getItemStack()
												.getAmount());
				e.getItemDrop().setItemStack(e.getPlayer().getItemInHand());
				e.getPlayer()
						.getInventory()
						.setItem(
								e.getPlayer().getInventory().getHeldItemSlot(),
								new ItemStack(Material.AIR));
			}
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

	public Gun getGun(ItemStack is) {
		return gunRegister.get(MaterialStorage.getMS(is.getType(),
				(int) is.getDurability()));
	}

	public Ammo getAmmo(ItemStack is) {
		return ammoRegister.get(MaterialStorage.getMS(is.getType(),
				(int) is.getDurability()));
	}

	public boolean isGun(ItemStack is) {
		return (is != null && gunRegister.containsKey(MaterialStorage.getMS(
				is.getType(), (int) is.getDurability())));
	}

	public boolean isAmmo(ItemStack is) {
		return (is != null && ammoRegister.containsKey(MaterialStorage.getMS(
				is.getType(), (int) is.getDurability())));
	}

	public boolean isIS(ItemStack is) {
		if (is != null && is.getType() == guntype
				&& is.getDurability() == (int) IronSightsToggleItem.getData())
			return true;
		return false;
	}
}
