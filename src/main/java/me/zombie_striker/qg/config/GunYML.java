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
import java.util.List;

public class GunYML extends ArmoryYML {

	public GunYML(File file) {
		super(file);
	}

	@Override
	public void verifyAllTagsExist() {
		super.verifyAllTagsExist();
		verify("weapontype", WeaponType.RIFLE.name());
		verify("enableIronSights", false);
		verify("setZoomLevel", 0);
		verify("ammotype", "556");
		verify("sway.defaultValue", 0.2);
		verify("firerate", 1);
		verify("maxbullets", 0);
		verify("delayForReload", 1.5);
		verify("delayForShoot", 0.3);
		verify("bullets-per-shot", 1);
		verify("isAutomatic", false);
		verify("maxBulletDistance", 200);
		verify("unlimitedAmmo", false);
		verify("LightLeveOnShoot", 14);
		verify("recoil", 1);
		verify("slownessOnEquip", 0);

		verify("particles.bullet_particle", "REDSTONE");
		verify("particles.bullet_particleR", 1);
		verify("particles.bullet_particleG", 1);
		verify("particles.bullet_particleB", 1);
		verify("particles.bullet_particleData", 0);
		verify("particles.bullet_particleMaterial", "COAL_BLOCK");

		verify("Version_18_Support", !QAMain.isVersionHigherThan(1, 9));
		verify("ChargingHandler", "none");
		verify("ReloadingHandler", "none");
		verify("addMuzzleSmoke", true);

		verify("drop-glow-color", "none");
		verify("headshotMultiplier", 3.5);
		verify("sway.defaultMultiplier", 2);

		verify("weaponsounds_volume", 4);
		verify("weaponsounds", WeaponSounds.getSoundByType(WeaponType.RIFLE));
		try {
			verify("weaponsounds_reloadingSound",
					get("ReloadingHandler") != null ?
							((ReloadingManager.getHandler((String) get("ReloadingHandler")).getDefaultReloadingSound()))
							: WeaponSounds.RELOAD_MAG_OUT.getSoundName());
			verify("weaponsounds_chargingSound",
					get("ChargingHandler") != null ?
							((ChargingManager.getHandler((String) get("ChargingHandler")).getDefaultChargingSound()))
							: WeaponSounds.RELOAD_BOLT.getSoundName());
		}catch (Error|Exception e5){}

		verify("sway.sneakModifier", true);
		verify("sway.moveModifier", true);
		verify("sway.runModifier", true);
		verify("sway.unscopedModifier", 1);
		verify("firing_knockback", 0);
		verify("KilledByMessage", "%player% was shot by %killer% using a %name%");
	}

	public GunYML setReloadingSound(String sound) {
		set(false, "weaponsounds_reloadingSound", sound);
		return this;
	}
	public GunYML setReloadingSound(WeaponSounds sound) {
		set(false, "weaponsounds_reloadingSound", sound.getSoundName());
		return this;
	}

	public GunYML setWeaponSound(WeaponSounds sound) {
		set( "weaponsounds", sound.getSoundName());
		return this;
	}
	public GunYML setSwayUnscopedModifier(int sway) {
		set(false, "sway.unscopedModifier", sway);
		return this;
	}

	public GunYML setKilledByMessage(String message) {
		set(false, "KilledByMessage", message);
		return this;
	}

	public GunYML setFiringKnockback(double message) {
		set(false, "firing_knockback", message);
		return this;
	}

	public GunYML setSlownessOnEquip(int amount) {
		set(false, "slownessOnEquip", amount);
		return this;
	}

	public GunYML setRecoil(double recoil) {
		set(false, "recoil", recoil);
		return this;
	}

	public GunYML setenableIronSights(boolean ironsights) {
		set(false, "enableIronSights", ironsights);
		return this;
	}

	public GunYML setMaxBullets(int amount) {
		set(false, "maxbullets", amount);
		return this;
	}

	public GunYML setSwayMultiplier(double multiplier) {
		set(false, "sway.defaultMultiplier", multiplier);
		return this;
	}

