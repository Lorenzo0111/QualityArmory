package me.zombie_striker.qualityarmory.boundingbox;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class NullBoundingBox implements AbstractBoundingBox{
	
	@Override
	public boolean intersects(Entity shooter, Location check, Entity base) {
		return false;
	}

	@Override
	public boolean allowsHeadshots() {
		return true;
	}

	@Override
	public boolean intersectsHead(Location check, Entity base) {
		return false;
	}

	@Override
	public boolean intersectsBody(Location check, Entity base) {
		return false;
	}

	@Override
	public double maximumCheckingDistance(Entity base) {
		return 0;
	}

}
