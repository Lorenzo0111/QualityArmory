package me.zombie_striker.customitemmanager.qa.versions.V1_8;

import me.zombie_striker.customitemmanager.*;
import me.zombie_striker.customitemmanager.pack.StaticPackProvider;
import me.zombie_striker.customitemmanager.qa.AbstractCustomGunItem;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.armor.ArmorObject;
import me.zombie_striker.qg.attachments.AttachmentBase;
import me.zombie_striker.qg.config.ArmoryYML;
import me.zombie_striker.qg.config.GunYMLCreator;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.guns.chargers.ChargingManager;
import me.zombie_striker.qg.guns.projectiles.ProjectileManager;
import me.zombie_striker.qg.guns.reloaders.ReloadingManager;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.guns.utils.WeaponType;
import me.zombie_striker.qg.handlers.MultiVersionLookup;

@SuppressWarnings({ "deprecation", "unused" })

public class CustomGunItem extends AbstractCustomGunItem {

	public CustomGunItem(){
		CustomItemManager.setResourcepack(new StaticPackProvider("https://www.dropbox.com/s/pjoeg5e8l3byauf/QualityArmory1.8v1.0.3.zip?dl=1"));
	}
	@Override
	public ItemStack getItem(Material material, int data, int variant) {
		return getItem(MaterialStorage.getMS(material,data,variant));
	}

