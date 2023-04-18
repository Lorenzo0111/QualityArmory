package me.zombie_striker.qualityarmory.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.zombie_striker.qualityarmory.ConfigKey;
import me.zombie_striker.qualityarmory.QAMain;
import me.zombie_striker.qualityarmory.api.QualityArmory;
import me.zombie_striker.qualityarmory.guns.Gun;
import me.zombie_striker.qualityarmory.interfaces.IHandler;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPIHook extends PlaceholderExpansion implements IHandler {

    private QAMain main;

    @Override
    public @NotNull String getIdentifier() {
        return "qualityarmory";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Lorenzo0111";
    }

    @Override
    public @NotNull String getVersion() {
        return main.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        Gun gun = QualityArmory.getInstance().getGunInHand(player);
        if (gun == null) return "None";

        switch (params.toLowerCase()) {
            case "ammo_type":
                return (String) gun.getData(ConfigKey.CUSTOMITEM_AMMOTYPE.getKey());
            case "ammo_amount":
                return String.valueOf(QualityArmory.getInstance().getAmmoInInventory(player,(String) gun.getData(ConfigKey.CUSTOMITEM_AMMOTYPE.getKey())));
        }
        return null;
    }

    @Override
    public void init(QAMain main) {
        this.main = main;
        register();
    }
}
