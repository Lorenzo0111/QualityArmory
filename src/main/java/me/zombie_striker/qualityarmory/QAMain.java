package me.zombie_striker.qualityarmory;

import de.tr7zw.changeme.nbtapi.utils.MinecraftVersion;
import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.customitemmanager.qa.ItemBridgePatch;
import me.zombie_striker.qualityarmory.api.QualityArmory;
import me.zombie_striker.qualityarmory.boundingbox.BoundingBoxManager;
import me.zombie_striker.qualityarmory.commands.QualityArmoryCommand;
import me.zombie_striker.qualityarmory.config.CommentYamlConfiguration;
import me.zombie_striker.qualityarmory.config.MessagesYML;
import me.zombie_striker.qualityarmory.handlers.*;
import me.zombie_striker.qualityarmory.hooks.MimicHookHandler;
import me.zombie_striker.qualityarmory.hooks.PlaceholderAPIHook;
import me.zombie_striker.qualityarmory.hooks.QuickShopHook;
import me.zombie_striker.qualityarmory.hooks.anticheat.AntiCheatHook;
import me.zombie_striker.qualityarmory.hooks.anticheat.MatrixHook;
import me.zombie_striker.qualityarmory.hooks.anticheat.VulcanHook;
import me.zombie_striker.qualityarmory.hooks.protection.ProtectionHandler;
import me.zombie_striker.qualityarmory.interfaces.IEconomy;
import me.zombie_striker.qualityarmory.interfaces.IHandler;
import me.zombie_striker.qualityarmory.interfaces.ISettingsReloader;
import me.zombie_striker.qualityarmory.listener.QAListener;
import me.zombie_striker.qualityarmory.npcs.Gunner;
import me.zombie_striker.qualityarmory.npcs.GunnerTrait;
import me.zombie_striker.qualityarmory.npcs_sentinel.SentinelQAHandler;
import me.zombie_striker.qualityarmory.utils.BlockCollisionUtil;
import me.zombie_striker.qualityarmory.utils.LocalUtils;
import me.zombie_striker.qualityarmory.utils.ParticleUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;

public class QAMain extends JavaPlugin {

    private String changelog = null;

    public static final int ViaVersionIdfor_1_8 = 106;
    private static final String SERVER_VERSION;

    private HashMap<String, String> craftingEntityNames = new HashMap<>();
    private HashMap<UUID, Location> recoilHelperMovedLocation = new HashMap<>();
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

    private final List<CustomBaseObject> customItems = new ArrayList<>();

    /**
     * Represents all the settings used by the plugin, as specified in the config.yml.
     * If the setting will be accessed more than once per tick, it is recommended to keep the
     * returned instance/setting stored as a field.
     * <p>
     * Please do not store all values of an enum (e.g. Material) as part of keys of the setting. Instead,
     * create a separate YML/File to store that information.
     */
    private final HashMap<String, Object> settings = new HashMap<>();

    /**
     * Everything that requires settings that may be changed in game should be in this list.
     */
    private final List<ISettingsReloader> reloadableSettingsInstances = new LinkedList<>();


    private MessagesYML messagesYml;
    public CommentYamlConfiguration resourcepackwhitelist;
    private String language = "en";
    private List<Scoreboard> coloredGunScoreboard = new ArrayList<Scoreboard>();

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
    private boolean hasVault;

    private IEconomy economyHandler;
    private BulletSwayHandler bulletSwayHandler;
    private BlockCollisionUtil blockCollisionHandler;
    private BulletHandler bulletHandler;
    private GunDataHandler gunDataHandler;
    private ControlHandler controlHandler;
    private KeyFrameGunHandler keyFrameGunHandler;
    private final List<IHandler> handlers = new ArrayList<>();

    public BulletHandler getBulletHandler() {
        return bulletHandler;
    }

    public BulletSwayHandler getBulletSwayHandler() {
        return bulletSwayHandler;
    }

    public BlockCollisionUtil getBlockCollisionHandler() {
        return blockCollisionHandler;
    }

