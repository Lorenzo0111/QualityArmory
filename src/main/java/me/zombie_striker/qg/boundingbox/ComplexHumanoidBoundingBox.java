package me.zombie_striker.qg.boundingbox;

import me.zombie_striker.qg.handlers.BoundingBoxUtil;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class ComplexHumanoidBoundingBox implements AbstractBoundingBox{

	private double bodyWidthRadius = 0.47;
	private double headWidthRadius = 0.47;

	private double bodyheight = 1.45;
	private double headTopHeight = 1.95;


	public ComplexHumanoidBoundingBox() {
	}

	public ComplexHumanoidBoundingBox(double bodyheight, double bodyRadius, double headTopHeight, double headRadius) {
		this();
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
