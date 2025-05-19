package me.zombie_striker.qg.hooks.protection.implementation;

import me.zombie_striker.qg.hooks.protection.ProtectionHook;
import org.bukkit.Location;
import org.kingdoms.constants.land.Land;
import org.kingdoms.constants.land.location.SimpleChunkLocation;

public class KingdomsHook implements ProtectionHook {

    @Override
    public boolean canPvp(Location location) {
        SimpleChunkLocation chunk = SimpleChunkLocation.of(location);
        Land land = chunk.getLand();

        return land == null || !land.isClaimed();
    }

    @Override
    public boolean canExplode(Location location) {
        SimpleChunkLocation chunk = SimpleChunkLocation.of(location);
        Land land = chunk.getLand();

        return land == null || !land.isClaimed();
    }

    @Override
    public boolean canBreak(Location location) {
        SimpleChunkLocation chunk = SimpleChunkLocation.of(location);
        Land land = chunk.getLand();

        return land == null || !land.isClaimed();
    }

}
