package me.zombie_striker.qg;

import com.cryptomorin.xseries.XPotion;
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
import me.zombie_striker.qg.ammo.AmmoBox;
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
import me.zombie_striker.qg.guns.chargers.*;
import me.zombie_striker.qg.guns.projectiles.*;
import me.zombie_striker.qg.guns.reloaders.M1GarandReloader;
import me.zombie_striker.qg.guns.reloaders.PumpactionReloader;
import me.zombie_striker.qg.guns.reloaders.SingleBulletReloader;
import me.zombie_striker.qg.guns.reloaders.SlideReloader;
import me.zombie_striker.qg.guns.utils.GunRefillerRunnable;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.handlers.*;
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
import java.util.stream.Collectors;

public class QAMain extends JavaPlugin {

    private static String changelog = null;

    public static final int ViaVersionIdfor_1_8 = 106;
    public static HashMap<MaterialStorage, Gun> gunRegister = new LinkedHashMap<>();
    public static HashMap<MaterialStorage, Ammo> ammoRegister = new LinkedHashMap<>();
    public static HashMap<MaterialStorage, CustomBaseObject> miscRegister = new LinkedHashMap<>();
    public static HashMap<MaterialStorage, ArmorObject> armorRegister = new LinkedHashMap<>();


    public static HashMap<String, String> craftingEntityNames = new HashMap<>();

    public static HashMap<UUID, Long> lastWeaponSwitch = new HashMap<>();
    public static Set<EntityType> avoidTypes = new HashSet<>();
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
    public static int regenDestructableBlocksAfter = -1;
    public static boolean enableInteractChests = false;
    public static boolean DEBUG = false;
    public static Object bulletTrail;

    public static boolean shouldSend = true;
    public static boolean sendOnJoin = false;
    public static boolean sendTitleOnJoin = false;
    public static boolean resourcepackInvincibility = false;
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
    public static String hit_sound = WeaponSounds.XP_ORG_PICKUP.getSoundName();
    public static List<EntityType> headshotBlacklist = new ArrayList<>();
    public static boolean HeadshotOneHit = true;
    public static boolean headshotPling = true;
    public static boolean enableHitSound = false;
    public static boolean showOutOfAmmoOnItem = false;
    public static boolean showOutOfAmmoOnTitle = false;
    public static boolean showReloadOnTitle = false;
    public static boolean addGlowEffects = false;
    public static boolean enableReloadWhenOutOfAmmo = false;
    public static boolean overrideURL = false;
    public static boolean kickIfDeniedRequest = false;
    public static boolean showAmmoInXPBar = false;
    public static boolean perWeaponPermission = false;
    public static boolean useMoveForRecoil = true;
    public static double weaponSwitchDelay = 0;
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
    public static boolean SHOW_BULLETS_LORE = false;
    public static boolean ENABLE_LORE_INFO = true;
    public static boolean ENABLE_LORE_HELP = true;
    public static boolean AutoDetectResourcepackVersion = true;
    public static boolean ITEM_enableUnbreakable = true;// TODO :stuufff
    public static boolean MANUALLYSELECT18 = false;
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

    private TreeFellerHandler tfh = null;
    private FileConfiguration config;
    private File configFile;
    private boolean saveTheConfig = false;

