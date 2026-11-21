package me.zombie_striker.qg.handlers;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.config.MainConfig;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.utils.Compat;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Handles repairing guns' durability, either by clicking a repair item onto a gun
 * in an inventory or through the Mending enchantment.
 */
public class GunRepairHandler {

	public static final String REPAIR_PERMISSION = "qualityarmory.repair";

	/**
	 * @return the repair item name configured for this gun, or null if the gun can't be repaired with an item
	 */
	public static String getRepairItemName(Gun gun) {
		String name = gun.getRepairItem() != null ? gun.getRepairItem() : config().defaultItem;
		if (name == null)
			return null;
		name = name.trim();
		if (name.isEmpty() || name.equalsIgnoreCase("none"))
			return null;
		return name;
	}

	public static int getRepairAmount(Gun gun) {
		return gun.getRepairAmount() >= 0 ? gun.getRepairAmount() : config().defaultAmount;
	}

	/**
	 * Checks if an item can be used to repair a gun. The repair item can either be a QualityArmory
	 * custom item (matched by name) or a vanilla material.
	 */
	public static boolean isRepairItem(Gun gun, ItemStack item) {
		if (item == null || item.getType() == Material.AIR)
			return false;
		String name = getRepairItemName(gun);
		if (name == null)
			return false;

		CustomBaseObject itemCustom = QualityArmory.getCustomItem(item);
		CustomBaseObject repairCustom = QualityArmory.getCustomItemByName(name);
		if (repairCustom != null)
			return repairCustom == itemCustom;

		// Never treat a QA custom item as its vanilla base material
		if (itemCustom != null || QualityArmory.isIronSights(item))
			return false;

		Material material = XMaterial.matchXMaterial(name).map(XMaterial::parseMaterial).orElse(null);
		if (material == null)
			material = Material.matchMaterial(name);
		return material != null && item.getType() == material;
	}

	private static MainConfig.Weapons.Repair config() {
		return QAMain.getConfiguration().weapons.repair;
	}

	/**
	 * A reloading gun is restored from a snapshot when the reload finishes,
	 * so any repair applied in the meantime would be lost.
	 */
	public static boolean isReloading(ItemStack item) {
		return item != null && Gun.isReloading(item);
	}

	/**
	 * Repairs the gun clicked with a repair item on the cursor.
	 * Left click uses as many repair items as needed to fully repair the gun, right click uses a single one.
	 *
	 * @return true if the click was handled (and cancelled) as a repair
	 */
	public static boolean handleRepairClick(InventoryClickEvent e) {
		if (!QAMain.getConfiguration().weapons.durability || !config().enabled)
			return false;
		if (e.getClick() != ClickType.LEFT && e.getClick() != ClickType.RIGHT)
			return false;
		if (!(e.getWhoClicked() instanceof Player))
			return false;
		// In creative mode the client controls the inventory, so we can't reliably edit it
		if (e instanceof InventoryCreativeEvent || ((Player) e.getWhoClicked()).getGameMode() == GameMode.CREATIVE)
			return false;
		// Only repair guns stored in regular slots. Result slots (anvil, grindstone, crafting...)
		// are recalculated from their inputs, so the repair would be lost.
		InventoryType.SlotType slotType = e.getSlotType();
		if (slotType != InventoryType.SlotType.CONTAINER && slotType != InventoryType.SlotType.QUICKBAR)
			return false;

		ItemStack cursor = e.getCursor();
		ItemStack current = e.getCurrentItem();
		if (cursor == null || current == null || cursor.getType() == Material.AIR)
			return false;

		// Only repair a single gun, never a stack
		if (current.getAmount() != 1 || isReloading(current))
			return false;

		Gun gun = QualityArmory.getGun(current);
		if (gun == null || !isRepairItem(gun, cursor))
			return false;

		Player player = (Player) e.getWhoClicked();
		if (config().requirePermission && !player.hasPermission(REPAIR_PERMISSION))
			return false;

		int max = gun.getDurability();
		int durability = Gun.getDamage(current);
		int perItem = getRepairAmount(gun);
		if (durability < 0 || durability >= max || perItem <= 0)
			return false;

		int needed = (int) Math.ceil((double) (max - durability) / perItem);
		int used = e.getClick() == ClickType.RIGHT ? 1 : Math.min(needed, cursor.getAmount());
		int newDurability = (int) Math.min(max, durability + (long) used * perItem);

		e.setCancelled(true);

		ItemStack repaired = Gun.setDurabilityDamage(gun, current.clone(), newDurability);
		e.setCurrentItem(repaired);

		ItemStack newCursor = null;
		if (cursor.getAmount() > used) {
			newCursor = cursor.clone();
			newCursor.setAmount(cursor.getAmount() - used);
		}
		player.setItemOnCursor(newCursor);

		XSound.BLOCK_ANVIL_USE.play(player, 0.7f, 1);
		QAMain.DEBUG("Repaired " + gun.getName() + " from " + durability + " to " + newDurability + " using " + used + " item(s)");
		return true;
	}

	/**
	 * Uses picked up experience to repair the held gun if it is enchanted with Mending,
	 * similarly to how vanilla Mending works.
	 */
	public static void handleMending(PlayerExpChangeEvent e) {
		if (!QAMain.getConfiguration().weapons.durability || !config().mending.enabled || e.getAmount() <= 0)
			return;
		// Mending was added in 1.9
		if (!Compat.supports(9))
			return;

		Enchantment mending = XEnchantment.MENDING.get();
		if (mending == null)
			return;

		Player player = e.getPlayer();
		ItemStack item = player.getItemInHand();
		boolean offhand = false;
		// While aiming, the gun is moved to the offhand
		if (QualityArmory.isIronSights(item)) {
			item = Update19OffhandChecker.getItemStackOFfhand(player);
			offhand = true;
		}

		Gun gun = QualityArmory.getGun(item);
		if (gun == null || item.getAmount() != 1 || isReloading(item) || !item.containsEnchantment(mending))
			return;
		if (config().requirePermission && !player.hasPermission(REPAIR_PERMISSION))
			return;

		int max = gun.getDurability();
		int durability = Gun.getDamage(item);
		if (durability < 0 || durability >= max)
			return;

		int perXP = Math.max(1, config().mending.durabilityPerXp);
		int repaired = (int) Math.min((long) e.getAmount() * perXP, max - durability);
		int xpUsed = (int) Math.ceil((double) repaired / perXP);

		ItemStack updated = Gun.setDurabilityDamage(gun, item.clone(), durability + repaired);
		if (offhand) {
			Update19OffhandChecker.setOffhand(player, updated);
		} else {
			player.setItemInHand(updated);
		}
		e.setAmount(e.getAmount() - xpUsed);
		QAMain.DEBUG("Mending repaired " + gun.getName() + " by " + repaired + " using " + xpUsed + " xp");
	}
}
