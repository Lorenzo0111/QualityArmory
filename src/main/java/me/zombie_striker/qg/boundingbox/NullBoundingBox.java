package me.zombie_striker.qg.boundingbox;

import me.zombie_striker.qg.handlers.BoundingBoxUtil;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

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
