package me.zombie_striker.qg.boundingbox;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public interface AbstractBoundingBox {

	public boolean intersects(Location check, Entity base);
	public boolean allowsHeadshots();
	public boolean isHeadShot(Location check, Entity base);
	public double maximumCheckingDistance(Entity base);
}
