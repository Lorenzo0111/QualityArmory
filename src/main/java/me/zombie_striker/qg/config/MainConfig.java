package me.zombie_striker.qg.config;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.config.system.*;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The main configuration, {@code config.yml}.
 * <p>
 * YAML paths are derived from the structure: section field names and {@link Config} field names in kebab-case,
 * unless a key is given explicitly.
 */
public class MainConfig extends BaseConfiguration {
    private static final String DEFAULT_RESOURCEPACK_PATH = "resourcepack.default-pack";

    public Debug debug = new Debug();
    public General general = new General();
    public Compat compat = new Compat();
    public Resourcepack resourcepack = new Resourcepack();
    public Weapons weapons = new Weapons();
    public Interactions interactions = new Interactions();
    public UI ui = new UI();
    public Features features = new Features();
    public Permissions permissions = new Permissions();
    public Menu menu = new Menu();
    public Limiter limiter = new Limiter();
    public Items items = new Items();
    public Medkit medkit = new Medkit();
    public Combat combat = new Combat();
    public World world = new World();

    @Section
    @Comment("Use these if you are experiencing errors")
    public static class Debug {
        @Comment("Broadcasts debug messages to players with the qualityarmory.debugmessages permission")
        @Config(oldPath = "ENABLE-DEBUG")
        public boolean enabled = false;

        @Comment("Prints the full stack trace of errors to the console")
        @Config(oldPath = "log-exceptions")
        public boolean logExceptions = true;
    }

    @Section
    public static class General {
        @Comment("Language file to use from the lang folder, for example en, it or cn")
        @Config(oldPath = "language")
        public String language = "en";

        @Comment({"Forces the item system of an older Minecraft version: none, 1.8, 1.13 or 1.14",
                "Leave it on none unless you know you need it"})
        @Config
        public String versionOverride = "none";
    }

    @Section
    public static class Compat {
        @Comment("Whether guns can damage players on the same team or party")
        @Config(oldPath = "FriendlyFireEnabled")
        public boolean friendlyFire = false;

        public Hooks hooks = new Hooks();

        @Section
        @Comment("Integrations with protection plugins. A hook is only used if the plugin is installed")
        public static class Hooks {
            @Config(value = "worldguard", oldPath = "hooks.WorldGuard")
            public boolean worldGuard = false;
            @Config(oldPath = "hooks.Towny")
            public boolean towny = true;
            @Config(oldPath = "hooks.Residence")
            public boolean residence = true;
            @Config(value = "griefprevention", oldPath = "hooks.GriefPrevention")
            public boolean griefPrevention = true;
            @Config(oldPath = "hooks.Kingdoms")
            public boolean kingdoms = true;
        }

        /**
         * @param plugin The name of the protection plugin
         * @return Whether the hook for the given plugin is enabled. Unknown plugins are enabled by default.
         */
        public boolean isHookEnabled(String plugin) {
            switch (plugin.toLowerCase(Locale.ROOT)) {
                case "worldguard":
                    return hooks.worldGuard;
                case "towny":
                    return hooks.towny;
                case "residence":
                    return hooks.residence;
                case "griefprevention":
                    return hooks.griefPrevention;
                case "kingdoms":
                    return hooks.kingdoms;
                default:
                    return true;
            }
        }
    }

    @Section
    public static class Resourcepack {
        @Comment("Whether players are asked to download the resourcepack that shows the custom models")
        @Config(oldPath = "useDefaultResourcepack")
        public boolean enabled = true;

        public SendOnJoin sendOnJoin = new SendOnJoin();

        @Comment("Kicks players who decline the resourcepack")
        @Config(oldPath = "KickPlayerIfDeniedResourcepack")
        public boolean kickIfDenied = false;

        @Comment("Protects players from damage while the resourcepack is downloading")
        @Config(oldPath = "resourcepackInvincibility")
        public boolean invincibility = false;

        @Comment("Sends the resourcepack matching each player's client version")
        @Config(oldPath = "Auto-Detect-Resourcepack")
        public boolean autoDetectVersion = true;

        @Comment({"Uses the pack in default-pack instead of the bundled one.",
                "default-pack is either one URL or a list of <version>: <URL>"})
        @Config(oldPath = "DefaultResourcepackOverride")
        public boolean overrideDefault = false;

        @Section
        public static class SendOnJoin {
            @Comment("Sends the resourcepack when a player joins")
            @Config(oldPath = "sendOnJoin")
            public boolean enabled = true;

