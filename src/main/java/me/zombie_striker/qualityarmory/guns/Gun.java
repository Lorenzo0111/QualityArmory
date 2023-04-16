package me.zombie_striker.qualityarmory.guns;

import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.qualityarmory.ConfigKey;
import org.jetbrains.annotations.NotNull;

public class Gun extends CustomBaseObject implements Comparable<Gun> {

	public Gun(String name, MaterialStorage storage, String displayname, int maxBullets) {
		super(name, storage);
		this.setData(ConfigKey.CUSTOMITEM_DISPLAYNAME.getKey(), displayname);
		this.setData(ConfigKey.CUSTOMITEM_MAXBULLETS.getKey(), maxBullets);
	}

	@Override
	public int compareTo(@NotNull Gun o) {
		return 0;
	}
}
