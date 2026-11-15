package me.zombie_striker.qg.config;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.guns.chargers.ChargingHandler;
import me.zombie_striker.qg.guns.chargers.ChargingManager;
import me.zombie_striker.qg.guns.reloaders.ReloadingHandler;
import me.zombie_striker.qg.guns.reloaders.ReloadingManager;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.guns.utils.WeaponType;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class GunYML extends ArmoryYML {

	public GunYML(File file) {
		super(file);
	}

	@Override
	public void verifyAllTagsExist() {
		super.verifyAllTagsExist();
		verify("weapon-type", WeaponType.RIFLE.name());
		verify("enable-iron-sights", false);
		verify("zoom-level", 0);
		verify("ammo-type", "556");
		verify("sway.default-value", 0.2);
		verify("fire-rate", 1);
		verify("max-bullets", 0);
		verify("delay-for-reload", 1.5);
		verify("delay-for-shoot", 0.3);
		verify("bullets-per-shot", 1);
		verify("automatic", false);
		verify("max-bullet-distance", 200);
		verify("unlimited-ammo", false);
		verify("light-level-on-shoot", 14);
		verify("recoil", 1);
		verify("slowness-on-equip", 0);

		verify("particles.bullet-particle", "REDSTONE");
		verify("particles.bullet-particle-r", 1);
		verify("particles.bullet-particle-g", 1);
		verify("particles.bullet-particle-b", 1);
		verify("particles.bullet-particle-data", 0);
		verify("particles.bullet-particle-material", "COAL_BLOCK");

		verify("version-18-support", !QAMain.isVersionHigherThan(1, 9));
		verify("charging-handler", "none");
		verify("reloading-handler", "none");
		verify("add-muzzle-smoke", true);

		verify("drop-glow-color", "none");
		verify("headshot-multiplier", 3.5);
		verify("sway.default-multiplier", 2);

		verify("weapon-sounds-volume", 4);
		verify("weapon-sounds", WeaponSounds.getSoundByType(WeaponType.RIFLE));
		// The default sounds come from the handlers; with no handler ("none") they are left unset, as before
		ReloadingHandler reloading = get("reloading-handler") != null ? ReloadingManager.getHandler((String) get("reloading-handler")) : null;
		if (get("reloading-handler") == null || reloading != null) {
			verify("reloading-sound", reloading != null ? reloading.getDefaultReloadingSound() : WeaponSounds.RELOAD_MAG_OUT.getSoundName());
			ChargingHandler charging = get("charging-handler") != null ? ChargingManager.getHandler((String) get("charging-handler")) : null;
			if (get("charging-handler") == null || charging != null)
				verify("charging-sound", charging != null ? charging.getDefaultChargingSound() : WeaponSounds.RELOAD_BOLT.getSoundName());
		}

		verify("sway.sneak-modifier", true);
		verify("sway.move-modifier", true);
		verify("sway.run-modifier", true);
		verify("sway.unscoped-modifier", 1);
		verify("firing-knockback", 0);
		verify("killed-by-message", "%player% was shot by %killer% using a %name%");
		verify("destructable-materials", Collections.singletonList("MATERIAL_NAME_HERE"));
	}

	public GunYML setReloadingSound(String sound) {
		set(false, "reloading-sound", sound);
		return this;
	}
	public GunYML setReloadingSound(WeaponSounds sound) {
		set(false, "reloading-sound", sound.getSoundName());
		return this;
	}

	public GunYML setWeaponSound(WeaponSounds sound) {
		set( "weapon-sounds", sound.getSoundName());
		return this;
	}
	public GunYML setSwayUnscopedModifier(int sway) {
		set(false, "sway.unscoped-modifier", sway);
		return this;
	}

	public GunYML setKilledByMessage(String message) {
		set(false, "killed-by-message", message);
		return this;
	}

	public GunYML setFiringKnockback(double message) {
		set(false, "firing-knockback", message);
		return this;
	}

	public GunYML setSlownessOnEquip(int amount) {
		set(false, "slowness-on-equip", amount);
		return this;
	}

	public GunYML setRecoil(double recoil) {
		set(false, "recoil", recoil);
		return this;
	}

	public GunYML setenableIronSights(boolean ironsights) {
		set(false, "enable-iron-sights", ironsights);
		return this;
	}

	public GunYML setMaxBullets(int amount) {
		set(false, "max-bullets", amount);
		return this;
	}

	public GunYML setSwayMultiplier(double multiplier) {
		set(false, "sway.default-multiplier", multiplier);
		return this;
	}

	public GunYML setHeadShotMultiplier(double multiplier) {
		set(false, "headshot-multiplier", multiplier);
		return this;
	}

	public GunYML setNightVisionOnScope(boolean b) {
		set(false, "night-vision-on-scope", b);
		return this;
	}

	public GunYML setVolume(float volume) {
		set(false, "weapon-sounds-volume", volume);
		return this;
	}

	public GunYML setIsSecondaryWeapon(boolean isSecondary) {
		set(false, "primary-weapon", !isSecondary);
		return this;
	}

	public GunYML setCustomProjectileExplosionRadius(double radius) {
		set(false, "custom-projectiles.explosion-radius", radius);
		return this;
	}

	public GunYML setCustomProjectileExplosionKnockback(double knockback) {
		set(false, "CustomProjectiles.explosionKnockback", knockback);
		return this;
	}

	public GunYML setCustomProjectile(String customProjectle) {
		set(false, "custom-projectiles.projectile-type", customProjectle);
		return this;
	}

	public GunYML setCustomProjectileVelocity(double velocity) {
		set(false, "custom-projectiles.velocity", velocity);
		return this;
	}

	@Override
	public GunYML setInvalid(boolean invalid) {
		set(false, "invalid", invalid);
		return this;
	}

	@Override
	public GunYML setLore(List<String> lore) {
		set(false, "lore", lore);
		return this;
	}

	@Override
	public GunYML setLore(String... lore) {
		set("lore", lore);
		return this;
	}

	@Override
	public GunYML setVariant(int var) {
		set(false, "variant", var);
		return this;
	}

	@Override
	public GunYML setDurability(int durib) {
		set(false, "durability", durib);
		return this;
	}

	@Override
	public GunYML setPrice(int cost) {
		set(false, "price", cost);
		return this;
	}

	@Override
	public GunYML setMaterial(Material mat) {
		set(false, "material", mat.name());
		return this;
	}

	public GunYML setZoomLevel(int zoom) {
		set(false, "zoom-level", zoom);
		return this;
	}

	public GunYML setDistance(int distance) {
		set(false, "max-bullet-distance", distance);
		return this;
	}

	public GunYML setLightLevel(int level) {
		set(false, "light-level-on-shoot", level);
		return this;
	}

	public GunYML setOn18(boolean on18) {
		set(false, "version-18-support", on18);
		return this;
	}

	public GunYML setMuzzleSmoke(boolean smoke) {
		set(false, "add-muzzle-smoke", smoke);
		return this;
	}

	public GunYML setChargingHandler(ChargingHandler ch) {
		return setChargingHandler(ch.getName());
	}

	public GunYML setChargingHandler(String ch) {
		set(false, "charging-handler", ch);
		return this;
	}

	public GunYML setReloadingHandler(ReloadingHandler rh) {
		return setReloadingHandler(rh.getName());
	}

	public GunYML setReloadingHandler(String rh) {
		set(false, "reloading-handler", rh);
		return this;
	}

	public GunYML setDelayReload(double reload) {
		set(false, "delay-for-reload", reload);
		return this;
	}

	public GunYML setDelayShoot(double delay) {
		set(false, "delay-for-shoot", delay);
		return this;
	}

	public GunYML setSway(double sway) {
		set(false, "sway.default-value", sway);
		return this;
	}

	public GunYML setBulletsPerShot(int shots) {
		set(false, "bullets-per-shot", shots);
		return this;
	}

	public GunYML setFireRate(int fireRate) {
		set(false, "fire-rate", fireRate);
		return this;
	}

	public GunYML setFullyAutomatic(int shots) {
		//set(false, "bullets-per-shot", shots);
		//set(false, "charging-handler", ChargingManager.RAPIDFIRE);
		set(false, "fire-rate", shots);
		set(false, "automatic", true);
		return this;
	}

	public GunYML setAutomatic(boolean automatic) {
		set(false, "automatic", automatic);
		return this;
	}

	public GunYML setUseOffhand(boolean offhand) {
		set(false, "enable-better-model-scopes", offhand);
		return this;
	}

	public GunYML setUnlimitedAmmo(boolean unlim) {
		set(false, "unlimited-ammo", unlim);
		return this;
	}

	public GunYML setParticle(String particle) {
		set(false, "particles.bullet-particle", particle);
		return this;
	}

	public GunYML setParticle(double r, double g, double b, Material m) {
		return setParticle("REDSTONE", r, g, b, m);
	}

	public GunYML setParticle(String particle, double r, double g, double b, Material m) {
		set(false, "particles.bullet-particle", particle);
		set(false, "particles.bullet-particle-r", r);
		set(false, "particles.bullet-particle-g", g);
		set(false, "particles.bullet-particle-b", b);
		set(false, "particles.bullet-particle-material", m.toString());
		return this;
	}

	public GunYML setDropGlow(ChatColor c) {
		return setDropGlow(c.name());
	}

	public GunYML setDropGlow(String c) {
		set(false, "drop-glow-color", c);
		return this;
	}


	public GunYML setSway_SneakModifier(boolean c) {
		set(false, "sway.sneak-modifier", c);
		return this;
	}

	public GunYML setSway_movementModifier(boolean c) {
		set(false, "sway.move-modifier", c);
		return this;
	}

	public GunYML setSway_runModifier(boolean c) {
		set(false, "sway.run-modifier", c);
		return this;
	}


}
