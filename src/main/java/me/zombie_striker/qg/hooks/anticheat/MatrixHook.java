package me.zombie_striker.qg.hooks.anticheat;

import org.bukkit.event.EventHandler;

import me.rerere.matrix.api.events.PlayerViolationEvent;

public class MatrixHook extends AntiCheatHook {

    @Override
    public String getName() { return "Matrix"; }

    @EventHandler
    public void onFlag(final PlayerViolationEvent event) { this.onViolation(event.getPlayer(), event); }
}
