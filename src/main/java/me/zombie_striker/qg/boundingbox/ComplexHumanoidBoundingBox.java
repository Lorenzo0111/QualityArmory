package me.zombie_striker.qg.boundingbox;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import me.zombie_striker.qg.handlers.BoundingBoxUtil;

public class ComplexHumanoidBoundingBox implements AbstractBoundingBox {

    private double bodyWidthRadius = 0.47;
    private double headWidthRadius = 0.47;

    private double bodyheight = 1.45;
    private double headTopHeight = 1.95;

    public ComplexHumanoidBoundingBox() {}

    public ComplexHumanoidBoundingBox(final double bodyheight, final double bodyRadius, final double headTopHeight,
            final double headRadius) {
        this();
        this.bodyheight = bodyheight;
        this.bodyWidthRadius = bodyRadius;
        this.headTopHeight = headTopHeight;
        this.headWidthRadius = headRadius;

    }

    @Override
    public boolean intersects(final Entity shooter, final Location check, final Entity base) {
        if (this.intersectsHead(check, base))
            return true;
        if (this.intersectsBody(check, base))
            return true;
        return false;
    }

    @Override
    public boolean allowsHeadshots() { return true; }

    @Override
    public boolean intersectsBody(final Location check, final Entity base) {
        return BoundingBoxUtil.within2D(base, check, this.headWidthRadius, this.bodyheight,
                Math.max(this.bodyWidthRadius, this.headWidthRadius));
    }

    @Override
    public boolean intersectsHead(final Location check, final Entity base) {
        return BoundingBoxUtil.within2D(base, check, this.headWidthRadius, (this.headTopHeight - this.bodyheight), this.bodyheight,
                Math.max(this.bodyWidthRadius, this.headWidthRadius));
    }

    @Override
    public double maximumCheckingDistance(final Entity base) { return Math.max(this.bodyWidthRadius, this.headWidthRadius) * 2; }

}
