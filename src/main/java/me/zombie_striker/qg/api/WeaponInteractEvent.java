package me.zombie_striker.qg.api;

import me.zombie_striker.qg.guns.Gun;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WeaponInteractEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Gun g;
    private final InteractType type;

    public WeaponInteractEvent(Player p, Gun g, InteractType type) {
        this.player = p;
        this.g = g;
        this.type = type;
    }

    public Gun getGun() {
        return g;
    }

    public Player getPlayer() {
        return player;
    }

    public InteractType getType() {
        return type;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public enum InteractType {
        AIM,
        UNAIM,
        RELOAD
    }
}
