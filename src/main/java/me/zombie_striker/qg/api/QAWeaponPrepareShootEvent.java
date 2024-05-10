package me.zombie_striker.qg.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.zombie_striker.qg.guns.Gun;

public class QAWeaponPrepareShootEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Gun g;
    private boolean cancel = false;

    public QAWeaponPrepareShootEvent(final Player p, final Gun g) {
        this.player = p;
        this.g = g;
    }

    public Gun getGun() { return this.g; }

    public Player getPlayer() { return this.player; }

    @Override
    public boolean isCancelled() { return this.cancel; }

    @Override
    public void setCancelled(final boolean canceled) { this.cancel = canceled; }

    @Override
    public HandlerList getHandlers() { return QAWeaponPrepareShootEvent.handlers; }

    public static HandlerList getHandlerList() { return QAWeaponPrepareShootEvent.handlers; }
}