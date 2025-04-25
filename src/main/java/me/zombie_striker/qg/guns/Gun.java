package me.zombie_striker.qg.guns;

import com.cryptomorin.xseries.XPotion;
import de.tr7zw.changeme.nbtapi.NBT;
import me.zombie_striker.customitemmanager.*;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.ammo.Ammo;
import me.zombie_striker.qg.api.QAWeaponPrepareShootEvent;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.guns.chargers.ChargingHandler;
import me.zombie_striker.qg.guns.projectiles.ProjectileManager;
import me.zombie_striker.qg.guns.projectiles.RealtimeCalculationProjectile;
import me.zombie_striker.qg.guns.reloaders.ReloadingHandler;
import me.zombie_striker.qg.guns.utils.GunRefillerRunnable;
import me.zombie_striker.qg.guns.utils.GunUtil;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.guns.utils.WeaponType;
import me.zombie_striker.qg.handlers.IronsightsHandler;
import me.zombie_striker.qg.handlers.Update19OffhandChecker;
import me.zombie_striker.qg.utils.LocalUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Gun extends CustomBaseObject implements ArmoryBaseObject, Comparable<Gun> {
    private static final String CALCTEXT = ChatColor.DARK_GRAY + "qadata:";

    // Refers to the last time a player shot a gun, on a per-gun basis.
    // Doing this should prevent players from fast-switching to get around
    // bullet-delays
    private final Map<UUID, Long> lastShot = new HashMap<>();
    private final Map<UUID, Long> lastRMB = new HashMap<>();

    private WeaponType type;
    private Ammo ammotype;
    private boolean unlimitedAmmo = false;
    private int maxBullets;
    private double reloadTime = 1.5;
    private ChargingHandler ch = null;
    private ReloadingHandler rh = null;
    private RealtimeCalculationProjectile customProjectile = null;

    private String reloadingSound = WeaponSounds.RELOAD_MAG_OUT.getSoundName();
    private String chargingSound = WeaponSounds.RELOAD_BOLT.getSoundName();
    private List<String> weaponSounds;
    private double volume = 4;

    private double recoil = 1;
    private double velocity = 2;
    private float durabilityDamage;
    private int maxDistance = 150;
    private int zoomLevel = 0;
    private boolean hasIronSights;
    private double delayBetweenShots = 0.25;
    private int shotsPerBullet = 1;
    private int fireRate = 1;
    private boolean isAutomatic;
    private boolean isPrimaryWeapon = true;
    private int lightLevelOnShoot = 20;
    private double explosionRadius = 10;

    private boolean nightVisionOnScope = false;
    private boolean useOffhandOverride = true;
    private boolean enableMuzzleSmoke = false;
    private boolean supports18 = false;
    private int durability = 1000;

    private Particle particle = null;
    private Material particleMaterial = Material.COAL_BLOCK;
    private int particleData = 1;
    private double particle_r = 1;
    private double particle_g = 1;
    private double particle_b = 1;
    public ChatColor glowEffect = null;

    private double knockbackPower = 0;
    private int slownessPower = 0;
    private double sway;
    private double swayMultiplier = 2;
    private double swayUnscopedMultiplier = 1;
    private double headshotMultiplier = 2;
    private boolean enableSwayMovementModifier = true;
    private boolean enableSwaySneakModifier = true;
    private boolean enableSwayRunModifier = true;

    private final List<Material> breakableMaterials = new ArrayList<>();
    private String killedByMessage = "%player% was shot by %killer% using a %name%";

    @Deprecated
    public Gun(String name, MaterialStorage id, WeaponType type, boolean hasIronSights, Ammo ammo, double sway, double swayMultiplier,
               int maxBullets, float durabilityDamage, boolean isAutomatic, int durability, String ws, double cost,
               ItemStack[] ingredients) {
        this(name, id, type, hasIronSights, ammo, sway, swayMultiplier, maxBullets, durabilityDamage, isAutomatic, durability, ws,
                null, ChatColor.GOLD + name, cost, ingredients);
    }

    @Deprecated
    public Gun(String name, MaterialStorage id, WeaponType type, boolean hasIronSights, Ammo ammo, double sway, double swayMultiplier,
               int maxBullets, float durabilityDamage, boolean isAutomatic, int durability, WeaponSounds ws, double cost,
               ItemStack[] ingredients) {
        this(name, id, type, hasIronSights, ammo, sway, swayMultiplier, maxBullets, durabilityDamage, isAutomatic, durability, ws, null,
                ChatColor.GOLD + name, cost, ingredients);
    }

    @Deprecated
    public Gun(String name, MaterialStorage id, WeaponType type, boolean hasIronSights, Ammo ammo, double sway, double swayMultiplier,
               int maxBullets, float durabilityDamage, boolean isAutomatic, int durability, WeaponSounds ws, List<String> lore,
               String displayname, double cost, ItemStack[] ingredients) {
        this(name, id, type, hasIronSights, ammo, sway, swayMultiplier, maxBullets, durabilityDamage, isAutomatic, durability, ws.getSoundName(),
                lore, displayname, cost, ingredients);
    }

    @Deprecated
    public Gun(String name, MaterialStorage id, WeaponType type, boolean hasIronSights, Ammo ammo, double sway, double swayMultiplier,
               int maxBullets, float durabilityDamage, boolean isAutomatic, int durability, String ws, List<String> lore,
               String displayname, double cost, ItemStack[] ingredients) {
        super(name, id, LocalUtils.colorize(displayname), lore, true);
        this.type = type;
        this.hasIronSights = hasIronSights;
        this.ammotype = ammo;
        this.sway = sway;
        this.maxBullets = maxBullets;
        this.durabilityDamage = durabilityDamage;
        this.durability = durability;
        this.swayMultiplier = swayMultiplier;
        this.isAutomatic = isAutomatic;
        this.weaponSounds = new ArrayList<>();
        this.weaponSounds.add(ws);

        setIngredients(ingredients);
        setPrice(cost);
    }

    public Gun(String name, MaterialStorage id) {
        super(name, id, name, null, true);
    }

    @SuppressWarnings("deprecation")
    public static boolean USE_THIS_INSTEAD_OF_INDIVIDUAL_SHOOT_METHODS(Gun g, Player player, double acc, boolean holdingRMB) {
        boolean offhand = QualityArmory.isIronSights(player.getInventory().getItemInHand());
        if ((!offhand && getAmount(player) > 0)
                || (offhand && Update19OffhandChecker.hasAmountOFfhandGreaterthan(player, 0))) {
            QAWeaponPrepareShootEvent shootEvent = new QAWeaponPrepareShootEvent(player, g);
            Bukkit.getPluginManager().callEvent(shootEvent);
            if (shootEvent.isCancelled())
                return false;
            GunUtil.basicShoot(offhand, g, player, acc, holdingRMB);
            return true;
        }
        return false;
    }

    public static int getAmount(Player player) {
        return getAmount(player.getInventory().getItemInHand());
    }

    public static int getAmount(ItemStack is) {
        if (is.getType().equals(Material.AIR)) return 0;

        return NBT.get(is, nbt -> nbt.hasTag("ammo") ? nbt.getInteger("ammo") : 0);
    }

    public static void updateAmmo(Gun g, ItemStack current, int amount) {
        NBT.modify(current, nbt -> {
            nbt.setInteger("ammo", amount);
        });
    }

    public static void updateAmmo(Gun g, Player player, int amount) {
        ItemStack current = player.getInventory().getItemInHand();
        updateAmmo(g, current, amount);

        if (QAMain.showAmmoInXPBar)
            GunUtil.updateXPBar(player, g, amount);
    }

    public static List<String> getGunLore(Gun g, ItemStack current, int amount) {
        List<String> lore = (current != null && current.hasItemMeta() && current.getItemMeta().hasLore()) ? current.getItemMeta().getLore() : new ArrayList<>();
        OLD_ItemFact.addVariantData(null, lore, g);
        if (QAMain.ENABLE_LORE_INFO) {
            lore.add(QAMain.S_ITEM_DAMAGE + ": " + g.getDurabilityDamage());
            lore.add(QAMain.S_ITEM_DPS + ": "
                    + (g.isAutomatic()
                    ? (2 * g.getFireRate() * g.getDurabilityDamage()) + ""
                    + (g.getBulletsPerShot() > 1 ? "x" + g.getBulletsPerShot() : "")
                    : "" + ((int) (1.0 / g.getDelayBetweenShotsInSeconds()) * g.getDurabilityDamage())
                    + (g.getBulletsPerShot() > 1 ? "x" + g.getBulletsPerShot() : "")));
            if (g.getAmmoType() != null)
                lore.add(QAMain.S_ITEM_AMMO + ": " + g.getAmmoType().getDisplayName());
        }
        if (QAMain.AutoDetectResourcepackVersion && Bukkit.getPluginManager().isPluginEnabled("ViaRewind")) {
            if (g.is18Support()) {
                lore.add(ChatColor.GRAY + "1.8 Weapon");
            }
        }

        if (QAMain.enableDurability)
            if (current == null) {
                double k = ((double) g.getDurabilityDamage()) / g.getDurability();
                ChatColor c = k > 0.5 ? ChatColor.DARK_GREEN : k > 0.25 ? ChatColor.GOLD : ChatColor.DARK_RED;
                lore.add(c + QAMain.S_ITEM_DURIB + ":" + g.getDurability() + "/" + g.getDurability());
            } else {
                lore = setDurabilityDamage(g, lore, getDamage(current));
            }
        if (QAMain.ENABLE_LORE_HELP) {
            if (g.isAutomatic()) {
                lore.add(QAMain.S_LMB_SINGLE);
                lore.add(QAMain.S_LMB_FULLAUTO);
                lore.add(QAMain.S_RMB_RELOAD);
            } else {
                lore.add(QAMain.S_LMB_SINGLE);
                lore.add(QAMain.enableIronSightsON_RIGHT_CLICK ? QAMain.S_RMB_R1 : QAMain.S_RMB_R2);
                if (g.hasIronSights())
                    lore.add(QAMain.enableIronSightsON_RIGHT_CLICK ? QAMain.S_RMB_A1 : QAMain.S_RMB_A2);
            }
        }

        if (current != null && current.hasItemMeta() && current.getItemMeta().hasLore())
            for (String s : current.getItemMeta().getLore()) {
                if (ChatColor.stripColor(s).contains("UUID")) {
                    lore.add(s);
                    break;
                }
            }
        return lore;
    }

    public static int getDamage(ItemStack is) {
        if (is != null && is.hasItemMeta() && is.getItemMeta().hasLore())
            for (String lore : is.getItemMeta().getLore()) {
                if (ChatColor.stripColor(lore).startsWith(ChatColor.stripColor(QAMain.S_ITEM_DURIB))) {
                    return Integer.parseInt(lore.split(":")[1].split("/")[0].trim());
                }
            }
        return -1;
    }

    public static ItemStack durabilityDamage(Gun g, ItemStack is) {
        return setDurabilityDamage(g, is, getDamage(is) - 1);
    }

    public static ItemStack setDurabilityDamage(Gun g, ItemStack is, int damage) {
        ItemMeta im = is.getItemMeta();
        im.setLore(setDurabilityDamage(g, im.getLore(), damage));
        is.setItemMeta(im);
        return is;
    }

    public static List<String> setDurabilityDamage(Gun g, List<String> lore, int damage) {
        boolean foundLine = false;
        double k = ((double) damage) / g.getDurability();
        ChatColor c = k > 0.5 ? ChatColor.DARK_GREEN : k > 0.25 ? ChatColor.GOLD : ChatColor.DARK_RED;
        for (int j = 0; j < lore.size(); j++) {
            if (ChatColor.stripColor(lore.get(j)).contains(ChatColor.stripColor(QAMain.S_ITEM_DURIB))) {
                lore.set(j, c + QAMain.S_ITEM_DURIB + ":" + damage + "/" + g.getDurability());
                foundLine = true;
                break;
            }
        }
        if (!foundLine) {
            lore.add(c + QAMain.S_ITEM_DURIB + ":" + damage + "/" + g.getDurability());
        }
        return lore;
    }

    public static int getCalculatedExtraDurability(ItemStack is) {
        if (CustomItemManager.isUsingCustomData())
            return -1;
        if (!is.hasItemMeta() || !is.getItemMeta().hasLore() || is.getItemMeta().getLore().isEmpty())
            return -1;
        List<String> lore = is.getItemMeta().getLore();
        for (int i = 0; i < lore.size(); i++) {
            if (lore.get(i).startsWith(CALCTEXT))
                return Integer.parseInt(lore.get(i).split(CALCTEXT)[1]);
        }
        return -1;
    }

    public static ItemStack addCalculatedExtraDurability(ItemStack is, int number) {
        if (CustomItemManager.isUsingCustomData())
            return is;
        ItemMeta im = is.getItemMeta();
        List<String> lore = im.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        } else {
            if (getCalculatedExtraDurability(is) != -1)
                is = removeCalculatedExtra(is);
        }
        lore.add(CALCTEXT + number);
        im.setLore(lore);
        is.setItemMeta(im);
        return is;
    }

    public static ItemStack decrementCalculatedExtra(ItemStack is) {
        if (CustomItemManager.isUsingCustomData())
            return is;
        ItemMeta im = is.getItemMeta();
        List<String> lore = is.getItemMeta().getLore();
        for (int i = 0; i < lore.size(); i++) {
            if (lore.get(i).startsWith(CALCTEXT)) {
                lore.set(i, CALCTEXT + "" + (Integer.parseInt(lore.get(i).split(CALCTEXT)[1]) - 1));
            }
        }
        im.setLore(lore);
        is.setItemMeta(im);
        return is;
    }

    public static ItemStack removeCalculatedExtra(ItemStack is) {
        if (is.hasItemMeta() && is.getItemMeta().hasLore()) {
            ItemMeta im = is.getItemMeta();
            List<String> lore = is.getItemMeta().getLore();
            for (int i = 0; i < lore.size(); i++) {
                if (lore.get(i).startsWith(CALCTEXT)) {
                    lore.remove(i);
                }
            }
            im.setLore(lore);
            is.setItemMeta(im);
        }
        return is;
    }

    public void copyFrom(Gun g) {
        this.setIngredientsRaw(g.getIngredientsRaw());
        this.type = g.type;
        this.hasIronSights = g.hasIronSights;
        this.zoomLevel = g.zoomLevel;
        this.ammotype = g.ammotype;
        this.sway = g.sway;
        this.swayMultiplier = g.swayMultiplier;
        this.maxBullets = g.maxBullets;
        this.durabilityDamage = g.durabilityDamage;
        this.durability = g.durability;
        this.isAutomatic = g.isAutomatic;
        this.supports18 = g.supports18;
        this.nightVisionOnScope = g.nightVisionOnScope;
        this.headshotMultiplier = g.headshotMultiplier;
        this.isPrimaryWeapon = g.isPrimaryWeapon;
        this.explosionRadius = g.explosionRadius;
        this.setCustomLore(g.getCustomLore());
        this.weaponSounds = g.weaponSounds;
        this.setPrice(g.getPrice());
        this.setEnableShop(g.isEnableShop());
        this.delayBetweenShots = g.delayBetweenShots;
        this.shotsPerBullet = g.shotsPerBullet;
        this.fireRate = g.fireRate;
        this.ch = g.ch;
        this.rh = g.rh;
        this.maxDistance = g.maxDistance;
        this.particle = g.particle;
        this.particle_r = g.particle_r;
        this.particle_g = g.particle_g;
        this.particle_b = g.particle_b;
        this.particleMaterial = g.particleMaterial;
        this.particleData = g.particleData;
        this.lightLevelOnShoot = g.lightLevelOnShoot;
        this.enableMuzzleSmoke = g.enableMuzzleSmoke;
        this.glowEffect = g.glowEffect;
        this.unlimitedAmmo = g.unlimitedAmmo;
        this.customProjectile = g.customProjectile;
        this.velocity = g.velocity;
        this.recoil = g.recoil;
    }

    public void setHasIronSights(boolean b) {
        this.hasIronSights = b;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }

    public void setSwayMultiplier(double multiplier) {
        this.swayMultiplier = multiplier;
    }

    public double getHeadshotMultiplier() {
        return headshotMultiplier;
    }

    public void setHeadshotMultiplier(double dam) {
        headshotMultiplier = dam;
    }

    public ChatColor getGlow() {
        return glowEffect;
    }

    /**
     * Sets the glow for the item. Null to disable the glow.
     */
    public void setGlow(ChatColor glow) {
        this.glowEffect = glow;
    }

    public int getLightOnShoot() {
        return lightLevelOnShoot;
    }

    public void setLightOnShoot(int level) {
        lightLevelOnShoot = level;
    }

    public double getRecoil() {
        return recoil;
    }

    public void setRecoil(double d) {
        this.recoil = d;
    }

    public void enableBetterAimingAnimations(boolean b) {
        useOffhandOverride = b;
    }

    public boolean hasBetterAimingAnimations() {
        return useOffhandOverride;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double f) {
        this.volume = f;
    }

    public double getReloadTime() {
        return reloadTime;
    }

    public void setReloadingTimeInSeconds(double time) {
        this.reloadTime = time;
    }

    public void setNightVision(boolean nightVisionOnScope) {
        this.nightVisionOnScope = nightVisionOnScope;
    }

    public void setAmmo(Ammo ammo) {
        this.ammotype = ammo;
    }

    public boolean hasNightVision() {
        return nightVisionOnScope;
    }

    public boolean usesCustomProjectiles() {
        return customProjectile != null;
    }

    public RealtimeCalculationProjectile getCustomProjectile() {
        return customProjectile;
    }

    public void setCustomProjectile(String key) {
        this.customProjectile = ProjectileManager.getHandler(key);
    }

    public void setRealtimeVelocity(double velocity) {
        this.velocity = velocity;
    }

    public double getVelocityForRealtimeCalculations() {
        return velocity;
    }

    public double getExplosionRadius() {
        return explosionRadius;
    }

    public void setExplosionRadius(double d) {
        this.explosionRadius = d;
    }

    public int getBulletsPerShot() {
        return shotsPerBullet;
    }

    public void setBulletsPerShot(int i) {
        this.shotsPerBullet = i;
    }

    public void setZoomLevel(int zoom) {
        this.zoomLevel = zoom;
    }

    public int getZoomWhenIronSights() {
        return zoomLevel;
    }

    public int getFireRate() {
        return fireRate;
    }

    public void setFireRate(int fireRate) {
        this.fireRate = fireRate;
    }

    public WeaponType getWeaponType() {
        return type;
    }

    public boolean isPrimaryWeapon() {
        return isPrimaryWeapon;
    }

    public void setIsPrimary(boolean isPrimary) {
        this.isPrimaryWeapon = isPrimary;
    }

    public boolean isAutomatic() {
        return isAutomatic;
    }

    public void setAutomatic(boolean automatic) {
        this.isAutomatic = automatic;
    }

    public void setUnlimitedAmmo(boolean b) {
        this.unlimitedAmmo = b;
    }

    public boolean shoot(Player player) {
        return shoot(player, false);
    }

    public boolean shoot(Player player, boolean holdingRMB) {
        return Gun.USE_THIS_INSTEAD_OF_INDIVIDUAL_SHOOT_METHODS(this, player, getSway(), holdingRMB);
    }

    public void setDeathMessage(String deathMessage) {
        this.killedByMessage = deathMessage;
    }

    public String getDeathMessage() {
        return killedByMessage;
    }

    public int getMaxBullets() {
        return maxBullets;
    }

    public void setMaxBullets(int amount) {
        this.maxBullets = amount;
    }

    public boolean playerHasAmmo(Player player) {
        if (player.getGameMode() == GameMode.CREATIVE)
            return true;
        if (hasUnlimitedAmmo())
            return true;
        if (getAmmoType() == null)
            return true;
        return GunUtil.hasAmmo(player, this);
    }

    public void setSound(WeaponSounds sound) {
        setSound(sound.getSoundName());
    }

    public void setSound(String sound) {
        this.weaponSounds.clear();
        this.weaponSounds.add(sound);
    }

    public void setSounds(List<String> sound) {
        this.weaponSounds = sound;
    }

    public void reload(Player player) {
        if (getChargingHandler() == null || (getReloadingHandler() == null || !getReloadingHandler().isReloading(player)))
            GunUtil.basicReload(this, player, hasUnlimitedAmmo(), reloadTime);
    }

    public double getDurabilityDamage() {
        return durabilityDamage;
    }

    public void setDurabilityDamage(float damage) {
        this.durabilityDamage = damage;
    }

    public int getDurability() {
        return this.durability;
    }

    public void setSwayUnscopedMultiplier(double multiplier) {
        this.swayUnscopedMultiplier = multiplier;
    }

    public double getSwayUnscopedMultiplier() {
        return swayUnscopedMultiplier;
    }

    public Ammo getAmmoType() {
        return ammotype;
    }

    public boolean hasIronSights() {
        return hasIronSights;
    }

    public boolean hasUnlimitedAmmo() {
        if (unlimitedAmmo)
            return true;
        return ammotype == null;
    }

    public double getSway() {
        return sway;
    }

    public void setSway(double sway) {
        this.sway = sway;
    }

    public double getMovementMultiplier() {
        return swayMultiplier;
    }

    @Deprecated
    public String getWeaponSound() {
        return weaponSounds.get(0);
    }

    public List<String> getWeaponSounds() {
        return weaponSounds;
    }

    public double getDelayBetweenShotsInSeconds() {
        return delayBetweenShots;
    }

    public void setDelayBetweenShots(double seconds) {
        this.delayBetweenShots = seconds;
    }

    public Map<UUID, Long> getLastShotForGun() {
        return lastShot;
    }

    public ChargingHandler getChargingHandler() {
        return ch;
    }

    public void setChargingHandler(ChargingHandler ch) {
        this.ch = ch;
    }

    public ReloadingHandler getReloadingHandler() {
        return rh;
    }

    public void setReloadingHandler(ReloadingHandler rh) {
        this.rh = rh;
    }

    public int getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(int a) {
        this.maxDistance = a;
    }

    @Override
    public boolean is18Support() {
        return supports18;
    }

    @Override
    public void set18Supported(boolean b) {
        supports18 = b;
    }

    public Particle getParticle() {
        return particle;
    }

    public double getParticleR() {
        return particle_r;
    }

    public double getParticleG() {
        return particle_g;
    }

    public double getParticleB() {
        return particle_b;
    }

    public int getParticleData() {
        return particleData;
    }

    public Material getParticleMaterial() {
        return this.particleMaterial;
    }

    public void setParticles(Particle p) {
        this.setParticles(p, 1, 1, 1, Material.COAL_BLOCK);
    }

    public void setParticles(Particle p, int data) {
        this.setParticles(p, 1, 1, 1, Material.COAL_BLOCK, data);
    }

    public void setParticles(Particle p, double r, double g, double b, Material m) {
        setParticles(p, r, g, b, m, 0);
    }

    public void setParticles(Particle p, double r, double g, double b, Material m, int data) {
        particle = p;
        this.particleData = data;
        this.particle_r = r;
        this.particle_g = g;
        this.particle_b = b;
        this.particleMaterial = m;
    }

    public boolean useMuzzleSmoke() {
        return enableMuzzleSmoke;
    }

    public void setUseMuzzleSmoke(boolean b) {
        this.enableMuzzleSmoke = b;
    }

    @Override
    public int compareTo(@NotNull Gun arg0) {
        if (QAMain.orderShopByPrice) {
            return (int) (this.getPrice() - arg0.getPrice());
        }
        return this.getDisplayName().compareTo(arg0.getDisplayName());
    }

    @Override
    public boolean onRMB(Player e, ItemStack usedItem) {
        return onClick(e, usedItem, (QAMain.reloadOnFOnly || !QAMain.SWAP_TO_LMB_SHOOT));
    }

    @Override
    public boolean onShift(Player shooter, ItemStack usedItem, boolean toggle) {
        return false;
    }

    @Override
    public boolean onLMB(Player e, ItemStack usedItem) {
        return onClick(e, usedItem, QAMain.SWAP_TO_LMB_SHOOT);
    }

    @Override
    public boolean onSwapTo(Player shooter, ItemStack usedItem) {
        if (getSoundOnEquip() != null)
            shooter.getWorld().playSound(shooter.getLocation(), getSoundOnEquip(), 1, 1);
        if (getSlownessPower() > 0) {
            if (shooter.hasPotionEffect(XPotion.SLOWNESS.getPotionEffectType()))
                if (shooter.getPotionEffect(XPotion.SLOWNESS.getPotionEffectType()).getAmplifier() != getSlownessPower())
                    shooter.removePotionEffect(XPotion.SLOWNESS.getPotionEffectType());
            shooter.addPotionEffect(new PotionEffect(XPotion.SLOWNESS.getPotionEffectType(), Integer.MAX_VALUE, getSlownessPower()));
        }
        return false;
    }

    @Override
    public boolean onSwapAway(Player shooter, ItemStack usedItem) {
        if (getSlownessPower() > 0) {
            if (shooter.hasPotionEffect(XPotion.SLOWNESS.getPotionEffectType()))
                if (shooter.getPotionEffect(XPotion.SLOWNESS.getPotionEffectType()).getAmplifier() == getSlownessPower())
                    shooter.removePotionEffect(XPotion.SLOWNESS.getPotionEffectType());
        }
        return false;
    }

    @Override
    public String getSoundOnEquip() {
        return null;
    }

    @Override
    public String getSoundOnHit() {
        return null;
    }

    @SuppressWarnings("deprecation")
    private boolean onClick(final Player player, ItemStack usedItem, boolean fire) {
        QAMain.DEBUG("CLICKED GUN " + getName());
        if (QAMain.requirePermsToShoot && !player.getPlayer().hasPermission("qualityarmory.usegun")) {
            player.getPlayer().sendMessage(QAMain.S_NOPERM);
            return true;
        }
        if (QAMain.perWeaponPermission && !player.getPlayer().hasPermission("qualityarmory.usegun." + getName())) {
            player.getPlayer().sendMessage(QAMain.S_NOPERM);
            return true;
        }

        QAMain.DEBUG("Dups check");
        QAMain.checkforDups(player.getPlayer(), usedItem);

        ItemStack offhandItem = Update19OffhandChecker.getItemStackOFfhand(player.getPlayer());
        boolean offhand = offhandItem != null && offhandItem.equals(usedItem);

        QAMain.DEBUG("Made it to gun/attachment check : " + getName());
        try {
            if (QAMain.enableInteractChests) {
                Block b = player.getTargetBlock(null, 6);
                if (b != null
                        && (b.getType() == Material.CHEST
                        || b.getType() == Material.TRAPPED_CHEST)
                        || b.getType() == Material.ENDER_CHEST) {
                    QAMain.DEBUG("Chest interactable check has return true!");
                    return true;
                }
            }
        } catch (Error | Exception e4) {
        }

        if (fire) {
            QAMain.DEBUG("Fire mode called");
            if (player.getPlayer().getItemInHand().getItemMeta().getDisplayName().contains(QAMain.S_RELOADING_MESSAGE)) {
                if (!GunRefillerRunnable.hasItemReloaded(player, usedItem)) {
                    ItemStack tempUsed = usedItem;
                    ItemMeta im = tempUsed.getItemMeta();
                    im.setDisplayName(getDisplayName());
                    tempUsed.setItemMeta(im);
                    if (offhand) {
                        Update19OffhandChecker.setOffhand(player.getPlayer(), tempUsed);
                        QAMain.DEBUG("odd. Reloading broke. Removing reloading message from offhand - firing");
                    } else {
                        player.getPlayer().setItemInHand(tempUsed);
                        QAMain.DEBUG("odd. Reloading broke. Removing reloading message from mainhand - firing");
                    }
                }
                QAMain.DEBUG("Reloading message 1!");
                return true;
            }
            if (!isAutomatic() && GunUtil.rapidfireshooters.containsKey(player.getPlayer().getUniqueId())) {
                GunUtil.rapidfireshooters.remove(player.getPlayer().getUniqueId()).cancel();
                if (QAMain.enableReloadWhenOutOfAmmo) {
                    if (getAmount(player) <= 0) {
                        if (offhand) {
                            player.getPlayer().setItemInHand(player.getPlayer().getInventory().getItemInOffHand());
                            player.getPlayer().getInventory().setItemInOffHand(null);
                            usedItem = player.getPlayer().getItemInHand();
                            offhand = false;
                        }
                        if (QAMain.allowGunReload) {
                            QualityArmory.sendHotbarGunAmmoCount(player.getPlayer(), this, usedItem, ((getMaxBullets() != getAmount(player))
                                    && GunUtil.hasAmmo(player.getPlayer(), this)));
                            if ((getMaxBullets() != getAmount(player))) {
                                QAMain.DEBUG("Ammo full");
                            } else if (playerHasAmmo(player.getPlayer())) {
                                QAMain.DEBUG("Trying to reload WITH AUTORELOAD. player has ammo");
                                reload(player.getPlayer());
                            } else {
                                if (QAMain.showOutOfAmmoOnItem) {
                                    QAMain.DEBUG("UNSUPPORTED: Out of ammo displayed on item");
                                }
                                QAMain.DEBUG("Trying to reload WITH AUTORELOAD. player DOES NOT have ammo");
                            }
                        }
                        return true;
                    }
                }
            } else {
                QAMain.DEBUG("About to fire single shot");
                if (getAmount(player) <= 0) {
                    QAMain.DEBUG("Out of ammo");

                    if (GunUtil.rapidfireshooters.containsKey(player.getUniqueId()))
                        GunUtil.rapidfireshooters.remove(player.getUniqueId()).cancel();

                    player.playSound(player.getLocation(), WeaponSounds.OUT_OF_AMMO_CLICK.getSoundName(), 1f, 1f);

                    if (QAMain.enableReloadWhenOutOfAmmo) {
                        if (offhand) {
                            player.getPlayer().setItemInHand(player.getPlayer().getInventory().getItemInOffHand());
                            player.getPlayer().getInventory().setItemInOffHand(null);
                            usedItem = player.getPlayer().getItemInHand();
                            offhand = false;
                        }
                        if (QAMain.allowGunReload) {
                            QualityArmory.sendHotbarGunAmmoCount(player.getPlayer(), this, usedItem,
                                    ((getMaxBullets() != getAmount(player))
                                            && GunUtil.hasAmmo(player.getPlayer(), this)));
                            if (playerHasAmmo(player.getPlayer())) {
                                QAMain.DEBUG("Trying to reload WITH AUTORELOAD. player has ammo");
                                reload(player.getPlayer());

                            } else {
                                QAMain.DEBUG("Trying to reload WITH AUTORELOAD. player DOES NOT have ammo");
                            }
                        }
                    }
                    return true;
                }
                if (!QAMain.enableDurability || getDamage(usedItem) > 0) {
                    boolean automaticFiring = true;
                    if (!QAMain.SWAP_TO_LMB_SHOOT) {
                        if (lastRMB.containsKey(player.getUniqueId())) {
                            if (System.currentTimeMillis() - lastRMB.get(player.getUniqueId()) <= 600) {
                                automaticFiring = true;
                            }
                        }
                        lastRMB.put(player.getUniqueId(), System.currentTimeMillis());
                    }
                    if (!GunUtil.isDelay(this, player) && getKnockbackPower() != 0) {
                        Vector currentVelocity = player.getVelocity();
                        currentVelocity.add(player.getLocation().getDirection().normalize().multiply(-getKnockbackPower()));
                        player.setVelocity(currentVelocity);
                    }
                    QAMain.DEBUG("" + shoot(player.getPlayer(), automaticFiring));
                } else {
                    player.getPlayer().playSound(player.getPlayer().getLocation(), WeaponSounds.METALHIT.getSoundName(), 1, 1);
                    QAMain.DEBUG("Durability less than 0");
                }
            }

            QualityArmory.sendHotbarGunAmmoCount(player.getPlayer(), this, usedItem, false);
            return true;

        } else {
            QAMain.DEBUG("Non-Fire mode activated");


            if (QAMain.enableIronSightsON_RIGHT_CLICK) {
                if (!Update19OffhandChecker.supportOffhand(player.getPlayer())) {
                    QAMain.enableIronSightsON_RIGHT_CLICK = false;
                    QAMain.DEBUG("Offhand checker returned false for the player. Disabling ironsights");
                    return true;
                }
                // Rest should be okay
                if (hasIronSights()) {
                    try {

                        if (player.getPlayer().getItemInHand().getItemMeta().getDisplayName()
                                .contains(QAMain.S_RELOADING_MESSAGE)) {
                            if (!GunRefillerRunnable.hasItemReloaded(player, usedItem)) {
                                ItemStack tempUsed = usedItem.clone();
                                ItemMeta im = tempUsed.getItemMeta();
                                im.setDisplayName(getDisplayName());
                                tempUsed.setItemMeta(im);
                                if (offhand) {
                                    Update19OffhandChecker.setOffhand(player.getPlayer(), tempUsed);
                                    QAMain.DEBUG(
                                            "odd. Reloading broke. Removing reloading message from offhand - reload");
                                } else {
                                    player.getPlayer().setItemInHand(tempUsed);
                                    QAMain.DEBUG(
                                            "odd. Reloading broke. Removing reloading message from mainhand - reload");
                                }
                            }
                            QAMain.DEBUG("Reloading message 1!");
                            return true;
                        }

                        IronsightsHandler.aim(player);

                        final Gun checkTo = QualityArmory
                                .getGun(Update19OffhandChecker.getItemStackOFfhand(player.getPlayer()));
                        new BukkitRunnable() {

                            @Override
                            public void run() {
                                if (!player.getPlayer().isOnline()) {
                                    QAMain.DEBUG("Canceling since player is offline");
                                    cancel();
                                    return;
                                }
                                Gun g = null;
                                if (!QualityArmory.isIronSights(player.getPlayer().getItemInHand())
                                        || (g = QualityArmory.getGun(
                                        Update19OffhandChecker.getItemStackOFfhand(player.getPlayer()))) == null
                                        || g != checkTo) {
                                    QAMain.toggleNightvision(player.getPlayer(), checkTo, false);
                                    QAMain.DEBUG(
                                            "Removing night vision since either the main hand is not ironsights/offhand gun is null. : "
                                                    + (!QualityArmory.isIronSights(player.getPlayer().getItemInHand()))
                                                    + " "
                                                    + ((g = QualityArmory.getGun(Update19OffhandChecker
                                                    .getItemStackOFfhand(player.getPlayer()))) == null)
                                                    + " " + (g != checkTo));
                                    cancel();
                                    return;
                                }

                            }
                        }.runTaskTimer(QAMain.getInstance(), 20, 20);


                        QualityArmory.sendHotbarGunAmmoCount(player.getPlayer(), this, usedItem, false);
                    } catch (Error e2) {
                        Bukkit.broadcastMessage(QAMain.prefix
                                + "Ironsights not compatible for versions lower than 1.8. The server owner should set EnableIronSights to false in the plugin's config");
                    }
                } else {
                }
                QAMain.DEBUG("Ironsights on RMB finished");
            } else {
                QAMain.DEBUG("Reload called");
                Block targetblock = player.getTargetBlock(null, 5);
                if (targetblock != null && QAMain.interactableBlocks.contains(targetblock.getType())) {
                    QAMain.DEBUG("Canceled interact because block is " + targetblock.getType().name());
                    return false;
                } else {
                    if (QAMain.allowGunReload) {
                        QualityArmory.sendHotbarGunAmmoCount(player.getPlayer(), this, usedItem,
                                ((getMaxBullets() != getAmount(player))
                                        && GunUtil.hasAmmo(player.getPlayer(), this)));
                        if (playerHasAmmo(player.getPlayer())) {
                            QAMain.DEBUG("Trying to reload. player has ammo");
                            reload(player.getPlayer());
                        } else {
                            QAMain.DEBUG("Trying to reload. player DOES NOT have ammo");
                        }
                    } else {
                        QAMain.DEBUG("Reloading has been disabled");
                    }
                }
            }

        }
        QAMain.DEBUG("Reached end for gun-check!");
        return true;
    }

    public void damageDurability(Player player) {
        if (QAMain.enableDurability) {
            if (QualityArmory.isIronSights(player.getItemInHand())) {
                Update19OffhandChecker.setOffhand(player, durabilityDamage(this, Update19OffhandChecker.getItemStackOFfhand(player)));
            } else {
                player.setItemInHand(durabilityDamage(this, player.getItemInHand()));
            }
        }
    }

    @Override
    public ItemStack getItemStack() {
        return CustomItemManager.getItemType("gun").getItem(this.getItemData().getMat(), this.getItemData().getData(), this.getItemData().getVariant());
    }

    public boolean isEnableSwaySneakModifier() {
        return enableSwaySneakModifier;
    }

    public void setEnableSwaySneakModifier(boolean enableSwaySneakModifier) {
        this.enableSwaySneakModifier = enableSwaySneakModifier;
    }

    public boolean isEnableSwayMovementModifier() {
        return enableSwayMovementModifier;
    }

    public void setEnableSwayMovementModifier(boolean enableSwayMovementModifier) {
        this.enableSwayMovementModifier = enableSwayMovementModifier;
    }

    public boolean isEnableSwayRunModifier() {
        return enableSwayRunModifier;
    }

    public void setEnableSwayRunModifier(boolean enableSwayRunModifier) {
        this.enableSwayRunModifier = enableSwayRunModifier;
    }

    public void setKnockbackPower(double power) {
        this.knockbackPower = power;
    }

    public double getKnockbackPower() {
        return knockbackPower;
    }

    public long getLastTimeRMB(Player player) {
        if (!lastRMB.containsKey(player.getUniqueId()))
            return 0;
        return lastRMB.get(player.getUniqueId());
    }

    public void setSlownessPower(int power) {
        this.slownessPower = power;
    }

    public int getSlownessPower() {
        return slownessPower;
    }

    public String getReloadingSound() {
        return reloadingSound;
    }

    public void setReloadingSound(String reloadingSound) {
        this.reloadingSound = reloadingSound;
    }

    public String getChargingSound() {
        return chargingSound;
    }

    public void setChargingSound(String chargingSound) {
        this.chargingSound = chargingSound;
    }

    public List<Material> getBreakableMaterials() {
        return breakableMaterials;
    }

    @Override
    public String toString() {
        return "Gun{" +
                "lastShot=" + lastShot +
                ", lastRMB=" + lastRMB +
                ", type=" + type +
                ", ammotype=" + ammotype +
                ", unlimitedAmmo=" + unlimitedAmmo +
                ", maxBullets=" + maxBullets +
                ", reloadTime=" + reloadTime +
                ", ch=" + ch +
                ", rh=" + rh +
                ", customProjectile=" + customProjectile +
                ", reloadingSound='" + reloadingSound + '\'' +
                ", chargingSound='" + chargingSound + '\'' +
                ", weaponSounds=" + weaponSounds +
                ", volume=" + volume +
                ", recoil=" + recoil +
                ", velocity=" + velocity +
                ", durabilityDamage=" + durabilityDamage +
                ", maxDistance=" + maxDistance +
                ", zoomLevel=" + zoomLevel +
                ", hasIronSights=" + hasIronSights +
                ", delayBetweenShots=" + delayBetweenShots +
                ", shotsPerBullet=" + shotsPerBullet +
                ", fireRate=" + fireRate +
                ", isAutomatic=" + isAutomatic +
                ", isPrimaryWeapon=" + isPrimaryWeapon +
                ", lightLevelOnShoot=" + lightLevelOnShoot +
                ", explosionRadius=" + explosionRadius +
                ", nightVisionOnScope=" + nightVisionOnScope +
                ", useOffhandOverride=" + useOffhandOverride +
                ", enableMuzzleSmoke=" + enableMuzzleSmoke +
                ", supports18=" + supports18 +
                ", durability=" + durability +
                ", particle=" + particle +
                ", particleMaterial=" + particleMaterial +
                ", particleData=" + particleData +
                ", particle_r=" + particle_r +
                ", particle_g=" + particle_g +
                ", particle_b=" + particle_b +
                ", glowEffect=" + glowEffect +
                ", knockbackPower=" + knockbackPower +
                ", slownessPower=" + slownessPower +
                ", sway=" + sway +
                ", swayMultiplier=" + swayMultiplier +
                ", swayUnscopedMultiplier=" + swayUnscopedMultiplier +
                ", headshotMultiplier=" + headshotMultiplier +
                ", enableSwayMovementModifier=" + enableSwayMovementModifier +
                ", enableSwaySneakModifier=" + enableSwaySneakModifier +
                ", enableSwayRunModifier=" + enableSwayRunModifier +
                ", breakableMaterials=" + breakableMaterials +
                ", killedByMessage='" + killedByMessage + '\'' +
                '}';
    }
}