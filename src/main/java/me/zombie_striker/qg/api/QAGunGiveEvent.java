package me.zombie_striker.qg.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

import me.zombie_striker.qg.guns.Gun;

public class QAGunGiveEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancel = false;
    private final Cause cause;
    private Gun gun;

    public QAGunGiveEvent(@NotNull final Player who, final Gun gun, final Cause cause) {
        super(who);
        this.gun = gun;
        this.cause = cause;
    }

    public Cause getCause() { return this.cause; }

    public Gun getGun() { return this.gun; }

    public void setGun(final Gun gun) { this.gun = gun; }

    @Override
    public boolean isCancelled() { return this.cancel; }

    @Override
    public void setCancelled(final boolean cancel) { this.cancel = cancel; }

    @Override
    @NotNull
    public HandlerList getHandlers() { return QAGunGiveEvent.handlers; }

    public static HandlerList getHandlerList() { return QAGunGiveEvent.handlers; }

    public enum Cause {
        SHOP, COMMAND
    }
}
