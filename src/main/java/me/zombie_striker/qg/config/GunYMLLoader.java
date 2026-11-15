package me.zombie_striker.qg.config;

import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.ammo.Ammo;
import me.zombie_striker.qg.ammo.AmmoType;
import me.zombie_striker.qg.armor.Helmet;
import me.zombie_striker.qg.attachments.AttachmentBase;
import me.zombie_striker.qg.config.items.*;
import me.zombie_striker.qg.crafting.Crafting;
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
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.logging.Level;

/**
 * Loads the item files (guns, ammo, misc, armor and attachments) into the {@link me.zombie_striker.qg.state.ItemRegistry}.
 */
public class GunYMLLoader {
	private static final String GUNS = "newGuns";
	private static final String AMMO = "ammo";
	private static final String MISC = "misc";
	private static final String ARMOR = "armor";
	private static final String ATTACHMENTS = "attachments";

	/**
	 * Migrates every item file to the current key names. Runs before the default items are created,
	 * so their "only if unchanged" updates see the admin's values under the new names.
	 */
	public static void migrateAll(File dataFolder) {
		migrateFolder(new File(dataFolder, GUNS), GunDefinition::new, true);
		migrateFolder(new File(dataFolder, AMMO), AmmoDefinition::new, false);
		migrateFolder(new File(dataFolder, MISC), MiscDefinition::new, false);
		migrateFolder(new File(dataFolder, ARMOR), ArmorDefinition::new, false);
		migrateFolder(new File(dataFolder, ATTACHMENTS), AttachmentDefinition::new, false);
	}

	private static void migrateFolder(File folder, Function<File, ItemDefinition> factory, boolean mayContainCrackshot) {
		for (File file : itemFiles(folder)) {
			try {
				if (mayContainCrackshot && CrackshotLoader.isCrackshotGun(YamlConfiguration.loadConfiguration(file)))
					continue;
				factory.apply(file).load();
			} catch (RuntimeException e) {
				QAMain.getInstance().getLogger().log(Level.SEVERE, "Could not migrate item file " + file.getPath(), e);
			}
		}
	}

	public static void loadAmmo(QAMain main) {
		File folder = new File(main.getDataFolder(), AMMO);
		if (!folder.exists()) return;
		int items = 0;
		for (File f : itemFiles(folder)) {
			AmmoDefinition def = load(main, f, AmmoDefinition::new, "Ammo");
			if (def == null || def.invalid) continue;
			if (QAMain.getConfiguration().features.verboseItemLogging)
				main.getLogger().info("-Loading AmmoType: " + def.name);

			String skullOwner = def.skullOwner;
			String customSkin = def.skullOwnerCustomUrl != null && !def.skullOwnerCustomUrl.equals(Ammo.NO_SKIN_STRING)
					? def.skullOwnerCustomUrl : null;

			MaterialStorage ms = MaterialStorage.getMS(material(def), def.id, def.variant, skullOwner, customSkin);
			ItemStack[] ingredients = Crafting.parseItemIngredients(def.craftingRequirements);
			String displayname = LocalUtils.colorize(def.displayNameOr(ChatColor.WHITE.toString()));
			List<String> lore = colorizedLore(def);
			double price = def.price != null ? def.price : 100;
			int maxStack = def.maxItemStack != null ? def.maxItemStack : 0;

			Ammo ammo = new Ammo(def.name, displayname, lore, ms, maxStack, false, 1, price, ingredients, def.piercingSeverity);
			ammo.setEnableShop(def.allowInShop && price > 0);
			ammo.setCustomLore(lore);
			QAMain.getItemRegistry().getAmmo().put(ms, ammo);
			items++;

			if (skullOwner != null)
				ammo.setSkullOwner(skullOwner);
			if (customSkin != null)
				ammo.setCustomSkin(customSkin);
			if (def.craftingReturnAmount != null)
				ammo.setCraftingReturn(def.craftingReturnAmount);
		}
		logLoaded(main, items, "Ammo types.");
	}

