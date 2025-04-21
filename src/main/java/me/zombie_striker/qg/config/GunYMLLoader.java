package me.zombie_striker.qg.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.ammo.Ammo;
import me.zombie_striker.qg.ammo.AmmoType;
import me.zombie_striker.qg.armor.Helmet;
import me.zombie_striker.qg.attachments.AttachmentBase;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.guns.chargers.ChargingManager;
import me.zombie_striker.qg.guns.reloaders.ReloadingManager;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.guns.utils.WeaponType;
import me.zombie_striker.qg.miscitems.AmmoBag;
import me.zombie_striker.qg.miscitems.Flashbang;
import me.zombie_striker.qg.miscitems.Grenade;
import me.zombie_striker.qg.miscitems.IncendaryGrenades;
import me.zombie_striker.qg.miscitems.MedKit;
import me.zombie_striker.qg.miscitems.MeleeItems;
import me.zombie_striker.qg.miscitems.Molotov;
import me.zombie_striker.qg.miscitems.ProxyMines;
import me.zombie_striker.qg.miscitems.SmokeGrenades;
import me.zombie_striker.qg.miscitems.StickyGrenades;
import me.zombie_striker.qg.miscitems.ThrowableItems;
import me.zombie_striker.qg.utils.LocalUtils;

public class GunYMLLoader {

