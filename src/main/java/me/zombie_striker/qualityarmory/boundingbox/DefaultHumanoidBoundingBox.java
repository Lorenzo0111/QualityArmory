package me.zombie_striker.qualityarmory.boundingbox;

import me.zombie_striker.qualityarmory.utils.BoundingBoxUtil;
import org.bukkit.Location;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;

public class DefaultHumanoidBoundingBox implements AbstractBoundingBox{

	private double bodyWidthRadius;
	private double bodyWidthRadius_baby = 0.48;

	private double bodyheight;
	private double headTopHeight;

	private double bodyheight_baby = 0.60;
	private double headTopHeight_baby = 1;

	public DefaultHumanoidBoundingBox(double bodyheight, double bodyRadius, double headTopHeight) {
		this.bodyheight = bodyheight;
		this.bodyWidthRadius = bodyRadius;
		this.headTopHeight = headTopHeight;

	}
	public DefaultHumanoidBoundingBox(double bodyheight, double bodyRadius, double headTopHeight, double b_bodyheight, double b_bodyRadius, double b_headTopHeight) {
		this(bodyheight,bodyRadius,headTopHeight);
		this.bodyheight_baby = b_bodyheight;
		this.bodyWidthRadius_baby = b_bodyRadius;
		this.headTopHeight_baby = b_headTopHeight;
	}
	
	@Override
	public boolean intersects(Entity shooter, Location check, Entity base) {
		if (base instanceof Ageable && !((Ageable) base).isAdult() ){
			boolean intersectsBody = BoundingBoxUtil.within2DWidth(base,check,bodyWidthRadius_baby,bodyWidthRadius_baby);
			if(!intersectsBody)
				return false;
			return intersectsHead(check, base) || intersectsBody(check, base); //BoundingBoxUtil.within2D(base,check,bodyWidthRadius_baby, headTopHeight_baby,bodyWidthRadius_baby);
		}
		boolean intersectsBody = BoundingBoxUtil.within2DWidth(base,check,bodyWidthRadius,bodyWidthRadius);
		if(!intersectsBody)
			return false;
		return intersectsHead(check, base) || intersectsBody(check, base);
	}

	@Override
	public boolean allowsHeadshots() {
		return true;
	}

	@Override
	public boolean intersectsHead(Location check, Entity base) {
		if (base instanceof Ageable && !((Ageable) base).isAdult() ){
			return BoundingBoxUtil.within2DHeight(base,check,(headTopHeight_baby-bodyheight_baby),bodyheight_baby);
		}
		return BoundingBoxUtil.within2DHeight(base,check,(headTopHeight-bodyheight), bodyheight);
	}

	@Override
	public boolean intersectsBody(Location check, Entity base) {
		if (base instanceof Ageable && !((Ageable) base).isAdult() ){
			boolean old_box =  BoundingBoxUtil.within2DHeight(base,check,bodyheight_baby);
			return old_box;
		}
		boolean old_box =  BoundingBoxUtil.within2DHeight(base,check,headTopHeight);
		return old_box;
	}

	@Override
	public double maximumCheckingDistance(Entity base) {
		return bodyWidthRadius*2;
	}

}
