package me.zombie_striker.qualityarmory.api.events;

import me.zombie_striker.qualityarmory.guns.Gun;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class QAHeadShotEvent extends Event implements Cancellable {
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