    public static void loadAmmo(final QAMain main) {

        if (new File(main.getDataFolder(), "ammo").exists()) {
            int items = 0;
            for (final File f : new File(main.getDataFolder(), "ammo").listFiles()) {
                try {
                    if (f.getName().contains("yml")) {
                        final FileConfiguration f2 = YamlConfiguration.loadConfiguration(f);
                        if ((!f2.contains("invalid")) || !f2.getBoolean("invalid")) {
                            final Material m = f2.contains("material") ? Material.matchMaterial(f2.getString("material"))
                                    : Material.DIAMOND_AXE;
                            final int variant = f2.contains("variant") ? f2.getInt("variant") : 0;
                            final String name = f2.getString("name");
                            if (QAMain.verboseLoadingLogging)
                                main.getLogger().info("-Loading AmmoType: " + name);

                            String extraData = null;
                            if (f2.contains("skull_owner")) {
                                extraData = f2.getString("skull_owner");
                            }
                            String ed2 = null;
                            if (f2.contains("skull_owner_custom_url")
                                    && !f2.getString("skull_owner_custom_url").equals(Ammo.NO_SKIN_STRING)) {
                                ed2 = f2.getString("skull_owner_custom_url");
                            }

                            final MaterialStorage ms = MaterialStorage.getMS(m, f2.getInt("id"), variant, extraData, ed2);
                            final ItemStack[] materails = main.convertIngredients(f2.getStringList("craftingRequirements"));
                            final String displayname = f2.contains("displayname") ? LocalUtils.colorize(f2.getString("displayname"))
                                    : (ChatColor.WHITE + name);
                            final List<String> extraLore2 = f2.contains("lore") ? f2.getStringList("lore") : null;
                            final List<String> extraLore = new ArrayList<String>();
                            try {
                                for (final String lore : extraLore2) {
                                    extraLore.add(LocalUtils.colorize(lore));
                                }
                            } catch (Error | Exception re52) {
                            }

                            final double price = f2.contains("price") ? f2.getDouble("price") : 100;
                            final boolean allowInShop = f2.getBoolean("allowInShop", true) && price > 0;

                            int amountA = f2.getInt("maxAmount");
                            if (f2.contains("maxItemStack")) {
                                amountA = (f2.getInt("maxItemStack"));
                            }
                            final double piercing = f2.getDouble("piercingSeverity");

                            final Ammo da = new Ammo(name, displayname, extraLore, ms, amountA, false, 1, price, materails, piercing);

                            da.setEnableShop(allowInShop);
                            da.setCustomLore(extraLore);

                            QAMain.ammoRegister.put(ms, da);
                            items++;

                            if (extraData != null) {
                                da.setSkullOwner(extraData);
                            }
                            if (ed2 != null) {
                                da.setCustomSkin(ed2);
                            }
                            if (f2.contains("craftingReturnAmount")) {
                                da.setCraftingReturn(f2.getInt("craftingReturnAmount"));
                            }

                        }
                    }
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
            if (!QAMain.verboseLoadingLogging)
                main.getLogger().info("-Loaded " + items + " Ammo types.");

        }
    }

    public static void loadArmor(final QAMain main) {
        if (new File(main.getDataFolder(), "armor").exists()) {
            int items = 0;

            for (final File f : new File(main.getDataFolder(), "armor").listFiles()) {
                try {
                    if (f.getName().contains("yml")) {
                        final FileConfiguration f2 = YamlConfiguration.loadConfiguration(f);
                        if ((!f2.contains("invalid")) || !f2.getBoolean("invalid")) {
                            final String name = f2.getString("name");
                            if (QAMain.verboseLoadingLogging)
                                main.getLogger().info("-Loading Armor: " + name);

                            final Material m = f2.contains("material") ? Material.matchMaterial(f2.getString("material"))
                                    : Material.DIAMOND_AXE;
                            final int variant = f2.contains("variant") ? f2.getInt("variant") : 0;
                            final MaterialStorage ms = MaterialStorage.getMS(m, f2.getInt("id"), variant);
                            final ItemStack[] materails = main.convertIngredients(f2.getStringList("craftingRequirements"));
                            final String displayname = f2.contains("displayname") ? LocalUtils.colorize(f2.getString("displayname"))
                                    : (ChatColor.WHITE + name);
                            final List<String> rawLore = f2.contains("lore") ? f2.getStringList("lore") : null;
                            final List<String> lore = new ArrayList<String>();
                            try {
                                for (final String lore2 : rawLore) {
                                    lore.add(LocalUtils.colorize(lore2));
                                }
                            } catch (Error | Exception re52) {
                            }

                            final int price = f2.contains("price") ? f2.getInt("price") : 100;
                            final boolean allowInShop = f2.getBoolean("allowInShop", true) && price > 0;

                            final WeaponType wt = WeaponType.getByName(f2.getString("MiscType"));

                            if (wt == WeaponType.HELMET) {
                                final Helmet helmet = new Helmet(name, displayname, lore, materails, ms, price, allowInShop);
                                helmet.setHeightMax(f2.getDouble("maxProtectionHeight"));
                                helmet.setHeightMin(f2.getDouble("minProtectionHeight"));
                                helmet.setProtection(f2.getInt("protection", 0));
                                QAMain.armorRegister.put(ms, helmet);
                                items++;
                            }
                        }
                    }
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
            if (!QAMain.verboseLoadingLogging)
                main.getLogger().info("-Loaded " + items + " Armor types.");
        }
    }

    public static void loadMisc(final QAMain main) {
        if (new File(main.getDataFolder(), "misc").exists()) {
            int items = 0;
            for (final File f : new File(main.getDataFolder(), "misc").listFiles()) {
                try {
                    if (f.getName().contains("yml")) {
                        final FileConfiguration f2 = YamlConfiguration.loadConfiguration(f);
                        if ((!f2.contains("invalid")) || !f2.getBoolean("invalid")) {
                            final String name = f2.getString("name");
                            if (QAMain.verboseLoadingLogging)
                                main.getLogger().info("-Loading Misc: " + name);

                            final Material m = f2.contains("material") ? Material.matchMaterial(f2.getString("material"))
                                    : Material.DIAMOND_AXE;
                            final int variant = f2.contains("variant") ? f2.getInt("variant") : 0;
                            final MaterialStorage ms = MaterialStorage.getMS(m, f2.getInt("id"), variant);
                            final ItemStack[] materails = main.convertIngredients(f2.getStringList("craftingRequirements"));
                            final String displayname = f2.contains("displayname") ? LocalUtils.colorize(f2.getString("displayname"))
                                    : (ChatColor.WHITE + name);
                            final List<String> rawLore = f2.contains("lore") ? f2.getStringList("lore") : null;
                            final List<String> lore = new ArrayList<String>();
                            try {
                                for (final String lore2 : rawLore) {
                                    lore.add(LocalUtils.colorize(lore2));
                                }
                            } catch (Error | Exception re52) {
                            }

                            final int price = f2.contains("price") ? f2.getInt("price") : 100;
                            final boolean allowInShop = f2.getBoolean("allowInShop", true) && price > 0;

                            final int damage = f2.contains("damage") ? f2.getInt("damage") : 1;
                            // int durib = f2.contains("durability") ? f2.getInt("durability") : 1000;

                            final WeaponType wt = WeaponType.getByName(f2.getString("MiscType"));

                            final double radius = f2.contains("radius") ? f2.getDouble("radius") : 0;
                            items++;

                            CustomBaseObject base = null;

                            final String soundEquip = f2.contains("sound_equip") ? f2.getString("sound_equip") : null;
                            final String soundHit = f2.contains("sound_meleehit") ? f2.getString("sound_meleehit") : null;

                            if (wt == WeaponType.MEDKIT)
                                QAMain.miscRegister.put(ms, base = new MedKit(ms, name, displayname, materails, price));
                            if (wt == WeaponType.AMMO_BAG)
                                QAMain.miscRegister.put(ms,
                                        base = new AmmoBag(ms, name, displayname, materails, f2.getInt("max", 5), price));
                            if (wt == WeaponType.MELEE) {
                                QAMain.miscRegister.put(ms, base = new MeleeItems(ms, name, displayname, materails, price, damage));
                                base.setSoundOnEquip(soundEquip);
                                base.setSoundOnHit(soundHit);
                                base.setCustomLore(lore);
                            }
                            if (wt == WeaponType.GRENADES)
                                QAMain.miscRegister.put(ms,
                                        base = new Grenade(materails, price, damage, radius, name, displayname, lore, ms));
                            if (wt == WeaponType.SMOKE_GRENADES)
                                QAMain.miscRegister.put(ms,
                                        base = new SmokeGrenades(materails, price, damage, radius, name, displayname, lore, ms));
                            if (wt == WeaponType.INCENDARY_GRENADES)
                                QAMain.miscRegister.put(ms,
                                        base = new IncendaryGrenades(materails, price, damage, radius, name, displayname, lore, ms));
                            if (wt == WeaponType.PROXYMINES)
                                QAMain.miscRegister.put(ms,
                                        base = new ProxyMines(materails, price, damage, radius, name, displayname, lore, ms));
                            if (wt == WeaponType.STICKYGRENADE)
                                QAMain.miscRegister.put(ms,
                                        base = new StickyGrenades(materails, price, damage, radius, name, displayname, lore, ms));
                            if (wt == WeaponType.MOLOTOV)
                                QAMain.miscRegister.put(ms,
                                        base = new Molotov(materails, price, damage, radius, name, displayname, lore, ms));
                            if (wt == WeaponType.FLASHBANGS)
                                QAMain.miscRegister.put(ms,
                                        base = new Flashbang(materails, price, damage, radius, name, displayname, lore, ms));

                            if (base != null) {
                                base.setCustomLore(lore);
                                base.setIngredients(materails);
                                base.setEnableShop(allowInShop);
                            }

                            if (f2.contains("maxItemStack"))
                                base.setMaxItemStack(f2.getInt("maxItemStack"));
                            if (base instanceof final ThrowableItems throwableItems) {
                                if (f2.contains("ThrowSpeed"))
                                    throwableItems.setThrowSpeed(f2.getDouble("ThrowSpeed"));
                            }

                        }
                    }
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
            if (!QAMain.verboseLoadingLogging)
                main.getLogger().info("-Loaded " + items + " Misc.");
        }
    }

    public static void loadGuns(final QAMain main, final File f) {
        if (f.getName().contains("yml")) {
            final FileConfiguration f2 = YamlConfiguration.loadConfiguration(f);
            if ((!f2.contains("invalid")) || !f2.getBoolean("invalid")) {
                final String name = f2.getString("name");
                if (QAMain.verboseLoadingLogging)
                    main.getLogger().info("-Loading Gun: " + name);

                final Material m = f2.contains("material") ? Material.matchMaterial(f2.getString("material")) : Material.DIAMOND_AXE;
                final int variant = f2.contains("variant") ? f2.getInt("variant") : 0;
                final MaterialStorage ms = MaterialStorage.getMS(m, f2.getInt("id"), variant);
                final WeaponType weatype = f2.contains("guntype") ? WeaponType.valueOf(f2.getString("guntype"))
                        : WeaponType.valueOf(f2.getString("weapontype"));
                final ItemStack[] materails = main.convertIngredients(f2.getStringList("craftingRequirements"));

                final String displayname = f2.contains("displayname") ? LocalUtils.colorize(f2.getString("displayname"))
                        : (ChatColor.GOLD + name);
                final List<String> extraLore2 = f2.contains("lore") ? f2.getStringList("lore") : null;
                final List<String> extraLore = new ArrayList<String>();

                try {
                    for (final String lore : extraLore2) {
                        extraLore.add(LocalUtils.colorize(lore));
                    }
                } catch (Error | Exception re52) {
                }
                if (weatype.isGun()) {
                    final Gun g = new Gun(name, ms);
                    g.setDisplayname(displayname);
                    g.setCustomLore(extraLore);
                    g.setIngredients(materails);
                    QAMain.gunRegister.put(ms, g);
                    GunYMLLoader.loadGunSettings(g, f2);
                }

            }
        }

    }

    @SuppressWarnings("unchecked")
    private static void loadGunSettings(final Gun g, final FileConfiguration f2) {

        if (f2.contains("ammotype"))
            g.setAmmo(AmmoType.getAmmo(f2.getString("ammotype")));

        if (f2.contains("sway.defaultValue")) {
            g.setSway(f2.getDouble("sway.defaultValue"));
        }
        if (f2.contains("sway.defaultMultiplier"))
            g.setSwayMultiplier(f2.getDouble("sway.defaultMultiplier"));
        if (f2.contains("enableIronSights"))
            g.setHasIronsights(f2.getBoolean("enableIronSights"));
        if (f2.contains("maxbullets"))
            g.setMaxBullets(f2.getInt("maxbullets"));
        if (f2.contains("damage"))
            g.setDurabilityDamage(f2.getInt("damage"));
        if (f2.contains("durability"))
            g.setDuribility(f2.getInt("durability"));
        if (f2.contains("price"))
            g.setPrice(f2.getDouble("price"));
        if (f2.contains("allowInShop"))
            g.setEnableShop(f2.getBoolean("allowInShop"));
        if (f2.contains("allowCrafting"))
            g.setEnableCrafting(f2.getBoolean("allowCrafting"));
        if (f2.contains("isAutomatic"))
            g.setAutomatic(f2.getBoolean("isAutomatic"));
        if (f2.contains("enableBetterModelScopes"))
            g.enableBetterAimingAnimations(f2.getBoolean("enableBetterModelScopes"));

        if (f2.contains("sway.sneakModifier"))
            g.setEnableSwaySneakModifier(f2.getBoolean("sway.sneakModifier"));
        if (f2.contains("sway.moveModifier"))
            g.setEnableSwayMovementModifier(f2.getBoolean("sway.moveModifier"));
        if (f2.contains("sway.runModifier"))
            g.setEnableSwayRunModifier(f2.getBoolean("sway.runModifier"));
        if (f2.contains("DestructableMaterials")) {
            g.getBreakableMaterials().clear();
            g.getBreakableMaterials().addAll(GunYMLLoader.getMaterials(f2.getStringList("DestructableMaterials")));
        }

        List<String> sounds = null;

        if (f2.contains("weaponsounds")) {
            final Object ss = f2.get("weaponsounds");
            if (ss instanceof String) {
                sounds = new ArrayList<>();
                sounds.add(f2.getString("weaponsounds"));
            } else if (ss instanceof List) {
                sounds = (List<String>) ss;
            }
        } else {
            sounds = new ArrayList<>();
            String sound = WeaponSounds.GUN_MEDIUM.getSoundName();
            if (g.getWeaponType() == WeaponType.PISTOL || g.getWeaponType() == WeaponType.SMG)
                sound = WeaponSounds.GUN_SMALL.getSoundName();
            if (g.getWeaponType() == WeaponType.SHOTGUN || g.getWeaponType() == WeaponType.SNIPER)
                sound = WeaponSounds.GUN_BIG.getSoundName();
            if (g.getWeaponType() == WeaponType.RPG)
                sound = WeaponSounds.WARHEAD_LAUNCH.getSoundName();
            if (g.getWeaponType() == WeaponType.LAZER)
                sound = WeaponSounds.LAZERSHOOT.getSoundName();
            sounds = new ArrayList<>();
            sounds.add(sound);
        }
        g.setSounds(sounds);

        if (f2.contains("weaponsounds_volume"))
            g.setVolume(f2.getDouble("weaponsounds_volume"));

        final double partr = f2.getDouble("particles.bullet_particleR", 1.0D);
        final double partg = f2.getDouble("particles.bullet_particleG", 1.0D);
        final double partb = f2.getDouble("particles.bullet_particleB", 1.0D);
        final int partdata = f2.getInt("particles.bullet_particleData", 0);
        Material partm = Material.matchMaterial(f2.getString("particles.bullet_particleMaterial", "COAL_BLOCK"));

        if (partm == null) {
            partm = Material.COAL_BLOCK;
        }

        if (f2.contains("addMuzzleSmoke")) {
            final boolean addMuzzleSmoke = f2.contains("addMuzzleSmoke") ? f2.getBoolean("addMuzzleSmoke") : false;
            g.setUseMuzzleSmoke(addMuzzleSmoke);
        }
        if (f2.contains("delayForReload"))
            g.setReloadingTimeInSeconds(f2.getDouble("delayForReload"));

        if (f2.contains("drop-glow-color") && !f2.getString("drop-glow-color").equals("none")) {
            ChatColor c = ChatColor.WHITE;
            for (final ChatColor cc : ChatColor.values())
                if (cc.name().equals(f2.getString("drop-glow-color"))) {
                    c = cc;
                    break;
                }
            g.setGlow(c);
        }

        if (f2.contains("CustomProjectiles.projectileType")) {
            g.setCustomProjectile(f2.getString("CustomProjectiles.projectileType"));
            if (f2.contains("CustomProjectiles.explosionRadius"))
                g.setExplosionRadius(f2.getDouble("CustomProjectiles.explosionRadius"));
            if (f2.contains("CustomProjectiles.Velocity"))
                g.setRealtimeVelocity(f2.getDouble("CustomProjectiles.Velocity"));
        }

        if (f2.contains("recoil"))
            g.setRecoil(f2.getDouble("recoil"));
        if (f2.contains("headshotMultiplier"))
            g.setHeadshotMultiplier(f2.getDouble("headshotMultiplier"));
        if (f2.contains("unlimitedAmmo"))
            g.setUnlimitedAmmo(f2.getBoolean("unlimitedAmmo"));
        if (f2.contains("LightLeveOnShoot"))
            g.setLightOnShoot(f2.getInt("LightLeveOnShoot"));
        if (f2.contains("firerate"))
            g.setFireRate(f2.getInt("firerate"));
        if (f2.contains("ReloadingHandler")) {
            g.setReloadingHandler(ReloadingManager.getHandler(f2.getString("ReloadingHandler")));
            if (g.getReloadingingHandler() != null) {
                g.setReloadingSound(g.getReloadingingHandler().getDefaultReloadingSound());
            }
        }
        if (f2.contains("ChargingHandler") && !f2.getString("ChargingHandler").equals("none")) {
            g.setChargingHandler(ChargingManager.getHandler(f2.getString("ChargingHandler")));
            if (g.getChargingHandler() != null) {
                g.setChargingSound(g.getChargingHandler().getDefaultChargingSound());
            }
        }
        if (f2.contains("delayForShoot"))
            g.setDelayBetweenShots(f2.getDouble("delayForShoot"));
        if (f2.contains("bullets-per-shot"))
            g.setBulletsPerShot(f2.getInt("bullets-per-shot"));
        if (f2.contains("maxBulletDistance"))
            g.setMaxDistance(f2.getInt("maxBulletDistance"));
        if (f2.contains("Version_18_Support"))
            g.set18Supported(f2.getBoolean("Version_18_Support"));
        if (f2.contains("hasNightVisionOnScope"))
            g.setNightVision(f2.getBoolean("hasNightVisionOnScope"));
        if (f2.contains("isPrimaryWeapon"))
            g.setIsPrimary(f2.getBoolean("isPrimaryWeapon"));
        if (f2.contains("setZoomLevel"))
            g.setZoomLevel(f2.getInt("setZoomLevel"));
        if (f2.contains("firing_knockback"))
            g.setKnockbackPower(f2.getDouble("firing_knockback"));
        if (f2.contains("slownessOnEquip"))
            g.setSlownessPower(f2.getInt("slownessOnEquip"));
        if (f2.contains("weaponsounds_reloadingSound"))
            g.setReloadingSound(f2.getString("weaponsounds_reloadingSound"));
        if (f2.contains("weaponsounds_chargingSound"))
            g.setChargingSound(f2.getString("weaponsounds_chargingSound"));
        if (f2.contains("maxItemStack"))
            g.setMaxItemStack(f2.getInt("maxItemStack"));

        if (f2.contains("particles.bullet_particle") || f2.contains("particles.bullet_particleR")) {
            try {
                final Particle particle = (Particle) (f2.contains("particles.bullet_particle")
                        ? Particle.valueOf(f2.getString("particles.bullet_particle"))
                        : QAMain.bulletTrail);
                g.setParticles(particle, partr, partg, partb, partm, partdata);
            } catch (Error | Exception er5) {
            }
        }
    }

    public static void loadGuns(final QAMain main) {
        if (new File(main.getDataFolder(), "newGuns").exists()) {
            int items = 0;
            for (final File f : new File(main.getDataFolder(), "newGuns").listFiles()) {
                final FileConfiguration f2 = YamlConfiguration.loadConfiguration(f);
                if (CrackshotLoader.isCrackshotGun(f2)) {
                    main.getLogger().info("-Converting Crackshot: " + f.getName());
                    final List<Gun> guns = CrackshotLoader.loadCrackshotGuns(f2);
                    CrackshotLoader.createYMLForGuns(guns, main.getDataFolder());
                    continue;
                }
                GunYMLLoader.loadGuns(main, f);
                items++;
            }
            if (!QAMain.verboseLoadingLogging)
                main.getLogger().info("-Loaded " + items + " Gun types.");
        }
    }

    public static void loadAttachments(final QAMain main) {
        if (new File(main.getDataFolder(), "attachments").exists()) {
            int items = 0;
            for (final File f : new File(main.getDataFolder(), "attachments").listFiles()) {
                try {
                    if (f.getName().contains("yml")) {
                        final FileConfiguration f2 = YamlConfiguration.loadConfiguration(f);
                        if ((!f2.contains("invalid")) || !f2.getBoolean("invalid")) {
                            final String name = f2.getString("name");
                            main.getLogger().info("-Loading Attachment: " + name);
                            final String displayname = f2.contains("displayname") ? LocalUtils.colorize(f2.getString("displayname"))
                                    : (ChatColor.GOLD + name);
                            final List<String> extraLore2 = f2.contains("lore") ? f2.getStringList("lore") : null;

                            final Material m = f2.contains("material") ? Material.matchMaterial(f2.getString("material"))
                                    : Material.DIAMOND_AXE;
                            final int variant = f2.contains("variant") ? f2.getInt("variant") : 0;
                            final MaterialStorage ms = MaterialStorage.getMS(m, f2.getInt("id"), variant);

                            // Gun baseGun = null;
                            MaterialStorage baseGunM = null;
                            final String base = f2.getString("baseGun");
                            for (final Entry<MaterialStorage, Gun> g : QAMain.gunRegister.entrySet()) {
                                if (g.getValue().getName().equalsIgnoreCase(base)) {
                                    // baseGun = g.getValue();
                                    baseGunM = g.getKey();
                                }
                            }

                            final List<String> extraLore = new ArrayList<String>();
                            try {
                                for (final String lore : extraLore2) {
                                    extraLore.add(LocalUtils.colorize(lore));
                                }
                            } catch (Error | Exception re52) {
                            }
                            if (baseGunM == null) {
                                main.getLogger()
                                        .info("--Failed to load " + name + " attachment because the base \"" + base + "\" does not exist.");
                                continue;
                            }

                            final AttachmentBase attach = new AttachmentBase(baseGunM, ms, name, displayname);
                            QAMain.gunRegister.put(ms, attach);
                            items++;

                            attach.setCustomLore(extraLore);

                            final Object[] materials = main.convertIngredientsRaw(f2.getStringList("craftingRequirements"));
                            attach.setIngredientsRaw(materials);

                            // QAMain.attachmentRegister.put(ms, attach);
                            GunYMLLoader.loadGunSettings(attach, f2);
                        }

                    }
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
            if (!QAMain.verboseLoadingLogging)
                main.getLogger().info("-Loaded " + items + " Attachment types.");
        }
    }

    public static @NotNull List<Material> getMaterials(@NotNull final List<String> list) {
        final List<Material> materials = new ArrayList<>();

        for (final String s : list) {
            if (s.equals("MATERIAL_NAME_HERE"))
                continue;

            try {
                final Material material = Material.getMaterial(s.toUpperCase());

                if (material == null) {
                    QAMain.getInstance().getLogger().warning("Invalid material name: " + s + ".");
                    continue;
                }

                materials.add(material);
            } catch (Error | Exception ignored) {
                QAMain.getInstance().getLogger().warning("Invalid material name: " + s + ".");
            }
        }

        return materials;
    }
}
