package me.zombie_striker.qg.handlers;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class BoundingBoxUtil {

    public static boolean within2D(final Entity e, final Location closest, final double minDist, final double height,
            final double centerOffset) {
        final boolean b1 = BoundingBoxUtil.within2DHeight(e, closest, height);
        final boolean b2 = BoundingBoxUtil.within2DWidth(e, closest, minDist, centerOffset);
        return b1 && b2;
    }

    public static boolean within2D(final Entity e, final Location closest, final double minDist, final double height,
            final double heightOffset, final double centerOffset) {
        final boolean b1 = BoundingBoxUtil.within2DHeight(e, closest, height, heightOffset);
        final boolean b2 = BoundingBoxUtil.within2DWidth(e, closest, minDist, centerOffset);
        return b1 && b2;
    }

    public static boolean within2DWidth(final Entity e, final Location closest, final double minDist, final double centerOffset) {
        double xS = (e.getLocation().clone().add(e.getVelocity()).getX()) - (closest.getX());
        xS *= xS;
        double zS = (e.getLocation().clone().add(e.getVelocity()).getZ()) - (closest.getZ());
        zS *= zS;

        final double distancesqr = xS + zS;
        return distancesqr <= (minDist * minDist);
    }

    public static boolean within2DHeight(final Entity e, final Location closest, final double height) {
        return BoundingBoxUtil.within2DHeight(e, closest, height, 0);
    }

    public static boolean within2DHeight(final Entity e, final Location closest, final double height, final double offset) {
        final double rel = closest.getY() - e.getLocation().getY();
        return rel >= offset && rel <= offset + height + e.getVelocity().getY();
    }
}