    public static QAMain getInstance() {
        return main;
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
        if (DEBUG)
            Bukkit.broadcast(prefix + ChatColor.GREEN + " [DEBUG] " + ChatColor.RESET + message, "qualityarmory.debugmessages");
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
        Inventory shopMenu = new QAInventoryHolder((shopping ? S_shopName : S_craftingBenchName) + page).getInventory();
        List<Gun> gunslistr = gunRegister.values().stream()
                .filter(CustomBaseObject::isEnableCrafting)
                .collect(Collectors.toList());
        List<Ammo> ammolistr = ammoRegister.values().stream()
                .filter(CustomBaseObject::isEnableCrafting)
                .collect(Collectors.toList());
        List<CustomBaseObject> misclistr = miscRegister.values().stream()
                .filter(CustomBaseObject::isEnableCrafting)
                .collect(Collectors.toList());
        List<ArmorObject> armorlistr = armorRegister.values().stream()
                .filter(CustomBaseObject::isEnableCrafting)
                .collect(Collectors.toList());

        List<CustomBaseObject> allItems = new ArrayList<>();
        allItems.addAll(gunslistr);
        allItems.addAll(ammolistr);
        allItems.addAll(misclistr);
        allItems.addAll(armorlistr);

        List<CustomBaseObject> filteredItems = allItems.stream()
                .filter(item -> (shopping && item.getPrice() >= 0 && item.isEnableShop()) || 
                               (!shopping && item.getIngredientsRaw() != null))
                .collect(Collectors.toList());

        int itemsPerPage = 9 * 5;
        int startIndex = page * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, filteredItems.size());

        shopMenu.setItem((9 * 6) - 1 - 8, prevButton);
        shopMenu.setItem((9 * 6) - 1, nextButton);

        for (int i = startIndex; i < endIndex; i++) {
            CustomBaseObject item = filteredItems.get(i);
            addToGUI(item, shopMenu, shopping);
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
                            String showName = LocalUtils.colorize((String) QAMain.m.a("EntityType." + itemName, itemName));
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
        main = this;
        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdirs();
        }

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

        Metrics metrics = new Metrics(this, 1699);

