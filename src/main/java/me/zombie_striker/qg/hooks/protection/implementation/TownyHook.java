package me.zombie_striker.qg.hooks.protection.implementation;

import org.bukkit.Location;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import me.zombie_striker.qg.hooks.protection.ProtectionHook;

public class TownyHook implements ProtectionHook {

    @Override
    public boolean canPvp(final Location location) {
        try {
            return TownyAPI.getInstance().isPVP(location);
        } catch (final Throwable ignored) {
            return true;
        }
    }

    @Override
    public boolean canExplode(final Location location) {
        try {
            Town towny = TownyAPI.getInstance().getTown(location);
            if (towny == null) return true;

            return towny.isExplosion();
        } catch (Throwable ignored) { return true; }
    }

    @Override
    public boolean canBreak(final Location location) {
        try {
            return TownyAPI.getInstance().getTown(location) == null;
        } catch (final Throwable ignored) {
            return true;
        }
    }

}
