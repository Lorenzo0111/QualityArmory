package me.zombie_striker.qg;

import me.zombie_striker.qg.handlers.UnbreakableItemsTask;
import me.zombie_striker.qg.commands.CommandRegistry;
import me.zombie_striker.qg.commands.Commands;
import org.bukkit.command.PluginCommand;
import me.zombie_striker.qg.utils.ErrorReporter;
import me.zombie_striker.qg.utils.Compat;
import com.cryptomorin.xseries.XPotion;
import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.reflection.XReflection;
import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.utils.MinecraftVersion;
import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.customitemmanager.CustomItemManager;
import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.customitemmanager.OLD_ItemFact;
import me.zombie_striker.customitemmanager.qa.AbstractCustomGunItem;
import me.zombie_striker.customitemmanager.qa.ItemBridgePatch;
import me.zombie_striker.customitemmanager.qa.versions.V1_13.CustomGunItem;
import me.zombie_striker.qg.ammo.Ammo;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.armor.ArmorObject;
import me.zombie_striker.qg.boundingbox.BoundingBoxManager;
import me.zombie_striker.qg.config.*;
import me.zombie_striker.qg.crafting.Crafting;
import me.zombie_striker.qg.state.*;
import me.zombie_striker.qg.gui.MenuManager;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.guns.chargers.*;
import me.zombie_striker.qg.guns.projectiles.*;
import me.zombie_striker.qg.guns.reloaders.M1GarandReloader;
import me.zombie_striker.qg.guns.reloaders.PumpactionReloader;
import me.zombie_striker.qg.guns.reloaders.SingleBulletReloader;
import me.zombie_striker.qg.guns.reloaders.SlideReloader;
import me.zombie_striker.qg.guns.utils.GunRefillerRunnable;
import me.zombie_striker.qg.handlers.*;
import me.zombie_striker.qg.hooks.MimicHookHandler;
import me.zombie_striker.qg.hooks.PlaceholderAPIHook;
import me.zombie_striker.qg.hooks.QuickShopHook;
import me.zombie_striker.qg.hooks.anticheat.AntiCheatHook;
import me.zombie_striker.qg.hooks.anticheat.MatrixHook;
import me.zombie_striker.qg.hooks.anticheat.VulcanHook;
import me.zombie_striker.qg.hooks.protection.ProtectionHandler;
import me.zombie_striker.qg.listener.QAListener;
import me.zombie_striker.qg.miscitems.ThrowableItemRegistry;
import me.zombie_striker.qg.npcs.Gunner;
import me.zombie_striker.qg.npcs.GunnerTrait;
import me.zombie_striker.qg.npcs_sentinel.SentinelQAHandler;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.*;
import java.util.*;
import java.util.logging.Level;

public class QAMain extends JavaPlugin {


    /** @deprecated Use {@link #getItemRegistry()}. Kept for addons; it is the same map. */
    @Deprecated
    public static HashMap<MaterialStorage, Gun> gunRegister = new LinkedHashMap<>();
    /** @deprecated Use {@link #getItemRegistry()}. Kept for addons; it is the same map. */
    @Deprecated
    public static HashMap<MaterialStorage, Ammo> ammoRegister = new LinkedHashMap<>();
    /** @deprecated Use {@link #getItemRegistry()}. Kept for addons; it is the same map. */
    @Deprecated
    public static HashMap<MaterialStorage, CustomBaseObject> miscRegister = new LinkedHashMap<>();
    /** @deprecated Use {@link #getItemRegistry()}. Kept for addons; it is the same map. */
    @Deprecated
    public static HashMap<MaterialStorage, ArmorObject> armorRegister = new LinkedHashMap<>();
    /** @deprecated Use {@link #getItemRegistry()}. Kept for addons; it is the same list. */
    @Deprecated
    public static ArrayList<MaterialStorage> expansionPacks = new ArrayList<>();
    /** @deprecated Use {@link #getPlayerState()}. Kept for addons; it is the same map. */
    @Deprecated
    public static HashMap<UUID, List<GunRefillerRunnable>> reloadingTasks = new HashMap<>();

    private static QAMain main;