	public static void loadArmor(QAMain main) {
		File folder = new File(main.getDataFolder(), ARMOR);
		if (!folder.exists()) return;
		int items = 0;
		for (File f : itemFiles(folder)) {
			ArmorDefinition def = load(main, f, ArmorDefinition::new, "Armor");
			if (def == null || def.invalid) continue;
			if (QAMain.getConfiguration().features.verboseItemLogging)
				main.getLogger().info("-Loading Armor: " + def.name);

			MaterialStorage ms = MaterialStorage.getMS(material(def), def.id, def.variant);
			ItemStack[] ingredients = Crafting.parseItemIngredients(def.craftingRequirements);
			String displayname = LocalUtils.colorize(def.displayNameOr(ChatColor.WHITE.toString()));
			int price = def.price != null ? def.price.intValue() : 100;

			if (WeaponType.getByName(def.miscType) == WeaponType.HELMET) {
				Helmet helmet = new Helmet(def.name, displayname, colorizedLore(def), ingredients, ms, price, def.allowInShop && price > 0);
				helmet.setHeightMax(def.maxProtectionHeight);
				helmet.setHeightMin(def.minProtectionHeight);
				helmet.setProtection(def.protection);
				helmet.setNegateHeadshots(def.stopsHeadshots);
				QAMain.getItemRegistry().getArmor().put(ms, helmet);
				items++;
			}
		}
		logLoaded(main, items, "Armor types.");
	}

	public static void loadMisc(QAMain main) {
		File folder = new File(main.getDataFolder(), MISC);
		if (!folder.exists()) return;
		int items = 0;
		for (File f : itemFiles(folder)) {
			MiscDefinition def = load(main, f, MiscDefinition::new, "Misc");
			if (def == null || def.invalid) continue;
			if (QAMain.getConfiguration().features.verboseItemLogging)
				main.getLogger().info("-Loading Misc: " + def.name);

			MaterialStorage ms = MaterialStorage.getMS(material(def), def.id, def.variant);
			ItemStack[] ingredients = Crafting.parseItemIngredients(def.craftingRequirements);
			String name = def.name;
			String displayname = LocalUtils.colorize(def.displayNameOr(ChatColor.WHITE.toString()));
			List<String> lore = colorizedLore(def);
			int price = def.price != null ? def.price.intValue() : 100;
			int damage = def.damage;
			double radius = def.radius;
			items++;

			CustomBaseObject base = createMisc(WeaponType.getByName(def.miscType), def, ms, name, displayname, lore, ingredients, price, damage, radius);
			if (base == null) continue;
			QAMain.getItemRegistry().getMisc().put(ms, base);

			base.setCustomLore(lore);
			base.setIngredients(ingredients);
			base.setEnableShop(def.allowInShop && price > 0);
			if (def.maxItemStack != null)
				base.setMaxItemStack(def.maxItemStack);
			if (base instanceof ThrowableItems && def.throwSpeed != null)
				((ThrowableItems) base).setThrowSpeed(def.throwSpeed);
			if (base instanceof Grenade && def.fuseTime != null)
				((Grenade) base).setFuseTicks((int) Math.round(def.fuseTime * 20));
			if (base instanceof SmokeGrenades && def.useModernParticles != null)
				((SmokeGrenades) base).setUseModernParticles(def.useModernParticles);
		}
		logLoaded(main, items, "Misc.");
	}

	private static CustomBaseObject createMisc(WeaponType type, MiscDefinition def, MaterialStorage ms, String name, String displayname,
											   List<String> lore, ItemStack[] ingredients, int price, int damage, double radius) {
		if (type == null) return null;
		switch (type) {
			case MEDKIT:
				return new MedKit(ms, name, displayname, ingredients, price);
			case AMMO_BAG:
				return new AmmoBag(ms, name, displayname, ingredients, def.max, price);
			case MELEE:
				MeleeItems melee = new MeleeItems(ms, name, displayname, ingredients, price, damage);
				melee.setSoundOnEquip(def.soundEquip);
				melee.setSoundOnEquipVolume((float) def.soundEquipVolume);
				melee.setSoundOnHit(def.soundMeleeHit);
				melee.setSoundOnHitVolume((float) def.soundMeleeHitVolume);
				return melee;
			case GRENADES:
				return new Grenade(ingredients, price, damage, radius, name, displayname, lore, ms);
			case SMOKE_GRENADES:
				return new SmokeGrenades(ingredients, price, damage, radius, name, displayname, lore, ms);
			case INCENDARY_GRENADES:
				return new IncendaryGrenades(ingredients, price, damage, radius, name, displayname, lore, ms);
			case PROXYMINES:
				return new ProxyMines(ingredients, price, damage, radius, name, displayname, lore, ms);
			case STICKYGRENADE:
				return new StickyGrenades(ingredients, price, damage, radius, name, displayname, lore, ms);
			case MOLOTOV:
				return new Molotov(ingredients, price, damage, radius, name, displayname, lore, ms);
			case FLASHBANGS:
				return new Flashbang(ingredients, price, damage, radius, name, displayname, lore, ms);
			default:
				return null;
		}
	}

