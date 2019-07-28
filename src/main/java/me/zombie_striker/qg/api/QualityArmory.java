package me.zombie_striker.qg.api;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import me.zombie_striker.customitemmanager.CustomItemManager;
import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.customitemmanager.OLD_ItemFact;
import me.zombie_striker.qg.handlers.IronsightsHandler;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import me.zombie_striker.pluginconstructor.HotbarMessager;
import me.zombie_striker.qg.*;
import me.zombie_striker.qg.ammo.Ammo;
import me.zombie_striker.qg.armor.ArmorObject;
import me.zombie_striker.qg.attachments.AttachmentBase;
import me.zombie_striker.qg.config.GunYML;
import me.zombie_striker.qg.config.GunYMLCreator;
import me.zombie_striker.qg.config.GunYMLLoader;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.guns.utils.WeaponType;
import me.zombie_striker.qg.handlers.WorldGuardSupport;

public class QualityArmory {

	public static GunYML createAndLoadNewGun(String name, String displayname, Material material, int id,
			WeaponType type, WeaponSounds sound, boolean hasIronSights, String ammotype, int damage, int maxBullets,
			int cost) {
		File newGunsDir = new File(QAMain.getInstance().getDataFolder(), "newGuns");
		final File gunFile = new File(newGunsDir, name);
		new BukkitRunnable() {
			public void run() {
				GunYMLLoader.loadGuns(QAMain.getInstance(), gunFile);
			}
		}.runTaskLater(QAMain.getInstance(), 1);
		return GunYMLCreator.createNewCustomGun(QAMain.getInstance().getDataFolder(), name, name, displayname, id, null,
				type, sound, hasIronSights, ammotype, damage, maxBullets, cost).setMaterial(material);
	}

	public static GunYML createNewGunYML(String name, String displayname, Material material, int id, WeaponType type,
			WeaponSounds sound, boolean hasIronSights, String ammotype, int damage, int maxBullets, int cost) {
		return GunYMLCreator.createNewCustomGun(QAMain.getInstance().getDataFolder(), name, name, displayname, id, null,
				type, sound, hasIronSights, ammotype, damage, maxBullets, cost);
	}

	public static void registerNewUsedExpansionItem(Material used, int id) {
		registerNewUsedExpansionItem(used, id, 0);
	}

	public static void registerNewUsedExpansionItem(Material used, int id, int var) {
		QAMain.expansionPacks.add(MaterialStorage.getMS(used, id, var));
	}

	public static ArmoryBaseObject getCustomItemByName(String name) {
		for (Entry<MaterialStorage, Gun> e : QAMain.gunRegister.entrySet())
			if (e.getValue().getName().equalsIgnoreCase(name)) {
				return e.getValue();
			}
		for (Entry<MaterialStorage, Ammo> e : QAMain.ammoRegister.entrySet())
			if (e.getValue().getName().equalsIgnoreCase(name)) {
				return e.getValue();
			}
		for (Entry<MaterialStorage, ArmoryBaseObject> e : QAMain.miscRegister.entrySet())
			if (e.getValue().getName().equalsIgnoreCase(name)) {
				return e.getValue();
			}
		for (Entry<MaterialStorage, ArmorObject> e : QAMain.armorRegister.entrySet())
			if (e.getValue().getName().equalsIgnoreCase(name)) {
				return e.getValue();
			}
		return null;
	}

