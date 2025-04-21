package me.zombie_striker.qg.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.ammo.AmmoType;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.guns.chargers.ChargingHandler;
import me.zombie_striker.qg.guns.chargers.ChargingManager;
import me.zombie_striker.qg.guns.projectiles.ProjectileManager;
import me.zombie_striker.qg.guns.reloaders.ReloadingHandler;
import me.zombie_striker.qg.guns.reloaders.ReloadingManager;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.guns.utils.WeaponType;
import me.zombie_striker.qg.utils.LocalUtils;

public class CrackshotLoader {

    public static boolean isCrackshotGun(final FileConfiguration crackshotFile) {
        for (final String s : crackshotFile.getKeys(false))
            if (crackshotFile.contains(s + ".Item_Information"))
                return true;
        return false;
    }

    public static void createYMLForGuns(final List<Gun> guns, final File data) {
        for (final Gun g : guns) {
            @SuppressWarnings("deprecation")
            WeaponSounds s = WeaponSounds.getByName(g.getWeaponSound());
            if (s == null)
                s = WeaponSounds.GUN_MEDIUM;
            final GunYML yml = GunYMLCreator.createNewCustomGun(data, "crackshot_" + g.getName(), g.getName(), g.getDisplayName(),
                    g.getItemData().getData(), null, g.getWeaponType(), s, g.hasIronSights(), g.getAmmoType().getName(),
                    (int) g.getDamage(), g.getMaxBullets(), (int) g.getPrice());
            yml.setLore(g.getCustomLore());
            yml.setSway(g.getSway());
            yml.setAutomatic(g.isAutomatic());
            yml.setBulletsPerShot(g.getBulletsPerShot());
            yml.setDelayReload(g.getReloadTime());
            yml.setVariant(g.getItemData().getVariant());
            yml.setMaterial(g.getItemData().getMat());
            yml.setZoomLevel(g.getZoomWhenIronSights());
            yml.setNightVisionOnScope(g.hasnightVision());
            yml.setParticle(g.getParticleR(), g.getParticleB(), g.getParticleG(), g.getParticleMaterial());
            if (g.getChargingHandler() != null)
                yml.setChargingHandler(g.getChargingHandler());
            yml.verifyAllTagsExist();
            yml.save();
            QAMain.DEBUG("-Creating CrackShot guns: " + g.getName());
        }
    }

