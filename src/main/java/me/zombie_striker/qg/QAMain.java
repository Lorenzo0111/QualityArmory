package me.zombie_striker.qg;

import com.cryptomorin.xseries.XPotion;
import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.reflection.XReflection;
import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.utils.MinecraftVersion;
import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.customitemmanager.CustomItemManager;
import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.customitemmanager.OLD_ItemFact;
import me.zombie_striker.customitemmanager.pack.MultiVersionPackProvider;
import me.zombie_striker.customitemmanager.pack.StaticPackProvider;
import me.zombie_striker.customitemmanager.qa.AbstractCustomGunItem;
import me.zombie_striker.customitemmanager.qa.ItemBridgePatch;
import me.zombie_striker.customitemmanager.qa.versions.V1_13.CustomGunItem;
import me.zombie_striker.qg.ammo.Ammo;
import me.zombie_striker.qg.api.QAGunGiveEvent;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.armor.ArmorObject;
import me.zombie_striker.qg.attachments.AttachmentBase;
import me.zombie_striker.qg.boundingbox.BoundingBoxManager;
import me.zombie_striker.qg.config.*;
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
import me.zombie_striker.qg.utils.LocalUtils;
import org.bukkit.*;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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

    private static String changelog = null;

    public static HashMap<MaterialStorage, Gun> gunRegister = new LinkedHashMap<>();
    public static HashMap<MaterialStorage, Ammo> ammoRegister = new LinkedHashMap<>();
    public static HashMap<MaterialStorage, CustomBaseObject> miscRegister = new LinkedHashMap<>();
    public static HashMap<MaterialStorage, ArmorObject> armorRegister = new LinkedHashMap<>();

    public static HashMap<String, String> craftingEntityNames = new HashMap<>();

    public static HashMap<UUID, Long> lastWeaponSwitch = new HashMap<>();
    public static HashMap<UUID, Location> recoilHelperMovedLocation = new HashMap<>();
    public static ArrayList<MaterialStorage> expansionPacks = new ArrayList<>();
    public static HashMap<UUID, List<GunRefillerRunnable>> reloadingTasks = new HashMap<>();
    public static HashMap<UUID, Long> sentResourcepack = new HashMap<>();
    public static ArrayList<UUID> resourcepackReq = new ArrayList<>();
    public static List<UUID> resourcepackLoading = new ArrayList<>();
    public static List<Gunner> gunners = new ArrayList<>();
    public static List<String> namesToBypass = new ArrayList<>();
    public static List<Material> interactableBlocks = new ArrayList<>();
    public static List<Material> destructableBlocks = new ArrayList<Material>();
    public static Object bulletTrail;
    public static boolean enableEconomy = false;
    public static boolean showOutOfAmmoOnItem = false;

    public static CommentYamlConfiguration resourcepackwhitelist;
    public static boolean hasParties = false;
    public static boolean hasProtocolLib = false;
    public static boolean hasViaVersion = false;
    public static boolean hasViaRewind = false;
    public static List<Scoreboard> coloredGunScoreboard = new ArrayList<Scoreboard>();
    public static List<UUID> currentlyScoping = new ArrayList<>();
    private static QAMain main;

    private TreeFellerHandler tfh = null;
    private MainConfig mainConfig;
    private MessagesConfig messages;
    private static MenuManager menuManager;

    public static QAMain getInstance() {
        return main;
    }

    public static MainConfig getConfiguration() {
        return getInstance().mainConfig;
    }

    public static MessagesConfig getMessages() {
        return getInstance().messages;
    }

    public static boolean isVersionHigherThan(int mainVersion, int secondVersion) {
        return XReflection.supports(secondVersion);
    }

    public static void toggleNightvision(Player player, Gun g, boolean add) {
        if (add) {
            if (g.getZoomWhenIronSights() > 0) {
                currentlyScoping.add(player.getUniqueId());
                player.addPotionEffect(new PotionEffect(XPotion.SLOWNESS.getPotionEffectType(), 1200, g.getZoomWhenIronSights()));
            }
            if (g.hasnightVision()) {
                currentlyScoping.add(player.getUniqueId());
                player.addPotionEffect(new PotionEffect(XPotion.NIGHT_VISION.getPotionEffectType(), 1200, 3));
            }
        } else {
            if (currentlyScoping.contains(player.getUniqueId())) {
                if (player.hasPotionEffect(XPotion.SLOWNESS.getPotionEffectType()) && (g == null || g.getZoomWhenIronSights() > 0))
                    player.removePotionEffect(XPotion.SLOWNESS.getPotionEffectType());
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

    public static boolean removeForIngre(Player player, CustomBaseObject a) {
        return removeForIngre(player, a.getIngredientsRaw());
    }

    @SuppressWarnings("deprecation")
    public static boolean removeForIngre(Player player, Object[] ings) {
        if (ings == null)
            return true;
        boolean[] bb = new boolean[ings.length];
        for (ItemStack is : player.getInventory().getContents()) {
            if (is != null) {
                CustomBaseObject obj = QualityArmory.getCustomItem(is);
                for (int i = 0; i < ings.length; i++) {
                    if (bb[i])
                        continue;
                    if (obj != null) {
                        if (ings[i] instanceof String && QualityArmory.getCustomItemByName((String) ings[i]) == obj) {
                            for (int slot = 0; slot < player.getInventory().getContents().length; slot++) {
                                if (player.getInventory().getItem(slot).equals(is)) {
                                    player.getInventory().setItem(slot, new ItemStack(Material.AIR));
                                    break;
                                }
                            }
                            bb[i] = true;
                            break;
                        }
                    } else if (ings[i] instanceof ItemStack) {
                        ItemStack check = (ItemStack) ings[i];
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

    public static void openShopMenu(Player player) {
        if (menuManager != null) menuManager.openShopMenu(player);
    }

    public static void openCraftMenu(Player player) {
        if (menuManager != null) menuManager.openCraftMenu(player);
    }

    public static void registerCraftEntityNames(HashMap<MaterialStorage, ?> regMaps) {
        if (null != regMaps && !regMaps.isEmpty()) {
            for (Object item : regMaps.values()) {
                try {
                    Object[] itemStacks = ((CustomBaseObject) item).getIngredientsRaw();
                    if (null != itemStacks && itemStacks.length > 0) {
                        for (Object itemStack : itemStacks) {
                            String itemName = ((ItemStack) itemStack).getType().name();
                            String showName = getMessages().getMaterialName(itemName);
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
        main = this;
        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdirs();
        }
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

        try {
            if (Bukkit.getPluginManager().isPluginEnabled("Mimic")) {
                MimicHookHandler.register();
                this.getLogger().info("Found Mimic. Loaded support");
            }
        } catch (Exception | Error ignored) {
        }

        try {
            ParticleHandlers.initValues();
        } catch (Error | Exception e5) {
        }
        if (Bukkit.getPluginManager().getPlugin("ItemBridge") != null) {
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

        if (getServer().getPluginManager().getPlugin("Citizens") != null) {
            try {
                // Register your trait with Citizens.
                net.citizensnpcs.api.CitizensAPI.getTraitFactory()
                        .registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(GunnerTrait.class));
            } catch (Error | Exception e4) {
                getLogger().log(Level.WARNING, "Citizens 2.0 failed to register gunner trait (Ignore this.)");
            }
        }

        Bukkit.getPluginManager().registerEvents(new QAListener(), this);
        Bukkit.getPluginManager().registerEvents(new AimManager(), this);
        try {
            if (Bukkit.getPluginManager().isPluginEnabled("ChestShop"))
                Bukkit.getPluginManager().registerEvents(new ChestShopHandler(), this);
        } catch (Error | Exception e43) {
        }

        try {
            Bukkit.getPluginManager().registerEvents(new Update19resourcepackhandler(), this);
        } catch (Exception | Error e) {
            getLogger().info(getMessages().general.prefix + " Resourcepack handler has been disabled due to the update being used.");
        }
        try {
            Bukkit.getPluginManager().registerEvents(new Update19Events(), this);
        } catch (Exception | Error e) {
        }

        try {
            if (getConfiguration().features.autoUpdate)
                GithubUpdater.autoUpdate(this, "Lorenzo0111", "QualityArmory", "QualityArmory.jar");
        } catch (Exception e) {
        }

        Metrics metrics = new Metrics(this, 1699);

        metrics.addCustomChart(new Metrics.SimplePie("GunCount", () -> gunRegister.size() + ""));
        metrics.addCustomChart(
                new Metrics.SimplePie("uses_default_resourcepack", () -> getConfiguration().resourcepack.overrideDefault + ""));
        metrics.addCustomChart(
                new Metrics.SimplePie("has_an_expansion_pack", () -> (expansionPacks.size() > 0) + ""));
        if (!CustomItemManager.isUsingCustomData()) {
            new BukkitRunnable() {
                @SuppressWarnings("deprecation")
                public void run() {
                    try {
                        // Cheaty, hacky fix
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            // if (p.getItemInHand().containsEnchantment(Enchantment.MENDING)) {
                            if (p.getItemInHand() != null && p.getItemInHand().hasItemMeta())
                                if (QualityArmory.isCustomItem(p.getItemInHand())) {
                                    if (getConfiguration().items.enableUnbreaking && (!p.getItemInHand().getItemMeta().isUnbreakable()
                                            && !getConfiguration().features.enableIgnoreUnbreakingChecks)) {
                                        ItemStack temp = p.getItemInHand();
                                        int j = QualityArmory.findSafeSpot(temp, false, getConfiguration().resourcepack.overrideDefault);
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
                                        if (getConfiguration().items.enableUnbreaking && (!p.getInventory().getItemInOffHand().getItemMeta()
                                                .isUnbreakable() && !getConfiguration().features.enableIgnoreUnbreakingChecks)) {
                                            ItemStack temp = p.getInventory().getItemInOffHand();
                                            int j = QualityArmory.findSafeSpot(temp, false, getConfiguration().resourcepack.overrideDefault);
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

        gunRegister.clear();
        ammoRegister.clear();
        miscRegister.clear();
        armorRegister.clear();
        interactableBlocks.clear();
        craftingEntityNames.clear();

        this.loadMessages();

        resourcepackwhitelist = CommentYamlConfiguration.loadConfiguration(new File(getDataFolder(), "resourcepackwhitelist.yml"));
        namesToBypass = (List<String>) resourcepackwhitelist.getOrSet("Names_Of_players_to_bypass", namesToBypass);

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


        if (Bukkit.getPluginManager().isPluginEnabled("WetSponge")) {
            getConfiguration().general.versionOverride = "1.8";
        }

        try {
            enableEconomy = EconHandler.setupEconomy();
        } catch (Exception | Error e) {
        }

        destructableBlocks.clear();
        destructableBlocks.addAll(GunYMLLoader.getMaterials(this.mainConfig.world.destructableMaterials));

        for (Material m : Material.values())
            if (m.isBlock())
                if (m.name().contains("DOOR") || m.name().contains("TRAPDOOR") || m.name().contains("BUTTON")
                        || m.name().contains("LEVER"))
                    interactableBlocks.add(m);
// Chris: default has 1.14 ItemType
        if ((getConfiguration().general.versionOverride.equalsIgnoreCase("1.8") || !isVersionHigherThan(1, 9)) && !getConfiguration().general.versionOverride.equalsIgnoreCase("1.13") && !getConfiguration().general.versionOverride.equalsIgnoreCase("1.14")) {
            //1.8
            CustomItemManager.registerItemType(getDataFolder(), "gun", new me.zombie_striker.customitemmanager.qa.versions.V1_8.CustomGunItem());
        } else if ((!isVersionHigherThan(1, 14) || getConfiguration().general.versionOverride.equalsIgnoreCase("1.13")) && !getConfiguration().general.versionOverride.equalsIgnoreCase("1.8") && !getConfiguration().general.versionOverride.equalsIgnoreCase("1.14")) {
            //1.9 to 1.13
            CustomItemManager.registerItemType(getDataFolder(), "gun", new CustomGunItem());
            //Make sure vehicles are safe
            if (!getConfiguration().resourcepack.overrideDefault) {
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
        } else if (true || getConfiguration().general.versionOverride.equalsIgnoreCase("1.14")) {
            //1.14. Use crossbows
            CustomItemManager.registerItemType(getDataFolder(), "gun", new me.zombie_striker.customitemmanager.qa.versions.V1_14.CustomGunItem().setOverrideAttackSpeed(this.mainConfig.items.overrideAttackSpeed));
        }

        // Chris: if switch on, create default items.
        if (getConfiguration().features.enableCreationOfDefaultFiles) {
            CustomItemManager.getItemType("gun").initItems(getDataFolder());
        }
        ((AbstractCustomGunItem) CustomItemManager.getItemType("gun")).initIronsights(getDataFolder());

        boolean saveConfig = false;
        Object defaultPack = this.mainConfig.getDefaultResourcepack();
        if (getConfiguration().resourcepack.overrideDefault && defaultPack != null) {
            if (defaultPack instanceof ConfigurationSection) {
                ConfigurationSection packSection = (ConfigurationSection) defaultPack;
                if (packSection.contains("21")) {
                    packSection.set("21-4", packSection.getString("21"));
                    packSection.set("21", null);
                    saveConfig = true;
                }

                CustomItemManager.setResourcepack(new MultiVersionPackProvider(packSection));
            } else {
                CustomItemManager.setResourcepack(new StaticPackProvider(String.valueOf(defaultPack)));
            }
        } else {
            Object serialized = CustomItemManager.getResourcepackProvider().serialize();
            if (defaultPack == null || !(defaultPack instanceof String) || !defaultPack.equals(serialized)) {
                this.mainConfig.setDefaultResourcepack(serialized);
                saveConfig = true;
            }
        }

        if (saveConfig) {
            DEBUG(getMessages().general.prefix + " Needed to save config: code=2");
            this.mainConfig.save();
        }


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
            coloredGunScoreboard = new ArrayList<>();
            coloredGunScoreboard.add(registerGlowTeams(Bukkit.getScoreboardManager().getMainScoreboard()));
        }

        //Chris: Find and reg craft entity name
        registerCraftEntityNames(gunRegister);
        registerCraftEntityNames(ammoRegister);
        registerCraftEntityNames(miscRegister);
        registerCraftEntityNames(armorRegister);
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
            if (b("dumpItem", args[0]))
                s.add("dumpItem");
            if (getConfiguration().menu.enableShop)
                if (b("shop", args[0]))
                    s.add("shop");
            if (getConfiguration().menu.enableCrafting)
                if (b("craft", args[0]))
                    s.add("craft");
            // if (b("getOpenGunSlot", args[0]))
            // s.add("getOpenGunSlot");
            if (sender.hasPermission("qualityarmory.reload"))
                if (b("reload", args[0]))
                    s.add("reload");
            if (sender.hasPermission("qualityarmory.createnewitem")) {
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
                for (Entry<MaterialStorage, CustomBaseObject> e : miscRegister.entrySet())
                    if (b(e.getValue().getName(), args[1]))
                        s.add(e.getValue().getName());
                for (Entry<MaterialStorage, ArmorObject> e : armorRegister.entrySet())
                    if (b(e.getValue().getName(), args[1]))
                        s.add(e.getValue().getName());
            }
            return s;
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("give")) {
                return null;
            }
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("QualityArmory")) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("version")) {
                    sender.sendMessage(getMessages().general.prefix + ChatColor.WHITE + " This server is using version " + ChatColor.GREEN
                            + this.getDescription().getVersion() + ChatColor.WHITE + " of QualityArmory");
                    sender.sendMessage("--==Changelog==--");
                    if (changelog == null) {
                        StringBuilder builder = new StringBuilder();

                        InputStream in = getClass().getResourceAsStream("/changelog.txt");
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        for (int i = 0; i < 7; i++) {
                            try {
                                String s = reader.readLine();
                                if (s.length() <= 1)
                                    break;
                                if (i == 6) {
                                    builder.append("......\n");
                                    break;
                                }
                                builder.append("\n").append(s);
                            } catch (IOException ignored) {
                            }
                        }

                        try {
                            in.close();
                            reader.close();
                        } catch (IOException e) {
                        }

                        changelog = LocalUtils.colorize(builder.toString());
                    }


                    sender.sendMessage(changelog);
                    return true;
                }
                if (args[0].equalsIgnoreCase("debug")) {
                    if (sender.hasPermission("qualityarmory.debug")) {
                        getConfiguration().debug.enabled = !getConfiguration().debug.enabled;
                        sender.sendMessage(getMessages().general.prefix + "Console debugging set to " + getConfiguration().debug.enabled);
                    } else {
                        sender.sendMessage(getMessages().general.prefix + ChatColor.RED + getMessages().general.noPermission);
                        return true;
                    }
                    return true;

                }
                if (args[0].equalsIgnoreCase("dumpItem")) {
                    if (sender.hasPermission("qualityarmory.debug")) {
                        if (!(sender instanceof Player)) {
                            return true;
                        }
                        Gun gun = QualityArmory.getGunInHand(((Player) sender));
                        if (gun == null) {
                            sender.sendMessage(getMessages().general.prefix + getMessages().commands.holdGunRequired);
                            return true;
                        }

                        sender.sendMessage(getMessages().general.prefix + ChatColor.YELLOW + gun.toString());
                    } else {
                        sender.sendMessage(getMessages().general.prefix + ChatColor.RED + getMessages().general.noPermission);
                        return true;
                    }
                    return true;

                }
                if (args[0].equalsIgnoreCase("createNewAmmo")) {
                    if (sender.hasPermission("qualityarmory.createnewitem")) {
                        if (args.length >= 2) {
                            ItemStack itemInHand = ((Player) sender).getItemInHand();
                            if (itemInHand == null) {
                                sender.sendMessage(getMessages().general.prefix + getMessages().commands.holdAmmoTemplateRequired);

                                return true;
                            }
                            GunYMLCreator.createSkullAmmo(true, getDataFolder(), false, "custom_" + args[1], args[1],
                                    args[1], Collections.singletonList("Custom_item"), itemInHand.getType(),
                                    itemInHand.getDurability(),
                                    (itemInHand.getType() == MultiVersionLookup.getSkull()
                                            ? ((SkullMeta) itemInHand.getItemMeta()).getOwner()
                                            : null),
                                    null, 100, 1, 64);
                            sender.sendMessage(getMessages().general.prefix + getMessages().commands.ammoCreated);
                            sender.sendMessage(
                                    getMessages().general.prefix + getMessages().commands.ammoCreatedReloadHint);
                        } else {
                            sendHelp(sender);
                        }
                    } else {
                        sender.sendMessage(getMessages().general.prefix + ChatColor.RED + getMessages().general.noPermission);
                        return true;
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("sendResourcepack")) {
                    Player player = null;
                    if (args.length > 1 && sender.hasPermission("qualityarmory.sendresourcepack.other")) {
                        player = Bukkit.getPlayer(args[1]);
                        if (player == null) {
                            sender.sendMessage(getMessages().general.prefix + getMessages().general.playerNotFound);
                            return true;
                        }
                    } else {
                        player = (Player) sender;
                    }
                    namesToBypass.remove(player.getName());
                    resourcepackwhitelist.set("Names_Of_players_to_bypass", namesToBypass);
                    sender.sendMessage(getMessages().general.prefix + getMessages().resourcepack.nowOptedIn);
                    QualityArmory.sendResourcepack(player, true);
                    return true;
                }
                if (args[0].equalsIgnoreCase("getResourcepack")) {
                    Player player = null;
                    if (args.length > 1) {
                        player = Bukkit.getPlayer(args[1]);
                        if (player == null) {
                            sender.sendMessage(getMessages().general.prefix + getMessages().general.playerNotFound);
                            return true;
                        }
                    } else {
                        player = (Player) sender;
                    }
                    if (!namesToBypass.contains(player.getName())) {
                        namesToBypass.add(player.getName());
                        resourcepackwhitelist.set("Names_Of_players_to_bypass", namesToBypass);
                    }

                    player.sendMessage(getMessages().general.prefix + getMessages().resourcepack.download);
                    player.sendMessage(CustomItemManager.getResourcepack(player));
                    player.sendMessage(getMessages().general.prefix + getMessages().resourcepack.nowBypassing);

                    return true;
                }


                if (args[0].equalsIgnoreCase("listItemIds")) {
                    if (sender.hasPermission("qualityarmory.getmaterialsused")) {
                        for (CustomBaseObject g : miscRegister.values())
                            sender.sendMessage(ChatColor.GREEN + g.getName() + ": " + ChatColor.WHITE
                                    + g.getItemData().getMat().name() + " : " + g.getItemData().getData());
                        for (Gun g : gunRegister.values())
                            sender.sendMessage(ChatColor.GOLD + g.getName() + ": " + ChatColor.WHITE
                                    + g.getItemData().getMat().name() + " : " + g.getItemData().getData());
                        for (Ammo g : ammoRegister.values())
                            sender.sendMessage(ChatColor.AQUA + g.getName() + ": " + ChatColor.WHITE
                                    + g.getItemData().getMat().name() + " : " + g.getItemData().getData());
                    } else {
                        sender.sendMessage(getMessages().general.prefix + ChatColor.RED + getMessages().general.noPermission);
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("reload")) {
                    if (sender.hasPermission("qualityarmory.reload")) {
                        reloadVals();
                        sender.sendMessage(getMessages().general.prefix + getMessages().general.reloaded);
                        return true;
                    } else {
                        sender.sendMessage(getMessages().general.prefix + ChatColor.RED + getMessages().general.noPermission);
                        return true;
                    }
                }

                if (args[0].equalsIgnoreCase("drop")) {
                    if (!sender.hasPermission("qualityarmory.drop")) {
                        sender.sendMessage(getMessages().general.prefix + ChatColor.RED + getMessages().general.noPermission);
                        return true;
                    }
                    if (args.length == 1) {
                        sender.sendMessage(getMessages().general.prefix + getMessages().commands.itemNameRequired);
                        StringBuilder sb = new StringBuilder();
                        sb.append(getMessages().commands.validItems);
                        for (Gun g : gunRegister.values()) {
                            sb.append(g.getName() + ", ");
                        }
                        sb.append(ChatColor.GRAY);
                        for (Ammo g : ammoRegister.values()) {
                            sb.append(g.getName() + ", ");
                        }
                        sb.append(ChatColor.WHITE);
                        for (CustomBaseObject g : miscRegister.values()) {
                            sb.append(g.getName() + ", ");
                        }
                        sb.append(ChatColor.GRAY);
                        for (ArmorObject g : armorRegister.values()) {
                            sb.append(g.getName() + ", ");
                        }
                        sb.append(ChatColor.WHITE);
                        sender.sendMessage(getMessages().general.prefix + sb.toString());
                        return true;
                    }

                    CustomBaseObject g = QualityArmory.getCustomItemByName(args[1]);
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
                            sender.sendMessage(getMessages().general.prefix + getMessages().commands.locationRequired);
                            return true;
                        }
                        ItemStack temp = null;

                        if (g instanceof Gun) {
                            temp = CustomItemManager.getItemType("gun").getItem(g.getItemData().getMat(), g.getItemData().getData(), g.getItemData().getVariant());
                        } else if (g instanceof Ammo) {
                            temp = CustomItemManager.getItemType("gun").getItem(g.getItemData().getMat(), g.getItemData().getData(), g.getItemData().getVariant());
                        } else {
                            temp = CustomItemManager.getItemType("gun").getItem(g.getItemData().getMat(), g.getItemData().getData(), g.getItemData().getVariant());
                            temp.setAmount(g.getCraftingReturn());
                        }
                        if (temp != null) {
                            loc.getWorld().dropItem(loc, temp);
                            sender.sendMessage(getMessages().general.prefix + MessagesConfig.format(getMessages().commands.droppingItem, "item", g.getName()));
                        } else {
                            sender.sendMessage(getMessages().general.prefix + MessagesConfig.format(getMessages().commands.dropFailed, "item", g.getName()));
                        }
                    } else {
                        sender.sendMessage(getMessages().general.prefix + MessagesConfig.format(getMessages().commands.itemNotFound, "item", args[1]));
                    }
                    return true;
                }

                if (args[0].equalsIgnoreCase("give")) {
                    if (!sender.hasPermission("qualityarmory.give")) {
                        sender.sendMessage(getMessages().general.prefix + ChatColor.RED + getMessages().general.noPermission);
                        return true;
                    }
                    if (args.length == 1) {
                        sender.sendMessage(getMessages().general.prefix + getMessages().commands.itemNameRequired);
                        StringBuilder sb = new StringBuilder();
                        sb.append(getMessages().commands.validItems);
                        for (Gun g : gunRegister.values()) {
                            sb.append(g.getName() + ", ");
                        }
                        sb.append(ChatColor.GRAY);
                        for (Ammo g : ammoRegister.values()) {
                            sb.append(g.getName() + ", ");
                        }
                        sb.append(ChatColor.WHITE);
                        for (CustomBaseObject g : miscRegister.values()) {
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
                        sender.sendMessage(getMessages().general.prefix + sb.toString());
                        return true;
                    }

                    CustomBaseObject g = QualityArmory.getCustomItemByName(args[1]);
                    if (g != null) {
                        Player who = null;
                        if (args.length > 2)
                            who = Bukkit.getPlayer(args[2]);
                        else if (sender instanceof Player)
                            who = ((Player) sender);
                        else {
                            sender.sendMessage(getMessages().general.prefix + getMessages().general.consoleRequiresPlayer);
                        }
                        if (who == null) {
                            sender.sendMessage(getMessages().general.prefix + getMessages().general.playerNotOnline);
                            return true;
                        }

                        ItemStack temp;

                        if (g instanceof Gun) {
                            QAGunGiveEvent event = new QAGunGiveEvent(who, (Gun) g, QAGunGiveEvent.Cause.COMMAND);
                            Bukkit.getPluginManager().callEvent(event);
                            if (event.isCancelled()) return true;

                            g = event.getGun();
                            temp = CustomItemManager.getItemType("gun").getItem(g.getItemData().getMat(), g.getItemData().getData(), g.getItemData().getVariant());
                            Gun.updateAmmo((Gun) g, temp, ((Gun) g).getMaxBullets());
                            who.getInventory().addItem(temp);
                        } else if (g instanceof Ammo) {
                            int amount = ((Ammo) g).getMaxItemStack();
                            if (args.length > 3)
                                amount = Integer.parseInt(args[3]);
                            QualityArmory.addAmmoToInventory(who, (Ammo) g, amount);
                        } else {
                            temp = CustomItemManager.getItemType("gun").getItem(g.getItemData().getMat(), g.getItemData().getData(), g.getItemData().getVariant());
                            temp.setAmount(g.getCraftingReturn());
                            who.getInventory().addItem(temp);
                        }

                        sender.sendMessage(getMessages().general.prefix + (sender == who
                                ? MessagesConfig.format(getMessages().commands.givingItemSelf, "item", g.getName())
                                : MessagesConfig.format(getMessages().commands.givingItemOther, "item", g.getName(), "player", who.getName())));
                    } else {
                        sender.sendMessage(getMessages().general.prefix + MessagesConfig.format(getMessages().commands.itemNotFound, "item", args[1]));
                    }
                    return true;
                }
            }
            if (sender instanceof Player) {
                final Player player = (Player) sender;
                if (args.length >= 1 && args[0].equalsIgnoreCase("override")) {
                    resourcepackReq.add(player.getUniqueId());
                    sender.sendMessage(getMessages().general.prefix + getMessages().commands.resourcepackOverride);
                    return true;
                }
                if (getConfiguration().resourcepack.enabled && !resourcepackReq.contains(player.getUniqueId())) {
                    QualityArmory.sendResourcepack(((Player) sender), true);
                    return true;
                }
                if (args.length == 0) {
                    sendHelp(player);
                    return true;
                }
                if (getConfiguration().menu.enableCrafting)
                    if (args[0].equalsIgnoreCase("craft")) {
                        if (!sender.hasPermission("qualityarmory.craft")) {
                            sender.sendMessage(getMessages().general.prefix + ChatColor.RED + getMessages().general.noPermission);
                            return true;
                        }

                        if (args.length == 2) {
                            CustomBaseObject g = QualityArmory.getCustomItemByName(args[1]);
                            if (g == null) {
                                openCraftMenu(player);
                                return true;
                            }

                            if (g instanceof Gun) {
                                if (getConfiguration().permissions.requireCraft && !player.hasPermission("qualityarmory.craftgun")) {
                                    player.sendMessage(getMessages().general.prefix + ChatColor.RED + getMessages().general.noPermission);
                                    return true;
                                }
                                if (getConfiguration().permissions.perWeapon.craft && !player.hasPermission("qualityarmory.craftgun." + g.getName())) {
                                    player.sendMessage(getMessages().general.prefix + ChatColor.RED + getMessages().general.noPermission);
                                    return true;
                                }
                            }

                            if (!lookForIngre(player, g)) {
                                player.sendMessage(QAMain.getMessages().general.prefix + QAMain.getMessages().crafting.missingIngredients);
                                return true;
                            }

                            removeForIngre(player, g);
                            ItemStack result = QualityArmory.getCustomItemAsItemStack(g);
                            result.setAmount(g.getCraftingReturn());

                            player.getInventory().addItem(result);

                            return true;
                        }

                        openCraftMenu(player);
                        return true;

                    }
                if (getConfiguration().menu.enableShop)
                    if (args[0].equalsIgnoreCase("shop")) {
                        if (args.length == 2 && sender.hasPermission("qualityarmory.shop.other")) {
                            Player target = Bukkit.getPlayer(args[1]);
                            if (target == null) {
                                sender.sendMessage(getMessages().general.prefix + getMessages().general.playerNotOnline);
                                return true;
                            }

                            openShopMenu(target);
                            return true;
                        }

                        if (!sender.hasPermission("qualityarmory.shop")) {
                            sender.sendMessage(getMessages().general.prefix + ChatColor.RED + getMessages().general.noPermission);
                            return true;
                        }
                        openShopMenu(player);
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
        MessagesConfig.Help help = getMessages().help;
        sender.sendMessage(getMessages().general.prefix + help.header);
        sender.sendMessage(help.give);
        sender.sendMessage(help.craft);
        sender.sendMessage(help.shop);
        sender.sendMessage(help.getResourcepack);
        sender.sendMessage(help.sendResourcepack);
        if (sender.hasPermission("qualityarmory.reload"))
            sender.sendMessage(help.reload);
        if (sender.hasPermission("qualityarmory.createnewitem"))
            sender.sendMessage(help.createNewAmmo);
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
