package me.zombie_striker.qg.boundingbox;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import me.zombie_striker.qg.handlers.BoundingBoxUtil;

public class PlayerBoundingBox implements AbstractBoundingBox {

    private double bodyWidthRadius = 0.51;

    private double bodyheight = 1.45;
    private double headTopHeight = 1.95;

    private final double headTopCrouching = 1.49;
    private final double bodyheightCrouching = 1;

    public PlayerBoundingBox() {
        // BoundingBoxManager.addBoundingBox("defaulthumanoid", this);
    }

    public PlayerBoundingBox(final double bodyheight, final double bodyRadius, final double headTopHeight) {
        this();
        this.bodyheight = bodyheight;
        this.bodyWidthRadius = bodyRadius;
        this.headTopHeight = headTopHeight;

    }

    @Override
    public boolean intersects(final Entity shooter, final Location check, final Entity base) {
        final boolean intersectsBodyWIDTH = BoundingBoxUtil.within2DWidth(base, check, this.bodyWidthRadius, this.bodyWidthRadius);
        if (!intersectsBodyWIDTH)
            return false;
        return this.intersectsHead(check, base) || this.intersectsBody(check, base);
    }

    @Override
    public boolean allowsHeadshots() { return true; }

    @Override
    public boolean intersectsHead(final Location check, final Entity base) {
        if (((Player) base).isSneaking()) {
            return BoundingBoxUtil.within2DHeight(base, check, (this.headTopCrouching - this.bodyheightCrouching),
                    this.bodyheightCrouching);
        }
        return BoundingBoxUtil.within2DHeight(base, check, (this.headTopHeight - this.bodyheight), this.bodyheight);
    }

    @Override
    public boolean intersectsBody(final Location check, final Entity base) {
        if (((Player) base).isSneaking()) {
            return BoundingBoxUtil.within2DHeight(base, check, this.bodyheightCrouching);
        }
        return BoundingBoxUtil.within2DHeight(base, check, this.bodyheight);
    }

    @Override
    public double maximumCheckingDistance(final Entity base) { return this.bodyWidthRadius * 2; }

}