	public static void loadGuns(QAMain main) {
		File folder = new File(main.getDataFolder(), GUNS);
		if (!folder.exists()) return;
		int items = 0;
		File[] files = folder.listFiles();
		if (files == null) return;
		for (File f : files) {
			YamlConfiguration raw = YamlConfiguration.loadConfiguration(f);
			if (CrackshotLoader.isCrackshotGun(raw)) {
				main.getLogger().info("-Converting Crackshot: " + f.getName());
				List<Gun> guns = CrackshotLoader.loadCrackshotGuns(raw);
				CrackshotLoader.createYMLForGuns(guns, main.getDataFolder());
				continue;
			}
			loadGuns(main, f);
			items++;
		}
		logLoaded(main, items, "Gun types.");
	}

	public static void loadGuns(QAMain main, File f) {
		if (!f.getName().contains("yml")) return;
		GunDefinition def = load(main, f, GunDefinition::new, "Gun");
		if (def == null || def.invalid) return;
		if (QAMain.getConfiguration().features.verboseItemLogging)
			main.getLogger().info("-Loading Gun: " + def.name);

		WeaponType type = parseWeaponType(main, def);
		if (type == null || !type.isGun()) return;

		MaterialStorage ms = MaterialStorage.getMS(material(def), def.id, def.variant);
		Gun g = new Gun(def.name, ms);
		g.setDisplayname(LocalUtils.colorize(def.displayNameOr(ChatColor.GOLD.toString())));
		g.setCustomLore(colorizedLore(def));
		g.setIngredients(Crafting.parseItemIngredients(def.craftingRequirements));
		QAMain.getItemRegistry().getGuns().put(ms, g);
		applyGunSettings(g, def);
	}

	public static void loadAttachments(QAMain main) {
		File folder = new File(main.getDataFolder(), ATTACHMENTS);
		if (!folder.exists()) return;
		int items = 0;
		for (File f : itemFiles(folder)) {
			AttachmentDefinition def = load(main, f, AttachmentDefinition::new, "Attachment");
			if (def == null || def.invalid) continue;
			if (QAMain.getConfiguration().features.verboseItemLogging)
				main.getLogger().info("-Loading Attachment: " + def.name);

			MaterialStorage ms = MaterialStorage.getMS(material(def), def.id, def.variant);

			MaterialStorage baseGun = null;
			for (Gun gun : QAMain.getItemRegistry().getGuns().values()) {
				if (gun.getName().equalsIgnoreCase(def.baseGun))
					baseGun = gun.getItemData();
			}
			if (baseGun == null) {
				main.getLogger().info("--Failed to load " + def.name + " attachment because the base \"" + def.baseGun + "\" does not exist.");
				continue;
			}

			AttachmentBase attach = new AttachmentBase(baseGun, ms, def.name, LocalUtils.colorize(def.displayNameOr(ChatColor.GOLD.toString())));
			QAMain.getItemRegistry().getGuns().put(ms, attach);
			items++;

			attach.setCustomLore(colorizedLore(def));
			attach.setIngredientsRaw(Crafting.parseIngredients(def.craftingRequirements));
			applyGunSettings(attach, def);
		}
		logLoaded(main, items, "Attachment types.");
	}

