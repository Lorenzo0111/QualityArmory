package me.zombie_striker.qg.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.MaterialStorage;
import me.zombie_striker.qg.QualityArmory;
import me.zombie_striker.qg.ammo.AmmoType;
import me.zombie_striker.qg.config.GunYML;
import me.zombie_striker.qg.config.GunYMLCreator;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.guns.projectiles.ProjectileManager;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.guns.utils.WeaponType;
import me.zombie_striker.qg.handlers.chargers.ChargingHandler;
import me.zombie_striker.qg.handlers.chargers.ChargingManager;
import me.zombie_striker.qg.handlers.reloaders.ReloadingHandler;
import me.zombie_striker.qg.handlers.reloaders.ReloadingManager;

public class CrackshotLoader {

	public static boolean isCrackshotGun(FileConfiguration crackshotFile) {
		for (String s : crackshotFile.getKeys(false))
			if (crackshotFile.contains(s + ".Item_Information"))
				return true;
		return false;
	}

	public static void createYMLForGuns(List<Gun> guns, File data) {
		for (Gun g : guns) {
			WeaponSounds s = WeaponSounds.getByName(g.getWeaponSound());
			if (s == null)
				s = WeaponSounds.GUN_MEDIUM;
			GunYML yml = GunYMLCreator.createNewCustomGun(data, "crackshot_" + g.getName(), g.getName(),
					g.getDisplayName(), g.getItemData().getData(), null, g.getWeaponType(), s, g.hasIronSights(),
					g.getAmmoType().getName(), (int) g.getDamage(), g.getMaxBullets(), (int) g.cost());
			yml.setLore(g.getCustomLore());
			yml.setSway(g.getSway());
			yml.setAutomatic(g.isAutomatic());
			yml.setBulletsPerShot(g.getBulletsPerShot());
			yml.setDelayReload(g.getReloadTime());
			yml.setVariant(g.getItemData().getVarient());
			yml.setMaterial(g.getItemData().getMat());
			yml.setZoomLevel(g.getZoomWhenIronSights());
			yml.setNightVisionOnScope(g.hasnightVision());
			yml.setParticle(g.getParticleR(), g.getParticleB(), g.getParticleG());
			if (g.getChargingVal() != null)
				yml.setChargingHandler(g.getChargingVal());
			yml.verifyAllTagsExist();
			yml.save();
			Main.DEBUG("-Creating CrackShot guns: " + g.getName());
		}
	}

