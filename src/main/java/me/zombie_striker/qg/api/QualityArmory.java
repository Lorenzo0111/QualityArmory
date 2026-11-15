package me.zombie_striker.qg.api;

import me.zombie_striker.qg.utils.FoliaRunnable;
import me.zombie_striker.qg.items.ItemState;
import me.zombie_striker.qg.utils.ErrorReporter;
import me.zombie_striker.qg.utils.CustomModelData;
import me.zombie_striker.qg.utils.Compat;
import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.customitemmanager.CustomItemManager;
import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.customitemmanager.OLD_ItemFact;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.ammo.Ammo;
import me.zombie_striker.qg.armor.ArmorObject;
import me.zombie_striker.qg.attachments.AttachmentBase;
import me.zombie_striker.qg.config.GunYML;
import me.zombie_striker.qg.config.GunYMLCreator;
import me.zombie_striker.qg.config.GunYMLLoader;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.guns.utils.WeaponType;
import me.zombie_striker.qg.handlers.HotbarMessager;
import me.zombie_striker.qg.handlers.IronsightsHandler;
import me.zombie_striker.qg.hooks.ViaVersionHook;
import me.zombie_striker.qg.hooks.protection.ProtectionHandler;
import me.zombie_striker.qg.miscitems.AmmoBag;
import me.zombie_striker.qg.utils.LocalUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class QualityArmory {

	public static GunYML createAndLoadNewGun(String name, String displayname, Material material, int id,
			WeaponType type, WeaponSounds sound, boolean hasIronSights, String ammotype, int damage, int maxBullets,
			int cost) {
		File newGunsDir = new File(QAMain.getInstance().getDataFolder(), "newGuns");
		final File gunFile = new File(newGunsDir, name);
		new FoliaRunnable() {
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
		QAMain.getItemRegistry().addExpansionPack(MaterialStorage.getMS(used, id, var));
	}
	public static void registerNewUsedExpansionItem(MaterialStorage ms) {
		QAMain.getItemRegistry().addExpansionPack(ms);
	}

	public static Iterator<Gun> getGuns(){
		return QAMain.getItemRegistry().getGuns().values().iterator();
	}
	public static Iterator<Ammo> getAmmo(){
		return QAMain.getItemRegistry().getAmmo().values().iterator();
	}
	public static Iterator<CustomBaseObject> getMisc(){
		return QAMain.getItemRegistry().getMisc().values().iterator();
	}
	public static Iterator<ArmorObject> getArmor(){
		return QAMain.getItemRegistry().getArmor().values().iterator();
	}
	public static Iterator<CustomBaseObject> getCustomItems(){
		return getCustomItemsAsList().iterator();
	}

	public static List<CustomBaseObject> getCustomItemsAsList(){
		List<CustomBaseObject> list = new ArrayList<>();
		list.addAll(QAMain.getItemRegistry().getGuns().values());
		list.addAll(QAMain.getItemRegistry().getAmmo().values());
		list.addAll(QAMain.getItemRegistry().getArmor().values());
		list.addAll(QAMain.getItemRegistry().getMisc().values());
		return list;
	}


	@SuppressWarnings({"deprecation", "unchecked"})
	public static void sendResourcepack(final Player player, final boolean warning) {
		new FoliaRunnable() {
			@Override
			public void run() {
				if (QAMain.getResourcepacks().isBypassing(player.getName())) {
					QAMain.getResourcepacks().setAccepted(player.getUniqueId());
					return;
				}
				if (warning) {
					if (TITLES) {
						player.sendTitle(LocalUtils.colorize(ChatColor.RED + QAMain.getMessages().resourcepack.downloading), LocalUtils.colorize(QAMain.getMessages().resourcepack.acceptPrompt));
					} else {
						player.sendMessage(LocalUtils.colorize(ChatColor.RED + QAMain.getMessages().resourcepack.downloading));
						player.sendMessage(LocalUtils.colorize(ChatColor.RED + QAMain.getMessages().resourcepack.acceptPrompt));
					}
				}
				if (QAMain.getConfiguration().ui.showPossibleCrashHelpMessage)
					player.sendMessage(LocalUtils.colorize(QAMain.getMessages().general.prefix + QAMain.getMessages().resourcepack.crashHelp));

				new FoliaRunnable() {
					@Override
					public void run() {
						try {
							QAMain.DEBUG("Sending resourcepack : " + (QAMain.getConfiguration().resourcepack.autoDetectVersion) + " || "
									+ QAMain.getConfiguration().general.versionOverride.equalsIgnoreCase("1.8") + " || " + QAMain.isVersionHigherThan(1, 9) + " || ");
							if (QAMain.getIntegrations().hasViaVersion() && QAMain.getConfiguration().debug.enabled) {
								try {
									QAMain.DEBUG("Has Viaversion: " + com.viaversion.viaversion.api.Via.getAPI()
											.getPlayerVersion(player) + " 1.8=" + ViaVersionHook.VIAVERSION_1_8);
								} catch (RuntimeException | LinkageError re4) {
									ErrorReporter.report("reading the client version from ViaVersion", re4);
								}
							}

							String pack = CustomItemManager.getResourcepack(player);
							boolean sent = false;
							if (QAMain.isVersionHigherThan(1, 19) && FORCED_RESOURCEPACK) {
								try {
									player.setResourcePack(pack, (byte[]) null, QAMain.getConfiguration().resourcepack.kickIfDenied);
									sent = true;
								} catch (IllegalArgumentException e4) {
									// Some versions require a hash with this method; fall back to the plain one
								}
							}
							if (!sent)
								player.setResourcePack(pack);

							if (!QAMain.isVersionHigherThan(1, 9)) {
								QAMain.getResourcepacks().setAccepted(player.getUniqueId());
								QAMain.getResourcepacks().markSent(player.getUniqueId());
								QAMain.getResourcepacks().setLoading(player.getUniqueId(), true);
							}
							// If the player is on 1.8, manually add them to the resource list.

						} catch (RuntimeException e) {
							ErrorReporter.report("sending the resourcepack", e);
						}
					}
				}.runTaskLater(QAMain.getInstance(), player, 20 * (warning ? 1 : 5));
			}
		}.runTaskLater(QAMain.getInstance(), player, (long) (20 * QAMain.getConfiguration().resourcepack.sendOnJoin.delay));
	}

	/** {@code Player#sendTitle(String, String)}, missing on early 1.8 builds. */
	private static final boolean TITLES = Compat.hasMethod("org.bukkit.entity.Player", "sendTitle", "java.lang.String", "java.lang.String");
	/** {@code Player#setResourcePack(String, byte[], boolean)} (1.17+). */
	private static final boolean FORCED_RESOURCEPACK = Compat.hasMethod("org.bukkit.entity.Player", "setResourcePack", "java.lang.String", "[B", "boolean");

	public static boolean allowGunsInRegion(Location loc) {
		try {
			return ProtectionHandler.canPvp(loc);
		} catch (LinkageError e) {
			// A protection plugin changed its API; don't block guns because of it
			ErrorReporter.report("checking PvP protection", e);
			return true;
		}
	}

	public static boolean isCustomItem(ItemStack is) {
		return isCustomItem(is, 0);
	}

	@SuppressWarnings("deprecation")
	public static boolean isCustomItemNextId(ItemStack is) {
		if (is == null)
			return false;
		if (CustomItemManager.isUsingCustomData())
			return false;
		List<MaterialStorage> ms = new ArrayList<MaterialStorage>();
		ms.addAll(QAMain.getItemRegistry().getExpansionPacks());
		ms.addAll(QAMain.getItemRegistry().getGuns().keySet());
		ms.addAll(QAMain.getItemRegistry().getArmor().keySet());
		ms.addAll(QAMain.getItemRegistry().getAmmo().keySet());
		ms.addAll(QAMain.getItemRegistry().getMisc().keySet());
		for (MaterialStorage mat : ms) {
			if (mat.getMat() == is.getType())
				if (mat.getData() == (is.getDurability() + 1))
					if (!mat.hasVariant())
						return true;
		}
		return false;
	}

	public static CustomBaseObject getCustomItem(MaterialStorage material) {
		if(QAMain.getItemRegistry().getGuns().containsKey(material))
			return QAMain.getItemRegistry().getGuns().get(material);
		if(QAMain.getItemRegistry().getAmmo().containsKey(material))
			return QAMain.getItemRegistry().getAmmo().get(material);
		if(QAMain.getItemRegistry().getMisc().containsKey(material))
			return QAMain.getItemRegistry().getMisc().get(material);
		if(QAMain.getItemRegistry().getArmor().containsKey(material))
			return QAMain.getItemRegistry().getArmor().get(material);
		return null;
	}

	public static CustomBaseObject getCustomItem(Material material, int data, int variant) {
		ItemStack is = new ItemStack(material);
		if(variant!=0) {
			ItemMeta im = is.getItemMeta();
			List<String> lore = im.getLore();
			OLD_ItemFact.addVariantData(im, lore, variant);
			im.setLore(lore);
			is.setItemMeta(im);
		}
		CustomModelData.setOrDurability(is, data);
		ItemState.setVariant(is, variant);
		return getCustomItem(is);
	}
	public static CustomBaseObject getCustomItem(ItemStack is) {
		if (isGun(is))
			return getGun(is);
		if (isAmmo(is))
			return getAmmo(is);
		if (isArmor(is))
			return getArmor(is);
		if (isMisc(is))
			return getMisc(is);
		return null;
	}

	@SuppressWarnings("deprecation")
	public static boolean isCustomItem(ItemStack is, int dataOffset) {
		if (is == null)
			return false;
		ItemStack itemstack = is.clone();
		if(dataOffset!=0) {
			ItemMeta im = CustomModelData.SUPPORTED ? itemstack.getItemMeta() : null;
			if (im != null) {
				int modeldata = 0;
				if(im.hasCustomModelData()) {
					modeldata = im.getCustomModelData();
				}else if (modeldata+dataOffset >0){
					return false;
				}
				im.setCustomModelData(modeldata + dataOffset);
				itemstack.setItemMeta(im);
			} else {
				itemstack.setDurability((short) (is.getDurability() + dataOffset));
			}
		}
		if(isIronSights(itemstack))
			return true;
		if(isGun(itemstack))
			return true;
		if(isAmmo(itemstack))
			return true;
		if(isMisc(itemstack))
			return true;
		if(isArmor(itemstack))
			return true;
		if(QAMain.getItemRegistry().getExpansionPacks().contains(MaterialStorage.getMS(is)))
			return true;
		return false;
	}

	@SuppressWarnings("deprecation")
	public static boolean isArmor(ItemStack is) {
		if (is == null)
			return false;
		return (is != null
				&& (QAMain.getItemRegistry().getArmor().containsKey(MaterialStorage.getMS(is))
						|| QAMain.getItemRegistry().getArmor().containsKey(MaterialStorage.getMS(is))));
	}

	@SuppressWarnings("deprecation")
	public static ArmorObject getArmor(ItemStack is) {
		if (QAMain.getItemRegistry().getArmor().containsKey(MaterialStorage.getMS(is)))
			return QAMain.getItemRegistry().getArmor().get(MaterialStorage.getMS(is));
		return QAMain.getItemRegistry().getArmor().get(MaterialStorage.getMS(is));
	}

	@SuppressWarnings("deprecation")
	public static boolean isMisc(ItemStack is) {
		if (is == null)
			return false;
		int var = MaterialStorage.getVariant(is);
		return (is != null
				&& (QAMain.getItemRegistry().getMisc().containsKey(MaterialStorage.getMS(is,var))
						|| QAMain.getItemRegistry().getMisc().containsKey(MaterialStorage.getMS(is,var))));
	}

	public static CustomBaseObject getMisc(ItemStack is) {
		return QAMain.getItemRegistry().getMisc().get(MaterialStorage.getMS(is));
	}

	public static Gun getGun(ItemStack is) {
		return QAMain.getItemRegistry().getGuns().get(MaterialStorage.getMS(is));
	}

	@Nullable
	public static Gun getGunInHand(@NotNull HumanEntity entity) {
		ItemStack stack = entity.getInventory().getItemInHand();
		if (stack == null || stack.getType().equals(Material.AIR)) return null;

		if (isGun(stack)) return getGun(stack);
		if (isIronSights(stack)) {
			if (Compat.OFFHAND) {
				ItemStack offHand = entity.getInventory().getItemInOffHand();
				if (isGun(offHand)) {
					return getGun(offHand);
				}
			}
		}

		return null;
	}

	public static boolean isGun(ItemStack is) {
		return (is != null && QAMain.getItemRegistry().getGuns().containsKey(MaterialStorage.getMS(is)));
	}

	@SuppressWarnings("deprecation")
	public static Ammo getAmmo(ItemStack is) {
		int var = MaterialStorage.getVariant(is);
		if (QAMain.getItemRegistry().getAmmo()
				.containsKey(MaterialStorage.getMS(is,var)))
			return QAMain.getItemRegistry().getAmmo()
					.get(MaterialStorage.getMS(is,var));
		return QAMain.getItemRegistry().getAmmo().get(MaterialStorage.getMS(is,var));
	}

	public static Ammo getAmmoByName(String name) {
		for (Entry<MaterialStorage, Ammo> e : QAMain.getItemRegistry().getAmmo().entrySet())
			if (e.getValue().getName().equalsIgnoreCase(name)) {
				return e.getValue();
			}
		return null;
	}

	public static int getAmmoInBag(@NotNull Player player, Ammo a) {
		int amount = 0;

		for (ItemStack is : player.getInventory().getContents()) {
			if (is == null || is.getType().equals(Material.AIR) || !isMisc(is)) continue;

			CustomBaseObject customItem = getMisc(is);
			if (customItem instanceof AmmoBag) {
				Ammo ammoType = ((AmmoBag) customItem).getAmmoType(is);
				if (ammoType == null || !ammoType.equals(a)) continue;

				amount += ((AmmoBag) customItem).getAmmo(is);
			}
		}

		return amount;
	}

	public static CustomBaseObject getCustomItemByName(String name){
		CustomBaseObject b = null;
		if((b=getAmmoByName(name))!=null)
			return b;
		if((b=getGunByName(name))!=null)
			return b;
		if((b=getArmorByName(name))!=null)
			return b;
		if((b=getMiscByName(name))!=null)
			return b;
		return null;
	}


	@SuppressWarnings("deprecation")
	public static boolean isAmmo(ItemStack is) {
		if (is == null)
			return false;
		int var = MaterialStorage.getVariant(is);

		MaterialStorage storage = MaterialStorage.getMS(is,var);

		return QAMain.getItemRegistry().getAmmo().containsKey(storage);
	}

	public static boolean isAmmoBag(ItemStack is) {
		if (is == null)
			return false;
		int var = MaterialStorage.getVariant(is);

		MaterialStorage storage = MaterialStorage.getMS(is,var);

		return QAMain.getItemRegistry().getMisc().containsKey(storage) && QAMain.getItemRegistry().getMisc().get(storage) instanceof AmmoBag;
	}

	@SuppressWarnings("deprecation")
	public static boolean isIronSights(ItemStack is) {
		if (is == null)
			return false;
		if (is.getType() != IronsightsHandler.ironsightsMaterial)
			return false;
		if (CustomModelData.SUPPORTED)
			return CustomModelData.has(is) && is.getItemMeta().getCustomModelData() == IronsightsHandler.ironsightsData;
		return is.getDurability() == IronsightsHandler.ironsightsData;
	}

	public static ArmorObject getArmorByName(String name) {
		for (ArmorObject g : QAMain.getItemRegistry().getArmor().values()) {
			if (g.getName().equals(name))
				return g;
		}
		return null;
	}

	public static CustomBaseObject getMiscByName(String name) {
		for (CustomBaseObject g : QAMain.getItemRegistry().getMisc().values()) {
			if (g.getName().equals(name))
				return g;
		}
		return null;
	}

	public static Gun getGunByName(String name) {
		for (Gun g : QAMain.getItemRegistry().getGuns().values()) {
			if (g.getName().equals(name))
				return g;
		}
		return null;
	}


	public static void sendHotbarGunAmmoCount(final Player p, final CustomBaseObject gun,
			ItemStack usedItem, boolean reloading) {
		Gun g = null;
		AttachmentBase base = null;
		if(gun instanceof AttachmentBase){
			base = (AttachmentBase)gun;
			g = base.getBaseGun();
		}else{
			g = (Gun) gun;
		}
		sendHotbarGunAmmoCount(p,gun,usedItem,reloading,QualityArmory.getBulletsInHand(p),g.getMaxBullets());
	}
		public static void sendHotbarGunAmmoCount(final Player p, final CustomBaseObject gun,
				ItemStack usedItem, boolean reloading, int currentAmountInGun, int maxAmount) {

		final Gun g;
		AttachmentBase base = null;
		if(gun instanceof AttachmentBase){
			base = (AttachmentBase)gun;
			g = base.getBaseGun();
		}else{
			g = (Gun) gun;
		}

		int ammoamount = getAmmoInInventory(p, g.getAmmoType());

		if (QAMain.getConfiguration().ui.showOutOfAmmoTitle && ammoamount <= 0 && Gun.getAmount(p) < 1) {
			p.sendTitle(" ", QAMain.getMessages().weapon.outOfAmmo, 0, 20, 1);
		} else if (QAMain.getConfiguration().ui.showReloadTitle && reloading) {
			for (int i = 1; i < g.getReloadTime() * 20; i += 2) {
				final int id = i;
				new FoliaRunnable() {
					@Override
					public void run() {
						StringBuilder sb = new StringBuilder();
						sb.append(ChatColor.GRAY);
						sb.append(repeat("#", (int) (20 * (1.0 * id / (20 * g.getReloadTime())))));
						sb.append(ChatColor.DARK_GRAY);
						sb.append(repeat("#", (int) (20 - ((int) (20.0 * id / (20 * g.getReloadTime()))))));
						p.sendTitle(QAMain.getMessages().weapon.reloading, sb.toString(), 0, 4, 0);
					}
				}.runTaskLater(QAMain.getInstance(), p, i);
			}
		} else {

			try {
				String message = QAMain.getMessages().weapon.hotbarFormat;

				if (QAMain.getConfiguration().ui.disableHotbarMessages.outOfAmmo && QAMain.getConfiguration().ui.disableHotbarMessages.reload
						&& QAMain.getConfiguration().ui.disableHotbarMessages.shoot)
					return;
				if (reloading && QAMain.getConfiguration().ui.disableHotbarMessages.reload)
					return;
				if (ammoamount <= 0 && QAMain.getConfiguration().ui.disableHotbarMessages.outOfAmmo)
					return;
				if (!reloading && ammoamount > 0 && QAMain.getConfiguration().ui.disableHotbarMessages.shoot)
					return;

				if (message.contains("%name%"))
					message = message.replace("%name%",
							(base != null ? base.getDisplayName() : g.getDisplayName()));
				if (message.contains("%amount%"))
					message = message.replace("%amount%", currentAmountInGun + "");
				if (message.contains("%max%"))
					message = message.replace("%max%", maxAmount + "");

				if (message.contains("%state%"))
					message = message.replace("%state%", reloading ? QAMain.getMessages().weapon.reloading
							: ammoamount <= 0 ? QAMain.getMessages().weapon.outOfAmmo : QAMain.getMessages().weapon.ammoCount);
				if (message.contains("%total%"))
					message = message.replace("%total%", "" + ammoamount);

				if (QAMain.getConfiguration().features.unknownTranslationKeyFixer) {
					message = ChatColor.stripColor(message);
				} else {
					message = LocalUtils.colorize(message);
				}
				HotbarMessager.sendHotBarMessage(p, message);
			} catch (Exception | LinkageError e5) {
				ErrorReporter.report("sending the hotbar ammo message", e5);
			}
		}
	}

	@SuppressWarnings("deprecation")
	public static int findSafeSpot(ItemStack newItem, boolean findHighest, boolean allowPockets) {
		if(CustomItemManager.isUsingCustomData())
			return -1;
		return findSafeSpot(newItem.getType(), CustomModelData.getOrDurability(newItem), findHighest, allowPockets);
	}

	public static int findSafeSpot(Material newItemtype, int startingData, boolean findHighest, boolean allowPockets) {
		int safeDurib = startingData;
		if (allowPockets) {
			List<Integer> idsToWorryAbout = new ArrayList<>();
			for (MaterialStorage j : QAMain.getItemRegistry().getAmmo().keySet())
				if (j.getMat() == newItemtype && ((j.getData() > safeDurib) == findHighest))
					idsToWorryAbout.add(j.getData());
			for (MaterialStorage j : QAMain.getItemRegistry().getGuns().keySet())
				if (j.getMat() == newItemtype && ((j.getData() > safeDurib) == findHighest))
					idsToWorryAbout.add(j.getData());
			for (MaterialStorage j : QAMain.getItemRegistry().getMisc().keySet())
				if (j.getMat() == newItemtype && ((j.getData() > safeDurib) == findHighest))
					idsToWorryAbout.add(j.getData());
			for (MaterialStorage j : QAMain.getItemRegistry().getArmor().keySet())
				if (j.getMat() == newItemtype && ((j.getData() > safeDurib) == findHighest))
					idsToWorryAbout.add(j.getData());
			for (MaterialStorage j : QAMain.getItemRegistry().getExpansionPacks())
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

		for (MaterialStorage j : QAMain.getItemRegistry().getAmmo().keySet())
			if (j.getMat() == newItemtype && (j.getData() > safeDurib) == findHighest)
				safeDurib = j.getData();
		for (MaterialStorage j : QAMain.getItemRegistry().getGuns().keySet())
			if (j.getMat() == newItemtype && (j.getData() > safeDurib) == findHighest)
				safeDurib = j.getData();
		for (MaterialStorage j : QAMain.getItemRegistry().getMisc().keySet())
			if (j.getMat() == newItemtype && (j.getData() > safeDurib) == findHighest)
				safeDurib = j.getData();
		for (MaterialStorage j : QAMain.getItemRegistry().getArmor().keySet())
			if (j.getMat() == newItemtype && (j.getData() > safeDurib) == findHighest)
				safeDurib = j.getData();
		for (MaterialStorage j : QAMain.getItemRegistry().getExpansionPacks())
			if (j.getMat() == newItemtype && (j.getData() > safeDurib) == findHighest)
				safeDurib = j.getData();
		return safeDurib;
	}

	@SuppressWarnings("deprecation")
	public static int findSafeSpotVariant(ItemStack newItem, boolean findHighest) {
		return findSafeSpotVariant(newItem.getType(), CustomModelData.getOrDurability(newItem), findHighest);
	}

	public static int findSafeSpotVariant(Material newItemtype, int startingData, boolean findHighest) {
		int safeDurib = 0;
		for (MaterialStorage j : QAMain.getItemRegistry().getAmmo().keySet())
			if (j.getMat() == newItemtype && (j.getData() == startingData)
					&& ((j.getVariant()> safeDurib) == findHighest))
				safeDurib = j.getVariant();
		for (MaterialStorage j : QAMain.getItemRegistry().getGuns().keySet())
			if (j.getMat() == newItemtype && (j.getData() == startingData)
					&& ((j.getVariant() > safeDurib) == findHighest))
				safeDurib = j.getVariant();
		for (MaterialStorage j : QAMain.getItemRegistry().getMisc().keySet())
			if (j.getMat() == newItemtype && (j.getData() == startingData)
					&& ((j.getVariant() > safeDurib) == findHighest))
				safeDurib = j.getVariant();
		for (MaterialStorage j : QAMain.getItemRegistry().getArmor().keySet())
			if (j.getMat() == newItemtype && (j.getData() == startingData)
					&& ((j.getVariant() > safeDurib) == findHighest))
				safeDurib = j.getVariant();
		for (MaterialStorage j : QAMain.getItemRegistry().getExpansionPacks())
			if (j.getMat() == newItemtype && (j.getData() == startingData)
					&& ((j.getVariant() > safeDurib) == findHighest))
				safeDurib = j.getVariant();
		return safeDurib;
	}

	public static int getMaxPagesForGUI() {
		return (QAMain.getItemRegistry().getArmor().size() + QAMain.getItemRegistry().getAmmo().size() + QAMain.getItemRegistry().getMisc().size()
				+ QAMain.getItemRegistry().getGuns().size()) / (9 * 5);
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
		return count >= (g.isPrimaryWeapon() ? QAMain.getConfiguration().limiter.primary.max : QAMain.getConfiguration().limiter.secondary.max);
	}

	public static ItemStack getCustomItemAsItemStack(String name) {
		return getCustomItemAsItemStack(getCustomItemByName(name));
	}

	public static ItemStack getCustomItemAsItemStack(CustomBaseObject obj) {
		if (obj == null) return null;
		return CustomItemManager.getItemType("gun").getItem(obj.getItemData());
	}

	public static ItemStack getIronSightsItemStack() {
		return OLD_ItemFact.getIronSights();
	}




	public static int getAmmoInInventory(Player player, Ammo a) {
		return getAmmoInInventory(player,a,false);
	}

	public static int getAmmoInInventory(Player player, Ammo a, boolean ignoreBag) {
		int amount = 0;
		if(player.getGameMode()==GameMode.CREATIVE)
			return 99999;
		for (ItemStack is : player.getInventory().getContents()) {
			if (isAmmo(is) && getAmmo(is).equals(a)) {
				amount += is.getAmount();
			}
		}
		return ignoreBag ? amount : amount + getAmmoInBag(player, a);
	}

	public static boolean addAmmoToInventory(Player player, Ammo a, int amount) {
		int remaining = amount;
		for (int i = 0; i < player.getInventory().getSize(); i++) {
			ItemStack is = player.getInventory().getItem(i);
			if (is != null && QualityArmory.isAmmo(is) && QualityArmory.getAmmo(is).equals(a)) {
				if (is.getAmount() + remaining <= a.getMaxItemStack()) {
					is.setAmount(is.getAmount() + remaining);
					remaining = 0;
				} else {
					remaining -= a.getMaxItemStack() - is.getAmount();
					is.setAmount(a.getMaxItemStack());
				}
				player.getInventory().setItem(i, is);
				if (remaining <= 0)
					break;
			}
		}
		if (remaining > 0) {
			ItemStack is = getCustomItemAsItemStack(a);
			is.setAmount(remaining);
			giveOrDrop(player, is);
			remaining = 0;
		}

		return true;
	}

	public static int getBulletsInHand(Player player){
		return Gun.getAmount(player);
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

		if (remaining > 0) {
			for (int i = 0; i < player.getInventory().getSize(); i++) {
				ItemStack is = player.getInventory().getItem(i);
				if (QualityArmory.isAmmoBag(is)) {
					AmmoBag ab = (AmmoBag) QualityArmory.getCustomItem(is);
					if (ab == null) continue;

					Ammo ammoType = ab.getAmmoType(is);
					if (ammoType != null && ammoType.equals(a)) {
						int amountInBag = ab.getAmmo(is);

						if (amountInBag >= remaining) {
							ab.updateAmmoLore(is, amountInBag - remaining);
							remaining = 0;
						} else {
							ab.updateAmmoLore(is, 0);
							remaining -= amountInBag;
						}
					}

				}
			}
		}

		return remaining <= 0;
	}

	public static void giveOrDrop(HumanEntity entity, ItemStack item) {
		if (entity.getInventory().firstEmpty() != -1) {
			entity.getInventory().addItem(item);
		} else {
			entity.getWorld().dropItem(entity.getLocation(), item);
		}
	}

	public static String repeat(String string, int times) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < times; i++) {
			sb.append(string);
		}
		return sb.toString();
	}
}
