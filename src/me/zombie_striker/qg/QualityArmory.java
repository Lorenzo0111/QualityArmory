package me.zombie_striker.qg;

import java.io.File;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import me.zombie_striker.pluginconstructor.HotbarMessager;
import me.zombie_striker.qg.ammo.Ammo;
import me.zombie_striker.qg.ammo.AmmoUtil;
import me.zombie_striker.qg.armor.ArmorObject;
import me.zombie_striker.qg.attachments.AttachmentBase;
import me.zombie_striker.qg.config.GunYML;
import me.zombie_striker.qg.config.GunYMLCreator;
import me.zombie_striker.qg.config.GunYMLLoader;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.guns.utils.WeaponType;
import me.zombie_striker.qg.handlers.MultiVersionLookup;
import me.zombie_striker.qg.handlers.SkullHandler;
import me.zombie_striker.qg.handlers.WorldGuardSupport;
import me.zombie_striker.qg.miscitems.IronSightsToggleItem;

public class QualityArmory {

	public static GunYML createAndLoadNewGun(String name, String displayname, Material material, int id,
			WeaponType type, WeaponSounds sound, boolean hasIronSights, String ammotype, int damage, int maxBullets,
			int cost) {
		File newGunsDir = new File(Main.getInstance().getDataFolder(), "newGuns");
		final File gunFile = new File(newGunsDir, name);
		new BukkitRunnable() {
			public void run() {
				GunYMLLoader.loadGuns(Main.getInstance(), gunFile);
			}
		}.runTaskLater(Main.getInstance(), 1);
		return GunYMLCreator.createNewCustomGun(Main.getInstance().getDataFolder(), name, name, displayname, id, null,
				type, sound, hasIronSights, ammotype, damage, maxBullets, cost).setMaterial(material);
	}

	public static GunYML createNewGunYML(String name, String displayname, Material material, int id, WeaponType type,
			WeaponSounds sound, boolean hasIronSights, String ammotype, int damage, int maxBullets, int cost) {
		return GunYMLCreator.createNewCustomGun(Main.getInstance().getDataFolder(), name, name, displayname, id, null,
				type, sound, hasIronSights, ammotype, damage, maxBullets, cost);
	}

	public static void registerNewUsedExpansionItem(Material used, int id) {
		registerNewUsedExpansionItem(used, id, 0);
	}

	public static void registerNewUsedExpansionItem(Material used, int id, int var) {
		Main.expansionPacks.add(MaterialStorage.getMS(used, id, var));
	}

