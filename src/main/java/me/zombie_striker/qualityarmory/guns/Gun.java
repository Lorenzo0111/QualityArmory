package me.zombie_striker.qualityarmory.guns;

import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.qualityarmory.ConfigKey;
import org.jetbrains.annotations.NotNull;

public class Gun extends CustomBaseObject implements Comparable<Gun> {

	public Gun(String name, MaterialStorage storage, String displayname, int maxBullets, String ammotype) {
		super(name, storage);
		this.setData(ConfigKey.CUSTOMITEM_DISPLAYNAME.getKey(), displayname);
		this.setData(ConfigKey.CUSTOMITEM_MAXBULLETS.getKey(), maxBullets);
		this.setData(ConfigKey.CUSTOMITEM_AMMOTYPE.getKey(), ammotype);
	}

    public Gun(String name, MaterialStorage currentMaterial) {
        super(name,currentMaterial);
    }

    @Override
	public int compareTo(@NotNull Gun o) {
		return this.getName().compareTo(o.getName());
	}
}
