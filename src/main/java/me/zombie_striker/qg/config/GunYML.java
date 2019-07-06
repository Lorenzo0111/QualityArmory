package me.zombie_striker.qg.config;

import java.io.File;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.guns.utils.WeaponType;
import me.zombie_striker.qg.handlers.chargers.ChargingHandler;
import me.zombie_striker.qg.handlers.reloaders.ReloadingHandler;

public class GunYML extends ArmoryYML {

	public GunYML(File file) {
		super(file);
	}

	@Override
	public void verifyAllTagsExist() {
		if(overrideVerify)
			return;
		super.verifyAllTagsExist();
		setNoOverride("weapontype", WeaponType.RIFLE.name());
		setNoOverride("enableIronSights", false);
		setNoOverride("setZoomLevel", 0);
		setNoOverride("ammotype", "556");
		setNoOverride("sway", 0.2);
		setNoOverride("maxbullets", 0);
		setNoOverride("delayForReload", 1.5);
		setNoOverride("delayForShoot", 0.3);
		setNoOverride("bullets-per-shot", 1);
		setNoOverride("isAutomatic", false);
		setNoOverride("maxBulletDistance", 200);
		setNoOverride("unlimitedAmmo", false);
		setNoOverride("LightLeveOnShoot", 14);
		setNoOverride("firerate", 1);
		setNoOverride("recoil", 1);

		setNoOverride("particles.bullet_particle", "REDSTONE");
		setNoOverride("particles.bullet_particleR", 1);
		setNoOverride("particles.bullet_particleG", 1);
		setNoOverride("particles.bullet_particleB", 1);
		setNoOverride("particles.bullet_particleMaterial", "COAL_BLOCK");

		setNoOverride("Version_18_Support", !QAMain.isVersionHigherThan(1, 9));
		setNoOverride("ChargingHandler", "none");
		setNoOverride("addMuzzleSmoke", true);

		setNoOverride("drop-glow-color", "none");
		setNoOverride("headshotMultiplier", 2);
		setNoOverride("swayMultiplier", 2);
	}
	public GunYML setRecoil(double recoil) {
		set(false, "recoil", recoil);
		return this;
	}
	public GunYML setSwayMultiplier(double multiplier) {
		set(false, "swayMultiplier", multiplier);
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
		return setChargingHandler(rh.getName());
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
		set(false, "sway", sway);
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

	public GunYML setUnlimitedAmmo(boolean unlim) {
		set(false, "unlimitedAmmo", unlim);
		return this;
	}

	public GunYML setParticle(String particle) {
		set(false,"particles.bullet_particle", particle);
		return this;
	}
	public GunYML setParticle(double r, double g, double b, Material m) {
		return setParticle("REDSTONE", r, g, b, m);
	}

	public GunYML setParticle(String particle, double r, double g, double b, Material m) {
		set(false,"particles.bullet_particle", particle);
		set(false,"particles.bullet_particleR", r);
		set(false,"particles.bullet_particleG", g);
		set(false,"particles.bullet_particleB", b);
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

}
