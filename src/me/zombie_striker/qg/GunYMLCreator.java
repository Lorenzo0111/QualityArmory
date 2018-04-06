package me.zombie_striker.qg;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.zombie_striker.qg.ammo.Ammo;
import me.zombie_striker.qg.guns.utils.WeaponType;
import me.zombie_striker.qg.handlers.gunvalues.ChargingHandlerEnum;

public class GunYMLCreator {

	public static void createNewGun(boolean forceUpdate, File dataFolder, String name, int id,
			List<String> craftingRequirements, WeaponType weapontype, boolean enableIronSights, Ammo ammotype,
			int damage, double sway, int maxBullets, int duribility, double delayReload, double delayShoot,
			int bulletspershot, boolean isAutomatic, int cost, ChargingHandlerEnum ch,int distance) {
		createNewGun(forceUpdate, dataFolder, false, "default_" + name, name, ChatColor.GOLD + name, null, id,
				craftingRequirements, weapontype, enableIronSights, ammotype, damage, sway, Main.guntype, maxBullets,
				duribility, delayReload, delayShoot, bulletspershot, isAutomatic, cost, ch,distance);
	}

	public static void createNewGun(boolean forceUpdate, File dataFolder, String name, int id,
			List<String> craftingRequirements, WeaponType weapontype, boolean enableIronSights, Ammo ammotype,
			int damage, double sway, int maxBullets, int duribility, int bulletspershot, boolean isAutomatic, int cost,
			ChargingHandlerEnum ch,int distance) {
		createNewGun(forceUpdate, dataFolder, name, id, craftingRequirements, weapontype, enableIronSights, ammotype,
				damage, sway, maxBullets, duribility, 1.5, 0.25, bulletspershot, isAutomatic, cost, ch,distance);
	}

	public static void createNewGun(boolean forceUpdate, File dataFolder, String name, int id,
			List<String> craftingRequirements, WeaponType weapontype, boolean enableIronSights, Ammo ammotype,
			int damage, double sway, int maxBullets, int duribility, boolean isAutomatic, int cost,
			ChargingHandlerEnum ch,int distance) {
		createNewGun(forceUpdate, dataFolder, name, id, craftingRequirements, weapontype, enableIronSights, ammotype,
				damage, sway, maxBullets, duribility, 1, isAutomatic, cost, ch,distance);
	}

	public static void createNewGun(boolean forceUpdate, File dataFolder, boolean invalid, String filename, String name,
			String displayname, List<String> lore, int id, List<String> craftingRequirements, WeaponType weapontype,
			boolean enableIronSights, Ammo ammotype, int damage, double sway, Material type, int maxBullets,
			int duribility, double delayReload, double delayShoot, int bulletspershot, boolean isAutomatic, int cost,
			ChargingHandlerEnum ch,int distance) {
		File f2 = new File(dataFolder, "newGuns/"+filename+".yml");
		if (!f2.exists() || forceUpdate) {
			if (!new File(dataFolder, "newGuns").exists())
				new File(dataFolder, "newGuns").mkdirs();

			FileConfiguration f = YamlConfiguration
					.loadConfiguration(f2);
			f.set("invalid", invalid);
			f.set("name", name);
			f.set("displayname", displayname);
			f.set("lore", lore);
			f.set("id", id);
			f.set("craftingRequirements", craftingRequirements);
			f.set("weapontype", weapontype.name());
			StringBuilder validGuns = new StringBuilder();
			for (WeaponType g : WeaponType.values()) {
				validGuns.append(g.name() + ", ");
			}
			f.set("_VALID_WEAPON_TYPES", validGuns.toString());
			f.set("enableIronSights", enableIronSights);
			f.set("ammotype", ammotype.getName());
			f.set("damage", damage);
			f.set("sway", sway);
			f.set("material", type.name());
			f.set("maxbullets", maxBullets);
			f.set("durability", duribility);
			f.set("delayForReload", delayReload);
			f.set("delayForShoot", delayShoot);
			f.set("bullets-per-shot", bulletspershot);
			f.set("isAutomatic", isAutomatic);
			f.set("price", cost);
			f.set("maxBulletDistance",distance);
			f.set("ChargingHandler", ch==null?"null":ch.getName());
			try {
				f.save(f2);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