	@SuppressWarnings("unchecked")
	private static void applyGunSettings(Gun g, GunDefinition def) {
		if (def.ammoType != null)
			g.setAmmo(AmmoType.getAmmo(def.ammoType));
		if (def.sway.defaultValue != null)
			g.setSway(def.sway.defaultValue);
		if (def.sway.defaultMultiplier != null)
			g.setSwayMultiplier(def.sway.defaultMultiplier);
		if (def.enableIronSights != null)
			g.setHasIronsights(def.enableIronSights);
		if (def.maxBullets != null)
			g.setMaxBullets(def.maxBullets);
		if (def.damage != null)
			g.setDurabilityDamage(def.damage.floatValue());
		if (def.durability != null)
			g.setDuribility(def.durability);
		if (def.price != null)
			g.setPrice(def.price);
		if (def.contains("allow-in-shop"))
			g.setEnableShop(def.allowInShop);
		if (def.allowCrafting != null)
			g.setEnableCrafting(def.allowCrafting);
		if (def.automatic != null)
			g.setAutomatic(def.automatic);
		if (def.enableBetterModelScopes != null)
			g.enableBetterAimingAnimations(def.enableBetterModelScopes);

		if (def.sway.sneakModifier != null)
			g.setEnableSwaySneakModifier(def.sway.sneakModifier);
		if (def.sway.moveModifier != null)
			g.setEnableSwayMovementModifier(def.sway.moveModifier);
		if (def.sway.runModifier != null)
			g.setEnableSwayRunModifier(def.sway.runModifier);
		if (def.destructableMaterials != null) {
			g.getBreakableMaterials().clear();
			g.getBreakableMaterials().addAll(getMaterials(def.destructableMaterials));
		}

		g.setSounds(weaponSounds(g, def));
		if (def.weaponSoundsVolume != null)
			g.setVolume(def.weaponSoundsVolume);

		if (def.addMuzzleSmoke != null)
			g.setUseMuzzleSmoke(def.addMuzzleSmoke);
		if (def.delayForReload != null)
			g.setReloadingTimeInSeconds(def.delayForReload);

		if (def.dropGlowColor != null && !def.dropGlowColor.equals("none")) {
			ChatColor color = ChatColor.WHITE;
			for (ChatColor c : ChatColor.values())
				if (c.name().equals(def.dropGlowColor)) {
					color = c;
					break;
				}
			g.setGlow(color);
		}

		if (def.customProjectiles.projectileType != null) {
			g.setCustomProjectile(def.customProjectiles.projectileType);
			if (def.customProjectiles.explosionRadius != null)
				g.setExplosionRadius(def.customProjectiles.explosionRadius);
			if (def.customProjectiles.velocity != null)
				g.setRealtimeVelocity(def.customProjectiles.velocity);
			if (def.customProjectiles.explosionKnockback != null)
				g.setExplosionKnockback(def.customProjectiles.explosionKnockback);
		}

		if (def.recoil != null)
			g.setRecoil(def.recoil);
		if (def.headshotMultiplier != null)
			g.setHeadshotMultiplier(def.headshotMultiplier);
		if (def.unlimitedAmmo != null)
			g.setUnlimitedAmmo(def.unlimitedAmmo);
		if (def.lightLevelOnShoot != null)
			g.setLightOnShoot(def.lightLevelOnShoot);
		if (def.fireRate != null)
			g.setFireRate(def.fireRate);
		if (def.reloadingHandler != null) {
			g.setReloadingHandler(ReloadingManager.getHandler(def.reloadingHandler));
			if (g.getReloadingingHandler() != null)
				g.setReloadingSound(g.getReloadingingHandler().getDefaultReloadingSound());
		}
		if (def.chargingHandler != null && !def.chargingHandler.equals("none")) {
			g.setChargingHandler(ChargingManager.getHandler(def.chargingHandler));
			if (g.getChargingHandler() != null)
				g.setChargingSound(g.getChargingHandler().getDefaultChargingSound());
		}
		if (def.delayForShoot != null)
			g.setDelayBetweenShots(def.delayForShoot);
		if (def.bulletsPerShot != null)
			g.setBulletsPerShot(def.bulletsPerShot);
		if (def.maxBulletDistance != null)
			g.setMaxDistance(def.maxBulletDistance);
		if (def.version18Support != null)
			g.set18Supported(def.version18Support);
		if (def.nightVisionOnScope != null)
			g.setNightVision(def.nightVisionOnScope);
		if (def.primaryWeapon != null)
			g.setIsPrimary(def.primaryWeapon);
		if (def.zoomLevel != null)
			g.setZoomLevel(def.zoomLevel);
		if (def.firingKnockback != null)
			g.setKnockbackPower(def.firingKnockback);
		if (def.slownessOnEquip != null)
			g.setSlownessPower(def.slownessOnEquip);
		if (def.reloadingSound != null)
			g.setReloadingSound(def.reloadingSound);
		if (def.chargingSound != null)
			g.setChargingSound(def.chargingSound);
		if (def.maxItemStack != null)
			g.setMaxItemStack(def.maxItemStack);

		GunDefinition.Particles particles = def.particles;
		if (particles.bulletParticle != null || particles.bulletParticleR != null) {
			Material particleMaterial = Material.matchMaterial(particles.bulletParticleMaterial);
			if (particleMaterial == null)
				particleMaterial = Material.COAL_BLOCK;
			Particle particle = null;
			if (particles.bulletParticle != null) {
				try {
					particle = Particle.valueOf(particles.bulletParticle);
				} catch (IllegalArgumentException e) {
					// Same as before: an unknown particle disables the gun's particles
					QAMain.getInstance().getLogger().warning("Unknown particle " + particles.bulletParticle + " in " + def.getFile().getName());
					return;
				}
			}
			g.setParticles(particle, particles.bulletParticleR != null ? particles.bulletParticleR : 1.0,
					particles.bulletParticleG, particles.bulletParticleB, particleMaterial, particles.bulletParticleData);
		}
	}

