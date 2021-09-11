package me.zombie_striker.qg.api;

import me.zombie_striker.customitemmanager.CustomBaseObject;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class QACustomItemInteractEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();

	private boolean cancel = false;
	private Player player;
	private CustomBaseObject g;

	public QACustomItemInteractEvent(Player p, CustomBaseObject g) {
		this.player = p;
		this.g = g;
	}

	public CustomBaseObject getCustomItem() {
		return g;
	}

	public Player getPlayer() {
		return player;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
}