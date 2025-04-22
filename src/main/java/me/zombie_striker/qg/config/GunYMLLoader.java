package me.zombie_striker.qg.config;

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
import me.zombie_striker.qg.miscitems.*;
import me.zombie_striker.qg.utils.LocalUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class GunYMLLoader {

	public static void loadAmmo(QAMain main) {
		File ammoFolder = new File(main.getDataFolder(), "ammo");
		if (!ammoFolder.exists()) return;

		File[] files = ammoFolder.listFiles();
		if (files == null || files.length == 0) return;

		int items = 0;

		for (File file : files) {
			if (!file.getName().endsWith(".yml")) continue;

			try {
				FileConfiguration config = YamlConfiguration.loadConfiguration(file);
				if (config.getBoolean("invalid", false)) continue;

				String name = config.getString("name");
				if(QAMain.verboseLoadingLogging) {
					main.getLogger().info("-Loading AmmoType: " + name);
				}

				String displayname = config.contains("displayname")
						? LocalUtils.colorize(config.getString("displayname"))
						: (ChatColor.WHITE + name);

				List<String> coloredLore = config.getStringList("lore").stream()
						.map(LocalUtils::colorize)
						.collect(Collectors.toList());

				int id = config.getInt("id");

				int variant = config.getInt("variant", 0);

				ItemStack[] ingredients = main.convertIngredients(config.getStringList("craftingRequirements"));

				int returnAmount = config.getInt("craftingReturnAmount", 1);

				double price = config.getDouble("price", 100.0);

				boolean allowInShop = config.getBoolean("allowInShop", true) && price > 0;

				int maxAmount = config.contains("maxItemStack")
						? config.getInt("maxItemStack")
						: config.getInt("maxAmount");

				Material material = Material.matchMaterial(config.getString("material", "DIAMOND_AXE"));

				String skullOwner = config.getString("skull_owner", null);
				String skullUrl = config.getString("skull_owner_custom_url", null);
				if (Ammo.NO_SKIN_STRING.equals(skullUrl)) skullUrl = null;

				double piercing = config.getDouble("piercingSeverity", 1.0);

				MaterialStorage ms = MaterialStorage.getMS(material, id, variant, skullOwner, skullUrl);

				Ammo ammo = new Ammo(name, displayname, coloredLore, ms, maxAmount,
						false, 1, price, ingredients, piercing
				);

				ammo.setEnableShop(allowInShop);
				if (skullOwner != null) ammo.setSkullOwner(skullOwner);
				if (skullUrl != null) ammo.setCustomSkin(skullUrl);
				if (returnAmount > 0) ammo.setCraftingReturn(returnAmount);

				QAMain.ammoRegister.put(ms, ammo);
				items++;

			} catch (Exception e) {
				e.printStackTrace(System.err);
			}
		}

		if(!QAMain.verboseLoadingLogging) {
			main.getLogger().info("-Loaded " + items + " Ammo types.");
		}
	}

	public static void loadArmor(QAMain main) {
		File armorFolder = new File(main.getDataFolder(), "armor");
		if (!armorFolder.exists()) return;

		File[] files = armorFolder.listFiles();
		if (files == null || files.length == 0) return;

		int items = 0;

		for (File file : files) {
			if (!file.getName().endsWith(".yml")) continue;

			try {
				FileConfiguration config = YamlConfiguration.loadConfiguration(file);
				if (config.getBoolean("invalid", false)) continue;

				WeaponType wt = WeaponType.getByName(config.getString("MiscType"));
				if (wt != WeaponType.HELMET) continue;

				String name = config.getString("name");
				if(QAMain.verboseLoadingLogging) {
					main.getLogger().info("-Loading Armor: " + name);
				}

				String displayname = config.contains("displayname")
						? LocalUtils.colorize(config.getString("displayname"))
						: (ChatColor.WHITE + name);

				List<String> lore = config.getStringList("lore").stream()
						.map(LocalUtils::colorize)
						.collect(Collectors.toList());

				int id = config.getInt("id");

				int variant = config.getInt("variant", 0);

				ItemStack[] ingredients = main.convertIngredients(config.getStringList("craftingRequirements"));

				double price = config.getDouble("price", 100.0);
				boolean allowInShop = config.getBoolean("allowInShop", true) && price > 0;

				double minProtectionHeight = config.getDouble("minProtectionHeight");
				double maxProtectionHeight = config.getDouble("maxProtectionHeight");
				int protection = config.getInt("protection", 0);

				Material material = Material.matchMaterial(config.getString("material", "DIAMOND_AXE"));

				MaterialStorage ms = MaterialStorage.getMS(material, id, variant);

				Helmet helmet = new Helmet(name, displayname, lore, ingredients, ms, price, allowInShop);

				helmet.setHeightMin(minProtectionHeight);
				helmet.setHeightMax(maxProtectionHeight);
				helmet.setProtection(protection);

                QAMain.armorRegister.put(ms, helmet);
                items++;

            } catch (Exception e) {
				e.printStackTrace(System.err);
			}
		}

		if(!QAMain.verboseLoadingLogging) {
			main.getLogger().info("-Loaded " + items + " Armor types.");
		}
	}

	public static void loadMisc(QAMain main) {
		File miscFolder = new File(main.getDataFolder(), "misc");
		if (!miscFolder.exists()) return;

		File[] files = miscFolder.listFiles();
		if (files == null || files.length == 0) return;

		int items = 0;

		for (File file : files) {
			if (!file.getName().endsWith(".yml")) continue;

			try {
				FileConfiguration config = YamlConfiguration.loadConfiguration(file);
				if (config.getBoolean("invalid", false)) continue;

				String name = config.getString("name");
				if (QAMain.verboseLoadingLogging) {
					main.getLogger().info("-Loading Misc: " + name);
				}

				String displayname = config.contains("displayname")
						? LocalUtils.colorize( config.getString("displayname"))
						: (ChatColor.WHITE + name);

				List<String> lore = config.getStringList("lore").stream()
						.map(LocalUtils::colorize)
						.collect(Collectors.toList());

				int id = config.getInt("id");

				int variant = config.getInt("variant", 0);

				ItemStack[] ingredients = main.convertIngredients(config.getStringList("craftingRequirements"));

				int price = config.contains("price") ? config.getInt("price") : 100;
				boolean allowInShop = config.getBoolean("allowInShop", true) && price > 0;

				Material material = Material.matchMaterial(config.getString("material", "DIAMOND_AXE"));

				MaterialStorage ms = MaterialStorage.getMS(material, id, variant);

				int damage = config.getInt("damage", 1);

				WeaponType wt = WeaponType.getByName(config.getString("MiscType"));

				double radius = config.getDouble("radius", 0.0);

				String soundEquip = config.getString("sound_equip", null);
				String soundHit = config.getString("sound_meleehit", null);

				CustomBaseObject base = null;
				switch (wt) {
					case MEDKIT:
						base = new MedKit(ms, name, displayname, ingredients, price);
						break;
					case AMMO_BAG:
						base = new AmmoBag(ms, name, displayname, ingredients, config.getInt("max", 5), price);
						break;
					case MELEE:
						base = new MeleeItems(ms, name, displayname, ingredients, price, damage);
						base.setSoundOnEquip(soundEquip);
						base.setSoundOnHit(soundHit);
						break;
					case GRENADES:
						base = new Grenade(ingredients, price, damage, radius, name, displayname, lore, ms);
						break;
					case SMOKE_GRENADES:
						base = new SmokeGrenades(ingredients, price, damage, radius, name, displayname, lore, ms);
						break;
					case INCENDARY_GRENADES:
						base = new IncendaryGrenades(ingredients, price, damage, radius, name, displayname, lore, ms);
						break;
					case PROXYMINES:
						base = new ProxyMines(ingredients, price, damage, radius, name, displayname, lore, ms);
						break;
					case STICKYGRENADE:
						base = new StickyGrenades(ingredients, price, damage, radius, name, displayname, lore, ms);
						break;
					case MOLOTOV:
						base = new Molotov(ingredients, price, damage, radius, name, displayname, lore, ms);
						break;
					case FLASHBANGS:
						base = new Flashbang(ingredients, price, damage, radius, name, displayname, lore, ms);
						break;
				}

				if (base == null) continue;

				base.setCustomLore(lore);
				base.setIngredients(ingredients);
				base.setEnableShop(allowInShop);

				if(config.contains("maxItemStack"))
					base.setMaxItemStack(config.getInt("maxItemStack"));

				if (base instanceof ThrowableItems) {
					ThrowableItems throwableItems = (ThrowableItems) base;
					if (config.contains("ThrowSpeed"))
						throwableItems.setThrowSpeed(config.getDouble("ThrowSpeed"));
				}

				QAMain.miscRegister.put(ms, base);
				items++;

			} catch (Exception e) {
				e.printStackTrace(System.err);
			}
		}

		if(!QAMain.verboseLoadingLogging) {
			main.getLogger().info("-Loaded " + items + " Misc.");
		}
	}

	public static void loadGuns(QAMain main) {
		File gunsFolder = new File(main.getDataFolder(), "newGuns");
		if (!gunsFolder.exists()) return;

		File[] files = gunsFolder.listFiles();
		if (files == null || files.length == 0) return;

		int items = 0;

		for (File file : files) {
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);

			if (CrackshotLoader.isCrackshotGun(config)) {
				main.getLogger().info("-Converting Crackshot: " + file.getName());
				List<Gun> guns = CrackshotLoader.loadCrackshotGuns(config);
				CrackshotLoader.createYMLForGuns(guns, main.getDataFolder());
				continue;
			}

			if (loadGun(main, file)) items++;
		}

		if(!QAMain.verboseLoadingLogging) {
			main.getLogger().info("-Loaded " + items + " Gun types.");
		}
	}

	public static boolean loadGun(QAMain main, File file) {
		if (!file.getName().endsWith(".yml")) return false;

		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		if (config.getBoolean("invalid", false)) return false;

		String name = config.getString("name");
		if(QAMain.verboseLoadingLogging) {
			main.getLogger().info("-Loading Gun: " + name);
		}

		Material material = Material.matchMaterial(config.getString("material", "DIAMOND_AXE"));

		int id = config.getInt("id");

		int variant = config.getInt("variant", 0);

		MaterialStorage ms = MaterialStorage.getMS(material, id, variant);

		WeaponType gunType = config.contains("guntype")
				? WeaponType.getByName(config.getString("guntype"))
				: WeaponType.getByName(config.getString("weapontype"));

		ItemStack[] ingredients = main.convertIngredients(config.getStringList("craftingRequirements"));

		String displayname = config.contains("displayname")
				? LocalUtils.colorize(config.getString("displayname"))
				: (ChatColor.GOLD + name);

		List<String> lore = config.getStringList("lore").stream()
				.map(LocalUtils::colorize)
				.collect(Collectors.toList());


		if (!gunType.isGun()) return false;

		Gun gun = new Gun(name, ms);
		gun.setDisplayname(displayname);
		gun.setCustomLore(lore);
		gun.setIngredients(ingredients);

		QAMain.gunRegister.put(ms, gun);
		loadGunSettings(gun, config);

		return true;
	}

	@SuppressWarnings("unchecked")
	private static void loadGunSettings(Gun g, FileConfiguration f2) {

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
			g.getBreakableMaterials().addAll(getMaterials(f2.getStringList("DestructableMaterials")));
		}

		List<String> sounds = null;

		if (f2.contains("weaponsounds")) {
			Object ss = f2.get("weaponsounds");
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

		double partr = f2.getDouble("particles.bullet_particleR", 1.0D);
		double partg = f2.getDouble("particles.bullet_particleG", 1.0D);
		double partb = f2.getDouble("particles.bullet_particleB", 1.0D);
		int partdata = f2.getInt("particles.bullet_particleData", 0);
		Material partm = Material.matchMaterial(f2.getString("particles.bullet_particleMaterial", "COAL_BLOCK"));

		if (partm == null) {
			partm = Material.COAL_BLOCK;
		}

		if (f2.contains("addMuzzleSmoke")) {
			boolean addMuzzleSmoke = f2.contains("addMuzzleSmoke") ? f2.getBoolean("addMuzzleSmoke") : false;
			g.setUseMuzzleSmoke(addMuzzleSmoke);
		}
		if (f2.contains("delayForReload"))
			g.setReloadingTimeInSeconds(f2.getDouble("delayForReload"));

		if (f2.contains("drop-glow-color") && !f2.getString("drop-glow-color").equals("none")) {
			ChatColor c = ChatColor.WHITE;
			for (ChatColor cc : ChatColor.values())
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
			if(g.getReloadingingHandler()!=null){
				g.setReloadingSound(g.getReloadingingHandler().getDefaultReloadingSound());
			}
		}
		if (f2.contains("ChargingHandler") && !f2.getString("ChargingHandler").equals("none")) {
			g.setChargingHandler(ChargingManager.getHandler(f2.getString("ChargingHandler")));
			if(g.getChargingHandler()!=null){
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
		if(f2.contains("firing_knockback"))
			g.setKnockbackPower(f2.getDouble("firing_knockback"));
		if(f2.contains("slownessOnEquip"))
			g.setSlownessPower(f2.getInt("slownessOnEquip"));
		if(f2.contains("weaponsounds_reloadingSound"))
			g.setReloadingSound(f2.getString("weaponsounds_reloadingSound"));
		if(f2.contains("weaponsounds_chargingSound"))
			g.setChargingSound(f2.getString("weaponsounds_chargingSound"));
		if(f2.contains("maxItemStack"))
			g.setMaxItemStack(f2.getInt("maxItemStack"));


		if (f2.contains("particles.bullet_particle") || f2.contains("particles.bullet_particleR")) {
			try {
				Particle particle = (Particle) (f2.contains("particles.bullet_particle")
						? Particle.valueOf(f2.getString("particles.bullet_particle"))
						: QAMain.bulletTrail);
				g.setParticles(particle, partr, partg, partb, partm, partdata);
			} catch (Error | Exception er5) {
			}
		}
	}

	public static void loadAttachments(QAMain main) {
		if (new File(main.getDataFolder(), "attachments").exists()) {
			int items = 0;
			for (File f : new File(main.getDataFolder(), "attachments").listFiles()) {
				try {
					if (f.getName().contains("yml")) {
						FileConfiguration f2 = YamlConfiguration.loadConfiguration(f);
						if ((!f2.contains("invalid")) || !f2.getBoolean("invalid")) {
							final String name = f2.getString("name");
							main.getLogger().info("-Loading Attachment: " + name);
							final String displayname = f2.contains("displayname")
									? LocalUtils.colorize( f2.getString("displayname"))
									: (ChatColor.GOLD + name);
							final List<String> extraLore2 = f2.contains("lore") ? f2.getStringList("lore") : null;

							Material m = f2.contains("material") ? Material.matchMaterial(f2.getString("material"))
									: Material.DIAMOND_AXE;
							int variant = f2.contains("variant") ? f2.getInt("variant") : 0;
							final MaterialStorage ms = MaterialStorage.getMS(m, f2.getInt("id"), variant);

							// Gun baseGun = null;
							MaterialStorage baseGunM = null;
							String base = f2.getString("baseGun");
							for (Entry<MaterialStorage, Gun> g : QAMain.gunRegister.entrySet()) {
								if (g.getValue().getName().equalsIgnoreCase(base)) {
									// baseGun = g.getValue();
									baseGunM = g.getKey();
								}
							}

							final List<String> extraLore = new ArrayList<String>();
							try {
								for (String lore : extraLore2) {
									extraLore.add(LocalUtils.colorize( lore));
								}
							} catch (Error | Exception re52) {
							}
							if(baseGunM==null){
								main.getLogger().info("--Failed to load "+name+" attachment because the base \""+base+"\" does not exist.");
								continue;
							}

							AttachmentBase attach = new AttachmentBase(baseGunM, ms, name, displayname);
							QAMain.gunRegister.put(ms, attach);
							items++;

							attach.setCustomLore(extraLore);

							final Object[] materials = main
									.convertIngredientsRaw(f2.getStringList("craftingRequirements"));
							attach.setIngredientsRaw(materials);

							// QAMain.attachmentRegister.put(ms, attach);
							loadGunSettings(attach, f2);
						}

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(!QAMain.verboseLoadingLogging)
				main.getLogger().info("-Loaded "+items+" Attachment types.");
		}
	}

	public static @NotNull List<Material> getMaterials(@NotNull List<String> list) {
		List<Material> materials = new ArrayList<>();

		for (String s : list) {
			if (s.equals("MATERIAL_NAME_HERE")) continue;

			try {
				Material material = Material.getMaterial(s.toUpperCase());

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
