package me.zombie_striker.qg.hooks.protection.implementation;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.ResidenceManager;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.zombie_striker.qg.hooks.protection.ProtectionHook;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.maxgamer.quickshop.integration.griefprevention.GriefPreventionIntegration;

import java.util.Objects;

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
