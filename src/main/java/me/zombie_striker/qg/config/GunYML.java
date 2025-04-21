package me.zombie_striker.qg.config;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.guns.chargers.ChargingHandler;
import me.zombie_striker.qg.guns.chargers.ChargingManager;
import me.zombie_striker.qg.guns.reloaders.ReloadingHandler;
import me.zombie_striker.qg.guns.reloaders.ReloadingManager;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.guns.utils.WeaponType;

public class GunYML extends ArmoryYML {

    public GunYML(final File file) { super(file); }

    @Override
    public void verifyAllTagsExist() {
        super.verifyAllTagsExist();
        this.verify("weapontype", WeaponType.RIFLE.name());
        this.verify("enableIronSights", false);
        this.verify("setZoomLevel", 0);
        this.verify("ammotype", "556");
        this.verify("sway.defaultValue", 0.2);
        this.verify("firerate", 1);
        this.verify("maxbullets", 0);
        this.verify("delayForReload", 1.5);
        this.verify("delayForShoot", 0.3);
        this.verify("bullets-per-shot", 1);
        this.verify("isAutomatic", false);
        this.verify("maxBulletDistance", 200);
        this.verify("unlimitedAmmo", false);
        this.verify("LightLeveOnShoot", 14);
        this.verify("recoil", 1);
        this.verify("slownessOnEquip", 0);

        this.verify("particles.bullet_particle", "REDSTONE");
        this.verify("particles.bullet_particleR", 1);
        this.verify("particles.bullet_particleG", 1);
        this.verify("particles.bullet_particleB", 1);
        this.verify("particles.bullet_particleData", 0);
        this.verify("particles.bullet_particleMaterial", "COAL_BLOCK");

        this.verify("Version_18_Support", !QAMain.isVersionHigherThan(1, 9));
        this.verify("ChargingHandler", "none");
        this.verify("ReloadingHandler", "none");
        this.verify("addMuzzleSmoke", true);

        this.verify("drop-glow-color", "none");
        this.verify("headshotMultiplier", 3.5);
        this.verify("sway.defaultMultiplier", 2);

        this.verify("weaponsounds_volume", 4);
        this.verify("weaponsounds", WeaponSounds.getSoundByType(WeaponType.RIFLE));
        try {
            this.verify("weaponsounds_reloadingSound",
                    this.get("ReloadingHandler") != null
                            ? ((ReloadingManager.getHandler((String) this.get("ReloadingHandler")).getDefaultReloadingSound()))
                            : WeaponSounds.RELOAD_MAG_OUT.getSoundName());
            this.verify("weaponsounds_chargingSound",
                    this.get("ChargingHandler") != null
                            ? ((ChargingManager.getHandler((String) this.get("ChargingHandler")).getDefaultChargingSound()))
                            : WeaponSounds.RELOAD_BOLT.getSoundName());
        } catch (Error | Exception e5) {
        }

        this.verify("sway.sneakModifier", true);
        this.verify("sway.moveModifier", true);
        this.verify("sway.runModifier", true);
        this.verify("sway.unscopedModifier", 1);
        this.verify("firing_knockback", 0);
        this.verify("KilledByMessage", "%player% was shot by %killer% using a %name%");
        this.verify("DestructableMaterials", Collections.singletonList("MATERIAL_NAME_HERE"));
    }

    public GunYML setReloadingSound(final String sound) {
        this.set(false, "weaponsounds_reloadingSound", sound);
        return this;
    }

    public GunYML setReloadingSound(final WeaponSounds sound) {
        this.set(false, "weaponsounds_reloadingSound", sound.getSoundName());
        return this;
    }

    @Override
    public GunYML setWeaponSound(final WeaponSounds sound) {
        this.set("weaponsounds", sound.getSoundName());
        return this;
    }

    public GunYML setSwayUnscopedModifier(final int sway) {
        this.set(false, "sway.unscopedModifier", sway);
        return this;
    }

    public GunYML setKilledByMessage(final String message) {
        this.set(false, "KilledByMessage", message);
        return this;
    }

    public GunYML setFiringKnockback(final double message) {
        this.set(false, "firing_knockback", message);
        return this;
    }

    public GunYML setSlownessOnEquip(final int amount) {
        this.set(false, "slownessOnEquip", amount);
        return this;
    }

    public GunYML setRecoil(final double recoil) {
        this.set(false, "recoil", recoil);
        return this;
    }

    public GunYML setenableIronSights(final boolean ironsights) {
        this.set(false, "enableIronSights", ironsights);
        return this;
    }

    public GunYML setMaxBullets(final int amount) {
        this.set(false, "maxbullets", amount);
        return this;
    }

    public GunYML setSwayMultiplier(final double multiplier) {
        this.set(false, "sway.defaultMultiplier", multiplier);
        return this;
    }

    public GunYML setHeadShotMultiplier(final double multiplier) {
        this.set(false, "headshotMultiplier", multiplier);
        return this;
    }

    public GunYML setNightVisionOnScope(final boolean b) {
        this.set(false, "hasNightVisionOnScope", b);
        return this;
    }

    public GunYML setVolume(final float volume) {
        this.set(false, "weaponsounds_volume", volume);
        return this;
    }

