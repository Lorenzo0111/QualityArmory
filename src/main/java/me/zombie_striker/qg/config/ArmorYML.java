package me.zombie_striker.qg.config;

import java.io.File;

import me.zombie_striker.qg.QAMain;

public class ArmorYML extends ArmoryYML {

	public ArmorYML(File file) {
		super(file);
	}

	@Override
	public void verifyAllTagsExist() {
		super.verifyAllTagsExist();
		verify("version-18-support", !QAMain.isVersionHigherThan(1, 9));
		verify("min-protection-height", 1);
		verify("max-protection-height", 2);
		verify("stops-headshots", false);
		verify("protection", 1);
	}

	public ArmorYML setStopsHeadshots(boolean b) {
		set("stops-headshots", b);
		return this;
	}

	public ArmorYML setProtectionRegion(double min, double max) {
		set(false, "min-protection-height", min);
		set(false, "max-protection-height", max);
		return this;
	}

	public ArmorYML setProtection(int protection) {
		set("protection", protection);
		return this;
	}

}