            @Comment("Shows a title while the resourcepack is sent")
            @Config(oldPath = "sendTitleOnJoin")
            public boolean title = false;

            @Comment("Seconds to wait after joining before sending the resourcepack")
            @Range(min = 0)
            @Config(oldPath = "SecondsTillRPIsSent")
            public double delay = 0.0;
        }
    }

    @Section
    public static class Weapons {
        @Comment("Whether guns lose durability when used")
        @Config(oldPath = "EnableWeaponDurability")
        public boolean durability = false;

        public Detection detection = new Detection();
        public BlockBullets blockBullets = new BlockBullets();
        public Sway sway = new Sway();

        @Comment("Whether bullets can hit entities")
        @Config(oldPath = "allowGunHitEntities")
        public boolean allowGunHitEntities = true;

        @Comment("Melee hits with a gun only count within this distance, in blocks")
        @Range(min = 0)
        @Config(oldPath = "hitDistance")
        public int hitDistance = 5;

        @Comment("Whether guns can be reloaded")
        @Config(oldPath = "allowGunReload")
        public boolean allowGunReload = true;

        @Comment("Whether guns have recoil")
        @Config(oldPath = "enableRecoil")
        public boolean enableRecoil = true;

        public Recoil recoil = new Recoil();

        @Comment("Seconds a player has to wait after switching weapons before shooting")
        @Range(min = 0)
        @Config(oldPath = "weaponSwitchDelay")
        public double switchDelaySeconds = 0.0;

        public Ironsights ironsights = new Ironsights();

        @Comment("Reloads automatically when the magazine is empty")
        @Config(oldPath = "automaticallyReloadGunWhenOutOfAmmo")
        public boolean autoReloadWhenOutOfAmmo = false;

        public Controls controls = new Controls();
        public Headshot headshot = new Headshot();
        public HitSound hitSound = new HitSound();

        @Comment("Entity types that bullets pass through")
        @Config(oldPath = "impenetrableEntityTypes")
        public List<EntityType> impenetrableEntities = new ArrayList<>(Collections.singletonList(EntityType.ARROW));

        @Section
        public static class Detection {
            @Comment("Distance between bullet hit checks, in blocks. Lower is more precise but slower")
            @Range(min = 0.01)
            @Config(oldPath = "BulletDetection.step")
            public double bulletStep = 0.10;

            @Comment("Gravity used to calculate bullet drop-off")
            @Range(min = 0)
            @Config(oldPath = "gravityConstantForDropoffCalculations")
            public double gravity = 0.05;
        }

        @Section
        @Comment("Which blocks stop bullets")
        public static class BlockBullets {
            @Config(oldPath = "BlockBullets.door")
            public boolean door = false;
            @Config(oldPath = "BlockBullets.halfslabs")
            public boolean halfslabs = false;
            @Config(oldPath = "BlockBullets.leaves")
            public boolean leaves = false;
            @Config(oldPath = "BlockBullets.water")
            public boolean water = false;
            @Config(oldPath = "BlockBullets.glass")
            public boolean glass = false;
        }

        @Section
        @Comment("Sway multipliers applied while moving. Higher is less accurate")
        public static class Sway {
            @Range(min = 0)
            @Config(oldPath = "generalModifiers.sway.Run")
            public double run = 2.0;
            @Range(min = 0)
            @Config(oldPath = "generalModifiers.sway.Walk")
            public double walk = 1.5;
            @Range(min = 0)
            @Config(oldPath = "generalModifiers.sway.Sneak")
            public double sneak = 0.7;
        }

        @Section
        public static class Recoil {
            @Comment("Applies recoil relative to where the player moved, which is smoother")
            @Config(oldPath = "useMoveForRecoil")
            public boolean usePlayerMove = true;
        }

        @Section
        public static class Ironsights {
            @Comment("Aim with right click instead of sneaking")
            @Config(oldPath = "IronSightsOnRightClick")
            public boolean rightClick = false;

            @Comment("Inverts sneaking: single fire while sneaking, automatic fire otherwise")
            @Config(oldPath = "SwapSneakToSingleFire")
            public boolean swapSneakToSingleFire = false;
        }

        @Section
        public static class Controls {
            @Comment("Reload when the drop key (Q) is pressed")
            @Config(oldPath = "enableReloadingOnDrop")
            public boolean reloadOnDrop = true;

            @Comment("Reload when the swap hands key (F) is pressed")
            @Config(oldPath = "enableReloadingWhenSwapToOffhand")
            public boolean reloadOnOffhandSwap = true;

