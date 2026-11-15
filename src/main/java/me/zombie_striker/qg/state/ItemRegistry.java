package me.zombie_striker.qg.state;

import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.qg.ammo.Ammo;
import me.zombie_striker.qg.armor.ArmorObject;
import me.zombie_striker.qg.guns.Gun;

import java.util.*;

/**
 * Every custom item loaded from the item files, keyed by the material and data that identify it.
 * <p>
 * The maps are created once and only cleared on reload, so references to them stay valid.
 */
public final class ItemRegistry {
    private final HashMap<MaterialStorage, Gun> guns = new LinkedHashMap<>();
    private final HashMap<MaterialStorage, Ammo> ammo = new LinkedHashMap<>();
    private final HashMap<MaterialStorage, CustomBaseObject> misc = new LinkedHashMap<>();
    private final HashMap<MaterialStorage, ArmorObject> armor = new LinkedHashMap<>();
    private final ArrayList<MaterialStorage> expansionPacks = new ArrayList<>();
    private final Map<String, String> craftingNames = new java.util.concurrent.ConcurrentHashMap<>();

    public HashMap<MaterialStorage, Gun> getGuns() {
        return guns;
    }

    public HashMap<MaterialStorage, Ammo> getAmmo() {
        return ammo;
    }

    public HashMap<MaterialStorage, CustomBaseObject> getMisc() {
        return misc;
    }

    public HashMap<MaterialStorage, ArmorObject> getArmor() {
        return armor;
    }

    /**
     * @return Item slots reserved by expansion packs, which QualityArmory must not use for its own items.
     */
    public ArrayList<MaterialStorage> getExpansionPacks() {
        return expansionPacks;
    }

    /**
     * Reserves an item slot for an expansion pack. Adding the same slot twice has no effect.
     */
    public void addExpansionPack(MaterialStorage storage) {
        if (!expansionPacks.contains(storage))
            expansionPacks.add(storage);
    }

    /**
     * @return Translated display names of crafting ingredients, keyed by material name.
     */
    public Map<String, String> getCraftingNames() {
        return craftingNames;
    }

    /**
     * Removes all loaded items before a reload. Expansion pack slots are kept, since plugins register them once.
     */
    public void clearItems() {
        guns.clear();
        ammo.clear();
        misc.clear();
        armor.clear();
        craftingNames.clear();
    }
}
