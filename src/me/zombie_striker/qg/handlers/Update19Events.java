package me.zombie_striker.qg.handlers;

import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.QualityArmory;
import me.zombie_striker.qg.attachments.AttachmentBase;
import me.zombie_striker.qg.guns.Gun;

import org.bukkit.*;
import org.bukkit.event.*;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class Update19Events implements Listener {

	@EventHandler
	@SuppressWarnings("deprecation")
	public void onAnvil(PrepareAnvilEvent e) {
		if (QualityArmory.isCustomItem(e.getResult())) {
			ItemStack newi = e.getResult();
			newi.setDurability((short) QualityArmory.findSafeSpot(e.getResult(), false));
			e.setResult(newi);
		}
		for (ItemStack is : e.getInventory().getContents()) {
			if (is != null && QualityArmory.isCustomItem(is)) {
				e.setResult(new ItemStack(Material.AIR));
				return;
			}
		}
	}

	@EventHandler
	public void onItemHandSwap(PlayerSwapHandItemsEvent e) {
		if (Main.reloadOnF || Main.reloadOnFOnly) {
			if (QualityArmory.isIronSights(e.getOffHandItem())) {
				e.setCancelled(true);
				e.getPlayer().getInventory().setItemInMainHand(e.getMainHandItem());
				e.getPlayer().getInventory().setItemInOffHand(null);
				return;
			} else if (QualityArmory.isGun(e.getMainHandItem())
					|| (QualityArmory.isGunWithAttchments(e.getMainHandItem()))) {
				e.setCancelled(true);
				e.getPlayer().getInventory().setItemInMainHand(e.getMainHandItem());
				e.getPlayer().getInventory().setItemInOffHand(null);
				return;
			}
			AttachmentBase attach = null;
			Gun g = null;
			if (QualityArmory.isIronSights(e.getOffHandItem())) {
				if (QualityArmory.isGunWithAttchments(e.getMainHandItem())) {
					attach = QualityArmory.getGunWithAttchments(e.getMainHandItem());
					g = Main.gunRegister.get(attach.getBase());
				} else {
					if (QualityArmory.isGun(e.getMainHandItem()))
						g = QualityArmory.getGun(e.getMainHandItem());
				}
			}else {
				if (QualityArmory.isGunWithAttchments(e.getOffHandItem())) {
					attach = QualityArmory.getGunWithAttchments(e.getOffHandItem());
					g = Main.gunRegister.get(attach.getBase());
				} else {
					if (QualityArmory.isGun(e.getOffHandItem()))
						g = QualityArmory.getGun(e.getOffHandItem());
				}
				
			}
			if (g != null) {
				e.setCancelled(true);
				if (g.playerHasAmmo(e.getPlayer())) {
					g.reload(e.getPlayer(), attach);
				}
			}

		} else {
			if (QualityArmory.isGun(e.getOffHandItem())) {
				e.setCancelled(true);
				return;
			}
			if (QualityArmory.isIronSights(e.getOffHandItem())) {
				e.setCancelled(true);
				return;
			}
			/*
			 * if (e.getOffHandItem() != null && QualityArmory.isGun(e.getOffHandItem())) {
			 * e.setCancelled(true); return; }
			 */
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onSwig(PlayerAnimationEvent e) {
		if (e.getAnimationType() == PlayerAnimationType.ARM_SWING) {
			try {
				if (e.getPlayer().getItemInHand() != null
						&& QualityArmory.isGun(e.getPlayer().getInventory().getItemInMainHand()))
					e.setCancelled(true);
				if (e.getPlayer().getInventory().getItemInOffHand() != null
						&& QualityArmory.isGun(e.getPlayer().getInventory().getItemInOffHand()))
					e.setCancelled(true);
			} catch (Error | Exception re) {
			}
		}
	}
}