    private TreeFellerHandler tfh = null;
    private MainConfig mainConfig;
    private MessagesConfig messages;
    private MenuManager menuManager;
    private final ItemRegistry itemRegistry = new ItemRegistry();
    private final PlayerStateManager playerState = new PlayerStateManager();
    private final Integrations integrations = new Integrations();
    private final BlockRules blockRules = new BlockRules();
    private ResourcepackManager resourcepacks;
    private final List<Gunner> gunners = new ArrayList<>();
    private final List<Scoreboard> glowScoreboards = new ArrayList<>();

    public static QAMain getInstance() {
        return main;
    }

    public static MainConfig getConfiguration() {
        return getInstance().mainConfig;
    }

    public static MessagesConfig getMessages() {
        return getInstance().messages;
    }

    public static ItemRegistry getItemRegistry() {
        return getInstance().itemRegistry;
    }

    public static PlayerStateManager getPlayerState() {
        return getInstance().playerState;
    }

    public static ResourcepackManager getResourcepacks() {
        return getInstance().resourcepacks;
    }

    public static Integrations getIntegrations() {
        return getInstance().integrations;
    }

    public static BlockRules getBlockRules() {
        return getInstance().blockRules;
    }

    /**
     * @return Citizens gunner NPCs, disposed when the plugin is disabled.
     */
    public List<Gunner> getGunners() {
        return gunners;
    }

    /**
     * @return Scoreboards that have the glow color teams registered.
     */
    public List<Scoreboard> getGlowScoreboards() {
        return glowScoreboards;
    }

    /** {@code LivingEntity#getPotionEffect} (1.11+). */
    private static final int[] VEHICLE_ITEM_IDS = {80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 92, 94, 95, 96, 97, 104, 111, 112, 114, 115, 116};
    private static final boolean GET_POTION_EFFECT = Compat.hasMethod("org.bukkit.entity.LivingEntity", "getPotionEffect", "org.bukkit.potion.PotionEffectType");

    public static boolean isVersionHigherThan(int mainVersion, int secondVersion) {
        return XReflection.supports(secondVersion);
    }

    public static void toggleNightvision(Player player, Gun g, boolean add) {
        if (add) {
            if (g.getZoomWhenIronSights() > 0) {
                getPlayerState().getScoping().add(player.getUniqueId());
                player.addPotionEffect(new PotionEffect(XPotion.SLOWNESS.getPotionEffectType(), 1200, g.getZoomWhenIronSights()));
            }
            if (g.hasnightVision()) {
                getPlayerState().getScoping().add(player.getUniqueId());
                player.addPotionEffect(new PotionEffect(XPotion.NIGHT_VISION.getPotionEffectType(), 1200, 3));
            }
        } else {
            if (getPlayerState().getScoping().contains(player.getUniqueId())) {
                if (player.hasPotionEffect(XPotion.SLOWNESS.getPotionEffectType()) && (g == null || g.getZoomWhenIronSights() > 0))
                    player.removePotionEffect(XPotion.SLOWNESS.getPotionEffectType());
                boolean potionEff = false;
                if (GET_POTION_EFFECT) {
                    potionEff = player.hasPotionEffect(PotionEffectType.NIGHT_VISION)
                            && (g == null || g.hasnightVision())
                            && player.getPotionEffect(PotionEffectType.NIGHT_VISION).getAmplifier() == 3;
                } else {
                    for (PotionEffect pe : player.getActivePotionEffects())
                        if (pe.getType() == PotionEffectType.NIGHT_VISION)
                            potionEff = (g == null || g.hasnightVision()) && pe.getAmplifier() == 3;
                }
                if (potionEff)
                    player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                getPlayerState().getScoping().remove(player.getUniqueId());
            }
        }

    }

    public static void DEBUG(String message) {
        if (getConfiguration().debug.enabled && getMessages() != null)
            Bukkit.broadcast(getMessages().general.prefix + ChatColor.GREEN + " [DEBUG] " + ChatColor.RESET + message, "qualityarmory.debugmessages");
    }

