package me.zombie_striker.customitemmanager;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ArmoryBaseObject {

    ItemStack getItemStack();

    boolean is18Support();

    void set18Supported(boolean b);

    boolean onRMB(Player shooter, ItemStack usedItem);

    boolean onShift(Player shooter, ItemStack usedItem, boolean toggle);

    boolean onLMB(Player shooter, ItemStack usedItem);

    boolean onSwapTo(Player shooter, ItemStack usedItem);

    boolean onSwapAway(Player shooter, ItemStack usedItem);

}