            @Comment("Only reload with the swap hands key (F)")
            @Config(oldPath = "enableReloadOnlyWhenSwapToOffhand")
            public boolean reloadOnlyOnOffhandSwap = false;

            @Comment("Unload the gun when the drop key (Q) is pressed")
            @Config(oldPath = "enableUnloadingOnDrop")
            public boolean unloadOnDrop = false;
        }

        @Section
        public static class Headshot {
            @Comment("Headshots kill instantly")
            @Config(oldPath = "Enable_Headshot_Instantkill")
            public boolean instantKill = false;

            @Comment("Plays a sound to the shooter on headshots")
            @Config(oldPath = "Enable_Headshot_Notification_Sound")
            public boolean playNotificationSound = false;

            @Config(oldPath = "Headshot_Notification_Sound")
            public String notificationSound = "entity.experience_orb.pickup";

            @Comment("Entity types that can't be instantly killed by headshots")
            @Config(oldPath = "Headshot_Blacklist")
            public List<EntityType> blacklist = new ArrayList<>();
        }

        @Section
        public static class HitSound {
            @Comment("Plays a sound to the shooter when a bullet hits")
            @Config(oldPath = "Enable_Hit_Sound")
            public boolean enabled = false;

            @Config(oldPath = "Hit_Notification_Sound")
            public String sound = "ENTITY_EXPERIENCE_ORB_PICKUP";
        }
    }

    @Section
    public static class Interactions {
        @Comment("Replaces anvils with the armory crafting bench")
        @Config(oldPath = "overrideAnvil")
        public boolean overrideAnvil = false;

        @Comment("Lets players open chests while holding a gun")
        @Config(oldPath = "enableInteract.Chests")
        public boolean enableChestInteract = false;

        @Comment("Bullets don't hit players the shooter can't see")
        @Config(oldPath = "preventHiddenPlayers")
        public boolean preventHiddenPlayersHit = true;

        @Comment("Stops hoppers from picking up guns")
        @Config(oldPath = "preventGunsInHoppers")
        public boolean preventGunsInHoppers = true;
    }

    @Section
    public static class UI {
        @Comment("Tells players how to get the resourcepack manually if it crashes their game")
        @Config(oldPath = "showPossibleCrashHelpMessage")
        public boolean showPossibleCrashHelpMessage = true;

        @Comment("Shows the ammo count in the experience bar")
        @Config(oldPath = "showAmmoInXPBar")
        public boolean showAmmoInXpBar = false;

        @Comment("Shows a title when the gun is out of ammo")
        @Config(oldPath = "showOutOfAmmoOnTitle")
        public boolean showOutOfAmmoTitle = false;

        @Comment("Shows a title while reloading")
        @Config(oldPath = "showReloadingTitle")
        public boolean showReloadTitle = false;

        public DisableHotbarMessages disableHotbarMessages = new DisableHotbarMessages();

        @Section
        @Comment("Disable these if the hotbar messages cause problems")
        public static class DisableHotbarMessages {
            @Config(oldPath = "disableHotbarMessages.OutOfAmmo")
            public boolean outOfAmmo = false;
            @Config(oldPath = "disableHotbarMessages.Shoot")
            public boolean shoot = false;
            @Config(oldPath = "disableHotbarMessages.Reload")
            public boolean reload = false;
        }
    }

    @Section
    public static class Features {
        @Comment("Damages entities directly instead of through the player. Enable it if an anticheat blocks gun damage")
        @Config(oldPath = "anticheatFix")
        public boolean anticheatFix = false;

        @Comment("Bullet damage ignores armor protection")
        @Config(oldPath = "enableIgnoreArmorProtection")
        public boolean enableIgnoreArmorProtection = false;

        @Comment("Debug: stops QualityArmory from making custom items unbreakable while held")
        @Config(oldPath = "enableIgnoreUnbreakingChecks")
        public boolean enableIgnoreUnbreakingChecks = false;

        @Comment("Debug: skips the monitor click handler for vanilla items")
        @Config(oldPath = "enableIgnoreSkipForBasegameItems")
        public boolean enableIgnoreSkipForBasegameItems = false;

        @Comment("Logs every item loaded at startup")
        @Config(oldPath = "verboseItemLogging")
        public boolean verboseItemLogging = false;

        @Comment("Explosions from rockets and grenades damage blocks")
        @Config(oldPath = "enableExplosionDamage")
        public boolean enableExplosionDamage = false;

        @Comment("Makes items glow in their configured color")
        @Config(oldPath = "EnableGlowEffects")
        public boolean enableGlowEffects = false;