    public static Scoreboard registerGlowTeams(Scoreboard sb) {
        if (sb.getTeam("QA_RED") == null) {
            for (ChatColor c : ChatColor.values()) {
                if (!c.isColor()) continue;

                String teamName = "QA_" + c.name();
                if (sb.getTeam(teamName) == null) {
                    Team team = sb.registerNewTeam(teamName);
                    team.setColor(c);
                }
            }
        }

        return sb;
    }

    public static void shopsSounds(Player player, boolean shop) {
        if (shop) {
            XSound.BLOCK_ANVIL_USE.play(player, 0.7f, 1);
        } else {
            XSound.BLOCK_NOTE_BLOCK_HARP.play(player, 0.7f, 1);
        }
    }

    /** @deprecated Use {@link Crafting#hasIngredients(Player, CustomBaseObject)}. */
    @Deprecated
    public static boolean lookForIngre(Player player, CustomBaseObject a) {
        return Crafting.hasIngredients(player, a);
    }

    /** @deprecated Use {@link Crafting#hasIngredients(Player, Object[])}. */
    @Deprecated
    public static boolean lookForIngre(Player player, Object[] ings) {
        return Crafting.hasIngredients(player, ings);
    }

    /** @deprecated Use {@link Crafting#removeIngredients(Player, CustomBaseObject)}. */
    @Deprecated
    public static boolean removeForIngre(Player player, CustomBaseObject a) {
        return Crafting.removeIngredients(player, a);
    }