	@SuppressWarnings("unchecked")
	private static List<String> weaponSounds(Gun g, GunDefinition def) {
		if (def.contains("weapon-sounds")) {
			if (def.weaponSounds instanceof String)
				return new ArrayList<>(Collections.singletonList((String) def.weaponSounds));
			if (def.weaponSounds instanceof List)
				return (List<String>) def.weaponSounds;
			return null;
		}
		String sound = WeaponSounds.GUN_MEDIUM.getSoundName();
		if (g.getWeaponType() == WeaponType.PISTOL || g.getWeaponType() == WeaponType.SMG)
			sound = WeaponSounds.GUN_SMALL.getSoundName();
		if (g.getWeaponType() == WeaponType.SHOTGUN || g.getWeaponType() == WeaponType.SNIPER)
			sound = WeaponSounds.GUN_BIG.getSoundName();
		if (g.getWeaponType() == WeaponType.RPG)
			sound = WeaponSounds.WARHEAD_LAUNCH.getSoundName();
		if (g.getWeaponType() == WeaponType.LAZER)
			sound = WeaponSounds.LAZERSHOOT.getSoundName();
		return new ArrayList<>(Collections.singletonList(sound));
	}

	private static WeaponType parseWeaponType(QAMain main, GunDefinition def) {
		if (def.weaponType == null) {
			main.getLogger().warning("Gun file " + def.getFile().getName() + " has no weapon-type");
			return null;
		}
		try {
			return WeaponType.valueOf(def.weaponType);
		} catch (IllegalArgumentException e) {
			main.getLogger().warning("Unknown weapon-type " + def.weaponType + " in " + def.getFile().getName());
			return null;
		}
	}

	/**
	 * @return The item material: DIAMOND_AXE when the file has none, as before.
	 */
	private static Material material(ItemDefinition def) {
		if (def.material == null)
			return Material.DIAMOND_AXE;
		Material material = Material.matchMaterial(def.material);
		if (material == null)
			QAMain.getInstance().getLogger().warning("Unknown material " + def.material + " in " + def.getFile().getName());
		return material;
	}

	private static List<String> colorizedLore(ItemDefinition def) {
		List<String> lore = new ArrayList<>();
		if (def.lore != null)
			for (String line : def.lore)
				lore.add(LocalUtils.colorize(line));
		return lore;
	}

	/**
	 * Loads and migrates an item file, logging errors with the file they come from.
	 */
	private static <T extends ItemDefinition> T load(QAMain main, File file, Function<File, T> factory, String kind) {
		try {
			T def = factory.apply(file);
			def.load();
			return def;
		} catch (RuntimeException e) {
			main.getLogger().log(Level.SEVERE, "Could not load " + kind.toLowerCase(Locale.ROOT) + " file " + file.getPath(), e);
			return null;
		}
	}

	private static List<File> itemFiles(File folder) {
		List<File> files = new ArrayList<>();
		File[] list = folder.listFiles();
		if (list == null) return files;
		for (File f : list)
			if (f.getName().contains("yml"))
				files.add(f);
		return files;
	}

	private static void logLoaded(QAMain main, int items, String what) {
		if (!QAMain.getConfiguration().features.verboseItemLogging)
			main.getLogger().info("-Loaded " + items + " " + what);
	}

	public static @NotNull List<Material> getMaterials(@NotNull List<String> list) {
		List<Material> materials = new ArrayList<>();
		for (String s : list) {
			if (s.equals("MATERIAL_NAME_HERE")) continue;
			Material material = Material.getMaterial(s.toUpperCase(Locale.ROOT));
			if (material == null) {
				QAMain.getInstance().getLogger().warning("Invalid material name: " + s + ".");
				continue;
			}
			materials.add(material);
		}
		return materials;
	}
}
