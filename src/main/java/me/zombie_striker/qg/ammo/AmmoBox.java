package me.zombie_striker.qg.ammo;

import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.miscitems.AmmoBag;
import me.zombie_striker.qg.utils.LocalUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AmmoBox extends AmmoBag {
    private final int maxAmmoCount;
    private final Ammo ammoType;

    public AmmoBox(MaterialStorage ms, String name, String displayname, ItemStack[] ings, int cost, int max, String ammoType) {
        super(ms, name, displayname, ings, max, cost);
        this.maxAmmoCount = max;
        this.ammoType = QualityArmory.getAmmoByName(ammoType);
        if (this.ammoType == null)
            throw new IllegalArgumentException("Invalid ammo type: " + ammoType);
    }

    @Override
    public boolean onRMB(Player shooter, ItemStack usedItem) {
        return false;
    }

    @Override
    public boolean onLMB(Player shooter, ItemStack usedItem) {
        return false;
    }

    @Override
    public void updateAmmoLore(@NotNull ItemStack item, int newAmmo) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        meta.setDisplayName(ammoType.getDisplayName() +
                ChatColor.GREEN + " " + newAmmo + ChatColor.GRAY + " | " + ChatColor.RED + maxAmmoCount
        );

        item.setItemMeta(meta);
    }

    @Override
    public int getAmmo(@NotNull ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return 0;

        String displayName = ChatColor.stripColor(meta.getDisplayName());
        String[] split = displayName.split(" ");

        return Integer.parseInt(split[1]);
    }

    @Override
    public @Nullable Ammo getAmmoType(@NotNull ItemStack item) {
        return ammoType;
    }

    public int getMaxAmmoCount() {
        return maxAmmoCount;
    }
}