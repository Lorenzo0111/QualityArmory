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
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class GunYMLLoader {

	public static void loadAmmo(QAMain main) {
		loadFolder(main, "ammo", "Ammo", (file, config) -> {
			String name = config.getString("name");
			if (QAMain.verboseLoadingLogging) {
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
			return true;
		});
	}

	public static void loadArmor(QAMain main) {
		loadFolder(main, "armor", "Armor", (file, config) -> {
			WeaponType wt = WeaponType.getByName(config.getString("MiscType"));
			if (wt != WeaponType.HELMET) return false;

			String name = config.getString("name");
			if (QAMain.verboseLoadingLogging) {
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
			return true;
		});
	}

	public static void loadMisc(QAMain main) {
		loadFolder(main, "misc", "Misc", (file, config) -> {
			String name = config.getString("name");
			if (QAMain.verboseLoadingLogging) {
				main.getLogger().info("-Loading Misc: " + name);
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
					base = new SmokeGrenade(ingredients, price, damage, radius, name, displayname, lore, ms);
					break;
				case INCENDARY_GRENADES:
					base = new IncendiaryGrenade(ingredients, price, damage, radius, name, displayname, lore, ms);
					break;
				case PROXYMINES:
					base = new ProxyMine(ingredients, price, damage, radius, name, displayname, lore, ms);
					break;
				case STICKYGRENADE:
					base = new StickyGrenade(ingredients, price, damage, radius, name, displayname, lore, ms);
					break;
				case MOLOTOV:
					base = new Molotov(ingredients, price, damage, radius, name, displayname, lore, ms);
					break;
				case FLASHBANGS:
					base = new Flashbang(ingredients, price, damage, radius, name, displayname, lore, ms);
					break;
			}

			if (base == null) return false;

			base.setCustomLore(lore);
			base.setIngredients(ingredients);
			base.setEnableShop(allowInShop);

			if (config.contains("maxItemStack"))
				base.setMaxItemStack(config.getInt("maxItemStack"));

			if (base instanceof ThrowableItems) {
				ThrowableItems throwableItems = (ThrowableItems) base;
				if (config.contains("ThrowSpeed"))
					throwableItems.setThrowSpeed(config.getDouble("ThrowSpeed"));
			}

			QAMain.miscRegister.put(ms, base);
			return true;
		});
	}

	public static void loadGuns(QAMain main) {
		loadFolder(main, "newGuns", "Gun", (file, config) -> {
			if (CrackshotLoader.isCrackshotGun(config)) {
				main.getLogger().info("-Converting Crackshot: " + file.getName());
				List<Gun> guns = CrackshotLoader.loadCrackshotGuns(config);
				CrackshotLoader.createYMLForGuns(guns, main.getDataFolder());
				return false;
			}

			return loadGun(main, file);
		});
	}

	public static boolean loadGun(QAMain main, File file) {
		if (!file.getName().endsWith(".yml")) return false;

		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		if (config.getBoolean("invalid", false)) return false;

		String typeName = config.contains("guntype")
				? config.getString("guntype")
				: config.getString("weapontype");

		if (!WeaponType.getByName(typeName).isGun()) return false;

		String name = config.getString("name");
		if (QAMain.verboseLoadingLogging) {
			main.getLogger().info("-Loading Gun: " + name);
		}

		String displayname = config.contains("displayname")
				? LocalUtils.colorize(config.getString("displayname"))
				: (ChatColor.GOLD + name);

		List<String> lore = config.getStringList("lore").stream()
				.map(LocalUtils::colorize)
				.collect(Collectors.toList());

		int id = config.getInt("id");

		int variant = config.getInt("variant", 0);

		ItemStack[] ingredients = main.convertIngredients(config.getStringList("craftingRequirements"));

		Material material = Material.matchMaterial(config.getString("material", "DIAMOND_AXE"));

		MaterialStorage ms = MaterialStorage.getMS(material, id, variant);

		Gun gun = new Gun(name, ms);
		gun.setDisplayname(displayname);
		gun.setCustomLore(lore);
		gun.setIngredients(ingredients);

		QAMain.gunRegister.put(ms, gun);
		loadGunSettings(gun, config);

		return true;
	}

	private static void loadGunSettings(Gun gun, FileConfiguration cfg) {

		if (cfg.contains("ammotype")) gun.setAmmo(AmmoType.getAmmo(cfg.getString("ammotype")));
		if (cfg.contains("sway.defaultValue")) gun.setSway(cfg.getDouble("sway.defaultValue"));
		if (cfg.contains("sway.defaultMultiplier"))
			gun.setSwayMultiplier(cfg.getDouble("sway.defaultMultiplier"));
		if (cfg.contains("enableIronSights")) gun.setHasIronSights(cfg.getBoolean("enableIronSights"));
		if (cfg.contains("maxbullets")) gun.setMaxBullets(cfg.getInt("maxbullets"));
		if (cfg.contains("damage")) gun.setDurabilityDamage(cfg.getInt("damage"));
		if (cfg.contains("durability")) gun.setDurability(cfg.getInt("durability"));
		if (cfg.contains("price")) gun.setPrice(cfg.getDouble("price"));
		if (cfg.contains("allowInShop")) gun.setEnableShop(cfg.getBoolean("allowInShop"));
		if (cfg.contains("allowCrafting")) gun.setEnableCrafting(cfg.getBoolean("allowCrafting"));
		if (cfg.contains("isAutomatic")) gun.setAutomatic(cfg.getBoolean("isAutomatic"));
		if (cfg.contains("enableBetterModelScopes"))
			gun.enableBetterAimingAnimations(cfg.getBoolean("enableBetterModelScopes"));

		if (cfg.contains("sway.sneakModifier"))
			gun.setEnableSwaySneakModifier(cfg.getBoolean("sway.sneakModifier"));
		if (cfg.contains("sway.moveModifier"))
			gun.setEnableSwayMovementModifier(cfg.getBoolean("sway.moveModifier"));
		if (cfg.contains("sway.runModifier"))
			gun.setEnableSwayRunModifier(cfg.getBoolean("sway.runModifier"));

		if (cfg.contains("DestructableMaterials")) {
			gun.getBreakableMaterials().clear();
			gun.getBreakableMaterials().addAll(getMaterials(cfg.getStringList("DestructableMaterials")));
		}

		List<String> sounds = new ArrayList<>();
		if (cfg.contains("weaponsounds")) {
			Object raw = cfg.get("weaponsounds");

			if (raw instanceof String)
				sounds.add(cfg.getString("weaponsounds"));
			else if (raw instanceof List<?>) {
				List<?> list = (List<?>) raw;
				for (Object item : list)
					if (item instanceof String)
						sounds.add((String) item);
			}
		}

		if (sounds.isEmpty()) sounds.add(WeaponSounds.getSoundByType(gun.getWeaponType()));
		gun.setSounds(sounds);

		if (cfg.contains("weaponsounds_volume")) gun.setVolume(cfg.getDouble("weaponsounds_volume"));
		if (cfg.contains("addMuzzleSmoke")) gun.setUseMuzzleSmoke(cfg.getBoolean("addMuzzleSmoke"));
		if (cfg.contains("delayForReload")) gun.setReloadingTimeInSeconds(cfg.getDouble("delayForReload"));

		String glowColorName = cfg.getString("drop-glow-color");
		if (glowColorName != null && !glowColorName.equalsIgnoreCase("none")) {
			ChatColor chosen = ChatColor.WHITE;
			for (ChatColor cc : ChatColor.values())
				if (cc.name().equals(glowColorName)) {
					chosen = cc;
					break;
				}
			gun.setGlow(chosen);
		}

		if (cfg.contains("CustomProjectiles.projectileType")) {
			gun.setCustomProjectile(cfg.getString("CustomProjectiles.projectileType"));
			if (cfg.contains("CustomProjectiles.explosionRadius"))
				gun.setExplosionRadius(cfg.getDouble("CustomProjectiles.explosionRadius"));
			if (cfg.contains("CustomProjectiles.Velocity"))
				gun.setRealtimeVelocity(cfg.getDouble("CustomProjectiles.Velocity"));
		}

		if (cfg.contains("recoil")) gun.setRecoil(cfg.getDouble("recoil"));
		if (cfg.contains("headshotMultiplier"))
			gun.setHeadshotMultiplier(cfg.getDouble("headshotMultiplier"));
		if (cfg.contains("unlimitedAmmo")) gun.setUnlimitedAmmo(cfg.getBoolean("unlimitedAmmo"));
		if (cfg.contains("LightLeveOnShoot")) gun.setLightOnShoot(cfg.getInt("LightLeveOnShoot"));
		if (cfg.contains("firerate")) gun.setFireRate(cfg.getInt("firerate"));

		if (cfg.contains("ReloadingHandler")) {
			gun.setReloadingHandler(ReloadingManager.getHandler(cfg.getString("ReloadingHandler")));

			if (gun.getReloadingHandler() != null)
				gun.setReloadingSound(gun.getReloadingHandler().getDefaultReloadingSound());
		}

		String chargingHandler = cfg.getString("ChargingHandler");
		if (chargingHandler != null && !chargingHandler.equalsIgnoreCase("none")) {
			gun.setChargingHandler(ChargingManager.getHandler(chargingHandler));

			if (gun.getChargingHandler() != null)
				gun.setChargingSound(gun.getChargingHandler().getDefaultChargingSound());
		}

		if (cfg.contains("delayForShoot")) gun.setDelayBetweenShots(cfg.getDouble("delayForShoot"));
		if (cfg.contains("bullets-per-shot")) gun.setBulletsPerShot(cfg.getInt("bullets-per-shot"));
		if (cfg.contains("maxBulletDistance")) gun.setMaxDistance(cfg.getInt("maxBulletDistance"));
		if (cfg.contains("Version_18_Support"))
			gun.set18Supported(cfg.getBoolean("Version_18_Support"));
		if (cfg.contains("hasNightVisionOnScope")) gun.setNightVision(cfg.getBoolean("hasNightVisionOnScope"));
		if (cfg.contains("isPrimaryWeapon")) gun.setIsPrimary(cfg.getBoolean("isPrimaryWeapon"));
		if (cfg.contains("setZoomLevel")) gun.setZoomLevel(cfg.getInt("setZoomLevel"));
		if (cfg.contains("firing_knockback")) gun.setKnockbackPower(cfg.getDouble("firing_knockback"));
		if (cfg.contains("slownessOnEquip")) gun.setSlownessPower(cfg.getInt("slownessOnEquip"));
		if (cfg.contains("weaponsounds_reloadingSound"))
			gun.setReloadingSound(cfg.getString("weaponsounds_reloadingSound"));
		if (cfg.contains("weaponsounds_chargingSound"))
			gun.setChargingSound(cfg.getString("weaponsounds_chargingSound"));
		if (cfg.contains("maxItemStack")) gun.setMaxItemStack(cfg.getInt("maxItemStack"));

		if (cfg.contains("particles.bullet_particle") || cfg.contains("particles.bullet_particleR")) {

			double partr = cfg.getDouble("particles.bullet_particleR", 1.0);
			double partg = cfg.getDouble("particles.bullet_particleG", 1.0);
			double partb = cfg.getDouble("particles.bullet_particleB", 1.0);
			int partdata = cfg.getInt("particles.bullet_particleData", 0);

			Material partm = Material.matchMaterial(
					cfg.getString("particles.bullet_particleMaterial", "COAL_BLOCK"));
			if (partm == null) partm = Material.COAL_BLOCK;

			try {
				Particle particle = (Particle) (cfg.contains("particles.bullet_particle")
						? Particle.valueOf(cfg.getString("particles.bullet_particle"))
						: QAMain.bulletTrail);
				gun.setParticles(particle, partr, partg, partb, partm, partdata);
			} catch (Error | Exception ignored) {}
		}
	}

	public static void loadAttachments(QAMain main) {
		loadFolder(main, "attachments", "Attachment", (file, config) -> {
			String name = config.getString("name");
			if (QAMain.verboseLoadingLogging) {
				main.getLogger().info("-Loading Attachment: " + name);
			}

			String displayname = config.contains("displayname")
					? LocalUtils.colorize(config.getString("displayname"))
					: (ChatColor.GOLD + name);

			List<String> lore = config.getStringList("lore").stream()
					.map(LocalUtils::colorize)
					.collect(Collectors.toList());

			int id = config.getInt("id");

			int variant = config.getInt("variant", 0);

			Object[] rawIngredients = main.convertIngredientsRaw(config.getStringList("craftingRequirements"));

			Material material = Material.matchMaterial(config.getString("material", "DIAMOND_AXE"));

			MaterialStorage ms = MaterialStorage.getMS(material, id, variant);

			MaterialStorage baseGunM = null;
			String base = config.getString("baseGun");
			if (base != null) {
				for (Entry<MaterialStorage, Gun> entry : QAMain.gunRegister.entrySet())
					if (entry.getValue().getName().equalsIgnoreCase(base))
						baseGunM = entry.getKey();
			}

			if (baseGunM == null) {
				main.getLogger().info("--Failed to load " + name
						+ " attachment because the base \"" + base + "\" does not exist.");
				return false;
			}

			AttachmentBase attach = new AttachmentBase(baseGunM, ms, name, displayname);

			attach.setCustomLore(lore);
			attach.setIngredientsRaw(rawIngredients);

			loadGunSettings(attach, config);

			QAMain.gunRegister.put(ms, attach);
			return true;
		});
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

	private static void loadFolder(QAMain main, String folderName, String logLabel,
								  BiFunction<File, FileConfiguration, Boolean> configProcessor) {
		File folder = new File(main.getDataFolder(), folderName);
		if (!folder.exists()) return;

		File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
		if (files == null) return;

		int items = 0;

		for (File file : files) {
			try {
				FileConfiguration config = YamlConfiguration.loadConfiguration(file);
				if (config.getBoolean("invalid", false)) continue;

				if (configProcessor.apply(file, config)) items++;

			} catch (Exception e) {
				e.printStackTrace(System.err);
			}
		}

		if (!QAMain.verboseLoadingLogging) {
			main.getLogger().info("-Loaded " + items + " " + logLabel + " types.");
		}
	}
}