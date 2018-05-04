package me.zombie_striker.qg.armor.angles;

import me.zombie_striker.qg.MaterialStorage;

public class AngledArmor {

	private MaterialStorage ms;
	private MaterialStorage base;
	private float angle = 90;
	
	public AngledArmor(MaterialStorage t, MaterialStorage base, float angle) {
		this.ms = t;
		this.base = base;
		this.angle = angle;
	}
	
	public MaterialStorage getBase() {
		return base;
	}
	public MaterialStorage getMaterial() {
		return ms;
	}
	public float getAngle() {
		return angle;
	}
}
