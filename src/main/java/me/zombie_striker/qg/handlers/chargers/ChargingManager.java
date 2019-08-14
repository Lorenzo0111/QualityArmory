package me.zombie_striker.qg.handlers.chargers;

import me.zombie_striker.qg.QAMain;

import java.util.HashMap;

public class ChargingManager {

	
	public static String BOLT = "BoltAction";
	public static String REVOLVER = ("Revolver");
	public static String PUMPACTION = ("PumpAction");
	public static String BREAKACTION = ("BreakAction");
	public static String BURSTFIRE = ("BurstFire");
	public static String DelayedBURSTFIRE = ("DelayedBurstFire");
	public static String PUSHBACK = "PushBackCharger";
	public static String REQUIREAIM = "RequireAimCharger";
	public static String PARTY = "PartyCharger";
	
	//DEPRECATED
	public static String LEGACY_RPG = ("SingleRPG");
	public static String LEGACY_MININUKELAUNCHER = ("MininukeLauncher");
	public static String LEGACY_HOMINGRPG = ("HomingRPG");
	

	public static HashMap<String, ChargingHandler> handlers = new HashMap<>();

	public static void add(ChargingHandler c) {
		handlers.put(c.getName(), c);
	}

	public static ChargingHandler getHandler(String name) {
		return handlers.get(name);
	}

	public static String getChargingLore(String name, String def) {
//		QAMain.DEBUG("Find Charger Lore: " + name + " def: " + def);
		if (handlers.containsKey(name)) {
			return (String) QAMain.m.a("Lore_Charger." + name, def);
		}
		return def;
	}
}
