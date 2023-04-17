package me.zombie_striker.qualityarmory.listener;

import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.customitemmanager.CustomItemManager;
import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.qualityarmory.QAMain;
import me.zombie_striker.qualityarmory.ammo.Ammo;
import me.zombie_striker.qualityarmory.api.QualityArmory;
import me.zombie_striker.qualityarmory.api.events.QACustomItemInteractEvent;
import me.zombie_striker.qualityarmory.api.events.QAGunGiveEvent;
import me.zombie_striker.qualityarmory.attachments.AttachmentBase;
import me.zombie_striker.qualityarmory.guns.Gun;
import me.zombie_striker.qualityarmory.guns.utils.GunRefillerRunnable;
import me.zombie_striker.qualityarmory.guns.utils.GunUtil;
import me.zombie_striker.qualityarmory.handlers.EconHandler;
import me.zombie_striker.qualityarmory.interfaces.ISettingsReloader;
import me.zombie_striker.qualityarmory.miscitems.Grenade;
import me.zombie_striker.qualityarmory.miscitems.MeleeItems;
import me.zombie_striker.qualityarmory.utils.IronsightsUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class QAListener implements Listener, ISettingsReloader {

	private QAMain main;

	public QAListener(QAMain main){
		this.main = main;
		main.registerSettingReloader(this);
	}

	@EventHandler
	public void onHopperpickup(InventoryPickupItemEvent e) {
		if (e.isCancelled())
			return;
		if (e.getInventory().getType() == InventoryType.HOPPER) {
			if (QualityArmory.getInstance().isGun(e.getItem().getItemStack()))
				e.setCancelled(true);

			if (Grenade.getGrenades().contains(e.getItem())) {
				e.setCancelled(true);
				DEBUG("Cancelled item pickup event because it was a grenade");
			}
		}
	}

	@EventHandler
	public void onHopper(InventoryMoveItemEvent e) {
		if (e.isCancelled())
			return;
		if (e.getSource().getType() == InventoryType.HOPPER || e.getSource().getType() == InventoryType.DISPENSER
				|| e.getSource().getType() == InventoryType.DROPPER)
			if (QualityArmory.getInstance().isGun(e.getItem()))
				e.setCancelled(true);
	}


	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST)
	public void oninvClick(final InventoryClickEvent e) {
		if (e.isCancelled())
			return;
		String name = null;

		if(e.getClickedInventory() instanceof PlayerInventory) {
			ItemStack cursor = e.getCursor();
			final int OFFHAND_SLOT = 40;
			// null check is done by isGun
			// offhand slot check also works with hotkey inventory swap (F by default) - tested on 1.16.5
			if(e.getSlot() == OFFHAND_SLOT && QualityArmory.isGun(cursor)) {
				e.setCancelled(true);
				// restore placed item because cancelling an event seems to wipe it.
				// it will make cursor item look disappeared, but it'll be dropped when inventory is closed
				e.getView().setCursor(cursor);
			}
		}

		if (e.getView().getTitle().startsWith((QAMain.S_craftingBenchName)) || e.getView().getTitle().startsWith((QAMain.S_shopName))) {
			if (e.getClick().isShiftClick()) {
				e.setCancelled(true);
				return;
			}
		}

		name = e.getView().getTitle();

		if (name != null && (name.startsWith(QAMain.S_craftingBenchName) || name.startsWith(QAMain.S_shopName))) {
			DEBUG("ClickedShop");

			boolean shop = (name.startsWith(QAMain.S_shopName));

			e.setCancelled(true);

			if (shop) {

				if (!main.hasVault()) {
					e.getWhoClicked().closeInventory();
					e.getWhoClicked().sendMessage(main.getPrefix() + QAMain.S_noEcon);
					return;
				}

			}

			if (e.getCurrentItem() != null) {
				if (shop) {
					if (e.getCurrentItem().isSimilar(QAMain.prevButton)) {
						int page = Integer.parseInt(e.getView().getTitle().split(QAMain.S_shopName)[1]) - 1;
						e.getWhoClicked().closeInventory();
						e.getWhoClicked().openInventory(QAMain.createShop(Math.max(0, page)));
						DEBUG("Prev_Shop");
						return;
					}
					if (e.getCurrentItem().isSimilar(QAMain.nextButton)) {
						int page = Integer.parseInt(e.getView().getTitle().split(QAMain.S_shopName)[1]) + 1;
						e.getWhoClicked().closeInventory();
						e.getWhoClicked().openInventory(QAMain.createShop(Math.min(QualityArmory.getMaxPagesForGUI(), page)));
						DEBUG("next_Shop");
						return;
					}
				} else {
					if (e.getCurrentItem().isSimilar(QAMain.prevButton)) {
						int page = Integer.parseInt(e.getView().getTitle().split(QAMain.S_craftingBenchName)[1]) - 1;
						e.getWhoClicked().closeInventory();
						e.getWhoClicked().openInventory(QAMain.createCraft(Math.max(0, page)));
						DEBUG("Prev_craft");
						return;
					}
					if (e.getCurrentItem().isSimilar(QAMain.nextButton)) {
						int page = Integer.parseInt(e.getView().getTitle().split(QAMain.S_craftingBenchName)[1]) + 1;
						e.getWhoClicked().closeInventory();
						e.getWhoClicked().openInventory(QAMain.createCraft(Math.min(QualityArmory.getMaxPagesForGUI(), page)));
						DEBUG("next_craft");
						return;
					}
				}
				if (QualityArmory.isAmmo(e.getCurrentItem())) {
					Ammo g = QualityArmory.getAmmo(e.getCurrentItem());
					if (g.getPrice() < 0 || !g.isEnableShop())
						return;
					if ((shop && EconHandler.hasEnough(g, (Player) e.getWhoClicked()))
							|| (!shop && QAMain.lookForIngre((Player) e.getWhoClicked(), g))
							|| (!shop && e.getWhoClicked().getGameMode() == GameMode.CREATIVE)) {
						if (shop) {
							e.getWhoClicked().sendMessage(
									QAMain.S_BUYCONFIRM.replaceAll("%item%", ChatColor.stripColor(g.getDisplayName()))
											.replaceAll("%cost%", "" + g.getPrice()));
							EconHandler.pay(g, (Player) e.getWhoClicked());
						} else
							QAMain.removeForIngredient((Player) e.getWhoClicked(), g);
						QualityArmory.addAmmoToInventory((Player) e.getWhoClicked(), g, g.getCraftingReturn());
						QAMain.shopsSounds(e, shop);
						DEBUG("Buy-ammo");
					} else {
						e.getWhoClicked().closeInventory();
						DEBUG("Failed to buy/craft ammo");
						if (shop)
							e.getWhoClicked().sendMessage(main.getPrefix()+ QAMain.S_noMoney);
						else
							e.getWhoClicked().sendMessage(main.getPrefix() + QAMain.S_missingIngredients);
						try {
							((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(),
									Sound.BLOCK_ANVIL_BREAK, 1, 1);
						} catch (Error e2) {
							((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(),
									Sound.valueOf("ANVIL_BREAK"), 1, 1);
						}
					}
				} else if (QualityArmory.isCustomItem(e.getCurrentItem())) {
					CustomBaseObject g = QualityArmory.getCustomItem(e.getCurrentItem());
					if (g.getPrice() < 0 || !g.isEnableShop())
						return;
					if ((shop && main.getEconHandler().hasEnough(g, (Player) e.getWhoClicked()))
							|| (!shop && QAMain.lookForIngre((Player) e.getWhoClicked(), g))
							|| (!shop && e.getWhoClicked().getGameMode() == GameMode.CREATIVE)) {
						if (shop) {
							EconHandler.pay(g, (Player) e.getWhoClicked());
							e.getWhoClicked().sendMessage(
									QAMain.S_BUYCONFIRM.replaceAll("%item%", ChatColor.stripColor(g.getDisplayName()))
											.replaceAll("%cost%", "" + g.getPrice()));
						} else
							QAMain.removeForIngredient((Player) e.getWhoClicked(), g);

						if (g instanceof Gun) {
							QAGunGiveEvent event = new QAGunGiveEvent(((Player) e.getWhoClicked()), ((Gun) g), QAGunGiveEvent.Cause.SHOP);
							if (event.isCancelled()) return;
							g = event.getGun();
						}

						ItemStack s = CustomItemManager.getItemType("gun").getItem(g.getItemData().getMat(), g.getItemData().getData(), g.getItemData().getVariant());
						QualityArmory.giveOrDrop(e.getWhoClicked(),s);
						QAMain.shopsSounds(e, shop);
						DEBUG("Buy-Item");
					} else {
						DEBUG("Failed to buy/craft Item");
						e.getWhoClicked().closeInventory();
						if (shop)
							e.getWhoClicked().sendMessage(main.getPrefix() + QAMain.S_noMoney);
						else
							e.getWhoClicked().sendMessage(main.getPrefix() + QAMain.S_missingIngredients);
						try {
							((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(),
									Sound.BLOCK_ANVIL_BREAK, 1, 1);
						} catch (Error e2) {
							((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(),
									Sound.valueOf("ANVIL_BREAK"), 1, 1);
						}
					}
				} else {
					e.setCancelled(true);
				}
			}
			return;
		}

		// player inv

		if ((e.getCurrentItem() != null && QualityArmory.isIronSights(e.getCurrentItem()))) {
			e.setCancelled(true);
			return;
		}
		try {
			if (e.getSlot() == 40 && e.getClickedInventory() == e.getWhoClicked().getInventory()
					&& QualityArmory.getInstance().isGun(e.getClickedInventory().getItem(40))) {
				e.setCancelled(true);
			}
		} catch (Error | Exception r5) {
			if (e.getSlot() == 40 && e.getInventory() == e.getWhoClicked().getInventory()
					&& QualityArmory.getInstance().isGun(e.getInventory().getItem(40))) {
				e.setCancelled(true);
			}

		}

		if (QualityArmory.getInstance().isAmmo(e.getCurrentItem())) {
			Ammo current = QualityArmory.getInstance().getAmmo(e.getCurrentItem());
			if (e.getCursor() == null) {
				e.setCursor(e.getCurrentItem());
				e.setCurrentItem(null);
				DEBUG("Clicked ammo: Swap: " + e.getSlot());
			} else if (QualityArmory.getInstance().isAmmo(e.getCursor())) {
				Ammo cursor = QualityArmory.getInstance().getAmmo(e.getCursor());
				if (current == cursor) {
					if (e.getCurrentItem().getAmount() < current.getMaxItemStack()) {
						e.setCancelled(true);
						ItemStack tempCur = e.getCurrentItem();
						if (e.isLeftClick()) {
							int required = current.getMaxItemStack() - e.getCurrentItem().getAmount();
							if (required <= e.getCursor().getAmount()) {
								tempCur.setAmount(current.getMaxItemStack());
								ItemStack tempCurs = e.getCursor();
								if (required == e.getCursor().getAmount()) {
									tempCurs = null;
								} else {
									tempCurs.setAmount(e.getCursor().getAmount() - required);
								}
								e.getClickedInventory().setItem(e.getSlot(), tempCur);
								e.getWhoClicked().setItemOnCursor(tempCurs);
								DEBUG("Clicked ammo: Half-Merger: " + e.getSlot());
							} else {
								tempCur.setAmount(tempCur.getAmount() + e.getCursor().getAmount());
								e.getClickedInventory().setItem(e.getSlot(), tempCur);
								e.getWhoClicked().setItemOnCursor(null);
								DEBUG("Clicked ammo: Full-Merger: " + e.getSlot());
							}
						} else if (e.isRightClick()) {
							ItemStack tempCurs = e.getCursor();
							tempCur.setAmount(tempCur.getAmount() + 1);
							if (tempCurs.getAmount() == 1)
								tempCurs = null;
							else
								tempCurs.setAmount(tempCurs.getAmount() - 1);
							e.getClickedInventory().setItem(e.getSlot(), tempCur);
							e.getWhoClicked().setItemOnCursor(tempCurs);
							DEBUG("Clicked ammo: Right click-Merger: " + e.getSlot());
						}
					}
				}
			}
			return;
		} else if (QualityArmory.getInstance().isCustomItem(e.getCurrentItem())) {
			CustomBaseObject base = null;
			if ((base = QualityArmory.getInstance().getCustomItem(e.getCurrentItem())) == QualityArmory.getCustomItem(e.getCursor())) {
				//Cursor and click are same custom item.
				if (e.getCursor() != null && e.getCurrentItem() != null && base != null)
					if (e.getCursor().getAmount() + e.getCurrentItem().getAmount() <= base.getMaxItemStack()) {
						e.getCursor().setAmount(e.getCurrentItem().getAmount() + e.getCursor().getAmount());
						e.getCurrentItem().setType(Material.AIR);
					} else {
						e.getCurrentItem().setAmount(e.getCursor().getAmount() + e.getCurrentItem().getAmount() - base.getMaxItemStack());
						e.getCursor().setAmount(base.getMaxItemStack());
					}
			}

		}
	}


	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGH)
	public void onDeath(PlayerDeathEvent e) {
		if (e.getEntity().getKiller() instanceof Player) {
			Player killer = e.getEntity().getKiller();
			if (QualityArmory.getInstance().isGun(killer.getItemInHand())) {
				DEBUG("This player \"" + e.getEntity().getName() + "\" was killed by a player with a gun");
			} else if (QualityArmory.getInstance().isCustomItem(e.getEntity().getItemInHand())) {
				DEBUG("This player \"" + e.getEntity().getName() + "\" was killed by a player, but not with a gun");
			}
		}
	}

	@EventHandler
	public void onResourcepackStatusEvent(PlayerResourcePackStatusEvent event) {
		QAMain.sentResourcepack.remove(event.getPlayer().getUniqueId());
		if (event.getStatus() == PlayerResourcePackStatusEvent.Status.ACCEPTED
				|| event.getStatus() == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED) {
			QAMain.resourcepackReq.add(event.getPlayer().getUniqueId());
		}else if (QAMain.kickIfDeniedRequest) {
			Bukkit.getScheduler().runTask(QAMain.getInstance(), () -> event.getPlayer().kickPlayer(QAMain.S_KICKED_FOR_RESOURCEPACK));
		}

		if (event.getStatus() == PlayerResourcePackStatusEvent.Status.DECLINED) {
			QAMain.resourcepackReq.add(event.getPlayer().getUniqueId()); // Add to the list, so it doesn't keep spamming the title
		}
	}


	@SuppressWarnings("deprecation")
	@EventHandler
	public void onSwig(PlayerAnimationEvent e) {
		if (e.getAnimationType() == PlayerAnimationType.ARM_SWING) {
			if (e.getPlayer().getItemInHand() != null && QualityArmory.isGun(e.getPlayer().getInventory().getItemInMainHand()))
				e.setCancelled(true);
			if (e.getPlayer().getInventory().getItemInOffHand() != null && QualityArmory.isGun(e.getPlayer().getInventory().getItemInOffHand()))
				e.setCancelled(true);
		}
	}

	@EventHandler
    public void onDrop(@NotNull InventoryClickEvent event) {
		DEBUG("Detected drop: " + event.getAction());
    }

	private void DEBUG(String s) {
		main.DEBUG(s);
	}

	@Override
	public void reloadSettings(QAMain main) {

	}
}
