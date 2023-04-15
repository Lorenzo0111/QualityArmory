package me.zombie_striker.qualityarmory.hooks.protection.implementation;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.ResidenceManager;
import me.zombie_striker.qualityarmory.hooks.protection.ProtectionHook;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Objects;

public class ResidenceHook implements ProtectionHook {
    private final ResidenceManager manager;

    public ResidenceHook() {
        this.manager = ((Residence) Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("Residence"))).getResidenceManager();
    }

    @Override
    public boolean canPvp(Location location) {
        ClaimedResidence res = manager.getByLoc(location);
        return res == null || res.getPermissions().has(Flags.pvp, false);
    }

    @Override
    public boolean canExplode(Location location) {
        ClaimedResidence res = manager.getByLoc(location);
        return res == null || res.getPermissions().has(Flags.explode, false);
    }

    @Override
    public boolean canBreak(Location location) {
        ClaimedResidence res = manager.getByLoc(location);
        return res == null || res.getPermissions().has(Flags.build, false);
    }

}
