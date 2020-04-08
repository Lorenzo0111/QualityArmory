package me.zombie_striker.qg.api;

import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.customitemmanager.CustomItemManager;
import me.zombie_striker.qg.guns.Gun;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class QACustomItemInteractEvent extends Event {
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