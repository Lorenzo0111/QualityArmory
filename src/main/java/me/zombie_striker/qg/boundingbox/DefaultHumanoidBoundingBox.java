package me.zombie_striker.qg.boundingbox;

import org.bukkit.Location;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;

import me.zombie_striker.qg.handlers.BoundingBoxUtil;

public class DefaultHumanoidBoundingBox implements AbstractBoundingBox {

    private double bodyWidthRadius = 0.48;
    private double bodyWidthRadius_baby = 0.48;

    private double bodyheight = 1.45;
    private double headTopHeight = 1.95;

    private double bodyheight_baby = 0.60;
    private double headTopHeight_baby = 1;

    public DefaultHumanoidBoundingBox() {
        // BoundingBoxManager.addBoundingBox("defaulthumanoid", this);
    }

    public DefaultHumanoidBoundingBox(final double bodyheight, final double bodyRadius, final double headTopHeight) {
        this();
        this.bodyheight = bodyheight;
        this.bodyWidthRadius = bodyRadius;
        this.headTopHeight = headTopHeight;

    }

    public DefaultHumanoidBoundingBox(final double bodyheight, final double bodyRadius, final double headTopHeight,
            final double b_bodyheight, final double b_bodyRadius, final double b_headTopHeight) {
        this(bodyheight, bodyRadius, headTopHeight);
        this.bodyheight_baby = b_bodyheight;
        this.bodyWidthRadius_baby = b_bodyRadius;
        this.headTopHeight_baby = b_headTopHeight;
    }

    @Override
    public boolean intersects(final Entity shooter, final Location check, final Entity base) {
        if (base instanceof Ageable && !((Ageable) base).isAdult()) {
            final boolean intersectsBody = BoundingBoxUtil.within2DWidth(base, check, this.bodyWidthRadius_baby, this.bodyWidthRadius_baby);
            if (!intersectsBody)
                return false;
            return this.intersectsHead(check, base) || this.intersectsBody(check, base); // BoundingBoxUtil.within2D(base,check,bodyWidthRadius_baby,
                                                                                         // headTopHeight_baby,bodyWidthRadius_baby);
        }
        final boolean intersectsBody = BoundingBoxUtil.within2DWidth(base, check, this.bodyWidthRadius, this.bodyWidthRadius);
        if (!intersectsBody)
            return false;
        return this.intersectsHead(check, base) || this.intersectsBody(check, base);
    }

    @Override
    public boolean allowsHeadshots() { return true; }

    @Override
    public boolean intersectsHead(final Location check, final Entity base) {
        if (base instanceof Ageable && !((Ageable) base).isAdult()) {
            return BoundingBoxUtil.within2DHeight(base, check, (this.headTopHeight_baby - this.bodyheight_baby), this.bodyheight_baby);
        }
        return BoundingBoxUtil.within2DHeight(base, check, (this.headTopHeight - this.bodyheight), this.bodyheight);
    }

    @Override
    public boolean intersectsBody(final Location check, final Entity base) {
        if (base instanceof Ageable && !((Ageable) base).isAdult()) {
            final boolean old_box = BoundingBoxUtil.within2DHeight(base, check, this.bodyheight_baby);

            return old_box;
        }
        final boolean old_box = BoundingBoxUtil.within2DHeight(base, check, this.headTopHeight);

        return old_box;
    }

    @Override
    public double maximumCheckingDistance(final Entity base) { return this.bodyWidthRadius * 2; }

}
