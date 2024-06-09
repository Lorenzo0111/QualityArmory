package me.zombie_striker.qg;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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

import de.tr7zw.changeme.nbtapi.utils.MinecraftVersion;
import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.customitemmanager.CustomItemManager;
import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.customitemmanager.OLD_ItemFact;
import me.zombie_striker.customitemmanager.qa.AbstractCustomGunItem;
import me.zombie_striker.customitemmanager.qa.ItemBridgePatch;
import me.zombie_striker.customitemmanager.qa.versions.V1_13.CustomGunItem;
import me.zombie_striker.qg.ammo.Ammo;
import me.zombie_striker.qg.api.QAGunGiveEvent;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.armor.ArmorObject;
import me.zombie_striker.qg.attachments.AttachmentBase;
import me.zombie_striker.qg.boundingbox.BoundingBoxManager;
import me.zombie_striker.qg.config.CommentYamlConfiguration;
import me.zombie_striker.qg.config.GunYMLCreator;
import me.zombie_striker.qg.config.GunYMLLoader;
import me.zombie_striker.qg.config.MessagesYML;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.guns.chargers.BoltactionCharger;
import me.zombie_striker.qg.guns.chargers.BreakactionCharger;
import me.zombie_striker.qg.guns.chargers.BurstFireCharger;
import me.zombie_striker.qg.guns.chargers.DelayedBurstFireCharger;
import me.zombie_striker.qg.guns.chargers.PumpactionCharger;
import me.zombie_striker.qg.guns.chargers.PushbackCharger;
import me.zombie_striker.qg.guns.chargers.RequireAimCharger;
import me.zombie_striker.qg.guns.chargers.RevolverCharger;
import me.zombie_striker.qg.guns.projectiles.ExplodingRoundProjectile;
import me.zombie_striker.qg.guns.projectiles.FireProjectile;
import me.zombie_striker.qg.guns.projectiles.HomingRocketProjectile;
import me.zombie_striker.qg.guns.projectiles.MiniNukeProjectile;
import me.zombie_striker.qg.guns.projectiles.RocketProjectile;
import me.zombie_striker.qg.guns.reloaders.M1GarandReloader;
import me.zombie_striker.qg.guns.reloaders.PumpactionReloader;
import me.zombie_striker.qg.guns.reloaders.SingleBulletReloader;
import me.zombie_striker.qg.guns.reloaders.SlideReloader;
import me.zombie_striker.qg.guns.utils.GunRefillerRunnable;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.handlers.AimManager;
import me.zombie_striker.qg.handlers.BulletWoundHandler;
import me.zombie_striker.qg.handlers.ChestShopHandler;
import me.zombie_striker.qg.handlers.EconHandler;
import me.zombie_striker.qg.handlers.MultiVersionLookup;
import me.zombie_striker.qg.handlers.ParticleHandlers;
import me.zombie_striker.qg.handlers.ProtocolLibHandler;
import me.zombie_striker.qg.handlers.QAInventoryHolder;
import me.zombie_striker.qg.handlers.TreeFellerHandler;
import me.zombie_striker.qg.handlers.Update19Events;
import me.zombie_striker.qg.handlers.Update19resourcepackhandler;
import me.zombie_striker.qg.hooks.MimicHookHandler;
import me.zombie_striker.qg.hooks.PlaceholderAPIHook;
import me.zombie_striker.qg.hooks.QuickShopHook;
import me.zombie_striker.qg.hooks.anticheat.AntiCheatHook;
import me.zombie_striker.qg.hooks.anticheat.MatrixHook;
import me.zombie_striker.qg.hooks.anticheat.VulcanHook;
import me.zombie_striker.qg.hooks.protection.ProtectionHandler;
import me.zombie_striker.qg.listener.QAListener;
import me.zombie_striker.qg.miscitems.ThrowableItems;
import me.zombie_striker.qg.miscitems.ThrowableItems.ThrowableHolder;
import me.zombie_striker.qg.npcs.Gunner;
import me.zombie_striker.qg.npcs.GunnerTrait;
import me.zombie_striker.qg.npcs_sentinel.SentinelQAHandler;
import me.zombie_striker.qg.utils.LocalUtils;

public class QAMain extends JavaPlugin {

    private static String changelog = null;

    public static final int ViaVersionIdfor_1_8 = 106;
    private static final String SERVER_VERSION;
    public static HashMap<MaterialStorage, Gun> gunRegister = new LinkedHashMap<>();
    public static HashMap<MaterialStorage, Ammo> ammoRegister = new LinkedHashMap<>();
    public static HashMap<MaterialStorage, CustomBaseObject> miscRegister = new LinkedHashMap<>();
    public static HashMap<MaterialStorage, ArmorObject> armorRegister = new LinkedHashMap<>();

    public static HashMap<String, String> craftingEntityNames = new HashMap<>();

    public static Set<EntityType> avoidTypes = new HashSet<>();
    public static HashMap<UUID, Location> recoilHelperMovedLocation = new HashMap<>();
    public static ArrayList<MaterialStorage> expansionPacks = new ArrayList<>();
    public static HashMap<UUID, List<GunRefillerRunnable>> reloadingTasks = new HashMap<>();
    public static HashMap<UUID, Long> sentResourcepack = new HashMap<>();
    public static ArrayList<UUID> resourcepackReq = new ArrayList<>();
    public static List<Gunner> gunners = new ArrayList<>();
    public static List<String> namesToBypass = new ArrayList<>();
    public static List<Material> interactableBlocks = new ArrayList<>();
    public static List<Material> destructableBlocks = new ArrayList<Material>();
    public static int regenDestructableBlocksAfter = -1;
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
    public static double swayModifier_Run = 1.3;
    public static boolean blockbullet_leaves = false;
    public static boolean blockbullet_halfslabs = false;
    public static boolean blockbullet_door = false;
    public static boolean blockbullet_water = false;
    public static boolean blockbullet_glass = false;
    public static boolean overrideAnvil = false;
    public static boolean enableIronSightsON_RIGHT_CLICK = false;
    public static boolean SwapSneakToSingleFire = false;
    public static boolean enableBulletTrails = true;
    public static boolean reloadOnQ = true;
    public static boolean reloadOnF = true;
    public static boolean reloadOnFOnly = true;
    public static boolean disableHotBarMessageOnShoot = false;
    public static boolean disableHotBarMessageOnReload = false;
    public static boolean disableHotBarMessageOnOutOfAmmo = false;
    public static boolean enableExplosionDamage = false;
    public static boolean enableExplosionDamageDrop = false;
    public static boolean requirePermsToShoot = false;
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
    public static boolean showAmmoInXPBar = false;
    public static boolean perWeaponPermission = false;

    public static boolean allowGunHitEntities = false;
    public static boolean anticheatFix = false;

    public static String S_NOPERM = "&c You do not have permission to do that.";

    public static String S_RELOAD = " Guns and configs have been reloaded.";
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
    public static String prefix = "&8[&2QualityArmory&8]&r";
    // public Inventory craftingMenu;
    public static String S_craftingBenchName = "Armory Crafting-Bench Page:";
    public static String S_missingIngredients = "You do not have all the materials needed to craft this.";
    // public Inventory shopMenu;
    public static String S_shopName = "Weapons Shop Page:";
    public static String S_noMoney = "You do not have enough money to buy this.";
    public static String S_noEcon = "ECONOMY NOT ENABLED. REPORT THIS TO THE OWNER!";
    public static String S_nextPage = "&6Next Page:";
    public static String S_prevPage = "&6Previous Page:";
    public static String bagAmmo = "&aAmmo: ";
    public static String bagAmmoType = "&aAmmo Type: ";

    public static ItemStack prevButton;
    public static ItemStack nextButton;
    public static MessagesYML m;
    public static CommentYamlConfiguration resourcepackwhitelist;
    public static String language = "en";
    public static boolean hasParties = false;
    public static boolean friendlyFire = false;
    public static boolean hasProtocolLib = false;
    public static boolean hasViaVersion = false;
    public static boolean hasViaRewind = false;
    public static boolean AUTOUPDATE = true;
    public static boolean SWAP_TO_LMB_SHOOT = true;
    public static boolean ENABLE_LORE_INFO = true;
    public static boolean ENABLE_LORE_HELP = true;
    public static boolean AutoDetectResourcepackVersion = true;
    public static boolean ITEM_enableUnbreakable = true;// TODO :stuufff
    public static boolean MANUALLYSELECT1DOT8 = false;
    public static boolean MANUALLYSELECT113 = false;
    public static boolean MANUALLYSELECT14 = false;
    public static boolean unknownTranslationKeyFixer = false;
    public static boolean enableCreationOfFiles = true;
    public static boolean changeDeathMessages = true;
    public static List<Scoreboard> coloredGunScoreboard = new ArrayList<Scoreboard>();
    public static boolean blockBreakTexture = false;
    public static boolean autoarm = false;
    public static List<UUID> currentlyScoping = new ArrayList<>();
    private static QAMain main;

    static {
        SERVER_VERSION = QAMain.getFormattedVersion();
    }

    private TreeFellerHandler tfh = null;
    private FileConfiguration config;
    private File configFile;
    private boolean saveTheConfig = false;

    public static QAMain getInstance() { return QAMain.main; }

