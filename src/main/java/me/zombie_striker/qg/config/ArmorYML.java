package me.zombie_striker.qg.config;

import java.io.File;

import me.zombie_striker.qg.QAMain;

public class ArmorYML extends ArmoryYML {

    public ArmorYML(final File file) { super(file); }

    @Override
    public void verifyAllTagsExist() {
        super.verifyAllTagsExist();
        this.verify("Version_18_Support", !QAMain.isVersionHigherThan(1, 9));
        this.verify("minProtectionHeight", 1);
        this.verify("maxProtectionHeight", 2);
        this.verify("stopsHeadshots", false);
        this.verify("protection", 1);
    }

    public ArmorYML setStopsHeadshots(final boolean b) {
        this.set("stopsHeadshots", b);
        return this;
    }

    public ArmorYML setProtectionRegion(final double min, final double max) {
        this.set(false, "minProtectionHeight", min);
        this.set(false, "maxProtectionHeight", max);
        return this;
    }

    public ArmorYML setProtection(final int protection) {
        this.set("protection", protection);
        return this;
    }

}