        metrics.addCustomChart(new Metrics.SimplePie("GunCount", () -> gunRegister.size() + ""));
        metrics.addCustomChart(
                new Metrics.SimplePie("uses_default_resourcepack", () -> overrideURL + ""));
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
                                    if (ITEM_enableUnbreakable && (!p.getItemInHand().getItemMeta().isUnbreakable()
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
                                                .isUnbreakable() && !ignoreUnbreaking)) {
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
        if (langFolder.exists() && !langFolder.isDirectory()) {
            langFolder.delete();
        }
        langFolder.mkdir();
        m = new MessagesYML(language, new File(langFolder, "message_" + language + ".yml"));
        prefix = LocalUtils.colorize((String) m.a("Prefix", prefix));
        S_ANVIL = LocalUtils.colorize((String) m.a("NoPermAnvilMessage", S_ANVIL));
        S_NOPERM = LocalUtils.colorize((String) m.a("NoPerm", S_NOPERM));
        S_RELOAD = LocalUtils.colorize((String) m.a("Reload", S_RELOAD));
        S_shopName = (String) m.a("ShopName", S_shopName);
        S_noMoney = LocalUtils.colorize((String) m.a("NoMoney", S_noMoney));
        S_craftingBenchName = (String) m.a("CraftingBenchName", S_craftingBenchName);
        S_missingIngredients = (String) m.a("Missing_Ingredients", S_missingIngredients);
        S_NORES1 = LocalUtils.colorize((String) m.a("NoResourcepackMessage1", S_NORES1));
        S_NORES2 = LocalUtils.colorize((String) m.a("NoResourcepackMessage2", S_NORES2));
        S_ITEM_AMMO = LocalUtils.colorize((String) m.a("Lore_Ammo", S_ITEM_AMMO));
        S_ITEM_BULLETS = LocalUtils.colorize((String) m.a("lore_bullets", S_ITEM_BULLETS));
        S_ITEM_DAMAGE = LocalUtils.colorize((String) m.a("Lore_Damage", S_ITEM_DAMAGE));
        S_ITEM_DURIB = LocalUtils.colorize((String) m.a("Lore_Durib", S_ITEM_DURIB));
        S_ITEM_ING = LocalUtils.colorize((String) m.a("Lore_ingredients", S_ITEM_ING));
        if (m.getConfig().contains("Lore_Varients"))
            S_ITEM_VARIANTS_LEGACY = LocalUtils.colorize(
                    (String) m.a("Lore_Varients", S_ITEM_VARIANTS_LEGACY));
        S_ITEM_VARIANTS_NEW = LocalUtils.colorize(
                (String) m.a("Lore_Variants", S_ITEM_VARIANTS_NEW));
        S_ITEM_COST = LocalUtils.colorize((String) m.a("Lore_Price", S_ITEM_COST));
        S_ITEM_DPS = LocalUtils.colorize((String) m.a("Lore_DamagePerSecond", S_ITEM_DPS));

        // Chris: add message Crafts
        S_ITEM_CRAFTS = LocalUtils.colorize((String) m.a("Lore_Crafts", S_ITEM_CRAFTS));
        S_ITEM_RETURNS = LocalUtils.colorize((String) m.a("Lore_Returns", S_ITEM_RETURNS));


        S_RELOADING_MESSAGE = LocalUtils.colorize(
                (String) m.a("Reloading_Message", S_RELOADING_MESSAGE));
        S_MAX_FOUND = LocalUtils.colorize((String) m.a("State_AmmoCount", S_MAX_FOUND));
        S_OUT_OF_AMMO = LocalUtils.colorize((String) m.a("State_OutOfAmmo", S_OUT_OF_AMMO));
        S_HOTBAR_FORMAT = LocalUtils.colorize((String) m.a("HotbarMessage", S_HOTBAR_FORMAT));

        S_KICKED_FOR_RESOURCEPACK = LocalUtils.colorize(
                (String) m.a("Kick_message_if_player_denied_request", S_KICKED_FOR_RESOURCEPACK));

        S_LMB_SINGLE = (String) m.a("Lore-LMB-Single", S_LMB_SINGLE);
        S_LMB_FULLAUTO = (String) m.a("Lore-LMB-FullAuto", S_LMB_FULLAUTO);
        S_RMB_RELOAD = (String) m.a("Lore-RMB-Reload", S_RMB_RELOAD);
        S_RMB_A1 = (String) m.a("Lore-Ironsights-RMB", S_RMB_A1);
        S_RMB_A2 = (String) m.a("Lore-Ironsights-Sneak", S_RMB_A2);
        S_RMB_R1 = (String) m.a("Lore-Reload-Dropitem", S_RMB_R1);
        S_RMB_R2 = (String) m.a("Lore-Reload-RMB", S_RMB_R2);
        S_HELMET_RMB = LocalUtils.colorize((String) m.a("Lore-Helmet-RMB", S_HELMET_RMB));

        S_BUYCONFIRM = LocalUtils.colorize((String) m.a("Shop_Confirm", S_BUYCONFIRM));

        S_RESOURCEPACK_HELP = (String) m.a("Resourcepack_InCaseOfCrash", S_RESOURCEPACK_HELP);
        S_RESOURCEPACK_DOWNLOAD = (String) m.a("Resourcepack_Download", S_RESOURCEPACK_DOWNLOAD);
        S_RESOURCEPACK_BYPASS = (String) m.a("Resourcepack_NowBypass", S_RESOURCEPACK_BYPASS);
        S_RESOURCEPACK_OPTIN = (String) m.a("Resourcepack_NowOptIn", S_RESOURCEPACK_OPTIN);

        S_GRENADE_PALREADYPULLPIN = LocalUtils.colorize((String) m.a("grenadeAlreadyPulled", S_GRENADE_PALREADYPULLPIN));
        S_GRENADE_PULLPIN = LocalUtils.colorize((String) m.a("grenadePull", S_GRENADE_PULLPIN));

        S_FULLYHEALED = LocalUtils.colorize((String) m.a("Medkit-FullyHealed", S_FULLYHEALED));
        S_MEDKIT_HEALING = LocalUtils.colorize(
                (String) m.a("Medkit-Healing", S_MEDKIT_HEALING));
        S_MEDKIT_BLEEDING = LocalUtils.colorize(
                (String) m.a("Medkit-Bleeding", S_MEDKIT_BLEEDING));

        S_MEDKIT_HEAL_AMOUNT = (double) m.a("Medkit-HEALING_HEARTS_AMOUNT", S_MEDKIT_HEAL_AMOUNT);

        S_MEDKIT_HEALDELAY = (double) m.a("Medkit-HEALING_WEAPPING_DELAY_IN_SECONDS", S_MEDKIT_HEALDELAY);

        S_MEDKIT_LORE_INFO = LocalUtils.colorize(
                (String) m.a("Medkit-Lore_RMB", S_MEDKIT_LORE_INFO));

        S_BLEEDOUT_LOSINGconscious = LocalUtils.colorize(
                (String) m.a("Bleeding.Losingconsciousness", S_BLEEDOUT_LOSINGconscious));
        S_BLEEDOUT_STARTBLEEDING = LocalUtils.colorize(
                (String) m.a("Bleeding.StartBleeding", S_BLEEDOUT_STARTBLEEDING));
        S_BULLETPROOFSTOPPEDBLEEDING = LocalUtils.colorize(
                (String) m.a("Bleeding.ProtectedByKevlar", S_BULLETPROOFSTOPPEDBLEEDING));
        S_prevPage = LocalUtils.colorize((String) m.a("gui.prevPage", S_prevPage));
        S_nextPage = LocalUtils.colorize((String) m.a("gui.nextPage", S_nextPage));
        bagAmmo = LocalUtils.colorize((String) m.a("AmmoBag.current", bagAmmo));
        bagAmmoType = LocalUtils.colorize((String) m.a("AmmoBag.type", bagAmmoType));

        Material glass = null;
        Material glass2 = null;
        try {
            glass = Material.matchMaterial("BLUE_STAINED_GLASS_PANE");
            glass2 = Material.matchMaterial("RED_STAINED_GLASS_PANE");
            prevButton = new ItemStack(glass, 1);
            nextButton = new ItemStack(glass2, 1);
            ItemMeta nextButtonMeta = nextButton.getItemMeta();
            nextButtonMeta.setDisplayName(S_nextPage);
            nextButton.setItemMeta(nextButtonMeta);
            ItemMeta prevButtonMeta = prevButton.getItemMeta();
            prevButtonMeta.setDisplayName(S_prevPage);
            prevButton.setItemMeta(prevButtonMeta);
        } catch (Error | Exception e45) {
            glass = Material.matchMaterial("STAINED_GLASS_PANE");
            prevButton = new ItemStack(glass, 1, (short) 14);
            nextButton = new ItemStack(glass, 1, (short) 5);
        }

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

        anticheatFix = (boolean) a("anticheatFix", anticheatFix);

        verboseLoadingLogging = (boolean) a("verboseItemLogging", verboseLoadingLogging);

        requirePermsToShoot = (boolean) a("enable_permssionsToShoot", requirePermsToShoot);

        sendOnJoin = (boolean) a("sendOnJoin", true);
        sendTitleOnJoin = (boolean) a("sendTitleOnJoin", false);
        resourcepackInvincibility = (boolean) a("resourcepackInvincibility", resourcepackInvincibility);
        secondsTilSend = Double.valueOf(a("SecondsTillRPIsSent", 5.0) + "");

        enableBulletTrails = (boolean) a("enableBulletTrails", true);
        smokeSpacing = Double.valueOf(a("BulletTrailsSpacing", 0.5) + "");

        enableArmorIgnore = (boolean) a("enableIgnoreArmorProtection", enableArmorIgnore);
        ignoreUnbreaking = (boolean) a("enableIgnoreUnbreakingChecks", ignoreUnbreaking);
        ignoreSkipping = (boolean) a("enableIgnoreSkipForBasegameItems", ignoreSkipping);

        ITEM_enableUnbreakable = (boolean) a("Items.enable_Unbreaking", ITEM_enableUnbreakable);

        // enableVisibleAmounts = (boolean) a("enableVisibleBulletCounts", false);
        reloadOnQ = (boolean) a("enableReloadingOnDrop", false);
        reloadOnF = (boolean) a("enableReloadingWhenSwapToOffhand", true);
        reloadOnFOnly = (boolean) a("enableReloadOnlyWhenSwapToOffhand", false);

        allowGunHitEntities = (boolean) a("allowGunHitEntities", false);


        // showOutOfAmmoOnItem = (boolean) a("showOutOfAmmoOnItem", false);
        showOutOfAmmoOnTitle = (boolean) a("showOutOfAmmoOnTitle", false);
        showReloadOnTitle = (boolean) a("showReloadingTitle", false);

        showAmmoInXPBar = (boolean) a("showAmmoInXPBar", false);
        perWeaponPermission = (boolean) a("perWeaponPermission", false);

        useMoveForRecoil = (boolean) a("useMoveForRecoil", useMoveForRecoil);

        weaponSwitchDelay = (double) a("weaponSwitchDelay", 0.0);

        enableExplosionDamage = (boolean) a("enableExplosionDamage", false);
        enableExplosionDamageDrop = (boolean) a("enableExplosionDamageDrop", false);

        enablePrimaryWeaponHandler = (boolean) a("enablePrimaryWeaponLimiter", false);
        primaryWeaponLimit = (int) a("weaponlimiter_primaries", primaryWeaponLimit);
        secondaryWeaponLimit = (int) a("weaponlimiter_secondaries", secondaryWeaponLimit);

        enableCrafting = (boolean) a("enableCrafting", true);
        enableShop = (boolean) a("enableShop", true);

        AUTOUPDATE = (boolean) a("AUTO-UPDATE", true);
        SWAP_TO_LMB_SHOOT = !(boolean) a("Swap-Reload-and-Shooting-Controls", false);

        orderShopByPrice = (boolean) a("Order-Shop-By-Price", orderShopByPrice);

        SHOW_BULLETS_LORE = (boolean) a("enable_lore_gun-bullets", false);
        ENABLE_LORE_INFO = (boolean) a("enable_lore_gun-info_messages", true);
        ENABLE_LORE_HELP = (boolean) a("enable_lore_control-help_messages", true);

        HeadshotOneHit = (boolean) a("Enable_Headshot_Instantkill", HeadshotOneHit);
        headshotPling = (boolean) a("Enable_Headshot_Notification_Sound", headshotPling);
        headshot_sound = (String) a("Headshot_Notification_Sound", headshot_sound);

        headshotBlacklist.clear();

        List<String> blacklist = (List<String>) a("Headshot_Blacklist", new ArrayList<String>());
        for (String s : blacklist) {
            try {
                headshotBlacklist.add(EntityType.valueOf(s));
            } catch (Error | Exception e4) {
            }
        }

        hit_sound = (String) a("Hit_Notification_Sound", hit_sound);
        enableHitSound = (boolean) a("Enable_Hit_Sound", enableHitSound);

        autoarm = (boolean) a("Enable_AutoArm_Grenades", autoarm);

        // ignoreArmorStands = (boolean) a("ignoreArmorStands", false);

        gravity = (double) a("gravityConstantForDropoffCalculations", gravity);

        allowGunReload = (boolean) a("allowGunReload", allowGunReload);
        AutoDetectResourcepackVersion = (boolean) a("Auto-Detect-Resourcepack", AutoDetectResourcepackVersion);
        MANUALLYSELECT18 = (boolean) a("ManuallyOverrideTo_1_8_systems",
                Bukkit.getPluginManager().isPluginEnabled("WetSponge") ? true : MANUALLYSELECT18);
        MANUALLYSELECT113 = (boolean) a("ManuallyOverrideTo_1_13_systems", MANUALLYSELECT113);
        MANUALLYSELECT14 = (boolean) a("ManuallyOverrideTo_1_14_systems", MANUALLYSELECT14);

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

        changeDeathMessages = (boolean) a("deathmessages.enable", changeDeathMessages);


        List<String> avoidTypes = (List<String>) a("impenetrableEntityTypes",
                Collections.singletonList(EntityType.ARROW.name()));
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
        if (getConfig().contains("SwapSneakToSingleFile")) {
            SwapSneakToSingleFire = (boolean) a("SwapSneakToSingleFire", a("SwapSneakToSingleFile", false));
            getConfig().set("SwapSneakToSingleFile", null);
            saveTheConfig = true;
        }
        SwapSneakToSingleFire = (boolean) a("SwapSneakToSingleFire", SwapSneakToSingleFire);

        List<String> destarray = (List<String>) a("DestructableMaterials",
                Collections.singletonList("MATERIAL_NAME_HERE"));
        destructableBlocks.addAll(GunYMLLoader.getMaterials(destarray));
        regenDestructableBlocksAfter = (int) a("RegenDestructableBlocksAfter", regenDestructableBlocksAfter);

        for (Material m : Material.values())
            if (m.isBlock())
                if (m.name().contains("DOOR") || m.name().contains("TRAPDOOR") || m.name().contains("BUTTON")
                        || m.name().contains("LEVER"))
                    interactableBlocks.add(m);
// Chris: default has 1.14 ItemType
        if ((MANUALLYSELECT18 || !isVersionHigherThan(1, 9)) && !MANUALLYSELECT113 && !MANUALLYSELECT14) {
            //1.8
            CustomItemManager.registerItemType(getDataFolder(), "gun", new me.zombie_striker.customitemmanager.qa.versions.V1_8.CustomGunItem());
        } else if ((!isVersionHigherThan(1, 14) || MANUALLYSELECT113) && !MANUALLYSELECT18 && !MANUALLYSELECT14) {
            //1.9 to 1.13
            CustomItemManager.registerItemType(getDataFolder(), "gun", new CustomGunItem());
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
        } else if (true || MANUALLYSELECT14) {
            //1.14. Use crossbows
            CustomItemManager.registerItemType(getDataFolder(), "gun", new me.zombie_striker.customitemmanager.qa.versions.V1_14.CustomGunItem().setOverrideAttackSpeed((boolean) a("overrideAttackSpeed", true)));
        }

        // Chris: if switch on, create default items.
        if (enableCreationOfFiles) {
            CustomItemManager.getItemType("gun").initItems(getDataFolder());
        }
        ((AbstractCustomGunItem) CustomItemManager.getItemType("gun")).initIronsights(getDataFolder());

        if (overrideURL) {
            if (!getConfig().contains("DefaultResourcepack")) {
                getConfig().set("DefaultResourcepack", CustomItemManager.getResourcepackProvider().serialize());
                saveTheConfig = true;
            } else {
                if (getConfig().get("DefaultResourcepack") instanceof String)
                    CustomItemManager.setResourcepack(new StaticPackProvider(getConfig().getString("DefaultResourcepack")));
                else {
                    ConfigurationSection packSection = getConfig().getConfigurationSection("DefaultResourcepack");
                    if (packSection != null) {
                        if (packSection.contains("21")) {
                            packSection.set("21-4", packSection.getString("21"));
                            packSection.set("21", null);
                            saveTheConfig = true;
                        }

                        CustomItemManager.setResourcepack(new MultiVersionPackProvider(packSection));
                    }
                }
            }
        } else {
            if (!getConfig().contains("DefaultResourcepack")
                    || !getConfig().getString("DefaultResourcepack").equals(CustomItemManager.getResourcepack(null))) {
                getConfig().set("DefaultResourcepack", CustomItemManager.getResourcepackProvider().serialize());
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
                    sender.sendMessage(prefix + ChatColor.WHITE + " This server is using version " + ChatColor.GREEN
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
                        DEBUG = !DEBUG;
                        sender.sendMessage(prefix + "Console debugging set to " + DEBUG);
                    } else {
                        sender.sendMessage(prefix + ChatColor.RED + S_NOPERM);
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
                            sender.sendMessage(prefix + ChatColor.RED + "You need to hold a gun to do this.");
                            return true;
                        }

                        sender.sendMessage(prefix + ChatColor.YELLOW + gun.toString());
                    } else {
                        sender.sendMessage(prefix + ChatColor.RED + S_NOPERM);
                        return true;
                    }
                    return true;

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
                    if (args.length > 1 && sender.hasPermission("qualityarmory.sendresourcepack.other")) {
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
                    if (!namesToBypass.contains(player.getName())) {
                        namesToBypass.add(player.getName());
                        resourcepackwhitelist.set("Names_Of_players_to_bypass", namesToBypass);
                    }

                    player.sendMessage(prefix + S_RESOURCEPACK_DOWNLOAD);
                    player.sendMessage(CustomItemManager.getResourcepack(player));
                    player.sendMessage(prefix + S_RESOURCEPACK_BYPASS);

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
                        sender.sendMessage(prefix + ChatColor.RED + S_NOPERM);
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("reload")) {
                    if (sender.hasPermission("qualityarmory.reload")) {
                        reloadConfig();
                        reloadVals();
                        sender.sendMessage(prefix + S_RELOAD);
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
                        for (CustomBaseObject g : miscRegister.values()) {
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
                            sender.sendMessage(prefix + " A valid location is required");
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
                        sender.sendMessage(prefix + sb.toString());
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
                        } else if (g instanceof AmmoBox) {
                            temp = CustomItemManager.getItemType("gun").getItem(g.getItemData());
                            ((AmmoBox) g).updateAmmoLore(temp, ((AmmoBox) g).getMaxAmmoCount());
                            NBT.modify(temp, nbt -> {
                                nbt.setUUID("uuid", UUID.randomUUID());
                            });
                            who.getInventory().addItem(temp);
                        } else {
                            temp = CustomItemManager.getItemType("gun").getItem(g.getItemData().getMat(), g.getItemData().getData(), g.getItemData().getVariant());
                            temp.setAmount(g.getCraftingReturn());
                            who.getInventory().addItem(temp);
                        }

                        sender.sendMessage(prefix + " Adding " + g.getName() + " to " + (sender == who ? "your" : who.getName() + "'s") + " inventory");
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

                        if (args.length == 2) {
                            CustomBaseObject g = QualityArmory.getCustomItemByName(args[1]);
                            if (g == null) {
                                player.openInventory(createCraft(0));
                                return true;
                            }

                            if (!lookForIngre(player, g)) {
                                player.sendMessage(QAMain.prefix + QAMain.S_missingIngredients);
                                return true;
                            }

                            removeForIngre(player, g);
                            ItemStack result = QualityArmory.getCustomItemAsItemStack(g);
                            result.setAmount(g.getCraftingReturn());

                            player.getInventory().addItem(result);

                            return true;
                        }

                        player.openInventory(createCraft(0));
                        return true;

                    }
                if (enableShop)
                    if (args[0].equalsIgnoreCase("shop")) {
                        if (args.length == 2 && sender.hasPermission("qualityarmory.shop.other")) {
                            Player target = Bukkit.getPlayer(args[1]);
                            if (target == null) {
                                sender.sendMessage(prefix + ChatColor.RED + "That player is not online");
                                return true;
                            }

                            target.openInventory(createShop(0));
                            return true;
                        }

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
        sender.sendMessage(LocalUtils.colorize(prefix + " Commands:"));
        sender.sendMessage(ChatColor.GOLD + "/QA give <Item> [player] [amount]:" + ChatColor.GRAY
                + " Gives the sender the item specified (guns, ammo, misc.)");
        sender.sendMessage(ChatColor.GOLD + "/QA craft [Item]:" + ChatColor.GRAY + " Opens the crafting menu.");
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
		/*InputStream defConfigStream = this.getResource("config.yml");
		if (defConfigStream != null) {
			config.setDefaults(
					YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
		}*/
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
}
