package me.zombie_striker.qg.config;

import java.io.File;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.handlers.gunvalues.ChargingHandler;
import me.zombie_striker.qg.handlers.gunvalues.ChargingManager;

public class GunYML extends ArmoryYML{

	public GunYML(File file) {
		super(file);
	}
	@Override
	public void verifyAllTagsExist() {
		super.verifyAllTagsExist();
		setNoOverride("enableIronSights", false);
		setNoOverride("ammotype", "556");
		setNoOverride("sway", 0.3);
		setNoOverride("maxbullets", 0);
		setNoOverride("delayForReload", 1.5);
		setNoOverride("delayForShoot", 0.3);
		setNoOverride("bullets-per-shot", 1);
		setNoOverride("isAutomatic", false);
		setNoOverride("maxBulletDistance", 200);
		setNoOverride("unlimitedAmmo", false);
		setNoOverride("LightLeveOnShoot", 14);

		setNoOverride("particles.bullet_particle", "REDSTONE");
		setNoOverride("particles.bullet_particleR", 1);
		setNoOverride("particles.bullet_particleG", 1);
		setNoOverride("particles.bullet_particleB", 1);

		setNoOverride("Version_18_Support", !Main.isVersionHigherThan(1, 9));
		setNoOverride("ChargingHandler", "nonw");
		setNoOverride("addMuzzleSmoke", true);

		setNoOverride("drop-glow-color", "none");
	}


	public GunYML setInvalid(boolean invalid) {
		set(false, "invalid", invalid);
		return this;
	}

	public GunYML setLore(List<String> lore) {
		set(false, "lore", lore);
		return this;
	}

	public GunYML setVariant(int var) {
		set(false, "variant", var);
		return this;
	}

	public GunYML setDurability(int durib) {
		set(false, "durability", durib);
		return this;
	}

	public GunYML setPrice(int cost) {
		set(false, "price", cost);
		return this;
	}
	public GunYML setMaterial(Material mat) {
		set(false,  "material", mat.name());
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

	public GunYML setFullyAutomatic(int shots) {
		set(false, "bullets-per-shot", shots);
		set(false, "ChargingHandler", ChargingManager.RAPIDFIRE);
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

	public GunYML setParticle(double r, double g, double b) {
		return setParticle("REDSTONE", r, g, b);
	}

	public GunYML setParticle(String particle, double r, double g, double b) {
		setNoOverride("particles.bullet_particle", particle);
		setNoOverride("particles.bullet_particleR", r);
		setNoOverride("particles.bullet_particleG", g);
		setNoOverride("particles.bullet_particleB", b);
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
