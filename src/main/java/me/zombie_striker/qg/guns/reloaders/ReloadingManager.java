package me.zombie_striker.qg.guns.reloaders;

import java.util.HashMap;

public class ReloadingManager {

    public static final String SINGLE_RELOAD = ("SingleBulletReload");
    public static final String PUMP_ACTION_RELOAD = ("PumpActionReload");
    public static final String SLIDE_RELOAD = ("SlideReload");
    public static final String M1GARAND_RELOAD = ("M1GarandReload");

    public static final HashMap<String, ReloadingHandler> handlers = new HashMap<>();

    public static void add(final ReloadingHandler c) { ReloadingManager.handlers.put(c.getName(), c); }

    public static ReloadingHandler getHandler(final String name) { return ReloadingManager.handlers.get(name); }
}
