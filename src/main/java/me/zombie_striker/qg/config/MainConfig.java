package me.zombie_striker.qg.config;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.config.system.BaseConfiguration;
import me.zombie_striker.qg.config.system.Config;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainConfig extends BaseConfiguration {
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
    public VersionOverride versionOverride = new VersionOverride();
    public Combat combat = new Combat();
    public World world = new World();

    public static class Debug {
        @Config(value = "debug.enabled", oldPath = "ENABLE-DEBUG")
        public boolean enabled = false;

        @Config(value = "debug.log-exceptions", oldPath = "log-exceptions")
        public boolean logExceptions = true;
    }

    public static class General {
        @Config(value = "general.language", oldPath = "language")
        public String language = "en";
    }

    public static class Compat {
        @Config(value = "compat.friendly-fire", oldPath = "FriendlyFireEnabled")
        public boolean friendlyFire = false;
    }

    public static class Resourcepack {
        @Config(value = "resourcepack.kick-if-denied", oldPath = "KickPlayerIfDeniedResourcepack")
        public boolean kickIfDeniedRequest = false;

        @Config(value = "resourcepack.use-default", oldPath = "useDefaultResourcepack")
        public boolean shouldSend = true;

        @Config(value = "resourcepack.send-on-join", oldPath = "sendOnJoin")
        public boolean sendOnJoin = true;

        @Config(value = "resourcepack.send-title-on-join", oldPath = "sendTitleOnJoin")
        public boolean sendTitleOnJoin = false;

        @Config(value = "resourcepack.invincibility", oldPath = "resourcepackInvincibility")
        public boolean resourcepackInvincibility = false;

        @Config(value = "resourcepack.send-delay-seconds", oldPath = "SecondsTillRPIsSent")
        public double secondsTilSend = 0.0;

        @Config(value = "resourcepack.auto-detect-version", oldPath = "Auto-Detect-Resourcepack")
        public boolean autoDetectVersion = true;

        @Config(value = "resourcepack.override-default-pack", oldPath = "DefaultResourcepackOverride")
        public boolean overrideDefaultPack = false;
    }

    public static class Weapons {
        @Config(value = "weapons.durability.enabled", oldPath = "EnableWeaponDurability")
        public boolean enableDurability = false;
        @Config(value = "weapons.detection.bullet-step", oldPath = "BulletDetection.step")
        public double bulletStep = 0.10;
        @Config(value = "weapons.block-bullets.door", oldPath = "BlockBullets.door")
        public boolean blockbullet_door = false;
        @Config(value = "weapons.block-bullets.halfslabs", oldPath = "BlockBullets.halfslabs")
        public boolean blockbullet_halfslabs = false;
        @Config(value = "weapons.block-bullets.leaves", oldPath = "BlockBullets.leaves")
        public boolean blockbullet_leaves = false;
        @Config(value = "weapons.block-bullets.water", oldPath = "BlockBullets.water")
        public boolean blockbullet_water = false;
        @Config(value = "weapons.block-bullets.glass", oldPath = "BlockBullets.glass")
        public boolean blockbullet_glass = false;
        @Config(value = "weapons.detection.gravity", oldPath = "gravityConstantForDropoffCalculations")
        public double gravity = 0.05;
        @Config(value = "weapons.sway.run", oldPath = "generalModifiers.sway.Run")
        public double swayModifier_Run = 2.0;
        @Config(value = "weapons.sway.walk", oldPath = "generalModifiers.sway.Walk")
        public double swayModifier_Walk = 1.5;
        @Config(value = "weapons.sway.ironsights", oldPath = "generalModifiers.sway.Ironsights")
        public double swayModifier_Ironsights = 0.8;
        @Config(value = "weapons.sway.sneak", oldPath = "generalModifiers.sway.Sneak")
        public double swayModifier_Sneak = 0.7;
        @Config(value = "weapons.allow-gun-hit-entities", oldPath = "allowGunHitEntities")
        public boolean allowGunHitEntities = true;
        @Config(value = "weapons.hit-distance", oldPath = "hitDistance")
        public int hitDistance = 5;
        @Config(value = "weapons.allow-gun-reload", oldPath = "allowGunReload")
        public boolean allowGunReload = true;
        @Config(value = "weapons.enable-recoil", oldPath = "enableRecoil")
        public boolean enableRecoil = true;
        @Config(value = "weapons.recoil.use-player-move", oldPath = "useMoveForRecoil")
        public boolean useMoveForRecoil = true;
        @Config(value = "weapons.switch-delay-seconds", oldPath = "weaponSwitchDelay")
        public double weaponSwitchDelay = 0.0;
        @Config(value = "weapons.ironsights.right-click", oldPath = "IronSightsOnRightClick")
        public boolean enableIronSightsON_RIGHT_CLICK = false;
        @Config(value = "weapons.ironsights.swap-sneak-to-single-fire", oldPath = "SwapSneakToSingleFire")
        public boolean SwapSneakToSingleFire = false;
        @Config(value = "weapons.auto-reload-when-out-of-ammo", oldPath = "automaticallyReloadGunWhenOutOfAmmo")
        public boolean enableReloadWhenOutOfAmmo = false;
        @Config(value = "weapons.controls.reload-on-drop", oldPath = "enableReloadingOnDrop")
        public boolean reloadOnQ = true;
        @Config(value = "weapons.controls.reload-on-offhand-swap", oldPath = "enableReloadingWhenSwapToOffhand")
        public boolean reloadOnF = true;
        @Config(value = "weapons.controls.reload-only-on-offhand-swap", oldPath = "enableReloadOnlyWhenSwapToOffhand")
        public boolean reloadOnFOnly = false;
        @Config(value = "weapons.controls.unload-on-drop", oldPath = "enableUnloadingOnDrop")
        public boolean unloadOnQ = false;
        @Config(value = "weapons.headshot.instant-kill", oldPath = "Enable_Headshot_Instantkill")
        public boolean HeadshotOneHit = false;
        @Config(value = "weapons.headshot.play-notification-sound", oldPath = "Enable_Headshot_Notification_Sound")
        public boolean headshotPling = false;
        @Config(value = "weapons.headshot.notification-sound", oldPath = "Headshot_Notification_Sound")
        public String headshot_sound = "entity.experience_orb.pickup";
        @Config(value = "weapons.hit-sound.enabled", oldPath = "Enable_Hit_Sound")
        public boolean enableHitSound = false;
        @Config(value = "weapons.hit-sound.sound", oldPath = "Hit_Notification_Sound")
        public String hit_sound = "ENTITY_EXPERIENCE_ORB_PICKUP";
        @Config(value = "weapons.headshot.blacklist", oldPath = "Headshot_Blacklist")
        public List<String> headshotBlacklist = Collections.emptyList();
        @Config(value = "weapons.impenetrable-entities", oldPath = "impenetrableEntityTypes")
        public List<String> impenetrableEntityTypes = Arrays.asList(EntityType.ARROW.name());
    }

    public static class Interactions {
        @Config(value = "interactions.override-anvil", oldPath = "overrideAnvil")
        public boolean overrideAnvil = false;
        @Config(value = "interactions.enable-chest-interact", oldPath = "enableInteract.Chests")
        public boolean enableInteractChests = false;
        @Config(value = "interactions.prevent-hidden-players-hit", oldPath = "preventHiddenPlayers")
        public boolean preventHiddenPlayers = true;
        @Config(value = "interactions.prevent-guns-in-hoppers", oldPath = "preventGunsInHoppers")
        public boolean preventGunsInHoppers = true;
    }

    public static class UI {
        @Config(value = "ui.show-possible-crash-help-message", oldPath = "showPossibleCrashHelpMessage")
        public boolean showCrashMessage = true;
        @Config(value = "ui.show-ammo-in-xp-bar", oldPath = "showAmmoInXPBar")
        public boolean showAmmoInXPBar = false;
        @Config(value = "ui.show-out-of-ammo-title", oldPath = "showOutOfAmmoOnTitle")
        public boolean showOutOfAmmoOnTitle = false;
        @Config(value = "ui.show-reload-title", oldPath = "showReloadingTitle")
        public boolean showReloadOnTitle = false;
        @Config(value = "ui.disable-hotbar-messages.out-of-ammo", oldPath = "disableHotbarMessages.OutOfAmmo")
        public boolean disableHotBarMessageOnOutOfAmmo = false;
        @Config(value = "ui.disable-hotbar-messages.shoot", oldPath = "disableHotbarMessages.Shoot")
        public boolean disableHotBarMessageOnShoot = false;
        @Config(value = "ui.disable-hotbar-messages.reload", oldPath = "disableHotbarMessages.Reload")
        public boolean disableHotBarMessageOnReload = false;
    }

    public static class Features {
        @Config(value = "features.anticheat-fix", oldPath = "anticheatFix")
        public boolean anticheatFix = false;
        @Config(value = "features.enable-ignore-armor-protection", oldPath = "enableIgnoreArmorProtection")
        public boolean enableArmorIgnore = false;
        @Config(value = "features.enable-ignore-unbreaking-checks", oldPath = "enableIgnoreUnbreakingChecks")
        public boolean ignoreUnbreaking = false;
        @Config(value = "features.enable-ignore-skip-for-basegame-items", oldPath = "enableIgnoreSkipForBasegameItems")
        public boolean ignoreSkipping = false;
        @Config(value = "features.verbose-item-logging", oldPath = "verboseItemLogging")
        public boolean verboseLoadingLogging = false;
        @Config(value = "features.enable-explosion-damage", oldPath = "enableExplosionDamage")
        public boolean enableExplosionDamage = false;
        @Config(value = "features.enable-explosion-damage-drop", oldPath = "enableExplosionDamageDrop")
        public boolean enableExplosionDamageDrop = false;
        @Config(value = "features.enable-glow-effects", oldPath = "EnableGlowEffects")
        public boolean addGlowEffects = false;
        @Config(value = "features.unknown-translation-key-fixer", oldPath = "unknownTranslationKeyFixer")
        public boolean unknownTranslationKeyFixer = false;
        @Config(value = "features.break-block-texture-if-shot", oldPath = "Break-Block-Texture-If-Shot")
        public boolean blockBreakTexture = true;
        @Config(value = "features.enable-auto-arm-grenades", oldPath = "Enable_AutoArm_Grenades")
        public boolean autoarm = false;
        @Config(value = "features.restore-offhand", oldPath = "restoreOffHand")
        public boolean restoreOffHand = false;
        @Config(value = "features.enable-bullet-trails", oldPath = "enableBulletTrails")
        public boolean enableBulletTrails = true;
        @Config(value = "features.bullet-trails-spacing", oldPath = "BulletTrailsSpacing")
        public double smokeSpacing = 0.5;
        @Config(value = "features.enable-creation-of-default-files", oldPath = "Enable_Creation_Of_Default_Files")
        public boolean enableCreationOfFiles = true;
        @Config(value = "features.auto-update", oldPath = "AUTO-UPDATE")
        public boolean AUTOUPDATE = true;
        @Config(value = "features.swap-reload-and-shooting-controls", oldPath = "Swap-Reload-and-Shooting-Controls")
        public boolean SwapReloadAndShootingControls = false;
        @Config(value = "features.enable-lore-gun-bullets", oldPath = "enable_lore_gun-bullets")
        public boolean SHOW_BULLETS_LORE = false;
        @Config(value = "features.enable-lore-gun-info-messages", oldPath = "enable_lore_gun-info_messages")
        public boolean ENABLE_LORE_INFO = true;
        @Config(value = "features.enable-lore-control-help-messages", oldPath = "enable_lore_control-help_messages")
        public boolean ENABLE_LORE_HELP = true;
    }

    public static class Permissions {
        @Config(value = "permissions.require-shoot", oldPath = "enable_permssionsToShoot")
        public boolean requirePermsToShoot = false;
        @Config(value = "permissions.require-craft", oldPath = "enable_permissionsToCraft")
        public boolean requirePermsToCraft = false;
        @Config(value = "permissions.require-buy", oldPath = "enable_permissionsToBuy")
        public boolean requirePermsToBuy = false;
        @Config(value = "permissions.per-weapon.shoot", oldPath = "perWeaponPermission")
        public boolean perWeaponPermission = false;
        @Config(value = "permissions.per-weapon.craft", oldPath = "perWeaponCraftPermission")
        public boolean perWeaponCraftPermission = false;
        @Config(value = "permissions.per-weapon.buy", oldPath = "perWeaponBuyPermission")
        public boolean perWeaponBuyPermission = false;
    }

    public static class Menu {
        @Config(value = "menu.enable-crafting", oldPath = "enableCrafting")
        public boolean enableCrafting = true;
        @Config(value = "menu.enable-shop", oldPath = "enableShop")
        public boolean enableShop = true;
        @Config(value = "menu.order-shop-by-price", oldPath = "Order-Shop-By-Price")
        public boolean orderShopByPrice = false;
    }

    public static class Limiter {
        @Config(value = "limiter.primary.enabled", oldPath = "enablePrimaryWeaponLimiter")
        public boolean enablePrimaryWeaponHandler = false;
        @Config(value = "limiter.primary.max", oldPath = "weaponlimiter_primaries")
        public int primaryWeaponLimit = 2;
        @Config(value = "limiter.secondary.max", oldPath = "weaponlimiter_secondaries")
        public int secondaryWeaponLimit = 2;
    }

    public static class Items {
        @Config(value = "items.enable-unbreaking", oldPath = "Items.enable_Unbreaking")
        public boolean ITEM_enableUnbreakable = true;
        @Config(value = "items.override-attack-speed", oldPath = "overrideAttackSpeed")
        public boolean overrideAttackSpeed = true;
    }

    public static class VersionOverride {
        @Config(value = "version-override.force-1-8", oldPath = "ManuallyOverrideTo_1_8_systems")
        public boolean MANUALLYSELECT18 = false;
        @Config(value = "version-override.force-1-13", oldPath = "ManuallyOverrideTo_1_13_systems")
        public boolean MANUALLYSELECT113 = false;
        @Config(value = "version-override.force-1-14", oldPath = "ManuallyOverrideTo_1_14_systems")
        public boolean MANUALLYSELECT14 = false;
    }

    public static class Combat {
        @Config(value = "combat.bleeding.enabled", oldPath = "experimental.BulletWounds.enableBleeding")
        public boolean enableBleeding = false;
        @Config(value = "combat.bleeding.initial-blood-level", oldPath = "experimental.BulletWounds.InitialBloodLevel")
        public double bulletWound_initialbloodamount = 1500;
        @Config(value = "combat.bleeding.blood-increase-per-second", oldPath = "experimental.BulletWounds.BloodIncreasePerSecond")
        public double bulletWound_BloodIncreasePerSecond = 0.01;
        @Config(value = "combat.bleeding.medkit-heal-bloodloss-rate", oldPath = "experimental.BulletWounds.Medkit_Heal_Bloodloss_Rate")
        public double bulletWound_MedkitBloodlossHealRate = 0.05;
        @Config(value = "combat.death-messages.enabled", oldPath = "deathmessages.enable")
        public boolean changeDeathMessages = true;
    }

    public static class World {
        @Config(value = "world.destructable-materials", oldPath = "DestructableMaterials")
        public List<String> destructableMaterials = Collections.singletonList("MATERIAL_NAME_HERE");
        @Config(value = "world.regen-destructable-blocks-after", oldPath = "RegenDestructableBlocksAfter")
        public int regenDestructableBlocksAfter = -1;
    }

    public MainConfig() {
        super(new File(QAMain.getInstance().getDataFolder(), "config.yml"));
    }


    @Override
    public int getVersion() {
        return 3;
    }

    @Override
    public void customMigrate() {
        if (this.config.contains("ignoreArmorStands") && this.config.getBoolean("ignoreArmorStands")) {
            if (!weapons.impenetrableEntityTypes.contains(EntityType.ARMOR_STAND.name())) {
                weapons.impenetrableEntityTypes.add(EntityType.ARMOR_STAND.name());
            }
        }
    }
}
