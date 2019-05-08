package me.zombie_striker.qg.config;

import java.io.File;

import me.zombie_striker.qg.QAMain;

public class ArmorYML extends ArmoryYML {

	public ArmorYML(File file) {
		super(file);
	}@Override
	public void verifyAllTagsExist() {
		if(overrideVerify)
			return;
		super.verifyAllTagsExist();
		setNoOverride("Version_18_Support", !QAMain.isVersionHigherThan(1, 9));
		setNoOverride("minProtectionHeight", 1);
		setNoOverride("maxProtectionHeight", 2);
		setNoOverride("stopsHeadshots", false);
	}
	public ArmorYML setStopsHeadshots(boolean b) {
		setNoOverride("stopsHeadshots", b);
		return this;		
	}
	public ArmorYML setProtectionRegion(double min, double max) {
		set(false, "minProtectionHeight", min);
		set(false, "maxProtectionHeight", max);
		return this;
	}

}
