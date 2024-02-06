package me.zombie_striker.qg.handlers;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

/**
 * This class is used to hold an inventory for a QAInventory
 * This is just a temporary fix while I work on the rewrite
 */
public class QAInventoryHolder implements InventoryHolder {
    private final Inventory inventory;

    public QAInventoryHolder(String title) {
        this.inventory = Bukkit.createInventory(this, 9 * 6, title);
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }

}
