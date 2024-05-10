package me.zombie_striker.qg.guns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.zombie_striker.customitemmanager.ArmoryBaseObject;
import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.customitemmanager.CustomItemManager;
import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.customitemmanager.OLD_ItemFact;
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

public class Gun extends CustomBaseObject implements ArmoryBaseObject, Comparable<Gun> {

    private static final String CALCTEXT = ChatColor.DARK_GRAY + "qadata:";
    public ChatColor glowEffect = null;
    public boolean unlimitedAmmo = false;
    // This refers to the last time a gun was shot by a player, on a per-gun basis.
    // Doing this should prevent players from fast-switching to get around
    // bullet-delays
    public HashMap<UUID, Long> lastshot = new HashMap<>();
    boolean supports18 = false;
    boolean nightVisionOnScope = false;
    RealtimeCalculationProjectile customProjectile = null;
    double velocity = 2;
    double explosionRadius = 10;
    double recoil = 1;
    private WeaponType type;
    private boolean hasIronSights;
    private int zoomLevel = 0;
    private Ammo ammotype;
    private double acc;
    private double swaymultiplier = 2;
    private double swayUnscopedMultiplier = 1;
    private int maxbull;
    private float damage;
    private int durib = 1000;
    private boolean isAutomatic;
    private double headshotMultiplier = 2;
    private boolean isPrimaryWeapon = true;
    private boolean useOffhandOverride = true;
    private List<String> weaponSounds;
    private double volume = 4;
    private double delayBetweenShots = 0.25;
    private int shotsPerBullet = 1;
    private int firerate = 1;
    private double reloadTime = 1.5;
    private ChargingHandler ch = null;
    private ReloadingHandler rh = null;
    private boolean enableSwayMovementModifier = true;
    private boolean enableSwaySneakModifier = true;
    private boolean enableSwayRunModifier = true;
    private int maxDistance = 150;
    private Particle particle = null;
    private int particle_data = 1;
    private double particle_r = 1;
    private double particle_g = 1;
    private double particle_b = 1;
    private Material particle_material = Material.COAL_BLOCK;
    private int lightl = 20;
    private boolean enableMuzzleSmoke = false;
    private double knockbackPower = 0;
    private int slownessPower = 0;
    private final List<Material> breakableMaterials = new ArrayList<>();

    private String reloadingSound = WeaponSounds.RELOAD_MAG_OUT.getSoundName();
    private String chargingSound = WeaponSounds.RELOAD_BOLT.getSoundName();

    private String killedByMessage = "%player% was shot by %killer% using a %name%";

    private final HashMap<UUID, Long> lastRMB = new HashMap<>();

    @Deprecated
    public Gun(final String name, final MaterialStorage id, final WeaponType type, final boolean h, final Ammo am, final double acc,
            final double swaymult, final int maxBullets, final float damage, final boolean isAutomatic, final int durib, final String ws,
            final double cost, final ItemStack[] ing) {
        this(name, id, type, h, am, acc, swaymult, maxBullets, damage, isAutomatic, durib, ws, null, ChatColor.GOLD + name, cost, ing);
    }

    @Deprecated
    public Gun(final String name, final MaterialStorage id, final WeaponType type, final boolean h, final Ammo am, final double acc,
            final double swaymult, final int maxBullets, final float damage, final boolean isAutomatic, final int durib,
            final WeaponSounds ws, final double cost, final ItemStack[] ing) {
        this(name, id, type, h, am, acc, swaymult, maxBullets, damage, isAutomatic, durib, ws, null, ChatColor.GOLD + name, cost, ing);
    }

    @Deprecated
    public Gun(final String name, final MaterialStorage id, final WeaponType type, final boolean h, final Ammo am, final double acc,
            final double swaymult, final int maxBullets, final float damage, final boolean isAutomatic, final int durib,
            final WeaponSounds ws, final List<String> extralore, final String displayname, final double cost, final ItemStack[] ing) {
        this(name, id, type, h, am, acc, swaymult, maxBullets, damage, isAutomatic, durib, ws.getSoundName(), extralore, displayname, cost,
                ing);
    }

    @Deprecated
    public Gun(final String name, final MaterialStorage id, final WeaponType type, final boolean h, final Ammo am, final double acc,
            final double swaymult, final int maxBullets, final float damage, final boolean isAutomatic, final int durib, final String ws,
            final List<String> extralore, final String displayname, final double cost, final ItemStack[] ing) {
        super(name, id, LocalUtils.colorize(displayname), extralore, true);
        this.type = type;
        this.hasIronSights = h;
        this.ammotype = am;
        this.setIngredients(ing);
        this.acc = acc;
        this.maxbull = maxBullets;
        this.damage = damage;
        this.durib = durib;
        this.swaymultiplier = swaymult;
        this.isAutomatic = isAutomatic;
        this.weaponSounds = new ArrayList<String>();
        this.weaponSounds.add(ws);

        this.setPrice(cost);

        // this.extralore = extralore;
        // this.displayname = LocalUtils.colorize(displayname);
    }

