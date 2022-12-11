package me.zombie_striker.qg.api;

import me.zombie_striker.qg.miscitems.ThrowableItems;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class QAThrowableExplodeEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final ThrowableItems throwable;
    private final Location location;
    private boolean cancel = false;

    public QAThrowableExplodeEvent(ThrowableItems throwable, Location location) {
        this.throwable = throwable;
        this.location = location;
    }

    public ThrowableItems getThrowable() {
        return throwable;
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
