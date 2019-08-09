package me.zombie_striker.customitemmanager.versions.V1_8;

import me.zombie_striker.customitemmanager.AbstractItem;
import me.zombie_striker.customitemmanager.AbstractItemFact;
import me.zombie_striker.customitemmanager.CustomItemManager;
import me.zombie_striker.customitemmanager.OLD_ItemFact;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.config.ArmoryYML;
import me.zombie_striker.qg.config.GunYMLCreator;
import me.zombie_striker.qg.guns.projectiles.ProjectileManager;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.guns.utils.WeaponType;
import me.zombie_striker.qg.handlers.MultiVersionLookup;
import me.zombie_striker.qg.handlers.chargers.ChargingManager;
import me.zombie_striker.qg.handlers.reloaders.ReloadingManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class CustomGunItem extends AbstractItem {

	@Override
	public ItemStack getItem(Material material, int data, int variant) {
		ItemStack is = new ItemStack(material, 1, (short) data);
		if (variant != 0) {
			ItemMeta im = is.getItemMeta();
			OLD_ItemFact.addVariantData(im, im.getLore(), variant);
		}
		return QualityArmory.getCustomItem(QualityArmory.getCustomItem(is));
	}


	@Override
	public boolean isCustomItem(ItemStack is) {
		return QualityArmory.isCustomItem(is);
	}

	@Override
	public void initItems(File dataFolder) {
		CustomItemManager.setResourcepack("https://www.dropbox.com/s/odvle92e0fz0ezr/QualityArmory1.8v1.0.2.zip?dl=1");


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

		List<String> stringsAmmo = Arrays.asList(new String[]{getIngString(Material.IRON_INGOT, 0, 1),
				getIngString(MultiVersionLookup.getGunpowder(), 0, 1), getIngString(Material.REDSTONE, 0, 1)});
		List<String> stringsAmmoMusket = Arrays.asList(new String[]{getIngString(Material.IRON_INGOT, 0, 4),
				getIngString(MultiVersionLookup.getGunpowder(), 0, 3),});
		List<String> stringsAmmoRPG = Arrays.asList(new String[]{getIngString(Material.IRON_INGOT, 0, 4),
				getIngString(MultiVersionLookup.getGunpowder(), 0, 6), getIngString(Material.REDSTONE, 0, 1)});

		List<String> StringsWool = Arrays.asList(new String[]{getIngString(MultiVersionLookup.getWool(), 0, 8)});

		List<String> stringsHealer = Arrays.asList(new String[]{getIngString(MultiVersionLookup.getWool(), 0, 6),
				getIngString(Material.GOLDEN_APPLE, 0, 1)});


		String additive = "_18";
		{
			GunYMLCreator
					.createNewCustomGun(dataFolder, "default_1_8_p30", "p30" + additive, "P30", 1,
							stringsPistol, WeaponType.PISTOL, null, true, "9mm", 3, 12, 100)
					.setMaterial(Material.IRON_HOE).setOn18(true).setIsSecondaryWeapon(true).done();
			GunYMLCreator
					.createNewCustomGun(dataFolder, "default_1_8_pkp", "pkp" + additive, "PKP", 1,
							stringsMetalRif, WeaponType.RIFLE, WeaponSounds.GUN_BIG, true, "556", 4, 100,
							3000)
					.setMaterial(Material.DIAMOND_AXE).setOn18(true).setFullyAutomatic(3)
					.setBulletsPerShot(1).done();
			GunYMLCreator
					.createNewCustomGun(dataFolder, "default_1_8_mp5k", "mp5k" + additive, "MP5K", 1,
							stringsMetalRif, WeaponType.SMG, null, false, "9mm", 3, 32, 1000)
					.setMaterial(MultiVersionLookup.getGoldPick()).setOn18(true).setFullyAutomatic(3)
					.setBulletsPerShot(1).done();
			GunYMLCreator
					.createNewCustomGun(dataFolder, "default_1_8_ak47", "ak47" + additive, "AK47", 1,
							stringsMetalRif, WeaponType.RIFLE, null, false, "556", 3, 40, 1500)
					.setMaterial(MultiVersionLookup.getGoldShovel()).setOn18(true).setFullyAutomatic(2)
					.setBulletsPerShot(1).done();
			GunYMLCreator
					.createNewCustomGun(dataFolder, "default_1_8_m16", "m16", "M16" + additive, 1,
							stringsMetalRif, WeaponType.RIFLE, null, true, "556", 3, 30, 2000)
					.setMaterial(MultiVersionLookup.getIronShovel()).setOn18(true).setFullyAutomatic(2)
					.setBulletsPerShot(1).done();
			GunYMLCreator
					.createNewCustomGun(dataFolder, "default_1_8_fnfal", "fnfal" + additive, "FNFal", 1,
							stringsMetalRif, WeaponType.RIFLE, null, false, "556", 3, 32, 2000)
					.setMaterial(MultiVersionLookup.getGoldHoe()).setOn18(true).setFullyAutomatic(2)
					.setBulletsPerShot(1).done();
			GunYMLCreator
					.createNewCustomGun(dataFolder, "default_1_8_rpg", "rpg" + additive, "RPG", 1,
							stringsRPG, WeaponType.RPG, null, false, "rocket", 100, 1, 4000)
					.setMaterial(Material.DIAMOND_HOE).setOn18(true).setCustomProjectile(ProjectileManager.RPG)
					.setCustomProjectileExplosionRadius(10).setCustomProjectileVelocity(2)// .setChargingHandler(ChargingManager.RPG)
					.setReloadingHandler(ReloadingManager.SINGLERELOAD).setDistance(500)
					.setParticle("SMOKE_LARGE").done();

			// TODO: New guns for resourcepack

			GunYMLCreator
					.createNewCustomGun(dataFolder, "default_1_8_famas", "famas" + additive, "FAMAS-G2", 1,
							stringsMetalRif, WeaponType.RIFLE, null, false, "556", 3, 30, 4500)
					.setFullyAutomatic(3).setRecoil(2).setMaterial(Material.PRISMARINE_CRYSTALS).setOn18(true)
					.done();
			GunYMLCreator
					.createNewCustomGun(dataFolder, "default_1_8_m79", "m79" + additive,
							"&6M79 \"Thumper\"", 1, stringsWoodRif, WeaponType.RPG, WeaponSounds.WARHEAD_LAUNCH,
							false, "40mm", 100, 1, 5000)
					.setDelayShoot(1).setCustomProjectile(ProjectileManager.EXPLODINGROUND)
					.setCustomProjectileVelocity(2).setCustomProjectileExplosionRadius(6)// .setChargingHandler(ChargingManager.MININUKELAUNCHER)
					.setReloadingHandler(ReloadingManager.SINGLERELOAD).setDistance(500)
					.setParticle(0.001, 0.001, 0.001, Material.COAL_BLOCK).setRecoil(10).setMaterial(Material.PRISMARINE_SHARD)
					.setOn18(true).done();
			GunYMLCreator
					.createNewCustomGun(dataFolder, "default_1_8_dp27", "dp27" + additive, "DP-27", 1,
							stringsMetalRif, WeaponType.RIFLE, WeaponSounds.GUN_BIG, false, "762", 3, 47, 3000)
					.setFullyAutomatic(2).setBulletsPerShot(1).setRecoil(2).setMaterial(Material.QUARTZ)
					.setOn18(true).done();
			GunYMLCreator
					.createNewCustomGun(dataFolder, "default_1_8_m40", "m40" + additive, "M40", 1,
							stringsWoodRif, WeaponType.SNIPER, null, false, "762", 10, 6, 2700)
					.setZoomLevel(9).setDelayShoot(0.7).setChargingHandler(ChargingManager.BOLT)
					.setSwayMultiplier(3).setDistance(280).setRecoil(5).setMaterial(Material.NETHER_BRICK)
					.setOn18(true).done();
			GunYMLCreator.createNewCustomGun(dataFolder, "default_1_8_uzi", "uzi" + additive, "UZI", 1,
					stringsMetalRif, WeaponType.SMG, WeaponSounds.GUN_SMALL_AUTO, false, "9mm", 2, 25, 2000)
					.setFullyAutomatic(3).setMaterial(Material.RABBIT_FOOT).setOn18(true).done();
			GunYMLCreator
					.createNewCustomGun(dataFolder, "default_1_8_aa12", "aa12" + additive, "AA-12", 26,
							stringsMetalRif, WeaponType.SHOTGUN, null, false, "shell", 2, 32, 4000)
					.setBulletsPerShot(10).setDistance(80).setFullyAutomatic(2).setRecoil(7)
					.setMaterial(MultiVersionLookup.getCarrotOnAStick()).setOn18(true).done();
			GunYMLCreator
					.createNewCustomGun(dataFolder, "default_1_8_spas12", "spas12" + additive, "Spas-12",
							1, stringsMetalRif, WeaponType.SHOTGUN, null, false, "shell", 2, 8, 1000)
					.setBulletsPerShot(20).setDistance(80).setRecoil(10).setMaterial(Material.RABBIT_HIDE)
					.setOn18(true).done();

		}

		ArmoryYML skullammo = GunYMLCreator.createSkullAmmo(false, dataFolder, false, "default18_ammo556",
				"556ammo", "&7 5.56x45mm NATO", null, MultiVersionLookup.getSkull(), 3, "cactus", null, 4, 1,
				50);
		skullammo.set(false, "skull_owner_custom_url_COMMENT",
				"Only specify the custom URL if the head does not use a player's skin, and instead sets the skin to a base64 value. If you need to get the head using a command, the URL should be set to the string of letters after \"Properties:{textures:[{Value:\"");
		skullammo.set(false, "skull_owner_custom_url",
				"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTg3ZmRmNDU4N2E2NDQ5YmZjOGJlMzNhYjJlOTM4ZTM2YmYwNWU0MGY2ZmFhMjc3ZDcxYjUwYmNiMGVhNjgzOCJ9fX0=");
		ArmoryYML skullammo2 = GunYMLCreator.createSkullAmmo(false, dataFolder, false, "default18_ammoRPG",
				"RPGammo", "&7 Rocket", null, MultiVersionLookup.getSkull(), 3, "cactus", null, 4, 1, 50);
		skullammo2.set(false, "skull_owner_custom_url_COMMENT",
				"Only specify the custom URL if the head does not use a player's skin, and instead sets the skin to a base64 value. If you need to get the head using a command, the URL should be set to the string of letters after \"Properties:{textures:[{Value:\"");
		skullammo2.set(false, "skull_owner_custom_url",
				"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTg3ZmRmNDU4N2E2NDQ5YmZjOGJlMzNhYjJlOTM4ZTM2YmYwNWU0MGY2ZmFhMjc3ZDcxYjUwYmNiMGVhNjgzOCJ9fX0=");

	}

	@Override
	public void initIronSights(File dataFolder) {

	}

	private AbstractItemFact fact = new ItemFactory();

	@Override
	public AbstractItemFact getItemFactory() {
		return fact;
	}

	public String getIngString(Material m, int durability, int amount) {
		return m.toString() + "," + durability + "," + amount;
	}
}