    public GunYML setIsSecondaryWeapon(final boolean isSecondary) {
        this.set(false, "isPrimaryWeapon", !isSecondary);
        return this;
    }

    public GunYML setCustomProjectileExplosionRadius(final double radius) {
        this.set(false, "CustomProjectiles.explosionRadius", radius);
        return this;
    }

    public GunYML setCustomProjectile(final String customProjectle) {
        this.set(false, "CustomProjectiles.projectileType", customProjectle);
        return this;
    }

    public GunYML setCustomProjectileVelocity(final double velocity) {
        this.set(false, "CustomProjectiles.Velocity", velocity);
        return this;
    }

    @Override
    public GunYML setInvalid(final boolean invalid) {
        this.set(false, "invalid", invalid);
        return this;
    }

    @Override
    public GunYML setLore(final List<String> lore) {
        this.set(false, "lore", lore);
        return this;
    }

    @Override
    public GunYML setLore(final String... lore) {
        this.set("lore", lore);
        return this;
    }

    @Override
    public GunYML setVariant(final int var) {
        this.set(false, "variant", var);
        return this;
    }

    @Override
    public GunYML setDurability(final int durib) {
        this.set(false, "durability", durib);
        return this;
    }

    @Override
    public GunYML setPrice(final int cost) {
        this.set(false, "price", cost);
        return this;
    }

    @Override
    public GunYML setMaterial(final Material mat) {
        this.set(false, "material", mat.name());
        return this;
    }

    public GunYML setZoomLevel(final int zoom) {
        this.set(false, "setZoomLevel", zoom);
        return this;
    }

    public GunYML setDistance(final int distance) {
        this.set(false, "maxBulletDistance", distance);
        return this;
    }

    public GunYML setLightLevel(final int level) {
        this.set(false, "LightLeveOnShoot", level);
        return this;
    }

    public GunYML setOn18(final boolean on18) {
        this.set(false, "Version_18_Support", on18);
        return this;
    }

    public GunYML setMuzzleSmoke(final boolean smoke) {
        this.set(false, "addMuzzleSmoke", smoke);
        return this;
    }

    public GunYML setChargingHandler(final ChargingHandler ch) { return this.setChargingHandler(ch.getName()); }

    public GunYML setChargingHandler(final String ch) {
        this.set(false, "ChargingHandler", ch);
        return this;
    }

    public GunYML setReloadingHandler(final ReloadingHandler rh) { return this.setReloadingHandler(rh.getName()); }

    public GunYML setReloadingHandler(final String rh) {
        this.set(false, "ReloadingHandler", rh);
        return this;
    }

    public GunYML setDelayReload(final double reload) {
        this.set(false, "delayForReload", reload);
        return this;
    }

    public GunYML setDelayShoot(final double delay) {
        this.set(false, "delayForShoot", delay);
        return this;
    }

    public GunYML setSway(final double sway) {
        this.set(false, "sway.defaultValue", sway);
        return this;
    }

    public GunYML setBulletsPerShot(final int shots) {
        this.set(false, "bullets-per-shot", shots);
        return this;
    }

    public GunYML setFireRate(final int fireRate) {
        this.set(false, "firerate", fireRate);
        return this;
    }

    public GunYML setFullyAutomatic(final int shots) {
        // set(false, "bullets-per-shot", shots);
        // set(false, "ChargingHandler", ChargingManager.RAPIDFIRE);
        this.set(false, "firerate", shots);
        this.set(false, "isAutomatic", true);
        return this;
    }

    public GunYML setAutomatic(final boolean automatic) {
        this.set(false, "isAutomatic", automatic);
        return this;
    }

    public GunYML setUseOffhand(final boolean offhand) {
        this.set(false, "enableBetterModelScopes", offhand);
        return this;
    }

    public GunYML setUnlimitedAmmo(final boolean unlim) {
        this.set(false, "unlimitedAmmo", unlim);
        return this;
    }

    public GunYML setParticle(final String particle) {
        this.set(false, "particles.bullet_particle", particle);
        return this;
    }

    public GunYML setParticle(final double r, final double g, final double b, final Material m) {
        return this.setParticle("REDSTONE", r, g, b, m);
    }

    public GunYML setParticle(final String particle, final double r, final double g, final double b, final Material m) {
        this.set(false, "particles.bullet_particle", particle);
        this.set(false, "particles.bullet_particleR", r);
        this.set(false, "particles.bullet_particleG", g);
        this.set(false, "particles.bullet_particleB", b);
        this.set(false, "particles.bullet_particleMaterial", m.toString());
        return this;
    }

    public GunYML setDropGlow(final ChatColor c) { return this.setDropGlow(c.name()); }

    public GunYML setDropGlow(final String c) {
        this.set(false, "drop-glow-color", c);
        return this;
    }

    public GunYML setSway_SneakModifier(final boolean c) {
        this.set(false, "sway.sneakModifier", c);
        return this;
    }

    public GunYML setSway_movementModifier(final boolean c) {
        this.set(false, "sway.moveModifier", c);
        return this;
    }

    public GunYML setSway_runModifier(final boolean c) {
        this.set(false, "sway.runModifier", c);
        return this;
    }

}
