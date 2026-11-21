package me.zombie_striker.qg.hooks.protection.implementation;

import me.zombie_striker.qg.utils.ErrorReporter;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import me.zombie_striker.qg.hooks.protection.ProtectionHook;
import org.bukkit.Location;

public class TownyHook implements ProtectionHook {

    @Override
    public boolean canPvp(Location location) {
        try {
            return TownyAPI.getInstance().isPVP(location);
        } catch (RuntimeException | LinkageError e) {
            ErrorReporter.report("checking Towny protection", e);
            return true;
        }
    }

    @Override
    public boolean canExplode(Location location) {
        try {
            Town towny = TownyAPI.getInstance().getTown(location);
            if (towny == null) return true;

            return towny.isExplosion();
        } catch (RuntimeException | LinkageError e) {
            ErrorReporter.report("checking Towny protection", e);
            return true;
        }
    }

    @Override
    public boolean canBreak(Location location) {
        try {
            return TownyAPI.getInstance().getTown(location) == null;
        } catch (RuntimeException | LinkageError e) {
            ErrorReporter.report("checking Towny protection", e);
            return true;
        }
    }

}
