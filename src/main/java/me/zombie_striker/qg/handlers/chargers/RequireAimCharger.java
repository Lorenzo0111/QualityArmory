package me.zombie_striker.qg.handlers.chargers;

import me.zombie_striker.qg.handlers.IronsightsHandler;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.guns.Gun;

public class RequireAimCharger implements ChargingHandler {

	public RequireAimCharger() {
	ChargingManager.add(this);
}

	@Override
	public String getName() {
		return ChargingManager.REQUIREAIM;
	}

	@Override
	public boolean isCharging(Player player) {
		return false;
	}

	@Override
	public boolean isReadyToFire(Gun g, Player player, ItemStack stack) {
		if (IronsightsHandler.isAiming(player.getInventory().getItemInMainHand())) {
			return true;
		}
//		QualityArmory.addAmmoToInventory(player, g.getAmmoType(), 1);
		return false;
	}

	@Override
	public boolean useChargingShoot() {
		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void shoot(Gun g, final Player player, ItemStack stack) {

	}



}
