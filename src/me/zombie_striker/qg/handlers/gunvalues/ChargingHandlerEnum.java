package me.zombie_striker.qg.handlers.gunvalues;

public enum ChargingHandlerEnum {

	BOLTACTION("BoltAction"), REVOLVER("Revolver"), PUMPACTION("PumpAction"), BREAKACTION("BreakAction"), RPG(
			"SingleRPG"), RAPIDFIRE("RapidFireMode"), RPG_ARC("SingleRPG_ARC");
	private String name;

	private ChargingHandlerEnum(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public ChargingHandler getHandler() {
		switch (this) {
		case BOLTACTION:
			return new BoltactionCharger();
		case REVOLVER:
			return new PistolCharger();
		case PUMPACTION:
			return new PumpactionCharger();
		case BREAKACTION:
			return new BreakactionCharger();
		case RPG:
			return new RPGCharger();
		case RPG_ARC:
			RPGCharger r = new RPGCharger();
			r.enableArc = true;
			return r;
		case RAPIDFIRE:
			return new RapidFireCharger();
		}
		return null;
	}

	public static ChargingHandlerEnum getEnumV(ChargingHandler en) {
		if (en instanceof BoltactionCharger)
			return ChargingHandlerEnum.BOLTACTION;
		if (en instanceof PistolCharger)
			return ChargingHandlerEnum.REVOLVER;
		if (en instanceof PumpactionCharger)
			return ChargingHandlerEnum.PUMPACTION;
		if (en instanceof BreakactionCharger)
			return ChargingHandlerEnum.BREAKACTION;
		if (en instanceof RPGCharger)
			if (((RPGCharger) en).enableArc) {
				return ChargingHandlerEnum.RPG_ARC;
			} else {
				return ChargingHandlerEnum.RPG;
			}
		if (en instanceof RapidFireCharger)
			return ChargingHandlerEnum.RAPIDFIRE;
		return null;
	}

	public static ChargingHandlerEnum getEnumV(String en) {
		for (ChargingHandlerEnum ch : ChargingHandlerEnum.values()) {
			if (ch.getName().equalsIgnoreCase(en))
				return ch;
		}
		return null;
	}
}
