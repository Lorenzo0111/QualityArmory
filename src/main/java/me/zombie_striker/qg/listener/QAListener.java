package me.zombie_striker.qg.listener;

import com.cryptomorin.xseries.reflection.XReflection;
import me.zombie_striker.customitemmanager.ArmoryBaseObject;
import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.customitemmanager.CustomItemManager;
import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.ammo.Ammo;
import me.zombie_striker.qg.api.QACustomItemInteractEvent;
import me.zombie_striker.qg.api.QAGunGiveEvent;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.attachments.AttachmentBase;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.guns.utils.GunRefillerRunnable;
import me.zombie_striker.qg.guns.utils.GunUtil;
import me.zombie_striker.qg.handlers.*;
import me.zombie_striker.qg.miscitems.Grenade;
import me.zombie_striker.qg.miscitems.MeleeItems;
import me.zombie_striker.qg.miscitems.ThrowableItems;
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
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class QAListener implements Listener {
	private final List<UUID> ignoreClick = new ArrayList<>();

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onHit(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			Player d = (Player) e.getDamager();
			if ((e.getCause() == DamageCause.ENTITY_ATTACK || e.getCause() == DamageCause.ENTITY_SWEEP_ATTACK)) {
				// Handle gun firing first, even if event is cancelled (e.g., for invulnerable entities)
				if (QAMain.allowGunHitEntities && !ignoreClick.contains(d.getUniqueId())) {
                    Gun g = QualityArmory.getGun(d.getItemInHand());
                    if (IronsightsHandler.isAiming(d)) g = IronsightsHandler.getGunUsed(d);

					if (g != null && d.getLocation().getWorld().equals(e.getEntity().getLocation().getWorld())) {
						double distance = d.getLocation().distance(e.getEntity().getLocation());
						// Removed the distance > 5 check to allow guns to work at longer ranges
						// and to fix the issue in Minecraft 1.19.4 where clicking on entities
						// within a few blocks doesn't properly trigger gun firing

						ignoreClick.add(d.getUniqueId());

						QACustomItemInteractEvent event = new QACustomItemInteractEvent(d, g);
						Bukkit.getPluginManager().callEvent(event);
						if (event.isCancelled()) {
							ignoreClick.remove(d.getUniqueId());
							return;
						}

						e.setCancelled(true);
						QAMain.DEBUG("Detected interact on entity, running LMB for " + g.getName());

						Gun finalG = g;
						Bukkit.getScheduler().runTaskLater(QAMain.getInstance(), () -> {
							finalG.onLMB(d, d.getItemInHand());
							ignoreClick.remove(d.getUniqueId());
						}, 1L);
						return; // Exit after handling gun, don't process melee items
					}
				}

				// Only check if event is cancelled for melee items
				if (e.isCancelled())
					return;

				// Handle melee items
				if (QualityArmory.isMisc(d.getItemInHand())) {
					CustomBaseObject aa = QualityArmory.getMisc(d.getItemInHand());
					if (aa instanceof MeleeItems) {
						DEBUG("Setting damage for " + aa.getName() + " to be equal to " + ((MeleeItems) aa).getDamage()
								+ ". was " + e.getDamage());
						e.setDamage(((MeleeItems) aa).getDamage());
					}
					if (aa.getSoundOnHit() != null) {
						e.getEntity().getWorld().playSound(e.getEntity().getLocation(), aa.getSoundOnHit(), 1, 1);
					}
				}
			}
		}
	}

	@EventHandler
	public void onHopperpickup(InventoryPickupItemEvent e) {
		if (e.isCancelled())
			return;
		if (e.getInventory().getType() == InventoryType.HOPPER) {
			if (QualityArmory.isGun(e.getItem().getItemStack()))
				e.setCancelled(true);

			if (Grenade.getGrenades().contains(e.getItem())) {
				e.setCancelled(true);
				QAMain.DEBUG("Cancelled item pickup event because it was a grenade");
			}
		}
	}

	@EventHandler
	public void onHopper(InventoryMoveItemEvent e) {
		if (e.isCancelled() || !QAMain.preventGunsInHoppers)
			return;
		if (e.getSource().getType() == InventoryType.HOPPER || e.getSource().getType() == InventoryType.DISPENSER
				|| e.getSource().getType() == InventoryType.DROPPER)
			if (QualityArmory.isGun(e.getItem()))
				e.setCancelled(true);
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onShift(final PlayerToggleSneakEvent e) {
		if (e.isCancelled())
			return;

		if (QualityArmory.isCustomItem(e.getPlayer().getItemInHand())) {
			CustomBaseObject base = QualityArmory.getCustomItem(e.getPlayer().getItemInHand());
			if (base instanceof ArmoryBaseObject) {
				((ArmoryBaseObject) base).onShift(e.getPlayer(), e.getPlayer().getItemInHand(), e.isSneaking());
			}
		}


		if (!QAMain.enableIronSightsON_RIGHT_CLICK) {
			DEBUG("Sneak Toggle Called");
			try {
				if (e.isSneaking()) {
					if (QualityArmory.isGun(e.getPlayer().getItemInHand())) {
						DEBUG("Is Sneaking with gun in main hand");
						if ((QualityArmory.isCustomItem(e.getPlayer().getInventory().getItemInOffHand())) && !QualityArmory.isIronSights(e.getPlayer().getInventory().getItemInOffHand())) {
							Gun g = QualityArmory.getGun(e.getPlayer().getItemInHand());
							CustomBaseObject offhandObj = QualityArmory.getCustomItem(e.getPlayer().getInventory().getItemInOffHand());
							if (g != offhandObj)
								e.getPlayer().getInventory().addItem(e.getPlayer().getInventory().getItemInOffHand());
							e.getPlayer().getInventory().setItemInOffHand(null);
							DEBUG("Removing custom item from offhand when it shouldn't be there. Custom item = " + offhandObj.getName());
						}
					}
					Gun g = QualityArmory.getGun(e.getPlayer().getItemInHand());
					if (g != null) {
						DEBUG("Gun used " + g.getName() + " attach?= " + (g instanceof AttachmentBase));
						if (g.hasIronSights()) {
							DEBUG("Gun has ironsights");
							try {

								if (!e.getPlayer().getItemInHand().hasItemMeta()
										|| !e.getPlayer().getItemInHand().getItemMeta().hasDisplayName()
										|| e.getPlayer().getItemInHand().getItemMeta().getDisplayName()
										.contains(QAMain.S_RELOADING_MESSAGE))
									return;

								IronsightsHandler.aim(e.getPlayer());
								new BukkitRunnable() {
									@Override
									public void run() {
										MaterialStorage ms1 = MaterialStorage.getMS(e.getPlayer().getItemInHand());
										MaterialStorage ms2 = MaterialStorage.getMS(e.getPlayer().getInventory().getItemInOffHand());
										if (ms2 == ms1) {
											e.getPlayer().getInventory().setItemInOffHand(null);
											DEBUG("Item Duped. Got Rid of using offhand override (code=1)");
										}
									}
								}.runTaskLater(QAMain.getInstance(), 1);
							} catch (Error e2) {
								Bukkit.broadcastMessage(QAMain.prefix
										+ "Ironsights not compatible for versions lower than 1.8. The server owner should set EnableIronSights to false in the plugin's config");
							}
						}
					}
				} else {
					if (IronsightsHandler.isAiming(e.getPlayer())) {
						new BukkitRunnable() {

							@Override
							public void run() {
								MaterialStorage ms1 = MaterialStorage.getMS(e.getPlayer().getItemInHand());
								MaterialStorage ms2 = MaterialStorage.getMS(e.getPlayer().getInventory().getItemInOffHand());

								if (ms2 == ms1) {
									e.getPlayer().getInventory().setItemInOffHand(null);
									DEBUG("Item Duped. Got Rid of using offhand override (code=2)");
								}
							}
						}.runTaskLater(QAMain.getInstance(), 5);

						IronsightsHandler.unAim(e.getPlayer());
						QAMain.DEBUG("Swap gun back to main hand");
					}
				}
			} catch (Error |

					Exception e2) {
				QAMain.DEBUG("Failed to sneak and put gun in off hand.");
			}
		}

	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if (e.isCancelled())
			return;
		if (e.getPlayer().getItemInHand() != null && (QualityArmory.isCustomItem(e.getPlayer().getItemInHand()))) {
			e.setCancelled(true);
			return;
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBreakMonitor(final BlockBreakEvent e) {
		if (e.isCancelled())
			return;
		if (CustomItemManager.isUsingCustomData())
			return;
		int k = 0;
		if (e.getPlayer().getItemInHand() != null)
			if ((k = Gun.getCalculatedExtraDurib(e.getPlayer().getItemInHand())) != -1) {
				ItemStack hand = e.getPlayer().getItemInHand();
				e.getBlock().breakNaturally(hand);
				e.setCancelled(true);
				final ItemStack t;
				if (k > 0) {
					t = Gun.decrementCalculatedExtra(hand);
				} else {
					t = Gun.removeCalculatedExtra(hand);
				}
				new BukkitRunnable() {

					@Override
					public void run() {
						e.getPlayer().setItemInHand(t);
					}
				}.runTaskLater(QAMain.getInstance(), 1);

			}
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
			} else {
				ItemStack item = e.getCurrentItem();

				if (e.getSlot() == OFFHAND_SLOT && QualityArmory.isGun(item)) {
					e.setCancelled(true);
				}
			}

			try {
				if (e.getClick() == ClickType.SWAP_OFFHAND &&
						QualityArmory.isGun(e.getWhoClicked().getInventory().getItemInOffHand()) &&
				        QualityArmory.isIronSights(e.getWhoClicked().getInventory().getItemInMainHand())) {
					e.setCancelled(true);
                    IronsightsHandler.unAim((Player) e.getWhoClicked());
					return;
				}
			} catch (Error | Exception ignored) {}
		}

		if (!(e.getInventory().getHolder() instanceof QAInventoryHolder)) return;

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
				if (!QAMain.enableEconomy) {
					try {
						QAMain.enableEconomy = EconHandler.setupEconomy();
					} catch (Exception | Error ignored) {}
				}

				if (!QAMain.enableEconomy) {
					e.getWhoClicked().closeInventory();
					e.getWhoClicked().sendMessage(QAMain.prefix + QAMain.S_noEcon);
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
							QAMain.removeForIngre((Player) e.getWhoClicked(), g);
						QualityArmory.addAmmoToInventory((Player) e.getWhoClicked(), g, g.getCraftingReturn());
						QAMain.shopsSounds(e, shop);
						DEBUG("Buy-ammo");
					} else {
						e.getWhoClicked().closeInventory();
						DEBUG("Failed to buy/craft ammo");
						if (shop)
							e.getWhoClicked().sendMessage(QAMain.prefix + QAMain.S_noMoney);
						else
							e.getWhoClicked().sendMessage(QAMain.prefix + QAMain.S_missingIngredients);
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
					if ((shop && EconHandler.hasEnough(g, (Player) e.getWhoClicked()))
							|| (!shop && QAMain.lookForIngre((Player) e.getWhoClicked(), g))
							|| (!shop && e.getWhoClicked().getGameMode() == GameMode.CREATIVE)) {
						if (shop) {
							EconHandler.pay(g, (Player) e.getWhoClicked());
							e.getWhoClicked().sendMessage(
									QAMain.S_BUYCONFIRM.replaceAll("%item%", ChatColor.stripColor(g.getDisplayName()))
											.replaceAll("%cost%", "" + g.getPrice()));
						} else
							QAMain.removeForIngre((Player) e.getWhoClicked(), g);

						if (g instanceof Gun) {
							QAGunGiveEvent event = new QAGunGiveEvent(((Player) e.getWhoClicked()), ((Gun) g), QAGunGiveEvent.Cause.SHOP);
							Bukkit.getPluginManager().callEvent(event);
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
							e.getWhoClicked().sendMessage(QAMain.prefix + QAMain.S_noMoney);
						else
							e.getWhoClicked().sendMessage(QAMain.prefix + QAMain.S_missingIngredients);
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
					&& QualityArmory.isGun(e.getClickedInventory().getItem(40))) {
				e.setCancelled(true);
			}
		} catch (Error | Exception r5) {
			if (e.getSlot() == 40 && e.getInventory() == e.getWhoClicked().getInventory()
					&& QualityArmory.isGun(e.getInventory().getItem(40))) {
				e.setCancelled(true);
			}

		}

		if (QualityArmory.isAmmo(e.getCurrentItem())) {
			Ammo current = QualityArmory.getAmmo(e.getCurrentItem());
			if (e.getCursor() == null) {
				e.setCursor(e.getCurrentItem());
				e.setCurrentItem(null);
				DEBUG("Clicked ammo: Swap: " + e.getSlot());
			} else if (QualityArmory.isAmmo(e.getCursor())) {
				Ammo cursor = QualityArmory.getAmmo(e.getCursor());
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
		} else if (QualityArmory.isCustomItem(e.getCurrentItem())) {
			CustomBaseObject base = null;
			if ((base = QualityArmory.getCustomItem(e.getCurrentItem())) == QualityArmory.getCustomItem(e.getCursor())) {
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

	@SuppressWarnings({"deprecation", "unchecked"})
	@EventHandler
	public void onPickup(PlayerPickupItemEvent e) {
		if (e.isCancelled())
			return;
		if (QualityArmory.isCustomItem(e.getItem().getItemStack())) {
			if (QAMain.shouldSend && !QAMain.namesToBypass.contains(e.getPlayer().getName())
					&& !QAMain.resourcepackReq.contains(e.getPlayer().getUniqueId())) {
				QualityArmory.sendResourcepack(e.getPlayer(), true);
			}

			if (QualityArmory.isGun(e.getItem().getItemStack())) {
				Gun g = QualityArmory.getGun(e.getItem().getItemStack());
				try {
					if (QAMain.AutoDetectResourcepackVersion && !QAMain.MANUALLYSELECT18) {
						if (com.viaversion.viaversion.api.Via.getAPI()
								.getPlayerVersion(e.getPlayer()) < QAMain.ViaVersionIdfor_1_8) {
							if (!g.is18Support()) {
								for (Gun g2 : QAMain.gunRegister.values()) {
									if (g2.is18Support()) {
										if (g2.getDisplayName().equals(g.getDisplayName())) {
											e.getItem().setItemStack(CustomItemManager.getItemType("gun").getItem(g2.getItemData().getMat(), g2.getItemData().getData(), g2.getItemData().getVariant()));
											QAMain.DEBUG("Custom-validation check 1");
											return;
										}
									}
								}
								// If there is no exact match for 1.8, get the closest gun that uses the same
								// ammo type.
								for (Gun g2 : QAMain.gunRegister.values()) {
									if (g2.is18Support()) {
										if (g2.getAmmoType().equals(g.getAmmoType())) {
											e.getItem().setItemStack(CustomItemManager.getItemType("gun").getItem(g2.getItemData().getMat(), g2.getItemData().getData(), g2.getItemData().getVariant()));
											QAMain.DEBUG("Custom-validation check 2");
											return;
										}
									}
								}
							}
						} else {
							if (com.viaversion.viaversion.api.Via.getAPI()
									.getPlayerVersion(e.getPlayer()) >= QAMain.ViaVersionIdfor_1_8) {
								if (g.is18Support()) {
									for (Gun g2 : QAMain.gunRegister.values()) {
										if (!g2.is18Support()) {
											if (g2.getDisplayName().equals(g.getDisplayName())) {
												e.getItem().setItemStack(CustomItemManager.getItemType("gun").getItem(g2.getItemData().getMat(), g2.getItemData().getData(), g2.getItemData().getVariant()));
												QAMain.DEBUG("Custom-validation check 3");
												return;
											}
										}
									}
									// If there is no exact match for 1.8, get the closest gun that uses the same
									// ammo type.
									for (Gun g2 : QAMain.gunRegister.values()) {
										if (!g2.is18Support()) {
											if (g2.getAmmoType().equals(g.getAmmoType())) {
												e.getItem().setItemStack(CustomItemManager.getItemType("gun").getItem(g2.getItemData().getMat(), g2.getItemData().getData(), g2.getItemData().getVariant()));
												QAMain.DEBUG("Custom-validation check 4");
												return;
											}
										}
									}
								}
							}
						}
					}
				} catch (Error | Exception e4) {
				}
				QAMain.checkforDups(e.getPlayer(), e.getItem().getItemStack());

				if (QAMain.enablePrimaryWeaponHandler)
					if (QualityArmory.isOverLimitForPrimaryWeapons(g, e.getPlayer()))
						e.setCancelled(true);
				return;
			}

			if (QualityArmory.isAmmo(e.getItem().getItemStack())) {
				QualityArmory.addAmmoToInventory(e.getPlayer(), QAMain.ammoRegister.get(MaterialStorage.getMS(e.getItem().getItemStack())),
						e.getItem().getItemStack().getAmount());
				e.setCancelled(true);
				e.getItem().remove();
				try {
					e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.2f, 1);
				} catch (Error e2) {
					e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.valueOf("CLICK"), 0.2f, 1);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onDropReload(PlayerDropItemEvent e) {
		if (QAMain.reloadOnQ && !QAMain.reloadOnFOnly) {
			Gun g = QualityArmory.getGun(e.getItemDrop().getItemStack());
			if (g != null) {
				e.setCancelled(true);
				reload(e.getPlayer(),g);
			}
		}
	}

	public static void reload(Player player, Gun g) {
		if (g.playerHasAmmo(player)) {
			g.reload(player);
		}
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		QAMain.recoilHelperMovedLocation.put(e.getPlayer().getUniqueId(), e.getTo());
	}

	@EventHandler
	public void onHeadPlace(BlockPlaceEvent e) {
		if (QualityArmory.isCustomItem(e.getItemInHand()))
			e.setCancelled(true);
	}


	@EventHandler
	public void onDeathChat(PlayerDeathEvent e) {
		if (QAMain.changeDeathMessages) {
			if (e.getEntity().getKiller() != null && e.getEntity().getKiller() instanceof Player) {
				Player killer = e.getEntity().getKiller();
				if (e.getDeathMessage() != null && e.getDeathMessage().contains(" using ")) {
                    Gun base = IronsightsHandler.getGunUsed(killer);
					if (base != null) {
						e.setDeathMessage(base.getDeathMessage()
								.replaceAll("%player%", e.getEntity().getDisplayName())
								.replaceAll("%killer%", killer.getDisplayName())
								.replaceAll("%name%", base.getDisplayName())
						);
					}
				}
			}
		}
	}


	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGH)
	public void onDeath(PlayerDeathEvent e) {
		if (ThrowableItems.throwItems.containsKey(e.getEntity())) {
			ThrowableItems.ThrowableHolder holder = ThrowableItems.throwItems.get(e.getEntity());

			ItemStack grenadeStack = null;
            Grenade grenade = null;

			for (ItemStack drop : new ArrayList<>(e.getDrops())) {
				if (QualityArmory.isMisc(drop)) {
					CustomBaseObject misc = QualityArmory.getMisc(drop);
					if (misc instanceof Grenade && misc.equals(holder.getGrenade())) {
						grenadeStack = drop;
                        grenade = (Grenade) misc;
						e.getDrops().remove(drop);
						break;
					}
				}
			}
			
			if (grenadeStack != null) {
                grenade.throwGrenade(e.getEntity(), null);

				if (grenadeStack.getAmount() > 1) {
					ItemStack remaining = grenadeStack.clone();
					remaining.setAmount(grenadeStack.getAmount() - 1);
					e.getDrops().add(remaining);
				}
				
				DEBUG("Forced throw of armed grenade on player death");
			}
		}
		
		for (ItemStack is : new ArrayList<>(e.getDrops())) {
			if (is == null)
				continue;
			if (QualityArmory.isIronSights(is)) {
				e.getDrops().remove(is);
				DEBUG("Removing IronSights");
			} else if (is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().getDisplayName().contains(QAMain.S_RELOADING_MESSAGE)) {
				Gun g = QualityArmory.getGun(is);
				ItemMeta im = is.getItemMeta();
				im.setDisplayName(g.getDisplayName());
				is.setItemMeta(im);
				DEBUG("Removed Reloading suffix");
			}
		}

		if (QAMain.showAmmoInXPBar) {
			e.setDroppedExp(0);
			e.setNewTotalExp(0);
		}

		if (e.getDeathMessage() != null
				&& e.getDeathMessage().contains(QualityArmory.getIronSightsItemStack().getItemMeta().getDisplayName())) {
			try {
				e.setDeathMessage(e.getDeathMessage().replace(QualityArmory.getIronSightsItemStack().getItemMeta().getDisplayName(),
						e.getEntity().getKiller().getInventory().getItemInOffHand().getItemMeta().getDisplayName()));
				DEBUG("Removing ironsights from death message and replaced with gun's name");
			} catch (Error | Exception e34) {
			}
		}
		BulletWoundHandler.bleedoutMultiplier.remove(e.getEntity().getUniqueId());
		BulletWoundHandler.bloodLevel.put(e.getEntity().getUniqueId(), QAMain.bulletWound_initialbloodamount);

		if (e.getEntity().getKiller() instanceof Player) {
			Player killer = e.getEntity().getKiller();
			if (QualityArmory.isGun(killer.getItemInHand()) || QualityArmory.isIronSights(killer.getItemInHand())) {
				DEBUG("This player \"" + e.getEntity().getName() + "\" was killed by a player with a gun");
			} else if (QualityArmory.isCustomItem(e.getEntity().getItemInHand())) {
				DEBUG("This player \"" + e.getEntity().getName() + "\" was killed by a player, but not with a gun");
			}
		}
	}

	@EventHandler
	public void onDeath(PlayerRespawnEvent e) {
		BulletWoundHandler.bleedoutMultiplier.remove(e.getPlayer().getUniqueId());
		BulletWoundHandler.bloodLevel.put(e.getPlayer().getUniqueId(), QAMain.bulletWound_initialbloodamount);
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR)
	public void onClickMONITOR(final PlayerInteractEvent e) {
		if (e.isCancelled())
			return;
		if (QAMain.ignoreSkipping)
			return;

		try {
			if (e.getHand() == org.bukkit.inventory.EquipmentSlot.OFF_HAND)
				return;
		} catch (Error | Exception e4) {

		}

		if (!CustomItemManager.isUsingCustomData()) {
			if (e.getPlayer().getItemInHand() != null && !QualityArmory.isCustomItem(e.getPlayer().getItemInHand())) {
				QAMain.DEBUG("Item is not any valid item - mainhand");
				if (QualityArmory.isCustomItemNextId(e.getPlayer().getItemInHand())) {
					QAMain.DEBUG("A player is using a non-gun item, but may reach the textures of one!");
					// If the item is not a gun, but the item below it is
					int safeDurib = QualityArmory.findSafeSpot(e.getPlayer().getItemInHand(), true, QAMain.overrideURL)
							+ (QAMain.overrideURL ? 0 : 3);

					// if (e.getItem().getDurability() == 1) {
					QAMain.DEBUG("Safe Durib= " + (safeDurib) + "! ORG " + e.getPlayer().getItemInHand().getDurability());
					ItemStack is = e.getPlayer().getItemInHand();
					is.setDurability((short) (safeDurib));
					is = Gun.addCalulatedExtraDurib(is, safeDurib - e.getPlayer().getItemInHand().getDurability());
					e.getPlayer().setItemInHand(is);
					// }
				}
			}
			if (Update19OffhandChecker.supportOffhand(e.getPlayer())) {
				if (e.getPlayer().getInventory().getItemInOffHand() != null
						&& !QualityArmory.isCustomItem(e.getPlayer().getInventory().getItemInOffHand())) {
					QAMain.DEBUG("Item is not any valid item - offhand");
					if (QualityArmory.isCustomItemNextId(e.getPlayer().getInventory().getItemInOffHand())) {
						QAMain.DEBUG("A player is using a non-gun item, but may reach the textures of one!");
						// If the item is not a gun, but the item below it is
						int safeDurib = QualityArmory.findSafeSpot(e.getPlayer().getInventory().getItemInOffHand(), true,
								QAMain.overrideURL) + (QAMain.overrideURL ? 0 : 3);

						// if (e.getItem().getDurability() == 1) {
						QAMain.DEBUG("Safe Durib= " + (safeDurib) + "! ORG "
								+ e.getPlayer().getInventory().getItemInOffHand().getDurability());
						ItemStack is = e.getPlayer().getInventory().getItemInOffHand();
						is.setDurability((short) (safeDurib));
						is = Gun.addCalulatedExtraDurib(is,
								safeDurib - e.getPlayer().getInventory().getItemInOffHand().getDurability());
						e.getPlayer().getInventory().setItemInOffHand(is);
						// }
					}
				}
			}
		}

	}

	@EventHandler
	public void onAnvilClick(final PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType() == Material.ANVIL
				&& QAMain.overrideAnvil && !e.getPlayer().isSneaking()) {
			QAMain.DEBUG("ANVIL InteractEvent Called");
			if (QAMain.shouldSend && !QAMain.resourcepackReq.contains(e.getPlayer().getUniqueId())) {
				QualityArmory.sendResourcepack(e.getPlayer(), true);
				e.setCancelled(true);
				QAMain.DEBUG("Resourcepack message being sent!");
				return;
			}
			if (!e.getPlayer().hasPermission("qualityarmory.craft")) {
				e.getPlayer().sendMessage(QAMain.prefix + ChatColor.RED + QAMain.S_ANVIL);
				return;
			}
			e.getPlayer().openInventory(QAMain.createCraft(0));
			e.setCancelled(true);
			QAMain.DEBUG("Opening crafting menu");
			return;
		}
	}

	@SuppressWarnings({"deprecation"})
	@EventHandler(priority = EventPriority.LOWEST)
	public void onClick(final PlayerInteractEvent e) {
		QAMain.DEBUG("InteractEvent Called. Custom item used = " + QualityArmory.isCustomItem(e.getPlayer().getItemInHand()));
		if (!CustomItemManager.isUsingCustomData()) {
			QAMain.DEBUG("Custom Data Check");
			try {
				if (QAMain.ITEM_enableUnbreakable && !QAMain.ignoreUnbreaking)
					if (QualityArmory.isCustomItem(e.getPlayer().getItemInHand())) {
						if (!e.getPlayer().getItemInHand().getItemMeta().isUnbreakable()) {
							QAMain.DEBUG("A player is using a breakable item that reached being a gun!");
							// If the item is not a gun, but the item below it is
							int safeDurib = QualityArmory.findSafeSpot(e.getPlayer().getItemInHand(), true, QAMain.overrideURL)
									+ (QAMain.overrideURL ? 0 : 3);

							// if (e.getItem().getDurability() == 1) {
							QAMain.DEBUG("Safe Durib= " + (safeDurib) + "! ORG "
									+ e.getPlayer().getItemInHand().getDurability());
							ItemStack is = e.getPlayer().getItemInHand();
							is.setDurability((short) (safeDurib));
							e.getPlayer().setItemInHand(is);
						}
					}
			} catch (Error | Exception e45) {
			}

			try {
				if (QAMain.ITEM_enableUnbreakable && !QAMain.ignoreUnbreaking)
					if (QualityArmory.isCustomItem(e.getPlayer().getInventory().getItemInOffHand())) {
						if (!e.getPlayer().getInventory().getItemInOffHand().getItemMeta().isUnbreakable()) {
							QAMain.DEBUG("A player is using a breakable item that reached being a gun!");
							// If the item is not a gun, but the item below it is
							int safeDurib = QualityArmory.findSafeSpot(e.getPlayer().getInventory().getItemInOffHand(),
									true, QAMain.overrideURL) + (QAMain.overrideURL ? 0 : 3);

							// if (e.getItem().getDurability() == 1) {
							QAMain.DEBUG("Safe Durib= " + (safeDurib) + "! ORG "
									+ e.getPlayer().getInventory().getItemInOffHand().getDurability());
							ItemStack is = e.getPlayer().getInventory().getItemInOffHand();
							is.setDurability((short) (safeDurib));
							e.getPlayer().getInventory().setItemInOffHand(is);
						}
					}
			} catch (Error | Exception e45) {
			}
		}

		CustomBaseObject object = null;
		if (!QualityArmory.isCustomItem(e.getPlayer().getItemInHand())) {
			ItemStack offhand = Update19OffhandChecker.getItemStackOFfhand(e.getPlayer());
			if (offhand == null || !QualityArmory.isCustomItem(offhand)) {
				return;
			} else {
				object = QualityArmory.getCustomItem(offhand);
			}
		} else {
			if (QualityArmory.isIronSights(e.getPlayer().getItemInHand())) {
				object = QualityArmory.getCustomItem(e.getPlayer().getInventory().getItemInOffHand());
			} else {
				object = QualityArmory.getCustomItem(e.getPlayer().getItemInHand());
			}
		}

		if (object == null)
			return;
		QAMain.DEBUG("It Is a custom item! = " + object.getName());

		QACustomItemInteractEvent event = new QACustomItemInteractEvent(e.getPlayer(), object);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled())
			return;

		if (QAMain.kickIfDeniedRequest && QAMain.sentResourcepack.containsKey(e.getPlayer().getUniqueId())
				&& System.currentTimeMillis() - QAMain.sentResourcepack.get(e.getPlayer().getUniqueId()) >= 3000) {
			// the player did not accept resourcepack, and got away with it
			e.setCancelled(true);
			e.getPlayer().kickPlayer(QAMain.S_KICKED_FOR_RESOURCEPACK);
			QAMain.DEBUG("Kick for custom resourcepack");
			return;
		}

		if (e.getItem() != null) {
			final ItemStack origin = e.getItem();
			final int slot = e.getPlayer().getInventory().getHeldItemSlot();
			if (!QAMain.isVersionHigherThan(1, 9)) {
				QAMain.DEBUG("1.8 item damage check");
				ItemStack temp1 = null;
				try {
					temp1 = e.getPlayer().getInventory().getItemInOffHand();
				} catch (Error | Exception re453) {
				}
				final ItemStack temp2 = temp1;

				new BukkitRunnable() {
					@Override
					public void run() {
						if (origin.getDurability() != e.getPlayer().getItemInHand().getDurability()
								&& slot == e.getPlayer().getInventory().getHeldItemSlot()
								&& (e.getPlayer().getItemInHand() != null
								&& e.getPlayer().getItemInHand().getType() == origin.getType())) {
							try {
								if (QualityArmory.isIronSights(e.getPlayer().getItemInHand())
										&& origin.getDurability() == e.getPlayer().getInventory().getItemInOffHand()
										.getDurability())
									return;
								if (temp2 != null
										&& temp2.getDurability() == e.getPlayer().getItemInHand().getDurability())
									return;
							} catch (Error | Exception re54) {
							}
							e.getPlayer().setItemInHand(origin);
							DEBUG("The item in the player's hand changed! Origin " + origin.getDurability() + " New "
									+ e.getPlayer().getItemInHand().getDurability());
						}

					}
				}.runTaskLater(QAMain.getInstance(), 0);
			}

			ItemStack usedItem = IronsightsHandler.getItemAiming(e.getPlayer());

			// Send the resourcepack if the player does not have it.
			if (QAMain.shouldSend && !QAMain.resourcepackReq.contains(e.getPlayer().getUniqueId())) {
				QAMain.DEBUG("Player does not have resourcepack!");
				QualityArmory.sendResourcepack(e.getPlayer(), true);
			}

			if (IronsightsHandler.isAiming(e.getPlayer())) {
				QAMain.DEBUG("Player is aiming!");
				try {
					if ((e.getAction() == Action.RIGHT_CLICK_AIR
							|| e.getAction() == Action.RIGHT_CLICK_BLOCK) == (QAMain.SWAP_TO_LMB_SHOOT)) {
						e.setCancelled(true);
						Gun g = IronsightsHandler.getGunUsed(e.getPlayer());
						QAMain.DEBUG("Swapping " + g.getName() + " from offhand to main hand to reload!");
						if (GunUtil.rapidfireshooters.containsKey(e.getPlayer().getUniqueId()))
							GunUtil.rapidfireshooters.remove(e.getPlayer().getUniqueId()).cancel();
						IronsightsHandler.unAim(e.getPlayer());
						if (QAMain.enableIronSightsON_RIGHT_CLICK || !e.getPlayer().isSneaking()) {
							QAMain.toggleNightvision(e.getPlayer(), g, false);
						}
						if (QAMain.enableIronSightsON_RIGHT_CLICK)
							return;
					}
				} catch (Error e2) {
				}
			}
			try {
				if (!IronsightsHandler.isAiming(e.getPlayer()) && event.getPlayer().getInventory().getItemInOffHand().equals(e.getItem())) {
					e.setCancelled(true);
					return;
				}
			} catch (Throwable ignored) {}

			CustomBaseObject qaItem = QualityArmory.getCustomItem(usedItem);
			if (qaItem != null) {
				QAMain.DEBUG(qaItem.getName() + " item is being used!");
				if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
					if (((ArmoryBaseObject) qaItem).onLMB(e.getPlayer(), usedItem))
						e.setCancelled(true);
				} else {
					if (((ArmoryBaseObject) qaItem).onRMB(e.getPlayer(), usedItem))
						e.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void swap(PlayerItemHeldEvent e) {
		if (e.isCancelled())
			return;
		ItemStack prev = e.getPlayer().getInventory().getItem(e.getPreviousSlot());
		ItemStack newslot = e.getPlayer().getInventory().getItem(e.getNewSlot());
		if (QualityArmory.isIronSights(prev) && QualityArmory.isCustomItem(e.getPlayer().getInventory().getItemInOffHand())) {
			int ammoCount = Gun.getAmount(e.getPlayer());

			e.getPlayer().getInventory().setItem(e.getPreviousSlot(), e.getPlayer().getInventory().getItemInOffHand());
			e.getPlayer().getInventory().setItemInOffHand(null);
			QAMain.toggleNightvision(e.getPlayer(), null, false);

			Gun.updateAmmo(null, e.getPlayer().getInventory().getItem(e.getPreviousSlot()), ammoCount);
		}
		if (QualityArmory.isCustomItem(prev)) {
			CustomBaseObject customBase = QualityArmory.getCustomItem(prev);
			if (customBase instanceof ArmoryBaseObject)
				((ArmoryBaseObject) customBase).onSwapAway(e.getPlayer(), prev);
		}
		if (QualityArmory.isCustomItem(newslot)) {
			CustomBaseObject customBase = QualityArmory.getCustomItem(newslot);
			if (customBase instanceof ArmoryBaseObject) {
				((ArmoryBaseObject) customBase).onSwapTo(e.getPlayer(), newslot);
				if (customBase.getSoundOnEquip() != null)
					e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), customBase.getSoundOnEquip(), 1, 1);
			}

			if (customBase instanceof Gun && e.getPlayer().isSneaking() && ((Gun) customBase).hasIronSights()) {
				Bukkit.getScheduler().runTaskLater(QAMain.getInstance(), () -> IronsightsHandler.aim(e.getPlayer()), 1);
			}

			QAMain.lastWeaponSwitch.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
		}

		if(QAMain.showAmmoInXPBar) {
			CustomBaseObject customBase = QualityArmory.getCustomItem(newslot);
			if (customBase instanceof Gun) {
				GunUtil.updateXPBar(e.getPlayer(), (Gun) customBase,Gun.getAmount(newslot));
			}else{
				e.getPlayer().setExp(0);
				e.getPlayer().setLevel(0);
			}
		}
	}

	@EventHandler
	public void onQuit(final PlayerQuitEvent e) {
		QAMain.sentResourcepack.remove(e.getPlayer().getUniqueId());
		QAMain.resourcepackLoading.remove(e.getPlayer().getUniqueId());
		QAMain.resourcepackReq.remove(e.getPlayer().getUniqueId());

		if (QAMain.reloadingTasks.containsKey(e.getPlayer().getUniqueId())) {
			for (GunRefillerRunnable r : QAMain.reloadingTasks.get(e.getPlayer().getUniqueId())) {
				r.getTask().cancel();
			}
		}
		QAMain.reloadingTasks.remove(e.getPlayer().getUniqueId());

		if (QualityArmory.isIronSights(e.getPlayer().getInventory().getItemInHand())) {
			try {
				e.getPlayer().getInventory().setItemInMainHand(e.getPlayer().getInventory().getItemInOffHand());
				e.getPlayer().getInventory().setItemInOffHand(new ItemStack(Material.AIR));
			} catch (Error e2) {
			}
		}
	}

	@EventHandler
	public void onJoin(final PlayerJoinEvent e) {
		if (XReflection.MINOR_NUMBER == 7) {
			Bukkit.broadcastMessage(
					QAMain.prefix + " QualityArmory does not support versions older than 1.9, and may crash clients");
			Bukkit.broadcastMessage(
					"Since there is no reason to stay on outdated updates, (1.7 and 1.8 has quite a number of exploits) update your server.");
			if (QAMain.shouldSend) {
				QAMain.shouldSend = false;
				Bukkit.broadcastMessage(QAMain.prefix + ChatColor.RED + " Disabling resourcepack.");
			}
		}
		if (QAMain.addGlowEffects) {
			new BukkitRunnable() {

				@Override
				public void run() {
					if (e.getPlayer().getScoreboard() != null
							&& !QAMain.coloredGunScoreboard.contains(e.getPlayer().getScoreboard())) {
						QAMain.coloredGunScoreboard.add(QAMain.registerGlowTeams(e.getPlayer().getScoreboard()));
					}
				}
			}.runTaskLater(QAMain.getInstance(), 20 * 15);
		}

		if (QAMain.shouldSend && QAMain.sendOnJoin) {
			QualityArmory.sendResourcepack(e.getPlayer(), QAMain.sendTitleOnJoin);
		} else {
			for (ItemStack i : e.getPlayer().getInventory().getContents()) {
				if (i != null && (QualityArmory.isGun(i) || QualityArmory.isAmmo(i) || QualityArmory.isMisc(i))) {
					if (QAMain.shouldSend && !QAMain.resourcepackReq.contains(e.getPlayer().getUniqueId())) {
						new BukkitRunnable() {

							@Override
							public void run() {
								QualityArmory.sendResourcepack(e.getPlayer(), false);
							}

						}.runTaskLater(QAMain.getInstance(), 0);
					}
					break;
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onDrop(final PlayerDropItemEvent e) {
		if (e.isCancelled())
			return;
		if (QAMain.showOutOfAmmoOnItem) {
			if (QualityArmory.isGun(e.getItemDrop().getItemStack())) {
				if (e.getItemDrop().getItemStack().getItemMeta().getDisplayName().contains(QAMain.S_OUT_OF_AMMO)) {
					ItemStack temp = e.getItemDrop().getItemStack();
					ItemMeta im = temp.getItemMeta();
					im.setDisplayName(QualityArmory.getGun(temp).getDisplayName());
					temp.setItemMeta(im);
					e.getItemDrop().setItemStack(temp);
					QAMain.DEBUG("UNSUPPORTED: Dropping item with Out of ammo displayed");
				}
			}
		}

		if (QualityArmory.isIronSights(e.getItemDrop().getItemStack())) {
			e.setCancelled(true);
			if (!QAMain.enableIronSightsON_RIGHT_CLICK)
				return;
		}

		QAMain.checkforDups(e.getPlayer(), e.getItemDrop().getItemStack());

		if (QAMain.enableIronSightsON_RIGHT_CLICK) {
			if (e.getPlayer().getItemInHand() != null && (QualityArmory.isGun(e.getItemDrop().getItemStack())
					|| QualityArmory.isIronSights(e.getItemDrop().getItemStack()))) {
				if (e.getItemDrop().getItemStack().getAmount() == 1) {
					try {
						boolean dealtWithDrop = false;
						if (QualityArmory.isIronSights(e.getItemDrop().getItemStack())) {
							e.getItemDrop().setItemStack(e.getPlayer().getInventory().getItemInOffHand());
							e.getPlayer().getInventory().setItemInOffHand(null);
							dealtWithDrop = true;
						}
						if (e.getPlayer().getItemInHand().getType() != e.getItemDrop().getItemStack().getType()
								|| e.getPlayer().getItemInHand().getDurability() != e.getItemDrop().getItemStack()
								.getDurability()) {
							return;
						}
						Gun g = QualityArmory.getGun(e.getItemDrop().getItemStack());
						if (g != null) {
							QAMain.DEBUG("Dropped gun is a gun. Checking for has ammo");
							if (!dealtWithDrop) {
								if (e.getPlayer().getItemInHand().getType() != Material.AIR) {
									if ((g.getMaxBullets() - 1) == Gun.getAmount(e.getPlayer())) {
										QAMain.DEBUG("Player is full on ammo. Don't reload");
										return;
									}
								}
							}
							if (GunUtil.hasAmmo(e.getPlayer(), g)) {
								QAMain.DEBUG("Has ammo. doing checks");
								e.setCancelled(true);

								final Gun gk = g;
								new BukkitRunnable() {
									@Override
									public void run() {
										QAMain.DEBUG("Reloaded after one tick");
										GunUtil.basicReload(gk, e.getPlayer(), gk.hasUnlimitedAmmo());
									}
								}.runTaskLater(QAMain.getInstance(), 1);
							}
						}

					} catch (Error e2) {
						e2.printStackTrace();
					}
				}
			}
		}

	}

	@EventHandler
	public void onDrop(ItemSpawnEvent event) {
		ItemStack item = event.getEntity().getItemStack();

		if (QualityArmory.isGun(item)) {
			Gun g = QualityArmory.getGun(item);
			if (g == null) return;

			if (g.getGlow() != null && QAMain.coloredGunScoreboard != null) {
				for (Scoreboard s : QAMain.coloredGunScoreboard)
					if (s.getTeam("QA_" + g.getGlow().name()) != null)
						s.getTeam("QA_" + g.getGlow().name()).addEntry(event.getEntity().getUniqueId().toString());

				QAMain.DEBUG("Added Glow");
				event.getEntity().setGlowing(true);
			}
		}
	}

	private void DEBUG(String s) {
		QAMain.DEBUG(s);
	}
}

