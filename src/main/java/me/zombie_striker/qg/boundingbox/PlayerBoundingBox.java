package me.zombie_striker.qg.boundingbox;

import me.zombie_striker.qg.handlers.BoundingBoxUtil;
import org.bukkit.Location;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PlayerBoundingBox implements AbstractBoundingBox{

	private double bodyWidthRadius = 0.47;

	private double bodyheight = 1.45;
	private double headTopHeight = 1.95;

	private double headTopCrouching = 1.49;
	private double bodyheightCrouching =1;

	public PlayerBoundingBox() {
		//BoundingBoxManager.addBoundingBox("defaulthumanoid", this);
	}
	public PlayerBoundingBox(double bodyheight, double bodyRadius, double headTopHeight) {
		this();
		this.bodyheight = bodyheight;
		this.bodyWidthRadius = bodyRadius;
		this.headTopHeight = headTopHeight;

	}
	
	@Override
	public boolean intersects(Entity shooter, Location check, Entity base) {
		boolean intersectsBodyWIDTH = BoundingBoxUtil.within2DWidth(base, check, bodyWidthRadius, bodyWidthRadius);
		if (!intersectsBodyWIDTH)
			return false;
		return intersectsHead(check, base) || intersectsBody(check, base);
	}

	@Override
	public boolean allowsHeadshots() {
		return true;
	}

	@Override
	public boolean intersectsHead(Location check, Entity base) {
		if(((Player)base).isSneaking()){
			return BoundingBoxUtil.within2DHeight(base,check,(headTopCrouching-bodyheightCrouching), bodyheightCrouching);
		}
		return BoundingBoxUtil.within2DHeight(base,check,(headTopHeight-bodyheight), bodyheight);
	}

	@Override
	public boolean intersectsBody(Location check, Entity base) {
		if(((Player)base).isSneaking()){
			return BoundingBoxUtil.within2DHeight(base,check,bodyheightCrouching);
		}
		return BoundingBoxUtil.within2DHeight(base,check,bodyheight);
	}

	@Override
	public double maximumCheckingDistance(Entity base) {
		return bodyWidthRadius*2;
	}

}
