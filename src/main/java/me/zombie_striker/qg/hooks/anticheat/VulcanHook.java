package me.zombie_striker.qg.hooks.anticheat;

import org.bukkit.event.EventHandler;

import me.frep.vulcan.api.event.VulcanFlagEvent;

public class VulcanHook extends AntiCheatHook {

    @Override
    public String getName() { return "Vulcan"; }

    @EventHandler
    public void onFlag(final VulcanFlagEvent event) {
        if (event.getCheck().getCategory().equalsIgnoreCase("movement") || event.getCheck().getCategory().equalsIgnoreCase("velocity")) {
            this.onViolation(event.getPlayer(), event);
        }
    }

}
