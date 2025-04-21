package me.zombie_striker.qg.hooks;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.guns.Gun;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() { return "qualityarmory"; }

    @Override
    public @NotNull String getAuthor() { return "Lorenzo0111"; }

    @Override
    public @NotNull String getVersion() { return QAMain.getInstance().getDescription().getVersion(); }

    @Override
    public boolean persist() { return true; }

    @Override
    public String onPlaceholderRequest(final Player player, final String params) {
        final Gun gun = QualityArmory.getGunInHand(player);
        if (gun == null)
            return "None";

        switch (params.toLowerCase()) {
        case "ammo_type":
            return gun.getAmmoType().getDisplayName();
        case "ammo_amount":
            return String.valueOf(QualityArmory.getAmmoInInventory(player, gun.getAmmoType()));
        }

        return null;
    }

}
