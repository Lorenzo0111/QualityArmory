package me.zombie_striker.qg.guns.chargers;

import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.guns.utils.WeaponSounds;

public class PartyCharger implements ChargingHandler {

    public PartyCharger() { ChargingManager.add(this); }

    @Override
    public boolean isCharging(final Player player) {
        return false;
        // represents if the code is currently active.
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean shoot(final Gun g, final Player player, final ItemStack stack) {
        player.playEffect(player.getEyeLocation(), Effect.FIREWORK_SHOOT, 0);
        return false;
        // This return false represents if the code should use the default shooting
        // system. If set to true, QA will not use the default shooting system.
    }

    @Override
    public String getName() { return "PartyCharger"; }

    @Override
    public String getDefaultChargingSound() {
        return WeaponSounds.PARTY_SHOT.getSoundName();
        // g.getChargingSound()
    }
}
