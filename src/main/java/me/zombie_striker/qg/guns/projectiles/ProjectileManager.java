package me.zombie_striker.qg.guns.projectiles;

import java.util.HashMap;

public class ProjectileManager {

    public static String EXPLODINGROUND = "ExplodingRound";
    public static String RPG = "RPG";
    public static String HOMING_RPG = "HomingRPG";
    public static String MINI_NUKE = "MiniNuke";
    public static String FIRE = "FlameThrower";

    public static HashMap<String, RealtimeCalculationProjectile> handlers = new HashMap<>();

    public static void add(final RealtimeCalculationProjectile c) { ProjectileManager.handlers.put(c.getName(), c); }

    public static RealtimeCalculationProjectile getHandler(final String name) { return ProjectileManager.handlers.get(name); }
}
