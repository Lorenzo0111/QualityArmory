package me.zombie_striker.qg.guns.chargers;

import java.util.HashMap;

public class ChargingManager {

	
	public static final String BOLT = "BoltAction";
	public static final String REVOLVER = ("Revolver");
	public static final String PUMPACTION = ("PumpAction");
	public static final String BREAKACTION = ("BreakAction");
	public static final String BURSTFIRE = ("BurstFire");
	public static final String DelayedBURSTFIRE = ("DelayedBurstFire");
	public static final String PUSHBACK = "PushBackCharger";
	public static final String REQUIREAIM = "RequireAimCharger";
	
	//DEPRECATED
	public static final String LEGACY_RPG = ("SingleRPG");
	public static final String LEGACY_MININUKELAUNCHER = ("MininukeLauncher");
	public static final String LEGACY_HOMINGRPG = ("HomingRPG");
	

	public static final HashMap<String, ChargingHandler> handlers = new HashMap<>();

	public static void add(ChargingHandler c) {
		handlers.put(c.getName(), c);
	}

	public static ChargingHandler getHandler(String name) {
		return handlers.get(name);
	}
}
