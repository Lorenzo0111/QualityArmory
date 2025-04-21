package me.zombie_striker.qg.config;

import java.io.File;

import me.zombie_striker.qg.guns.utils.WeaponType;

public class MiscYML extends ArmoryYML {

    @SuppressWarnings("unused")
    private WeaponType misctype;

    public MiscYML(final File file) { super(file); }

    public void setMiscType(final WeaponType misctype) {
        this.set("MiscType", misctype.name());
        this.misctype = misctype;
    }

    public void setThrowSpeed(final double speed) { this.set("ThrowSpeed", 1.5); }

    @Override
    public void verifyAllTagsExist() {
        super.verifyAllTagsExist();
        this.verify("ThrowSpeed", 1.5);
    }
}
