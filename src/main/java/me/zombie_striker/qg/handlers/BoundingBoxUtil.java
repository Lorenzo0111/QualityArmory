package me.zombie_striker.qg.handlers;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class BoundingBoxUtil {

	public static boolean within2D(Entity e, Location closest, double minDist, double height, double centerOffset) {
		boolean b1 = within2DHeight(e, closest, height);
		boolean b2 = within2DWidth(e, closest, minDist, centerOffset);
		return b1 && b2;
	}
	public static boolean within2D(Entity e, Location closest, double minDist, double height, double heightOffset, double centerOffset) {
		boolean b1 = within2DHeight(e, closest, height, heightOffset);
		boolean b2 = within2DWidth(e, closest, minDist, centerOffset);
		return b1 && b2;
	}

	public static boolean within2DWidth(Entity e, Location closest, double minDist, double centerOffset) {
		double xS = (e.getLocation().clone().add(e.getVelocity()).getX()) - (closest.getX());
		xS *= xS;
		double zS = (e.getLocation().clone().add(e.getVelocity()).getZ()) - (closest.getZ());
		zS *= zS;

		double distancesqr = xS + zS;
		return distancesqr <= (minDist*minDist);
	}

	public static boolean within2DHeight(Entity e, Location closest, double height) {
		return within2DHeight(e,closest,height,0);
	}
	public static boolean within2DHeight(Entity e, Location closest, double height, double offset) {
		double rel = closest.getY() - e.getLocation().getY();
		return rel >= offset && rel <= offset+height+e.getVelocity().getY();
	}
}