	@SuppressWarnings("deprecation")
	public static void sendResourcepack(final Player player, final boolean warning) {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (Main.namesToBypass.contains(player.getName())) {
					Main.resourcepackReq.add(player.getUniqueId());
					return;
				}
				if (warning)
					try {
						player.sendTitle(ChatColor.RED + Main.S_NORES1, Main.S_NORES2);
					} catch (Error e2) {
						player.sendMessage(ChatColor.RED + Main.S_NORES1);
						player.sendMessage(ChatColor.RED + Main.S_NORES2);
					}
				player.sendMessage(Main.prefix + Main.S_RESOURCEPACK_HELP);
				new BukkitRunnable() {
					@Override
					public void run() {
						try {
							try {
								if (Main.AutoDetectResourcepackVersion
										&& us.myles.ViaVersion.bukkit.util.ProtocolSupportUtil
												.getProtocolVersion(player) < Main.ID18) {
									player.setResourcePack(Main.url18);
								} else {
									player.setResourcePack(Main.url);
								}
							} catch (Error | Exception e4) {
								player.setResourcePack(Main.url);
							}

							if (!Main.isVersionHigherThan(1, 9)) {
								Main.resourcepackReq.add(player.getUniqueId());
								Main.sentResourcepack.put(player.getUniqueId(), System.currentTimeMillis());
							}
							// If the player is on 1.8, manually add them to the resource list.

						} catch (Exception e) {

						}
					}
				}.runTaskLater(Main.getInstance(), 20 * (warning ? 1 : 5));
			}
		}.runTaskLater(Main.getInstance(), (long) (20 * Main.secondsTilSend));
	}

	public static boolean allowGunsInRegion(Location loc) {
		if (!Main.supportWorldGuard)
			return true;
		try {
			return WorldGuardSupport.a(loc);
		} catch (Error e) {
		}
		return true;
	}

	public static boolean isCustomItem(ItemStack is) {
		return isCustomItem(is, 0);
	}

	public static ArmoryBaseObject getCustomItem(ItemStack is) {
		if (isGun(is))
			return getGun(is);
		if (isAmmo(is))
			return getAmmo(is);
		if (isArmor(is))
			return getArmor(is);
		if (isMisc(is))
			return getMisc(is);
		if (isGunWithAttchments(is))
			return Main.gunRegister.get(getGunWithAttchments(is).getBase());
		return null;
	}

	@SuppressWarnings("deprecation")
	public static boolean isCustomItem(ItemStack is, int dataOffset) {
		if (is == null)
			return false;
		ItemStack itemstack = is.clone();
		itemstack.setDurability((short) (is.getDurability() + dataOffset));
		return isArmor(itemstack) || isGunWithAttchments(itemstack) || isAmmo(itemstack) || isMisc(itemstack)
				|| isGun(itemstack) || isIronSights(itemstack) 
				|| Main.expansionPacks.contains(MaterialStorage.getMS(is));
	}

	@SuppressWarnings("deprecation")
	public static boolean isArmor(ItemStack is) {
		if (is == null)
			return false;
		int var = MaterialStorage.getVarient(is);
		return (is != null
				&& (Main.armorRegister.containsKey(MaterialStorage.getMS(is.getType(), is.getDurability(), var))
						|| Main.armorRegister.containsKey(MaterialStorage.getMS(is.getType(), -1, var))));
	}



	@SuppressWarnings("deprecation")
	public static ArmorObject getArmor(ItemStack is) {
		int var = MaterialStorage.getVarient(is);
		if (Main.armorRegister.containsKey(MaterialStorage.getMS(is.getType(), is.getDurability(), var)))
			return Main.armorRegister.get(MaterialStorage.getMS(is.getType(), is.getDurability(), var));
		return Main.armorRegister.get(MaterialStorage.getMS(is.getType(), -1, var));
	}

	@SuppressWarnings("deprecation")
	public static boolean isMisc(ItemStack is) {
		if (is == null)
			return false;
		int var = MaterialStorage.getVarient(is);
		return (is != null
				&& (Main.miscRegister.containsKey(MaterialStorage.getMS(is.getType(), is.getDurability(), var))
						|| Main.miscRegister.containsKey(MaterialStorage.getMS(is.getType(), -1, var))));
	}

	@SuppressWarnings("deprecation")
	public static ArmoryBaseObject getMisc(ItemStack is) {
		int var = MaterialStorage.getVarient(is);
		if (Main.miscRegister.containsKey(MaterialStorage.getMS(is.getType(), is.getDurability(), var)))
			return Main.miscRegister.get(MaterialStorage.getMS(is.getType(), is.getDurability(), var));
		return Main.miscRegister.get(MaterialStorage.getMS(is.getType(), -1, var));
	}

	@SuppressWarnings("deprecation")
	public static Gun getGun(ItemStack is) {
		int var = MaterialStorage.getVarient(is);
		if (Main.gunRegister.containsKey(MaterialStorage.getMS(is.getType(), is.getDurability(), var)))
			return Main.gunRegister.get(MaterialStorage.getMS(is.getType(), is.getDurability(), var));
		return Main.gunRegister.get(MaterialStorage.getMS(is.getType(), -1, var));
	}

	@SuppressWarnings("deprecation")
	public static boolean isGun(ItemStack is) {
		if (is == null)
			return false;
		int var = MaterialStorage.getVarient(is);
		return (is != null
				&& (Main.gunRegister.containsKey(MaterialStorage.getMS(is.getType(), is.getDurability(), var))
						|| Main.gunRegister.containsKey(MaterialStorage.getMS(is.getType(), -1, var))));
	}

	@SuppressWarnings("deprecation")

	public static AttachmentBase getGunWithAttchments(ItemStack is) {
		int var = MaterialStorage.getVarient(is);
		if (Main.attachmentRegister.containsKey(MaterialStorage.getMS(is.getType(), is.getDurability(), var)))
			return Main.attachmentRegister.get(MaterialStorage.getMS(is.getType(), is.getDurability(), var));
		return Main.attachmentRegister.get(MaterialStorage.getMS(is.getType(), -1, var));
	}

	@SuppressWarnings("deprecation")
	public static boolean isGunWithAttchments(ItemStack is) {
		if (is == null)
			return false;
		int var = MaterialStorage.getVarient(is);
		return (is != null
				&& (Main.attachmentRegister.containsKey(MaterialStorage.getMS(is.getType(), is.getDurability(), var))
						|| Main.attachmentRegister.containsKey(MaterialStorage.getMS(is.getType(), -1, var))));
	}

	@SuppressWarnings("deprecation")
	public static Ammo getAmmo(ItemStack is) {
		int var = MaterialStorage.getVarient(is);
		String extraData = is.getType() == MultiVersionLookup.getSkull() ? ((SkullMeta) is.getItemMeta()).getOwner()
				: null;
		String temp = SkullHandler.getURL64(is);
		if (Main.ammoRegister
				.containsKey(MaterialStorage.getMS(is.getType(), is.getDurability(), var, extraData, temp)))
			return Main.ammoRegister.get(MaterialStorage.getMS(is.getType(), is.getDurability(), var, extraData, temp));
		return Main.ammoRegister.get(MaterialStorage.getMS(is.getType(), -1, var, extraData, temp));
	}

	public static Ammo getAmmoByName(String name) {
		for (Entry<MaterialStorage, Ammo> e : Main.ammoRegister.entrySet())
			if (e.getValue().getName().equalsIgnoreCase(name)) {
				return e.getValue();
			}
		return null;
	}

	public static AttachmentBase getAttachmentByName(String name) {
		for (Entry<MaterialStorage, AttachmentBase> e : Main.attachmentRegister.entrySet())
			if (e.getValue().getAttachmentName().equalsIgnoreCase(name)) {
				return e.getValue();
			}
		return null;
	}

	@SuppressWarnings("deprecation")
	public static boolean isAmmo(ItemStack is) {
		if (is == null)
			return false;
		int var = MaterialStorage.getVarient(is);
		String extraData = is.getType() == MultiVersionLookup.getSkull() ? ((SkullMeta) is.getItemMeta()).getOwner()
				: null;
		String temp = null;
		try {
			temp = SkullHandler.getURL64(is);
		} catch (Error | Exception e4) {
		}
		boolean k = (is != null && (Main.ammoRegister
				.containsKey(MaterialStorage.getMS(is.getType(), is.getDurability(), var, extraData, temp))
				|| Main.ammoRegister.containsKey(MaterialStorage.getMS(is.getType(), -1, var, extraData, temp))));
		return k;
	}

	@SuppressWarnings("deprecation")
	public static boolean isIronSights(ItemStack is) {
		if (is == null)
			return false;
		if (is != null && is.getType() == IronSightsToggleItem.getMat()
				&& is.getDurability() == IronSightsToggleItem.getData())
			return true;
		return false;
	}

	public static ArmorObject getArmorByName(String name) {
		for (ArmorObject g : Main.armorRegister.values()) {
			if (g.getName().equals(name))
				return g;
		}
		return null;
	}

	public static ArmoryBaseObject getMiscByName(String name) {
		for (ArmoryBaseObject g : Main.miscRegister.values()) {
			if (g.getName().equals(name))
				return g;
		}
		return null;
	}

	public static Gun getGunByName(String name) {
		for (Gun g : Main.gunRegister.values()) {
			if (g.getName().equals(name))
				return g;
		}
		return null;
	}

	public static void sendHotbarGunAmmoCount(Player p, Gun g, ItemStack usedItem, boolean reloading) {
		sendHotbarGunAmmoCount(p, g, null, usedItem, reloading);
	}

	public static void sendHotbarGunAmmoCount(final Player p, final Gun g, AttachmentBase attachmentBase,
			ItemStack usedItem, boolean reloading) {
		int ammoamount = AmmoUtil.getAmmoAmount(p, g.getAmmoType());

		if (Main.showOutOfAmmoOnTitle && ammoamount <= 0 && ItemFact.getAmount(usedItem) < 1) {
			p.sendTitle(" ", Main.S_OUT_OF_AMMO, 0, 20, 1);
		} else if (Main.showReloadOnTitle && reloading) {
			// p.sendTitle(Main.S_RELOADING_MESSAGE,,0,2,1);
			for (int i = 1; i < g.getReloadTime() * 20; i += 2) {
				final int id = i;
				new BukkitRunnable() {
					@Override
					public void run() {
						StringBuilder sb = new StringBuilder();
						sb.append(ChatColor.GRAY);
						sb.append(StringUtils.repeat("#", (int) (20 * (1.0 * id / (20 * g.getReloadTime())))));
						sb.append(ChatColor.DARK_GRAY);
						sb.append(StringUtils.repeat("#", (int) (20 - ((int) (20.0 * id / (20 * g.getReloadTime()))))));
						p.sendTitle(Main.S_RELOADING_MESSAGE, sb.toString(), 0, 4, 0);
					}
				}.runTaskLater(Main.getInstance(), i);
			}
		} else {

			try {
				String message = Main.S_HOTBAR_FORMAT;

				if (Main.disableHotBarMessageOnOutOfAmmo && Main.disableHotBarMessageOnReload
						&& Main.disableHotBarMessageOnShoot)
					return;
				if (reloading && Main.disableHotBarMessageOnReload)
					return;
				if (ammoamount <= 0 && Main.disableHotBarMessageOnOutOfAmmo)
					return;
				if (!reloading && ammoamount > 0 && Main.disableHotBarMessageOnShoot)
					return;

				if (message.contains("%name%"))
					message = message.replace("%name%",
							(attachmentBase != null ? attachmentBase.getDisplayName() : g.getDisplayName()));
				if (message.contains("%amount%"))
					message = message.replace("%amount%", ItemFact.getAmount(usedItem) + "");
				if (message.contains("%max%"))
					message = message.replace("%max%", g.getMaxBullets() + "");

				if (message.contains("%state%"))
					message = message.replace("%state%", reloading ? Main.S_RELOADING_MESSAGE
							: ammoamount <= 0 ? Main.S_OUT_OF_AMMO : Main.S_MAX_FOUND);
				if (message.contains("%total%"))
					message = message.replace("%total%", "" + ammoamount);

				// (attachmentBase != null ? attachmentBase.getDisplayName() :
				// g.getDisplayName()) + " = "
				// + ItemFact.getAmount(usedItem) + "/" + (g.getMaxBullets()) + "";
				if (Main.unknownTranslationKeyFixer) {
					message = ChatColor.stripColor(message);
				} else {
					message = ChatColor.translateAlternateColorCodes('&', message);
				}
				HotbarMessager.sendHotBarMessage(p, message);
			} catch (Error | Exception e5) {
			}
		}
	}

	@SuppressWarnings("deprecation")
	public static int findSafeSpot(ItemStack newItem, boolean findHighest) {
		return findSafeSpot(newItem.getType(), newItem.getDurability(), findHighest);
	}

	public static int findSafeSpot(Material newItemtype, int startingData, boolean findHighest) {
		int safeDurib = startingData;
		for (MaterialStorage j : Main.ammoRegister.keySet())
			if (j.getMat() == newItemtype && (j.getData() > safeDurib) == findHighest)
				safeDurib = j.getData();
		for (MaterialStorage j : Main.gunRegister.keySet())
			if (j.getMat() == newItemtype && (j.getData() > safeDurib) == findHighest)
				safeDurib = j.getData();
		for (MaterialStorage j : Main.miscRegister.keySet())
			if (j.getMat() == newItemtype && (j.getData() > safeDurib) == findHighest)
				safeDurib = j.getData();
		for (MaterialStorage j : Main.armorRegister.keySet())
			if (j.getMat() == newItemtype && (j.getData() > safeDurib) == findHighest)
				safeDurib = j.getData();
		for (MaterialStorage j : Main.attachmentRegister.keySet())
			if (j.getMat() == newItemtype && (j.getData() > safeDurib) == findHighest)
				safeDurib = j.getData();
		for (MaterialStorage j : Main.expansionPacks)
			if (j.getMat() == newItemtype && (j.getData() > safeDurib) == findHighest)
				safeDurib = j.getData();
		return safeDurib;
	}

	@SuppressWarnings("deprecation")
	public static int findSafeSpotVariant(ItemStack newItem, boolean findHighest) {
		return findSafeSpotVariant(newItem.getType(), newItem.getDurability(), findHighest);
	}

	public static int findSafeSpotVariant(Material newItemtype, int startingData, boolean findHighest) {
		int safeDurib = 0;
		for (MaterialStorage j : Main.ammoRegister.keySet())
			if (j.getMat() == newItemtype && (j.getData() == startingData)
					&& ((j.getVarient() > safeDurib) == findHighest))
				safeDurib = j.getVarient();
		for (MaterialStorage j : Main.gunRegister.keySet())
			if (j.getMat() == newItemtype && (j.getData() == startingData)
					&& ((j.getVarient() > safeDurib) == findHighest))
				safeDurib = j.getVarient();
		for (MaterialStorage j : Main.miscRegister.keySet())
			if (j.getMat() == newItemtype && (j.getData() == startingData)
					&& ((j.getVarient() > safeDurib) == findHighest))
				safeDurib = j.getVarient();
		for (MaterialStorage j : Main.armorRegister.keySet())
			if (j.getMat() == newItemtype && (j.getData() == startingData)
					&& ((j.getVarient() > safeDurib) == findHighest))
				safeDurib = j.getVarient();
		for (MaterialStorage j : Main.attachmentRegister.keySet())
			if (j.getMat() == newItemtype && (j.getData() == startingData)
					&& ((j.getVarient() > safeDurib) == findHighest))
				safeDurib = j.getVarient();
		for (MaterialStorage j : Main.expansionPacks)
			if (j.getMat() == newItemtype && (j.getData() == startingData)
					&& ((j.getVarient() > safeDurib) == findHighest))
				safeDurib = j.getVarient();
		return safeDurib;
	}

	public static int getMaxPages() {
		return (Main.armorRegister.size() + Main.ammoRegister.size() + Main.miscRegister.size()
				+ Main.gunRegister.size()) / (9 * 5);
	}

	public static boolean isOverLimitForPrimaryWeapons(Gun g, Player p) {
		int count = 0;
		for (ItemStack is : p.getInventory().getContents()) {
			if (is != null && isGun(is)) {
				Gun g2 = getGun(is);
				if (g2.isPrimaryWeapon() == g.isPrimaryWeapon())
					count++;
			}
		}
		return count >= (g.isPrimaryWeapon() ? Main.primaryWeaponLimit : Main.secondaryWeaponLimit);
	}

	public static ItemStack getCustomItem(ArmoryBaseObject customItem) {
		if (customItem instanceof Gun) {
			return getGunItemStack((Gun) customItem);
		} else if (customItem instanceof AttachmentBase) {
			return getGunItemStack((AttachmentBase) customItem);
		} else if (customItem instanceof Ammo) {
			return getAmmoItemStack((Ammo) customItem);
		} else {
			return getMiscObject(customItem);
		}
	}

	public static ItemStack getGunItemStack(String name) {
		AttachmentBase a = getAttachmentByName(name);
		if (name != null)
			return getGunItemStack(a);
		return getGunItemStack(getGunByName(name));
	}

	public static ItemStack getAmmoItemStack(String name) {
		return getAmmoItemStack(getAmmoByName(name));
	}

	public static ItemStack getGunItemStack(Gun g) {
		return ItemFact.getGun(g);
	}

	public static ItemStack getGunItemStack(AttachmentBase g) {
		return ItemFact.getGun(g);
	}

	public static ItemStack getAmmoItemStack(Ammo g) {
		return ItemFact.getAmmo(g, 1);
	}

	public static ItemStack getMiscObject(String name) {
		return ItemFact.getObject(getMiscByName(name));
	}

	public static ItemStack getMiscObject(ArmoryBaseObject obj) {
		return ItemFact.getObject(obj, 1);
	}

	public static ItemStack getIronSightsItemStack() {
		return ItemFact.getIronSights();
	}

}
