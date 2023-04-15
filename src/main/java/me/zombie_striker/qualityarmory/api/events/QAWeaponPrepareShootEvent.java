package me.zombie_striker.qualityarmory.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.zombie_striker.qualityarmory.guns.Gun;

public class QAWeaponPrepareShootEvent extends Event implements Cancellable {
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

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean canceled) {
		this.cancel = canceled;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}