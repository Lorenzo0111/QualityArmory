package me.zombie_striker.qg.api;

import me.zombie_striker.qg.guns.Gun;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class QAWeaponDamageBlockEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private final Player player;
	private final Gun g;
	private final Block damaged;
	private boolean cancel = false;

	public QAWeaponDamageBlockEvent(Player p, Gun g, Block damaged) {
		this.player = p;
		this.g = g;
		this.damaged = damaged;
	}

	public Block getBlock() {
		return damaged;
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