    public Gun(final String name, final MaterialStorage id) { super(name, id, name, null, true); }

    public static boolean USE_THIS_INSTEAD_OF_INDEVIDUAL_SHOOT_METHODS(final Gun g, final Player player, final double acc,
            final boolean holdingRMB) {
        final boolean offhand = QualityArmory.isIronSights(player.getInventory().getItemInMainHand());
        if ((!offhand && Gun.getAmount(player) > 0) || (offhand && Update19OffhandChecker.hasAmountOFfhandGreaterthan(player, 0))) {
            final QAWeaponPrepareShootEvent shootevent = new QAWeaponPrepareShootEvent(player, g);
            Bukkit.getPluginManager().callEvent(shootevent);
            if (shootevent.isCancelled())
                return false;
            GunUtil.basicShoot(offhand, g, player, acc, holdingRMB);
            return true;
        }
        return false;
    }

    public static int getAmount(final Player player) { return Gun.getAmount(player.getInventory().getItemInMainHand()); }

    public static int getAmount(final ItemStack is) {
        if (is.getType().equals(Material.AIR))
            return 0;

        final NBTItem item = new NBTItem(is);
        if (item.hasTag("ammo")) {
            return item.getInteger("ammo");
        }

        return 0;
    }

    public static void updateAmmo(final Gun g, final ItemStack current, final int amount) {
        final NBTItem item = new NBTItem(current);
        item.setInteger("ammo", amount);
        item.applyNBT(current);
    }

    public static void updateAmmo(final Gun g, final Player player, final int amount) {
        final ItemStack current = player.getInventory().getItemInMainHand();
        Gun.updateAmmo(g, current, amount);
    }