    @Override
    public ItemStack getItem(final MaterialStorage ms) {
        final CustomBaseObject base = QualityArmory.getCustomItem(ms);
        if (base == null)
            return null;
        final String displayname = base.getDisplayName();
        if (ms == null || ms.getMat() == null)
            return new ItemStack(Material.AIR);

        final ItemStack is = new ItemStack(ms.getMat(), 1, (short) ms.getData());
        if (ms.getData() < 0)
            is.setDurability((short) 0);
        ItemMeta im = is.getItemMeta();
        if (im == null)
            im = Bukkit.getServer().getItemFactory().getItemMeta(ms.getMat());
        if (im != null) {
            im.setDisplayName(displayname);
            final List<String> lore = base.getCustomLore() != null ? new ArrayList<>(base.getCustomLore()) : new ArrayList<>();

            if (base instanceof Gun)
                lore.addAll(Gun.getGunLore((Gun) base, null, ((Gun) base).getMaxBullets()));
            if (base instanceof AttachmentBase)
                lore.addAll(Gun.getGunLore(((AttachmentBase) base).getBaseGun(), null, ((AttachmentBase) base).getMaxBullets()));
            if (base instanceof ArmorObject)
                lore.addAll(OLD_ItemFact.getArmorLore((ArmorObject) base));

            OLD_ItemFact.addVariantData(im, lore, base);

            im.setLore(lore);
            if (QAMain.ITEM_enableUnbreakable) {
                try {
                    im.setUnbreakable(true);
                } catch (Error | Exception e34) {
                    /*
                     * try { im.spigot().setUnbreakable(true); } catch (Error | Exception e344) { }
                     */
                    // TODO: Readd Unbreakable support for 1.9
                }
            }
            try {
                if (QAMain.ITEM_enableUnbreakable) {
                    im.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_UNBREAKABLE);
                }
                im.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES);
                im.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_DESTROYS);
            } catch (final Error e) {

            }

            if (ms.getVariant() != 0) {
                OLD_ItemFact.addVariantData(im, im.getLore(), ms.getVariant());
            }
            is.setItemMeta(im);
        } else {
            // Item meta is still null. Catch and report.
            QAMain.getInstance().getLogger().warning(QAMain.prefix + " ItemMeta is null for " + base.getName() + ". I have");
        }
        is.setAmount(1);
        return is;
    }

    @Override
    public boolean isCustomItem(final ItemStack is) { return QualityArmory.isCustomItem(is); }

    @Override
    public void initIronsights(final File dataFolder) {

    }

    @Override
    public void initItems(final File dataFolder) {

        final List<String> stringsWoodRif = Arrays.asList(new String[] { this.getIngString(Material.IRON_INGOT, 0, 12),
                this.getIngString(MultiVersionLookup.getWood(), 0, 2), this.getIngString(Material.REDSTONE, 0, 5) });

        final List<String> stringsGoldRif = Arrays.asList(new String[] { this.getIngString(Material.IRON_INGOT, 0, 12),
                this.getIngString(Material.GOLD_INGOT, 0, 2), this.getIngString(Material.REDSTONE, 0, 5) });
        final List<String> stringsMetalRif = Arrays
                .asList(new String[] { this.getIngString(Material.IRON_INGOT, 0, 15), this.getIngString(Material.REDSTONE, 0, 5) });
        final List<String> stringsPistol = Arrays
                .asList(new String[] { this.getIngString(Material.IRON_INGOT, 0, 5), this.getIngString(Material.REDSTONE, 0, 2) });
        final List<String> stringsRPG = Arrays
                .asList(new String[] { this.getIngString(Material.IRON_INGOT, 0, 32), this.getIngString(Material.REDSTONE, 0, 10) });

        final List<String> stringsHelmet = Arrays
                .asList(new String[] { this.getIngString(Material.IRON_INGOT, 0, 5), this.getIngString(Material.OBSIDIAN, 0, 1) });

        final List<String> stringsGrenades = Arrays.asList(
                new String[] { this.getIngString(Material.IRON_INGOT, 0, 6), this.getIngString(MultiVersionLookup.getGunpowder(), 0, 10) });

        final List<String> stringsAmmo = Arrays.asList(new String[] { this.getIngString(Material.IRON_INGOT, 0, 1),
                this.getIngString(MultiVersionLookup.getGunpowder(), 0, 1), this.getIngString(Material.REDSTONE, 0, 1) });
        final List<String> stringsAmmoMusket = Arrays.asList(
                new String[] { this.getIngString(Material.IRON_INGOT, 0, 4), this.getIngString(MultiVersionLookup.getGunpowder(), 0, 3), });
        final List<String> stringsAmmoRPG = Arrays.asList(new String[] { this.getIngString(Material.IRON_INGOT, 0, 4),
                this.getIngString(MultiVersionLookup.getGunpowder(), 0, 6), this.getIngString(Material.REDSTONE, 0, 1) });

        final List<String> StringsWool = Arrays.asList(new String[] { this.getIngString(MultiVersionLookup.getWool(), 0, 8) });

        final List<String> stringsHealer = Arrays.asList(
                new String[] { this.getIngString(MultiVersionLookup.getWool(), 0, 6), this.getIngString(Material.GOLDEN_APPLE, 0, 1) });

        final String additive = "_18";
        {
            GunYMLCreator.createNewCustomGun(dataFolder, "default_1_8_p30", "p30" + additive, "P30", 1, stringsPistol, WeaponType.PISTOL,
                    null, true, "9mm", 3, 12, 100).setMaterial(Material.IRON_HOE).setOn18(true).setIsSecondaryWeapon(true).done();
            GunYMLCreator
                    .createNewCustomGun(dataFolder, "default_1_8_pkp", "pkp" + additive, "PKP", 1, stringsMetalRif, WeaponType.RIFLE,
                            WeaponSounds.GUN_BIG, true, "556", 4, 100, 3000)
                    .setMaterial(Material.DIAMOND_AXE).setOn18(true).setFullyAutomatic(3).setBulletsPerShot(1).done();
            GunYMLCreator
                    .createNewCustomGun(dataFolder, "default_1_8_mp5k", "mp5k" + additive, "MP5K", 1, stringsMetalRif, WeaponType.SMG, null,
                            false, "9mm", 3, 32, 1000)
                    .setMaterial(MultiVersionLookup.getGoldPick()).setOn18(true).setFullyAutomatic(3).setBulletsPerShot(1).done();
            GunYMLCreator
                    .createNewCustomGun(dataFolder, "default_1_8_ak47", "ak47" + additive, "AK47", 1, stringsMetalRif, WeaponType.RIFLE,
                            null, false, "556", 3, 40, 1500)
                    .setMaterial(MultiVersionLookup.getGoldShovel()).setOn18(true).setFullyAutomatic(2).setBulletsPerShot(1).done();
            GunYMLCreator
                    .createNewCustomGun(dataFolder, "default_1_8_m16", "m16", "M16" + additive, 1, stringsMetalRif, WeaponType.RIFLE, null,
                            true, "556", 3, 30, 2000)
                    .setMaterial(MultiVersionLookup.getIronShovel()).setOn18(true).setFullyAutomatic(2).setBulletsPerShot(1).done();
            GunYMLCreator
                    .createNewCustomGun(dataFolder, "default_1_8_fnfal", "fnfal" + additive, "FNFal", 1, stringsMetalRif, WeaponType.RIFLE,
                            null, false, "556", 3, 32, 2000)
                    .setMaterial(MultiVersionLookup.getGoldHoe()).setOn18(true).setFullyAutomatic(2).setBulletsPerShot(1).done();
            GunYMLCreator
                    .createNewCustomGun(dataFolder, "default_1_8_rpg", "rpg" + additive, "RPG", 1, stringsRPG, WeaponType.RPG, null, false,
                            "rocket", 100, 1, 4000)
                    .setMaterial(Material.DIAMOND_HOE).setOn18(true).setCustomProjectile(ProjectileManager.RPG)
                    .setCustomProjectileExplosionRadius(10).setCustomProjectileVelocity(2)// .setChargingHandler(ChargingManager.RPG)
                    .setReloadingHandler(ReloadingManager.SINGLE_RELOAD).setDistance(500).setParticle("SMOKE_LARGE").done();

            // TODO: New guns for resourcepack

            GunYMLCreator
                    .createNewCustomGun(dataFolder, "default_1_8_famas", "famas" + additive, "FAMAS-G2", 1, stringsMetalRif,
                            WeaponType.RIFLE, null, false, "556", 3, 30, 4500)
                    .setFullyAutomatic(3).setRecoil(2).setMaterial(Material.PRISMARINE_CRYSTALS).setOn18(true).done();
            GunYMLCreator
                    .createNewCustomGun(dataFolder, "default_1_8_m79", "m79" + additive, "&6M79 \"Thumper\"", 1, stringsWoodRif,
                            WeaponType.RPG, WeaponSounds.WARHEAD_LAUNCH, false, "40mm", 100, 1, 5000)
                    .setDelayShoot(1).setCustomProjectile(ProjectileManager.EXPLODINGROUND).setCustomProjectileVelocity(2)
                    .setCustomProjectileExplosionRadius(6)// .setChargingHandler(ChargingManager.MININUKELAUNCHER)
                    .setReloadingHandler(ReloadingManager.SINGLE_RELOAD).setDistance(500)
                    .setParticle(0.001, 0.001, 0.001, Material.COAL_BLOCK).setRecoil(10).setMaterial(Material.PRISMARINE_SHARD)
                    .setOn18(true).done();
            GunYMLCreator
                    .createNewCustomGun(dataFolder, "default_1_8_dp27", "dp27" + additive, "DP-27", 0, stringsMetalRif, WeaponType.RIFLE,
                            WeaponSounds.GUN_BIG, false, "762", 3, 47, 3000)
                    .setFullyAutomatic(2).setBulletsPerShot(1).setRecoil(2).setMaterial(Material.QUARTZ).setOn18(true).done();
            GunYMLCreator
                    .createNewCustomGun(dataFolder, "default_1_8_m40", "m40" + additive, "M40", 0, stringsWoodRif, WeaponType.SNIPER, null,
                            false, "762", 10, 6, 2700)
                    .setZoomLevel(9).setDelayShoot(0.7).setChargingHandler(ChargingManager.BOLT).setSwayMultiplier(3).setDistance(280)
                    .setRecoil(5).setMaterial(Material.NETHER_BRICK).setOn18(true).done();
            GunYMLCreator
                    .createNewCustomGun(dataFolder, "default_1_8_uzi", "uzi" + additive, "UZI", 0, stringsMetalRif, WeaponType.SMG,
                            WeaponSounds.GUN_SMALL_AUTO, false, "9mm", 2, 25, 2000)
                    .setFullyAutomatic(3).setMaterial(Material.RABBIT_FOOT).setOn18(true).done();
            GunYMLCreator
                    .createNewCustomGun(dataFolder, "default_1_8_aa12", "aa12" + additive, "AA-12", 0, stringsMetalRif, WeaponType.SHOTGUN,
                            null, false, "shell", 2, 32, 4000)
                    .setBulletsPerShot(10).setDistance(80).setFullyAutomatic(2).setRecoil(7)
                    .setMaterial(MultiVersionLookup.getCarrotOnAStick()).setOn18(true).done();
            GunYMLCreator
                    .createNewCustomGun(dataFolder, "default_1_8_spas12", "spas12" + additive, "Spas-12", 0, stringsMetalRif,
                            WeaponType.SHOTGUN, null, false, "shell", 2, 8, 1000)
                    .setBulletsPerShot(20).setDistance(80).setRecoil(10).setMaterial(Material.RABBIT_HIDE).setOn18(true).done();

        }

        final ArmoryYML skullammo = GunYMLCreator.createSkullAmmo(false, dataFolder, false, "default18_ammo556", "556ammo",
                "&7 5.56x45mm NATO", null, MultiVersionLookup.getSkull(), 3, "cactus", null, 4, 1, 50);
        skullammo.set(false, "skull_owner_custom_url_COMMENT",
                "Only specify the custom URL if the head does not use a player's skin, and instead sets the skin to a base64 value. If you need to get the head using a command, the URL should be set to the string of letters after \"Properties:{textures:[{Value:\"");
        skullammo.set(false, "skull_owner_custom_url",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTg3ZmRmNDU4N2E2NDQ5YmZjOGJlMzNhYjJlOTM4ZTM2YmYwNWU0MGY2ZmFhMjc3ZDcxYjUwYmNiMGVhNjgzOCJ9fX0=");
        final ArmoryYML skullammo2 = GunYMLCreator.createSkullAmmo(false, dataFolder, false, "default18_ammoRPG", "RPGammo", "&7 Rocket",
                null, MultiVersionLookup.getSkull(), 3, "cactus", null, 4, 1, 50);
        skullammo2.set(false, "skull_owner_custom_url_COMMENT",
                "Only specify the custom URL if the head does not use a player's skin, and instead sets the skin to a base64 value. If you need to get the head using a command, the URL should be set to the string of letters after \"Properties:{textures:[{Value:\"");
        skullammo2.set(false, "skull_owner_custom_url",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTg3ZmRmNDU4N2E2NDQ5YmZjOGJlMzNhYjJlOTM4ZTM2YmYwNWU0MGY2ZmFhMjc3ZDcxYjUwYmNiMGVhNjgzOCJ9fX0=");

    }

    public String getIngString(final Material m, final int durability, final int amount) {
        return m.toString() + "," + durability + "," + amount;
    }
}
