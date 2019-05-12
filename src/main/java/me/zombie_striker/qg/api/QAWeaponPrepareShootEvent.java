package me.zombie_striker.qg.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.zombie_striker.qg.guns.Gun;

public class QAWeaponPrepareShootEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

	private boolean cancel = false;
	private Player player;
	private Gun g;

	public QAWeaponPrepareShootEvent(Player p, Gun g) {
		this.player = p;
		this.g = g;
	}

	public Gun getGun() {
		return g;
	}

	public Player getPlayer() {
		return player;
	}

	public boolean isCanceled() {
		return cancel;
	}

	public void setCanceled(boolean canceled) {
		this.cancel = canceled;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}