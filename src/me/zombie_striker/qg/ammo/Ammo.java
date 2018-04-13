package me.zombie_striker.qg.ammo;

import me.zombie_striker.qg.ArmoryBaseObject;


public interface Ammo extends ArmoryBaseObject{
	public int getMaxAmount();
	public boolean individualDrop();
	public double getPiercingDamage();
	

	public boolean isSkull();
	public String getSkullOwner();
	public void setSkullOwner(String s);
}
