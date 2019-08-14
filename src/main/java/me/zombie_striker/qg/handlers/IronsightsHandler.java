package me.zombie_striker.qg.handlers;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.guns.Gun;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class IronsightsHandler {

	public static Material ironsightsMaterial = Material.DIAMOND_AXE;
	public static int ironsightsData = 21;
	public static String ironsightsDisplay = "Iron Sights Enabled";


    /**
     * Start Aiming, Swapping the gun to the offhand and create IronSight in main hand.
     * <p />
     * or charging the bow if gun's attribute enableBetterModelScopes has been set to false.
     * <p />
     *
     * if Player has something on offhand, like a shield.
     * put it in backpack or drop it on the floor.
     *
     * @param player
     * @return
     */
	public synchronized static ItemStack aim(final Player player){
//		boolean swap = true;
		ItemStack inHand = player.getInventory().getItemInMainHand();
		ItemStack offHand = player.getInventory().getItemInOffHand();
        // Already checked player holding a gun and the gun has IronSights in Up step
        Gun g = QualityArmory.getGun(inHand);
        if (null != g) {
            if (!g.isOffhandOverride()) {
                // if gun has enableBetterModelScopes set false(default),
                // they will not use IronSights in enableIronSightsON_RIGHT_CLICK is false
                // charge ARROW in CROSSBOW
                QAMain.DEBUG("The " + g.getName() + " attribute enableBetterModelScopes has been set to false, don't use IronSights.");
                CrossbowMeta im = (CrossbowMeta) inHand.getItemMeta();
                im.addChargedProjectile(new ItemStack(Material.ARROW));
                inHand.setItemMeta(im);
                player.getInventory().setItemInMainHand(inHand);
            } else {
                if (!"AIR".equals(offHand.getType().name())) {
                    QAMain.DEBUG("Player holds " + offHand.getType().name() + " at offhand");
                    // clean offhand
                    HashMap<Integer, ItemStack> overflow = player.getInventory().addItem(offHand);
                    if (!overflow.isEmpty()) {
                        for (ItemStack itemStack: overflow.values()) {
                            player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
                            QAMain.DEBUG("Inventory is full, drop the item " + offHand.getType().name());
                        }
                    }
                    player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
                }
                QAMain.DEBUG("Enable IronSights, swapping " + g.getName() + " from main hand to offhand!");
                // Swapping to offhand and make a IronSights to main hand.
                player.getInventory().setItemInOffHand(inHand);
                player.getInventory().setItemInMainHand(QualityArmory.getIronSightsItemStack());
                // Enable nigitvision
                QAMain.toggleNightvision(player, g, true);
//                try {
//                    // Create a new thread to disable IronSights
//                    final Gun checkTo = QualityArmory.getGun(player.getInventory().getItemInOffHand());
//                    new BukkitRunnable() {
//                        @Override
//                        public void run() {
//                            if (!player.isOnline()) {
//                                QAMain.DEBUG("Canceling since player is offline");
//                                cancel();
//                                return;
//                            }
//                            Gun g = null;
//                            if (!QualityArmory.isIronSights(player.getInventory().getItemInMainHand())
//                                    || (g = QualityArmory.getGun(player.getInventory().getItemInOffHand())) == null
//                                    || g != checkTo) {
//                                unAim(player);
//                                QAMain.DEBUG(
//                                        "Forced disable IronSights the main hand is not ironsights or offhand gun is null."
//                                                + (!QualityArmory.isIronSights(player.getInventory().getItemInMainHand()))
//                                                + " "
//                                                + ((g = QualityArmory.getGun(player.getInventory().getItemInOffHand())) == null)
//                                                + " "
//                                                + (g != checkTo));
//                                cancel();
//                                return;
//                            }
//
//                        }
//                    }.runTaskTimer(QAMain.getInstance(), 20, 20);
//                } catch (Exception e) {}

                return player.getInventory().getItemInMainHand();
            }
        }
        return inHand;


//		try {
//			if(player.getItemInHand() != null && player.getItemInHand().getType().name().equals("CROSSBOW")){
//				Gun g = QualityArmory.getGun(player.getItemInHand());
//				if(!g.isOffhandOverride()) {
//                    // if gun has enableBetterModelScopes set false(default),
//                    // they will not use IronSights in enableIronSightsON_RIGHT_CLICK is false
//                    QAMain.DEBUG("Gun " + g.getName() + " has set enableBetterModelScopes to false, don't use IronSights.");
//					ItemStack is = player.getItemInHand();
//					CrossbowMeta im = (CrossbowMeta) is.getItemMeta();
//					im.addChargedProjectile(new ItemStack(Material.ARROW));
//					is.setItemMeta(im);
//					player.setItemInHand(is);
//					swap = false;
//					return;
//				}
//			}
//		} catch (Error|Exception e4){
//
//		} finally {
//		    if(swap) {
//                if (!QualityArmory.isIronSights(inHand)) {
//                    Gun g = QualityArmory.getGun(inHand);
//                    QAMain.DEBUG("Swapping " + g.getName() + " from main hand to offhand!");
//                    player.getInventory().setItemInOffHand(inHand);
//                    player.setItemInHand(QualityArmory.getIronSightsItemStack());
//                }
//            }
//		}
	}

    /**
     * Cancel Aiming, swapping gun from offhand to slot.
     * <p />
     * or uncharged bow if gun's attribute enableBetterModelScopes has been set to false.
     * <p />
     *
     * check the item on player's main hand.
     *
     * @param player
     * @return the gun that was on offhand || Uncharged bow
     */
	public synchronized static ItemStack unAim(Player player){
		ItemStack inHand = player.getInventory().getItemInMainHand();
		unAim(player, inHand, -1);
        return player.getInventory().getItemInMainHand();

//		try {
//			if(player.getItemInHand()!=null && player.getItemInHand().getType().name().equals("CROSSBOW")){
//				Gun g = QualityArmory.getGun(player.getItemInHand());
//				if(!g.isOffhandOverride()) {
//					ItemStack is = player.getItemInHand();
//					CrossbowMeta im = (CrossbowMeta) is.getItemMeta();
//					im.setChargedProjectiles(null);
//					is.setItemMeta(im);
//					player.setItemInHand(is);
//					swap = false;
//					return;
//				}
//			}
//		}catch (Error|Exception e4){
//
//		}finally {
//			if(swap)
//			if(QualityArmory.isIronSights(player.getItemInHand())){
//				player.getInventory().setItemInMainHand(player.getInventory().getItemInOffHand());
//				player.getInventory().setItemInOffHand(null);
//			}
//		}
	}

    /**
     * Cancel Aiming, swapping gun from offhand to slot.
     * <p />
     * or uncharged bow if gun's attribute enableBetterModelScopes has been set to false.
     * <p />
     *
     * if slot < 0 mean swapping to main hand
     *
     * @param player
     * @param itemStack What the player is holding in his hand
     * @param slot
     * @return the gun that was on offhand || Uncharged bow || still is IronSights because no gun in offhand
     */
	public synchronized static ItemStack unAim(Player player, ItemStack itemStack, int slot) {
        // Check the main hand is hold the IronSights then exchange hands.
        if (QualityArmory.isIronSights(itemStack)) {
            ItemStack offHand = player.getInventory().getItemInOffHand();
            Gun g = QualityArmory.getGun(offHand);
            // Maybe just holding the sight but not equipped with weapons
            if (null != g) {
                if (slot >= 0) {
                    QAMain.DEBUG("Disable IronSights, swapping " + g.getName() + " from offhand to slot " + slot + "!");
                    player.getInventory().setItem(slot, new ItemStack(Material.AIR));
                    player.getInventory().setItem(slot, offHand);
                    offHand = player.getInventory().getItem(slot);
                } else {
                    QAMain.DEBUG("Disable IronSights, swapping " + g.getName() + " from offhand to main hand!");
                    player.getInventory().setItemInMainHand(offHand);
                    offHand = player.getInventory().getItemInMainHand();
                }
                player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
                // Disable Nightvision
                QAMain.toggleNightvision(player, g, false);
                // Return the gun.
                if (slot >= 0) {

                }
                return offHand;
            }
        } else if (Material.CROSSBOW.name().equals(itemStack.getType().name())) {
            // Hold a gun
            Gun g = QualityArmory.getGun(itemStack);
            if (null != g && !g.isOffhandOverride()) {
                // if gun has enableBetterModelScopes set false(default),
                // they will not use IronSights in enableIronSightsON_RIGHT_CLICK is false
                CrossbowMeta im = (CrossbowMeta) itemStack.getItemMeta();
                im.setChargedProjectiles(null);
                itemStack.setItemMeta(im);
            }
        }
        return itemStack;
    }

    /**
     * Check the item is IronSights or a charged bow.
     *
     * @param itemStack
     * @return
     */
    public static boolean isAiming(ItemStack itemStack) {
        if (QualityArmory.isIronSights(itemStack)) {
            return true;
        } else if (Material.CROSSBOW.name().equals(itemStack.getType().name())) {
            Gun g = QualityArmory.getGun(itemStack);
            if (null != g && !g.isOffhandOverride()) {
                CrossbowMeta im = (CrossbowMeta) itemStack.getItemMeta();
                return im.hasChargedProjectiles();
            }
        }
        return false;
    }

    /**
     *  Check the item on player's main hand is IronSights or a charged bow.
     * @param player
     * @return
     */
	public static boolean isAiming(Player player){
	    ItemStack onHand = player.getInventory().getItemInMainHand();
        return isAiming(onHand);
//		try {
//			if(player.getItemInHand()!=null && player.getItemInHand().getType().name().equals("CROSSBOW")){
//				Gun g = QualityArmory.getGun(player.getItemInHand());
//				if(!g.isOffhandOverride()) {
//					ItemStack is = player.getItemInHand();
//					CrossbowMeta im = (CrossbowMeta) is.getItemMeta();
//					return im.hasChargedProjectiles();
//				}
//			}
//		}catch (Error|Exception e4){
//
//		}finally {
//			if(QualityArmory.isIronSights(player.getItemInHand())){
//				return true;
//			}
//		}
//		return false;
	}

	public static ItemStack getItemAiming(Player player) {
		try {
			if (player.getItemInHand() != null && player.getItemInHand().getType().name().equals("CROSSBOW")) {
				Gun g = QualityArmory.getGun(player.getItemInHand());
				if(!g.isOffhandOverride()) {
					return player.getItemInHand();
				}
			}
		} catch (Error | Exception e4) {

		} finally {
			if (!QualityArmory.isIronSights(player.getItemInHand())) {
				return player.getItemInHand();
			} else {
				return player.getInventory().getItemInOffHand();
			}
		}
	}
	public static Gun getGunUsed(Player player){
		return QualityArmory.getGun(getItemAiming(player));
	}
	public static void setItemAiming(Player player){}
}
