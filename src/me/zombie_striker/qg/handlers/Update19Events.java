package me.zombie_striker.qg.handlers;

import me.zombie_striker.qg.Main;
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
					e.getPlayer().getInventory()
							.setItemInMainHand(e.getMainHandItem());
					e.getPlayer().getInventory().setItemInOffHand(null);
				}
			} catch (Error e2) {
			}
			Gun g = null;
			if (Main.getInstance().isGun(e.getOffHandItem())) {
				g = Main.getInstance().getGun(e.getOffHandItem());
			} else if (Main.getInstance().isGun(e.getMainHandItem())) {
				g = Main.getInstance().getGun(e.getMainHandItem());
			}
			if (g != null) {
				e.setCancelled(true);
				if (g.playerHasAmmo(e.getPlayer())) {
					g.reload(e.getPlayer());
				}
			}

		} else {
			if (Main.getInstance().isGun(e.getMainHandItem())) {
				e.setCancelled(true);
				return;
			}
			if (e.getOffHandItem() != null
					&& e.getOffHandItem().getType() == Main.guntype
					&& Main.gunRegister.containsKey((int) e.getOffHandItem()
							.getDurability())) {
				e.setCancelled(true);
				return;
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onSwig(PlayerAnimationEvent e) {
		if (e.getAnimationType() == PlayerAnimationType.ARM_SWING) {
			if (e.getPlayer().getItemInHand() != null
					&& e.getPlayer().getItemInHand().getType() == Main.guntype
					&& Main.gunRegister.containsKey((int) e.getPlayer()
							.getItemInHand().getDurability()))
				e.setCancelled(true);
			if (e.getPlayer().getInventory().getItemInOffHand() != null
					&& e.getPlayer().getInventory().getItemInOffHand()
							.getType() == Main.guntype
					&& Main.gunRegister.containsKey((int) e.getPlayer()
							.getInventory().getItemInOffHand().getDurability()))
				e.setCancelled(true);
		}
	}
}
