package me.zombie_striker.qg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.guns.utils.WeaponType;
import me.zombie_striker.qg.handlers.gunvalues.ChargingHandlerEnum;

public class GunYMLCreator {

	public static void createNewGun(boolean forceUpdate, File dataFolder, String name, String displayname, int id,
			List<String> craftingRequirements, WeaponType weapontype, boolean enableIronSights, String ammotype,
			int damage, double sway, int maxBullets, int duribility, double delayReload, double delayShoot,
			int bulletspershot, boolean isAutomatic, int cost, ChargingHandlerEnum ch, int distance, WeaponSounds ws) {
		createNewGun(forceUpdate, dataFolder, false, name, name, displayname, null, id, craftingRequirements,
				weapontype, enableIronSights, ammotype, damage, sway, Main.guntype, maxBullets, duribility, delayReload,
				delayShoot, bulletspershot, isAutomatic, cost, ch, distance, false, ws);
	}

	public static void createNewGun(boolean forceUpdate, File dataFolder, String name, String displayname, int id,
			List<String> craftingRequirements, WeaponType weapontype, boolean enableIronSights, String ammotype,
			int damage, double sway, int maxBullets, int duribility, int bulletspershot, boolean isAutomatic, int cost,
			ChargingHandlerEnum ch, int distance, WeaponSounds ws) {
		createNewGun(forceUpdate, dataFolder, name, displayname, id, craftingRequirements, weapontype, enableIronSights,
				ammotype, damage, sway, maxBullets, duribility, 1.5, 0.25, bulletspershot, isAutomatic, cost, ch,
				distance, ws);
	}

	public static void createNewGun(boolean forceUpdate, File dataFolder, String name, String displayname, int id,
			List<String> craftingRequirements, WeaponType weapontype, boolean enableIronSights, String ammotype,
			int damage, double sway, int maxBullets, int duribility, boolean isAutomatic, int cost,
			ChargingHandlerEnum ch, int distance, WeaponSounds ws) {
		createNewGun(forceUpdate, dataFolder, name, displayname, id, craftingRequirements, weapontype, enableIronSights,
				ammotype, damage, sway, maxBullets, duribility, 1, isAutomatic, cost, ch, distance, ws);
	}

	// Displaynames above
	// non- below
	public static void createNewGun(boolean forceUpdate, File dataFolder, String name, int id,
			List<String> craftingRequirements, WeaponType weapontype, boolean enableIronSights, String ammotype,
			int damage, double sway, int maxBullets, int duribility, double delayReload, double delayShoot,
			int bulletspershot, boolean isAutomatic, int cost, ChargingHandlerEnum ch, int distance, WeaponSounds ws) {
		createNewGun(forceUpdate, dataFolder, false, "default_" + name, name, "&" + ChatColor.GOLD.getChar() + name,
				null, id, craftingRequirements, weapontype, enableIronSights, ammotype, damage, sway, Main.guntype,
				maxBullets, duribility, delayReload, delayShoot, bulletspershot, isAutomatic, cost, ch, distance, false,
				ws);
	}

	public static void createNewGun(boolean forceUpdate, File dataFolder, String name, int id,
			List<String> craftingRequirements, WeaponType weapontype, boolean enableIronSights, String ammotype,
			int damage, double sway, int maxBullets, int duribility, int bulletspershot, boolean isAutomatic, int cost,
			ChargingHandlerEnum ch, int distance, WeaponSounds ws) {
		createNewGun(forceUpdate, dataFolder, name, id, craftingRequirements, weapontype, enableIronSights, ammotype,
				damage, sway, maxBullets, duribility, 1.5, 0.25, bulletspershot, isAutomatic, cost, ch, distance, ws);
	}