    @SuppressWarnings("deprecation")
    public static List<Gun> loadCrackshotGuns(final FileConfiguration crackshotFile) {
        final List<Gun> guns4 = new ArrayList<>();
        for (final String name : crackshotFile.getKeys(false)) {
            final String internalname = name.toLowerCase();
            // Make sure all weapon names are lowercase
            if (me.zombie_striker.qg.api.QualityArmory.getGunByName(internalname) != null) {
                QAMain.DEBUG("CrackShot gun " + name + " already has a QA Counterpart.");
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
                final String[] temp = type.split(":");
                type = temp[0];
                data = Integer.parseInt(temp[1]);
            }
            try {
                final int i = Integer.parseInt(type);
                for (final Material m : Material.values()) {
                    if (m.getId() == i)
                        materialtype = m;
                }
                final int highestData = QualityArmory.findSafeSpot(materialtype, data, true, false) + 1;
                if (highestData >= materialtype.getMaxDurability()) {
                    variant = QualityArmory.findSafeSpotVariant(materialtype, data, true) + 1;
                } else {
                    data = highestData;
                }
            } catch (Error | Exception e45) {
                final Material configMaterial = Material.matchMaterial(type);
                if (configMaterial != null) {
                    materialtype = configMaterial;
                } else {
                    data = QualityArmory.findSafeSpot(materialtype, 2, true, false) + 1;
                }
            }

            final MaterialStorage ms = MaterialStorage.getMS(materialtype, data, variant);

            WeaponType wt = WeaponType.RIFLE;
            ChargingHandler ch = null;
            ReloadingHandler rh = null;
            String ammo = "762";
            final int maxbullets = crackshotFile.getInt(name + ".Reload.Reload_Amount");
            final double sway = crackshotFile.getDouble(name + ".Shooting.Bullet_Spread") / 9;
            final int damage = (int) crackshotFile.getDouble(name + ".Shooting.Projectile_Damage");
            final boolean automatic = crackshotFile.getBoolean(name + ".Fully_Automatic.Enable");
            String loreString = crackshotFile.getString(name + ".Item_Information.Item_Lore");
            int firerate = -1;
            if (loreString == null)
                loreString = "";
            final List<String> lore = Arrays.asList(LocalUtils.colorize(loreString).split("\\|"));
            final String displayname = LocalUtils.colorize(crackshotFile.getString(name + ".Item_Information.Item_Name"));
            final double price = crackshotFile.contains(name + ".SignShops.Price")
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
                    rh = ReloadingManager.getHandler(ReloadingManager.PUMP_ACTION_RELOAD);
                    break;
                case "break":
                    ch = ChargingManager.getHandler(ChargingManager.BREAKACTION);
                    rh = ReloadingManager.getHandler(ReloadingManager.SINGLE_RELOAD);
                    break;
                case "revolver":
                    ch = ChargingManager.getHandler(ChargingManager.REVOLVER);
                    rh = ReloadingManager.getHandler(ReloadingManager.SINGLE_RELOAD);
                    break;
                }
            }
            if (crackshotFile.contains(name + ".Reload.Reload_Bullets_Individually")
                    && crackshotFile.getBoolean(name + ".Reload.Reload_Bullets_Individually")) {
                if (rh == null)
                    rh = ReloadingManager.getHandler(ReloadingManager.SINGLE_RELOAD);
                // ch = ChargingManager.getHandler(ChargingManager.REVOLVER);
                reloadDelayInTicks *= maxbullets;
            }
            if (crackshotFile.contains(name + ".Burstfire") && crackshotFile.getBoolean(name + ".Burstfire.Enable")) {
                ch = ChargingManager.getHandler(ChargingManager.BURSTFIRE);
                projectiles = crackshotFile.getInt(name + ".Burstfire.Shots_Per_Burst");
                firerate = 3;
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
            // wt = WeaponType.RPG;
            // ch = ChargingManager.getHandler(ChargingManager.RPG);
            // rh = ReloadingManager.getHandler(ReloadingManager.SINGLERELOAD);
            // ammo = "rocket";
            // break;
            case "energy":
                wt = WeaponType.LAZER;
                break;
            }
            final ItemStack[] maters = new ItemStack[0];
            final Gun g = new Gun(internalname, ms, wt, false, AmmoType.getAmmo(ammo), sway, 2, maxbullets, damage, automatic, 1000,
                    WeaponSounds.getByName(WeaponSounds.getSoundByType(wt)), lore, internalname, price, maters);

            if (crackshotFile.getBoolean(name + ".Explosions.Enable")) {
                final int radius = crackshotFile.getInt(name + ".Explosions.Explosion_Radius");
                g.setCustomProjectile(ProjectileManager.RPG);
                g.setExplosionRadius(radius);
            }

            if (crackshotFile.contains(name + "Shooting.Delay_Between_Shots")) {
                final double time = crackshotFile.getDouble(name + "Shooting.Delay_Between_Shots") * 20;
                g.setDelayBetweenShots(time);
            }

            g.setDisplayname(displayname);
            if (ch != null)
                g.setChargingHandler(ch);
            if (rh != null)
                g.setReloadingHandler(rh);
            if (projectiles > 1)
                g.setBulletsPerShot(projectiles);
            if (firerate > 0)
                g.setFireRate(firerate);
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
            Material mP = Material.COAL_BLOCK;

            if (crackshotFile.contains(name + ".Particles")) {
                if (crackshotFile.contains(name + ".Particles.bullet_particle"))
                    particleName = crackshotFile.getString(name + ".Particles.bullet_particle");
                if (crackshotFile.contains(name + ".Particles.bullet_particleR"))
                    rP = crackshotFile.getDouble(name + ".Particles.bullet_particleR");
                if (crackshotFile.contains(name + ".Particles.bullet_particleG"))
                    gP = crackshotFile.getDouble(name + ".Particles.bullet_particleG");
                if (crackshotFile.contains(name + ".Particles.bullet_particleB"))
                    bP = crackshotFile.getDouble(name + ".Particles.bullet_particleB");
                if (crackshotFile.contains(name + ".Particles.bullet_particleMaterial"))
                    mP = Material.matchMaterial(crackshotFile.getString(name + ".Particles.bullet_particleMaterial", "COAL_BLOCK"));
            }

            if (mP == null) {
                mP = Material.COAL_BLOCK;
            }

            g.setParticles(Particle.valueOf(particleName), rP, gP, bP, mP);

            g.setReloadingTimeInSeconds(reloadDelayInTicks / 20);
            guns4.add(g);
            QAMain.gunRegister.put(ms, g);
            QAMain.DEBUG("-Registering Crackshot Gun: " + internalname);
        }
        return guns4;
    }
}
