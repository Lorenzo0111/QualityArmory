package me.zombie_striker.qualityarmory.hooks.protection;

import org.bukkit.Location;

public interface ProtectionHook {
    boolean canPvp(Location location);
    boolean canExplode(Location location);
    boolean canBreak(Location location);
}
