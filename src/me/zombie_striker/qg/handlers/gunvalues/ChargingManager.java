package me.zombie_striker.qg.handlers.gunvalues;

import java.util.HashMap;

public class ChargingManager {

	
	public static String BOLT = "BoltAction";
	public static String REVOLVER = ("Revolver");
	public static String PUMPACTION = ("PumpAction");
	public static String BREAKACTION = ("BreakAction");
	public static String RPG = ("SingleRPG");
	public static String RAPIDFIRE = ("RapidFireMode");
	public static String MININUKELAUNCHER = ("MininukeLauncher");
	public static String HOMINGRPG = ("HomingRPG");

	public static HashMap<String, ChargingHandler> handlers = new HashMap<>();

	public static void add(ChargingHandler c) {
		handlers.put(c.getName(), c);
	}

	public static ChargingHandler getHandler(String name) {
		return handlers.get(name);
	}
}
