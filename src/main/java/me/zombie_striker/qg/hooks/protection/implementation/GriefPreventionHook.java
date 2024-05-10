package me.zombie_striker.qg.hooks.protection.implementation;

import org.bukkit.Location;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.zombie_striker.qg.hooks.protection.ProtectionHook;

public class GriefPreventionHook implements ProtectionHook {
    private final GriefPrevention plugin;

    public GriefPreventionHook() { this.plugin = GriefPrevention.instance; }

    @Override
    public boolean canPvp(final Location location) {
        final Claim claim = this.plugin.dataStore.getClaimAt(location, true, null);
        return claim == null;
    }

    @Override
    public boolean canExplode(final Location location) {
        final Claim claim = this.plugin.dataStore.getClaimAt(location, true, null);
        return claim == null;
    }

    @Override
    public boolean canBreak(final Location location) {
        final Claim claim = this.plugin.dataStore.getClaimAt(location, true, null);
        return claim == null;
    }

}
