package me.zombie_striker.qg.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.zombie_striker.qg.guns.Gun;

public class WeaponInteractEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Gun g;
    private final InteractType type;

    public WeaponInteractEvent(final Player p, final Gun g, final InteractType type) {
        this.player = p;
        this.g = g;
        this.type = type;
    }

    public Gun getGun() { return this.g; }

    public Player getPlayer() { return this.player; }

    public InteractType getType() { return this.type; }

    @Override
    public HandlerList getHandlers() { return WeaponInteractEvent.handlers; }

    public static HandlerList getHandlerList() { return WeaponInteractEvent.handlers; }

    public enum InteractType {
        AIM, UNAIM, RELOAD
    }
}
