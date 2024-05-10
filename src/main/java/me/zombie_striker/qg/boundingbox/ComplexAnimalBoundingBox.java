package me.zombie_striker.qg.boundingbox;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import me.zombie_striker.qg.handlers.BoundingBoxUtil;

public class ComplexAnimalBoundingBox implements AbstractBoundingBox {

    private double bodyWidthRadius = 0.47;
    private double headWidthRadius = 0.47;

    private double bodyheight = 1.45;
    private double headTopHeight = 1.95;
    private double headBottomHeight = 1.95;

    private double headOffsetDistance = 0;

    public ComplexAnimalBoundingBox(final double bodyheight, final double bodyRadius, final double headBottomHeight,
            final double headTopHeight, final double headRadius, final double headOffsetDistance) {
        this.bodyheight = bodyheight;
        this.bodyWidthRadius = bodyRadius;
        this.headBottomHeight = headBottomHeight;
        this.headTopHeight = headTopHeight;
        this.headWidthRadius = headRadius;
        this.headOffsetDistance = headOffsetDistance;

    }

    @Override
    public boolean intersects(final Entity shooter, final Location check, final Entity base) {
        if (this.intersectsBody(check, base))
            return true;
        return this.intersectsHead(check, base);
    }

    @Override
    public boolean allowsHeadshots() { return true; }

    @Override
    public boolean intersectsBody(final Location check, final Entity base) {
        return BoundingBoxUtil.within2D(base, check, this.bodyWidthRadius, this.bodyheight, this.bodyWidthRadius);
    }

    @Override
    public boolean intersectsHead(final Location check, final Entity base) {
        final double cos = Math.cos(base.getLocation().getYaw() / 180 * Math.PI);
        final double sin = Math.sin(base.getLocation().getYaw() / 180 * Math.PI);
        final Vector newVal = new Vector((this.headOffsetDistance * cos) - (0 * sin), 0, (0 * cos) + (this.headOffsetDistance * sin));

        final Location newCheck = check.clone();
        newCheck.subtract(newVal);
        return BoundingBoxUtil.within2D(base, newCheck, this.headWidthRadius, (this.headTopHeight - this.headBottomHeight),
                this.headBottomHeight, Math.max(this.bodyWidthRadius, this.headWidthRadius));
    }

    @Override
    public double maximumCheckingDistance(final Entity base) {
        return Math.max(this.bodyWidthRadius, this.headWidthRadius + this.headOffsetDistance + this.bodyWidthRadius) * 2;
    }

}