    private static String getFormattedVersion() {
        final String bukkitVersion = Bukkit.getBukkitVersion();
        // Exemplo de bukkitVersion: "1.16.5-R0.1-SNAPSHOT"

        // Extrai a versão principal
        final String[] parts = bukkitVersion.split("-");
        String version = parts[0]; // "1.16.5"

        // Remove os pontos e substitui por underscores
        version = version.replace('.', '_'); // "1_16_5"

        // Extrai o número de revisão
        final String revision = parts.length > 1 ? parts[1] : "R0";

        // Combina a versão e a revisão no formato "x_xx_Rx"
        return version + "_" + revision;
    }

    public static boolean isVersionHigherThan(final int mainVersion, final int secondVersion) {
        final String firstChar = QAMain.SERVER_VERSION.substring(1, 2);
        final int fInt = Integer.parseInt(firstChar);
        if (fInt < mainVersion)
            return false;
        final StringBuilder secondChar = new StringBuilder();
        for (int i = 3; i < 10; i++) {
            if (QAMain.SERVER_VERSION.charAt(i) == '_' || QAMain.SERVER_VERSION.charAt(i) == '.')
                break;
            secondChar.append(QAMain.SERVER_VERSION.charAt(i));
        }

        final int sInt = Integer.parseInt(secondChar.toString());
        if (sInt < secondVersion)
            return false;
        return true;
    }

