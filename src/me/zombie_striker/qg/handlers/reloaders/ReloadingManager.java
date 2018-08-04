package me.zombie_striker.qg.handlers.reloaders;

import java.util.HashMap;

public class ReloadingManager {

	
	public static String SINGLERELOAD = ("SingleBulletReload");
	public static String PUMPACTIONRELOAD = ("PumpActionReload");

	public static HashMap<String, ReloadingHandler> handlers = new HashMap<>();

	public static void add(ReloadingHandler c) {
		handlers.put(c.getName(), c);
	}

	public static ReloadingHandler getHandler(String name) {
		return handlers.get(name);
	}
}