	@SuppressWarnings("deprecation")
	public static void sendResourcepack(final Player player, final boolean warning) {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (QAMain.namesToBypass.contains(player.getName())) {
					QAMain.resourcepackReq.add(player.getUniqueId());
					return;
				}
				if (warning) {
					try {
						player.sendTitle(ChatColor.RED + QAMain.S_NORES1, QAMain.S_NORES2);
					} catch (Error e2) {
						player.sendMessage(ChatColor.RED + QAMain.S_NORES1);
						player.sendMessage(ChatColor.RED + QAMain.S_NORES2);
					}
				}
				if (QAMain.showCrashMessage)
					player.sendMessage(QAMain.prefix + QAMain.S_RESOURCEPACK_HELP);

				new BukkitRunnable() {
					@Override
					public void run() {
						try {
							try {
								QAMain.DEBUG("Sending resourcepack : " + (QAMain.AutoDetectResourcepackVersion) + " || "
										+ QAMain.MANUALLYSELECT18 + " || " + QAMain.isVersionHigherThan(1, 9) + " || ");
								try {
									if (QAMain.hasViaVersion) {
										QAMain.DEBUG(
												"Has Viaversion: " + us.myles.ViaVersion.bukkit.util.ProtocolSupportUtil
														.getProtocolVersion(player) + " 1.8=" + QAMain.ID18);

									}
								} catch (Error | Exception re4) {
								}
								player.setResourcePack(CustomItemManager.getResourcepack());
								/*if (QAMain.AutoDetectResourcepackVersion
										&& us.myles.ViaVersion.bukkit.util.ProtocolSupportUtil
												.getProtocolVersion(player) < QAMain.ID18) {
									player.setResourcePack(QAMain.url18);
								} else if (QAMain.MANUALLYSELECT18) {
									player.setResourcePack(QAMain.url18New);
								} else {
									player.setResourcePack(QAMain.url);
								}*/
							} catch (Error | Exception e4) {
								/*if (!QAMain.isVersionHigherThan(1, 9)) {
									player.setResourcePack(QAMain.url18);
								} else if (QAMain.MANUALLYSELECT18) {
									player.setResourcePack(QAMain.url18New);
								} else {
									player.setResourcePack(QAMain.url);
								}*/
								player.setResourcePack(CustomItemManager.getResourcepack());
							}

							if (!QAMain.isVersionHigherThan(1, 9)) {
								QAMain.resourcepackReq.add(player.getUniqueId());
								QAMain.sentResourcepack.put(player.getUniqueId(), System.currentTimeMillis());
							}
							// If the player is on 1.8, manually add them to the resource list.

						} catch (Exception e) {

						}
					}
				}.runTaskLater(QAMain.getInstance(), 20 * (warning ? 1 : 5));
			}
		}.runTaskLater(QAMain.getInstance(), (long) (20 * QAMain.secondsTilSend));
	}

	public static boolean allowGunsInRegion(Location loc) {
		if (!QAMain.supportWorldGuard)
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

	@SuppressWarnings("deprecation")
	public static boolean isCustomItemNextId(ItemStack is) {
		if (is == null)
			return false;
		try{
			if(CustomItemManager.isUsingCustomData())
				return false;
		}catch (Error|Exception e4){}
		List<MaterialStorage> ms = new ArrayList<MaterialStorage>();
		ms.addAll(QAMain.expansionPacks);
		ms.addAll(QAMain.gunRegister.keySet());
		ms.addAll(QAMain.armorRegister.keySet());
		ms.addAll(QAMain.ammoRegister.keySet());
		ms.addAll(QAMain.miscRegister.keySet());
		for (MaterialStorage mat : ms) {
			if (mat.getMat() == is.getType())
				if (mat.getData() == (is.getDurability() + 1))
					if (!mat.hasVariant())
						return true;
		}
		return false;
	}

	public static ArmoryBaseObject getCustomItem(MaterialStorage material) {
		if(QAMain.gunRegister.containsKey(material))
			return QAMain.gunRegister.get(material);
		if(QAMain.ammoRegister.containsKey(material))
			return QAMain.ammoRegister.get(material);
		if(QAMain.miscRegister.containsKey(material))
			return QAMain.miscRegister.get(material);
		if(QAMain.armorRegister.containsKey(material))
			return QAMain.armorRegister.get(material);
		return null;
	}

	public static ArmoryBaseObject getCustomItem(Material material, int data, int variant) {
		ItemStack is = new ItemStack(material);
		if(variant!=0) {
			ItemMeta im = is.getItemMeta();
			OLD_ItemFact.addVariantData(im, im.getLore(), variant);
			is.setItemMeta(im);
		}
		try{
			ItemMeta im = is.getItemMeta();
			im.setCustomModelData(data);
			is.setItemMeta(im);
		}catch (Error|Exception e4){
			is.setDurability((short) data);
		}
		return getCustomItem(is);
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
			return QAMain.gunRegister.get(getGunWithAttchments(is).getBase());
		return null;
	}

	@SuppressWarnings("deprecation")
	public static boolean isCustomItem(ItemStack is, int dataOffset) {
		if (is == null)
			return false;
		ItemStack itemstack = is.clone();
		if(dataOffset!=0) {
			try {
				ItemMeta im = itemstack.getItemMeta();
				im.setCustomModelData(im.getCustomModelData() + dataOffset);
				itemstack.setItemMeta(im);
			} catch (Error | Exception ed4) {
				itemstack.setDurability((short) (is.getDurability() + dataOffset));
			}
		}
		return isArmor(itemstack) || isAmmo(itemstack) || isMisc(itemstack) || isGun(itemstack)
				|| isIronSights(itemstack) || QAMain.expansionPacks.contains(MaterialStorage.getMS(is));
	}

	@SuppressWarnings("deprecation")
	public static boolean isArmor(ItemStack is) {
		if (is == null)
			return false;
		return (is != null
				&& (QAMain.armorRegister.containsKey(MaterialStorage.getMS(is))
						|| QAMain.armorRegister.containsKey(MaterialStorage.getMS(is))));
	}

	@SuppressWarnings("deprecation")
	public static ArmorObject getArmor(ItemStack is) {
		if (QAMain.armorRegister.containsKey(MaterialStorage.getMS(is)))
			return QAMain.armorRegister.get(MaterialStorage.getMS(is));
		return QAMain.armorRegister.get(MaterialStorage.getMS(is));
	}

	@SuppressWarnings("deprecation")
	public static boolean isMisc(ItemStack is) {
		if (is == null)
			return false;
		int var = MaterialStorage.getVariant(is);
		return (is != null
				&& (QAMain.miscRegister.containsKey(MaterialStorage.getMS(is,var))
						|| QAMain.miscRegister.containsKey(MaterialStorage.getMS(is,var))));
	}

	@SuppressWarnings("deprecation")
	public static ArmoryBaseObject getMisc(ItemStack is) {
		return QAMain.miscRegister.get(MaterialStorage.getMS(is));
	}

	@SuppressWarnings("deprecation")
	public static Gun getGun(ItemStack is) {
		return QAMain.gunRegister.get(MaterialStorage.getMS(is));
	}

	@SuppressWarnings("deprecation")
	public static boolean isGun(ItemStack is) {
		return (is != null && QAMain.gunRegister.containsKey(MaterialStorage.getMS(is)));
	}

	@Deprecated
	public static AttachmentBase getGunWithAttchments(ItemStack is) {
		return null;
	}

	@Deprecated
	public static boolean isGunWithAttchments(
			ItemStack is) {/*
							 * if (is == null) return false; int var = MaterialStorage.getVarient(is);
							 * return (is != null &&
							 * (QAMain.attachmentRegister.containsKey(MaterialStorage.getMS(is.getType(),
							 * is.getDurability(), var)) ||
							 * QAMain.attachmentRegister.containsKey(MaterialStorage.getMS(is.getType(), -1,
							 * var))));
							 */
		return false;
	}

	@SuppressWarnings("deprecation")
	public static Ammo getAmmo(ItemStack is) {
		int var = MaterialStorage.getVariant(is);
		//String extraData = is.getType() == MultiVersionLookup.getSkull() ? ((SkullMeta) is.getItemMeta()).getOwner()
		//		: null;
		//String temp = SkullHandler.getURL64(is);
		if (QAMain.ammoRegister
				.containsKey(MaterialStorage.getMS(is,var)))
			return QAMain.ammoRegister
					.get(MaterialStorage.getMS(is,var));
		return QAMain.ammoRegister.get(MaterialStorage.getMS(is,var));
	}

	public static Ammo getAmmoByName(String name) {
		for (Entry<MaterialStorage, Ammo> e : QAMain.ammoRegister.entrySet())
			if (e.getValue().getName().equalsIgnoreCase(name)) {
				return e.getValue();
			}
		return null;
	}

	@Deprecated
	public static AttachmentBase getAttachmentByName(
			String name) {/*
							 * for (Entry<MaterialStorage, AttachmentBase> e :
							 * QAMain.attachmentRegister.entrySet()) if
							 * (e.getValue().getName().equalsIgnoreCase(name)) { return e.getValue(); }
							 */
		return null;
	}

	@SuppressWarnings("deprecation")
	public static boolean isAmmo(ItemStack is) {
		if (is == null)
			return false;
		int var = MaterialStorage.getVariant(is);
	//	String extraData = is.getType() == MultiVersionLookup.getSkull() ? ((SkullMeta) is.getItemMeta()).getOwner()
	//			: null;
	//	String temp = null;
	//	try {
	//		temp = SkullHandler.getURL64(is);
	//	} catch (Error | Exception e4) {
	//	}
		boolean k = (is != null && (QAMain.ammoRegister
				.containsKey(MaterialStorage.getMS(is,var))
				|| QAMain.ammoRegister.containsKey(MaterialStorage.getMS(is,var))));
		return k;
	}

	@SuppressWarnings("deprecation")
	public static boolean isIronSights(ItemStack is) {
		if (is == null)
			return false;
		if (is != null && is.getType() == IronsightsHandler.ironsightsMaterial
				)
			try{
				if(is.getItemMeta().getCustomModelData() == IronsightsHandler
				.ironsightsData)
					return true;
			}catch (Error|Exception e4){
				if(is.getDurability() == IronsightsHandler.ironsightsData)
					return true;
			}
		return false;
	}

	public static ArmorObject getArmorByName(String name) {
		for (ArmorObject g : QAMain.armorRegister.values()) {
			if (g.getName().equals(name))
				return g;
		}
		return null;
	}

	public static ArmoryBaseObject getMiscByName(String name) {
		for (ArmoryBaseObject g : QAMain.miscRegister.values()) {
			if (g.getName().equals(name))
				return g;
		}
		return null;
	}

	public static Gun getGunByName(String name) {
		for (Gun g : QAMain.gunRegister.values()) {
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
		int ammoamount = getAmmoInInventory(p, g.getAmmoType());

		if (QAMain.showOutOfAmmoOnTitle && ammoamount <= 0 && Gun.getAmount(usedItem) < 1) {
			p.sendTitle(" ", QAMain.S_OUT_OF_AMMO, 0, 20, 1);
		} else if (QAMain.showReloadOnTitle && reloading) {
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
						p.sendTitle(QAMain.S_RELOADING_MESSAGE, sb.toString(), 0, 4, 0);
					}
				}.runTaskLater(QAMain.getInstance(), i);
			}
		} else {

			try {
				String message = QAMain.S_HOTBAR_FORMAT;

				if (QAMain.disableHotBarMessageOnOutOfAmmo && QAMain.disableHotBarMessageOnReload
						&& QAMain.disableHotBarMessageOnShoot)
					return;
				if (reloading && QAMain.disableHotBarMessageOnReload)
					return;
				if (ammoamount <= 0 && QAMain.disableHotBarMessageOnOutOfAmmo)
					return;
				if (!reloading && ammoamount > 0 && QAMain.disableHotBarMessageOnShoot)
					return;

				if (message.contains("%name%"))
					message = message.replace("%name%",
							(attachmentBase != null ? attachmentBase.getDisplayName() : g.getDisplayName()));
				if (message.contains("%amount%"))
					message = message.replace("%amount%", Gun.getAmount(usedItem) + "");
				if (message.contains("%max%"))
					message = message.replace("%max%", g.getMaxBullets() + "");

				if (message.contains("%state%"))
					message = message.replace("%state%", reloading ? QAMain.S_RELOADING_MESSAGE
							: ammoamount <= 0 ? QAMain.S_OUT_OF_AMMO : QAMain.S_MAX_FOUND);
				if (message.contains("%total%"))
					message = message.replace("%total%", "" + ammoamount);

				// (attachmentBase != null ? attachmentBase.getDisplayName() :
				// g.getDisplayName()) + " = "
				// + ItemFact.getAmount(usedItem) + "/" + (g.getMaxBullets()) + "";
				if (QAMain.unknownTranslationKeyFixer) {
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
	public static int findSafeSpot(ItemStack newItem, boolean findHighest, boolean allowPockets) {
		try{

			return findSafeSpot(newItem.getType(), newItem.getItemMeta().getCustomModelData(), findHighest, allowPockets);
		}catch (Error|Exception e534){}
		return findSafeSpot(newItem.getType(), newItem.getDurability(), findHighest, allowPockets);
	}

	public static int findSafeSpot(Material newItemtype, int startingData, boolean findHighest, boolean allowPockets) {
		int safeDurib = startingData;
		if (allowPockets) {
			List<Integer> idsToWorryAbout = new ArrayList<>();
			for (MaterialStorage j : QAMain.ammoRegister.keySet())
				if (j.getMat() == newItemtype && ((j.getData() > safeDurib) == findHighest))
					idsToWorryAbout.add(j.getData());
			for (MaterialStorage j : QAMain.gunRegister.keySet())
				if (j.getMat() == newItemtype && ((j.getData() > safeDurib) == findHighest))
					idsToWorryAbout.add(j.getData());
			for (MaterialStorage j : QAMain.miscRegister.keySet())
				if (j.getMat() == newItemtype && ((j.getData() > safeDurib) == findHighest))
					idsToWorryAbout.add(j.getData());
			for (MaterialStorage j : QAMain.armorRegister.keySet())
				if (j.getMat() == newItemtype && ((j.getData() > safeDurib) == findHighest))
					idsToWorryAbout.add(j.getData());
			for (MaterialStorage j : QAMain.expansionPacks)
				if (j.getMat() == newItemtype && ((j.getData() > safeDurib) == findHighest))
					idsToWorryAbout.add(j.getData());
			if (findHighest) {
				for (int id = safeDurib + 1; id < safeDurib + 1000; id++) {
					if (!idsToWorryAbout.contains(id))
						return id;
				}
			} else {
				for (int id = safeDurib - 1; id > 0; id--) {
					if (!idsToWorryAbout.contains(id))
						return id;
				}
			}
			return 0;
		}

		for (MaterialStorage j : QAMain.ammoRegister.keySet())
			if (j.getMat() == newItemtype && (j.getData() > safeDurib) == findHighest)
				safeDurib = j.getData();
		for (MaterialStorage j : QAMain.gunRegister.keySet())
			if (j.getMat() == newItemtype && (j.getData() > safeDurib) == findHighest)
				safeDurib = j.getData();
		for (MaterialStorage j : QAMain.miscRegister.keySet())
			if (j.getMat() == newItemtype && (j.getData() > safeDurib) == findHighest)
				safeDurib = j.getData();
		for (MaterialStorage j : QAMain.armorRegister.keySet())
			if (j.getMat() == newItemtype && (j.getData() > safeDurib) == findHighest)
				safeDurib = j.getData();
		/*
		 * for (MaterialStorage j : QAMain.attachmentRegister.keySet()) if (j.getMat()
		 * == newItemtype && (j.getData() > safeDurib) == findHighest) safeDurib =
		 * j.getData();
		 */
		for (MaterialStorage j : QAMain.expansionPacks)
			if (j.getMat() == newItemtype && (j.getData() > safeDurib) == findHighest)
				safeDurib = j.getData();
		return safeDurib;
	}

	@SuppressWarnings("deprecation")
	public static int findSafeSpotVariant(ItemStack newItem, boolean findHighest) {
		try{
			return findSafeSpotVariant(newItem.getType(), newItem.getItemMeta().getCustomModelData(), findHighest);

		}catch (Error|Exception e4){
		}
		return findSafeSpotVariant(newItem.getType(), newItem.getDurability(), findHighest);
	}

	public static int findSafeSpotVariant(Material newItemtype, int startingData, boolean findHighest) {
		int safeDurib = 0;
		for (MaterialStorage j : QAMain.ammoRegister.keySet())
			if (j.getMat() == newItemtype && (j.getData() == startingData)
					&& ((j.getVariant()> safeDurib) == findHighest))
				safeDurib = j.getVariant();
		for (MaterialStorage j : QAMain.gunRegister.keySet())
			if (j.getMat() == newItemtype && (j.getData() == startingData)
					&& ((j.getVariant() > safeDurib) == findHighest))
				safeDurib = j.getVariant();
		for (MaterialStorage j : QAMain.miscRegister.keySet())
			if (j.getMat() == newItemtype && (j.getData() == startingData)
					&& ((j.getVariant() > safeDurib) == findHighest))
				safeDurib = j.getVariant();
		for (MaterialStorage j : QAMain.armorRegister.keySet())
			if (j.getMat() == newItemtype && (j.getData() == startingData)
					&& ((j.getVariant() > safeDurib) == findHighest))
				safeDurib = j.getVariant();
		/*
		 * for (MaterialStorage j : QAMain.attachmentRegister.keySet()) if (j.getMat()
		 * == newItemtype && (j.getData() == startingData) && ((j.getVarient() >
		 * safeDurib) == findHighest)) safeDurib = j.getVarient();
		 */
		for (MaterialStorage j : QAMain.expansionPacks)
			if (j.getMat() == newItemtype && (j.getData() == startingData)
					&& ((j.getVariant() > safeDurib) == findHighest))
				safeDurib = j.getVariant();
		return safeDurib;
	}

	public static int getMaxPages() {
		return (QAMain.armorRegister.size() + QAMain.ammoRegister.size() + QAMain.miscRegister.size()
				+ QAMain.gunRegister.size()) / (9 * 5);
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
		return count >= (g.isPrimaryWeapon() ? QAMain.primaryWeaponLimit : QAMain.secondaryWeaponLimit);
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
		return CustomItemManager.getItemFact("gun").getItem(g.getItemData(),1);
	}

	public static ItemStack getGunItemStack(AttachmentBase g) {
		return CustomItemManager.getItemFact("gun").getItem(g.getItemData(),1);
	}

	public static ItemStack getAmmoItemStack(Ammo g) {
		return CustomItemManager.getItemFact("gun").getItem(g.getItemData(),1);
	}

	public static ItemStack getMiscObject(String name) {
		return CustomItemManager.getItemFact("gun").getItem(getMiscByName(name).getItemData(),1);
	}

	public static ItemStack getMiscObject(ArmoryBaseObject obj) {
		return CustomItemManager.getItemFact("gun").getItem(obj.getItemData(),1);
	}

	public static ItemStack getIronSightsItemStack() {
		return OLD_ItemFact.getIronSights();
	}

	


	public static int getAmmoInInventory(Player player, Ammo a) {
		int amount = 0;
		if(player.getGameMode()==GameMode.CREATIVE)
			return 99999;
		for (ItemStack is : player.getInventory().getContents()) {
			if (is != null && me.zombie_striker.qg.api.QualityArmory.isAmmo(is)&&QualityArmory.getAmmo(is).equals(a)) {
				amount += is.getAmount();
			}
		}
		return amount;
	}

	public static boolean addAmmoToInventory(Player player, Ammo a, int amount) {
		int remaining = amount;
		for (int i = 0; i < player.getInventory().getSize(); i++) {
			ItemStack is = player.getInventory().getItem(i);
			if (is != null && QualityArmory.isAmmo(is) && QualityArmory.getAmmo(is).equals(a)) {
				if (is.getAmount() + remaining <= a.getMaxAmount()) {
					is.setAmount(is.getAmount() + remaining);
					remaining = 0;
				} else {
					remaining -= a.getMaxAmount() - is.getAmount();
					is.setAmount(a.getMaxAmount());
				}
				player.getInventory().setItem(i, is);
				if (remaining <= 0)
					break;
			}
		}
		if (remaining > 0) {
			if (player.getInventory().firstEmpty() >= 0) {
				ItemStack is = CustomItemManager.getItemFact("gun").getItem(a.getItemData(),1);
				is.setAmount(remaining);
				player.getInventory().addItem(is);
				remaining = 0;
			}
		}
		return remaining <= 0;
	}

	public static boolean removeAmmoFromInventory(Player player, Ammo a, int amount) {
		int remaining = amount;
		if(player.getGameMode()==GameMode.CREATIVE)
			return true;
		for (int i = 0; i < player.getInventory().getSize(); i++) {
			ItemStack is = player.getInventory().getItem(i);
			if (is != null && QualityArmory.isAmmo(is)&&QualityArmory.getAmmo(is).equals(a)) {
				int temp = is.getAmount();
				if (remaining < is.getAmount()) {
					is.setAmount(is.getAmount() - remaining);
				} else {
					is.setType(Material.AIR);
				}
				remaining -= temp;
				player.getInventory().setItem(i, is);
				if (remaining <= 0)
					break;

			}
		}
		return remaining <= 0;
	}
	
}
