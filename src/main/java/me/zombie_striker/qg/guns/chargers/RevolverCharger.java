package me.zombie_striker.qg.guns.chargers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.zombie_striker.qg.api.QualityArmory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.guns.utils.WeaponSounds;

public class RevolverCharger implements ChargingHandler {

	List<UUID> timeC = new ArrayList<>();
public RevolverCharger() {
	ChargingManager.add(this);
}
	@Override
	public boolean isCharging(Player player) {
		return timeC.contains(player.getUniqueId());
	}

	@Override
	public boolean shoot(Gun g, final Player player, ItemStack stack) {
		timeC.add(player.getUniqueId());
		new BukkitRunnable() {
			@Override
			public void run() {try {
				player.getWorld().playSound(player.getLocation(), g.getChargingSound(), 1,
						0.75f);

			}catch(Error|Exception e43) {}
			}
		}.runTaskLater(QAMain.getInstance(), 10);
		new BukkitRunnable() {
			
			@Override
			public void run() {
				timeC.remove(player.getUniqueId());
			}
		}.runTaskLater(QAMain.getInstance(), 15);
		return true;
	}

	@Override
	public String getName() {
		return ChargingManager.REVOLVER;
	}
	@Override
	public String getDefaultChargingSound() {
		return WeaponSounds.RELOAD_BULLET.getSoundName();
		//g.getChargingSound()
	}

}
