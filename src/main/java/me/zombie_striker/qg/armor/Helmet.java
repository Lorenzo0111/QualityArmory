package me.zombie_striker.qg.armor;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import me.zombie_striker.customitemmanager.MaterialStorage;

public class Helmet extends ArmorObject {

    public Helmet(final String name, final String displayname, final List<String> lore, final ItemStack[] ing, final MaterialStorage st,
            final double cost) {
        this(name, displayname, lore, ing, st, cost, cost > 0);
    }

    public Helmet(final String name, final String displayname, final List<String> lore, final ItemStack[] ing, final MaterialStorage st,
            final double cost, final boolean allowInShop) {
        super(name, displayname, lore, ing, st, cost);
        this.setEnableShop(allowInShop);
    }

}
