package me.zombie_striker.qualityarmory.guns;

import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.qualityarmory.ConfigKey;
import org.jetbrains.annotations.NotNull;

public class Ammo extends CustomBaseObject implements Comparable<Ammo> {

    public Ammo(String name, MaterialStorage storage, String displayname, String ammotype) {
        super(name, storage);
        this.setData(ConfigKey.CUSTOMITEM_DISPLAYNAME.getKey(), displayname);
        this.setData(ConfigKey.CUSTOMITEM_AMMOTYPE.getKey(), ammotype);
    }

    @Override
    public int compareTo(@NotNull Ammo o) {
        return getName().compareTo(o.getName());
    }
}