	public GunYML setHeadShotMultiplier(double multiplier) {
		set(false, "headshotMultiplier", multiplier);
		return this;
	}

	public GunYML setNightVisionOnScope(boolean b) {
		set(false, "hasNightVisionOnScope", b);
		return this;
	}

	public GunYML setVolume(float volume) {
		set(false, "weaponsounds_volume", volume);
		return this;
	}

	public GunYML setIsSecondaryWeapon(boolean isSecondary) {
		set(false, "isPrimaryWeapon", !isSecondary);
		return this;
	}

	public GunYML setCustomProjectileExplosionRadius(double radius) {
		set(false, "CustomProjectiles.explosionRadius", radius);
		return this;
	}

	public GunYML setCustomProjectile(String customProjectle) {
		set(false, "CustomProjectiles.projectileType", customProjectle);
		return this;
	}

	public GunYML setCustomProjectileVelocity(double velocity) {
		set(false, "CustomProjectiles.Velocity", velocity);
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
		set(false, "setZoomLevel", zoom);
		return this;
	}

	public GunYML setDistance(int distance) {
		set(false, "maxBulletDistance", distance);
		return this;
	}

	public GunYML setLightLevel(int level) {
		set(false, "LightLeveOnShoot", level);
		return this;
	}

	public GunYML setOn18(boolean on18) {
		set(false, "Version_18_Support", on18);
		return this;
	}

	public GunYML setMuzzleSmoke(boolean smoke) {
		set(false, "addMuzzleSmoke", smoke);
		return this;
	}

	public GunYML setChargingHandler(ChargingHandler ch) {
		return setChargingHandler(ch.getName());
	}

	public GunYML setChargingHandler(String ch) {
		set(false, "ChargingHandler", ch);
		return this;
	}

	public GunYML setReloadingHandler(ReloadingHandler rh) {
		return setReloadingHandler(rh.getName());
	}

	public GunYML setReloadingHandler(String rh) {
		set(false, "ReloadingHandler", rh);
		return this;
	}

	public GunYML setDelayReload(double reload) {
		set(false, "delayForReload", reload);
		return this;
	}

	public GunYML setDelayShoot(double delay) {
		set(false, "delayForShoot", delay);
		return this;
	}

	public GunYML setSway(double sway) {
		set(false, "sway.defaultValue", sway);
		return this;
	}

	public GunYML setBulletsPerShot(int shots) {
		set(false, "bullets-per-shot", shots);
		return this;
	}

	public GunYML setFireRate(int fireRate) {
		set(false, "firerate", fireRate);
		return this;
	}

	public GunYML setFullyAutomatic(int shots) {
		//set(false, "bullets-per-shot", shots);
		//set(false, "ChargingHandler", ChargingManager.RAPIDFIRE);
		set(false, "firerate", shots);
		set(false, "isAutomatic", true);
		return this;
	}

	public GunYML setAutomatic(boolean automatic) {
		set(false, "isAutomatic", automatic);
		return this;
	}

	public GunYML setUseOffhand(boolean offhand) {
		set(false, "enableBetterModelScopes", offhand);
		return this;
	}

	public GunYML setUnlimitedAmmo(boolean unlim) {
		set(false, "unlimitedAmmo", unlim);
		return this;
	}

	public GunYML setParticle(String particle) {
		set(false, "particles.bullet_particle", particle);
		return this;
	}

	public GunYML setParticle(double r, double g, double b, Material m) {
		return setParticle("REDSTONE", r, g, b, m);
	}

	public GunYML setParticle(String particle, double r, double g, double b, Material m) {
		set(false, "particles.bullet_particle", particle);
		set(false, "particles.bullet_particleR", r);
		set(false, "particles.bullet_particleG", g);
		set(false, "particles.bullet_particleB", b);
		set(false, "particles.bullet_particleMaterial", m.toString());
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
		set(false, "sway.sneakModifier", c);
		return this;
	}

	public GunYML setSway_movementModifier(boolean c) {
		set(false, "sway.moveModifier", c);
		return this;
	}

	public GunYML setSway_runModifier(boolean c) {
		set(false, "sway.runModifier", c);
		return this;
	}


}
