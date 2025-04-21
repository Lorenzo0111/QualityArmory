package me.zombie_striker.qg.api;

import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.zombie_striker.qg.guns.projectiles.RealtimeCalculationProjectile;

public class QAProjectileExplodeEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final RealtimeCalculationProjectile projectile;
    private final Location location;
    private boolean cancel = false;

    public QAProjectileExplodeEvent(final RealtimeCalculationProjectile projectile, final Location location) {
        this.projectile = projectile;
        this.location = location;
    }

    public RealtimeCalculationProjectile getProjectile() { return this.projectile; }

    public Location getLocation() { return this.location; }

    @Override
    public boolean isCancelled() { return this.cancel; }

    @Override
    public void setCancelled(final boolean canceled) { this.cancel = canceled; }

    @Override
    public HandlerList getHandlers() { return QAProjectileExplodeEvent.handlers; }

    public static HandlerList getHandlerList() { return QAProjectileExplodeEvent.handlers; }
}