    public static List<String> getGunLore(final Gun g, final ItemStack current, final int amount) {
        List<String> lore = (current != null && current.hasItemMeta() && current.getItemMeta().hasLore()) ? current.getItemMeta().getLore()
                : new ArrayList<>();
        OLD_ItemFact.addVariantData(null, lore, g);
        if (QAMain.ENABLE_LORE_INFO) {
            lore.add(QAMain.S_ITEM_DAMAGE + ": " + g.getDamage());
            lore.add(QAMain.S_ITEM_DPS + ": "
                    + (g.isAutomatic()
                            ? (2 * g.getFireRate() * g.getDamage()) + "" + (g.getBulletsPerShot() > 1 ? "x" + g.getBulletsPerShot() : "")
                            : "" + ((int) (1.0 / g.getDelayBetweenShotsInSeconds()) * g.getDamage())
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
                final double k = ((double) g.getDamage()) / g.getDurability();
                final ChatColor c = k > 0.5 ? ChatColor.DARK_GREEN : k > 0.25 ? ChatColor.GOLD : ChatColor.DARK_RED;
                lore.add(c + QAMain.S_ITEM_DURIB + ":" + g.getDurability() + "/" + g.getDurability());
            } else {
                lore = Gun.setDurabilityDamage(g, lore, Gun.getDamage(current));
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
            for (final String s : current.getItemMeta().getLore()) {
                if (ChatColor.stripColor(s).contains("UUID")) {
                    lore.add(s);
                    break;
                }
            }
        return lore;
    }

    public static int getDamage(final ItemStack is) {
        if (is != null && is.hasItemMeta() && is.getItemMeta().hasLore())
            for (final String lore : is.getItemMeta().getLore()) {
                if (ChatColor.stripColor(lore).startsWith(ChatColor.stripColor(QAMain.S_ITEM_DURIB))) {
                    return Integer.parseInt(lore.split(":")[1].split("/")[0].trim());
                }
            }
        return -1;
    }

    public static ItemStack durabilityDamage(final Gun g, final ItemStack is) {
        return Gun.setDurabilityDamage(g, is, Gun.getDamage(is) - 1);
    }

    public static ItemStack setDurabilityDamage(final Gun g, final ItemStack is, final int damage) {
        final ItemMeta im = is.getItemMeta();
        im.setLore(Gun.setDurabilityDamage(g, im.getLore(), damage));
        is.setItemMeta(im);
        return is;
    }

    public static List<String> setDurabilityDamage(final Gun g, final List<String> lore, final int damage) {
        boolean foundLine = false;
        final double k = ((double) damage) / g.getDurability();
        final ChatColor c = k > 0.5 ? ChatColor.DARK_GREEN : k > 0.25 ? ChatColor.GOLD : ChatColor.DARK_RED;
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

    public static int getCalculatedExtraDurib(final ItemStack is) {
        if (CustomItemManager.isUsingCustomData())
            return -1;
        if (!is.hasItemMeta() || !is.getItemMeta().hasLore() || is.getItemMeta().getLore().isEmpty())
            return -1;
        final List<String> lore = is.getItemMeta().getLore();
        for (int i = 0; i < lore.size(); i++) {
            if (lore.get(i).startsWith(Gun.CALCTEXT))
                return Integer.parseInt(lore.get(i).split(Gun.CALCTEXT)[1]);
        }
        return -1;
    }

    public static ItemStack addCalulatedExtraDurib(ItemStack is, final int number) {
        if (CustomItemManager.isUsingCustomData())
            return is;
        final ItemMeta im = is.getItemMeta();
        List<String> lore = im.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        } else {
            if (Gun.getCalculatedExtraDurib(is) != -1)
                is = Gun.removeCalculatedExtra(is);
        }
        lore.add(Gun.CALCTEXT + number);
        im.setLore(lore);
        is.setItemMeta(im);
        return is;
    }

    public static ItemStack decrementCalculatedExtra(final ItemStack is) {
        if (CustomItemManager.isUsingCustomData())
            return is;
        final ItemMeta im = is.getItemMeta();
        final List<String> lore = is.getItemMeta().getLore();
        for (int i = 0; i < lore.size(); i++) {
            if (lore.get(i).startsWith(Gun.CALCTEXT)) {
                lore.set(i, Gun.CALCTEXT + "" + (Integer.parseInt(lore.get(i).split(Gun.CALCTEXT)[1]) - 1));
            }
        }
        im.setLore(lore);
        is.setItemMeta(im);
        return is;
    }

    public static ItemStack removeCalculatedExtra(final ItemStack is) {
        if (is.hasItemMeta() && is.getItemMeta().hasLore()) {
            final ItemMeta im = is.getItemMeta();
            final List<String> lore = is.getItemMeta().getLore();
            for (int i = 0; i < lore.size(); i++) {
                if (lore.get(i).startsWith(Gun.CALCTEXT)) {
                    lore.remove(i);
                }
            }
            im.setLore(lore);
            is.setItemMeta(im);
        }
        return is;
    }

    public void copyFrom(final Gun g) {
        this.setIngredientsRaw(g.getIngredientsRaw());
        this.type = g.type;
        this.hasIronSights = g.hasIronSights;
        this.zoomLevel = g.zoomLevel;
        this.ammotype = g.ammotype;
        this.acc = g.acc;
        this.swaymultiplier = g.swaymultiplier;
        this.maxbull = g.maxbull;
        this.damage = g.damage;
        this.durib = g.durib;
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
        this.firerate = g.firerate;
        this.ch = g.ch;
        this.rh = g.rh;
        this.maxDistance = g.maxbull;
        this.particle = g.particle;
        this.particle_r = g.particle_r;
        this.particle_g = g.particle_g;
        this.particle_b = g.particle_b;
        this.particle_material = g.particle_material;
        this.particle_data = g.particle_data;
        this.lightl = g.lightl;
        this.enableMuzzleSmoke = g.enableMuzzleSmoke;
        this.glowEffect = g.glowEffect;
        this.unlimitedAmmo = g.unlimitedAmmo;
        this.customProjectile = g.customProjectile;
        this.velocity = g.velocity;
        this.recoil = g.recoil;

    }

    public void setHasIronsights(final boolean b) { this.hasIronSights = b; }

    public void setDuribility(final int durib) { this.durib = durib; }

    public void setSwayMultiplier(final double multiplier) { this.swaymultiplier = multiplier; }

    public double getHeadshotMultiplier() { return this.headshotMultiplier; }

    public void setHeadshotMultiplier(final double dam) { this.headshotMultiplier = dam; }

    public ChatColor getGlow() { return this.glowEffect; }

    /**
     * Sets the glow for the item. Null to disable the glow.
     */
    public void setGlow(final ChatColor glow) { this.glowEffect = glow; }

    public int getLightOnShoot() { return this.lightl; }

    public void setLightOnShoot(final int level) { this.lightl = level; }

    public double getRecoil() { return this.recoil; }

    public void setRecoil(final double d) { this.recoil = d; }

    @Override
    public void enableBetterAimingAnimations(final boolean b) { this.useOffhandOverride = b; }

    @Override
    public boolean hasBetterAimingAnimations() { return this.useOffhandOverride; }

    public double getVolume() { return this.volume; }

    public void setVolume(final double f) { this.volume = f; }

    public double getReloadTime() { return this.reloadTime; }

    public void setReloadingTimeInSeconds(final double time) { this.reloadTime = time; }

    public void setNightVision(final boolean nightVisionOnScope) { this.nightVisionOnScope = nightVisionOnScope; }

    public void setAmmo(final Ammo ammo) { this.ammotype = ammo; }

    public boolean hasnightVision() { return this.nightVisionOnScope; }

    public boolean usesCustomProjctiles() { return this.customProjectile != null; }

    public RealtimeCalculationProjectile getCustomProjectile() { return this.customProjectile; }

    public void setCustomProjectile(final String key) { this.customProjectile = ProjectileManager.getHandler(key); }

    public void setRealtimeVelocity(final double velocity) { this.velocity = velocity; }

    public double getVelocityForRealtimeCalculations() { return this.velocity; }

    public double getExplosionRadius() { return this.explosionRadius; }

    public void setExplosionRadius(final double d) { this.explosionRadius = d; }

    public int getBulletsPerShot() { return this.shotsPerBullet; }

    public void setBulletsPerShot(final int i) { this.shotsPerBullet = i; }

    public void setZoomLevel(final int zoom) { this.zoomLevel = zoom; }

    public int getZoomWhenIronSights() { return this.zoomLevel; }

    public int getFireRate() { return this.firerate; }

    public void setFireRate(final int firerate) { this.firerate = firerate; }

    public WeaponType getWeaponType() { return this.type; }

    public boolean isPrimaryWeapon() { return this.isPrimaryWeapon; }

    public void setIsPrimary(final boolean isPrimary) { this.isPrimaryWeapon = isPrimary; }

    public boolean isAutomatic() { return this.isAutomatic; }

    public void setAutomatic(final boolean automatic) { this.isAutomatic = automatic; }

    public void setUnlimitedAmmo(final boolean b) { this.unlimitedAmmo = b; }

    public boolean shoot(final Player player) { return this.shoot(player, false); }

    public boolean shoot(final Player player, final boolean holdingRMB) {
        return Gun.USE_THIS_INSTEAD_OF_INDEVIDUAL_SHOOT_METHODS(this, player, this.getSway(), holdingRMB);
    }

    public void setDeathMessage(final String deathMessage) { this.killedByMessage = deathMessage; }

    public String getDeathMessage() { return this.killedByMessage; }

    public int getMaxBullets() { return this.maxbull; }

    public void setMaxBullets(final int amount) { this.maxbull = amount; }

    public boolean playerHasAmmo(final Player player) {
        if (player.getGameMode() == GameMode.CREATIVE)
            return true;
        if (this.hasUnlimitedAmmo())
            return true;
        if (this.getAmmoType() == null)
            return true;
        return GunUtil.hasAmmo(player, this);
    }

    public void setSound(final WeaponSounds sound) { this.setSound(sound.getSoundName()); }

    public void setSound(final String sound) {
        this.weaponSounds.clear();
        this.weaponSounds.add(sound);
    }

    public void setSounds(final List<String> sound) { this.weaponSounds = sound; }

    public void reload(final Player player) {
        if (this.getChargingHandler() == null
                || (this.getReloadingingHandler() == null || !this.getReloadingingHandler().isReloading(player)))
            GunUtil.basicReload(this, player, this.hasUnlimitedAmmo(), this.reloadTime);
    }

    public double getDamage() { return this.damage; }

    public void setDurabilityDamage(final float damage) { this.damage = damage; }

    public int getDurability() { return this.durib; }

    public void setSwayUnscopedMultiplier(final double swaymultiplier) { this.swayUnscopedMultiplier = swaymultiplier; }

    public double getSwayUnscopedMultiplier() { return this.swayUnscopedMultiplier; }

    public Ammo getAmmoType() { return this.ammotype; }

    public boolean hasIronSights() { return this.hasIronSights; }

    public boolean hasUnlimitedAmmo() {
        if (this.unlimitedAmmo)
            return true;
        return this.ammotype == null;
    }

    public double getSway() { return this.acc; }

    public void setSway(final double sway) { this.acc = sway; }

    public double getMovementMultiplier() { return this.swaymultiplier; }

    @Deprecated
    public String getWeaponSound() { return this.weaponSounds.get(0); }

    public List<String> getWeaponSounds() { return this.weaponSounds; }

    public double getDelayBetweenShotsInSeconds() { return this.delayBetweenShots; }

    public void setDelayBetweenShots(final double seconds) { this.delayBetweenShots = seconds; }

    public HashMap<UUID, Long> getLastShotForGun() { return this.lastshot; }

    public ChargingHandler getChargingHandler() { return this.ch; }

    public void setChargingHandler(final ChargingHandler ch) { this.ch = ch; }

    public ReloadingHandler getReloadingingHandler() { return this.rh; }

    public void setReloadingHandler(final ReloadingHandler rh) { this.rh = rh; }

    public int getMaxDistance() { return this.maxDistance; }

    public void setMaxDistance(final int a) { this.maxDistance = a; }

    @Override
    public boolean is18Support() { return this.supports18; }

    @Override
    public void set18Supported(final boolean b) { this.supports18 = b; }

    public Particle getParticle() { return this.particle; }

    public double getParticleR() { return this.particle_r; }

    public double getParticleG() { return this.particle_g; }

    public double getParticleB() { return this.particle_b; }

    public int getParticleData() { return this.particle_data; }

    public Material getParticleMaterial() { return this.particle_material; }

    public void setParticles(final Particle p) { this.setParticles(p, 1, 1, 1, Material.COAL_BLOCK); }

    public void setParticles(final Particle p, final int data) { this.setParticles(p, 1, 1, 1, Material.COAL_BLOCK, data); }

    public void setParticles(final Particle p, final double r, final double g, final double b, final Material m) {
        this.setParticles(p, r, g, b, m, 0);
    }

    public void setParticles(final Particle p, final double r, final double g, final double b, final Material m, final int data) {
        this.particle = p;
        this.particle_data = data;
        this.particle_r = r;
        this.particle_g = g;
        this.particle_b = b;
        this.particle_material = m;
    }

    public boolean useMuzzleSmoke() { return this.enableMuzzleSmoke; }

    public void setUseMuzzleSmoke(final boolean b) { this.enableMuzzleSmoke = b; }

    @Override
    public int compareTo(final Gun arg0) {
        if (QAMain.orderShopByPrice) {
            return (int) (this.getPrice() - arg0.getPrice());
        }
        return this.getDisplayName().compareTo(arg0.getDisplayName());
    }

    @Override
    public boolean onRMB(final Player e, final ItemStack usedItem) {
        return this.onClick(e, usedItem, (QAMain.reloadOnFOnly || !QAMain.SWAP_TO_LMB_SHOOT));
    }

    @Override
    public boolean onShift(final Player shooter, final ItemStack usedItem, final boolean toggle) { return false; }

    @Override
    public boolean onLMB(final Player e, final ItemStack usedItem) { return this.onClick(e, usedItem, QAMain.SWAP_TO_LMB_SHOOT); }

    @Override
    public boolean onSwapTo(final Player shooter, final ItemStack usedItem) {
        if (this.getSoundOnEquip() != null)
            shooter.getWorld().playSound(shooter.getLocation(), this.getSoundOnEquip(), 1, 1);
        if (this.getSlownessPower() > 0) {
            if (shooter.hasPotionEffect(PotionEffectType.SLOWNESS))
                if (shooter.getPotionEffect(PotionEffectType.SLOWNESS).getAmplifier() != this.getSlownessPower())
                    shooter.removePotionEffect(PotionEffectType.SLOWNESS);
            shooter.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, Integer.MAX_VALUE, this.getSlownessPower()));
        }
        return false;
    }

    @Override
    public boolean onSwapAway(final Player shooter, final ItemStack usedItem) {
        if (this.getSlownessPower() > 0) {
            if (shooter.hasPotionEffect(PotionEffectType.SLOWNESS))
                if (shooter.getPotionEffect(PotionEffectType.SLOWNESS).getAmplifier() == this.getSlownessPower())
                    shooter.removePotionEffect(PotionEffectType.SLOWNESS);
        }
        return false;
    }

    @Override
    public String getSoundOnEquip() { return null; }

    @Override
    public String getSoundOnHit() { return null; }

    @SuppressWarnings("deprecation")
    private boolean onClick(final Player player, ItemStack usedItem, final boolean fire) {
        QAMain.DEBUG("CLICKED GUN " + this.getName());
        if (QAMain.requirePermsToShoot && !player.getPlayer().hasPermission("qualityarmory.usegun")) {
            player.getPlayer().sendMessage(QAMain.S_NOPERM);
            return true;
        }
        if (QAMain.perWeaponPermission && !player.getPlayer().hasPermission("qualityarmory.usegun." + this.getName())) {
            player.getPlayer().sendMessage(QAMain.S_NOPERM);
            return true;
        }

        QAMain.DEBUG("Dups check");
        QAMain.checkforDups(player.getPlayer(), usedItem);

        final ItemStack offhandItem = Update19OffhandChecker.getItemStackOFfhand(player.getPlayer());
        boolean offhand = offhandItem != null && offhandItem.equals(usedItem);

        QAMain.DEBUG("Made it to gun/attachment check : " + this.getName());
        try {
            if (QAMain.enableInteractChests) {
                final Block b = player.getTargetBlock(null, 6);
                if (b != null && (b.getType() == Material.CHEST || b.getType() == Material.TRAPPED_CHEST)
                        || b.getType() == Material.ENDER_CHEST) {
                    QAMain.DEBUG("Chest interactable check has return true!");
                    return true;
                }
            }
        } catch (Error | Exception e4) {
        }

        if (fire) {
            QAMain.DEBUG("Fire mode called");
            if (player.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().contains(QAMain.S_RELOADING_MESSAGE)) {
                if (!GunRefillerRunnable.hasItemReloaded(player, usedItem)) {
                    final ItemStack tempused = usedItem;
                    final ItemMeta im = tempused.getItemMeta();
                    im.setDisplayName(this.getDisplayName());
                    tempused.setItemMeta(im);
                    if (offhand) {
                        Update19OffhandChecker.setOffhand(player.getPlayer(), tempused);
                        QAMain.DEBUG("odd. Reloading broke. Removing reloading message from offhand - firing");
                    } else {
                        player.getPlayer().setItemInHand(tempused);
                        QAMain.DEBUG("odd. Reloading broke. Removing reloading message from mainhand - firing");
                    }
                }
                QAMain.DEBUG("Reloading message 1!");
                return true;
            }
            if (!this.isAutomatic() && GunUtil.rapidfireshooters.containsKey(player.getPlayer().getUniqueId())) {
                GunUtil.rapidfireshooters.remove(player.getPlayer().getUniqueId()).cancel();
                if (QAMain.enableReloadWhenOutOfAmmo) {
                    if (Gun.getAmount(player) <= 0) {
                        if (offhand) {
                            player.getPlayer().setItemInHand(player.getPlayer().getInventory().getItemInOffHand());
                            player.getPlayer().getInventory().setItemInOffHand(null);
                            usedItem = player.getPlayer().getInventory().getItemInMainHand();
                            offhand = false;
                        }
                        if (QAMain.allowGunReload) {
                            QualityArmory.sendHotbarGunAmmoCount(player.getPlayer(), this, usedItem,
                                    ((this.getMaxBullets() != Gun.getAmount(player)) && GunUtil.hasAmmo(player.getPlayer(), this)));
                            if ((this.getMaxBullets() != Gun.getAmount(player))) {
                                QAMain.DEBUG("Ammo full");
                            } else if (this.playerHasAmmo(player.getPlayer())) {
                                QAMain.DEBUG("Trying to reload WITH AUTORELOAD. player has ammo");
                                this.reload(player.getPlayer());
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
                if (Gun.getAmount(player) <= 0) {
                    QAMain.DEBUG("Out of ammo");

                    if (GunUtil.rapidfireshooters.containsKey(player.getUniqueId()))
                        GunUtil.rapidfireshooters.remove(player.getUniqueId()).cancel();

                    player.playSound(player.getLocation(), WeaponSounds.OUT_OF_AMMO_CLICK.getSoundName(), 1f, 1f);

                    if (QAMain.enableReloadWhenOutOfAmmo) {
                        if (offhand) {
                            player.getPlayer().setItemInHand(player.getPlayer().getInventory().getItemInOffHand());
                            player.getPlayer().getInventory().setItemInOffHand(null);
                            usedItem = player.getPlayer().getInventory().getItemInMainHand();
                            offhand = false;
                        }
                        if (QAMain.allowGunReload) {
                            QualityArmory.sendHotbarGunAmmoCount(player.getPlayer(), this, usedItem,
                                    ((this.getMaxBullets() != Gun.getAmount(player)) && GunUtil.hasAmmo(player.getPlayer(), this)));
                            if (this.playerHasAmmo(player.getPlayer())) {
                                QAMain.DEBUG("Trying to reload WITH AUTORELOAD. player has ammo");
                                this.reload(player.getPlayer());

                            } else {
                                QAMain.DEBUG("Trying to reload WITH AUTORELOAD. player DOES NOT have ammo");
                            }
                        }
                    }
                    return true;
                }
                if (!QAMain.enableDurability || Gun.getDamage(usedItem) > 0) {
                    boolean automaticfiring = true;
                    if (!QAMain.SWAP_TO_LMB_SHOOT) {
                        if (this.lastRMB.containsKey(player.getUniqueId())) {
                            if (System.currentTimeMillis() - this.lastRMB.get(player.getUniqueId()) <= 600) {
                                automaticfiring = true;
                            }
                        }
                        this.lastRMB.put(player.getUniqueId(), System.currentTimeMillis());
                    }
                    if (!GunUtil.isDelay(this, player) && this.getKnockbackPower() != 0) {
                        final Vector currentVelocity = player.getVelocity();
                        currentVelocity.add(player.getLocation().getDirection().normalize().multiply(-this.getKnockbackPower()));
                        player.setVelocity(currentVelocity);
                    }
                    QAMain.DEBUG("" + this.shoot(player.getPlayer(), automaticfiring));
                } else {
                    player.getPlayer().playSound(player.getPlayer().getLocation(), WeaponSounds.METALHIT.getSoundName(), 1, 1);
                    QAMain.DEBUG("Durablility less than 0");
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
                if (this.hasIronSights()) {
                    try {

                        if (player.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName()
                                .contains(QAMain.S_RELOADING_MESSAGE)) {
                            if (!GunRefillerRunnable.hasItemReloaded(player, usedItem)) {
                                final ItemStack tempused = usedItem.clone();
                                final ItemMeta im = tempused.getItemMeta();
                                im.setDisplayName(this.getDisplayName());
                                tempused.setItemMeta(im);
                                if (offhand) {
                                    Update19OffhandChecker.setOffhand(player.getPlayer(), tempused);
                                    QAMain.DEBUG("odd. Reloading broke. Removing reloading message from offhand - reload");
                                } else {
                                    player.getPlayer().setItemInHand(tempused);
                                    QAMain.DEBUG("odd. Reloading broke. Removing reloading message from mainhand - reload");
                                }
                            }
                            QAMain.DEBUG("Reloading message 1!");
                            return true;
                        }

                        IronsightsHandler.aim(player);

                        final Gun checkTo = QualityArmory.getGun(Update19OffhandChecker.getItemStackOFfhand(player.getPlayer()));
                        new BukkitRunnable() {

                            @Override
                            public void run() {
                                if (!player.getPlayer().isOnline()) {
                                    QAMain.DEBUG("Canceling since player is offline");
                                    this.cancel();
                                    return;
                                }
                                Gun g = null;
                                if (!QualityArmory.isIronSights(player.getPlayer().getInventory().getItemInMainHand())
                                        || (g = QualityArmory
                                                .getGun(Update19OffhandChecker.getItemStackOFfhand(player.getPlayer()))) == null
                                        || g != checkTo) {
                                    QAMain.toggleNightvision(player.getPlayer(), checkTo, false);
                                    QAMain.DEBUG(
                                            "Removing nightvision since either the main hand is not ironsights/ offhand gun is null. : "
                                                    + (!QualityArmory.isIronSights(player.getPlayer().getInventory().getItemInMainHand()))
                                                    + " "
                                                    + ((g = QualityArmory.getGun(
                                                            Update19OffhandChecker.getItemStackOFfhand(player.getPlayer()))) == null)
                                                    + " " + (g != checkTo));
                                    this.cancel();
                                    return;
                                }

                            }
                        }.runTaskTimer(QAMain.getInstance(), 20, 20);

                        QualityArmory.sendHotbarGunAmmoCount(player.getPlayer(), this, usedItem, false);
                    } catch (final Error e2) {
                        Bukkit.broadcastMessage(QAMain.prefix
                                + "Ironsights not compatible for versions lower than 1.8. The server owner should set EnableIronSights to false in the plugin's config");
                    }
                } else {
                }
                QAMain.DEBUG("Ironsights on RMB finished");
            } else {
                QAMain.DEBUG("Reload called");
                final Block targetblock = player.getTargetBlock(null, 5);
                if (targetblock != null && QAMain.interactableBlocks.contains(targetblock.getType())) {
                    QAMain.DEBUG("Canceled interact because block is " + targetblock.getType().name());
                    return false;
                } else {
                    if (QAMain.allowGunReload) {
                        QualityArmory.sendHotbarGunAmmoCount(player.getPlayer(), this, usedItem,
                                ((this.getMaxBullets() != Gun.getAmount(player)) && GunUtil.hasAmmo(player.getPlayer(), this)));
                        if (this.playerHasAmmo(player.getPlayer())) {
                            QAMain.DEBUG("Trying to reload. player has ammo");
                            this.reload(player.getPlayer());
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

    public void damageDurability(final Player player) {
        if (QAMain.enableDurability) {
            if (QualityArmory.isIronSights(player.getInventory().getItemInMainHand())) {
                Update19OffhandChecker.setOffhand(player, Gun.durabilityDamage(this, Update19OffhandChecker.getItemStackOFfhand(player)));
            } else {
                player.getInventory().setItemInMainHand(Gun.durabilityDamage(this, player.getInventory().getItemInMainHand()));
            }
        }
    }

    @Override
    public ItemStack getItemStack() {
        return CustomItemManager.getItemType("gun").getItem(this.getItemData().getMat(), this.getItemData().getData(),
                this.getItemData().getVariant());
    }

    public boolean isEnableSwaySneakModifier() { return this.enableSwaySneakModifier; }

    public void setEnableSwaySneakModifier(final boolean enableSwaySneakModifier) {
        this.enableSwaySneakModifier = enableSwaySneakModifier;
    }

    public boolean isEnableSwayMovementModifier() { return this.enableSwayMovementModifier; }

    public void setEnableSwayMovementModifier(final boolean enableSwayMovementModifier) {
        this.enableSwayMovementModifier = enableSwayMovementModifier;
    }

    public boolean isEnableSwayRunModifier() { return this.enableSwayRunModifier; }

    public void setEnableSwayRunModifier(final boolean enableSwayRunModifier) { this.enableSwayRunModifier = enableSwayRunModifier; }

    public void setKnockbackPower(final double power) { this.knockbackPower = power; }

    public double getKnockbackPower() { return this.knockbackPower; }

    public long getLastTimeRMB(final Player player) {
        if (!this.lastRMB.containsKey(player.getUniqueId()))
            return 0;
        return this.lastRMB.get(player.getUniqueId());
    }

    public void setSlownessPower(final int power) { this.slownessPower = power; }

    public int getSlownessPower() { return this.slownessPower; }

    public String getReloadingSound() { return this.reloadingSound; }

    public void setReloadingSound(final String reloadingSound) { this.reloadingSound = reloadingSound; }

    public String getChargingSound() { return this.chargingSound; }

    public void setChargingSound(final String chargingSound) { this.chargingSound = chargingSound; }

    public List<Material> getBreakableMaterials() { return this.breakableMaterials; }

    @Override
    public String toString() {
        return "Gun{" + "glowEffect=" + this.glowEffect + ", unlimitedAmmo=" + this.unlimitedAmmo + ", lastshot=" + this.lastshot
                + ", supports18=" + this.supports18 + ", nightVisionOnScope=" + this.nightVisionOnScope + ", customProjectile="
                + this.customProjectile + ", velocity=" + this.velocity + ", explosionRadius=" + this.explosionRadius + ", recoil="
                + this.recoil + ", type=" + this.type + ", hasIronSights=" + this.hasIronSights + ", zoomLevel=" + this.zoomLevel
                + ", ammotype=" + this.ammotype + ", acc=" + this.acc + ", swaymultiplier=" + this.swaymultiplier
                + ", swayUnscopedMultiplier=" + this.swayUnscopedMultiplier + ", maxbull=" + this.maxbull + ", damage=" + this.damage
                + ", durib=" + this.durib + ", isAutomatic=" + this.isAutomatic + ", headshotMultiplier=" + this.headshotMultiplier
                + ", isPrimaryWeapon=" + this.isPrimaryWeapon + ", useOffhandOverride=" + this.useOffhandOverride + ", weaponSounds="
                + this.weaponSounds + ", volume=" + this.volume + ", delayBetweenShots=" + this.delayBetweenShots + ", shotsPerBullet="
                + this.shotsPerBullet + ", firerate=" + this.firerate + ", reloadTime=" + this.reloadTime + ", ch=" + this.ch + ", rh="
                + this.rh + ", enableSwayMovementModifier=" + this.enableSwayMovementModifier + ", enableSwaySneakModifier="
                + this.enableSwaySneakModifier + ", enableSwayRunModifier=" + this.enableSwayRunModifier + ", maxDistance="
                + this.maxDistance + ", particle=" + this.particle + ", particle_data=" + this.particle_data + ", particle_r="
                + this.particle_r + ", particle_g=" + this.particle_g + ", particle_b=" + this.particle_b + ", particle_material="
                + this.particle_material + ", lightl=" + this.lightl + ", enableMuzzleSmoke=" + this.enableMuzzleSmoke + ", knockbackPower="
                + this.knockbackPower + ", slownessPower=" + this.slownessPower + ", breakableMaterials=" + this.breakableMaterials
                + ", reloadingSound='" + this.reloadingSound + '\'' + ", chargingSound='" + this.chargingSound + '\''
                + ", killedByMessage='" + this.killedByMessage + '\'' + ", lastRMB=" + this.lastRMB + '}';
    }
}