	public static void createNewGun(boolean forceUpdate, File dataFolder, String name, int id,
			List<String> craftingRequirements, WeaponType weapontype, boolean enableIronSights, String ammotype,
			int damage, double sway, int maxBullets, int duribility, boolean isAutomatic, int cost,
			ChargingHandlerEnum ch, int distance, WeaponSounds ws) {
		createNewGun(forceUpdate, dataFolder, name, id, craftingRequirements, weapontype, enableIronSights, ammotype,
				damage, sway, maxBullets, duribility, 1, isAutomatic, cost, ch, distance, ws);
	}

	public static void createNewGun(boolean forceUpdate, File dataFolder, boolean invalid, String filename, String name,
			String displayname, List<String> lore, int id, List<String> craftingRequirements, WeaponType weapontype,
			boolean enableIronSights, String ammotype, int damage, double sway, Material type, int maxBullets,
			int duribility, double delayReload, double delayShoot, int bulletspershot, boolean isAutomatic, int cost,
			ChargingHandlerEnum ch, int distance, boolean version18, WeaponSounds ws) {
		createNewGun(forceUpdate, dataFolder, invalid, filename, name, displayname, lore, id, craftingRequirements,
				weapontype, enableIronSights, ammotype, damage, sway, type, maxBullets, duribility, delayReload,
				delayShoot, bulletspershot, isAutomatic, cost, ch, distance, 0, version18, ws);
	}