    /** @deprecated Use {@link Crafting#removeIngredients(Player, Object[])}. */
    @Deprecated
    public static boolean removeForIngre(Player player, Object[] ings) {
        return Crafting.removeIngredients(player, ings);
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

    public static void openShopMenu(Player player) {
        MenuManager menus = getInstance().menuManager;
        if (menus != null) menus.openShopMenu(player);
    }

    public static void openCraftMenu(Player player) {
        MenuManager menus = getInstance().menuManager;
        if (menus != null) menus.openCraftMenu(player);
    }

    /** @deprecated Use {@link Crafting#registerIngredientNames(Map)}. */
    @Deprecated
    public static void registerCraftEntityNames(HashMap<MaterialStorage, ?> regMaps) {
        Crafting.registerIngredientNames(regMaps);
    }

    /** @deprecated Use {@link Crafting#findIngredientName(String, String)}. */
    @Deprecated
    public static String findCraftEntityName(String itemName, String defaultName) {
        return Crafting.findIngredientName(itemName, defaultName);
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
        List<Entity> throwableEntities = new ArrayList<>(ThrowableItemRegistry.keySet());
        for (Entity entity : throwableEntities) {
            if (entity instanceof Item)
                entity.remove();
        }
        ThrowableItemRegistry.clear();

        for (Scoreboard s : glowScoreboards)
            for (Team t : s.getTeams())
                t.unregister();

        for (Gunner g : gunners) {
            g.dispose();
        }

        playerState.shutdown();
        if (resourcepacks != null)
            resourcepacks.save();
    }

    @Override
    public void onEnable() {
        main = this;
        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdirs();
        }
        this.resourcepacks = new ResourcepackManager(this.getDataFolder(), this.getLogger());
        gunRegister = itemRegistry.getGuns();
        ammoRegister = itemRegistry.getAmmo();
        miscRegister = itemRegistry.getMisc();
        armorRegister = itemRegistry.getArmor();
        expansionPacks = itemRegistry.getExpansionPacks();
        reloadingTasks = playerState.getReloadingTasks();
        this.mainConfig = new MainConfig();
        this.mainConfig.load();
        this.loadMessages();

        if (!NBT.preloadApi()) {
            getLogger().severe("NBT-API wasn't initialized properly, disabling the plugin");
            getPluginLoader().disablePlugin(this);
            return;
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

        if (Bukkit.getPluginManager().isPluginEnabled("Mimic")) {
            try {
                MimicHookHandler.register();
                this.getLogger().info("Found Mimic. Loaded support");
            } catch (RuntimeException | LinkageError e) {
                ErrorReporter.report("registering the Mimic hook", e);
            }
        }

        ParticleHandlers.initValues();
        if (Bukkit.getPluginManager().getPlugin("ItemBridge") != null) {
            ItemBridgePatch.setup(this);
        }

        DEBUG(ChatColor.RED + "NOTICE ME");
        reloadVals();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers())
                    resourcepacks.setAccepted(player.getUniqueId());
            }
        }.runTaskLater(this, 1);

        // check if Citizens is present and enabled.

        if (getServer().getPluginManager().getPlugin("Citizens") != null) {
            try {
                // Register your trait with Citizens.
                net.citizensnpcs.api.CitizensAPI.getTraitFactory()
                        .registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(GunnerTrait.class));
            } catch (RuntimeException | LinkageError e4) {
                getLogger().log(Level.WARNING, "Citizens 2.0 failed to register gunner trait (Ignore this.)", e4);
            }
        }

        CommandRegistry commands = Commands.create();
        PluginCommand command = getCommand("QualityArmory");
        if (command != null) {
            command.setExecutor(commands);
            command.setTabCompleter(commands);
        }

        Bukkit.getPluginManager().registerEvents(new QAListener(), this);
        Bukkit.getPluginManager().registerEvents(new AimManager(), this);
        if (Bukkit.getPluginManager().isPluginEnabled("ChestShop")) {
            try {
                Bukkit.getPluginManager().registerEvents(new ChestShopHandler(), this);
            } catch (RuntimeException | LinkageError e) {
                ErrorReporter.report("registering the ChestShop hook", e);
            }
        }

        if (Compat.hasClass("org.bukkit.event.player.PlayerResourcePackStatusEvent")) {
            Bukkit.getPluginManager().registerEvents(new Update19resourcepackhandler(), this);
        } else {
            getLogger().info(getMessages().general.prefix + " Resourcepack handler has been disabled due to the update being used.");
        }
        // Off hand, anvil and swing events (1.9+)
        if (Compat.OFFHAND)
            Bukkit.getPluginManager().registerEvents(new Update19Events(), this);

        if (getConfiguration().features.autoUpdate) {
            try {
                GithubUpdater.autoUpdate(this, "Lorenzo0111", "QualityArmory", "QualityArmory.jar");
            } catch (RuntimeException e) {
                ErrorReporter.report("checking for updates", e);
            }
        }

        Metrics metrics = new Metrics(this, 1699);

        metrics.addCustomChart(new Metrics.SimplePie("GunCount", () -> itemRegistry.getGuns().size() + ""));
        metrics.addCustomChart(
                new Metrics.SimplePie("uses_default_resourcepack", () -> getConfiguration().resourcepack.overrideDefault + ""));
        metrics.addCustomChart(
                new Metrics.SimplePie("has_an_expansion_pack", () -> !itemRegistry.getExpansionPacks().isEmpty() + ""));
        if (UnbreakableItemsTask.isNeeded())
            new UnbreakableItemsTask().runTaskTimer(this, 20, 15);
    }

    @SuppressWarnings({"unchecked", "deprecation"})
    public void reloadVals() {
        ErrorReporter.reset();
        this.mainConfig.load();

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

        itemRegistry.clearItems();

        this.loadMessages();

        resourcepacks.load();
        integrations.detect();

        if (getServer().getPluginManager().isPluginEnabled("Sentinel"))
            try {
                org.mcmonkey.sentinel.SentinelPlugin.integrations.add(new SentinelQAHandler());
            } catch (RuntimeException | LinkageError e4) {
                ErrorReporter.report("registering the Sentinel integration", e4);
            }


        if (Bukkit.getPluginManager().isPluginEnabled("WetSponge")) {
            getConfiguration().general.versionOverride = "1.8";
        }

        blockRules.reload(GunYMLLoader.getMaterials(this.mainConfig.world.destructableMaterials));
// Chris: default has 1.14 ItemType
        if ((getConfiguration().general.versionOverride.equalsIgnoreCase("1.8") || !isVersionHigherThan(1, 9)) && !getConfiguration().general.versionOverride.equalsIgnoreCase("1.13") && !getConfiguration().general.versionOverride.equalsIgnoreCase("1.14")) {
            //1.8
            CustomItemManager.registerItemType(getDataFolder(), "gun", new me.zombie_striker.customitemmanager.qa.versions.V1_8.CustomGunItem());
        } else if ((!isVersionHigherThan(1, 14) || getConfiguration().general.versionOverride.equalsIgnoreCase("1.13")) && !getConfiguration().general.versionOverride.equalsIgnoreCase("1.8") && !getConfiguration().general.versionOverride.equalsIgnoreCase("1.14")) {
            //1.9 to 1.13
            CustomItemManager.registerItemType(getDataFolder(), "gun", new CustomGunItem());
            //Make sure vehicles are safe
            // Item slots used by QualityArmory Vehicles
            if (!getConfiguration().resourcepack.overrideDefault) {
                for (int id : VEHICLE_ITEM_IDS)
                    itemRegistry.addExpansionPack(MaterialStorage.getMS(Material.DIAMOND_AXE, id, 0));
            }
        } else if (true || getConfiguration().general.versionOverride.equalsIgnoreCase("1.14")) {
            //1.14. Use crossbows
            CustomItemManager.registerItemType(getDataFolder(), "gun", new me.zombie_striker.customitemmanager.qa.versions.V1_14.CustomGunItem().setOverrideAttackSpeed(this.mainConfig.items.overrideAttackSpeed));
        }

        // Chris: if switch on, create default items.
        if (getConfiguration().features.enableCreationOfDefaultFiles) {
            CustomItemManager.getItemType("gun").initItems(getDataFolder());
        }
        ((AbstractCustomGunItem) CustomItemManager.getItemType("gun")).initIronsights(getDataFolder());

        resourcepacks.applyDefaultPack(this.mainConfig);


        // Skull texture
        GunYMLLoader.loadAmmo(this);
        GunYMLLoader.loadMisc(this);
        GunYMLLoader.loadGuns(this);
        GunYMLLoader.loadAttachments(this);
        GunYMLLoader.loadArmor(this);
        if (QAMain.getConfiguration().combat.bleeding.enabled)
            BulletWoundHandler.startTimer();

        if (tfh != null) {
            tfh = new TreeFellerHandler();
            Bukkit.getPluginManager().registerEvents(tfh, this);
        }
        if (getConfiguration().features.enableGlowEffects) {
            glowScoreboards.clear();
            glowScoreboards.add(registerGlowTeams(Bukkit.getScoreboardManager().getMainScoreboard()));
        }

        //Chris: Find and reg craft entity name
        Crafting.registerIngredientNames(itemRegistry.getGuns());
        Crafting.registerIngredientNames(itemRegistry.getAmmo());
        Crafting.registerIngredientNames(itemRegistry.getMisc());
        Crafting.registerIngredientNames(itemRegistry.getArmor());
        this.messages.saveIfChanged();

        if (menuManager != null) {
            menuManager.reload();
        } else {
            menuManager = new MenuManager(this);
        }

        BoundingBoxManager.initEntityTypeBoundingBoxes();
    }

    private void loadMessages() {
        this.messages = new MessagesConfig(this.mainConfig.general.language);
        this.messages.load();
    }

    /** @deprecated Use {@link Crafting#parseItemIngredients(List)}. */
    @Deprecated
    public ItemStack[] convertIngredients(List<String> e) {
        return Crafting.parseItemIngredients(e);
    }

    /** @deprecated Use {@link Crafting#parseIngredients(List)}. */
    @Deprecated
    public Object[] convertIngredientsRaw(List<String> e) {
        return Crafting.parseIngredients(e);
    }

    /** @deprecated Use {@link Crafting#toIngredientString(Material, int, int)}. */
    @Deprecated
    public String getIngString(Material m, int durability, int amount) {
        return Crafting.toIngredientString(m, durability, amount);
    }

    /** @deprecated Use {@link CommandRegistry#sendHelp(CommandSender)}. */
    @Deprecated
    public void sendHelp(CommandSender sender) {
        CommandRegistry.sendHelp(sender);
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

    public void handleException(Exception e) {
        if (this.mainConfig == null || this.mainConfig.debug.logExceptions)
            this.getLogger().log(Level.SEVERE, "An exception occurred in QualityArmory", e);
        else this.getLogger().severe("An exception occurred in QualityArmory. Enable 'log-exceptions' for more information: " + e.getMessage());
    }
}
