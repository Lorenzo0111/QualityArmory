package me.zombie_striker.qg.handlers;

import org.bukkit.Location;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;

public class HeadShotUtil {

	public static boolean isHeadShot(Entity e, Location shot) {
		double modifier = 1;
		if (e instanceof Ageable) {
			modifier = ((Ageable) e).isAdult() ? 1 : 0.5;
		}
		switch (e.getType().name()) {

			case "CHICKEN":
			case "SILVERFISH":
			case "OCELOT":
			case "ENDERMITE":
			case "BAT":
			case "RABBIT":
			case "PARROT":
			case "CAVE_SPIDER":
			case "VEX":
				return shot.getY() - e.getLocation().getY() > 0.6 * modifier;
			case "PIG":
			case "WOLF":
			case "SQUID":
			case "SPIDER":
			case "FOX":
			case "CAT":
				return shot.getY() - e.getLocation().getY() > 0.3 * modifier;

			case "COW":
			case "SHEEP":
			case "MUSHROOM_COW":
			case "POLAR_BEAR":
				return shot.getY() - e.getLocation().getY() > 1.0 * modifier;

			case "ENDERMAN":
				return shot.getY() - e.getLocation().getY() > 2.2 * modifier;

			case "GIANT":
				return shot.getY() - e.getLocation().getY() > 12 * modifier;

			case "IRON_GOLEM":
			case "WITHER":
			case "HORSE":
			case "LLAMA":
			case "SKELETON_HORSE":
			case "MULE":
			case "DONKEY":
			case "ZOMBIE_HORSE":
			case "PANDA":
				return shot.getY() - e.getLocation().getY() > 2.0 * modifier;

			case "MAGMA_CUBE":
			case "SLIME":
			case "GUARDIAN":
			case "RAVAGER":
				return false;

			case "GHAST":
				return false;
		}
		return shot.getY() - e.getLocation().getY() > 1.3 * modifier;

		/*
		 * case "CHICKEN": case "PIG": case "SILVERFISH": case "ENDERMITE": case "BAT":
		 * case "WOLF": case "RABBIT": case "PARROT": case "SQUID": case "SPIDER": case
		 * "CAVE_SPIDER": return Math.abs(e.getLocation().getY() - shot.getY()) < 1;
		 * case "ENDERMAN": case "IRON_GOLEM": return Math.abs(e.getLocation().getY() -
		 * shot.getY() + 2.9) < 1; case "MAGMA_CUBE": case "SLIME": case "GUARDIAN":
		 * case "GHAST": return true; } return Math.abs(e.getLocation().getY() -
		 * shot.getY() + 1.9) < 1;
		 */
	}

	public static boolean closeEnough(Entity e, Location closest) {
		switch (e.getType().name()) {

			/**
			 *
			 * case "CHICKEN": case "PIG": case "SILVERFISH": case "ENDERMITE": case "BAT":
			 * case "WOLF": case "RABBIT": case "PARROT": case "SQUID": case "SPIDER": case
			 * "CAVE_SPIDER": return closest.distance(e.getLocation().clone().add(0, 0.5,
			 * 0)) < 0.7; case "ENDERMAN": case "IRON_GOLEM": return
			 * closest.distance(e.getLocation().clone().add(0, 1.5, 0)) < 1.5; case
			 * "MAGMA_CUBE": case "SLIME": case "GUARDIAN": return
			 * closest.distance(e.getLocation().clone().add(0, 1.5, 0)) < 2; case "GHAST":
			 * return closest.distance(e.getLocation().clone().add(0, 1, 0)) < 3;
			 */

			case "CHICKEN":
			case "SILVERFISH":
			case "OCELOT":
			case "ENDERMITE":
			case "BAT":
			case "RABBIT":
			case "PARROT":
			case "CAVE_SPIDER":
			case "VEX":
				return within2D(e, closest, 0.45, 1);
			case "PIG":
			case "WOLF":
			case "SQUID":
			case "SPIDER":
			case "CAT":
			case "FOX":
				return within2D(e, closest, 1, 1);

			case "COW":
			case "SHEEP":
			case "MUSHROOM_COW":
			case "POLAR_BEAR":
				return within2D(e, closest, 1, 1);

			case "PHANTOM":
				return within2D(e, closest, 1, 0.5);

			case "ENDERMAN":
				return within2D(e, closest, 0.5, 3);

			case "GIANT":
				return within2D(e, closest, 2, 16);

			case "IRON_GOLEM":
			case "WITHER":
			case "HORSE":
			case "LLAMA":
			case "SKELETON_HORSE":
			case "MULE":
			case "DONKEY":
			case "ZOMBIE_HORSE":
			case "PANDA":
				return within2D(e, closest, 1, 3);

			case "MAGMA_CUBE":
			case "SLIME":
			case "GUARDIAN":
			case "RAVAGER":
				return within2D(e, closest, 1, 2);

			case "GHAST":
				return within2D(e, closest, 3, 3);
		}
		// return closest.distance(e.getLocation().clone().add(0, 1, 0)) < 1;
		return within2D(e, closest, 0.5, 2);
	}

	public static boolean within2D(Entity e, Location closest, double minDist, double height) {
		boolean b1 = within2DHeight(e, closest, height);
		boolean b2 = within2DWidth(e, closest, minDist);
		return b1 && b2;
	}

	public static boolean within2DWidth(Entity e, Location closest, double minDist) {
		double xS = e.getLocation().getX() - closest.getX();
		xS *= xS;
		double zS = e.getLocation().getZ() - closest.getZ();
		zS *= zS;

		double distance = Math.sqrt(xS + zS);
		return distance < minDist;
	}

	public static boolean within2DHeight(Entity e, Location closest, double height) {
		double rel = closest.getY() - e.getLocation().getY();
		return rel >= 0 && rel <= height;
	}
}
