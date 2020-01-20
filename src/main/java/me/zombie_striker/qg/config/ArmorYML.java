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
		verify("Version_18_Support", !QAMain.isVersionHigherThan(1, 9));
		verify("minProtectionHeight", 1);
		verify("maxProtectionHeight", 2);
		verify("stopsHeadshots", false);
	}

	public ArmorYML setStopsHeadshots(boolean b) {
		set("stopsHeadshots", b);
		return this;
	}

	public ArmorYML setProtectionRegion(double min, double max) {
		set(false, "minProtectionHeight", min);
		set(false, "maxProtectionHeight", max);
		return this;
	}

}
