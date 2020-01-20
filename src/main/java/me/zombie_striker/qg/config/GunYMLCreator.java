package me.zombie_striker.qg.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.qg.ammo.Ammo;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.guns.utils.WeaponType;

public class GunYMLCreator {

	public static GunYML createNewDefaultGun(File dataFolder, String name, String displayname, int id,
			List<String> craftingRequirements, WeaponType weapontype, WeaponSounds ws, boolean enableIronSights,
			String ammotype, int damage, int maxBullets, int cost) {
		return createNewCustomGun(dataFolder, "default_" + name, name, displayname, id, craftingRequirements,
				weapontype, ws, enableIronSights, ammotype, damage, maxBullets, cost);
	}

	public static GunYML createNewCustomGun(File dataFolder, String filename, String name, String displayname, int id,
			List<String> craftingRequirements, WeaponType weapontype, WeaponSounds ws, boolean enableIronSights,
			String ammotype, int damage, int maxBullets, int cost) {

		File f2 = new File(dataFolder, "newGuns/" + filename + ".yml");
		if (!new File(dataFolder, "newGuns").exists())
			new File(dataFolder, "newGuns").mkdirs();

		GunYML h = new GunYML(f2);

		
		h.set(false, "name", name);
		h.set(false, "displayname", displayname.startsWith("&") ? displayname : "&6" + displayname);

		h.set(false, "id", id);
		h.set(false, "variant", 0);
		h.set(false, "craftingRequirements", craftingRequirements);
		h.set(false, "weapontype", weapontype.name());
		h.set(false, "weaponsounds", ws != null ? ws.getSoundName() : WeaponSounds.getSoundByType(weapontype));
		StringBuilder validGuns = new StringBuilder();
		for (WeaponType g : WeaponType.values()) {
			validGuns.append(g.name() + ", ");
		}
		h.set(false, "_VALID_WEAPON_TYPES", validGuns.toString());
		h.set(false, "enableIronSights", enableIronSights);
		h.set(false, "ammotype", ammotype);
		h.set(false, "damage", damage);
		h.set(false, "maxbullets", maxBullets);

		h.set(false, "price", cost);

		return h;
	}

	public static ArmoryYML createNewGun(boolean forceUpdate, File dataFolder, boolean invalid, String filename,
			String name, String displayname, List<String> lore, int id, List<String> craftingRequirements,
			WeaponType weapontype, boolean enableIronSights, String ammotype, int damage, double sway, Material type,
			int maxBullets, int duribility, double delayReload, double delayShoot, int bulletspershot,
			boolean isAutomatic, int cost, String ch, int distance, int var, boolean version18, WeaponSounds ws,
			String particle, double particleR, double particleG, double particleB, boolean addMuzzleSmoke) {
		File f2 = new File(dataFolder, "newGuns/" + filename + ".yml");
		if (!new File(dataFolder, "newGuns").exists())
			new File(dataFolder, "newGuns").mkdirs();

		ArmoryYML h = new ArmoryYML(f2);
		h.set(invalid, "invalid", invalid);
		h.set(invalid, "name", name);
		h.set(invalid, "displayname", displayname);
		h.set(invalid, "lore", (lore == null ? new ArrayList<String>() : lore));
		h.set(invalid, "material", type.name());
		h.set(invalid, "id", id);
		h.set(invalid, "variant", var);
		h.set(invalid, "craftingRequirements", craftingRequirements);
		h.set(invalid, "weapontype", weapontype.name());
		h.set(invalid, "weaponsounds", ws != null ? ws.getSoundName() : WeaponSounds.getSoundByType(weapontype));
		StringBuilder validGuns = new StringBuilder();
		for (WeaponType g : WeaponType.values()) {
			validGuns.append(g.name() + ", ");
		}
		h.set(invalid, "_VALID_WEAPON_TYPES", validGuns.toString());
		h.set(invalid, "enableIronSights", enableIronSights);
		h.set(invalid, "ammotype", ammotype);
		h.set(invalid, "damage", damage);
		h.set(invalid, "sway", sway);
		h.set(invalid, "maxbullets", maxBullets);
		h.set(invalid, "durability", duribility);
		h.set(invalid, "delayForReload", delayReload);
		h.set(invalid, "delayForShoot", delayShoot);
		h.set(invalid, "bullets-per-shot", bulletspershot);
		h.set(invalid, "isAutomatic", isAutomatic);
		h.set(invalid, "price", cost);
		h.set(invalid, "maxBulletDistance", distance);
		h.set(invalid, "unlimitedAmmo", false);
		h.set(invalid, "LightLeveOnShoot", 14);

		h.set(invalid, "particles.bullet_particle", particle);
		if (particle.equals("REDSTONE")) {
			h.set(invalid, "particles.bullet_particleR", particleR);
			h.set(invalid, "particles.bullet_particleG", particleG);
			h.set(invalid, "particles.bullet_particleB", particleB);
		}

		if (version18)
			h.set(invalid, "Version_18_Support", version18);
		h.set(invalid, "ChargingHandler", ch == null ? "null" : ch);
		if (addMuzzleSmoke)
			h.set(invalid, "addMuzzleSmoke", addMuzzleSmoke);

		if (invalid) {
			h.set(invalid, "drop-glow-color", ChatColor.WHITE.name());
		}
		if (h.saveNow)
			h.save();
		return h;
	}

