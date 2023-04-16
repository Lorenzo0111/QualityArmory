package me.zombie_striker.qualityarmory;

import de.tr7zw.changeme.nbtapi.utils.MinecraftVersion;
import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.customitemmanager.CustomItemManager;
import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.customitemmanager.qa.AbstractCustomGunItem;
import me.zombie_striker.customitemmanager.qa.ItemBridgePatch;
import me.zombie_striker.customitemmanager.qa.versions.V1_13.CustomGunItem;
import me.zombie_striker.qualityarmory.ammo.Ammo;
import me.zombie_striker.qualityarmory.api.events.QAGunGiveEvent;
import me.zombie_striker.qualityarmory.api.QualityArmory;
import me.zombie_striker.qualityarmory.armor.ArmorObject;
import me.zombie_striker.qualityarmory.attachments.AttachmentBase;
import me.zombie_striker.qualityarmory.boundingbox.BoundingBoxManager;
import me.zombie_striker.qualityarmory.commands.QualityArmoryCommand;
import me.zombie_striker.qualityarmory.config.CommentYamlConfiguration;
import me.zombie_striker.qualityarmory.config.GunYMLCreator;
import me.zombie_striker.qualityarmory.config.GunYMLLoader;
import me.zombie_striker.qualityarmory.config.MessagesYML;
import me.zombie_striker.qualityarmory.guns.Gun;
import me.zombie_striker.qualityarmory.guns.projectiles.*;
import me.zombie_striker.qualityarmory.guns.reloaders.M1GarandReloader;
import me.zombie_striker.qualityarmory.guns.reloaders.SlideReloader;
import me.zombie_striker.qualityarmory.guns.utils.GunRefillerRunnable;
import me.zombie_striker.qualityarmory.handlers.*;
import me.zombie_striker.qualityarmory.guns.chargers.*;
import me.zombie_striker.qualityarmory.guns.reloaders.PumpactionReloader;
import me.zombie_striker.qualityarmory.guns.reloaders.SingleBulletReloader;
import me.zombie_striker.qualityarmory.hooks.MimicHookHandler;
import me.zombie_striker.qualityarmory.hooks.PlaceholderAPIHook;
import me.zombie_striker.qualityarmory.hooks.anticheat.AntiCheatHook;
import me.zombie_striker.qualityarmory.hooks.anticheat.MatrixHook;
import me.zombie_striker.qualityarmory.hooks.QuickShopHook;
import me.zombie_striker.qualityarmory.hooks.anticheat.VulcanHook;
import me.zombie_striker.qualityarmory.hooks.protection.ProtectionHandler;
import me.zombie_striker.qualityarmory.listener.QAListener;
import me.zombie_striker.qualityarmory.listener.Update19Events;
import me.zombie_striker.qualityarmory.listener.Update19resourcepackhandler;
import me.zombie_striker.qualityarmory.miscitems.ThrowableItems;
import me.zombie_striker.qualityarmory.miscitems.ThrowableItems.ThrowableHolder;
import me.zombie_striker.qualityarmory.npcs.Gunner;
import me.zombie_striker.qualityarmory.npcs.GunnerTrait;
import me.zombie_striker.qualityarmory.npcs_sentinel.SentinelQAHandler;
import me.zombie_striker.qualityarmory.utils.LocalUtils;
import me.zombie_striker.qualityarmory.utils.ParticleUtil;
import org.bukkit.*;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;

public class QAMain extends JavaPlugin {

    private String changelog = null;

    public static final int ViaVersionIdfor_1_8 = 106;
    private static final String SERVER_VERSION;

    private HashMap<String, String> craftingEntityNames = new HashMap<>();
    private HashMap<UUID, Location> recoilHelperMovedLocation = new HashMap<>();
    private HashMap<UUID, List<GunRefillerRunnable>> reloadingTasks = new HashMap<>();
    private HashMap<UUID, Long> sentResourcepack = new HashMap<>();
    private ArrayList<UUID> resourcepackReq = new ArrayList<>();
    private List<Gunner> gunners = new ArrayList<>();
    private List<String> namesToBypass = new ArrayList<>();
    private List<Material> interactableBlocks = new ArrayList<>();
    private List<Material> destructableBlocks = new ArrayList<Material>();
    public int regenDestructableBlocksAfter = -1;
    private boolean saveSettings = false;

