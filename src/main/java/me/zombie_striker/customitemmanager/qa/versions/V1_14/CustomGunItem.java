package me.zombie_striker.customitemmanager.qa.versions.V1_14;

import com.cryptomorin.xseries.profiles.builder.XSkull;
import com.cryptomorin.xseries.profiles.objects.ProfileInputType;
import com.cryptomorin.xseries.profiles.objects.Profileable;
import me.zombie_striker.customitemmanager.*;
import me.zombie_striker.customitemmanager.pack.MultiVersionPackProvider;
import me.zombie_striker.customitemmanager.qa.AbstractCustomGunItem;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.ammo.Ammo;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.armor.ArmorObject;
import me.zombie_striker.qg.config.GunYMLCreator;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.guns.projectiles.ProjectileManager;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.guns.utils.WeaponType;
import me.zombie_striker.qg.handlers.IronsightsHandler;
import me.zombie_striker.qg.handlers.MultiVersionLookup;
import me.zombie_striker.qg.guns.chargers.ChargingManager;
import me.zombie_striker.qg.guns.reloaders.ReloadingManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CustomGunItem extends AbstractCustomGunItem {

	private boolean overrideAttackSpeed = true;

	public CustomGunItem(){
		Map<String, String> versions = new HashMap<>();
		versions.put("0", "https://github.com/ZombieStriker/QualityArmory-Resourcepack/releases/download/latest/QualityArmory.zip");
		versions.put("21-4", "https://github.com/ZombieStriker/QualityArmory-Resourcepack/releases/download/latest/QualityArmory-21.zip");

		CustomItemManager.setResourcepack(new MultiVersionPackProvider(versions));
	}

	public static MaterialStorage m(int d) {
		return MaterialStorage.getMS(Material.CROSSBOW, d, 0);
	}

	@Override
	public ItemStack getItem(Material material, int data, int variant) {
		return getItem(MaterialStorage.getMS(material,data,variant));
	}

	@Override
	public ItemStack getItem(MaterialStorage ms) {
		CustomBaseObject base = QualityArmory.getCustomItem(ms);
		if(base==null)
			return null;
		String displayname = base.getDisplayName();
		if (ms == null || ms.getMat() == null)
			return new ItemStack(Material.AIR);

		ItemStack is = new ItemStack(ms.getMat());
		if (ms.getData() < 0)
			is.setDurability((short) 0);


		ItemMeta im = is.getItemMeta();
		if (im == null)
			im = Bukkit.getServer().getItemFactory().getItemMeta(ms.getMat());
		if (im != null) {
			im.setDisplayName(displayname);
			List<String> lore = base.getCustomLore()!=null?new ArrayList<>(base.getCustomLore()):new ArrayList<>();

			if (base instanceof Ammo) {
				boolean setSkull = false;

				if (((Ammo) base).hasCustomSkin()) {
					setSkull = true;
					is = XSkull.createItem()
							.profile(Profileable.of(ProfileInputType.BASE64, ((Ammo) base).getCustomSkin()))
							.apply();
				}

				if (((Ammo) base).isSkull() && !setSkull) {
					is = XSkull.createItem()
							.profile(Profileable.of(ProfileInputType.USERNAME, ((Ammo) base).getSkullOwner()))
							.apply();
				}
			}


			if(base instanceof Gun)
				lore.addAll(Gun.getGunLore((Gun) base, null, ((Gun) base).getMaxBullets()));
			if (base instanceof ArmorObject)
				lore.addAll(OLD_ItemFact.getArmorLore((ArmorObject) base));

			im.setLore(lore);


			if (overrideAttackSpeed) {
				AttributeModifier modifier = new AttributeModifier(base.getUuid(), "generic.attackSpeed", 0, AttributeModifier.Operation.ADD_NUMBER);
				im.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, modifier);
			}

			if (QAMain.ITEM_enableUnbreakable) {
				try {
					im.setUnbreakable(true);
				} catch (Error | Exception e34) {
				}
			}
			try {
				if (QAMain.ITEM_enableUnbreakable) {
					im.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_UNBREAKABLE);
				}
				im.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES);
				im.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_DESTROYS);
			} catch (Error e) {

			}
			if (ms.getData() >= 0)
				im.setCustomModelData(ms.getData());

			is.setItemMeta(im);
		} else {
			QAMain.getInstance().getLogger()
					.warning(QAMain.prefix + " ItemMeta is null for " + base.getName() + ". I have");
		}
		is.setAmount(1);
		return is;
	}

	@Override
	public boolean isCustomItem(ItemStack is) {
		return QualityArmory.isCustomItem(is);
	}

	@Override
	public void initIronsights(File dataFolder) {
		File ironsights = new File(dataFolder, "default_ironsightstoggleitem.yml");
		YamlConfiguration ironconfig = YamlConfiguration.loadConfiguration(ironsights);
		if (!ironconfig.contains("displayname")) {
			ironconfig.set("material", Material.CROSSBOW.name());
			ironconfig.set("id", 68);
			ironconfig.set("displayname", IronsightsHandler.ironsightsDisplay);
			try {
				ironconfig.save(ironsights);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		IronsightsHandler.ironsightsMaterial = Material.matchMaterial(ironconfig.getString("material"));
		IronsightsHandler.ironsightsData = ironconfig.getInt("id");
		IronsightsHandler.ironsightsDisplay = ironconfig.getString("displayname");

	}

	@Override
	public void initItems(File dataFolder) {


		List<String> stringsWoodRif = Arrays.asList(new String[]{getIngString(Material.IRON_INGOT, 0, 12),
				getIngString(MultiVersionLookup.getWood(), 0, 2), getIngString(Material.REDSTONE, 0, 5)});
		List<String> stringsGoldRif = Arrays.asList(new String[]{getIngString(Material.IRON_INGOT, 0, 12),
				getIngString(Material.GOLD_INGOT, 0, 2), getIngString(Material.REDSTONE, 0, 5)});
		List<String> stringsMetalRif = Arrays.asList(
				new String[]{getIngString(Material.IRON_INGOT, 0, 15), getIngString(Material.REDSTONE, 0, 5)});
		List<String> stringsPistol = Arrays.asList(
				new String[]{getIngString(Material.IRON_INGOT, 0, 5), getIngString(Material.REDSTONE, 0, 2)});
		List<String> stringsRPG = Arrays.asList(
				new String[]{getIngString(Material.IRON_INGOT, 0, 32), getIngString(Material.REDSTONE, 0, 10)});

		List<String> stringsHelmet = Arrays.asList(
				new String[]{getIngString(Material.IRON_INGOT, 0, 5), getIngString(Material.OBSIDIAN, 0, 1)});

		List<String> stringsGrenades = Arrays.asList(new String[]{getIngString(Material.IRON_INGOT, 0, 6),
				getIngString(MultiVersionLookup.getGunpowder(), 0, 10)});
		List<String> stringsAmmoBag = Arrays.asList(new String[]{getIngString(Material.STRING, 0, 2),
				getIngString(Material.LEATHER, 0, 6)});

		List<String> stringsAmmo = Arrays.asList(new String[]{getIngString(Material.IRON_INGOT, 0, 1),
				getIngString(MultiVersionLookup.getGunpowder(), 0, 1), getIngString(Material.REDSTONE, 0, 1)});
		List<String> stringsAmmoMusket = Arrays.asList(new String[]{getIngString(Material.IRON_INGOT, 0, 4),
				getIngString(MultiVersionLookup.getGunpowder(), 0, 3),});
		List<String> stringsAmmoRPG = Arrays.asList(new String[]{getIngString(Material.IRON_INGOT, 0, 4),
				getIngString(MultiVersionLookup.getGunpowder(), 0, 6), getIngString(Material.REDSTONE, 0, 1)});

		List<String> StringsWool = Arrays.asList(new String[]{getIngString(MultiVersionLookup.getWool(), 0, 8)});

		List<String> stringsHealer = Arrays.asList(new String[]{getIngString(MultiVersionLookup.getWool(), 0, 6),
				getIngString(Material.GOLDEN_APPLE, 0, 1)});
		List<String> stringsMini = Arrays.asList(
				new String[]{getIngString(Material.IRON_INGOT, 0, 10), getIngString(Material.TNT, 0, 16)});

		List<String> strings10mm = Arrays.asList(new String[]{getIngString(Material.IRON_INGOT, 0, 10),
				getIngString(Material.REDSTONE, 0, 4)});


		GunYMLCreator.createAmmo(false, dataFolder, false, "9mm", "&f9mm", Material.PHANTOM_MEMBRANE, 1, stringsAmmo, 2, 0.7, 50,
				10).done();
		GunYMLCreator.createAmmo(false, dataFolder, false, "556", "&f5.56 NATO", Material.PHANTOM_MEMBRANE, 2, stringsAmmo, 4, 1, 50,
				5).done();
		GunYMLCreator.createAmmo(false, dataFolder, false, "762", "&f7.62x39mm", Material.PHANTOM_MEMBRANE, 3, stringsAmmo, 5, 1.2,
				50, 5).done();
		GunYMLCreator.createAmmo(false, dataFolder, false, "shell", "&fBuckshot", Material.PHANTOM_MEMBRANE, 4, stringsAmmo, 10, 0.5,
				8, 4).done();
		GunYMLCreator.createAmmo(false, dataFolder, false, "rocket", "&fRocket", Material.PHANTOM_MEMBRANE, 5, stringsAmmoRPG, 100,
				1000, 1).done();
		GunYMLCreator.createAmmo(false, dataFolder, false, "musketball", "&fMusket Ball", Material.PHANTOM_MEMBRANE, 6,
				stringsAmmoMusket, 1, 0.7, 32, 8).done();
		GunYMLCreator.createAmmo(false, dataFolder, false, "50bmg", "&f.50BMG", Material.PHANTOM_MEMBRANE, 7, stringsAmmo, 10, 3, 30,
				1).done();
		GunYMLCreator.createAmmo(false, dataFolder, false, "40mm", "&f40x46mm", Material.PHANTOM_MEMBRANE, 8, stringsAmmo, 30, 10,
				10, 1).done();
		GunYMLCreator
				.createAmmo(false, dataFolder, false, "default_flamerfuel", "fuel", "&fFlamerFuel", null,
						Material.BLAZE_POWDER, 0,
						Arrays.asList(new String[]{getIngString(Material.BLAZE_ROD, 0, 1),}), 1, 1, 64, 2)
				.done();

		//BACKPACK GREEN
		QualityArmory.registerNewUsedExpansionItem(MaterialStorage.getMS(Material.PHANTOM_MEMBRANE,11,0));
		QualityArmory.registerNewUsedExpansionItem(MaterialStorage.getMS(Material.PHANTOM_MEMBRANE,12,0));
		QualityArmory.registerNewUsedExpansionItem(MaterialStorage.getMS(Material.PHANTOM_MEMBRANE,13,0));
		QualityArmory.registerNewUsedExpansionItem(MaterialStorage.getMS(Material.PHANTOM_MEMBRANE,14,0));
		QualityArmory.registerNewUsedExpansionItem(MaterialStorage.getMS(Material.PHANTOM_MEMBRANE,15,0));
		QualityArmory.registerNewUsedExpansionItem(MaterialStorage.getMS(Material.PHANTOM_MEMBRANE,16,0));
		QualityArmory.registerNewUsedExpansionItem(MaterialStorage.getMS(Material.PHANTOM_MEMBRANE,17,0));


		GunYMLCreator.createAmmo(true, dataFolder, false, "mininuke", "MiniNuke", Material.PHANTOM_MEMBRANE, 9,
				stringsMini, 3000, 100, 1).done();

		GunYMLCreator.createAmmo(true, dataFolder, false, "fusion_cell",
				"Fusion Cell", Material.PHANTOM_MEMBRANE, 10, strings10mm, 60, 0.2, 30).done();


		GunYMLCreator.createNewDefaultGun(dataFolder, "p30", "P30", 1, stringsPistol, WeaponType.PISTOL,
				null, true, "9mm", 3, 12, 700).setReloadingHandler(ReloadingManager.SLIDE_RELOAD).setIsSecondaryWeapon(true).setMaterial(Material.CROSSBOW).done();
		GunYMLCreator
				.createNewDefaultGun(dataFolder, "pkp", "PKP", 2, stringsMetalRif, WeaponType.RIFLE,
						WeaponSounds.GUN_BIG, true, "762", 3, 100, 12000)
				.setFullyAutomatic(3).setBulletsPerShot(1).setRecoil(2).setMaterial(Material.CROSSBOW).done();
		GunYMLCreator
				.createNewDefaultGun(dataFolder, "mp5k", "MP5K", 3, stringsMetalRif, WeaponType.SMG,
						WeaponSounds.GUN_SMALL_AUTO, false, "9mm", 2, 32, 2500)
				.setFullyAutomatic(3).setBulletsPerShot(1).setMaterial(Material.CROSSBOW).done();
		GunYMLCreator
				.createNewDefaultGun(dataFolder, "ak47", "AK47", 4, stringsMetalRif, WeaponType.RIFLE,
						null, true, "762", 3, 40, 5000)
				.setSway(0.19).setFullyAutomatic(2).setBulletsPerShot(1).setRecoil(2).setMaterial(Material.CROSSBOW)
				.setKilledByMessage("%player% was shot by %killer% using an %name%").setReloadingSound(WeaponSounds.RELOAD_AK47).setWeaponSound(WeaponSounds.GUN_AK47).done();
		GunYMLCreator
				.createNewDefaultGun(dataFolder, "ak47u", "AK47-U", 5, stringsMetalRif, WeaponType.RIFLE,
						null, true, "762", 3, 30, 5000)
				.setFullyAutomatic(2).setBulletsPerShot(1).setRecoil(2).setMaterial(Material.CROSSBOW).setReloadingSound(WeaponSounds.RELOAD_AK47).setWeaponSound(WeaponSounds.GUN_AK47).done();
		GunYMLCreator.createNewDefaultGun(dataFolder, "m16", "M16", 6, stringsMetalRif, WeaponType.RIFLE,
				null, true, "556", 3, 30, 3600).setFullyAutomatic(2).setBulletsPerShot(1).setMaterial(Material.CROSSBOW)
				.setKilledByMessage("%player% was shot by %killer% using an %name%").setRecoil(2).setReloadingSound(WeaponSounds.RELOAD_M16).done();
		GunYMLCreator
				.createNewDefaultGun(dataFolder, "remington", "Remington", 7, stringsMetalRif,
						WeaponType.SHOTGUN, null, false, "shell", 3, 8, 1000)
				.setChargingHandler(ChargingManager.PUMPACTION).setDelayReload(0.7)
				.setReloadingHandler(ReloadingManager.PUMP_ACTION_RELOAD).setWeaponSound(WeaponSounds.GUN_SHOTGUN).setBulletsPerShot(20).setDistance(70)
				.setRecoil(10).setMaterial(Material.CROSSBOW).done();
		GunYMLCreator
				.createNewDefaultGun(dataFolder, "fnfal", "FN Fal", 8, stringsMetalRif, WeaponType.RIFLE,
						null, false, "762", 3, 32, 3800)
				.setFullyAutomatic(2).setBulletsPerShot(1).setRecoil(2).setMaterial(Material.CROSSBOW).setReloadingSound(WeaponSounds.RELOAD_FN).done();
		GunYMLCreator
				.createNewDefaultGun(dataFolder, "rpg", "RPG", 9, stringsRPG, WeaponType.RPG, null, false,
						"rocket", 100, 1, 4000)
				.setDelayShoot(1).setCustomProjectile(ProjectileManager.RPG)
				.setCustomProjectileExplosionRadius(10).setCustomProjectileVelocity(2)// .setChargingHandler(ChargingManager.RPG)
				.setReloadingHandler(ReloadingManager.SINGLE_RELOAD).setDistance(500).setMaterial(Material.CROSSBOW).setParticle("SMOKE_LARGE")
				.setRecoil(15).setKilledByMessage("%player% was blown up by %killer% using a %name%").done();
		GunYMLCreator
				.createNewDefaultGun(dataFolder, "ump", "UMP", 10, stringsMetalRif, WeaponType.SMG,
						WeaponSounds.GUN_SMALL_AUTO, false, "9mm", 2, 32, 1700)
				.setFullyAutomatic(2).setBulletsPerShot(1).setMaterial(Material.CROSSBOW).done();
		GunYMLCreator.createNewDefaultGun(dataFolder, "sw1911", "SW-1911", 11, stringsPistol,
				WeaponType.PISTOL, null, true, "9mm", 3, 12, 700).setKilledByMessage("%player% was shot by %killer% using an %name%")
				.setReloadingHandler(ReloadingManager.SLIDE_RELOAD).setMaterial(Material.CROSSBOW).setIsSecondaryWeapon(true).done();
		GunYMLCreator
				.createNewDefaultGun(dataFolder, "m40", "M40", 12, stringsWoodRif, WeaponType.SNIPER, null,
						true, "762", 10, 6, 2700)
				.setZoomLevel(9).setDelayShoot(0.7).setChargingHandler(ChargingManager.BOLT).setMaterial(Material.CROSSBOW).setUseOffhand(true)
				.setSwayMultiplier(3).setDistance(280).setRecoil(5).setReloadingSound(WeaponSounds.RELOAD_FN).done();
		GunYMLCreator
				.createNewDefaultGun(dataFolder, "enfield", "Enfield", 13, stringsPistol,
						WeaponType.PISTOL, null, true, "9mm", 3, 6, 200)
				.setIsSecondaryWeapon(true).setChargingHandler(ChargingManager.REVOLVER).setMaterial(Material.CROSSBOW)
				.setReloadingHandler(ReloadingManager.SINGLE_RELOAD).setKilledByMessage("%player% was shot by %killer% using an %name%")
				.done();
		GunYMLCreator
				.createNewDefaultGun(dataFolder, "mauser", "Mauser C96", 14, stringsPistol,
						WeaponType.PISTOL, null, true, "9mm", 3, 12, 700).setReloadingHandler(ReloadingManager.SLIDE_RELOAD).setMaterial(Material.CROSSBOW)
				.setSwayMultiplier(3).setIsSecondaryWeapon(true).setWeaponSound(WeaponSounds.GUN_MAUSER).done();

		GunYMLCreator.createMisc(false, dataFolder, false, "default_grenade", "grenade", "&7Grenade",
				Arrays.asList(ChatColor.DARK_GRAY + "[LMB] to pull pin", ChatColor.DARK_GRAY + "[RMB] to throw",
						ChatColor.DARK_GRAY + "Grenades wait " + ChatColor.GRAY + "FIVE seconds"
								+ ChatColor.DARK_GRAY + " before exploding.",
						ChatColor.DARK_RED + "<!>Will Explode Even If Not Thrown<!>"),
				m(15), stringsGrenades, 100, WeaponType.GRENADES, 100, 1).set(false, "radius", 10).setMaterial(Material.CROSSBOW).done();
		GunYMLCreator.createMisc(false, dataFolder, false, "default_ammobag", "ammobag", "&7Ammo Bag",
				Arrays.asList(ChatColor.DARK_GRAY + "[Left-Click] to unload", ChatColor.DARK_GRAY + "[Right-Click] to load"), m(85), stringsAmmoBag, 100, WeaponType.AMMO_BAG, 0, 1000).set(false, "max", 6).done();
		GunYMLCreator
				.createNewDefaultGun(dataFolder, "dragunov", "Dragunov", 16, stringsMetalRif,
						WeaponType.SNIPER, null, true, "762", 7, 12, 2100).setMaterial(Material.CROSSBOW)
				.setUseOffhand(false).setDelayShoot(0.4).setZoomLevel(9).setSwayMultiplier(3).setRecoil(5).setReloadingSound(WeaponSounds.RELOAD_FN).setKilledByMessage("%player% was sniped by %killer% using an %name%").done();
		GunYMLCreator
				.createNewDefaultGun(dataFolder, "spas12", "Spas-12", 17, stringsMetalRif,
						WeaponType.SHOTGUN, null, false, "shell", 2, 8, 1000).setMaterial(Material.CROSSBOW)
				.setBulletsPerShot(20).setDistance(80).setRecoil(10).setReloadingHandler(ReloadingManager.PUMP_ACTION_RELOAD).setWeaponSound(WeaponSounds.GUN_SHOTGUN).done();
		GunYMLCreator
				.createNewDefaultGun(dataFolder, "aa12", "AA-12", 18, stringsMetalRif, WeaponType.SHOTGUN,
						null, false, "shell", 2, 32, 4000).setMaterial(Material.CROSSBOW).setWeaponSound(WeaponSounds.GUN_SHOTGUN)
				.setBulletsPerShot(10).setDistance(80).setFullyAutomatic(2).setRecoil(7).setKilledByMessage("%player% was shot to bits by %killer% using an %name%").done();

		/**
		 * 27 - 36 taken for custom weapons
		 */
		//	GunYMLCreator.createMisc(false,dataFolder, false, "default_Medkit_camo", "medkitcamo", "&5Medkit",
		//			null, m(37), stringsHealer, 300, WeaponType.MEDKIT, 1, 1000).setMaterial(Material.CROSSBOW).done();
//TODO: The medical bag is unneeded. The systems broken, so we can delete it.
		GunYMLCreator
				.createNewDefaultGun(dataFolder, "magnum", "Magnum", 19, stringsPistol, WeaponType.PISTOL,
						WeaponSounds.GUN_BIG, true, "9mm", 6, 6, 700)
				.setChargingHandler(ChargingManager.REVOLVER).setReloadingHandler(ReloadingManager.SINGLE_RELOAD).setMaterial(Material.CROSSBOW)
				.setIsSecondaryWeapon(true).setRecoil(10).setKilledByMessage("%player% was shot by %killer% using a %name%").done();
		GunYMLCreator
				.createNewDefaultGun(dataFolder, "awp", "AWP", 20, stringsMetalRif, WeaponType.SNIPER,
						WeaponSounds.GUN_BIG, true, "762", 10, 12, 3000)
				.setUseOffhand(false).setDelayShoot(0.8).setZoomLevel(9).setSway(1).setSwayMultiplier(10).setRecoil(5)
				.setKilledByMessage("%player% was sniped by %killer% using an %name%").setMaterial(Material.CROSSBOW).setReloadingSound(WeaponSounds.RELOAD_FN).setSwayUnscopedModifier(3).setWeaponSound(WeaponSounds.GUN_BIG).done();

		GunYMLCreator.createMisc(false, dataFolder, false, "default_smokegrenade", "smokegrenade",
				"&7Smoke Grenade",
				Arrays.asList(ChatColor.DARK_GRAY + "[LMB] to pull pin", ChatColor.DARK_GRAY + "[RMB] to throw",
						ChatColor.DARK_GRAY + "Smoke Grenades wait " + ChatColor.GRAY + "FIVE seconds"
								+ ChatColor.DARK_GRAY + " before exploding.",
						ChatColor.DARK_RED + "<!>Will Explode Even If Not Thrown<!>"),
				m(21), stringsGrenades, 100, WeaponType.SMOKE_GRENADES, 100, 1).set(false, "radius", 5).done();
		GunYMLCreator.createMisc(false, dataFolder, false, "default_flashbang", "flashbang", "&7FlashBang",
				Arrays.asList(ChatColor.DARK_GRAY + "[LMB] to pull pin", ChatColor.DARK_GRAY + "[RMB] to throw",
						ChatColor.DARK_GRAY + "Flashbangs wait " + ChatColor.GRAY + "FIVE seconds"
								+ ChatColor.DARK_GRAY + " before exploding.",
						ChatColor.DARK_RED + "<!>Will Explode Even If Not Thrown<!>"),
				m(22), stringsGrenades, 100, WeaponType.FLASHBANGS, 100, 1).set(false, "radius", 5).done();

		GunYMLCreator
				.createAttachment(false, dataFolder, false, "default_p30_silencer", "p30silenced",
						"P30[Silenced]", null, m(23),  Arrays.asList(
								new String[]{getIngString(Material.IRON_INGOT, 0, 4), "p30"}), 1000, "p30").setReloadingHandler(ReloadingManager.SLIDE_RELOAD)
				.setWeaponSound(WeaponSounds.SILENCEDSHOT).setMaterial(Material.CROSSBOW).done();
		GunYMLCreator
				.createAttachment(false, dataFolder, false, "default_awp_asiimov", "awpasiimov",
						"AWP[Asiimov-skin]", null, m(24), Arrays.asList(
								new String[]{getIngString(Material.WHITE_DYE, 0, 1), "awp"}), 1000, "awp").setMaterial(Material.CROSSBOW)
				.setUseOffhand(false).done();

		GunYMLCreator
				.createNewDefaultGun(dataFolder, "m4a1s", "M4A1s", 25, stringsMetalRif, WeaponType.RIFLE,
						null, true, "556", 3, 30, 3600)
				.setFullyAutomatic(2).setBulletsPerShot(1).setRecoil(5).setReloadingSound(WeaponSounds.RELOAD_FN).setWeaponSound(WeaponSounds.SILENCEDSHOT).setMaterial(Material.CROSSBOW).done();

		GunYMLCreator
				.createNewDefaultGun(dataFolder, "rpk", "RPK", 26, stringsWoodRif, WeaponType.RIFLE, null,
						false, "762", 3, 70, 7000)
				.setFullyAutomatic(3).setBulletsPerShot(1).setRecoil(2).setReloadingSound(WeaponSounds.RELOAD_AK47).setWeaponSound(WeaponSounds.GUN_AK47).setMaterial(Material.CROSSBOW).done();

		GunYMLCreator
				.createNewDefaultGun(dataFolder, "sg553", "SG-553", 27, stringsMetalRif, WeaponType.RIFLE,
						null, true, "556", 3, 40, 3200)
				.setFullyAutomatic(2).setBulletsPerShot(1).setRecoil(2).setMaterial(Material.CROSSBOW).setReloadingSound(WeaponSounds.RELOAD_FN).done();
		GunYMLCreator.createNewDefaultGun(dataFolder, "fnfiveseven", "FN-Five-Seven", 69, stringsPistol,
				WeaponType.PISTOL, null, true, "9mm", 3, 12, 700).setMaterial(Material.CROSSBOW).setIsSecondaryWeapon(true).done();

		GunYMLCreator
				.createNewDefaultGun(dataFolder, "dp27", "DP-27", 28, stringsMetalRif, WeaponType.RIFLE,
						WeaponSounds.GUN_BIG, true, "762", 3, 47, 3000)
				.setFullyAutomatic(2).setBulletsPerShot(1).setRecoil(2).setMaterial(Material.CROSSBOW).done();

		GunYMLCreator
				.createMisc(false, dataFolder, false, "default_incendarygrenade", "incendarygrenade",
						"&7Incendary Grenade",
						Arrays.asList(ChatColor.DARK_GRAY + "[LMB] to pull pin",
								ChatColor.DARK_GRAY + "[RMB] to throw",
								ChatColor.DARK_GRAY + "Incendary Grenades wait " + ChatColor.GRAY
										+ "FIVE seconds" + ChatColor.DARK_GRAY + " before exploding.",
								ChatColor.DARK_RED + "<!>Will Explode Even If Not Thrown<!>"),
						m(29), stringsGrenades, 100, WeaponType.INCENDARY_GRENADES, 100, 1)
				.set(false, "radius", 5).done();
		GunYMLCreator
				.createNewDefaultGun(dataFolder, "homingrpg", "Homing RPG Launcher", 30, stringsMetalRif,
						WeaponType.RPG, null, false, "rocket", 100, 1, 5000)
				.setDelayShoot(1).setCustomProjectile(ProjectileManager.HOMING_RPG)
				.setCustomProjectileExplosionRadius(10).setCustomProjectileVelocity(2)// .setChargingHandler(ChargingManager.HOMINGRPG)
				.setReloadingHandler(ReloadingManager.SINGLE_RELOAD).setDistance(800).setNightVisionOnScope(true)
				.setParticle("SMOKE_LARGE").setRecoil(10).setMaterial(Material.CROSSBOW)
				.setKilledByMessage("%player% was blown to bits with precision the likes of which has never been seen before by %killer% using an %name%").done();

		GunYMLCreator
				.createNewDefaultGun(dataFolder, "flintlockpistol", "\"Harper's Ferry\" Flintlock Pistol",
						31, stringsMetalRif, WeaponType.RIFLE, WeaponSounds.GUN_AUTO, true, "musketball", 10, 1,
						100).setMaterial(Material.CROSSBOW)
				.setSway(0.4).setDelayReload(4).setDelayShoot(1).setIsSecondaryWeapon(true).setRecoil(8).done();

		// Jump for armor

		/**
		 * (boolean forceUpdate, File dataFolder, boolean invalid, String filename,
		 * String name, String displayname, List<String> lore, int id, List<String>
		 * craftingRequirements, WeaponType weapontype, boolean enableIronSights, String
		 * ammotype, int damage, double sway, Material type, int maxBullets, int
		 * duribility, double delayReload, double delayShoot, int bulletspershot,
		 * boolean isAutomatic, int cost, ChargingHandlerEnum ch, int distance, int var,
		 * boolean version18, WeaponSounds ws, String particle, double particleR, double
		 * particleG, double particleB, boolean addMuzzleSmoke)
		 *
		 */


		List<String> stringsFatman = Arrays.asList(new String[]{getIngString(Material.IRON_INGOT, 0, 32),
				getIngString(Material.REDSTONE, 0, 16), getIngString(Material.BLAZE_POWDER, 0, 8)});

		GunYMLCreator
				.createNewDefaultGun(dataFolder, "lazerrifle", "Lazer Rifle", 32, stringsMetalRif,
						WeaponType.LAZER, WeaponSounds.LAZERSHOOT, false, "fusion_cell", 4, 20, 2000)
				.setAutomatic(true).setParticle(1, 0, 0, Material.REDSTONE_BLOCK).setDistance(150).setSwayMultiplier(3).setSway(0.2)
				.setRecoil(0).setMaterial(Material.CROSSBOW)
				.setKilledByMessage("%player% was pew-pew'd by %killer% using a %name%").done();
		GunYMLCreator
				.createNewDefaultGun(dataFolder, "fatman", "Fatman", 33, stringsFatman, WeaponType.RPG,
						WeaponSounds.WARHEAD_LAUNCH, false, "mininuke", 500, 1, 6000)
				.setDelayShoot(1).setCustomProjectile(ProjectileManager.MINI_NUKE)
				.setCustomProjectileExplosionRadius(10).setCustomProjectileVelocity(3)// .setChargingHandler(ChargingManager.MININUKELAUNCHER)
				.setReloadingHandler(ReloadingManager.SINGLE_RELOAD).setDistance(500).setParticle(0.3, 0.9, 0.3, Material.COAL_BLOCK).setMaterial(Material.CROSSBOW)
				.setRecoil(5).setKilledByMessage("%player% was nuked by %killer% using a %name%").done();


		GunYMLCreator
				.createNewDefaultGun(dataFolder, "instituterifle", "Institute Rifle", 35,
						stringsMetalRif, WeaponType.LAZER, WeaponSounds.LAZERSHOOT, false, "fusion_cell", 4, 20,
						2000).setMaterial(Material.CROSSBOW)
				.setAutomatic(true).setParticle(0.5, 0.9, 0.9, Material.LAPIS_BLOCK).setDistance(150).setSwayMultiplier(3)
				.setSway(0.2).setRecoil(0)
				.setKilledByMessage("%player% was pew-pew'd by %killer% using an %name%").done();

		GunYMLCreator
				.createNewDefaultGun(dataFolder, "musket", "\"Brown Bess\" Musket", 36, stringsMetalRif,
						WeaponType.RIFLE, WeaponSounds.GUN_AUTO, true, "musketball", 10, 1, 100)
				.setSway(0.3).setDelayReload(5).setDelayShoot(1).setSwayMultiplier(3).setRecoil(3).setMaterial(Material.CROSSBOW).done();

		List<String> stringsRifle = Arrays.asList(new String[]{getIngString(Material.IRON_INGOT, 0, 8),
				getIngString(Material.REDSTONE, 0, 3)});
		List<String> stringsLight = Arrays.asList(new String[]{getIngString(Material.IRON_INGOT, 0, 8),
				getIngString(Material.NETHER_STAR, 0, 1)});

		GunYMLCreator
				.createNewCustomGun(dataFolder, "default_aliensrifle", "m41pulserifle",
						"M41PulseRifle", 37, stringsRifle, WeaponType.RIFLE,
						WeaponSounds.GUN_MEDIUM, false, "556", 4, 30, 5000)
				.setLore(Collections.singletonList("&fGame over, man. Game over!")).setFullyAutomatic(3)
				.setBulletsPerShot(1).setMuzzleSmoke(false).setRecoil(2).setMaterial(Material.CROSSBOW).done();
		GunYMLCreator
				.createNewCustomGun(dataFolder, "default_auto9", "auto9", "Auto9", 38,
						stringsPistol, WeaponType.PISTOL, WeaponSounds.GUN_DEAGLE, true, "556", 5, 12, 700)
				.setLore(Collections.singletonList("&fDead or alive, you're coming with me! ")).setReloadingHandler(ReloadingManager.SLIDE_RELOAD).setMaterial(Material.CROSSBOW).setZoomLevel(1)
				.setRecoil(2).done();
		GunYMLCreator
				.createNewCustomGun(dataFolder, "default_arcgun9", "arcgun9",
						"The Arc-Gun-9", 39, strings10mm, WeaponType.LAZER,
						WeaponSounds.SHOCKWAVE, false, "fusion_cell", 0, 10, 2400)
				.setLore(Collections.singletonList("&fPushy!")).setMaterial(Material.CROSSBOW)
				.setChargingHandler(ChargingManager.getHandler(ChargingManager.PUSHBACK)).setFiringKnockback(1).setKilledByMessage("%player% was ?????? by %killer% using a %name%").done();
		GunYMLCreator
				.createNewCustomGun(dataFolder, "default_halorifle", "unscassaultrifle",
						"UNSCAssaultRifle", 40, stringsRifle, WeaponType.RIFLE,
						WeaponSounds.GUN_MEDIUM, true, "556", 3, 32, 3800)
				.setFullyAutomatic(3).setBulletsPerShot(1).setMaterial(Material.CROSSBOW)
				.setLore(Collections.singletonList("&fAlso known as the \"MA5B\"")).setReloadingSound(WeaponSounds.RELOAD_FN).setRecoil(2).done();
		GunYMLCreator
				.createNewCustomGun(dataFolder, "default_haloalien", "alienneedler",
						"\"Needler\"", 41, stringsRifle, WeaponType.PISTOL,
						WeaponSounds.GUN_NEEDLER, true, "fusion_cell", 1, 32, 2000)
				.setFullyAutomatic(4).setBulletsPerShot(1).setMaterial(Material.CROSSBOW)
				.setLore(Collections.singletonList("&fWarning: Sharp")).setParticle("REDSTONE", 1, 0.1, 1, Material.DIAMOND_BLOCK)
				.done();
		GunYMLCreator
				.createNewCustomGun(dataFolder, "default_thatgun", "thatgun",
						"\"That Gun\"", 42, stringsRifle, WeaponType.PISTOL,
						WeaponSounds.GUN_DEAGLE, true, "556", 5, 12, 2000)
				.setLore(Collections.singletonList("&fAlso known as the \"LAPD 2019 Detective Special\"")).setReloadingHandler(ReloadingManager.SLIDE_RELOAD).setMaterial(Material.CROSSBOW)
				.setRecoil(2).done();
		GunYMLCreator
				.createNewCustomGun(dataFolder, "default_blaster", "blaster",
						"\"Blaster\" Pistol", 43, stringsGoldRif, WeaponType.LAZER,
						WeaponSounds.GUN_STARWARS, false, "fusion_cell", 4, 20, 1600)
				.setFullyAutomatic(1).setBulletsPerShot(1).setMuzzleSmoke(false).setParticle(1, 0, 0, Material.REDSTONE_BLOCK)
				.setLore(Collections.singletonList("&fMiss all the shots you want!")).setRecoil(0).setMaterial(Material.CROSSBOW)
				.setKilledByMessage("%player% was pew-pew'd by %killer% using a %name%").done();
		GunYMLCreator
				.createNewCustomGun(dataFolder, "default_hl2pulserifle", "pulserifle",
						"Overwatch Pulse Rifle", 44, stringsGoldRif, WeaponType.LAZER,
						WeaponSounds.GUN_HALOLAZER, true, "fusion_cell", 4, 30, 5000)
				.setFullyAutomatic(3).setBulletsPerShot(1).setMuzzleSmoke(false).setMaterial(Material.CROSSBOW)
				.setLore(Collections.singletonList("&fStardard Issue Rifles for Combie solders."))
				.setParticle(0.5, 0.99, 0.99, Material.GOLD_BLOCK).setRecoil(2).done();
		GunYMLCreator
				.createNewCustomGun(dataFolder, "default_vera", "vera", "Vera", 45,
						stringsGoldRif, WeaponType.RIFLE, WeaponSounds.GUN_DEAGLE, true, "556", 3, 30, 3000)
				.setUseOffhand(false).setNightVisionOnScope(true).setZoomLevel(5).setMaterial(Material.CROSSBOW)
				.setLore(Arrays.asList("&fThe Callahan Full-bore Auto-lock.", "&7\"Customized trigger, �",
						"&7double cartridge thorough gauge.", "&7It is my very favorite gun ",
						"&7This is the best gun made by man.", "&7 It has extreme sentimental value ",
						"&7I call her Vera.\"-Jayne Cobb"))
				.setFullyAutomatic(2).setBulletsPerShot(1).setRecoil(2).done();

		GunYMLCreator.createMisc(false, dataFolder, false, "default_lightsaberblue", "LightSaberBlue",
				"LightSaber (Blue)", Arrays.asList("&fMay The Force be with you", "&fAlways"),
				Material.CROSSBOW, 46, stringsLight, 10000, WeaponType.MELEE, 9, 1000).setSoundOnEquip(WeaponSounds.LIGHTSABER_LIGHT_START.getSoundName()).setSoundOnHit(WeaponSounds.LIGHTSABER_HIT.getSoundName()).done();
		GunYMLCreator.createMisc(false, dataFolder, false, "default_lightsaberred", "LightSaberRed",
				"LightSaber (Red)", Arrays.asList("&fNo, I am your father.","&fSearch your feelings. You know it to be true."),
				Material.CROSSBOW, 47, stringsLight, 10000, WeaponType.MELEE, 9, 1000).setSoundOnEquip(WeaponSounds.LIGHTSABER_SITH_START.getSoundName()).setSoundOnHit(WeaponSounds.LIGHTSABER_HIT.getSoundName()).done();

		GunYMLCreator.createNewDefaultGun(dataFolder, "mac10", "Mac-10", 48, stringsMetalRif,
				WeaponType.SMG, WeaponSounds.GUN_SMALL_AUTO, true, "9mm", 2, 32, 2500).setReloadingHandler(ReloadingManager.SLIDE_RELOAD).setFullyAutomatic(3)
				.setMaterial(Material.CROSSBOW).done();
		GunYMLCreator.createNewDefaultGun(dataFolder, "uzi", "UZI", 49, stringsMetalRif, WeaponType.SMG,
				WeaponSounds.GUN_SMALL_AUTO, true, "9mm", 2, 25, 2000).setReloadingHandler(ReloadingManager.SLIDE_RELOAD).setFullyAutomatic(3).setMaterial(Material.CROSSBOW).done();

		GunYMLCreator
				.createNewDefaultGun(dataFolder, "skorpion", "Skorpion vz.61", 50, stringsMetalRif,
						WeaponType.SMG, WeaponSounds.GUN_SMALL_AUTO, true, "9mm", 2, 20, 1600).setMaterial(Material.CROSSBOW)
				.setFullyAutomatic(3).done();

		GunYMLCreator
				.createNewDefaultGun(dataFolder, "sks", "SKS-45", 51, stringsWoodRif, WeaponType.SNIPER,
						null, true, "762", 7, 10, 2000).setMaterial(Material.CROSSBOW)
				.setUseOffhand(false).setDelayShoot(0.6).setZoomLevel(6).setDistance(290).setSwayMultiplier(3).setRecoil(8).setReloadingSound(WeaponSounds.RELOAD_SLIDE)
				.setKilledByMessage("%player% was sniped by %killer% using a %name%").done();

		GunYMLCreator
				.createNewDefaultGun(dataFolder, "barrett", "Barrett-M82", 52, stringsWoodRif,
						WeaponType.SNIPER, WeaponSounds.GUN_BIG, true, "50bmg", 17, 10, 4000)
				.setDelayShoot(1).setZoomLevel(6).setDistance(350).setDelayReload(2.5).setSwayMultiplier(3).setMaterial(Material.CROSSBOW).setWeaponSound(WeaponSounds.GUN_SHOTGUN).setReloadingSound(WeaponSounds.RELOAD_FN).setUseOffhand(false)
				.setNightVisionOnScope(true).setRecoil(15).setKilledByMessage("%player% was sniped by %killer% using a %name%").done();

		GunYMLCreator
				.createNewDefaultGun(dataFolder, "makarov", "Makarov \"PM\"", 53, stringsPistol,
						WeaponType.PISTOL, WeaponSounds.GUN_SMALL_AUTO, true, "9mm", 3, 8, 700).setReloadingHandler(ReloadingManager.SLIDE_RELOAD).setMaterial(Material.CROSSBOW)
				.setIsSecondaryWeapon(true).done();

		GunYMLCreator.createNewDefaultGun(dataFolder, "ppsh41", "PPSh-41", 54, stringsWoodRif,
				WeaponType.RIFLE, null, true, "762", 3, 71, 7000).setReloadingSound(WeaponSounds.RELOAD_FN).setFullyAutomatic(3).setRecoil(2).setMaterial(Material.CROSSBOW).done();
		GunYMLCreator
				.createNewDefaultGun(dataFolder, "m79", "M79 \"Thumper\"", 55, stringsFatman,
						WeaponType.RPG, WeaponSounds.THUMPER, true, "40mm", 100, 1, 5000)
				.setDelayShoot(1).setCustomProjectile(ProjectileManager.EXPLODINGROUND)
				.setCustomProjectileVelocity(2).setCustomProjectileExplosionRadius(6)// .setChargingHandler(ChargingManager.MININUKELAUNCHER)
				.setReloadingHandler(ReloadingManager.SINGLE_RELOAD).setDistance(500).setMaterial(Material.CROSSBOW)
				.setParticle(0.001, 0.001, 0.001, Material.COAL_BLOCK).setRecoil(10).setKilledByMessage("%player% was thump'd by %killer% using a %name%").done();
		GunYMLCreator
				.createNewDefaultGun(dataFolder, "minigun", "Minigun", 56, stringsMetalRif,
						WeaponType.BIG_GUN, WeaponSounds.GUN_BIG, true, "556", 2, 200, 15000)
				.setFullyAutomatic(5).setBulletsPerShot(1).setChargingHandler(ChargingManager.REQUIREAIM).setMaterial(Material.CROSSBOW)
				.setSway(0.5).setSwayMultiplier(2.4).setUseOffhand(false).setParticle(0.9, 0.9, 0.9, Material.STONE)
				.setKilledByMessage("%player% was BRRRRRRRR'd by %killer% using a big %name%").done();
		GunYMLCreator// TODO: MINIGUN RECOIL
				.createNewDefaultGun(dataFolder, "mk19", "Mk-19", 57, stringsMetalRif, WeaponType.BIG_GUN,
						WeaponSounds.WARHEAD_LAUNCH, true, "40mm", 50, 50, 20000)
				.setFullyAutomatic(1).setCustomProjectile(ProjectileManager.EXPLODINGROUND).setWeaponSound(WeaponSounds.GUN_SHOTGUN)
				.setCustomProjectileVelocity(4).setCustomProjectileExplosionRadius(5)
				.setUseOffhand(false).setChargingHandler(ChargingManager.REQUIREAIM).setSway(0.5).setSwayMultiplier(2.4).setMaterial(Material.CROSSBOW)
				.setParticle(0.001, 0.001, 0.001, Material.COAL_BLOCK).setRecoil(7)
				.setKilledByMessage("%player% was blown up by %killer% using a %name%").done();
		GunYMLCreator
				.createNewDefaultGun(dataFolder, "asval", "AS-Val", 58, stringsMetalRif, WeaponType.RIFLE,
						WeaponSounds.SILENCEDSHOT, true, "762", 3, 30, 7000).setMaterial(Material.CROSSBOW)
				.setUseOffhand(false).setSway(0.2).setFullyAutomatic(3).setRecoil(2).setReloadingSound(WeaponSounds.RELOAD_FN).done();
		GunYMLCreator
				.createNewDefaultGun(dataFolder, "fnp90", "FN-P90", 59, stringsMetalRif, WeaponType.SMG,
						WeaponSounds.SILENCEDSHOT, true, "556", 2, 50, 3000).setMaterial(Material.CROSSBOW).setReloadingSound(WeaponSounds.RELOAD_FN)
				.setDelayReload(2.5).setFullyAutomatic(4).setRecoil(2).done();
		GunYMLCreator
				.createNewDefaultGun(dataFolder, "kar98k", "Kar-98K", 60, stringsWoodRif,
						WeaponType.SNIPER, null, true, "762", 10, 6, 2500).setMaterial(Material.CROSSBOW)
				.setUseOffhand(false).setZoomLevel(2).setDelayShoot(0.7).setChargingHandler(ChargingManager.BOLT)
				.setSwayMultiplier(3).setDistance(280).setRecoil(7).done();
		GunYMLCreator.createNewDefaultGun(dataFolder, "mp40", "MP 40", 61, stringsMetalRif,
				WeaponType.SMG, WeaponSounds.GUN_SMALL, true, "9mm", 2, 32, 3800).setFullyAutomatic(3).setMaterial(Material.CROSSBOW).setReloadingSound(WeaponSounds.RELOAD_FN).done();
		GunYMLCreator
				.createNewDefaultGun(dataFolder, "sturmgewehr44", "Sturmgewehr 44", 62, stringsMetalRif,
						WeaponType.SMG, WeaponSounds.GUN_AUTO, true, "762", 3, 30, 3800).setMaterial(Material.CROSSBOW).setReloadingSound(WeaponSounds.RELOAD_AK47)
				.setFullyAutomatic(3).setRecoil(2).done();

		/**
		 * Variant systems
		 */
		GunYMLCreator
				.createNewDefaultGun(dataFolder, "vz58", "VZ.58", 4, stringsMetalRif, WeaponType.RIFLE,
						null, true, "762", 3, 30, 4500)
				.setSway(0.2).setFullyAutomatic(2).setBulletsPerShot(1).setVariant(1).setMaterial(Material.CROSSBOW).done();
		GunYMLCreator.createNewDefaultGun(dataFolder, "cz75", "CZ.75", 2, stringsPistol, WeaponType.PISTOL,
				null, true, "9mm", 3, 12, 700).setIsSecondaryWeapon(true).setVariant(1).setMaterial(Material.CROSSBOW).done();

		GunYMLCreator
				.createNewDefaultGun(dataFolder, "sawedoffshotgun", "Sawed-off Shotgun", 63,
						stringsMetalRif, WeaponType.SHOTGUN, null, true, "shell", 2, 2, 1000)
				.setReloadingHandler(ReloadingManager.SINGLE_RELOAD).setReloadingSound(WeaponSounds.RELOAD_SHELL).setWeaponSound(WeaponSounds.GUN_SHOTGUN).setDelayReload(1).setBulletsPerShot(20).setMaterial(Material.CROSSBOW)
				.setDistance(80).setRecoil(11).done();
		GunYMLCreator
				.createNewDefaultGun(dataFolder, "famas", "FAMAS-G2", 64, stringsMetalRif,
						WeaponType.RIFLE, null, true, "556", 3, 30, 4500).setMaterial(Material.CROSSBOW).setReloadingSound(WeaponSounds.RELOAD_FN)
				.setFullyAutomatic(3).setRecoil(2).done();

		GunYMLCreator.createDefaultArmor(dataFolder, false, "assaulthelmet", "Assault Helmet", null, 18,
				stringsHelmet, 3000, WeaponType.HELMET, 1.5, 2, true).setMaterial(Material.PHANTOM_MEMBRANE).done();
		GunYMLCreator.createDefaultArmor(dataFolder, false, "ncrhelmet", "NCR Ranger Helmet", null, 19,
				stringsHelmet, 5000, WeaponType.HELMET, 1.5, 2, true).setMaterial(Material.PHANTOM_MEMBRANE).done();
		GunYMLCreator.createDefaultArmor(dataFolder, false, "skimask", "Ski Mask", null, 20, StringsWool,
				50, WeaponType.HELMET, 1, 0, false).setMaterial(Material.PHANTOM_MEMBRANE).done();
		GunYMLCreator.createDefaultArmor(dataFolder, false, "ushanka", "Ushanka-Hat", null, 21,
				StringsWool, 50, WeaponType.HELMET, 1, 0, false).setMaterial(Material.PHANTOM_MEMBRANE).done();




		GunYMLCreator
				.createNewDefaultGun(dataFolder, "flamer", "Flamer", 65, stringsMetalRif,
						WeaponType.FLAMER, WeaponSounds.HISS, false, "fuel", 1, 60, 8000)
				.setFullyAutomatic(5).setRecoil(0).setCustomProjectile(ProjectileManager.FIRE).setDistance(11).setMaterial(Material.CROSSBOW)
				.setKilledByMessage("%player% was burned alive by %killer% using a %name%")
				.done();
		GunYMLCreator
				.createNewDefaultGun(dataFolder, "glock", "Glock-17", 1, stringsPistol, WeaponType.PISTOL,
						null, true, "9mm", 3, 15, 1800)
				.setIsSecondaryWeapon(true).setFireRate(3).setVariant(2).setReloadingHandler(ReloadingManager.SLIDE_RELOAD).setMaterial(Material.CROSSBOW).done();

		GunYMLCreator
				.createNewDefaultGun(dataFolder, "sten", "STEN Gun", 66, stringsMetalRif,
						WeaponType.RIFLE, WeaponSounds.GUN_SMALL_AUTO, true, "9mm", 2, 32, 2500)
				.setFullyAutomatic(3).setRecoil(1).setMaterial(Material.CROSSBOW).setReloadingSound(WeaponSounds.RELOAD_FN).done();

		GunYMLCreator
				.createNewDefaultGun(dataFolder, "m4a1sburst", "M4A1s (Burst)", 25, stringsMetalRif,
						WeaponType.RIFLE, null, true, "556", 3, 30, 3600)
				.setVariant(1).setChargingHandler(ChargingManager.BURSTFIRE).setFireRate(3).setBulletsPerShot(3).setMaterial(Material.CROSSBOW).setReloadingSound(WeaponSounds.RELOAD_M16)
				.setRecoil(1).done();

		GunYMLCreator
				.createNewDefaultGun(dataFolder, "m32a1", "M32A1", 67, stringsFatman,
						WeaponType.RPG, WeaponSounds.WARHEAD_LAUNCH, true, "40mm", 100, 6, 60000)
				.setDelayShoot(0.7).setCustomProjectile(ProjectileManager.EXPLODINGROUND)
				.setCustomProjectileVelocity(2).setCustomProjectileExplosionRadius(6)// .setChargingHandler(ChargingManager.MININUKELAUNCHER)
				.setReloadingHandler(ReloadingManager.SINGLE_RELOAD).setDelayReload(5).setDistance(500).setMaterial(Material.CROSSBOW)
				.setParticle(0.001, 0.001, 0.001, Material.COAL_BLOCK).setRecoil(8).done();

		GunYMLCreator
				.createAttachment(false, dataFolder, false, "default_uzicorn", "uzicorn",
						"Uzicorn", null, m(75),  Arrays.asList(
								new String[]{getIngString(Material.WHITE_WOOL, 0, 6), "uzi"}), 2400, "uzi")
				.setKilledByMessage("%player% was surprised by %killer% using a %name%")
				.setReloadingHandler(ReloadingManager.SLIDE_RELOAD)
				.setWeaponSound(WeaponSounds.SILENCEDSHOT).setMaterial(Material.CROSSBOW).done();


		GunYMLCreator
				.createNewDefaultGun(dataFolder, "henryrifle", "Henry Rifle", 76, stringsGoldRif,
						WeaponType.RIFLE, null, true, "556", 4, 6, 400)
				.setChargingHandler(ChargingManager.BREAKACTION).setMaterial(Material.CROSSBOW).setDelayReload(0.5)
				.setReloadingHandler(ReloadingManager.SINGLE_RELOAD).done();

		GunYMLCreator
				.createNewDefaultGun(dataFolder, "deagle", "Deagle", 78, stringsMetalRif,
						WeaponType.PISTOL, WeaponSounds.GUN_DEAGLE, true, "9mm", 8, 7, 1800).setMaterial(Material.CROSSBOW)
				.setReloadingHandler(ReloadingManager.SLIDE_RELOAD)
				.setKilledByMessage("%player% was shot by %killer% using a %name%").done();





		GunYMLCreator.createMisc(false, dataFolder, false, "default_lightsaberblack", "LightSaberBlack",
				"LightSaber (Black)", Arrays.asList("&fAnyone can hold the Darksaber.","&fThe trick is keeping it, along with your head."),
				Material.CROSSBOW, 70, stringsLight, 10000, WeaponType.MELEE, 9, 1000).setSoundOnEquip(WeaponSounds.LIGHTSABER_SITH_START.getSoundName()).setSoundOnHit(WeaponSounds.LIGHTSABER_HIT.getSoundName()).done();

		GunYMLCreator.createMisc(false, dataFolder, false, "default_lightsaberwhite", "LightSaberWhite",
				"LightSaber (White)", Arrays.asList("&fThey used to be red. When the creature had them,","&fthey were red.", "&fBut I heard them before I ever saw him on Radaa,","&f and knew that they were meant for me."),
				Material.CROSSBOW, 71, stringsLight, 10000, WeaponType.MELEE, 9, 1000).setSoundOnEquip(WeaponSounds.LIGHTSABER_LIGHT_START.getSoundName()).setSoundOnHit(WeaponSounds.LIGHTSABER_HIT.getSoundName()).done();

		GunYMLCreator.createMisc(false, dataFolder, false, "default_lightsabergreen", "LightSaberGreen",
				"LightSaber (Green)", Arrays.asList("&fI see you have constructed a new lightsaber.","&fYour skills are complete.", "&fIndeed you are powerful, as the Emperor has foreseen."),
				Material.CROSSBOW, 72, stringsLight, 10000, WeaponType.MELEE, 9, 1000).setSoundOnEquip(WeaponSounds.LIGHTSABER_LIGHT_START.getSoundName()).setSoundOnHit(WeaponSounds.LIGHTSABER_HIT.getSoundName()).done();

		GunYMLCreator.createMisc(false, dataFolder, false, "default_lightsaberPurple", "LightSaberPurple",
				"LightSaber (Purple)", Arrays.asList("&fIn the name of the Galactic Senate of the Republic,","&fyou're under arrest, Chancellor."),
				Material.CROSSBOW, 73, stringsLight, 10000, WeaponType.MELEE, 9, 1000).setSoundOnEquip(WeaponSounds.LIGHTSABER_SITH_START.getSoundName()).setSoundOnHit(WeaponSounds.LIGHTSABER_HIT.getSoundName()).done();

		GunYMLCreator.createMisc(false, dataFolder, false, "default_lightsaberorange", "LightSaberOrange",
				"LightSaber (Orange)", Arrays.asList("&fAn extremely rare saber."),
				Material.CROSSBOW, 74, stringsLight, 10000, WeaponType.MELEE, 9, 1000).setSoundOnEquip(WeaponSounds.LIGHTSABER_SITH_START.getSoundName()).setSoundOnHit(WeaponSounds.LIGHTSABER_HIT.getSoundName()).done();

		GunYMLCreator.createMisc(false, dataFolder, false, "default_spear", "spear",
				"Spear", null,
				Material.CROSSBOW, 77, stringsMetalRif, 100, WeaponType.MELEE, 6, 1000).done();


		GunYMLCreator
				.createNewDefaultGun(dataFolder, "pancorjackhammer", "Pancor Jackhammer", 79, stringsMetalRif, WeaponType.SHOTGUN,
						null, false, "shell", 2, 8, 4000).setMaterial(Material.CROSSBOW)
				.setBulletsPerShot(10).setDistance(80).setFullyAutomatic(2).setRecoil(7).setReloadingSound(WeaponSounds.RELOAD_FN).setKilledByMessage("%player% was shot to bits by %killer% using an %name%, a gun that does not exist.").done();



		GunYMLCreator
				.createMisc(false, dataFolder, false, "default_molotov", "molotov",
						"&7Molotov",
						Arrays.asList("&8[LMB] to light",
								"&8[RMB] to throw",
								"&8Molotovs explode on contact",
								"&4<!>Will Not Explode If Not Thrown<!>"),
						m(80), stringsGrenades, 100, WeaponType.MOLOTOV, 100, 1)
				.set(false, "radius", 5).done();

		GunYMLCreator
				.createMisc(false, dataFolder, false, "default_proxymine", "proxymine",
						"&7Proxy-Mine",
						Arrays.asList("&8[LMB] to activate",
								"&8[RMB] to throw",
								"&8Proxy-Mines explode after being thrown by pressing [SHIFT]",
								"&4<!>Will Not Explode If Not Thrown<!>"),
						m(81), stringsGrenades, 100, WeaponType.PROXYMINES, 100, 1)
				.set(false, "radius", 5).done();


		GunYMLCreator.createMisc(false, dataFolder, false, "default_medkit", "medkit",
				"Medkit", null,
				Material.PHANTOM_MEMBRANE, 22, stringsHealer, 10000, WeaponType.MEDKIT, 9, 1000).setSoundOnEquip(WeaponSounds.OPENBAG.getSoundName()).setSoundOnHit(WeaponSounds.OPENBAG.getSoundName()).done();

		GunYMLCreator
				.createNewDefaultGun(dataFolder, "m1garand", "M1 Garand", 82, stringsWoodRif, WeaponType.SNIPER, null,
						true, "762", 10, 6, 2700)
				.setZoomLevel(9).setDelayShoot(0.7).setReloadingHandler(ReloadingManager.getHandler(ReloadingManager.M1GARAND_RELOAD)).setMaterial(Material.CROSSBOW).setUseOffhand(true)
				.setSwayMultiplier(3).setDistance(280).setRecoil(5).done();


		GunYMLCreator
				.createNewDefaultGun(dataFolder, "galil", "Galil-AR", 83, stringsMetalRif,
						WeaponType.RIFLE, null, true, "556", 3, 30, 4500).setMaterial(Material.CROSSBOW).setReloadingSound(WeaponSounds.RELOAD_FN)
				.setFullyAutomatic(3).setRecoil(2).done();
		GunYMLCreator
				.createNewDefaultGun(dataFolder, "ctar21", "CTAR-21", 84, stringsMetalRif, WeaponType.RIFLE,
						null, true, "762", 3, 40, 5000)
				.setSway(0.19).setFullyAutomatic(2).setBulletsPerShot(1).setRecoil(2).setMaterial(Material.CROSSBOW)
				.setKilledByMessage("%player% was shot by %killer% using an %name%").setReloadingSound(WeaponSounds.RELOAD_AK47).setWeaponSound(WeaponSounds.GUN_AK47).done();

		GunYMLCreator.createNewDefaultGun(dataFolder, "debuggun", "Debug Gun", 34, strings10mm,
				WeaponType.PISTOL, WeaponSounds.LAZERSHOOT, true, null, 5, 69420, -1)
				.setKilledByMessage("%player% was \"killed\" by %killer% using a %name%")
				.setLore("&3No sway. Perfect for detecting bounding boxes.").setReloadingHandler(ReloadingManager.SLIDE_RELOAD).setIsSecondaryWeapon(true).setDelayShoot(0).setRecoil(0).setFullyAutomatic(12).setSway(0).setSwayMultiplier(0).setHeadShotMultiplier(12).setVariant(12).setMaterial(Material.CROSSBOW).done();


	}

	public String getIngString(Material m, int durability, int amount) {
		return m.toString() + "," + durability + "," + amount;
	}

	public boolean isOverrideAttackSpeed() {
		return overrideAttackSpeed;
	}

	public CustomGunItem setOverrideAttackSpeed(boolean overrideAttackSpeed) {
		this.overrideAttackSpeed = overrideAttackSpeed;
		return this;
	}
}
