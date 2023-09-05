package me.zombie_striker.qg.api;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.zombie_striker.qg.guns.Gun;

public class QAHeadShotEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private final Entity damaged;
	private final Player player;
	private final Gun g;
	private boolean cancel = false;

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