    /**
     * List of all frequently used variables across the plugin.
     */
    private boolean DEBUG = false;
    private String prefix = ChatColor.GOLD + "[" + ChatColor.GRAY + "QualityArmory" + ChatColor.GOLD + "]" + ChatColor.RESET;


    /**
     * Represents all the settings used by the plugin, as specified in the config.yml.
     * If the setting will be accessed more than once per tick, it is recommended to keep the
     * returned instance/setting stored as a field.
     * <p>
     * Please do not store all values of an enum (e.g. Material) as part of keys of the setting. Instead,
     * create a separate YML/File to store that information.
     */
    private final HashMap<String, Object> settings = new HashMap<>();


    public MessagesYML messagesYml;
    public CommentYamlConfiguration resourcepackwhitelist;
    public String language = "en";
    public List<Scoreboard> coloredGunScoreboard = new ArrayList<Scoreboard>();

    static {
        String name = Bukkit.getServer().getClass().getName();
        name = name.substring(name.indexOf("craftbukkit.") + "craftbukkit.".length());
        name = name.substring(0, name.indexOf("."));
        SERVER_VERSION = name;
    }

    private FileConfiguration config;
    private File configFile;
    private boolean saveTheConfig = false;
    private boolean hasParties;
    private boolean hasViaRewind;
    private boolean hasViaVersion;
    private boolean hasProtocolLib;

    public static boolean isVersionHigherThan(int mainVersion, int secondVersion) {
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
        return sInt >= secondVersion;
    }

