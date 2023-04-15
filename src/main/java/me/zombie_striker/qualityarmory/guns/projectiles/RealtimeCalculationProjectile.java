package me.zombie_striker.qualityarmory.guns.projectiles;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import me.zombie_striker.qualityarmory.guns.Gun;

public interface RealtimeCalculationProjectile {

	public String getName();
	
	public void spawn(final Gun g, final Location starting, final Player player, final Vector dir);
}
