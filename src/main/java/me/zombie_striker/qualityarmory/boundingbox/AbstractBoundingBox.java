package me.zombie_striker.qualityarmory.boundingbox;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public interface AbstractBoundingBox {

	boolean intersects(Entity shooter, Location check, Entity base);
	boolean allowsHeadshots();
	boolean intersectsHead(Location check, Entity base);
	boolean intersectsBody(Location check, Entity base);
	double maximumCheckingDistance(Entity base);
}
