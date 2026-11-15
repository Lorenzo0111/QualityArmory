package me.zombie_striker.qg.guns.projectiles;

import java.util.HashMap;

public class ProjectileManager {

	public static final String EXPLODINGROUND = "ExplodingRound";
	public static final String RPG = "RPG";
	public static final String HOMING_RPG = "HomingRPG";
	public static final String MINI_NUKE = "MiniNuke";
	public static final String FIRE = "FlameThrower";
	
	public static final HashMap<String, RealtimeCalculationProjectile> handlers = new HashMap<>();

	public static void add(RealtimeCalculationProjectile c) {
		handlers.put(c.getName(), c);
	}

	public static RealtimeCalculationProjectile getHandler(String name) {
		return handlers.get(name);
	}
}