	public static void createNewGun(boolean forceUpdate, File dataFolder, boolean invalid, String filename, String name,
			String displayname, List<String> lore, int id, List<String> craftingRequirements, WeaponType weapontype,
			boolean enableIronSights, String ammotype, int damage, double sway, Material type, int maxBullets,
			int duribility, double delayReload, double delayShoot, int bulletspershot, boolean isAutomatic, int cost,
			ChargingHandlerEnum ch, int distance, int var, boolean version18, WeaponSounds ws) {
		File f2 = new File(dataFolder, "newGuns/" + filename + ".yml");
		if (!new File(dataFolder, "newGuns").exists())
			new File(dataFolder, "newGuns").mkdirs();

		FileConfiguration f = YamlConfiguration.loadConfiguration(f2);
		set(false, f, "invalid", invalid);
		set(false, f, "name", name);
		set(false, f, "displayname", displayname);
		set(false, f, "lore", (lore == null ? new ArrayList<String>() : lore));
		set(f.contains("material") && f.get("material").equals(Material.DIAMOND_HOE.name())
				&& Main.guntype != Material.DIAMOND_HOE, f, "material", type.name());
		set(false, f, "id", id);
		set(false, f, "variant", var);
		set(false, f, "craftingRequirements", craftingRequirements);
		set(false, f, "weapontype", weapontype.name());
		set(false, f, "weaponsounds", ws != null ? ws.getName() : null);
		StringBuilder validGuns = new StringBuilder();
		for (WeaponType g : WeaponType.values()) {
			validGuns.append(g.name() + ", ");
		}
		set(false, f, "_VALID_WEAPON_TYPES", validGuns.toString());
		set(false, f, "enableIronSights", enableIronSights);
		set(false, f, "ammotype", ammotype);
		set(false, f, "damage", damage);
		set(false, f, "sway", sway);
		set(false, f, "maxbullets", maxBullets);
		set(false, f, "durability", duribility);
		set(false, f, "delayForReload", delayReload);
		set(false, f, "delayForShoot", delayShoot);
		set(false, f, "bullets-per-shot", bulletspershot);
		set(false, f, "isAutomatic", isAutomatic);
		set(false, f, "price", cost);
		set(false, f, "maxBulletDistance", distance);
		if (version18)
			set(!f.contains("Version_18_Support"), f, "Version_18_Support", version18);
		set(false, f, "ChargingHandler", ch == null ? "null" : ch.getName());
		if (saveNow)
			try {
				f.save(f2);
				saveNow = false;
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	public static void createAmmo(boolean forceUpdate, File dataFolder, boolean invalid, String name,
			String displayname, int id, List<String> craftingRequirements, int cost, double severity, int maxAmount) {
		createAmmo(forceUpdate, dataFolder, invalid, "default_" + name, name, displayname, null, Main.guntype, id,
				craftingRequirements, cost, severity, maxAmount);
	}

	public static void createAmmo(boolean forceUpdate, File dataFolder, boolean invalid, String filename, String name,
			String displayname, int id, List<String> craftingRequirements, int cost, double severity, int maxAmount) {
		createAmmo(forceUpdate, dataFolder, invalid, filename, name, displayname, null, Main.guntype, id,
				craftingRequirements, cost, severity, maxAmount);
	}

	public static void createAmmo(boolean forceUpdate, File dataFolder, boolean invalid, String filename, String name,
			String displayname, List<String> lore, Material type, int id, List<String> craftingRequirements, int cost,
			double severity, int maxAmount) {
		createSkullAmmo(forceUpdate, dataFolder, invalid, filename, name, displayname, lore, type, id, null, craftingRequirements, cost, severity, maxAmount);
	}
	public static void createSkullAmmo(boolean forceUpdate, File dataFolder, boolean invalid, String filename, String name,
			String displayname, List<String> lore, Material type, int id, String SKULL_OWNER, List<String> craftingRequirements, int cost,
			double severity, int maxAmount) {
		File f2 = new File(dataFolder, "ammo/" + filename + ".yml");
		if (!new File(dataFolder, "ammo").exists())
			new File(dataFolder, "ammo").mkdirs();

		FileConfiguration f = YamlConfiguration.loadConfiguration(f2);
		set(false, f, "invalid", invalid);
		set(false, f, "name", name);
		set(false, f, "displayname", displayname);
		set(false, f, "lore", (lore == null ? new ArrayList<String>() : lore));
		set(false, f, "id", id);
		set(false, f, "craftingRequirements", craftingRequirements);
		set(false, f, "price", cost);
		set(false, f, "maxAmount", maxAmount);
		set(f.contains("material") && f.get("material").equals(Material.DIAMOND_HOE.name()), f, "material",
				type.name());

		if(SKULL_OWNER!=null)
			set(false,f,"skull_owner",SKULL_OWNER);
		
		set(false, f, "piercingSeverity", severity);
		if (saveNow)
			try {
				f.save(f2);
				saveNow = false;
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	public static void createMisc(boolean forceUpdate, File dataFolder, boolean invalid, String filename, String name,
			String displayname, List<String> lore, MaterialStorage ms, List<String> craftingRequirements, int cost,
			WeaponType misctype,int damage, int durability) {
		createMisc(forceUpdate, dataFolder, invalid, filename, name, displayname, lore, ms.getMat(), ms.getData(),
				craftingRequirements, cost, misctype,damage, durability);
	}

	public static void createMisc(boolean forceUpdate, File dataFolder, boolean invalid, String filename, String name,
			String displayname, List<String> lore, Material type, int id, List<String> craftingRequirements, int cost,
			WeaponType misctype, int damage, int durability) {
		File f2 = new File(dataFolder, "misc/" + filename + ".yml");
		if (!new File(dataFolder, "misc").exists())
			new File(dataFolder, "misc").mkdirs();

		FileConfiguration f = YamlConfiguration.loadConfiguration(f2);
		set(false, f, "invalid", invalid);
		set(false, f, "name", name);
		set(false, f, "displayname", displayname);
		set(false, f, "lore", (lore == null ? new ArrayList<String>() : lore));
		set(false, f, "id", id);
		set(false, f, "craftingRequirements", craftingRequirements);
		set(false, f, "price", cost);
		set(f.contains("material") && f.get("material").equals(Material.DIAMOND_HOE.name()), f, "material",
				type.name());
		

		set(false,f,"damage",damage);
		set(false,f ,"durability",durability);		

		set(false, f, "MiscType", misctype.name());
		if (saveNow)
			try {
				f.save(f2);
				saveNow = false;
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	public static boolean saveNow = false;

	public static void set(boolean force, FileConfiguration f, String name, Object v) {
		if (!f.contains(name) || (force && !f.get(name).equals(v))) {
			f.set(name, v);
			saveNow = true;
		}
	}
}
