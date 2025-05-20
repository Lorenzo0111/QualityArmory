package me.zombie_striker.qg.api;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import me.zombie_striker.customitemmanager.*;
import me.zombie_striker.qg.ammo.AmmoBox;
import me.zombie_striker.qg.handlers.HotbarMessager;
import me.zombie_striker.qg.handlers.IronsightsHandler;
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
import org.bukkit.scheduler.BukkitRunnable;

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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
	public static void registerNewUsedExpansionItem(MaterialStorage ms) {
		QAMain.expansionPacks.add(ms);
	}

	public static Iterator<Gun> getGuns(){
		return QAMain.gunRegister.values().iterator();
	}
	public static Iterator<Ammo> getAmmo(){
		return QAMain.ammoRegister.values().iterator();
	}
	public static Iterator<CustomBaseObject> getMisc(){
		return QAMain.miscRegister.values().iterator();
	}
	public static Iterator<ArmorObject> getArmor(){
		return QAMain.armorRegister.values().iterator();
	}
	public static Iterator<CustomBaseObject> getCustomItems(){
		return getCustomItemsAsList().iterator();
	}

	public static List<CustomBaseObject> getCustomItemsAsList(){
		List<CustomBaseObject> list = new ArrayList<>();
		list.addAll(QAMain.gunRegister.values());
		list.addAll(QAMain.ammoRegister.values());
		list.addAll(QAMain.armorRegister.values());
		list.addAll(QAMain.miscRegister.values());
		return list;
	}


	@SuppressWarnings({"deprecation", "unchecked"})
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
						player.sendTitle(LocalUtils.colorize(ChatColor.RED + QAMain.S_NORES1), LocalUtils.colorize(QAMain.S_NORES2));
					} catch (Error e2) {
						player.sendMessage(LocalUtils.colorize(ChatColor.RED + QAMain.S_NORES1));
						player.sendMessage(LocalUtils.colorize(ChatColor.RED + QAMain.S_NORES2));
					}
				}
				if (QAMain.showCrashMessage)
					player.sendMessage(LocalUtils.colorize(QAMain.prefix + QAMain.S_RESOURCEPACK_HELP));

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
												"Has Viaversion: " + com.viaversion.viaversion.api.Via.getAPI()
														.getPlayerVersion(player) + " 1.8=" + QAMain.ViaVersionIdfor_1_8);

									}
								} catch (Error | Exception re4) {
								}

								if (QAMain.isVersionHigherThan(1, 19))
									player.setResourcePack(CustomItemManager.getResourcepack(player), null, QAMain.kickIfDeniedRequest);
                                else player.setResourcePack(CustomItemManager.getResourcepack(player));

							} catch (Error | Exception e4) {

								player.setResourcePack(CustomItemManager.getResourcepack(player));
							}

							if (!QAMain.isVersionHigherThan(1, 9)) {
								QAMain.resourcepackReq.add(player.getUniqueId());
								QAMain.sentResourcepack.put(player.getUniqueId(), System.currentTimeMillis());
								QAMain.resourcepackLoading.add(player.getUniqueId());
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
		try {
			return ProtectionHandler.canPvp(loc);
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

	public static CustomBaseObject getCustomItem(MaterialStorage material) {
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

	public static CustomBaseObject getCustomItem(Material material, int data, int variant) {
		ItemStack is = new ItemStack(material);
		if(variant!=0) {
			ItemMeta im = is.getItemMeta();
			List<String> lore = im.getLore();
			OLD_ItemFact.addVariantData(im, lore, variant);
			im.setLore(lore);
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
			try {
				ItemMeta im = itemstack.getItemMeta();
				int modeldata = 0;
				if(im.hasCustomModelData()) {
					modeldata = im.getCustomModelData();
				}else if (modeldata+dataOffset >0){
					return false;
				}
				im.setCustomModelData(modeldata + dataOffset);
				itemstack.setItemMeta(im);
			} catch (Error | Exception ed4) {
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
		if(QAMain.expansionPacks.contains(MaterialStorage.getMS(is)))
			return true;
		return false;
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

	public static CustomBaseObject getMisc(ItemStack is) {
		return QAMain.miscRegister.get(MaterialStorage.getMS(is));
	}

	public static Gun getGun(ItemStack is) {
		return QAMain.gunRegister.get(MaterialStorage.getMS(is));
	}

	@Nullable
	public static Gun getGunInHand(@NotNull HumanEntity entity) {
		ItemStack stack = entity.getInventory().getItemInHand();
		if (stack == null || stack.getType().equals(Material.AIR)) return null;

		if (isGun(stack)) return getGun(stack);
		if (isIronSights(stack)) {
			try {
				ItemStack offHand = entity.getInventory().getItemInOffHand();
				if (isGun(offHand)) {
					return getGun(offHand);
				}
			} catch (NoSuchMethodError ignored) {}
		}

		return null;
	}

	public static boolean isGun(ItemStack is) {
		return (is != null && QAMain.gunRegister.containsKey(MaterialStorage.getMS(is)));
	}

	@SuppressWarnings("deprecation")
	public static Ammo getAmmo(ItemStack is) {
		int var = MaterialStorage.getVariant(is);
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

		return QAMain.ammoRegister.containsKey(storage);
	}

	public static boolean isAmmoBag(ItemStack is) {
		if (is == null)
			return false;
		int var = MaterialStorage.getVariant(is);

		MaterialStorage storage = MaterialStorage.getMS(is,var);

		return QAMain.miscRegister.containsKey(storage) && QAMain.miscRegister.get(storage) instanceof AmmoBag;
	}

	@SuppressWarnings("deprecation")
	public static boolean isIronSights(ItemStack is) {
		if (is == null)
			return false;
		if (is.getType() == IronsightsHandler.ironsightsMaterial)
			try{
				if(!is.hasItemMeta() || !is.getItemMeta().hasCustomModelData())
					return false;
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

	public static CustomBaseObject getMiscByName(String name) {
		for (CustomBaseObject g : QAMain.miscRegister.values()) {
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

		if (QAMain.showOutOfAmmoOnTitle && ammoamount <= 0 && Gun.getAmount(p) < 1) {
			p.sendTitle(" ", QAMain.S_OUT_OF_AMMO, 0, 20, 1);
		} else if (QAMain.showReloadOnTitle && reloading) {
			for (int i = 1; i < g.getReloadTime() * 20; i += 2) {
				final int id = i;
				new BukkitRunnable() {
					@Override
					public void run() {
						StringBuilder sb = new StringBuilder();
						sb.append(ChatColor.GRAY);
						sb.append(repeat("#", (int) (20 * (1.0 * id / (20 * g.getReloadTime())))));
						sb.append(ChatColor.DARK_GRAY);
						sb.append(repeat("#", (int) (20 - ((int) (20.0 * id / (20 * g.getReloadTime()))))));
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
							(base != null ? base.getDisplayName() : g.getDisplayName()));
				if (message.contains("%amount%"))
					message = message.replace("%amount%", currentAmountInGun + "");
				if (message.contains("%max%"))
					message = message.replace("%max%", maxAmount + "");

				if (message.contains("%state%"))
					message = message.replace("%state%", reloading ? QAMain.S_RELOADING_MESSAGE
							: ammoamount <= 0 ? QAMain.S_OUT_OF_AMMO : QAMain.S_MAX_FOUND);
				if (message.contains("%total%"))
					message = message.replace("%total%", "" + ammoamount);

				if (QAMain.unknownTranslationKeyFixer) {
					message = ChatColor.stripColor(message);
				} else {
					message = LocalUtils.colorize(message);
				}
				HotbarMessager.sendHotBarMessage(p, message);
			} catch (Error | Exception e5) {
			}
		}
	}

	@SuppressWarnings("deprecation")
	public static int findSafeSpot(ItemStack newItem, boolean findHighest, boolean allowPockets) {
		if(CustomItemManager.isUsingCustomData())
			return -1;
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
		for (MaterialStorage j : QAMain.expansionPacks)
			if (j.getMat() == newItemtype && (j.getData() == startingData)
					&& ((j.getVariant() > safeDurib) == findHighest))
				safeDurib = j.getVariant();
		return safeDurib;
	}

	public static int getMaxPagesForGUI() {
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
			if (player.getInventory().firstEmpty() >= 0) {
				ItemStack is = getCustomItemAsItemStack(a);
				is.setAmount(remaining);
				player.getInventory().addItem(is);
				remaining = 0;
			}
		}
		return remaining <= 0;
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

						int newAmount = 0;

						if (amountInBag >= remaining) {
							ab.updateAmmoLore(is, newAmount = (amountInBag - remaining));
							remaining = 0;
						} else {
							ab.updateAmmoLore(is, 0);
							remaining -= amountInBag;
						}

						if (newAmount == 0 && ab instanceof AmmoBox)
							player.getInventory().setItem(i, new ItemStack(Material.AIR));

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
