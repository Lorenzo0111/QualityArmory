package me.zombie_striker.qg.handlers;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class HeadShotUtil {

	public static boolean isHeadShot(Entity e, Location shot) {
		switch (e.getType().name()) {
		case "CHICKEN":
		case "PIG":
		case "SILVERFISH":
		case "ENDERMITE":
		case "BAT":
		case "WOLF":
		case "RABBIT":
		case "PARROT":
		case "SQUID":
		case "SPIDER":
		case "CAVE_SPIDER":
			return e.getLocation().distance(shot) < e.getLocation().clone()
					.add(0, 1, 0).distance(shot);
		case "ENDERMAN":
		case "IRON_GOLEM":
			return e.getLocation().distance(shot) < e.getLocation().clone()
					.add(0, 4, 0).distance(shot);
		case "MAGMA_CUBE":
		case "SLIME":
		case "GUARDIAN":
		case "GHAST":
			return true;
		}
		return e.getLocation().distance(shot) < e.getLocation().clone()
				.add(0, 2, 0).distance(shot);
	}

	public static boolean nearestDistance(Entity e, Location closest) {
		switch (e.getType().name()) {
		case "CHICKEN":
		case "PIG":
		case "SILVERFISH":
		case "ENDERMITE":
		case "BAT":
		case "WOLF":
		case "RABBIT":
		case "PARROT":
		case "SQUID":
		case "SPIDER":
		case "CAVE_SPIDER":
			return closest.distance(e.getLocation().clone().add(0, 0.5, 0)) < 1;
		case "ENDERMAN":
		case "IRON_GOLEM":
			return closest.distance(e.getLocation().clone().add(0, 1.5, 0)) < 2;
		case "MAGMA_CUBE":
		case "SLIME":
		case "GUARDIAN":
			return closest.distance(e.getLocation().clone().add(0, 1.5, 0)) < 2;
		case "GHAST":
			return closest.distance(e.getLocation().clone().add(0, 1, 0)) < 3;
		}
		return closest.distance(e.getLocation().clone().add(0, 1, 0)) < 1.5;
	}
}
