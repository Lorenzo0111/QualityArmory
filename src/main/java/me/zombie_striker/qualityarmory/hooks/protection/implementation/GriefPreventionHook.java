package me.zombie_striker.qualityarmory.hooks.protection.implementation;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.zombie_striker.qualityarmory.hooks.protection.ProtectionHook;
import org.bukkit.Location;

public class GriefPreventionHook implements ProtectionHook {
    private final GriefPrevention plugin;

    public GriefPreventionHook() {
        this.plugin = GriefPrevention.instance;
    }

    @Override
    public boolean canPvp(Location location) {
        Claim claim = plugin.dataStore.getClaimAt(location, true, null);
        return claim == null;
    }

    @Override
    public boolean canExplode(Location location) {
        Claim claim = plugin.dataStore.getClaimAt(location, true, null);
        return claim == null;
    }

    @Override
    public boolean canBreak(Location location) {
        Claim claim = plugin.dataStore.getClaimAt(location, true, null);
        return claim == null;
    }

}
