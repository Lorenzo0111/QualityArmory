package me.zombie_striker.qg.hooks.anticheat;

import me.vagdedes.spartan.api.PlayerViolationEvent;
import org.bukkit.event.EventHandler;

public class SpartanHook extends AntiCheatHook {

    @Override
    public String getName() {
        return "Spartan";
    }

    @EventHandler
    public void onFlag(PlayerViolationEvent event) {
        this.onViolation(event.getPlayer(), event);
    }

}
