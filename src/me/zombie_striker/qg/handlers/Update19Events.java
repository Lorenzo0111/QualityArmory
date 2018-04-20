package me.zombie_striker.qg.handlers;

import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.attachments.AttachmentBase;
import me.zombie_striker.qg.guns.Gun;

import org.bukkit.event.*;
import org.bukkit.event.player.*;

public class Update19Events implements Listener {

	@EventHandler
	public void onItemHandSwap(PlayerSwapHandItemsEvent e) {
		if (Main.reloadOnF) {
			try {
				if (Main.getInstance().isIS(e.getOffHandItem())) {
					e.setCancelled(true);
					e.getPlayer().getInventory().setItemInMainHand(e.getMainHandItem());
					e.getPlayer().getInventory().setItemInOffHand(null);
				}
			} catch (Error e2) {
			}
			Gun g = null;
			if (Main.isGun(e.getOffHandItem())) {
				g = Main.getInstance().getGun(e.getOffHandItem());
			} else if (Main.isGun(e.getMainHandItem())) {
				g = Main.getInstance().getGun(e.getMainHandItem());
			}
			if (g != null) {
				e.setCancelled(true);
				if (g.playerHasAmmo(e.getPlayer())) {
					AttachmentBase attachmentBase = Main.getInstance().getGunWithAttchments(e.getOffHandItem());
					g.reload(e.getPlayer(),attachmentBase);
				}
			}

		} else {
			if (Main.isGun(e.getMainHandItem())) {
				e.setCancelled(true);
				return;
			}
			if (e.getOffHandItem() != null && Main.isGun(e.getOffHandItem())) {
				e.setCancelled(true);
				return;
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onSwig(PlayerAnimationEvent e) {
		if (e.getAnimationType() == PlayerAnimationType.ARM_SWING) {
			try {
				if (e.getPlayer().getItemInHand() != null
						&& Main.isGun(e.getPlayer().getInventory().getItemInMainHand()))
					e.setCancelled(true);
				if (e.getPlayer().getInventory().getItemInOffHand() != null
						&& Main.isGun(e.getPlayer().getInventory().getItemInOffHand()))
					e.setCancelled(true);
			} catch (Error | Exception re) {
			}
		}
	}
}
