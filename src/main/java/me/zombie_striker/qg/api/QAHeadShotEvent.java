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

    public QAHeadShotEvent(final Entity damaged, final Player p, final Gun g) {
        this.player = p;
        this.g = g;
        this.damaged = damaged;
    }

    public Gun getGun() { return this.g; }

    public Entity getDamagedEntity() { return this.damaged; }

    public Player getShooter() { return this.player; }

    @Override
    public boolean isCancelled() { return this.cancel; }

    @Override
    public void setCancelled(final boolean canceled) { this.cancel = canceled; }

    @Override
    public HandlerList getHandlers() { return QAHeadShotEvent.handlers; }

    public static HandlerList getHandlerList() { return QAHeadShotEvent.handlers; }
}