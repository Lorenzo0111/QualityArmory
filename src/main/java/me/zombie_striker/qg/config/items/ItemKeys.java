package me.zombie_striker.qg.config.items;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Item file keys renamed to kebab-case in v3. Files without a version are migrated with this map, and
 * {@link me.zombie_striker.qg.config.ArmoryYML} translates old names, so addons that still use them keep working.
 */
public final class ItemKeys {
    /**
     * Old key to new key. When two old keys map to the same new key, the first one present wins
     * ({@code guntype} over {@code weapontype}, {@code maxItemStack} over {@code maxAmount}), as before.
     */
    public static final Map<String, String> RENAMED;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        // Shared
        m.put("displayname", "display-name");
        m.put("craftingRequirements", "crafting-requirements");
        m.put("craftingReturnAmount", "crafting-return-amount");
        m.put("allowInShop", "allow-in-shop");
        m.put("maxItemStack", "max-item-stack");
        m.put("MiscType", "misc-type");
        m.put("Version_18_Support", "version-18-support");
        m.put("lastModifiedByQA", "last-modified-by-qa");
        m.put("AllowUserModifications", "allow-user-modifications");
        m.put("HOW_TO_USE", "how-to-use");
        // Ammo
        m.put("skull_owner", "skull-owner");
        m.put("skull_owner_custom_url", "skull-owner-custom-url");
        m.put("skull_owner_custom_url_COMMENT", "skull-owner-custom-url-comment");
        m.put("piercingSeverity", "piercing-severity");
        // Armor
        m.put("maxProtectionHeight", "max-protection-height");
        m.put("minProtectionHeight", "min-protection-height");
        m.put("stopsHeadshots", "stops-headshots");
        // Misc
        m.put("sound_equip", "sound-equip");
        m.put("sound_equip_volume", "sound-equip-volume");
        m.put("sound_meleehit", "sound-melee-hit");
        m.put("sound_meleehit_volume", "sound-melee-hit-volume");
        m.put("ThrowSpeed", "throw-speed");
        m.put("useModernParticles", "use-modern-particles");
        m.put("FuseTime", "fuse-time");
        // Guns and attachments
        m.put("guntype", "weapon-type");
        m.put("weapontype", "weapon-type");
        m.put("baseGun", "base-gun");
        m.put("ammotype", "ammo-type");
        m.put("enableIronSights", "enable-iron-sights");
        m.put("setZoomLevel", "zoom-level");
        m.put("hasNightVisionOnScope", "night-vision-on-scope");
        m.put("enableBetterModelScopes", "enable-better-model-scopes");
        m.put("maxbullets", "max-bullets");
        m.put("allowCrafting", "allow-crafting");
        m.put("isAutomatic", "automatic");
        m.put("isPrimaryWeapon", "primary-weapon");
        m.put("firerate", "fire-rate");
        m.put("delayForReload", "delay-for-reload");
        m.put("delayForShoot", "delay-for-shoot");
        m.put("maxBulletDistance", "max-bullet-distance");
        m.put("unlimitedAmmo", "unlimited-ammo");
        m.put("LightLeveOnShoot", "light-level-on-shoot");
        m.put("headshotMultiplier", "headshot-multiplier");
        m.put("slownessOnEquip", "slowness-on-equip");
        m.put("firing_knockback", "firing-knockback");
        m.put("addMuzzleSmoke", "add-muzzle-smoke");
        m.put("ChargingHandler", "charging-handler");
        m.put("ReloadingHandler", "reloading-handler");
        m.put("DestructableMaterials", "destructable-materials");
        m.put("KilledByMessage", "killed-by-message");
        m.put("weaponsounds", "weapon-sounds");
        m.put("weaponsounds_volume", "weapon-sounds-volume");
        m.put("weaponsounds_reloadingSound", "reloading-sound");
        m.put("weaponsounds_chargingSound", "charging-sound");
        m.put("sway.defaultValue", "sway.default-value");
        m.put("sway.defaultMultiplier", "sway.default-multiplier");
        m.put("sway.sneakModifier", "sway.sneak-modifier");
        m.put("sway.moveModifier", "sway.move-modifier");
        m.put("sway.runModifier", "sway.run-modifier");
        m.put("sway.unscopedModifier", "sway.unscoped-modifier");
        m.put("particles.bullet_particle", "particles.bullet-particle");
        m.put("particles.bullet_particleR", "particles.bullet-particle-r");
        m.put("particles.bullet_particleG", "particles.bullet-particle-g");
        m.put("particles.bullet_particleB", "particles.bullet-particle-b");
        m.put("particles.bullet_particleData", "particles.bullet-particle-data");
        m.put("particles.bullet_particleMaterial", "particles.bullet-particle-material");
        m.put("CustomProjectiles.projectileType", "custom-projectiles.projectile-type");
        m.put("CustomProjectiles.explosionRadius", "custom-projectiles.explosion-radius");
        m.put("CustomProjectiles.Velocity", "custom-projectiles.velocity");
        m.put("CustomProjectiles.explosionKnockback", "custom-projectiles.explosion-knockback");
        RENAMED = Collections.unmodifiableMap(m);
    }

    private ItemKeys() {
    }

    /**
     * @return The current name of a key, which is the key itself if it wasn't renamed.
     */
    public static String translate(String key) {
        String renamed = RENAMED.get(key);
        return renamed != null ? renamed : key;
    }
}
