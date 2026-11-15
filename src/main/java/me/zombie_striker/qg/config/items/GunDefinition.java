package me.zombie_striker.qg.config.items;

import me.zombie_striker.qg.config.system.Config;
import me.zombie_striker.qg.config.system.Section;

import java.io.File;
import java.util.List;

/**
 * A gun file in {@code newGuns}, also used for the gun settings of attachments.
 */
public class GunDefinition extends ItemDefinition {
    @Config
    public String weaponType;
    @Config
    public String ammoType;
    @Config
    public Boolean enableIronSights;
    @Config
    public Integer zoomLevel;
    @Config
    public Boolean nightVisionOnScope;
    @Config
    public Boolean enableBetterModelScopes;
    @Config
    public Integer maxBullets;
    @Config
    public Double damage;
    @Config
    public Integer durability;
    @Config
    public Boolean allowCrafting;
    @Config
    public Boolean automatic;
    @Config
    public Boolean primaryWeapon;
    @Config
    public Integer fireRate;
    @Config
    public Double delayForReload;
    @Config
    public Double delayForShoot;
    @Config
    public Integer bulletsPerShot;
    @Config
    public Integer maxBulletDistance;
    @Config
    public Boolean unlimitedAmmo;
    @Config
    public Integer lightLevelOnShoot;
    @Config
    public Double recoil;
    @Config
    public Double headshotMultiplier;
    @Config
    public Integer slownessOnEquip;
    @Config
    public Double firingKnockback;
    @Config
    public Boolean addMuzzleSmoke;
    @Config("version-18-support")
    public Boolean version18Support;
    @Config
    public String chargingHandler;
    @Config
    public String reloadingHandler;
    @Config
    public List<String> destructableMaterials;
    @Config
    public String dropGlowColor;
    /** A single sound name or a list of them, one picked at random per shot. */
    @Config
    public Object weaponSounds;
    @Config
    public Double weaponSoundsVolume;
    @Config
    public String reloadingSound;
    @Config
    public String chargingSound;

    public Sway sway = new Sway();
    public Particles particles = new Particles();
    public CustomProjectiles customProjectiles = new CustomProjectiles();

    @Section
    public static class Sway {
        @Config
        public Double defaultValue;
        @Config
        public Double defaultMultiplier;
        @Config
        public Boolean sneakModifier;
        @Config
        public Boolean moveModifier;
        @Config
        public Boolean runModifier;
    }

    @Section
    public static class Particles {
        @Config
        public String bulletParticle;
        @Config
        public Double bulletParticleR;
        @Config
        public double bulletParticleG = 1.0;
        @Config
        public double bulletParticleB = 1.0;
        @Config
        public int bulletParticleData = 0;
        @Config
        public String bulletParticleMaterial = "COAL_BLOCK";
    }

    @Section
    public static class CustomProjectiles {
        @Config
        public String projectileType;
        @Config
        public Double explosionRadius;
        @Config
        public Double velocity;
        @Config
        public Double explosionKnockback;
    }

    public GunDefinition(File file) {
        super(file);
    }
}
