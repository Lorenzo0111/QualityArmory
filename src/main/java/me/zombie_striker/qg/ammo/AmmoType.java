package me.zombie_striker.qg.ammo;

import java.util.HashMap;
import java.util.Set;

public class AmmoType {

    private static HashMap<String, Ammo> ammoHolder = new HashMap<>();

    public static void addAmmo(final Ammo ammo, final String key) { AmmoType.ammoHolder.put(key, ammo); }

    public static Ammo getAmmo(final String key) { return AmmoType.ammoHolder.get(key); }

    public static Set<String> getKeys() { return AmmoType.ammoHolder.keySet(); }
}
