package me.zombie_striker.qg.boundingbox;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import me.zombie_striker.qg.handlers.HeadShotUtil;

public class DefaultBoundingBox implements AbstractBoundingBox{

	public DefaultBoundingBox() {
		BoundingBoxManager.addBoundingBox("default", this);
	}
	
	@Override
	public boolean intersects(Entity shooter, Location check, Entity base) {
		return HeadShotUtil.closeEnough(base, check);
	}

	@Override
	public boolean allowsHeadshots() {
		return true;
	}

	@Override
	public boolean isHeadShot(Location check, Entity base) {
		return HeadShotUtil.isHeadShot(base, check);
	}

	@Override
	public double maximumCheckingDistance(Entity base) {
		return 5;
	}

}
