package me.zombie_striker.qg.handlers.chargers;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.zombie_striker.customitemmanager.OLD_ItemFact;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.guns.utils.GunUtil;

public class BurstFireCharger implements ChargingHandler {

	public static HashMap<UUID, BukkitTask> shooters = new HashMap<>();

	public BurstFireCharger() {
		ChargingManager.add(this);
	}

    @Override
    public String getName() {
        return ChargingManager.BURSTFIRE;
    }

	@Override
	public boolean isCharging(Player player) {
		return shooters.containsKey(player.getUniqueId());
	}

	@Override
	public boolean isReadyToFire(final Gun g, final Player player, final ItemStack stack) {
		return true;
	}

	@Override
	public boolean useChargingShoot() {
		return true;
	}

	@Override
	public void shoot(final Gun g, final Player player, final ItemStack stack) {
		QAMain.DEBUG("BurstFireCharger shoot.");
//		GunUtil.shootHandler(g, player, 1);
	//	final AttachmentBase attach = QualityArmory.getGunWithAttchments(stack);
//		GunUtil.playShoot(g,  player);

		shooters.put(player.getUniqueId(), new BukkitRunnable() {
			int slotUsed = player.getInventory().getHeldItemSlot();
			boolean isSneak = player.isSneaking();
			@SuppressWarnings("deprecation")
			boolean offhand = QualityArmory.isIronSights(player.getItemInHand());
			// Shot count
			int shotCurrently = 0;



			@Override
			@SuppressWarnings("deprecation")
			public void run() {
//				QAMain.DEBUG("------ Burst Fire Mission Start ------");
				// Change sneak
				if (player.isSneaking() != isSneak) {
					QAMain.DEBUG("Toggle sneak, stop burst shooting, count " + shotCurrently);
					if (shooters.containsKey(player.getUniqueId())) {
						shooters.remove(player.getUniqueId()).cancel();
					}
					cancel();
					return;
				}
				// try to drop IronSights, Copy bug warning!!!
				// TODO Maybe can set a lock if gun is firing.
				if (Material.AIR.name().equals(player.getInventory().getItemInMainHand().getType().name())) {
					QAMain.DEBUG("Drop the gun, stop burst shooting, count " + shotCurrently);
					if (shooters.containsKey(player.getUniqueId())) {
						shooters.remove(player.getUniqueId()).cancel();
					}
					cancel();
					return;
				}
				// Check IronSights change
				if (offhand && !QualityArmory.isIronSights(player.getInventory().getItemInMainHand())) {
					QAMain.DEBUG("IronSights disable, stop burst shooting, count " + shotCurrently);
					if (shooters.containsKey(player.getUniqueId())) {
						shooters.remove(player.getUniqueId()).cancel();
					}
					cancel();
					return;
				}
				int amount = Gun.getAmount(stack);
				if (shotCurrently >= g.getBulletsPerShot() || slotUsed != player.getInventory().getHeldItemSlot()
						|| amount <= 0) {
					QAMain.DEBUG("Burst shot " + shotCurrently + " times / Out of ammo, stop burst shooting");
					if (shooters.containsKey(player.getUniqueId())) {
						shooters.remove(player.getUniqueId()).cancel();
					}
					cancel();
					return;
				}
				// A single shoot
				GunUtil.shootHandler(g, player, 1);
				GunUtil.playShoot(g,  player);
				if(QAMain.enableRecoil && g.getRecoil() > 0) {
					GunUtil.addRecoil(player, g);
				}
				shotCurrently++;
				// ↓ will set the gun back if player drop it. Copy bug warning!!!
				GunUtil.reduceOneAmmo(player, stack, offhand, g);
//				amount--;
//
//				if (amount < 0)
//					amount = 0;
//
//				//if (QAMain.enableVisibleAmounts) {
//				//	stack.setAmount(amount > 64 ? 64 : amount == 0 ? 1 : amount);
//				//}
//				ItemMeta im = stack.getItemMeta();
//				int slot;
//				if (offhand) {
//					slot = -1;
//				} else {
//					slot = player.getInventory().getHeldItemSlot();
//				}
//				im.setLore(Gun.getGunLore(g, stack, amount));
//				stack.setItemMeta(im);
//				if (slot == -1) {
//					try {
//						if (QualityArmory.isIronSights(player.getItemInHand())) {
//							player.getInventory().setItemInOffHand(stack);
//						} else {
//							player.getInventory().setItemInHand(stack);
//						}
//
//					} catch (Error e) {
//					}
//				} else {
//					player.getInventory().setItem(slot, stack);
//				}
//				QualityArmory.sendHotbarGunAmmoCount(player, g, stack, false);
//				QAMain.DEBUG("------ Burst Fire Mission End ------");
			}
		}.runTaskTimer(QAMain.getInstance(), 10 / g.getFireRate(), 10 / g.getFireRate()));
	}


}
