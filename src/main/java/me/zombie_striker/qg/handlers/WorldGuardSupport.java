package me.zombie_striker.qg.handlers;

import org.bukkit.Location;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class WorldGuardSupport {
	public static boolean a(Location loc){
			WorldGuard wGuard = WorldGuard.getInstance();
			for (ProtectedRegion k : wGuard.getPlatform().getRegionContainer().get(BukkitAdapter.adapt(loc.getWorld())).getRegions().values()) {
				if (k.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) {
					if (k.getFlag(Flags.PVP) == com.sk89q.worldguard.protection.flags.StateFlag.State.DENY)
						return false;
				}
			}
		return true;
	}
	public static boolean canExplode(Location loc){
		WorldGuard wGuard = WorldGuard.getInstance();
		for (ProtectedRegion k : wGuard.getPlatform().getRegionContainer().get(BukkitAdapter.adapt(loc.getWorld())).getRegions().values()) {
			if (k.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) {
				if (k.getFlag(Flags.OTHER_EXPLOSION) == com.sk89q.worldguard.protection.flags.StateFlag.State.DENY)
					return false;
			}
		}
		return true;
	}
}
