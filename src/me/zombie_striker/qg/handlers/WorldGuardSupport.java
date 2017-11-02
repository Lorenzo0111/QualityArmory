package me.zombie_striker.qg.handlers;

import org.bukkit.Location;

public class WorldGuardSupport {
	public static boolean a(Location loc){
		for (com.sk89q.worldguard.protection.regions.ProtectedRegion k : com.sk89q.worldguard.bukkit.WorldGuardPlugin.inst()
				.getRegionManager(loc.getWorld()).getRegions().values()) {
			if (k.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) {
				if (k.getFlag(com.sk89q.worldguard.protection.flags.DefaultFlag.PVP) == com.sk89q.worldguard.protection.flags.StateFlag.State.DENY)
					return false;
			}
		}
		return true;
	}
}