	@SuppressWarnings("deprecation")
	public static List<Gun> loadCrackshotGuns(FileConfiguration crackshotFile) {
		List<Gun> guns4 = new ArrayList<>();
		for (String name : crackshotFile.getKeys(false)) {
			String internalname = name.toLowerCase();
			// Make sure all weapon names are lowercase
			if (QualityArmory.getGunByName(internalname) != null) {
				Main.DEBUG("CrackShot gun " + name + " already has a QA Counterpart.");
				continue;
			}

			// Check for RoitShield
			if (crackshotFile.contains(name + ".Riot_Shield"))
				continue;

			Material materialtype = Material.DIAMOND_AXE;
			int data = 0;
			int variant = 0;

			String type = crackshotFile.getString(name + ".Item_Information.Item_Type");
			if (type.contains(":")) {
				String[] temp = type.split(":");
				type = temp[0];
				data = Integer.parseInt(temp[1]);
			}
			try {
				int i = Integer.parseInt(type);
				for (Material m : Material.values()) {
					if (m.getId() == i)
						materialtype = m;
				}
				int highestData = QualityArmory.findSafeSpot(materialtype, data, true) + 1;
				if (highestData >= materialtype.getMaxDurability()) {
					variant = QualityArmory.findSafeSpotVariant(materialtype, data, true) + 1;
				} else {
					data = highestData;
				}
			} catch (Error | Exception e45) {
				Material configMaterial = Material.matchMaterial(type);
				if (configMaterial != null) {
					materialtype = configMaterial;
				} else {
					data = QualityArmory.findSafeSpot(materialtype, 2, true) + 1;
				}
			}

			MaterialStorage ms = MaterialStorage.getMS(materialtype, data, variant);

			WeaponType wt = WeaponType.RIFLE;
			ChargingHandler ch = null;
			ReloadingHandler rh = null;
			String ammo = "762";
			int maxbullets = crackshotFile.getInt(name + ".Reload.Reload_Amount");
			double sway = crackshotFile.getDouble(name + ".Shooting.Bullet_Spread") / 9;
			int damage = (int) crackshotFile.getDouble(name + ".Shooting.Projectile_Damage");
			boolean automatic = crackshotFile.getBoolean(name + ".Fully_Automatic.Enable");
			String loreString = crackshotFile.getString(name + ".Item_Information.Item_Lore");
			if (loreString == null)
				loreString = "";
			List<String> lore = Arrays.asList(ChatColor.translateAlternateColorCodes('&', loreString).split("\\|"));
			String displayname = ChatColor.translateAlternateColorCodes('&',
					crackshotFile.getString(name + ".Item_Information.Item_Name"));
			double price = crackshotFile.contains(name + ".SignShops.Price")
					? Integer.parseInt(crackshotFile.getString(name + ".SignShops.Price").split("-")[0])
					: 1000;
			int projectiles = crackshotFile.getInt(name + ".Shooting.Projectile_Amount");
			int reloadDelayInTicks = crackshotFile.getInt(name + ".Reload.Reload_Duration");

			if (crackshotFile.contains(name + ".Firearm_Action.Type")) {
				switch (crackshotFile.getString(name + ".Firearm_Action.Type")) {
				case "slide":
					break;
				case "bolt":
					ch = ChargingManager.getHandler(ChargingManager.BOLT);
					break;
				case "lever":
					ch = ChargingManager.getHandler(ChargingManager.BOLT);
					break;
				case "pump":
					ch = ChargingManager.getHandler(ChargingManager.PUMPACTION);
					rh = ReloadingManager.getHandler(ReloadingManager.PUMPACTIONRELOAD);
					break;
				case "break":
					ch = ChargingManager.getHandler(ChargingManager.BREAKACTION);
					rh = ReloadingManager.getHandler(ReloadingManager.SINGLERELOAD);
					break;
				case "revolver":
					ch = ChargingManager.getHandler(ChargingManager.REVOLVER);
					rh = ReloadingManager.getHandler(ReloadingManager.SINGLERELOAD);
					break;
				}
			}
			if (crackshotFile.contains(name + ".Reload.Reload_Bullets_Individually")
					&& crackshotFile.getBoolean(name + ".Reload.Reload_Bullets_Individually")) {
				if (rh == null)
					rh = ReloadingManager.getHandler(ReloadingManager.SINGLERELOAD);
				// ch = ChargingManager.getHandler(ChargingManager.REVOLVER);
				reloadDelayInTicks *= maxbullets;
			}
			if (crackshotFile.contains(name + ".Burstfire") && crackshotFile.getBoolean(name + ".Burstfire.Enable")) {
				ch = ChargingManager.getHandler(ChargingManager.BURSTFIRE);
				projectiles = crackshotFile.getInt(name + ".Burstfire.Shots_Per_Burst");
			}

			switch (crackshotFile.getString(name + ".Shooting.Projectile_Type")) {
			case "egg":
				wt = WeaponType.PISTOL;
				ammo = "9mm";
				break;
			case "fireball":
			case "snowball":
				wt = WeaponType.RIFLE;
					break;
			//	wt = WeaponType.RPG;
			//	ch = ChargingManager.getHandler(ChargingManager.RPG);
			//	rh = ReloadingManager.getHandler(ReloadingManager.SINGLERELOAD);
			//	ammo = "rocket";
			//	break;
			case "energy":
				wt = WeaponType.LAZER;
				break;
			}
			ItemStack[] maters = new ItemStack[0];
			Gun g = new Gun(internalname, ms, wt, false, AmmoType.getAmmo(ammo), sway, 2, maxbullets, damage, automatic,
					1000, WeaponSounds.getByName(WeaponSounds.getSoundByType(wt)), lore, internalname, price, maters);

			if(crackshotFile.getBoolean(name+".Explosions.Enable")) {
				int radius = crackshotFile.getInt(name+".Explosions.Explosion_Radius");
				g.setCustomProjectile(ProjectileManager.RPG);
				g.setExplosionRadius(radius);
			}
			
			if(crackshotFile.contains(name+"Shooting.Delay_Between_Shots")) {
				double time = crackshotFile.getDouble(name+"Shooting.Delay_Between_Shots")*20;
				g.setDelayBetweenShots(time);
			}
			
			
			g.setDisplayName(displayname);
			if (ch != null)
				g.setChargingHandler(ch);
			if (rh != null)
				g.setReloadingHandler(rh);
			if (projectiles > 1)
				g.setBulletsPerShot(projectiles);
			if (automatic && crackshotFile.contains(name + ".Fully_Automatic.Fire_Rate")) {
				g.setFireRate(crackshotFile.getInt(name + ".Fully_Automatic.Fire_Rate") / 2);
			}

			if (crackshotFile.contains(name + ".Scope") && crackshotFile.getBoolean(name + ".Scope.Enable")) {
				if (crackshotFile.contains(name + ".Scope.Zoom_Amount"))
					g.setZoomLevel(crackshotFile.getInt(name + ".Scope.Zoom_Amount"));
				if (crackshotFile.contains(name + ".Scope.Night_Vision"))
					g.setNightVision(crackshotFile.getBoolean(name + ".Scope.Night_Vision"));
			}
			double rP = 1, gP = 1, bP = 1;
			String particleName = "REDSTONE";
			if (crackshotFile.contains(name + ".Particles")) {
				if (crackshotFile.contains(name + ".Particles.bullet_particle"))
					particleName = crackshotFile.getString(name + ".Particles.bullet_particle");
				if (crackshotFile.contains(name + ".Particles.bullet_particleR"))
					rP = crackshotFile.getDouble(name + ".Particles.bullet_particleR");
				if (crackshotFile.contains(name + ".Particles.bullet_particleG"))
					gP = crackshotFile.getDouble(name + ".Particles.bullet_particleG");
				if (crackshotFile.contains(name + ".Particles.bullet_particleB"))
					bP = crackshotFile.getDouble(name + ".Particles.bullet_particleB");
			}
			g.setParticles(Particle.valueOf(particleName), rP, gP, bP);

			g.setReloadingTimeInSeconds(reloadDelayInTicks / 20);
			guns4.add(g);
			Main.gunRegister.put(ms, g);
			Main.DEBUG("-Registering Crackshot Gun: " + internalname);
		}
		return guns4;
	}
}
