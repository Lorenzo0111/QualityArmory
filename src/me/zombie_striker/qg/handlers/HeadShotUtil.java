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
			return Math.abs(e.getLocation().getY()-shot.getY())<1;
		case "ENDERMAN":
		case "IRON_GOLEM":
			return Math.abs(e.getLocation().getY()-shot.getY()+2.9)<1;
		case "MAGMA_CUBE":
		case "SLIME":
		case "GUARDIAN":
		case "GHAST":
			return true;
		}
		return Math.abs(e.getLocation().getY()-shot.getY()+1.9)<1;
	}

	public static boolean closeEnough(Entity e, Location closest) {
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
			return closest.distance(e.getLocation().clone().add(0, 0.5, 0)) < 0.7;
		case "ENDERMAN":
		case "IRON_GOLEM":
			return closest.distance(e.getLocation().clone().add(0, 1.5, 0)) < 1.5;
		case "MAGMA_CUBE":
		case "SLIME":
		case "GUARDIAN":
			return closest.distance(e.getLocation().clone().add(0, 1.5, 0)) < 2;
		case "GHAST":
			return closest.distance(e.getLocation().clone().add(0, 1, 0)) < 3;
		}
		return closest.distance(e.getLocation().clone().add(0, 1, 0)) < 0.8;
	}
}
