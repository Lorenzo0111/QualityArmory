package me.zombie_striker.qualityarmory.hooks.anticheat;

import me.rerere.matrix.api.events.PlayerViolationEvent;
import org.bukkit.event.EventHandler;

public class MatrixHook extends AntiCheatHook {

    @Override
    public String getName() {
        return "Matrix";
    }

    @EventHandler
    public void onFlag(PlayerViolationEvent event) {
        this.onViolation(event.getPlayer(),event);
    }
}