    public static void toggleNightvision(final Player player, final Gun g, final boolean add) {
        if (add) {
            if (g.getZoomWhenIronSights() > 0) {
                QAMain.currentlyScoping.add(player.getUniqueId());
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 1200, g.getZoomWhenIronSights()));
            }
            if (g.hasnightVision()) {
                QAMain.currentlyScoping.add(player.getUniqueId());
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1200, 3));
            }
        } else {
            if (QAMain.currentlyScoping.contains(player.getUniqueId())) {
                if (player.hasPotionEffect(PotionEffectType.SLOWNESS) && (g == null || g.getZoomWhenIronSights() > 0))
                    player.removePotionEffect(PotionEffectType.SLOWNESS);
                boolean potionEff = false;
                try {
                    potionEff = player.hasPotionEffect(PotionEffectType.NIGHT_VISION) && (g == null || g.hasnightVision())
                            && player.getPotionEffect(PotionEffectType.NIGHT_VISION).getAmplifier() == 3;
                } catch (Error | Exception e3452) {
                    for (final PotionEffect pe : player.getActivePotionEffects())
                        if (pe.getType() == PotionEffectType.NIGHT_VISION)
                            potionEff = (g == null || g.hasnightVision()) && pe.getAmplifier() == 3;
                }
                if (potionEff)
                    player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                QAMain.currentlyScoping.remove(player.getUniqueId());
            }
        }

    }

    public static void DEBUG(final String message) {
        if (QAMain.DEBUG)
            Bukkit.broadcast(QAMain.prefix + ChatColor.GREEN + " [DEBUG] " + ChatColor.RESET + message, "qualityarmory.debugmessages");
    }

    public static Scoreboard registerGlowTeams(final Scoreboard sb) {
        if (sb.getTeam("QA_RED") == null) {
            for (final ChatColor c : ChatColor.values()) {
                if (sb.getTeam("QA_" + c.name() + "") == null)
                    sb.registerNewTeam("QA_" + c.name() + "").setPrefix(c + "");
            }
        }
        return sb;
    }

    public static void shopsSounds(final InventoryClickEvent e, final boolean shop) {

        if (shop) {
            try {
                ((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(), Sound.BLOCK_ANVIL_USE, 0.7f, 1);
            } catch (final Error e2) {
                ((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(), Sound.valueOf("ANVIL_USE"), 0.7f, 1);
            }
        } else {
            try {
                ((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(), MultiVersionLookup.getHarp(), 0.7f, 1);
            } catch (final Error e2) {
                ((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(), Sound.valueOf("NOTE_PIANO"), 0.7f, 1);
            }
        }
    }

    public static boolean lookForIngre(final Player player, final CustomBaseObject a) {
        return QAMain.lookForIngre(player, a.getIngredientsRaw());
    }

    @SuppressWarnings("deprecation")
    public static boolean lookForIngre(final Player player, final Object[] ings) {
        if (ings == null)
            return true;
        final boolean[] bb = new boolean[ings.length];
        for (final ItemStack is : player.getInventory().getContents()) {
            if (is != null) {
                for (int i = 0; i < ings.length; i++) {
                    if (bb[i])
                        continue;
                    if (ings[i] instanceof final ItemStack check) {
                        if (is.getType() == check.getType()
                                && (check.getDurability() == 0 || is.getDurability() == check.getDurability())) {
                            if (is.getAmount() >= check.getAmount())
                                bb[i] = true;
                            break;
                        }
                    } else if (ings[i] instanceof String) {
                        final CustomBaseObject base = QualityArmory.getCustomItemByName((String) ings[i]);
                        if (QualityArmory.getCustomItem(is) == base) {
                            bb[i] = true;
                            break;
                        }

                    }
                }
            }
        }
        for (final boolean b : bb) {
            if (!b)
                return false;
        }
        return true;
    }

    public static boolean removeForIngre(final Player player, final CustomBaseObject a) {
        return QAMain.removeForIngre(player, a.getIngredientsRaw());
    }

    @SuppressWarnings("deprecation")
    public static boolean removeForIngre(final Player player, final Object[] ings) {
        if (ings == null)
            return true;
        final boolean[] bb = new boolean[ings.length];
        for (final ItemStack is : player.getInventory().getContents()) {
            if (is != null) {
                final CustomBaseObject obj = QualityArmory.getCustomItem(is);
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
                    } else if (ings[i] instanceof final ItemStack check) {
                        if (is.getType() == check.getType() && is.getDurability() == check.getDurability()) {
                            if (is.getAmount() > check.getAmount()) {
                                bb[i] = true;
                                final int slot = player.getInventory().first(is);
                                is.setAmount(is.getAmount() - check.getAmount());
                                player.getInventory().setItem(slot, is);
                            } else if (is.getAmount() == check.getAmount()) {
                                bb[i] = true;
                                final int slot = player.getInventory().first(is);
                                player.getInventory().setItem(slot, new ItemStack(Material.AIR));
                            }
                            break;
                        }
                    }
                }
            }
        }
        for (final boolean b : bb) {
            if (!b)
                return false;
        }
        return true;
    }

    public static void checkforDups(final Player p, final ItemStack... curr) {
        for (final ItemStack curs : curr)
            for (int i = 0; i < p.getInventory().getSize(); i++) {
                final ItemStack cont = p.getInventory().getItem(i);
                if (cont != null)
                    if (curs != null && OLD_ItemFact.sameGun(cont, curs))
                        if (!cont.equals(curs))
                            p.getInventory().setItem(i, null);

            }
    }

    public static MaterialStorage m(final int d) { return MaterialStorage.getMS(Material.DIAMOND_AXE, d, 0); }

    public static Inventory createShop(final int page) { return QAMain.createCustomInventory(page, true); }

    public static Inventory createCraft(final int page) { return QAMain.createCustomInventory(page, false); }

    private static boolean addToGUI(final CustomBaseObject obj, final Inventory gui, final boolean shop) {
        if (shop && (obj.getPrice() < 0 || !obj.isEnableShop()))
            return false;
        if (!shop && obj.getIngredientsRaw() == null)
            return false;
        try {
            final ItemStack is = CustomItemManager.getItemType("gun").getItem(obj.getItemData().getMat(), obj.getItemData().getData(),
                    obj.getItemData().getVariant());
            is.setAmount(obj.getCraftingReturn());
            if (is.getAmount() <= 0)
                is.setAmount(1);
            final ItemMeta im = is.getItemMeta();
            final List<String> lore = im.hasLore() ? im.getLore() : new ArrayList<>();
            if (shop) {
                lore.addAll(OLD_ItemFact.addShopLore(obj));
            } else {
                lore.addAll(OLD_ItemFact.getCraftingLore(obj));
            }
            im.setLore(lore);
            is.setItemMeta(im);
            gui.addItem(is);
            return true;
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Inventory createCustomInventory(final int page, final boolean shopping) {
        final Inventory shopMenu = new QAInventoryHolder((shopping ? QAMain.S_shopName : QAMain.S_craftingBenchName) + page).getInventory();
        final List<Gun> gunslistr = QAMain.gunRegister.values().stream().filter(CustomBaseObject::isEnableCrafting)
                .collect(Collectors.toList());
        final List<Ammo> ammolistr = QAMain.ammoRegister.values().stream().filter(CustomBaseObject::isEnableCrafting)
                .collect(Collectors.toList());
        final List<CustomBaseObject> misclistr = QAMain.miscRegister.values().stream().filter(CustomBaseObject::isEnableCrafting)
                .collect(Collectors.toList());
        final List<ArmorObject> armorlistr = QAMain.armorRegister.values().stream().filter(CustomBaseObject::isEnableCrafting)
                .collect(Collectors.toList());

        int basei = 0;
        int index = (page * 9 * 5);
        final int maxIndex = (index + (9 * 5));

        shopMenu.setItem((9 * 6) - 1 - 8, QAMain.prevButton);
        shopMenu.setItem((9 * 6) - 1, QAMain.nextButton);

        if (basei + gunslistr.size() < index) {
            basei += gunslistr.size();
        } else {
            for (final Gun g : gunslistr) {
                if (basei < index) {
                    basei++;
                    continue;
                }
                basei++;
                if (index >= maxIndex)
                    break;
                if (QAMain.addToGUI(g, shopMenu, shopping))
                    index++;
            }
        }
        if (basei + ammolistr.size() < index) {
            basei += ammolistr.size();
        } else {
            for (final CustomBaseObject ammo : ammolistr) {
                if (basei < index) {
                    basei++;
                    continue;
                }
                basei++;
                if (index >= maxIndex)
                    break;
                if (QAMain.addToGUI(ammo, shopMenu, shopping))
                    index++;
            }
        }
        if (basei + misclistr.size() < index) {
            basei += misclistr.size();
        } else {
            for (final CustomBaseObject abo : misclistr) {
                if (basei < index) {
                    basei++;
                    continue;
                }
                basei++;
                if (index >= maxIndex)
                    break;
                if (QAMain.addToGUI(abo, shopMenu, shopping))
                    index++;
            }
        }
        if (basei + armorlistr.size() < index) {
            basei += armorlistr.size();
        } else {
            for (final ArmorObject armor : armorlistr) {
                if (basei < index) {
                    basei++;
                    continue;
                }
                basei++;
                if (index >= maxIndex)
                    break;
                if (QAMain.addToGUI(armor, shopMenu, shopping))
                    index++;
            }
        }
        return shopMenu;
    }

    public static void registerCraftEntityNames(final HashMap<MaterialStorage, ?> regMaps) {
        if (null != regMaps && !regMaps.isEmpty()) {
            for (final Object item : regMaps.values()) {
                try {
                    final Object[] itemStacks = ((CustomBaseObject) item).getIngredientsRaw();
                    if (null != itemStacks && itemStacks.length > 0) {
                        for (final Object itemStack : itemStacks) {
                            final String itemName = ((ItemStack) itemStack).getType().name();
                            final String showName = LocalUtils.colorize((String) QAMain.m.a("EntityType." + itemName, itemName));
                            QAMain.craftingEntityNames.put(itemName, showName);
                        }
                    }
                } catch (final Exception e) {
                    //
                }
            }
        }
    }

    public static String findCraftEntityName(final String itemName, final String defaultName) {
        String value = QAMain.craftingEntityNames.get(itemName);
        if (null == value || value.trim().length() <= 0) {
            value = QAMain.craftingEntityNames.put(itemName, defaultName);
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
        for (final Entry<MaterialStorage, CustomBaseObject> misc : QAMain.miscRegister.entrySet()) {
            if (misc instanceof ThrowableItems) {
                for (final Entry<Entity, ThrowableHolder> e : ThrowableItems.throwItems.entrySet()) {
                    if (e.getKey() instanceof Item)
                        e.getKey().remove();
                }
            }
        }

        for (final Scoreboard s : QAMain.coloredGunScoreboard)
            for (final Team t : s.getTeams())
                t.unregister();

        for (final Gunner g : QAMain.gunners) {
            g.dispose();
        }

        try {
            QAMain.resourcepackwhitelist.save(new File(this.getDataFolder(), "resourcepackwhitelist.yml"));
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public Object a(final String path, final Object def) {
        if (this.getConfig().contains(path)) {
            return this.getConfig().get(path);
        }
        this.getConfig().set(path, def);
        this.saveTheConfig = true;
        return def;
    }

    @Override
    public void onEnable() {
        QAMain.main = this;
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
            ParticleHandlers.initValues();
        } catch (Error | Exception e5) {
        }
        if (Bukkit.getPluginManager().getPlugin("ItemBridge") != null) {
            ItemBridgePatch.setup(this);
        }

        QAMain.DEBUG(ChatColor.RED + "NOTICE ME");
        this.reloadVals();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (final Player player : Bukkit.getOnlinePlayers())
                    QAMain.resourcepackReq.add(player.getUniqueId());
            }
        }.runTaskLater(this, 1);

        // check if Citizens is present and enabled.

        if (this.getServer().getPluginManager().getPlugin("Citizens") != null) {
            try {
                // Register your trait with Citizens.
                net.citizensnpcs.api.CitizensAPI.getTraitFactory()
                        .registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(GunnerTrait.class));
            } catch (Error | Exception e4) {
                this.getLogger().log(Level.WARNING, "Citizens 2.0 failed to register gunner trait (Ignore this.)");
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
            this.getLogger().info(QAMain.prefix + " Resourcepack handler has been disabled due to the update being used.");
        }
        try {
            Bukkit.getPluginManager().registerEvents(new Update19Events(), this);
        } catch (Exception | Error e) {
        }

        try {
            if (QAMain.AUTOUPDATE)
                GithubUpdater.autoUpdate(this, "EuSouVoce", "QualityArmory", "QualityArmory.jar");
        } catch (final Exception e) {
        }

        final Metrics metrics = new Metrics(this, 1699);

        metrics.addCustomChart(new Metrics.SimplePie("GunCount", () -> QAMain.gunRegister.size() + ""));
        metrics.addCustomChart(new Metrics.SimplePie("uses_default_resourcepack", () -> QAMain.overrideURL + ""));
        metrics.addCustomChart(new Metrics.SimplePie("has_an_expansion_pack", () -> (QAMain.expansionPacks.size() > 0) + ""));
        if (!CustomItemManager.isUsingCustomData()) {
            new BukkitRunnable() {
                @Override
                @SuppressWarnings("deprecation")
                public void run() {
                    try {
                        // Cheaty, hacky fix
                        for (final Player p : Bukkit.getOnlinePlayers()) {
                            // if
                            // (p.getInventory().getItemInMainHand().containsEnchantment(Enchantment.MENDING))
                            // {
                            if (p.getInventory().getItemInMainHand() != null && p.getInventory().getItemInMainHand().hasItemMeta())
                                if (QualityArmory.isCustomItem(p.getInventory().getItemInMainHand())) {
                                    if (QAMain.ITEM_enableUnbreakable
                                            && (!p.getInventory().getItemInMainHand().getItemMeta().isUnbreakable()
                                                    && !QAMain.ignoreUnbreaking)) {
                                        ItemStack temp = p.getInventory().getItemInMainHand();
                                        final int j = QualityArmory.findSafeSpot(temp, false, QAMain.overrideURL);
                                        temp.setDurability((short) Math.max(0, j - 1));
                                        temp = Gun.removeCalculatedExtra(temp);
                                        p.setItemInHand(temp);
                                    }
                                }
                            try {

                                // if
                                // (p.getInventory().getItemInOffHand().containsEnchantment(Enchantment.MENDING))
                                // {
                                if (p.getInventory().getItemInOffHand() != null && p.getInventory().getItemInOffHand().hasItemMeta())
                                    if (QualityArmory.isCustomItem(p.getInventory().getItemInOffHand())) {
                                        if (QAMain.ITEM_enableUnbreakable
                                                && (!p.getInventory().getItemInOffHand().getItemMeta().isUnbreakable()
                                                        && !QAMain.ignoreUnbreaking)) {
                                            ItemStack temp = p.getInventory().getItemInOffHand();
                                            final int j = QualityArmory.findSafeSpot(temp, false, QAMain.overrideURL);
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

    @SuppressWarnings({ "unchecked", "deprecation", "unused" })
    public void reloadVals() {
        this.reloadConfig();
        QAMain.DEBUG = (boolean) this.a("ENABLE-DEBUG", false);

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

        QAMain.gunRegister.clear();
        QAMain.ammoRegister.clear();
        QAMain.miscRegister.clear();
        QAMain.armorRegister.clear();
        QAMain.interactableBlocks.clear();
        QAMain.craftingEntityNames.clear();

        // attachmentRegister.clear();
        // Chris: Support more language file lang/message_xx.yml
        QAMain.language = (String) this.a("language", "en");
        final File langFolder = new File(this.getDataFolder(), "lang");
        if (langFolder.exists() && !langFolder.isDirectory()) {
            langFolder.delete();
        }
        langFolder.mkdir();
        QAMain.m = new MessagesYML(QAMain.language, new File(langFolder, "message_" + QAMain.language + ".yml"));
        QAMain.prefix = LocalUtils.colorize((String) QAMain.m.a("Prefix", QAMain.prefix));
        QAMain.S_ANVIL = LocalUtils.colorize((String) QAMain.m.a("NoPermAnvilMessage", QAMain.S_ANVIL));
        QAMain.S_NOPERM = LocalUtils.colorize((String) QAMain.m.a("NoPerm", QAMain.S_NOPERM));
        QAMain.S_RELOAD = LocalUtils.colorize((String) QAMain.m.a("Reload", QAMain.S_RELOAD));
        QAMain.S_shopName = (String) QAMain.m.a("ShopName", QAMain.S_shopName);
        QAMain.S_noMoney = LocalUtils.colorize((String) QAMain.m.a("NoMoney", QAMain.S_noMoney));
        QAMain.S_craftingBenchName = (String) QAMain.m.a("CraftingBenchName", QAMain.S_craftingBenchName);
        QAMain.S_missingIngredients = (String) QAMain.m.a("Missing_Ingredients", QAMain.S_missingIngredients);
        QAMain.S_NORES1 = LocalUtils.colorize((String) QAMain.m.a("NoResourcepackMessage1", QAMain.S_NORES1));
        QAMain.S_NORES2 = LocalUtils.colorize((String) QAMain.m.a("NoResourcepackMessage2", QAMain.S_NORES2));
        QAMain.S_ITEM_AMMO = LocalUtils.colorize((String) QAMain.m.a("Lore_Ammo", QAMain.S_ITEM_AMMO));
        QAMain.S_ITEM_BULLETS = LocalUtils.colorize((String) QAMain.m.a("lore_bullets", QAMain.S_ITEM_BULLETS));
        QAMain.S_ITEM_DAMAGE = LocalUtils.colorize((String) QAMain.m.a("Lore_Damage", QAMain.S_ITEM_DAMAGE));
        QAMain.S_ITEM_DURIB = LocalUtils.colorize((String) QAMain.m.a("Lore_Durib", QAMain.S_ITEM_DURIB));
        QAMain.S_ITEM_ING = LocalUtils.colorize((String) QAMain.m.a("Lore_ingredients", QAMain.S_ITEM_ING));
        if (QAMain.m.getConfig().contains("Lore_Varients"))
            QAMain.S_ITEM_VARIANTS_LEGACY = LocalUtils.colorize((String) QAMain.m.a("Lore_Varients", QAMain.S_ITEM_VARIANTS_LEGACY));
        QAMain.S_ITEM_VARIANTS_NEW = LocalUtils.colorize((String) QAMain.m.a("Lore_Variants", QAMain.S_ITEM_VARIANTS_NEW));
        QAMain.S_ITEM_COST = LocalUtils.colorize((String) QAMain.m.a("Lore_Price", QAMain.S_ITEM_COST));
        QAMain.S_ITEM_DPS = LocalUtils.colorize((String) QAMain.m.a("Lore_DamagePerSecond", QAMain.S_ITEM_DPS));

        // Chris: add message Crafts
        QAMain.S_ITEM_CRAFTS = LocalUtils.colorize((String) QAMain.m.a("Lore_Crafts", QAMain.S_ITEM_CRAFTS));
        QAMain.S_ITEM_RETURNS = LocalUtils.colorize((String) QAMain.m.a("Lore_Returns", QAMain.S_ITEM_RETURNS));

        QAMain.S_RELOADING_MESSAGE = LocalUtils.colorize((String) QAMain.m.a("Reloading_Message", QAMain.S_RELOADING_MESSAGE));
        QAMain.S_MAX_FOUND = LocalUtils.colorize((String) QAMain.m.a("State_AmmoCount", QAMain.S_MAX_FOUND));
        QAMain.S_OUT_OF_AMMO = LocalUtils.colorize((String) QAMain.m.a("State_OutOfAmmo", QAMain.S_OUT_OF_AMMO));
        QAMain.S_HOTBAR_FORMAT = LocalUtils.colorize((String) QAMain.m.a("HotbarMessage", QAMain.S_HOTBAR_FORMAT));

        QAMain.S_KICKED_FOR_RESOURCEPACK = LocalUtils
                .colorize((String) QAMain.m.a("Kick_message_if_player_denied_request", QAMain.S_KICKED_FOR_RESOURCEPACK));

        QAMain.S_LMB_SINGLE = (String) QAMain.m.a("Lore-LMB-Single", QAMain.S_LMB_SINGLE);
        QAMain.S_LMB_FULLAUTO = (String) QAMain.m.a("Lore-LMB-FullAuto", QAMain.S_LMB_FULLAUTO);
        QAMain.S_RMB_RELOAD = (String) QAMain.m.a("Lore-RMB-Reload", QAMain.S_RMB_RELOAD);
        QAMain.S_RMB_A1 = (String) QAMain.m.a("Lore-Ironsights-RMB", QAMain.S_RMB_A1);
        QAMain.S_RMB_A2 = (String) QAMain.m.a("Lore-Ironsights-Sneak", QAMain.S_RMB_A2);
        QAMain.S_RMB_R1 = (String) QAMain.m.a("Lore-Reload-Dropitem", QAMain.S_RMB_R1);
        QAMain.S_RMB_R2 = (String) QAMain.m.a("Lore-Reload-RMB", QAMain.S_RMB_R2);
        QAMain.S_HELMET_RMB = LocalUtils.colorize((String) QAMain.m.a("Lore-Helmet-RMB", QAMain.S_HELMET_RMB));

        QAMain.S_BUYCONFIRM = LocalUtils.colorize((String) QAMain.m.a("Shop_Confirm", QAMain.S_BUYCONFIRM));

        QAMain.S_RESOURCEPACK_HELP = (String) QAMain.m.a("Resourcepack_InCaseOfCrash", QAMain.S_RESOURCEPACK_HELP);
        QAMain.S_RESOURCEPACK_DOWNLOAD = (String) QAMain.m.a("Resourcepack_Download", QAMain.S_RESOURCEPACK_DOWNLOAD);
        QAMain.S_RESOURCEPACK_BYPASS = (String) QAMain.m.a("Resourcepack_NowBypass", QAMain.S_RESOURCEPACK_BYPASS);
        QAMain.S_RESOURCEPACK_OPTIN = (String) QAMain.m.a("Resourcepack_NowOptIn", QAMain.S_RESOURCEPACK_OPTIN);

        QAMain.S_GRENADE_PALREADYPULLPIN = LocalUtils
                .colorize((String) QAMain.m.a("grenadeAlreadyPulled", QAMain.S_GRENADE_PALREADYPULLPIN));
        QAMain.S_GRENADE_PULLPIN = LocalUtils.colorize((String) QAMain.m.a("grenadePull", QAMain.S_GRENADE_PULLPIN));

        QAMain.S_FULLYHEALED = LocalUtils.colorize((String) QAMain.m.a("Medkit-FullyHealed", QAMain.S_FULLYHEALED));
        QAMain.S_MEDKIT_HEALING = LocalUtils.colorize((String) QAMain.m.a("Medkit-Healing", QAMain.S_MEDKIT_HEALING));
        QAMain.S_MEDKIT_BLEEDING = LocalUtils.colorize((String) QAMain.m.a("Medkit-Bleeding", QAMain.S_MEDKIT_BLEEDING));

        QAMain.S_MEDKIT_HEAL_AMOUNT = (double) QAMain.m.a("Medkit-HEALING_HEARTS_AMOUNT", QAMain.S_MEDKIT_HEAL_AMOUNT);

        QAMain.S_MEDKIT_HEALDELAY = (double) QAMain.m.a("Medkit-HEALING_WEAPPING_DELAY_IN_SECONDS", QAMain.S_MEDKIT_HEALDELAY);

        QAMain.S_MEDKIT_LORE_INFO = LocalUtils.colorize((String) QAMain.m.a("Medkit-Lore_RMB", QAMain.S_MEDKIT_LORE_INFO));

        QAMain.S_BLEEDOUT_LOSINGconscious = LocalUtils
                .colorize((String) QAMain.m.a("Bleeding.Losingconsciousness", QAMain.S_BLEEDOUT_LOSINGconscious));
        QAMain.S_BLEEDOUT_STARTBLEEDING = LocalUtils
                .colorize((String) QAMain.m.a("Bleeding.StartBleeding", QAMain.S_BLEEDOUT_STARTBLEEDING));
        QAMain.S_BULLETPROOFSTOPPEDBLEEDING = LocalUtils
                .colorize((String) QAMain.m.a("Bleeding.ProtectedByKevlar", QAMain.S_BULLETPROOFSTOPPEDBLEEDING));
        QAMain.S_prevPage = LocalUtils.colorize((String) QAMain.m.a("gui.prevPage", QAMain.S_prevPage));
        QAMain.S_nextPage = LocalUtils.colorize((String) QAMain.m.a("gui.nextPage", QAMain.S_nextPage));
        QAMain.bagAmmo = LocalUtils.colorize((String) QAMain.m.a("AmmoBag.current", QAMain.bagAmmo));
        QAMain.bagAmmoType = LocalUtils.colorize((String) QAMain.m.a("AmmoBag.type", QAMain.bagAmmoType));

        Material glass = null;
        Material glass2 = null;
        try {
            glass = Material.matchMaterial("BLUE_STAINED_GLASS_PANE");
            glass2 = Material.matchMaterial("RED_STAINED_GLASS_PANE");
            QAMain.prevButton = new ItemStack(glass, 1);
            QAMain.nextButton = new ItemStack(glass2, 1);
            final ItemMeta nextButtonMeta = QAMain.nextButton.getItemMeta();
            nextButtonMeta.setDisplayName(QAMain.S_nextPage);
            QAMain.nextButton.setItemMeta(nextButtonMeta);
            final ItemMeta prevButtonMeta = QAMain.prevButton.getItemMeta();
            prevButtonMeta.setDisplayName(QAMain.S_prevPage);
            QAMain.prevButton.setItemMeta(prevButtonMeta);
        } catch (Error | Exception e45) {
            glass = Material.matchMaterial("STAINED_GLASS_PANE");
            QAMain.prevButton = new ItemStack(glass, 1, (short) 14);
            QAMain.nextButton = new ItemStack(glass, 1, (short) 5);
        }

        QAMain.resourcepackwhitelist = CommentYamlConfiguration
                .loadConfiguration(new File(this.getDataFolder(), "resourcepackwhitelist.yml"));
        QAMain.namesToBypass = (List<String>) QAMain.resourcepackwhitelist.getOrSet("Names_Of_players_to_bypass", QAMain.namesToBypass);

        if (!new File(this.getDataFolder(), "config.yml").exists()) {
            this.saveDefaultConfig();
            this.reloadConfig();
        }

        if (this.getServer().getPluginManager().isPluginEnabled("Parties"))
            QAMain.hasParties = true;
        if (Bukkit.getPluginManager().isPluginEnabled("ViaRewind"))
            QAMain.hasViaRewind = true;
        if (Bukkit.getPluginManager().isPluginEnabled("ViaVersion"))
            QAMain.hasViaVersion = true;
        if (this.getServer().getPluginManager().isPluginEnabled("ProtocolLib")) {
            QAMain.hasProtocolLib = true;
            ProtocolLibHandler.initRemoveArmswing();
            ProtocolLibHandler.initAimBow();
        }

        if (this.getServer().getPluginManager().isPluginEnabled("Sentinel"))
            try {
                org.mcmonkey.sentinel.SentinelPlugin.integrations.add(new SentinelQAHandler());
            } catch (Error | Exception e4) {
            }

        QAMain.friendlyFire = (boolean) this.a("FriendlyFireEnabled", false);

        QAMain.kickIfDeniedRequest = (boolean) this.a("KickPlayerIfDeniedResourcepack", false);
        QAMain.shouldSend = (boolean) this.a("useDefaultResourcepack", true);

        QAMain.enableDurability = (boolean) this.a("EnableWeaponDurability", false);

        QAMain.bulletStep = (double) this.a("BulletDetection.step", 0.10);

        QAMain.blockbullet_door = (boolean) this.a("BlockBullets.door", false);
        QAMain.blockbullet_halfslabs = (boolean) this.a("BlockBullets.halfslabs", false);
        QAMain.blockbullet_leaves = (boolean) this.a("BlockBullets.leaves", false);
        QAMain.blockbullet_water = (boolean) this.a("BlockBullets.water", false);
        QAMain.blockbullet_glass = (boolean) this.a("BlockBullets.glass", false);

        QAMain.enableInteractChests = (boolean) this.a("enableInteract.Chests", false);

        QAMain.overrideAnvil = (boolean) this.a("overrideAnvil", false);

        QAMain.showCrashMessage = (boolean) this.a("showPossibleCrashHelpMessage", QAMain.showCrashMessage);

        QAMain.anticheatFix = (boolean) this.a("anticheatFix", QAMain.anticheatFix);

        QAMain.verboseLoadingLogging = (boolean) this.a("verboseItemLogging", QAMain.verboseLoadingLogging);

        QAMain.requirePermsToShoot = (boolean) this.a("enable_permssionsToShoot", QAMain.requirePermsToShoot);

        QAMain.sendOnJoin = (boolean) this.a("sendOnJoin", true);
        QAMain.sendTitleOnJoin = (boolean) this.a("sendTitleOnJoin", false);
        QAMain.secondsTilSend = Double.valueOf(this.a("SecondsTillRPIsSent", 5.0) + "");

        QAMain.enableBulletTrails = (boolean) this.a("enableBulletTrails", true);
        QAMain.smokeSpacing = Double.valueOf(this.a("BulletTrailsSpacing", 0.5) + "");

        QAMain.enableArmorIgnore = (boolean) this.a("enableIgnoreArmorProtection", QAMain.enableArmorIgnore);
        QAMain.ignoreUnbreaking = (boolean) this.a("enableIgnoreUnbreakingChecks", QAMain.ignoreUnbreaking);
        QAMain.ignoreSkipping = (boolean) this.a("enableIgnoreSkipForBasegameItems", QAMain.ignoreSkipping);

        QAMain.ITEM_enableUnbreakable = (boolean) this.a("Items.enable_Unbreaking", QAMain.ITEM_enableUnbreakable);

        // enableVisibleAmounts = (boolean) a("enableVisibleBulletCounts", false);
        QAMain.reloadOnQ = (boolean) this.a("enableReloadingOnDrop", false);
        QAMain.reloadOnF = (boolean) this.a("enableReloadingWhenSwapToOffhand", true);
        QAMain.reloadOnFOnly = (boolean) this.a("enableReloadOnlyWhenSwapToOffhand", false);

        QAMain.allowGunHitEntities = (boolean) this.a("allowGunHitEntities", false);

        // showOutOfAmmoOnItem = (boolean) a("showOutOfAmmoOnItem", false);
        QAMain.showOutOfAmmoOnTitle = (boolean) this.a("showOutOfAmmoOnTitle", false);
        QAMain.showReloadOnTitle = (boolean) this.a("showReloadingTitle", false);

        QAMain.showAmmoInXPBar = (boolean) this.a("showAmmoInXPBar", false);
        QAMain.perWeaponPermission = (boolean) this.a("perWeaponPermission", false);

        QAMain.enableExplosionDamage = (boolean) this.a("enableExplosionDamage", false);
        QAMain.enableExplosionDamageDrop = (boolean) this.a("enableExplosionDamageDrop", false);

        QAMain.enablePrimaryWeaponHandler = (boolean) this.a("enablePrimaryWeaponLimiter", false);
        QAMain.primaryWeaponLimit = (int) this.a("weaponlimiter_primaries", QAMain.primaryWeaponLimit);
        QAMain.secondaryWeaponLimit = (int) this.a("weaponlimiter_secondaries", QAMain.secondaryWeaponLimit);

        QAMain.enableCrafting = (boolean) this.a("enableCrafting", true);
        QAMain.enableShop = (boolean) this.a("enableShop", true);

        QAMain.AUTOUPDATE = (boolean) this.a("AUTO-UPDATE", true);
        QAMain.SWAP_TO_LMB_SHOOT = !(boolean) this.a("Swap-Reload-and-Shooting-Controls", false);

        QAMain.orderShopByPrice = (boolean) this.a("Order-Shop-By-Price", QAMain.orderShopByPrice);

        QAMain.ENABLE_LORE_INFO = (boolean) this.a("enable_lore_gun-info_messages", true);
        QAMain.ENABLE_LORE_HELP = (boolean) this.a("enable_lore_control-help_messages", true);

        QAMain.HeadshotOneHit = (boolean) this.a("Enable_Headshot_Instantkill", QAMain.HeadshotOneHit);
        QAMain.headshotPling = (boolean) this.a("Enable_Headshot_Notification_Sound", QAMain.headshotPling);
        QAMain.headshot_sound = (String) this.a("Headshot_Notification_Sound", QAMain.headshot_sound);
        QAMain.headshotGoreSounds = (boolean) this.a("Enable_Headshot_Sounds", QAMain.headshotGoreSounds);

        QAMain.autoarm = (boolean) this.a("Enable_AutoArm_Grenades", QAMain.autoarm);

        // ignoreArmorStands = (boolean) a("ignoreArmorStands", false);

        QAMain.gravity = (double) this.a("gravityConstantForDropoffCalculations", QAMain.gravity);

        QAMain.allowGunReload = (boolean) this.a("allowGunReload", QAMain.allowGunReload);
        QAMain.AutoDetectResourcepackVersion = (boolean) this.a("Auto-Detect-Resourcepack", QAMain.AutoDetectResourcepackVersion);
        QAMain.MANUALLYSELECT1DOT8 = (boolean) this.a("ManuallyOverrideTo_1_8_systems",
                Bukkit.getPluginManager().isPluginEnabled("WetSponge") ? true : QAMain.MANUALLYSELECT1DOT8);
        QAMain.MANUALLYSELECT113 = (boolean) this.a("ManuallyOverrideTo_1_13_systems", QAMain.MANUALLYSELECT113);
        QAMain.MANUALLYSELECT14 = (boolean) this.a("ManuallyOverrideTo_1_14_systems", QAMain.MANUALLYSELECT14);

        QAMain.unknownTranslationKeyFixer = (boolean) this.a("unknownTranslationKeyFixer", false);

        QAMain.enableCreationOfFiles = (boolean) this.a("Enable_Creation_Of_Default_Files", true);

        QAMain.addGlowEffects = (boolean) this.a("EnableGlowEffects", false);

        QAMain.blockBreakTexture = (boolean) this.a("Break-Block-Texture-If-Shot", true);

        QAMain.enableRecoil = (boolean) this.a("enableRecoil", true);

        QAMain.bulletWound_initialbloodamount = (double) this.a("experimental.BulletWounds.InitialBloodLevel",
                QAMain.bulletWound_initialbloodamount);
        QAMain.bulletWound_BloodIncreasePerSecond = (double) this.a("experimental.BulletWounds.BloodIncreasePerSecond",
                QAMain.bulletWound_BloodIncreasePerSecond);
        QAMain.bulletWound_MedkitBloodlossHealRate = (double) this.a("experimental.BulletWounds.Medkit_Heal_Bloodloss_Rate",
                QAMain.bulletWound_MedkitBloodlossHealRate);
        QAMain.enableBleeding = (boolean) this.a("experimental.BulletWounds.enableBleeding", QAMain.enableBleeding);

        QAMain.disableHotBarMessageOnOutOfAmmo = (boolean) this.a("disableHotbarMessages.OutOfAmmo", false);
        QAMain.disableHotBarMessageOnShoot = (boolean) this.a("disableHotbarMessages.Shoot", false);
        QAMain.disableHotBarMessageOnReload = (boolean) this.a("disableHotbarMessages.Reload", false);

        QAMain.enableReloadWhenOutOfAmmo = (boolean) this.a("automaticallyReloadGunWhenOutOfAmmo", false);

        QAMain.swayModifier_Run = (double) this.a("generalModifiers.sway.Run", QAMain.swayModifier_Run);
        QAMain.swayModifier_Walk = (double) this.a("generalModifiers.sway.Walk", QAMain.swayModifier_Walk);
        QAMain.swayModifier_Ironsights = (double) this.a("generalModifiers.sway.Ironsights", QAMain.swayModifier_Ironsights);
        QAMain.swayModifier_Sneak = (double) this.a("generalModifiers.sway.Sneak", QAMain.swayModifier_Sneak);

        QAMain.changeDeathMessages = (boolean) this.a("deathmessages.enable", QAMain.changeDeathMessages);

        final List<String> avoidTypes = (List<String>) this.a("impenetrableEntityTypes",
                Collections.singletonList(EntityType.ARROW.name()));
        if (this.getConfig().getBoolean("ignoreArmorStands"))
            QAMain.avoidTypes.add(EntityType.ARMOR_STAND);
        for (final String s : avoidTypes) {
            try {
                QAMain.avoidTypes.add(EntityType.valueOf(s));
            } catch (Error | Exception e4) {
            }
        }

        try {
            QAMain.enableEconomy = EconHandler.setupEconomy();
        } catch (Exception | Error e) {
        }

        QAMain.overrideURL = (boolean) this.a("DefaultResourcepackOverride", false);

        QAMain.enableIronSightsON_RIGHT_CLICK = (boolean) this.a("IronSightsOnRightClick", false);
        if (this.getConfig().contains("SwapSneakToSingleFile")) {
            QAMain.SwapSneakToSingleFire = (boolean) this.a("SwapSneakToSingleFire", this.a("SwapSneakToSingleFile", false));
            this.getConfig().set("SwapSneakToSingleFile", null);
            this.saveTheConfig = true;
        }
        QAMain.SwapSneakToSingleFire = (boolean) this.a("SwapSneakToSingleFire", QAMain.SwapSneakToSingleFire);

        final List<String> destarray = (List<String>) this.a("DestructableMaterials", Collections.singletonList("MATERIAL_NAME_HERE"));
        QAMain.destructableBlocks.addAll(GunYMLLoader.getMaterials(destarray));
        QAMain.regenDestructableBlocksAfter = (int) this.a("RegenDestructableBlocksAfter", QAMain.regenDestructableBlocksAfter);

        for (final Material m : Material.values())
            if (m.isBlock())
                if (m.name().contains("DOOR") || m.name().contains("TRAPDOOR") || m.name().contains("BUTTON") || m.name().contains("LEVER"))
                    QAMain.interactableBlocks.add(m);
        // Chris: default has 1.14 ItemType
        if ((QAMain.MANUALLYSELECT1DOT8 || !QAMain.isVersionHigherThan(1, 9)) && !QAMain.MANUALLYSELECT113 && !QAMain.MANUALLYSELECT14) {
            // 1.8
            CustomItemManager.registerItemType(this.getDataFolder(), "gun",
                    new me.zombie_striker.customitemmanager.qa.versions.V1_8.CustomGunItem());
        } else if ((!QAMain.isVersionHigherThan(1, 14) || QAMain.MANUALLYSELECT113) && !QAMain.MANUALLYSELECT1DOT8
                && !QAMain.MANUALLYSELECT14) {
            // 1.9 to 1.13
            CustomItemManager.registerItemType(this.getDataFolder(), "gun", new CustomGunItem());
            // Make sure vehicles are safe
            if (!QAMain.overrideURL) {
                QAMain.expansionPacks.add(MaterialStorage.getMS(Material.DIAMOND_AXE, 80, 0));
                QAMain.expansionPacks.add(MaterialStorage.getMS(Material.DIAMOND_AXE, 81, 0));
                QAMain.expansionPacks.add(MaterialStorage.getMS(Material.DIAMOND_AXE, 82, 0));
                QAMain.expansionPacks.add(MaterialStorage.getMS(Material.DIAMOND_AXE, 83, 0));
                QAMain.expansionPacks.add(MaterialStorage.getMS(Material.DIAMOND_AXE, 84, 0));
                QAMain.expansionPacks.add(MaterialStorage.getMS(Material.DIAMOND_AXE, 85, 0));
                QAMain.expansionPacks.add(MaterialStorage.getMS(Material.DIAMOND_AXE, 86, 0));
                QAMain.expansionPacks.add(MaterialStorage.getMS(Material.DIAMOND_AXE, 87, 0));
                QAMain.expansionPacks.add(MaterialStorage.getMS(Material.DIAMOND_AXE, 88, 0));
                QAMain.expansionPacks.add(MaterialStorage.getMS(Material.DIAMOND_AXE, 89, 0));
                QAMain.expansionPacks.add(MaterialStorage.getMS(Material.DIAMOND_AXE, 92, 0));
                QAMain.expansionPacks.add(MaterialStorage.getMS(Material.DIAMOND_AXE, 94, 0));
                QAMain.expansionPacks.add(MaterialStorage.getMS(Material.DIAMOND_AXE, 95, 0));
                QAMain.expansionPacks.add(MaterialStorage.getMS(Material.DIAMOND_AXE, 96, 0));
                QAMain.expansionPacks.add(MaterialStorage.getMS(Material.DIAMOND_AXE, 97, 0));
                QAMain.expansionPacks.add(MaterialStorage.getMS(Material.DIAMOND_AXE, 104, 0));
                QAMain.expansionPacks.add(MaterialStorage.getMS(Material.DIAMOND_AXE, 111, 0));
                QAMain.expansionPacks.add(MaterialStorage.getMS(Material.DIAMOND_AXE, 112, 0));
                QAMain.expansionPacks.add(MaterialStorage.getMS(Material.DIAMOND_AXE, 114, 0));
                QAMain.expansionPacks.add(MaterialStorage.getMS(Material.DIAMOND_AXE, 115, 0));
                QAMain.expansionPacks.add(MaterialStorage.getMS(Material.DIAMOND_AXE, 116, 0));
            }
        } else if (true && !QAMain.MANUALLYSELECT14) {
            // 1.20.6 Use crossbows
            CustomItemManager.registerItemType(this.getDataFolder(), "gun",
                    new me.zombie_striker.customitemmanager.qa.versions.V1_20.CustomGunItem()
                            .setOverrideAttackSpeed((boolean) this.a("overrideAttackSpeed", true)));
        } else if (true || QAMain.MANUALLYSELECT14) {
            // 1.14. Use crossbows
            CustomItemManager.registerItemType(this.getDataFolder(), "gun",
                    new me.zombie_striker.customitemmanager.qa.versions.V1_14.CustomGunItem()
                            .setOverrideAttackSpeed((boolean) this.a("overrideAttackSpeed", true)));
        }

        // Chris: if switch on, create default items.
        if (QAMain.enableCreationOfFiles) {
            CustomItemManager.getItemType("gun").initItems(this.getDataFolder());
        }
        ((AbstractCustomGunItem) CustomItemManager.getItemType("gun")).initIronsights(this.getDataFolder());

        if (QAMain.overrideURL) {
            CustomItemManager.setResourcepack((String) this.a("DefaultResourcepack", CustomItemManager.getResourcepack()));
        } else {
            if (!this.getConfig().contains("DefaultResourcepack")
                    || !this.getConfig().getString("DefaultResourcepack").equals(CustomItemManager.getResourcepack())) {
                this.getConfig().set("DefaultResourcepack", CustomItemManager.getResourcepack());
                CustomItemManager.setResourcepack(CustomItemManager.getResourcepack());
                this.saveTheConfig = true;
            }
        }

        if (this.saveTheConfig) {
            QAMain.DEBUG(QAMain.prefix + " Needed to save config: code=2");
            this.saveConfig();
        }

        // Skull texture
        GunYMLLoader.loadAmmo(this);
        GunYMLLoader.loadMisc(this);
        GunYMLLoader.loadGuns(this);
        GunYMLLoader.loadAttachments(this);
        GunYMLLoader.loadArmor(this);
        if (QAMain.enableBleeding)
            BulletWoundHandler.startTimer();

        if (this.tfh != null) {
            this.tfh = new TreeFellerHandler();
            Bukkit.getPluginManager().registerEvents(this.tfh, this);
        }
        if (QAMain.addGlowEffects) {
            QAMain.coloredGunScoreboard = new ArrayList<>();
            QAMain.coloredGunScoreboard.add(QAMain.registerGlowTeams(Bukkit.getScoreboardManager().getMainScoreboard()));
        }

        // Chris: Find and reg craft entity name
        QAMain.registerCraftEntityNames(QAMain.gunRegister);
        QAMain.registerCraftEntityNames(QAMain.ammoRegister);
        QAMain.registerCraftEntityNames(QAMain.miscRegister);
        QAMain.registerCraftEntityNames(QAMain.armorRegister);

        BoundingBoxManager.initEntityTypeBoundingBoxes();
    }

    public ItemStack[] convertIngredients(final List<String> e) {
        final Object[] list = this.convertIngredientsRaw(e);
        final ItemStack[] li = new ItemStack[list.length];
        for (int i = 0; i < list.length; i++) {
            if (list[i] instanceof ItemStack) {
                li[i] = (ItemStack) list[i];
            }
        }
        return li;

    }

    @SuppressWarnings("deprecation")
    public Object[] convertIngredientsRaw(final List<String> e) {
        final Object[] list = new Object[e.size()];
        for (int i = 0; i < e.size(); i++) {
            ItemStack temp = null;
            if (!e.get(i).contains(",")) {
                list[i] = e.get(i);
                continue;
            }
            final String[] k = e.get(i).split(",");

            try {
                temp = new ItemStack(Material.matchMaterial(k[0]));
            } catch (final Exception e2) {
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

    public String getIngString(final Material m, final int durability, final int amount) {
        return m.toString() + "," + durability + "," + amount;
    }

    public boolean b(final String arg, final String startsWith) {
        if (arg == null || startsWith == null)
            return false;
        return arg.toLowerCase().startsWith(startsWith.toLowerCase());
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        if (args.length == 1) {
            final List<String> s = new ArrayList<String>();
            if (this.b("give", args[0]))
                s.add("give");
            if (this.b("drop", args[0]))
                s.add("drop");
            if (this.b("getResourcepack", args[0]))
                s.add("getResourcepack");
            if (this.b("sendResourcepack", args[0]))
                s.add("sendResourcepack");
            if (this.b("version", args[0]))
                s.add("version");
            if (this.b("dumpItem", args[0]))
                s.add("dumpItem");
            if (QAMain.enableShop)
                if (this.b("shop", args[0]))
                    s.add("shop");
            if (QAMain.enableCrafting)
                if (this.b("craft", args[0]))
                    s.add("craft");
            // if (b("getOpenGunSlot", args[0]))
            // s.add("getOpenGunSlot");
            if (sender.hasPermission("qualityarmory.reload"))
                if (this.b("reload", args[0]))
                    s.add("reload");
            if (sender.hasPermission("qualityarmory.createnewitem")) {
            }

            return s;
        }
        if (args.length == 2) {
            final List<String> s = new ArrayList<String>();
            if (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("drop")) {
                for (final Entry<MaterialStorage, Gun> e : QAMain.gunRegister.entrySet()) {
                    if (e.getValue() instanceof AttachmentBase) {
                        if (this.b(e.getValue().getName(), args[1]))
                            s.add(e.getValue().getName());
                    } else if (this.b(e.getValue().getName(), args[1]))
                        s.add(e.getValue().getName());
                }
                for (final Entry<MaterialStorage, Ammo> e : QAMain.ammoRegister.entrySet())
                    if (this.b(e.getValue().getName(), args[1]))
                        s.add(e.getValue().getName());
                for (final Entry<MaterialStorage, CustomBaseObject> e : QAMain.miscRegister.entrySet())
                    if (this.b(e.getValue().getName(), args[1]))
                        s.add(e.getValue().getName());
                for (final Entry<MaterialStorage, ArmorObject> e : QAMain.armorRegister.entrySet())
                    if (this.b(e.getValue().getName(), args[1]))
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
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (command.getName().equalsIgnoreCase("QualityArmory")) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("version")) {
                    sender.sendMessage(QAMain.prefix + ChatColor.WHITE + " This server is using version " + ChatColor.GREEN
                            + this.getDescription().getVersion() + ChatColor.WHITE + " of QualityArmory");
                    sender.sendMessage("--==Changelog==--");
                    if (QAMain.changelog == null) {
                        final StringBuilder builder = new StringBuilder();

                        final InputStream in = this.getClass().getResourceAsStream("/changelog.txt");
                        final BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        for (int i = 0; i < 7; i++) {
                            try {
                                final String s = reader.readLine();
                                if (s.length() <= 1)
                                    break;
                                if (i == 6) {
                                    builder.append("......\n");
                                    break;
                                }
                                builder.append("\n").append(s);
                            } catch (final IOException ignored) {
                            }
                        }

                        try {
                            in.close();
                            reader.close();
                        } catch (final IOException e) {
                        }

                        QAMain.changelog = LocalUtils.colorize(builder.toString());
                    }

                    sender.sendMessage(QAMain.changelog);
                    return true;
                }
                if (args[0].equalsIgnoreCase("debug")) {
                    if (sender.hasPermission("qualityarmory.debug")) {
                        QAMain.DEBUG = !QAMain.DEBUG;
                        sender.sendMessage(QAMain.prefix + "Console debugging set to " + QAMain.DEBUG);
                    } else {
                        sender.sendMessage(QAMain.prefix + ChatColor.RED + QAMain.S_NOPERM);
                        return true;
                    }
                    return true;

                }
                if (args[0].equalsIgnoreCase("dumpItem")) {
                    if (sender.hasPermission("qualityarmory.debug")) {
                        if (!(sender instanceof Player)) {
                            return true;
                        }
                        final Gun gun = QualityArmory.getGunInHand(((Player) sender));
                        if (gun == null) {
                            sender.sendMessage(QAMain.prefix + ChatColor.RED + "You need to hold a gun to do this.");
                            return true;
                        }

                        sender.sendMessage(QAMain.prefix + ChatColor.YELLOW + gun.toString());
                    } else {
                        sender.sendMessage(QAMain.prefix + ChatColor.RED + QAMain.S_NOPERM);
                        return true;
                    }
                    return true;

                }
                if (args[0].equalsIgnoreCase("createNewAmmo")) {
                    if (sender.hasPermission("qualityarmory.createnewitem")) {
                        if (args.length >= 2) {
                            final ItemStack itemInHand = ((Player) sender).getInventory().getItemInMainHand();
                            if (itemInHand == null) {
                                sender.sendMessage(QAMain.prefix + " You need to hold an item that will be used as ammo.");

                                return true;
                            }
                            GunYMLCreator.createSkullAmmo(true, this.getDataFolder(), false, "custom_" + args[1], args[1], args[1],
                                    Collections.singletonList("Custom_item"), itemInHand.getType(), itemInHand.getDurability(),
                                    (itemInHand.getType() == MultiVersionLookup.getSkull()
                                            ? ((SkullMeta) itemInHand.getItemMeta()).getOwner()
                                            : null),
                                    null, 100, 1, 64);
                            sender.sendMessage(QAMain.prefix + " A new ammo type has been created.");
                            sender.sendMessage(QAMain.prefix + " You will need to use /qa reload for the new ammo type to appear.");
                        } else {
                            this.sendHelp(sender);
                        }
                    } else {
                        sender.sendMessage(QAMain.prefix + ChatColor.RED + QAMain.S_NOPERM);
                        return true;
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("sendResourcepack")) {
                    Player player = null;
                    if (args.length > 1 && sender.hasPermission("qualityarmory.sendresourcepack.other")) {
                        player = Bukkit.getPlayer(args[1]);
                        if (player == null) {
                            sender.sendMessage(QAMain.prefix + " This player does not exist.");
                            return true;
                        }
                    } else {
                        player = (Player) sender;
                    }
                    QAMain.namesToBypass.remove(player.getName());
                    QAMain.resourcepackwhitelist.set("Names_Of_players_to_bypass", QAMain.namesToBypass);
                    sender.sendMessage(QAMain.prefix + QAMain.S_RESOURCEPACK_OPTIN);
                    QualityArmory.sendResourcepack(player, true);
                    return true;
                }
                if (args[0].equalsIgnoreCase("getResourcepack")) {
                    Player player = null;
                    if (args.length > 1) {
                        player = Bukkit.getPlayer(args[1]);
                        if (player == null) {
                            sender.sendMessage(QAMain.prefix + " This player does not exist.");
                            return true;
                        }
                    } else {
                        player = (Player) sender;
                    }
                    if (!QAMain.namesToBypass.contains(player.getName())) {
                        QAMain.namesToBypass.add(player.getName());
                        QAMain.resourcepackwhitelist.set("Names_Of_players_to_bypass", QAMain.namesToBypass);
                    }

                    player.sendMessage(QAMain.prefix + QAMain.S_RESOURCEPACK_DOWNLOAD);
                    player.sendMessage(CustomItemManager.getResourcepack());
                    player.sendMessage(QAMain.prefix + QAMain.S_RESOURCEPACK_BYPASS);

                    return true;
                }

                if (args[0].equalsIgnoreCase("listItemIds")) {
                    if (sender.hasPermission("qualityarmory.getmaterialsused")) {
                        for (final CustomBaseObject g : QAMain.miscRegister.values())
                            sender.sendMessage(ChatColor.GREEN + g.getName() + ": " + ChatColor.WHITE + g.getItemData().getMat().name()
                                    + " : " + g.getItemData().getData());
                        for (final Gun g : QAMain.gunRegister.values())
                            sender.sendMessage(ChatColor.GOLD + g.getName() + ": " + ChatColor.WHITE + g.getItemData().getMat().name()
                                    + " : " + g.getItemData().getData());
                        for (final Ammo g : QAMain.ammoRegister.values())
                            sender.sendMessage(ChatColor.AQUA + g.getName() + ": " + ChatColor.WHITE + g.getItemData().getMat().name()
                                    + " : " + g.getItemData().getData());
                    } else {
                        sender.sendMessage(QAMain.prefix + ChatColor.RED + QAMain.S_NOPERM);
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("reload")) {
                    if (sender.hasPermission("qualityarmory.reload")) {
                        this.reloadConfig();
                        this.reloadVals();
                        sender.sendMessage(QAMain.prefix + QAMain.S_RELOAD);
                        return true;
                    } else {
                        sender.sendMessage(QAMain.prefix + ChatColor.RED + QAMain.S_NOPERM);
                        return true;
                    }
                }

                if (args[0].equalsIgnoreCase("drop")) {
                    if (!sender.hasPermission("qualityarmory.drop")) {
                        sender.sendMessage(QAMain.prefix + ChatColor.RED + QAMain.S_NOPERM);
                        return true;
                    }
                    if (args.length == 1) {
                        sender.sendMessage(QAMain.prefix + " The item name is required");
                        final StringBuilder sb = new StringBuilder();
                        sb.append("Valid items: ");
                        for (final Gun g : QAMain.gunRegister.values()) {
                            sb.append(g.getName() + ", ");
                        }
                        sb.append(ChatColor.GRAY);
                        for (final Ammo g : QAMain.ammoRegister.values()) {
                            sb.append(g.getName() + ", ");
                        }
                        sb.append(ChatColor.WHITE);
                        for (final CustomBaseObject g : QAMain.miscRegister.values()) {
                            sb.append(g.getName() + ", ");
                        }
                        sb.append(ChatColor.GRAY);
                        for (final ArmorObject g : QAMain.armorRegister.values()) {
                            sb.append(g.getName() + ", ");
                        }
                        sb.append(ChatColor.WHITE);
                        sender.sendMessage(QAMain.prefix + sb.toString());
                        return true;
                    }

                    final CustomBaseObject g = QualityArmory.getCustomItemByName(args[1]);
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
                            sender.sendMessage(QAMain.prefix + " A valid location is required");
                            return true;
                        }
                        ItemStack temp = null;

                        if (g instanceof Gun) {
                            temp = CustomItemManager.getItemType("gun").getItem(g.getItemData().getMat(), g.getItemData().getData(),
                                    g.getItemData().getVariant());
                        } else if (g instanceof Ammo) {
                            temp = CustomItemManager.getItemType("gun").getItem(g.getItemData().getMat(), g.getItemData().getData(),
                                    g.getItemData().getVariant());
                        } else {
                            temp = CustomItemManager.getItemType("gun").getItem(g.getItemData().getMat(), g.getItemData().getData(),
                                    g.getItemData().getVariant());
                            temp.setAmount(g.getCraftingReturn());
                        }
                        if (temp != null) {
                            loc.getWorld().dropItem(loc, temp);
                            sender.sendMessage(QAMain.prefix + " Dropping item " + g.getName() + " at that location");
                        } else {
                            sender.sendMessage(QAMain.prefix + " Failed to drop item " + g.getName() + " at that location");
                        }
                    } else {
                        sender.sendMessage(QAMain.prefix + " Could not find item \"" + args[1] + "\"");
                    }
                    return true;
                }

                if (args[0].equalsIgnoreCase("give")) {
                    if (!sender.hasPermission("qualityarmory.give")) {
                        sender.sendMessage(QAMain.prefix + ChatColor.RED + QAMain.S_NOPERM);
                        return true;
                    }
                    if (args.length == 1) {
                        sender.sendMessage(QAMain.prefix + " The item name is required");
                        final StringBuilder sb = new StringBuilder();
                        sb.append("Valid items: ");
                        for (final Gun g : QAMain.gunRegister.values()) {
                            sb.append(g.getName() + ", ");
                        }
                        sb.append(ChatColor.GRAY);
                        for (final Ammo g : QAMain.ammoRegister.values()) {
                            sb.append(g.getName() + ", ");
                        }
                        sb.append(ChatColor.WHITE);
                        for (final CustomBaseObject g : QAMain.miscRegister.values()) {
                            sb.append(g.getName() + ", ");
                        }
                        sb.append(ChatColor.GRAY);
                        for (final ArmorObject g : QAMain.armorRegister.values()) {
                            sb.append(g.getName() + ", ");
                        }
                        sb.append(ChatColor.WHITE);
                        // for (AttachmentBase g : attachmentRegister.values()) {
                        // sb.append(g.getAttachmentName() + ",");
                        // }
                        sender.sendMessage(QAMain.prefix + sb.toString());
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
                            sender.sendMessage("A USER IS REQUIRED FOR CONSOLE. /QA give <gun> <player> <amount>");
                        }
                        if (who == null) {
                            sender.sendMessage("That player is not online");
                            return true;
                        }

                        ItemStack temp;

                        if (g instanceof Gun) {
                            final QAGunGiveEvent event = new QAGunGiveEvent(who, (Gun) g, QAGunGiveEvent.Cause.COMMAND);
                            Bukkit.getPluginManager().callEvent(event);
                            if (event.isCancelled())
                                return true;

                            g = event.getGun();
                            temp = CustomItemManager.getItemType("gun").getItem(g.getItemData().getMat(), g.getItemData().getData(),
                                    g.getItemData().getVariant());
                            Gun.updateAmmo((Gun) g, temp, ((Gun) g).getMaxBullets());
                            who.getInventory().addItem(temp);
                        } else if (g instanceof Ammo) {
                            int amount = ((Ammo) g).getMaxItemStack();
                            if (args.length > 3)
                                amount = Integer.parseInt(args[3]);
                            QualityArmory.addAmmoToInventory(who, (Ammo) g, amount);
                        } else {
                            temp = CustomItemManager.getItemType("gun").getItem(g.getItemData().getMat(), g.getItemData().getData(),
                                    g.getItemData().getVariant());
                            temp.setAmount(g.getCraftingReturn());
                            who.getInventory().addItem(temp);
                        }

                        sender.sendMessage(QAMain.prefix + " Adding " + g.getName() + " to "
                                + (sender == who ? "your" : who.getName() + "'s") + " inventory");
                    } else {
                        sender.sendMessage(QAMain.prefix + " Could not find item \"" + args[1] + "\"");
                    }
                    return true;
                }
            }
            if (sender instanceof final Player player) {
                if (args.length >= 1 && args[0].equalsIgnoreCase("override")) {
                    QAMain.resourcepackReq.add(player.getUniqueId());
                    sender.sendMessage(QAMain.prefix + " Overriding resourcepack requirement.");
                    return true;
                }
                if (QAMain.shouldSend && !QAMain.resourcepackReq.contains(player.getUniqueId())) {
                    QualityArmory.sendResourcepack(((Player) sender), true);
                    return true;
                }
                if (args.length == 0) {
                    this.sendHelp(player);
                    return true;
                }
                if (QAMain.enableCrafting)
                    if (args[0].equalsIgnoreCase("craft")) {
                        if (!sender.hasPermission("qualityarmory.craft")) {
                            sender.sendMessage(QAMain.prefix + ChatColor.RED + QAMain.S_NOPERM);
                            return true;
                        }
                        player.openInventory(QAMain.createCraft(0));
                        return true;

                    }
                if (QAMain.enableShop)
                    if (args[0].equalsIgnoreCase("shop")) {
                        if (args.length == 2 && sender.hasPermission("qualityarmory.shop.other")) {
                            final Player target = Bukkit.getPlayer(args[1]);
                            if (target == null) {
                                sender.sendMessage(QAMain.prefix + ChatColor.RED + "That player is not online");
                                return true;
                            }

                            target.openInventory(QAMain.createShop(0));
                            return true;
                        }

                        if (!sender.hasPermission("qualityarmory.shop")) {
                            sender.sendMessage(QAMain.prefix + ChatColor.RED + QAMain.S_NOPERM);
                            return true;
                        }
                        player.openInventory(QAMain.createShop(0));
                        return true;

                    }
                // The user sent an invalid command/no args.
                this.sendHelp(player);
                return true;
            }
        }
        return true;
    }

    public void sendHelp(final CommandSender sender) {
        sender.sendMessage(LocalUtils.colorize(QAMain.prefix + " Commands:"));
        sender.sendMessage(ChatColor.GOLD + "/QA give <Item> <player> <amount>:" + ChatColor.GRAY
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
            sender.sendMessage(ChatColor.GOLD + "/QA createNewAmmo <name>: " + ChatColor.GRAY
                    + "Creats a new ammo type using the item in your hand as a template");
        }
    }

    public boolean isDuplicateGun(final ItemStack is1, final Player player) {
        for (final ItemStack is : player.getInventory().getContents()) {
            if (is != null && is1 != null)
                if (OLD_ItemFact.sameGun(is, is1)) {
                    return true;
                }
        }
        return false;
    }

    @Override
    public void reloadConfig() {
        if (this.configFile == null) {
            this.configFile = new File(this.getDataFolder(), "config.yml");
            if (!this.getDataFolder().exists())
                this.getDataFolder().mkdirs();
            if (!this.configFile.exists()) {
                try {
                    this.configFile.createNewFile();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }
        this.config = CommentYamlConfiguration.loadConfiguration(this.configFile);
        /*
         * InputStream defConfigStream = this.getResource("config.yml"); if
         * (defConfigStream != null) { config.setDefaults(
         * YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream,
         * Charsets.UTF_8))); }
         */
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
            this.config.save(this.configFile);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
