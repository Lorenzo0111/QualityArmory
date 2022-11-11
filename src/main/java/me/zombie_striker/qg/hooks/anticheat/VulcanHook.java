package me.zombie_striker.qg.hooks.anticheat;

import me.frep.vulcan.api.event.VulcanFlagEvent;
import org.bukkit.event.EventHandler;

public class VulcanHook extends AntiCheatHook {

    @Override
    public String getName() {
        return "Vulcan";
    }

    @EventHandler
    public void onFlag(VulcanFlagEvent event) {
        if (event.getCheck().getCategory().equalsIgnoreCase("movement") || event.getCheck().getCategory().equalsIgnoreCase("velocity")) {
            this.onViolation(event.getPlayer(),event);
        }
    }

}
