package me.zombie_striker.qg.api;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.zombie_striker.qg.guns.Gun;

public class QAWeaponDamageEntityEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();

	private boolean cancel = false;
	private Player player;
	private Gun g;
	private Entity damaged;
	private boolean wasHeadshot = false;
	private double damage;
	private boolean hasProtection = false;

	public QAWeaponDamageEntityEvent(Player p, Gun g, Entity damaged, boolean wasHeadshot, double damage, boolean hasProtection) {
		this.player = p;
		this.g = g;
		this.damage = damage;
		this.wasHeadshot = wasHeadshot;
		this.damaged = damaged;
		this.hasProtection = hasProtection;
	}
	
	public double getDamage() {
		return damage;
	}
	public void setDamage(double d) {
		this.damage =d ;
	}
	public Entity getDamaged() {
		return damaged;
	}
	public boolean isHeadshot() {
		return wasHeadshot;
	}
	public boolean hadProtection() {
		return hasProtection;
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

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}