package me.zombie_striker.qg.api;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.zombie_striker.qg.guns.Gun;

public class QAHeadShotEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

	private boolean cancel = false;
	private Entity damaged;
	private Player player;
	private Gun g;

	public QAHeadShotEvent(Entity damaged, Player p, Gun g) {
		this.player = p;
		this.g = g;
		this.damaged = damaged;
	}

	public Gun getGun() {
		return g;
	}

	public Entity getDamagedEntity() {
		return damaged;
	}

	public Player getShooter() {
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