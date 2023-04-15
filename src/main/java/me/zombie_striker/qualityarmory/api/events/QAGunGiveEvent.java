package me.zombie_striker.qualityarmory.api.events;

import me.zombie_striker.qualityarmory.guns.Gun;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class QAGunGiveEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancel = false;
    private Gun gun;
    private final Cause cause;

    public QAGunGiveEvent(@NotNull Player who, Gun gun, Cause cause) {
        super(who);
        this.gun = gun;
        this.cause = cause;
    }

    public Cause getCause() {
        return cause;
    }

    public Gun getGun() {
        return gun;
    }

    public void setGun(Gun gun) {
        this.gun = gun;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public enum Cause {
        SHOP,
        COMMAND
    }
}