    public static boolean isVersionHigherThan(int mainVersion, int secondVersion) {
        String firstChar = SERVER_VERSION.substring(1, 2);
        int fInt = Integer.parseInt(firstChar);
        if (fInt < mainVersion) return false;
        StringBuilder secondChar = new StringBuilder();
        for (int i = 3; i < 10; i++) {
            if (SERVER_VERSION.charAt(i) == '_' || SERVER_VERSION.charAt(i) == '.') break;
            secondChar.append(SERVER_VERSION.charAt(i));
        }

        int sInt = Integer.parseInt(secondChar.toString());
        return sInt >= secondVersion;
    }

    public void DEBUG(String message) {
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

    @SuppressWarnings("deprecation")
    public static boolean lookForIngre(Player player, Object[] ings) {
        if (ings == null) return true;
        boolean[] bb = new boolean[ings.length];
        for (ItemStack is : player.getInventory().getContents()) {
            if (is != null) {
                for (int i = 0; i < ings.length; i++) {
                    if (bb[i]) continue;
                    if (ings[i] instanceof ItemStack) {
                        ItemStack check = (ItemStack) ings[i];
                        if (is.getType() == check.getType() && (check.getDurability() == 0 || is.getDurability() == check.getDurability())) {
                            if (is.getAmount() >= check.getAmount()) bb[i] = true;
                            break;
                        }
                    } else if (ings[i] instanceof String) {
                        CustomBaseObject base = QualityArmory.getInstance().getCustomItemByName((String) ings[i]);
                        if (QualityArmory.getInstance().getCustomItem(is) == base) {
                            bb[i] = true;
                            break;
                        }

                    }
                }
            }
        }
        for (boolean b : bb) {
            if (!b) return false;
        }
        return true;
    }

    @SuppressWarnings("deprecation")
    public static boolean removeForIngredient(Player player, Object[] ingredients) {
        if (ingredients == null) return true;
        boolean[] bb = new boolean[ingredients.length];
        for (ItemStack is : player.getInventory().getContents()) {
            if (is != null) {
                CustomBaseObject obj = QualityArmory.getInstance().getCustomItem(is);
                for (int i = 0; i < ingredients.length; i++) {
                    if (bb[i]) continue;
                    if (obj != null) {
                        if (ingredients[i] instanceof String && QualityArmory.getInstance().getCustomItemByName((String) ingredients[i]) == obj) {
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
            if (!b) return false;
        }
        return true;
    }

    public static MaterialStorage m(int d) {
        return MaterialStorage.getMS(Material.DIAMOND_AXE, d, 0);
    }

    @Override
    public void onDisable() {
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

        if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            this.hasVault = true;
            this.economyHandler = new EconHandler();
            this.economyHandler.setupEconomy();
            this.getLogger().info("Found Vault. Loaded support");
        }
        if (Bukkit.getPluginManager().isPluginEnabled("Mimic")) {
            MimicHookHandler.register();
            this.getLogger().info("Found Mimic. Loaded support");
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
                net.citizensnpcs.api.CitizensAPI.getTraitFactory().registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(GunnerTrait.class));
            } catch (Error | Exception e4) {
                getLogger().log(Level.WARNING, "Citizens 2.0 failed to register gunner trait (Ignore this.)");
            }
        }

        if (getServer().getPluginManager().isPluginEnabled("Parties")) hasParties = true;
        if (Bukkit.getPluginManager().isPluginEnabled("ViaRewind")) hasViaRewind = true;
        if (Bukkit.getPluginManager().isPluginEnabled("ViaVersion")) hasViaVersion = true;
        if (getServer().getPluginManager().isPluginEnabled("ProtocolLib")) {
            hasProtocolLib = true;
        }

        if (getServer().getPluginManager().isPluginEnabled("Sentinel")) try {
            org.mcmonkey.sentinel.SentinelPlugin.integrations.add(new SentinelQAHandler());
        } catch (Error | Exception e4) {
        }

        if (Bukkit.getPluginManager().isPluginEnabled("ChestShop")) this.handlers.add(new ChestShopHandler());

        QualityArmoryCommand qac = new QualityArmoryCommand(this);
        getCommand("QualityArmory").setExecutor(qac);
        getCommand("QualityArmory").setTabCompleter(qac);

        QAListener qaListener = new QAListener(this);
        this.reloadableSettingsInstances.add(qaListener);
        Bukkit.getPluginManager().registerEvents(qaListener, this);

        this.handlers.add(this.bulletSwayHandler = new BulletSwayHandler());
        this.handlers.add(this.blockCollisionHandler = new BlockCollisionUtil());
        this.handlers.add(this.bulletHandler = new BulletHandler(this));
        this.handlers.add(this.gunDataHandler = new GunDataHandler());
        this.handlers.add(this.controlHandler = new ControlHandler());
        this.handlers.add(this.keyFrameGunHandler = new KeyFrameGunHandler());

        this.handlers.add(new InvisibleBlockForAutomaticHandler());

        try {
            if ((boolean) getSettingIfPresent("autoUpdate", true))
                GithubUpdater.autoUpdate(this, "ZombieStriker", "QualityArmory", "QualityArmory.jar");
        } catch (Exception e) {
        }

        Metrics metrics = new Metrics(this, 1699);


        for (IHandler handler : handlers) {
            handler.init(this);
        }
    }

    @SuppressWarnings({"unchecked", "deprecation"})
    public void reloadVals() {
        reloadConfig();
        DEBUG = (boolean) getSettingIfPresent("ENABLE-DEBUG", false);

        interactableBlocks.clear();
        craftingEntityNames.clear();

        // attachmentRegister.clear();
//Chris: Support more language file lang/message_xx.yml
        language = (String) getSettingIfPresent("language", "en");
        File langFolder = new File(getDataFolder(), "lang");
        if (langFolder.exists() && !langFolder.isDirectory()) {
            langFolder.delete();
        }
        langFolder.mkdir();
        messagesYml = new MessagesYML(this, language, new File(langFolder, "message_" + language + ".yml"));
        prefix = LocalUtils.colorize((String) messagesYml.getOrSet("Prefix", prefix));

        resourcepackwhitelist = CommentYamlConfiguration.loadConfiguration(new File(getDataFolder(), "resourcepackwhitelist.yml"));
        namesToBypass = (List<String>) resourcepackwhitelist.getOrSet("Names_Of_players_to_bypass", namesToBypass);


        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveDefaultConfig();
            reloadConfig();
        }

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
            if (temp == null) continue;
            if (k.length > 1) temp.setDurability(Short.parseShort(k[1]));
            if (k.length > 2) temp.setAmount(Integer.parseInt(k[2]));
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
            if (!this.getDataFolder().exists()) this.getDataFolder().mkdirs();
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
        if (getSettings().containsKey(key)) return getSettings().get(key);
        getSettings().put(key, defaultValue);
        saveSettings = true;
        return defaultValue;
    }

    public void registerSettingReloader(ISettingsReloader settingsReloader) {
        this.reloadableSettingsInstances.add(settingsReloader);
    }

    public String getPrefix() {
        return prefix;
    }

    public boolean hasParties() {
        return hasParties;
    }

    public boolean hasProtocolLib() {
        return hasProtocolLib;
    }

    public boolean hasViaRewind() {
        return hasViaRewind;
    }

    public boolean hasViaVersion() {
        return hasViaVersion;
    }

    public boolean hasVault() {
        return hasVault;
    }

    public List<CustomBaseObject> getCustomItems() {
        return customItems;
    }

    public void registerCustomItem(CustomBaseObject customItem) {
        this.customItems.add(customItem);
    }

    public void setSetting(ConfigKey setting, Object val) {
        this.settings.put(setting.getKey(), val);
        saveSettings = true;
    }

    public IEconomy getEconHandler() {
        return economyHandler;
    }

    public List<IHandler> getHandlers() {
        return handlers;
    }

    public GunDataHandler getGunDataHandler() {
        return gunDataHandler;
    }

    public ControlHandler getControlHandler() {
        return controlHandler;
    }

    public KeyFrameGunHandler getKeyFrameGunHandler() {
        return keyFrameGunHandler;
    }

    public MessagesYML getMessagesYml() {
        return messagesYml;
    }
}