        @Comment("Strips colors from the hotbar message. Enable it if clients show translation keys")
        @Config(oldPath = "unknownTranslationKeyFixer")
        public boolean unknownTranslationKeyFixer = false;

        @Comment("Shows block break particles where bullets hit")
        @Config(oldPath = "Break-Block-Texture-If-Shot")
        public boolean breakBlockTextureIfShot = true;

        @Comment("Grenades are armed as soon as they are thrown")
        @Config(oldPath = "Enable_AutoArm_Grenades")
        public boolean enableAutoArmGrenades = false;

        @Comment("Gives the previous offhand item back after aiming")
        @Config(oldPath = "restoreOffHand")
        public boolean restoreOffhand = false;

        @Config(oldPath = "enableBulletTrails")
        public boolean enableBulletTrails = true;

        @Comment("Distance between bullet trail particles, in blocks")
        @Range(min = 0.01)
        @Config(oldPath = "BulletTrailsSpacing")
        public double bulletTrailsSpacing = 0.5;

        @Comment("Creates the default gun, ammo and item files")
        @Config(oldPath = "Enable_Creation_Of_Default_Files")
        public boolean enableCreationOfDefaultFiles = true;

        @Comment("Downloads new versions automatically")
        @Config(oldPath = "AUTO-UPDATE")
        public boolean autoUpdate = true;

        @Comment("Shoot with right click and reload with left click")
        @Config(oldPath = "Swap-Reload-and-Shooting-Controls")
        public boolean swapReloadAndShootingControls = false;

        @Comment("Shows the bullet count in the gun lore")
        @Config(oldPath = "enable_lore_gun-bullets")
        public boolean enableLoreGunBullets = false;

        @Comment("Shows damage, DPS and ammo type in the gun lore")
        @Config(oldPath = "enable_lore_gun-info_messages")
        public boolean enableLoreGunInfoMessages = true;

        @Comment("Shows the controls in the gun lore")
        @Config(oldPath = "enable_lore_control-help_messages")
        public boolean enableLoreControlHelpMessages = true;
    }

    @Section
    public static class Permissions {
        @Comment("Require qualityarmory.usegun to shoot")
        @Config(oldPath = "enable_permssionsToShoot")
        public boolean requireShoot = false;

        @Comment("Require qualityarmory.craftgun to craft guns")
        @Config(oldPath = "enable_permissionsToCraft")
        public boolean requireCraft = false;

        @Comment("Require qualityarmory.shopgun to buy guns")
        @Config(oldPath = "enable_permissionsToBuy")
        public boolean requireBuy = false;

        public PerWeapon perWeapon = new PerWeapon();

        @Section
        @Comment("Require a permission per weapon, for example qualityarmory.craftgun.<name>")
        public static class PerWeapon {
            @Config(oldPath = "perWeaponPermission")
            public boolean shoot = false;
            @Config(oldPath = "perWeaponCraftPermission")
            public boolean craft = false;
            @Config(oldPath = "perWeaponBuyPermission")
            public boolean buy = false;
        }
    }

    @Section
    public static class Menu {
        @Config(oldPath = "enableCrafting")
        public boolean enableCrafting = true;
        @Config(oldPath = "enableShop")
        public boolean enableShop = true;
        @Config(oldPath = "Order-Shop-By-Price")
        public boolean orderShopByPrice = false;
    }

    @Section
    @Comment("Limits how many weapons a player can carry")
    public static class Limiter {
        public Primary primary = new Primary();
        public Secondary secondary = new Secondary();

        @Section
        public static class Primary {
            @Config(oldPath = "enablePrimaryWeaponLimiter")
            public boolean enabled = false;
            @Range(min = 0)
            @Config(oldPath = "weaponlimiter_primaries")
            public int max = 2;
        }

        @Section
        public static class Secondary {
            @Range(min = 0)
            @Config(oldPath = "weaponlimiter_secondaries")
            public int max = 2;
        }
    }

    @Section
    public static class Items {
        @Comment("Makes custom items unbreakable")
        @Config(oldPath = "Items.enable_Unbreaking")
        public boolean enableUnbreaking = true;

        @Comment("Removes the attack cooldown from guns (1.14+ items)")
        @Config(oldPath = "overrideAttackSpeed")
        public boolean overrideAttackSpeed = true;
    }

    @Section
    public static class Medkit {
        @Comment("Hearts healed per healing step")
        @Range(min = 0)
        @Config
        public double healAmount = 1;

