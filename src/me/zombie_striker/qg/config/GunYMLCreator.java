package me.zombie_striker.qg.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import me.zombie_striker.qg.MaterialStorage;
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
				
		h.setNoSave(false, "AllowUserModifications",
				(h.contains("allowUpdates") ? (!(boolean) h.get("allowUpdates")) : false));
		
		h.setNoSave(false, "name", name);
		h.setNoSave(false, "displayname", displayname.startsWith("&") ? displayname : "&6" + displayname);

		h.setNoSave(false, "id", id);
		h.setNoSave(false, "variant", 0);
		h.setNoSave(false, "craftingRequirements", craftingRequirements);
		h.setNoSave(false, "weapontype", weapontype.name());
		h.setNoSave(false, "weaponsounds", ws != null ? ws.getSoundName() : WeaponSounds.getSoundByType(weapontype));
		StringBuilder validGuns = new StringBuilder();
		for (WeaponType g : WeaponType.values()) {
			validGuns.append(g.name() + ", ");
		}
		h.setNoSave(false, "_VALID_WEAPON_TYPES", validGuns.toString());
		h.setNoSave(false, "enableIronSights", enableIronSights);
		h.setNoSave(false, "ammotype", ammotype);
		h.setNoSave(false, "damage", damage);
		h.setNoSave(false, "maxbullets", maxBullets);

		h.setNoSave(false, "price", cost);

		return h;
	}

	public static ArmoryYML createNewGun(boolean forceUpdate, File dataFolder, String name, String displayname, int id,
			List<String> craftingRequirements, WeaponType weapontype, boolean enableIronSights, String ammotype,
			int damage, double sway, int maxBullets, int duribility, double delayReload, double delayShoot,
			int bulletspershot, boolean isAutomatic, int cost, String ch, int distance, WeaponSounds ws) {
		return createNewGun(forceUpdate, dataFolder, false, name, name, displayname, null, id, craftingRequirements,
				weapontype, enableIronSights, ammotype, damage, sway, Material.DIAMOND_AXE, maxBullets, duribility,
				delayReload, delayShoot, bulletspershot, isAutomatic, cost, ch, distance, false, ws);
	}

	public static ArmoryYML createNewGun(boolean forceUpdate, File dataFolder, String name, String displayname, int id,
			List<String> craftingRequirements, WeaponType weapontype, boolean enableIronSights, String ammotype,
			int damage, double sway, int maxBullets, int duribility, int bulletspershot, boolean isAutomatic, int cost,
			String ch, int distance, WeaponSounds ws) {
		return createNewGun(forceUpdate, dataFolder, name, displayname, id, craftingRequirements, weapontype,
				enableIronSights, ammotype, damage, sway, maxBullets, duribility, 1.5, 0.25, bulletspershot,
				isAutomatic, cost, ch, distance, ws);
	}

	public static ArmoryYML createNewGun(boolean forceUpdate, File dataFolder, String name, String displayname, int id,
			List<String> craftingRequirements, WeaponType weapontype, boolean enableIronSights, String ammotype,
			int damage, double sway, int maxBullets, int duribility, boolean isAutomatic, int cost, String ch,
			int distance, WeaponSounds ws) {
		return createNewGun(forceUpdate, dataFolder, name, displayname, id, craftingRequirements, weapontype,
				enableIronSights, ammotype, damage, sway, maxBullets, duribility, 1, isAutomatic, cost, ch, distance,
				ws);
	}

	// Displaynames above
	// non- below
	public static ArmoryYML createNewGun(boolean forceUpdate, File dataFolder, String name, int id,
			List<String> craftingRequirements, WeaponType weapontype, boolean enableIronSights, String ammotype,
			int damage, double sway, int maxBullets, int duribility, double delayReload, double delayShoot,
			int bulletspershot, boolean isAutomatic, int cost, String ch, int distance, WeaponSounds ws) {
		return createNewGun(forceUpdate, dataFolder, false, "default_" + name, name,
				"&" + ChatColor.GOLD.getChar() + name, null, id, craftingRequirements, weapontype, enableIronSights,
				ammotype, damage, sway, Material.DIAMOND_AXE, maxBullets, duribility, delayReload, delayShoot,
				bulletspershot, isAutomatic, cost, ch, distance, false, ws);
	}

	public static ArmoryYML createNewGun(boolean forceUpdate, File dataFolder, String name, int id,
			List<String> craftingRequirements, WeaponType weapontype, boolean enableIronSights, String ammotype,
			int damage, double sway, int maxBullets, int duribility, int bulletspershot, boolean isAutomatic, int cost,
			String ch, int distance, WeaponSounds ws) {
		return createNewGun(forceUpdate, dataFolder, name, id, craftingRequirements, weapontype, enableIronSights,
				ammotype, damage, sway, maxBullets, duribility, 1.5, 0.25, bulletspershot, isAutomatic, cost, ch,
				distance, ws);
	}

	public static ArmoryYML createNewGun(boolean forceUpdate, File dataFolder, String name, int id,
			List<String> craftingRequirements, WeaponType weapontype, boolean enableIronSights, String ammotype,
			int damage, double sway, int maxBullets, int duribility, boolean isAutomatic, int cost, String ch,
			int distance, WeaponSounds ws) {
		return createNewGun(forceUpdate, dataFolder, name, id, craftingRequirements, weapontype, enableIronSights,
				ammotype, damage, sway, maxBullets, duribility, 1, isAutomatic, cost, ch, distance, ws);
	}

	public static ArmoryYML createNewGun(boolean forceUpdate, File dataFolder, boolean invalid, String filename,
			String name, String displayname, List<String> lore, int id, List<String> craftingRequirements,
			WeaponType weapontype, boolean enableIronSights, String ammotype, int damage, double sway, Material type,
			int maxBullets, int duribility, double delayReload, double delayShoot, int bulletspershot,
			boolean isAutomatic, int cost, String ch, int distance, boolean version18, WeaponSounds ws) {
		return createNewGun(forceUpdate, dataFolder, invalid, filename, name, displayname, lore, id,
				craftingRequirements, weapontype, enableIronSights, ammotype, damage, sway, type, maxBullets,
				duribility, delayReload, delayShoot, bulletspershot, isAutomatic, cost, ch, distance, 0, version18, ws);
	}

	public static ArmoryYML createNewGun(boolean forceUpdate, File dataFolder, boolean invalid, String filename,
			String name, String displayname, List<String> lore, int id, List<String> craftingRequirements,
			WeaponType weapontype, boolean enableIronSights, String ammotype, int damage, double sway, Material type,
			int maxBullets, int duribility, double delayReload, double delayShoot, int bulletspershot,
			boolean isAutomatic, int cost, String ch, int distance, int var, boolean version18, WeaponSounds ws) {

		double particlecolorGB = weapontype == WeaponType.LAZER ? 0.0 : 1.0;

		return createNewGun(forceUpdate, dataFolder, invalid, filename, name, displayname, lore, id,
				craftingRequirements, weapontype, enableIronSights, ammotype, damage, sway, type, maxBullets,
				duribility, delayReload, delayShoot, bulletspershot, isAutomatic, cost, ch, distance, var, version18,
				ws, "REDSTONE", 1.0, particlecolorGB, particlecolorGB);
	}

	public static ArmoryYML createNewGun(boolean forceUpdate, File dataFolder, boolean invalid, String filename,
			String name, String displayname, List<String> lore, int id, List<String> craftingRequirements,
			WeaponType weapontype, boolean enableIronSights, String ammotype, int damage, double sway, Material type,
			int maxBullets, int duribility, double delayReload, double delayShoot, int bulletspershot,
			boolean isAutomatic, int cost, String ch, int distance, int var, boolean version18, WeaponSounds ws,
			String particle, double particleR, double particleG, double particleB) {
		return createNewGun(forceUpdate, dataFolder, invalid, filename, name, displayname, lore, id,
				craftingRequirements, weapontype, enableIronSights, ammotype, damage, sway, type, maxBullets,
				duribility, delayReload, delayShoot, bulletspershot, isAutomatic, cost, ch, distance, var, version18,
				ws, particle, particleR, particleG, particleB, false);
	}

	public static ArmoryYML createNewGun(boolean forceUpdate, File dataFolder, boolean invalid, String name,
			String displayname, List<String> lore, int id, List<String> craftingRequirements, WeaponType weapontype,
			boolean enableIronSights, String ammotype, int damage, double sway, Material type, int maxBullets,
			int duribility, boolean isAutomatic, int cost, String ch, int distance, int var, boolean version18,
			WeaponSounds ws, String particle, double particleR, double particleG, double particleB) {
		return createNewGun(forceUpdate, dataFolder, invalid, "default_" + name, name, displayname, lore, id,
				craftingRequirements, weapontype, enableIronSights, ammotype, damage, sway, type, maxBullets,
				duribility, 1.5, 0.3, 1, isAutomatic, cost, ch, distance, var, version18, ws, particle, particleR,
				particleG, particleB, false);
	}

	public static ArmoryYML createNewGun(boolean forceUpdate, File dataFolder, boolean invalid, String name,
			String displayname, List<String> lore, int id, List<String> craftingRequirements, WeaponType weapontype,
			boolean enableIronSights, String ammotype, int damage, double sway, Material type, int maxBullets,
			int duribility, double delayReload, double delayShoot, int bulletspershot, boolean isAutomatic, int cost,
			String ch, int distance, int var, boolean version18, WeaponSounds ws, String particle, double particleR,
			double particleG, double particleB) {
		return createNewGun(forceUpdate, dataFolder, invalid, "default_" + name, name, displayname, lore, id,
				craftingRequirements, weapontype, enableIronSights, ammotype, damage, sway, type, maxBullets,
				duribility, delayReload, delayShoot, bulletspershot, isAutomatic, cost, ch, distance, var, version18,
				ws, particle, particleR, particleG, particleB, false);
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
		h.setNoSave(invalid, "AllowUserModifications",
				(h.contains("allowUpdates") ? (!(boolean) h.get("allowUpdates")) : false));
		h.setNoSave(invalid, "invalid", invalid);
		h.setNoSave(invalid, "name", name);
		h.setNoSave(invalid, "displayname", displayname);
		h.setNoSave(invalid, "lore", (lore == null ? new ArrayList<String>() : lore));
		h.setNoSave(invalid, "material", type.name());
		h.setNoSave(invalid, "id", id);
		h.setNoSave(invalid, "variant", var);
		h.setNoSave(invalid, "craftingRequirements", craftingRequirements);
		h.setNoSave(invalid, "weapontype", weapontype.name());
		h.setNoSave(invalid, "weaponsounds", ws != null ? ws.getSoundName() : WeaponSounds.getSoundByType(weapontype));
		StringBuilder validGuns = new StringBuilder();
		for (WeaponType g : WeaponType.values()) {
			validGuns.append(g.name() + ", ");
		}
		h.setNoSave(invalid, "_VALID_WEAPON_TYPES", validGuns.toString());
		h.setNoSave(invalid, "enableIronSights", enableIronSights);
		h.setNoSave(invalid, "ammotype", ammotype);
		h.setNoSave(invalid, "damage", damage);
		h.setNoSave(invalid, "sway", sway);
		h.setNoSave(invalid, "maxbullets", maxBullets);
		h.setNoSave(invalid, "durability", duribility);
		h.setNoSave(invalid, "delayForReload", delayReload);
		h.setNoSave(invalid, "delayForShoot", delayShoot);
		h.setNoSave(invalid, "bullets-per-shot", bulletspershot);
		h.setNoSave(invalid, "isAutomatic", isAutomatic);
		h.setNoSave(invalid, "price", cost);
		h.setNoSave(invalid, "maxBulletDistance", distance);
		h.setNoSave(invalid, "unlimitedAmmo", false);
		h.setNoSave(invalid, "LightLeveOnShoot", 14);

		h.setNoSave(invalid, "particles.bullet_particle", particle);
		if (particle.equals("REDSTONE")) {
			h.setNoSave(invalid, "particles.bullet_particleR", particleR);
			h.setNoSave(invalid, "particles.bullet_particleG", particleG);
			h.setNoSave(invalid, "particles.bullet_particleB", particleB);
		}

		if (version18)
			h.setNoSave(invalid, "Version_18_Support", version18);
		h.setNoSave(invalid, "ChargingHandler", ch == null ? "null" : ch);
		if (addMuzzleSmoke)
			h.setNoSave(invalid, "addMuzzleSmoke", addMuzzleSmoke);

		if (invalid) {
			h.setNoSave(invalid, "drop-glow-color", ChatColor.WHITE.name());
		}
		if (h.saveNow)
			h.save();
		return h;
	}

	public static ArmoryYML createAmmo(boolean forceUpdate, File dataFolder, boolean invalid, String name,
			String displayname, int id, List<String> craftingRequirements, int cost, double severity, int maxAmount) {
		return createAmmo(forceUpdate, dataFolder, invalid, "default_" + name, name, displayname, null,
				Material.DIAMOND_AXE, id, craftingRequirements, cost, severity, maxAmount);
	}

	public static ArmoryYML createAmmo(boolean forceUpdate, File dataFolder, boolean invalid, String name,
			String displayname, int id, List<String> craftingRequirements, int cost, double severity, int maxAmount,
			int returnamount) {
		return createAmmo(forceUpdate, dataFolder, invalid, "default_" + name, name, displayname, null,
				Material.DIAMOND_AXE, id, craftingRequirements, cost, severity, maxAmount, returnamount);
	}

	public static ArmoryYML createAmmo(boolean forceUpdate, File dataFolder, boolean invalid, String filename,
			String name, String displayname, int id, List<String> craftingRequirements, int cost, double severity,
			int maxAmount) {
		return createAmmo(forceUpdate, dataFolder, invalid, filename, name, displayname, null, Material.DIAMOND_AXE, id,
				craftingRequirements, cost, severity, maxAmount);
	}

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

		h.setNoSave(invalid, "AllowUserModifications",
				(h.contains("allowUpdates") ? (!(boolean) h.get("allowUpdates")) : false));
		h.setNoSave(invalid, "invalid", invalid);
		h.setNoSave(invalid, "name", name);
		h.setNoSave(invalid, "displayname", displayname);
		h.setNoSave(invalid, "lore", (lore == null ? new ArrayList<String>() : lore));
		h.setNoSave(invalid, "id", id);
		h.setNoSave(invalid, "craftingRequirements", craftingRequirements);
		h.setNoSave(invalid, "craftingReturnAmount", craftingReturn);
		h.setNoSave(invalid, "price", cost);
		h.setNoSave(invalid, "maxAmount", maxAmount);
		h.setNoSave(invalid, "material", type.name());

		if (SKULL_OWNER != null) {
			h.setNoSave(invalid, "skull_owner", SKULL_OWNER);
			h.setNoSave(invalid, "skull_owner_custom_url", Ammo.NO_SKIN_STRING);
		}

		h.setNoSave(invalid, "piercingSeverity", severity);
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
			h.setNoSave(false, "HOW_TO_USE",
					"Below is just the required values to create a new attachment for the 'basegun'. If you want to modify more parts of the gun, copy the value you want to change from the 'base' gun and paste it here with the value you want.");

		h.setNoSave(invalid, "AllowUserModifications",
				(h.contains("allowUpdates") ? (!(boolean) h.get("allowUpdates")) : false));
		h.setNoSave(invalid, "invalid", invalid);
		h.setNoSave(invalid, "name", name);
		h.setNoSave(invalid, "displayname", displayname);
		h.setNoSave(invalid, "lore", (lore == null ? new ArrayList<String>() : lore));
		h.setNoSave(invalid, "id", ms.getData());
		h.setNoSave(invalid, "craftingRequirements", craftingRequirements);
		h.setNoSave(invalid, "price", cost);
		h.setNoSave(invalid, "material", ms.getMat().name());

		h.setNoSave(invalid, "baseGun", originalGun);
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
		h.setNoSave(invalid, "AllowUserModifications",
				(h.contains("allowUpdates") ? (!(boolean) h.get("allowUpdates")) : false));
		h.setNoSave(invalid, "invalid", invalid);
		h.setNoSave(invalid, "name", name);
		h.setNoSave(invalid, "displayname", displayname);
		h.setNoSave(invalid, "lore", (lore == null ? new ArrayList<String>() : lore));
		h.setNoSave(invalid, "id", id);
		h.setNoSave(invalid, "craftingRequirements", craftingRequirements);
		h.setNoSave(invalid, "price", cost);
		// h.setNoSave(false, "material", type.name());

		h.setNoSave(invalid, "MiscType", misctype.name());

		h.setNoOverride("minProtectionHeight", min);
		h.setNoOverride("maxProtectionHeight", max);
		h.setNoOverride("stopsHeadshots", stopHeadshots);
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
		h.setNoSave(invalid, "AllowUserModifications",
				(h.contains("allowUpdates") ? (!(boolean) h.get("allowUpdates")) : false));
		h.setNoSave(invalid, "invalid", invalid);
		h.setNoSave(invalid, "name", name);
		h.setNoSave(invalid, "displayname", displayname);
		h.setNoSave(invalid, "lore", (lore == null ? new ArrayList<String>() : lore));
		h.setNoSave(invalid, "id", id);
		h.setNoSave(invalid, "craftingRequirements", craftingRequirements);
		h.setNoSave(invalid, "price", cost);
		h.setNoSave(invalid, "material", type.name());

		h.setNoSave(invalid, "damage", damage);
		h.setNoSave(invalid, "durability", durability);

		h.setNoSave(invalid, "MiscType", misctype.name());
		if (h.saveNow)
			h.save();
		return h;
	}
}