    public static void toggleNightVision(Player player, Gun g, boolean add) {
        if (add) {
            if (g.getZoomWhenIronSights() > 0) {
                currentlyScoping.add(player.getUniqueId());
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1200, g.getZoomWhenIronSights()));
            }
            if (g.hasNightVision()) {
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
                            && (g == null || g.hasNightVision())
                            && player.getPotionEffect(PotionEffectType.NIGHT_VISION).getAmplifier() == 3;
                } catch (Error | Exception e3452) {
                    for (PotionEffect pe : player.getActivePotionEffects())
                        if (pe.getType() == PotionEffectType.NIGHT_VISION)
                            potionEff = (g == null || g.hasNightVision()) && pe.getAmplifier() == 3;
                }
                if (potionEff)
                    player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                currentlyScoping.remove(player.getUniqueId());
            }
        }

    }

    public static void DEBUG(String message) {
        if (DEBUG)
            Bukkit.broadcast(prefix + ChatColor.GREEN + " [DEBUG] " + ChatColor.RESET + message, "qualityarmory.debugmessages");
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

    public static boolean lookForIngre(Player player, CustomBaseObject a) {
        return lookForIngre(player, a.getIngredientsRaw());
    }

    @SuppressWarnings("deprecation")
    public static boolean lookForIngre(Player player, Object[] ings) {
        if (ings == null)
            return true;
        boolean[] bb = new boolean[ings.length];
        for (ItemStack is : player.getInventory().getContents()) {
            if (is != null) {
                for (int i = 0; i < ings.length; i++) {
                    if (bb[i])
                        continue;
                    if (ings[i] instanceof ItemStack) {
                        ItemStack check = (ItemStack) ings[i];
                        if (is.getType() == check.getType()
                                && (check.getDurability() == 0 || is.getDurability() == check.getDurability())) {
                            if (is.getAmount() >= check.getAmount())
                                bb[i] = true;
                            break;
                        }
                    } else if (ings[i] instanceof String) {
                        CustomBaseObject base = QualityArmory.getCustomItemByName((String) ings[i]);
                        if (QualityArmory.getCustomItem(is) == base) {
                            bb[i] = true;
                            break;
                        }

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

    public static boolean removeForIngredient(Player player, CustomBaseObject a) {
        return removeForIngredient(player, a.getIngredientsRaw());
    }

    @SuppressWarnings("deprecation")
    public static boolean removeForIngredient(Player player, Object[] ingredients) {
        if (ingredients == null)
            return true;
        boolean[] bb = new boolean[ingredients.length];
        for (ItemStack is : player.getInventory().getContents()) {
            if (is != null) {
                CustomBaseObject obj = QualityArmory.getCustomItem(is);
                for (int i = 0; i < ingredients.length; i++) {
                    if (bb[i])
                        continue;
                    if (obj != null) {
                        if (ingredients[i] instanceof String && QualityArmory.getCustomItemByName((String) ingredients[i]) == obj) {
                            for (int slot = 0; slot < player.getInventory().getContents().length; slot++) {
                                if (player.getInventory().getItem(slot).equals(is)) {
                                    player.getInventory().setItem(slot, new ItemStack(Material.AIR));
                                    break;
                                }
                            }
                            bb[i] = true;
                            break;
                        }
                    } else if (ingredients[i] instanceof ItemStack) {
                        ItemStack check = (ItemStack) ingredients[i];
                        if (is.getType() == check.getType() && is.getDurability() == check.getDurability()) {
                            if (is.getAmount() > check.getAmount()) {
                                bb[i] = true;
                                int slot = player.getInventory().first(is);
                                is.setAmount(is.getAmount() - check.getAmount());
                                player.getInventory().setItem(slot, is);
                            } else if (is.getAmount() == check.getAmount()) {
                                bb[i] = true;
                                int slot = player.getInventory().first(is);
                                player.getInventory().setItem(slot, new ItemStack(Material.AIR));
                            }
                            break;
                        }
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


    private static boolean addToGUI(CustomBaseObject obj, Inventory gui, boolean shop) {
        if (shop && (obj.getPrice() < 0 || !obj.isEnableShop()))
            return false;
        if (!shop && obj.getIngredientsRaw() == null)
            return false;
        try {
            ItemStack is = CustomItemManager.getItemType("gun").getItem(obj.getItemData().getMat(), obj.getItemData().getData(), obj.getItemData().getVariant());
            is.setAmount(obj.getCraftingReturn());
            if (is.getAmount() <= 0)
                is.setAmount(1);
            ItemMeta im = is.getItemMeta();
            List<String> lore = im.hasLore() ? im.getLore() : new ArrayList<>();
            if (shop) {
                lore.addAll(OLD_ItemFact.addShopLore(obj));
            } else {
                lore.addAll(OLD_ItemFact.getCraftingLore(obj));
            }
            im.setLore(lore);
            is.setItemMeta(im);
            gui.addItem(is);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static Inventory createCustomInventory(int page, boolean shopping) {
        Inventory shopMenu = Bukkit.createInventory(null, 9 * 6, (shopping ? S_shopName : S_craftingBenchName) + page);
        List<Gun> gunslistr = new ArrayList<>(gunRegister.values());
        Collections.sort(gunslistr);
        int basei = 0;
        int index = (page * 9 * 5);
        int maxIndex = (index + (9 * 5));

        shopMenu.setItem((9 * 6) - 1 - 8, prevButton);
        shopMenu.setItem((9 * 6) - 1, nextButton);

        if (basei + gunslistr.size() < index) {
            basei += gunslistr.size();
        } else {
            for (Gun g : gunslistr) {
                if (basei < index) {
                    basei++;
                    continue;
                }
                basei++;
                if (index >= maxIndex)
                    break;
                if (addToGUI(g, shopMenu, shopping))
                    index++;
            }
        }
        if (basei + ammoRegister.values().size() < index) {
            basei += ammoRegister.values().size();
        } else {
            for (CustomBaseObject ammo : ammoRegister.values()) {
                if (basei < index) {
                    basei++;
                    continue;
                }
                basei++;
                if (index >= maxIndex)
                    break;
                if (addToGUI(ammo, shopMenu, shopping))
                    index++;
            }
        }
        if (basei + miscRegister.values().size() < index) {
            basei += miscRegister.values().size();
        } else {
            for (CustomBaseObject abo : miscRegister.values()) {
                if (basei < index) {
                    basei++;
                    continue;
                }
                basei++;
                if (index >= maxIndex)
                    break;
                if (addToGUI(abo, shopMenu, shopping))
                    index++;
            }
        }
        if (basei + armorRegister.values().size() < index) {
            basei += armorRegister.values().size();
        } else {
            for (ArmorObject armor : armorRegister.values()) {
                if (basei < index) {
                    basei++;
                    continue;
                }
                basei++;
                if (index >= maxIndex)
                    break;
                if (addToGUI(armor, shopMenu, shopping))
                    index++;
            }
        }
        return shopMenu;
    }

    public static void registerCraftEntityNames(HashMap<MaterialStorage, ?> regMaps) {
        if (null != regMaps && !regMaps.isEmpty()) {
            for (Object item : regMaps.values()) {
                try {
                    Object[] itemStacks = ((CustomBaseObject) item).getIngredientsRaw();
                    if (null != itemStacks && itemStacks.length > 0) {
                        for (Object itemStack : itemStacks) {
                            String itemName = ((ItemStack) itemStack).getType().name();
                            String showName = LocalUtils.colorize((String) QAMain.messagesYml.a("EntityType." + itemName, itemName));
                            craftingEntityNames.put(itemName, showName);
                        }
                    }
                } catch (Exception e) {
                    //
                }
            }
        }
    }

    @Override
    public void onDisable() {
        for (Entry<MaterialStorage, CustomBaseObject> misc : miscRegister.entrySet()) {
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

        try {
            resourcepackwhitelist.save(new File(getDataFolder(), "resourcepackwhitelist.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Object a(String path, Object def) {
        if (getConfig().contains(path)) {
            return getConfig().get(path);
        }
        getConfig().set(path, def);
        saveTheConfig = true;
        return def;
    }

    @Override
    public void onEnable() {
        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdirs();
        }

        MinecraftVersion.replaceLogger(this.getLogger());
        MinecraftVersion.disableUpdateCheck();

        ProtectionHandler.init();

        AntiCheatHook.registerHook("Matrix", MatrixHook.class);
        AntiCheatHook.registerHook("Vulcan", VulcanHook.class);

        if (Bukkit.getPluginManager().isPluginEnabled("QuickShop")) {
            Bukkit.getPluginManager().registerEvents(new QuickShopHook(), this);
            this.getLogger().info("Found QuickShop. Loaded support");
        }

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderAPIHook().register();
            this.getLogger().info("Found PlaceholderAPI. Loaded support");
        }

        try {
            if (Bukkit.getPluginManager().isPluginEnabled("Mimic")) {
                MimicHookHandler.register();
                this.getLogger().info("Found Mimic. Loaded support");
            }
        } catch (Exception | Error ignored) {
        }

        try {
            ParticleUtil.initValues();
        } catch (Error | Exception e5) {
        }
        if (Bukkit.getPluginManager().isPluginEnabled("ItemBridge")) {
            ItemBridgePatch.setup(this);
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

        if (getServer().getPluginManager().isPluginEnabled("Citizens")) {
            try {
                // Register your trait with Citizens.
                net.citizensnpcs.api.CitizensAPI.getTraitFactory()
                        .registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(GunnerTrait.class));
            } catch (Error | Exception e4) {
                getLogger().log(Level.WARNING, "Citizens 2.0 failed to register gunner trait (Ignore this.)");
            }
        }


        if (Bukkit.getPluginManager().isPluginEnabled("ChestShop"))
            Bukkit.getPluginManager().registerEvents(new ChestShopHandler(), this);

        QualityArmoryCommand qac = new QualityArmoryCommand(this);
        getCommand("QualityArmory").setExecutor(qac);
        getCommand("QualityArmory").setTabCompleter(qac);
        Bukkit.getPluginManager().registerEvents(new QAListener(), this);
        Bukkit.getPluginManager().registerEvents(new Update19resourcepackhandler(), this);
        Bukkit.getPluginManager().registerEvents(new Update19Events(), this);

        try {
            if ((boolean) getSettingIfPresent("autoUpdate",true))
                GithubUpdater.autoUpdate(this, "ZombieStriker", "QualityArmory", "QualityArmory.jar");
        } catch (Exception e) {
        }

        Metrics metrics = new Metrics(this, 1699);

        metrics.addCustomChart(new Metrics.SimplePie("GunCount", () -> gunRegister.size() + ""));
        metrics.addCustomChart(
                new Metrics.SimplePie("uses_default_resourcepack", () -> overrideURL + ""));
        metrics.addCustomChart(
                new Metrics.SimplePie("has_an_expansion_pack", () -> (expansionPacks.size() > 0) + ""));
    }

    @SuppressWarnings({"unchecked", "deprecation"})
    public void reloadVals() {
        reloadConfig();
        DEBUG = (boolean) a("ENABLE-DEBUG", false);

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
        new SlideReloader();
        new M1GarandReloader();

        new MiniNukeProjectile();
        new ExplodingRoundProjectile();
        new HomingRocketProjectile();
        new RocketProjectile();
        new FireProjectile();

        interactableBlocks.clear();
        craftingEntityNames.clear();

        // attachmentRegister.clear();
//Chris: Support more language file lang/message_xx.yml
        language = (String) a("language", "en");
        File langFolder = new File(getDataFolder(), "lang");
        if (langFolder.exists() && !langFolder.isDirectory()) {
            langFolder.delete();
        }
        langFolder.mkdir();
        messagesYml = new MessagesYML(language, new File(langFolder, "message_" + language + ".yml"));
        prefix = LocalUtils.colorize((String) messagesYml.a("Prefix", prefix));

        resourcepackwhitelist = CommentYamlConfiguration.loadConfiguration(new File(getDataFolder(), "resourcepackwhitelist.yml"));
        namesToBypass = (List<String>) resourcepackwhitelist.getOrSet("Names_Of_players_to_bypass", namesToBypass);


        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveDefaultConfig();
            reloadConfig();
        }

        if (getServer().getPluginManager().isPluginEnabled("Parties"))
            hasParties = true;
        if (Bukkit.getPluginManager().isPluginEnabled("ViaRewind"))
            hasViaRewind = true;
        if (Bukkit.getPluginManager().isPluginEnabled("ViaVersion"))
            hasViaVersion = true;
        if (getServer().getPluginManager().isPluginEnabled("ProtocolLib")) {
            hasProtocolLib = true;
        }

        if (getServer().getPluginManager().isPluginEnabled("Sentinel"))
            try {
                org.mcmonkey.sentinel.SentinelPlugin.integrations.add(new SentinelQAHandler());
            } catch (Error | Exception e4) {
            }


        // Skull texture
        GunYMLLoader.loadAmmo(this);
        GunYMLLoader.loadMisc(this);
        GunYMLLoader.loadGuns(this);
        GunYMLLoader.loadAttachments(this);
        GunYMLLoader.loadArmor(this);

        BoundingBoxManager.initEntityTypeBoundingBoxes();
    }

    public ItemStack[] convertIngredients(List<String> e) {
        Object[] list = convertIngredientsRaw(e);
        ItemStack[] li = new ItemStack[list.length];
        for (int i = 0; i < list.length; i++) {
            if (list[i] instanceof ItemStack) {
                li[i] = (ItemStack) list[i];
            }
        }
        return li;

    }

    @SuppressWarnings("deprecation")
    public Object[] convertIngredientsRaw(List<String> e) {
        Object[] list = new Object[e.size()];
        for (int i = 0; i < e.size(); i++) {
            ItemStack temp = null;
            if (!e.get(i).contains(",")) {
                list[i] = e.get(i);
                continue;
            }
            String[] k = e.get(i).split(",");

            try {
                temp = new ItemStack(Material.matchMaterial(k[0]));
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            if (temp == null)
                continue;
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



    @Override
    public void reloadConfig() {
        if (configFile == null) {
            configFile = new File(this.getDataFolder(), "config.yml");
            if (!this.getDataFolder().exists())
                this.getDataFolder().mkdirs();
            if (!configFile.exists()) {
                try {
                    configFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        config = CommentYamlConfiguration.loadConfiguration(configFile);
    }

    @Override
    public FileConfiguration getConfig() {
        if (this.config == null) {
            this.reloadConfig();
        }
        return this.config;
    }

    @Override
    public void saveConfig() {
        if (this.config == null) {
            this.reloadConfig();
        }
        try {
            this.config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadSettings() {
        FileConfiguration config = getConfig();
        for (String key : config.getKeys(false)) {
            loadConfigurationSection(key, config, config.getConfigurationSection(key));
        }


    }

    public void loadConfigurationSection(String path, FileConfiguration config, ConfigurationSection section) {
        for (String key : section.getKeys(false)) {
            if (config.isConfigurationSection(path + "." + key)) {
                loadConfigurationSection(path + "." + key, config, config.getConfigurationSection(path + "." + key));
            } else {
                settings.put(path + "." + key, config.get(path + "." + key));
            }
        }
    }

    public void saveSettings() {
        for (Entry<String, Object> entry : settings.entrySet()) {
            getConfig().set(entry.getKey(), entry.getValue());
        }
        saveConfig();
    }

    public HashMap<String, Object> getSettings() {
        return settings;
    }

    public Object getSetting(String key) {
        return getSettings().get(key);
    }

    public boolean getSettingBoolean(String key) {
        return (boolean) getSetting(key);
    }

    public String getSettingString(String key) {
        return (String) getSetting(key);
    }

    public int getSettingInt(String key) {
        return (int) getSetting(key);
    }

    public double getSettingDouble(String key) {
        return (double) getSetting(key);
    }

    public Object getSettingIfPresent(String key, Object defaultValue) {
        if (getSettings().containsKey(key))
            return getSettings().get(key);
        getSettings().put(key, defaultValue);
        saveSettings = true;
        return defaultValue;
    }
}
