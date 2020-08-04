package me.zombie_striker.qg.config;

import me.zombie_striker.qg.guns.utils.WeaponType;

import java.io.File;

public class MiscYML extends ArmoryYML{

	private WeaponType misctype;

	public MiscYML(File file) {
		super(file);
	}

	public void setMiscType(WeaponType misctype) {
		set("MiscType",misctype.name());
		this.misctype = misctype;
	}
	public void setThrowSpeed(double speed){
		set("ThrowSpeed",1.5);
	}

	@Override
	public void verifyAllTagsExist() {
		super.verifyAllTagsExist();
		verify("ThrowSpeed",1.5);
	}
}
