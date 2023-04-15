package me.zombie_striker.qualityarmory.boundingbox;

import me.zombie_striker.qualityarmory.handlers.BoundingBoxUtil;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class ComplexHumanoidBoundingBox implements AbstractBoundingBox{

	private double bodyWidthRadius;
	private double headWidthRadius;

	private double bodyheight;
	private double headTopHeight;


	public ComplexHumanoidBoundingBox(double bodyheight, double bodyRadius, double headTopHeight, double headRadius) {
		this.bodyheight = bodyheight;
		this.bodyWidthRadius = bodyRadius;
		this.headTopHeight = headTopHeight;
		this.headWidthRadius = headRadius;

	}
	
	@Override
	public boolean intersects(Entity shooter, Location check, Entity base) {
		if(intersectsHead(check, base))
			return true;
		if(intersectsBody(check, base))
			return true;
		return false;
	}

	@Override
	public boolean allowsHeadshots() {
		return true;
	}

	@Override
	public boolean intersectsBody(Location check, Entity base) {
		return BoundingBoxUtil.within2D(base,check,headWidthRadius,bodyheight,Math.max(bodyWidthRadius,headWidthRadius));
	}

	@Override
	public boolean intersectsHead(Location check, Entity base) {
		return BoundingBoxUtil.within2D(base,check,headWidthRadius,(headTopHeight-bodyheight),bodyheight,Math.max(bodyWidthRadius,headWidthRadius));
	}

	@Override
	public double maximumCheckingDistance(Entity base) {
		return Math.max(bodyWidthRadius,headWidthRadius)*2;
	}

}