	public static ArmoryYML createAmmo(boolean forceUpdate, File dataFolder, boolean invalid, String name,
			String displayname,  Material type, int id, List<String> craftingRequirements, int cost, double severity, int maxAmount) {
		return createAmmo(forceUpdate, dataFolder, invalid, "default_" + name, name, displayname, null,
				type, id, craftingRequirements, cost, severity, maxAmount);
	}

	public static ArmoryYML createAmmo(boolean forceUpdate, File dataFolder, boolean invalid, String name,
			String displayname,  Material type, int id, List<String> craftingRequirements, int cost, double severity, int maxAmount,
			int returnamount) {
		return createAmmo(forceUpdate, dataFolder, invalid, "default_" + name, name, displayname, null,
			type, id, craftingRequirements, cost, severity, maxAmount, returnamount);
	}

	/*public static ArmoryYML createAmmo(boolean forceUpdate, File dataFolder, boolean invalid, String filename,
			String name, String displayname, int id, List<String> craftingRequirements, int cost, double severity,
			int maxAmount) {
		return createAmmo(forceUpdate, dataFolder, invalid, filename, name, displayname, null, Material.DIAMOND_AXE, id,
				craftingRequirements, cost, severity, maxAmount);
	}*/

	public static ArmoryYML createAmmo(boolean forceUpdate, File dataFolder, boolean invalid, String filename,
			String name, String displayname, List<String> lore, Material type, int id,
			List<String> craftingRequirements, int cost, double severity, int maxAmount) {
		return createAmmo(forceUpdate, dataFolder, invalid, filename, name, displayname, lore, type, id,
				craftingRequirements, cost, severity, maxAmount, maxAmount);
	}

	public static ArmoryYML createAmmo(boolean forceUpdate, File dataFolder, boolean invalid, String filename,
			String name, String displayname, List<String> lore, Material type, int id,
			List<String> craftingRequirements, int cost, double severity, int maxAmount, int returnamount) {
		return createSkullAmmo(forceUpdate, dataFolder, invalid, filename, name, displayname, lore, type, id, null,
				craftingRequirements, cost, severity, maxAmount, returnamount);
	}

	public static ArmoryYML createSkullAmmo(boolean forceUpdate, File dataFolder, boolean invalid, String filename,
			String name, String displayname, List<String> lore, Material type, int id, String SKULL_OWNER,
			List<String> craftingRequirements, int cost, double severity, int maxAmount) {
		return createSkullAmmo(forceUpdate, dataFolder, invalid, filename, name, displayname, lore, type, id,
				SKULL_OWNER, craftingRequirements, cost, severity, maxAmount, maxAmount);
	}

