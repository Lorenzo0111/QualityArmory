package me.zombie_striker.qg.guns.chargers;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.guns.utils.WeaponSounds;

public class RequireAimCharger implements ChargingHandler {

    public RequireAimCharger() { ChargingManager.add(this); }

    @Override
    public boolean isCharging(final Player player) { return false; }

    @Override
    public boolean shoot(final Gun g, final Player player, final ItemStack stack) {
        if (QualityArmory.isIronSights(player.getInventory().getItemInMainHand())) {
            return true;
        }
        QualityArmory.addAmmoToInventory(player, g.getAmmoType(), 1);
        return false;
    }

    @Override
    public String getName() {

        return ChargingManager.REQUIREAIM;
    }

    @Override
    public String getDefaultChargingSound() {
        return WeaponSounds.SHOCKWAVE.getSoundName();
        // g.getChargingSound()
    }

}
