package me.zombie_striker.qg.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.qg.ammo.Ammo;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.guns.utils.WeaponType;

public class GunYMLCreator {

    public static GunYML createNewDefaultGun(final File dataFolder, final String name, final String displayname, final int id,
            final List<String> craftingRequirements, final WeaponType weapontype, final WeaponSounds ws, final boolean enableIronSights,
            final String ammotype, final int damage, final int maxBullets, final int cost) {
        return GunYMLCreator.createNewCustomGun(dataFolder, "default_" + name, name, displayname, id, craftingRequirements, weapontype, ws,
                enableIronSights, ammotype, damage, maxBullets, cost);
    }

    public static GunYML createNewCustomGun(final File dataFolder, final String filename, final String name, final String displayname,
            final int id, final List<String> craftingRequirements, final WeaponType weapontype, final WeaponSounds ws,
            final boolean enableIronSights, final String ammotype, final int damage, final int maxBullets, final int cost) {

        final File f2 = new File(dataFolder, "newGuns/" + filename + ".yml");
        if (!new File(dataFolder, "newGuns").exists())
            new File(dataFolder, "newGuns").mkdirs();

        final GunYML h = new GunYML(f2);

        h.set(false, "name", name);
        h.set(false, "displayname", displayname.startsWith("&") ? displayname : "&6" + displayname);

        h.set(false, "id", id);
        h.set(false, "variant", 0);
        h.set(false, "craftingRequirements", craftingRequirements);
        h.set(false, "weapontype", weapontype.name());
        h.set(false, "weaponsounds", ws != null ? ws.getSoundName() : WeaponSounds.getSoundByType(weapontype));
        final StringBuilder validGuns = new StringBuilder();
        for (final WeaponType g : WeaponType.values()) {
            validGuns.append(g.name() + ", ");
        }
        // h.set(false, "_VALID_WEAPON_TYPES", validGuns.toString());
        h.set(false, "enableIronSights", enableIronSights);
        h.set(false, "ammotype", ammotype);
        h.set(false, "damage", damage);
        h.set(false, "maxbullets", maxBullets);

        h.set(false, "price", cost);

        return h;
    }

    public static ArmoryYML createAmmo(final boolean forceUpdate, final File dataFolder, final boolean invalid, final String name,
            final String displayname, final Material type, final int id, final List<String> craftingRequirements, final int cost,
            final double severity, final int maxAmount) {
        return GunYMLCreator.createAmmo(forceUpdate, dataFolder, invalid, "default_" + name, name, displayname, null, type, id,
                craftingRequirements, cost, severity, maxAmount);
    }

    public static ArmoryYML createAmmo(final boolean forceUpdate, final File dataFolder, final boolean invalid, final String name,
            final String displayname, final Material type, final int id, final List<String> craftingRequirements, final int cost,
            final double severity, final int maxAmount, final int returnamount) {
        return GunYMLCreator.createAmmo(forceUpdate, dataFolder, invalid, "default_" + name, name, displayname, null, type, id,
                craftingRequirements, cost, severity, maxAmount, returnamount);
    }

    public static ArmoryYML createAmmo(final boolean forceUpdate, final File dataFolder, final boolean invalid, final String filename,
            final String name, final String displayname, final List<String> lore, final Material type, final int id,
            final List<String> craftingRequirements, final int cost, final double severity, final int maxAmount) {
        return GunYMLCreator.createAmmo(forceUpdate, dataFolder, invalid, filename, name, displayname, lore, type, id, craftingRequirements,
                cost, severity, maxAmount, maxAmount);
    }

    public static ArmoryYML createAmmo(final boolean forceUpdate, final File dataFolder, final boolean invalid, final String filename,
            final String name, final String displayname, final List<String> lore, final Material type, final int id,
            final List<String> craftingRequirements, final int cost, final double severity, final int maxAmount, final int returnamount) {
        return GunYMLCreator.createSkullAmmo(forceUpdate, dataFolder, invalid, filename, name, displayname, lore, type, id, null,
                craftingRequirements, cost, severity, maxAmount, returnamount);
    }

    public static ArmoryYML createSkullAmmo(final boolean forceUpdate, final File dataFolder, final boolean invalid, final String filename,
            final String name, final String displayname, final List<String> lore, final Material type, final int id,
            final String SKULL_OWNER, final List<String> craftingRequirements, final int cost, final double severity, final int maxAmount) {
        return GunYMLCreator.createSkullAmmo(forceUpdate, dataFolder, invalid, filename, name, displayname, lore, type, id, SKULL_OWNER,
                craftingRequirements, cost, severity, maxAmount, maxAmount);
    }

    public static ArmoryYML createSkullAmmo(final boolean forceUpdate, final File dataFolder, final boolean invalid, final String filename,
            final String name, final String displayname, final List<String> lore, final Material type, final int id,
            final String SKULL_OWNER, final List<String> craftingRequirements, final int cost, final double severity, final int maxAmount,
            final int craftingReturn) {
        final File f2 = new File(dataFolder, "ammo/" + filename + ".yml");
        if (!new File(dataFolder, "ammo").exists())
            new File(dataFolder, "ammo").mkdirs();

        final ArmoryYML h = new ArmoryYML(f2);

        h.set(invalid, "invalid", invalid);
        h.set(invalid, "name", name);
        h.set(invalid, "displayname", displayname);
        h.set(invalid, "lore", (lore == null ? new ArrayList<String>() : lore));
        h.set(invalid, "id", id);
        h.set(invalid, "craftingRequirements", craftingRequirements);
        h.set(invalid, "craftingReturnAmount", craftingReturn);
        h.set(invalid, "price", cost);
        h.set(invalid, "maxItemStack", maxAmount);
        h.set(invalid, "material", type.name());

        if (SKULL_OWNER != null) {
            h.set(invalid, "skull_owner", SKULL_OWNER);
            h.set(invalid, "skull_owner_custom_url", Ammo.NO_SKIN_STRING);
        }

        h.set(invalid, "piercingSeverity", severity);
        if (h.saveNow)
            h.save();
        return h;
    }