        @Comment("Seconds a full heal takes")
        @Range(min = 0.05)
        @Config
        public double healDelaySeconds = 6;
    }

    @Section
    public static class Combat {
        public Bleeding bleeding = new Bleeding();
        public DeathMessages deathMessages = new DeathMessages();

        @Section
        @Comment("Experimental: bullet wounds make players bleed until they use a medkit")
        public static class Bleeding {
            @Config(oldPath = "experimental.BulletWounds.enableBleeding")
            public boolean enabled = false;
            @Range(min = 1)
            @Config(oldPath = "experimental.BulletWounds.InitialBloodLevel")
            public double initialBloodLevel = 1500;
            @Config(oldPath = "experimental.BulletWounds.BloodIncreasePerSecond")
            public double bloodIncreasePerSecond = 0.01;
            @Config(oldPath = "experimental.BulletWounds.Medkit_Heal_Bloodloss_Rate")
            public double medkitHealBloodlossRate = 0.05;
        }

        @Section
        public static class DeathMessages {
            @Comment("Replaces death messages with ones naming the gun used")
            @Config(oldPath = "deathmessages.enable")
            public boolean enabled = true;
        }
    }

    @Section
    public static class World {
        @Comment("Blocks that explosions and bullets can destroy")
        @Config(oldPath = "DestructableMaterials")
        public List<String> destructableMaterials = new ArrayList<>(Collections.singletonList("MATERIAL_NAME_HERE"));

        @Comment("Seconds before destroyed blocks come back. -1 to never regenerate them")
        @Config(oldPath = "RegenDestructableBlocksAfter")
        public int regenDestructableBlocksAfter = -1;
    }

    public MainConfig() {
        super(new File(QAMain.getInstance().getDataFolder(), "config.yml"), QAMain.getInstance().getLogger());

        // Pre-v3 flat config
        migration(3, config -> {
            if (config.getBoolean("ignoreArmorStands")) {
                List<String> types = config.contains("weapons.impenetrable-entities")
                        ? new ArrayList<>(config.getStringList("weapons.impenetrable-entities"))
                        : new ArrayList<>(Collections.singletonList(EntityType.ARROW.name()));
                if (!types.contains(EntityType.ARMOR_STAND.name()))
                    types.add(EntityType.ARMOR_STAND.name());
                config.set("weapons.impenetrable-entities", types);
            }
            config.set("ignoreArmorStands", null);

            if (!config.contains("general.version-override")) {
                if (config.getBoolean("ManuallyOverrideTo_1_8_systems"))
                    config.set("general.version-override", "1.8");
                else if (config.getBoolean("ManuallyOverrideTo_1_13_systems"))
                    config.set("general.version-override", "1.13");
                else if (config.getBoolean("ManuallyOverrideTo_1_14_systems"))
                    config.set("general.version-override", "1.14");
            }
            config.set("ManuallyOverrideTo_1_8_systems", null);
            config.set("ManuallyOverrideTo_1_13_systems", null);
            config.set("ManuallyOverrideTo_1_14_systems", null);

            // Old typo of SwapSneakToSingleFire
            move("SwapSneakToSingleFile", "weapons.ironsights.swap-sneak-to-single-fire");

            // Options that no longer exist
            config.set("generalModifiers.sway.Ironsights", null);
            config.set("enableExplosionDamageDrop", null);
        });

        // The default resourcepack moved into the resourcepack section
        migration(4, config -> move("DefaultResourcepack", DEFAULT_RESOURCEPACK_PATH));
    }

    @Override
    public int getVersion() {
        return 4;
    }

    @Override
    protected String[] getHeader() {
        return new String[]{
                "QualityArmory configuration",
                "Use /qa reload to apply changes."
        };
    }

    /**
     * The default resourcepack is either a single URL or a section of version -> URL,
     * so it is stored raw instead of through a {@link Config} field.
     *
     * @return The raw default resourcepack value (a String or a ConfigurationSection), or null if unset.
     */
    public Object getDefaultResourcepack() {
        return this.config.get(DEFAULT_RESOURCEPACK_PATH);
    }

    /**
     * Sets the raw default resourcepack value. Call {@link #save()} to persist it.
     *
     * @param value A String URL, a Map of version -> URL, or null to remove it.
     */
    public void setDefaultResourcepack(Object value) {
        if (value instanceof Map) {
            this.config.createSection(DEFAULT_RESOURCEPACK_PATH, (Map<?, ?>) value);
        } else {
            this.config.set(DEFAULT_RESOURCEPACK_PATH, value);
        }
    }
}
