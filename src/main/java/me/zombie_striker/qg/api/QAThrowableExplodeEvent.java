package me.zombie_striker.qg.api;

import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.zombie_striker.qg.miscitems.ThrowableItems;

public class QAThrowableExplodeEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final ThrowableItems throwable;
    private final Location location;
    private boolean cancel = false;

    public QAThrowableExplodeEvent(final ThrowableItems throwable, final Location location) {
        this.throwable = throwable;
        this.location = location;
    }

    public ThrowableItems getThrowable() { return this.throwable; }

    public Location getLocation() { return this.location; }

    @Override
    public boolean isCancelled() { return this.cancel; }

    @Override
    public void setCancelled(final boolean canceled) { this.cancel = canceled; }

    @Override
    public HandlerList getHandlers() { return QAThrowableExplodeEvent.handlers; }

    public static HandlerList getHandlerList() { return QAThrowableExplodeEvent.handlers; }
}
