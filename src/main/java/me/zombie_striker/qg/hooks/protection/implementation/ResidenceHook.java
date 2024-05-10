package me.zombie_striker.qg.hooks.protection.implementation;

import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.ResidenceManager;

import me.zombie_striker.qg.hooks.protection.ProtectionHook;

public class ResidenceHook implements ProtectionHook {
    private final ResidenceManager manager;

    public ResidenceHook() {
        this.manager = ((Residence) Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("Residence"))).getResidenceManager();
    }

    @Override
    public boolean canPvp(final Location location) {
        final ClaimedResidence res = this.manager.getByLoc(location);
        return res == null || res.getPermissions().has(Flags.pvp, false);
    }

    @Override
    public boolean canExplode(final Location location) {
        final ClaimedResidence res = this.manager.getByLoc(location);
        return res == null || res.getPermissions().has(Flags.explode, false);
    }

    @Override
    public boolean canBreak(final Location location) {
        final ClaimedResidence res = this.manager.getByLoc(location);
        return res == null || res.getPermissions().has(Flags.build, false);
    }

}
