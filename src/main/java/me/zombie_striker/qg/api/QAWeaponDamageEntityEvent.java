package me.zombie_striker.qg.api;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.zombie_striker.qg.guns.Gun;

public class QAWeaponDamageEntityEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Gun g;
    private final Entity damaged;
    private boolean wasHeadshot = false;
    private double damage;
    private boolean hasProtection = false;
    private boolean cancel = false;

    public QAWeaponDamageEntityEvent(final Player p, final Gun g, final Entity damaged, final boolean wasHeadshot, final double damage,
            final boolean hasProtection) {
        this.player = p;
        this.g = g;
        this.damage = damage;
        this.wasHeadshot = wasHeadshot;
        this.damaged = damaged;
        this.hasProtection = hasProtection;
    }

    public double getDamage() { return this.damage; }

    public void setDamage(final double d) { this.damage = d; }

    public Entity getDamaged() { return this.damaged; }

    public boolean isHeadshot() { return this.wasHeadshot; }

    public boolean hadProtection() { return this.hasProtection; }

    public Gun getGun() { return this.g; }

    public Player getPlayer() { return this.player; }

    @Override
    public boolean isCancelled() { return this.cancel; }

    @Override
    public void setCancelled(final boolean canceled) { this.cancel = canceled; }

    @Override
    public HandlerList getHandlers() { return QAWeaponDamageEntityEvent.handlers; }

    public static HandlerList getHandlerList() { return QAWeaponDamageEntityEvent.handlers; }
}