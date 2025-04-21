package me.zombie_striker.qg.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.zombie_striker.customitemmanager.CustomBaseObject;

public class QACustomItemInteractEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private boolean cancel = false;
    private final Player player;
    private final CustomBaseObject g;

    public QACustomItemInteractEvent(final Player p, final CustomBaseObject g) {
        this.player = p;
        this.g = g;
    }

    public CustomBaseObject getCustomItem() { return this.g; }

    public Player getPlayer() { return this.player; }

    @Override
    public HandlerList getHandlers() { return QACustomItemInteractEvent.handlers; }

    public static HandlerList getHandlerList() { return QACustomItemInteractEvent.handlers; }

    @Override
    public boolean isCancelled() { return this.cancel; }

    @Override
    public void setCancelled(final boolean cancel) { this.cancel = cancel; }
}