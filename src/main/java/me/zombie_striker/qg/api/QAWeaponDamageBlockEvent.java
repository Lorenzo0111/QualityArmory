package me.zombie_striker.qg.api;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.zombie_striker.qg.guns.Gun;

public class QAWeaponDamageBlockEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Gun g;
    private final Block damaged;
    private boolean cancel = false;

    public QAWeaponDamageBlockEvent(final Player p, final Gun g, final Block damaged) {
        this.player = p;
        this.g = g;
        this.damaged = damaged;
    }

    public Block getBlock() { return this.damaged; }

    public Gun getGun() { return this.g; }

    public Player getPlayer() { return this.player; }

    @Override
    public boolean isCancelled() { return this.cancel; }

    @Override
    public void setCancelled(final boolean canceled) { this.cancel = canceled; }

    @Override
    public HandlerList getHandlers() { return QAWeaponDamageBlockEvent.handlers; }

    public static HandlerList getHandlerList() { return QAWeaponDamageBlockEvent.handlers; }
}