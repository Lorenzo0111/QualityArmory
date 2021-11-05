package me.zombie_striker.qg.hooks.protection.implementation;

import com.palmergames.bukkit.towny.TownyAPI;
import me.zombie_striker.qg.hooks.protection.ProtectionHook;
import org.bukkit.Location;

public class TownyHook implements ProtectionHook {

    @Override
    public boolean canPvp(Location location) {
        try {
            return TownyAPI.getInstance().isPVP(location);
        } catch (Throwable ignored) { return true; }
    }

    @Override
    public boolean canExplode(Location location) {
        return true;
    }

}
