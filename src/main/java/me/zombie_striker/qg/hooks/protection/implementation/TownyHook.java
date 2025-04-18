package me.zombie_striker.qg.hooks.protection.implementation;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
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
        try {
            Town towny = TownyAPI.getInstance().getTown(location);
            if (towny == null) return true;

            return towny.isExplosion();
        } catch (Throwable ignored) { return true; }
    }

    @Override
    public boolean canBreak(Location location) {
        try {
            return TownyAPI.getInstance().getTown(location) == null;
        } catch (Throwable ignored) { return true; }
    }

}
