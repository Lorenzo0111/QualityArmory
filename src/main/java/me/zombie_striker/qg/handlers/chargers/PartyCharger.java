package me.zombie_striker.qg.handlers.chargers;

import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.zombie_striker.qg.guns.Gun;

public class PartyCharger implements ChargingHandler {

	public PartyCharger() {
		ChargingManager.add(this);
	}

	@Override
	public String getName() {
		return ChargingManager.PARTY;
	}

	@Override
	public boolean isCharging(Player player) {
		return false;
		// represents if the code is currently active.
	}

	@Override
	public boolean isReadyToFire(Gun g, Player player, ItemStack stack) {
		return true;
	}

	@Override
	public boolean useChargingShoot() {
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void shoot(Gun g, final Player player, ItemStack stack) {
		player.playEffect(player.getEyeLocation(), Effect.FIREWORK_SHOOT, 0);
		// This return false represents if the code should use the default shooting
		// system. If set to true, QA will not use the default shooting system.
	}


}