	public static ArmoryYML createSkullAmmo(boolean forceUpdate, File dataFolder, boolean invalid, String filename,
			String name, String displayname, List<String> lore, Material type, int id, String SKULL_OWNER,
			List<String> craftingRequirements, int cost, double severity, int maxAmount, int craftingReturn) {
		File f2 = new File(dataFolder, "ammo/" + filename + ".yml");
		if (!new File(dataFolder, "ammo").exists())
			new File(dataFolder, "ammo").mkdirs();

		ArmoryYML h = new ArmoryYML(f2);

		h.set(invalid, "invalid", invalid);
		h.set(invalid, "name", name);
		h.set(invalid, "displayname", displayname);
		h.set(invalid, "lore", (lore == null ? new ArrayList<String>() : lore));
		h.set(invalid, "id", id);
		h.set(invalid, "craftingRequirements", craftingRequirements);
		h.set(invalid, "craftingReturnAmount", craftingReturn);
		h.set(invalid, "price", cost);
		h.set(invalid, "maxAmount", maxAmount);
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

	public static GunYML createAttachment(boolean forceUpdate, File dataFolder, boolean invalid, String filename,
			String name, String displayname, List<String> lore, MaterialStorage ms, List<String> craftingRequirements,
			int cost, Gun originalGun) {
		return createAttachment(forceUpdate, dataFolder, invalid, filename, name, displayname, lore, ms,
				craftingRequirements, cost, originalGun.getName());
	}

	public static GunYML createAttachment(boolean forceUpdate, File dataFolder, boolean invalid, String filename,
			String name, String displayname, List<String> lore, MaterialStorage ms, List<String> craftingRequirements,
			int cost, String originalGun) {
		File f2 = new File(dataFolder, "attachments/" + filename + ".yml");
		if (!new File(dataFolder, "attachments").exists())
			new File(dataFolder, "attachments").mkdirs();
		GunYML h = new GunYML(f2);
		if (invalid)
			h.set(false, "HOW_TO_USE",
					"Below is just the required values to create a new attachment for the 'basegun'. If you want to modify more parts of the gun, copy the value you want to change from the 'base' gun and paste it here with the value you want.");

		h.set(invalid, "invalid", invalid);
		h.set(invalid, "name", name);
		h.set(invalid, "displayname", displayname);
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

	public static ArmorYML createDefaultArmor(File dataFolder, boolean invalid, String name, String displayname,
			List<String> lore, int id, List<String> craftingRequirements, int cost, WeaponType misctype, double min,
			double max, boolean stopHeadshots) {
		return createArmor(false, dataFolder, invalid, "default_" + name, name, "&6" + displayname, lore, id,
				craftingRequirements, cost, misctype, min, max, stopHeadshots);
	}

	public static ArmorYML createArmor(boolean forceUpdate, File dataFolder, boolean invalid, String filename,
			String name, String displayname, List<String> lore, int id, List<String> craftingRequirements, int cost,
			WeaponType misctype, double min, double max, boolean stopHeadshots) {
		File f2 = new File(dataFolder, "armor/" + filename + ".yml");
		if (!new File(dataFolder, "armor").exists())
			new File(dataFolder, "armor").mkdirs();
		ArmorYML h = new ArmorYML(f2);

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

	public static ArmoryYML createMisc(boolean forceUpdate, File dataFolder, boolean invalid, String filename,
			String name, String displayname, List<String> lore, MaterialStorage ms, List<String> craftingRequirements,
			int cost, WeaponType misctype, int damage, int durability) {
		return createMisc(forceUpdate, dataFolder, invalid, filename, name, displayname, lore, ms.getMat(),
				ms.getData(), craftingRequirements, cost, misctype, damage, durability);
	}

	public static ArmoryYML createMisc(boolean forceUpdate, File dataFolder, boolean invalid, String filename,
			String name, String displayname, List<String> lore, Material type, int id,
			List<String> craftingRequirements, int cost, WeaponType misctype, int damage, int durability) {
		File f2 = new File(dataFolder, "misc/" + filename + ".yml");
		if (!new File(dataFolder, "misc").exists())
			new File(dataFolder, "misc").mkdirs();
		ArmoryYML h = new ArmoryYML(f2);
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

		h.set(invalid, "MiscType", misctype.name());
		if (h.saveNow)
			h.save();
		return h;
	}
}
