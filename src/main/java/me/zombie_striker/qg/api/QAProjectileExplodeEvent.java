package me.zombie_striker.qg.api;

import me.zombie_striker.qg.guns.projectiles.RealtimeCalculationProjectile;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class QAProjectileExplodeEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final RealtimeCalculationProjectile projectile;
    private final Location location;
    private boolean cancel = false;

    public QAProjectileExplodeEvent(RealtimeCalculationProjectile projectile, Location location) {
        this.projectile = projectile;
        this.location = location;
    }

    public RealtimeCalculationProjectile getProjectile() {
        return projectile;
    }

    public Location getLocation() {
        return location;
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