    public static AttachmentYML createAttachment(final boolean forceUpdate, final File dataFolder, final boolean invalid,
            final String filename, final String name, final String displayname, final List<String> lore, final MaterialStorage ms,
            final List<String> craftingRequirements, final int cost, final Gun originalGun) {
        return GunYMLCreator.createAttachment(forceUpdate, dataFolder, invalid, filename, name, displayname, lore, ms, craftingRequirements,
                cost, originalGun.getName());
    }

    public static AttachmentYML createAttachment(final boolean forceUpdate, final File dataFolder, final boolean invalid,
            final String filename, final String name, final String displayname, final List<String> lore, final MaterialStorage ms,
            final List<String> craftingRequirements, final int cost, final String originalGun) {
        final File f2 = new File(dataFolder, "attachments/" + filename + ".yml");
        if (!new File(dataFolder, "attachments").exists())
            new File(dataFolder, "attachments").mkdirs();
        final AttachmentYML h = new AttachmentYML(f2);
        if (invalid)
            h.set(false, "HOW_TO_USE",
                    "Below is just the required values to create a new attachment for the 'basegun'. If you want to modify more parts of the gun, copy the value you want to change from the 'base' gun and paste it here with the value you want.");

        h.set(invalid, "invalid", invalid);
        h.set(invalid, "name", name);
        h.set(invalid, "displayname", displayname.startsWith("&") ? displayname : "&6" + displayname);
        h.set(invalid, "lore", (lore == null ? new ArrayList<String>() : lore));
        h.set(invalid, "id", ms.getData());
        h.set(invalid, "craftingRequirements", craftingRequirements);
        h.set(invalid, "price", cost);
        h.set(invalid, "material", ms.getMat().name());

        h.set(invalid, "baseGun", originalGun);
        if (!invalid) {

        }
        if (h.saveNow)
            h.save();
        return h;

    }

    public static ArmorYML createDefaultArmor(final File dataFolder, final boolean invalid, final String name, final String displayname,
            final List<String> lore, final int id, final List<String> craftingRequirements, final int cost, final WeaponType misctype,
            final double min, final double max, final boolean stopHeadshots) {
        return GunYMLCreator.createArmor(false, dataFolder, invalid, "default_" + name, name, "&6" + displayname, lore, id,
                craftingRequirements, cost, misctype, min, max, stopHeadshots);
    }

    public static ArmorYML createArmor(final boolean forceUpdate, final File dataFolder, final boolean invalid, final String filename,
            final String name, final String displayname, final List<String> lore, final int id, final List<String> craftingRequirements,
            final int cost, final WeaponType misctype, final double min, final double max, final boolean stopHeadshots) {
        final File f2 = new File(dataFolder, "armor/" + filename + ".yml");
        if (!new File(dataFolder, "armor").exists())
            new File(dataFolder, "armor").mkdirs();
        final ArmorYML h = new ArmorYML(f2);

        h.set(invalid, "invalid", invalid);
        h.set(invalid, "name", name);
        h.set(invalid, "displayname", displayname);
        h.set(invalid, "lore", (lore == null ? new ArrayList<String>() : lore));
        h.set(invalid, "id", id);
        h.set(invalid, "craftingRequirements", craftingRequirements);
        h.set(invalid, "price", cost);
        // h.setNoSave(false, "material", type.name());

        h.set(invalid, "MiscType", misctype.name());

        h.set("minProtectionHeight", min);
        h.set("maxProtectionHeight", max);
        h.set("stopsHeadshots", stopHeadshots);
        if (h.saveNow)
            h.save();
        return h;
    }

    public static MiscYML createMisc(final boolean forceUpdate, final File dataFolder, final boolean invalid, final String filename,
            final String name, final String displayname, final List<String> lore, final MaterialStorage ms,
            final List<String> craftingRequirements, final int cost, final WeaponType misctype, final int damage, final int durability) {
        return GunYMLCreator.createMisc(forceUpdate, dataFolder, invalid, filename, name, displayname, lore, ms.getMat(), ms.getData(),
                craftingRequirements, cost, misctype, damage, durability);
    }

    public static MiscYML createMisc(final boolean forceUpdate, final File dataFolder, final boolean invalid, final String filename,
            final String name, final String displayname, final List<String> lore, final Material type, final int id,
            final List<String> craftingRequirements, final int cost, final WeaponType misctype, final int damage, final int durability) {
        final File f2 = new File(dataFolder, "misc/" + filename + ".yml");
        if (!new File(dataFolder, "misc").exists())
            new File(dataFolder, "misc").mkdirs();
        final MiscYML h = new MiscYML(f2);
        h.set(invalid, "invalid", invalid);
        h.set(invalid, "name", name);
        h.set(invalid, "displayname", displayname);
        h.set(invalid, "lore", (lore == null ? new ArrayList<String>() : lore));
        h.set(invalid, "id", id);
        h.set(invalid, "craftingRequirements", craftingRequirements);
        h.set(invalid, "price", cost);
        h.set(invalid, "material", type.name());

        h.set(invalid, "damage", damage);
        h.set(invalid, "durability", durability);

        h.setMiscType(misctype);
        if (h.saveNow)
            h.save();
        return h;
    }
}
