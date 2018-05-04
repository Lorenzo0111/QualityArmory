package me.zombie_striker.qg;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import me.zombie_striker.qg.ammo.Ammo;
import me.zombie_striker.qg.ammo.AmmoType;
import me.zombie_striker.qg.attachments.AttachmentBase;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.guns.utils.WeaponType;
import me.zombie_striker.qg.handlers.gunvalues.ChargingHandlerEnum;
import me.zombie_striker.qg.miscitems.Flashbang;
import me.zombie_striker.qg.miscitems.Grenades;
import me.zombie_striker.qg.miscitems.MedKit;
import me.zombie_striker.qg.miscitems.MeleeItems;
import me.zombie_striker.qg.miscitems.SmokeGrenades;

public class GunYMLLoader {

	public static void loadAmmo(Main main) {

		if (new File(main.getDataFolder(), "ammo").exists())
			for (File f : new File(main.getDataFolder(), "ammo").listFiles()) {
				try {
					if (f.getName().contains("yml")) {
						FileConfiguration f2 = YamlConfiguration.loadConfiguration(f);
						if ((!f2.contains("invalid")) || !f2.getBoolean("invalid")) {
							Material m = (Material) (f2.contains("material")
									? Material.matchMaterial(f2.getString("material"))
									: Main.guntype);
							int variant = f2.contains("variant") ? f2.getInt("variant") : 0;
							final String name = f2.getString("name");
							main.getLogger().info("-Loading AmmoType: " + name);

							String extraData = null;
							if (f2.contains("skull_owner")) {
								extraData = f2.getString("skull_owner");
							}
							String ed2 = null;
							if (f2.contains("skull_owner_custom_url")
									&& !f2.getString("skull_owner_custom_url").equals(Ammo.dontuseskin)) {
								ed2 = f2.getString("skull_owner_custom_url");
							}

							final MaterialStorage ms = MaterialStorage.getMS(m, f2.getInt("id"), variant, extraData,
									ed2);
							final ItemStack[] materails = main
									.convertIngredients(f2.getStringList("craftingRequirements"));
							final String displayname = f2.contains("displayname")
									? ChatColor.translateAlternateColorCodes('&', f2.getString("displayname"))
									: (ChatColor.WHITE + name);
							final List<String> extraLore2 = f2.contains("lore") ? f2.getStringList("lore") : null;
							final List<String> extraLore = new ArrayList<String>();
							try {
								for (String lore : extraLore2) {
									extraLore.add(ChatColor.translateAlternateColorCodes('&', lore));
								}
							} catch (Error | Exception re52) {
							}

							final double price = f2.contains("price") ? f2.getDouble("price") : 100;

							int amountA = f2.getInt("maxAmount");

							double piercing = f2.getDouble("piercingSeverity");

							Ammo da = new Ammo(name, displayname, extraLore, ms, amountA, false, 1, price, materails,
									piercing);

							Main.ammoRegister.put(ms, da);

							if (extraData != null) {
								da.setSkullOwner(extraData);
							}
							if (ed2 != null) {
								da.setCustomSkin(ed2);
							}
							if (f2.contains("craftingReturnAmount")) {
								da.setCraftingReturn(f2.getInt("craftingReturnAmount"));
							}

						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
	}

	public static void loadMisc(Main main) {
		if (new File(main.getDataFolder(), "misc").exists())
			for (File f : new File(main.getDataFolder(), "misc").listFiles()) {
				try {
					if (f.getName().contains("yml")) {
						FileConfiguration f2 = YamlConfiguration.loadConfiguration(f);
						if ((!f2.contains("invalid")) || !f2.getBoolean("invalid")) {
							final String name = f2.getString("name");
							main.getLogger().info("-Loading Misc: " + name);

							Material m = (Material) (f2.contains("material")
									? Material.matchMaterial(f2.getString("material"))
									: Main.guntype);
							int variant = f2.contains("variant") ? f2.getInt("variant") : 0;
							final MaterialStorage ms = MaterialStorage.getMS(m, f2.getInt("id"), variant, null);
							final ItemStack[] materails = main
									.convertIngredients(f2.getStringList("craftingRequirements"));
							final String displayname = f2.contains("displayname")
									? ChatColor.translateAlternateColorCodes('&', f2.getString("displayname"))
									: (ChatColor.WHITE + name);
							final List<String> rawLore = f2.contains("lore") ? f2.getStringList("lore") : null;
							final List<String> lore = new ArrayList<String>();
							try {
								for (String lore2 : rawLore) {
									lore.add(ChatColor.translateAlternateColorCodes('&', lore2));
								}
							} catch (Error | Exception re52) {
							}

							final int price = f2.contains("price") ? f2.getInt("price") : 100;

							int damage = f2.contains("damage") ? f2.getInt("damage") : 1;
							// int durib = f2.contains("durability") ? f2.getInt("durability") : 1000;

							WeaponType wt = WeaponType.getByName(f2.getString("MiscType"));

							int radius = f2.contains("radius") ? f2.getInt("radius") : 0;

							if (wt == WeaponType.MEDKIT)
								Main.miscRegister.put(ms, new MedKit(ms, name, displayname, materails, price));
							if (wt == WeaponType.MEELEE)
								Main.miscRegister.put(ms,
										new MeleeItems(ms, name, displayname, materails, price, damage));
							if (wt == WeaponType.GRENADES)
								Main.miscRegister.put(ms,
										new Grenades(materails, price, damage, radius, name, displayname, lore, ms));
							if (wt == WeaponType.SMOKE_GRENADES)
								Main.miscRegister.put(ms, new SmokeGrenades(materails, price, damage, radius, name,
										displayname, lore, ms));
							if (wt == WeaponType.FLASHBANGS)
								Main.miscRegister.put(ms,
										new Flashbang(materails, price, damage, radius, name, displayname, lore, ms));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
	}

	public static void loadGuns(Main main) {
		for (File f : new File(main.getDataFolder(), "newGuns").listFiles()) {
			try {
				if (f.getName().contains("yml")) {
					FileConfiguration f2 = YamlConfiguration.loadConfiguration(f);
					if ((!f2.contains("invalid")) || !f2.getBoolean("invalid")) {
						final String name = f2.getString("name");
						main.getLogger().info("-Loading Gun: " + name);

						Material m = (Material) (f2.contains("material")
								? Material.matchMaterial(f2.getString("material"))
								: Main.guntype);
						int variant = f2.contains("variant") ? f2.getInt("variant") : 0;
						final MaterialStorage ms = MaterialStorage.getMS(m, f2.getInt("id"), variant, null);
						WeaponType weatype = f2.contains("guntype") ? WeaponType.valueOf(f2.getString("guntype"))
								: WeaponType.valueOf(f2.getString("weapontype"));
						final ItemStack[] materails = main.convertIngredients(f2.getStringList("craftingRequirements"));

						final String displayname = f2.contains("displayname")
								? ChatColor.translateAlternateColorCodes('&', f2.getString("displayname"))
								: (ChatColor.GOLD + name);
						final List<String> extraLore2 = f2.contains("lore") ? f2.getStringList("lore") : null;
						final List<String> extraLore = new ArrayList<String>();

						double partr = f2.contains("particles.bullet_particleR")
								? f2.getDouble("particles.bullet_particleR")
								: 1.0;
						double partg = f2.contains("particles.bullet_particleG")
								? f2.getDouble("particles.bullet_particleG")
								: 1.0;
						double partb = f2.contains("particles.bullet_particleB")
								? f2.getDouble("particles.bullet_particleB")
								: 1.0;

						boolean addMuzzleSmoke = f2.contains("addMuzzleSmoke") ? f2.getBoolean("addMuzzleSmoke")
								: false;

						try {
							for (String lore : extraLore2) {
								extraLore.add(ChatColor.translateAlternateColorCodes('&', lore));
							}
						} catch (Error | Exception re52) {
						}

						final double price = f2.contains("price") ? f2.getDouble("price") : 100;

						if (weatype.isGun()) {
							boolean isAutomatic = f2.contains("isAutomatic") ? f2.getBoolean("isAutomatic") : false;

							String sound = WeaponSounds.GUN_MEDIUM.getName();

							if (f2.contains("weaponsounds")) {
								sound = f2.getString("weaponsounds");
							} else {

								if (weatype == WeaponType.PISTOL || weatype == WeaponType.SMG)
									sound = WeaponSounds.GUN_SMALL.getName();
								if (weatype == WeaponType.SHOTGUN || weatype == WeaponType.SNIPER)
									sound = WeaponSounds.GUN_BIG.getName();
								if (weatype == WeaponType.RPG)
									sound = WeaponSounds.WARHEAD_LAUNCH.getName();
								if (weatype == WeaponType.LAZER)
									sound = WeaponSounds.LAZERSHOOT.getName();
							}

							Gun g = new Gun(name, ms, weatype, f2.getBoolean("enableIronSights"),
									AmmoType.getAmmo(f2.getString("ammotype")), f2.getDouble("sway"), 2,
									f2.getInt("maxbullets"), f2.getInt("damage"), isAutomatic, f2.getInt("durability"),
									sound, extraLore, displayname, price, materails);

							Main.gunRegister.put(ms, g);

							g.setUseMuzzleSmoke(addMuzzleSmoke);

							g.setReloadingTimeInSeconds(f2.getDouble("delayForReload"));
							try {
								if (!f2.getString("ChargingHandler").equals("null"))
									g.setChargingHandler(
											ChargingHandlerEnum.getEnumV(f2.getString("ChargingHandler")).getHandler());
							} catch (Error | Exception e445) {
							}
							try {
								g.setDelayBetweenShots(f2.getDouble("delayForShoot"));
							} catch (Error | Exception er5) {
							}
							try {
								g.setBulletsPerShot(f2.getInt("bullets-per-shot"));
							} catch (Error | Exception er5) {
							}

							try {
								g.setMaxDistance(f2.getInt("maxBulletDistance"));
							} catch (Error | Exception er5) {
							}
							if (f2.contains("Version_18_Support"))
								g.set18Supported(f2.getBoolean("Version_18_Support"));

							try {
								Particle particle = (Particle) (f2.contains("particles.bullet_particle")
										? Particle.valueOf(f2.getString("particles.bullet_particle"))
										: Main.bulletTrail);
								g.setParticles(particle, partr, partg, partb);
							} catch (Error | Exception er5) {
							}
						}

					}
				}
			} catch (Exception e) {
			}
		}
	}

	public static void loadAttachments(Main main) {
		for (File f : new File(main.getDataFolder(), "attachments").listFiles()) {
			try {
				if (f.getName().contains("yml")) {
					FileConfiguration f2 = YamlConfiguration.loadConfiguration(f);
					if ((!f2.contains("invalid")) || !f2.getBoolean("invalid")) {
						final String name = f2.getString("name");
						main.getLogger().info("-Loading Attachment: " + name);
						final String displayname = f2.contains("displayname")
								? ChatColor.translateAlternateColorCodes('&', f2.getString("displayname"))
								: (ChatColor.GOLD + name);
						final List<String> extraLore2 = f2.contains("lore") ? f2.getStringList("lore") : null;

						Material m = (Material) (f2.contains("material")
								? Material.matchMaterial(f2.getString("material"))
								: Main.guntype);
						int variant = f2.contains("variant") ? f2.getInt("variant") : 0;
						final MaterialStorage ms = MaterialStorage.getMS(m, f2.getInt("id"), variant, null);

						Gun baseGun = null;
						MaterialStorage baseGunM = null;
						for (Entry<MaterialStorage, Gun> g : Main.gunRegister.entrySet()) {
							if (g.getValue().getName().equalsIgnoreCase(f2.getString("baseGun"))) {
								baseGun = g.getValue();
								baseGunM = g.getKey();
							}
						}

						final List<String> extraLore = new ArrayList<String>();
						try {
							for (String lore : extraLore2) {
								extraLore.add(ChatColor.translateAlternateColorCodes('&', lore));
							}
						} catch (Error | Exception re52) {
						}

						AttachmentBase attach = new AttachmentBase(baseGunM, ms, name, displayname, extraLore);

						Main.attachmentRegister.put(ms, attach);

						attach.setPrice(f2.contains("price") ? f2.getDouble("price") : baseGun.cost());

						if (f2.contains("craftingRequirements"))
							attach.setCraftingRequirements(
									main.convertIngredients(f2.getStringList("craftingRequirements")));

						if (f2.contains("weapontype"))
							attach.setNewWeaponType(WeaponType.getByName(f2.getString("weapontype")));

						if (f2.contains("isAutomatic"))
							attach.setAutomatic(f2.getBoolean("isAutomatic"));

						try {
							if (f2.contains("weaponsounds"))
								attach.setNewSound(WeaponSounds.getByName(f2.getString("weaponsounds")));
						} catch (Error | Exception e543) {
						}

						try {
							if (f2.contains("addMuzzleSmoke"))
								attach.setMuzzleSmoke(f2.getBoolean("addMuzzleSmoke"));
						} catch (Error | Exception e543) {
						}
						try {
							if (f2.contains("delayForReload"))
								attach.setDelayReload(f2.getDouble("delayForReload"));
						} catch (Error | Exception e543) {
						}
						try {
							if (f2.contains("ChargingHandler") && !f2.getString("ChargingHandler").equals("null"))
								attach.setCh(
										ChargingHandlerEnum.getEnumV(f2.getString("ChargingHandler")).getHandler());
						} catch (Error | Exception e445) {
						}
						try {
							if (f2.contains("delayForShoot"))
								attach.setDelayShoot(f2.getDouble("delayForShoot"));
						} catch (Error | Exception er5) {
						}
						try {
							if (f2.contains("bullets-per-shot"))
								attach.setBulletsPerShot(f2.getInt("bullets-per-shot"));
						} catch (Error | Exception er5) {
						}

						try {
							if (f2.contains("maxBulletDistance"))
								attach.setMaxDistance(f2.getInt("maxBulletDistance"));
						} catch (Error | Exception er5) {
						}
						if (f2.contains("Version_18_Support"))
							attach.setSupports18(f2.getBoolean("Version_18_Support"));

						try {
							if (f2.contains("particles.bullet_particle")) {
								Particle particle = (Particle) (f2.contains("particles.bullet_particle")
										? Particle.valueOf(f2.getString("particles.bullet_particle"))
										: Main.bulletTrail);
								double partr = f2.contains("particles.bullet_particleR")
										? f2.getDouble("particles.bullet_particleR")
										: -1.0;
								double partg = f2.contains("particles.bullet_particleG")
										? f2.getDouble("particles.bullet_particleG")
										: -1.0;
								double partb = f2.contains("particles.bullet_particleB")
										? f2.getDouble("particles.bullet_particleB")
										: -1.0;
								attach.setNewParticles(particle, partr, partg, partb);
							}
						} catch (Error | Exception er5) {
						}
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
