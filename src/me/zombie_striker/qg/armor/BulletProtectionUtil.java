package me.zombie_striker.qg.armor;

import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.MaterialStorage;

public class BulletProtectionUtil {

	public static boolean stoppedBullet(Player p, Location bullet, Vector velocity) {
		if (p.getInventory().getChestplate() != null) {
			for (Entry<MaterialStorage, ArmorObject> entry : Main.armorRegister.entrySet()) {
				if (entry.getKey().getMat() == p.getInventory().getChestplate().getType()
						&& entry.getKey().getData() == p.getInventory().getChestplate().getDurability()) {
					double offset = (p.isSneaking() ? entry.getValue().getShifitngHeightOffset() : 0);
					if (bullet.getY() - p.getLocation().getY() > entry.getValue().getMinH() + offset) {
						if (bullet.getY() - p.getLocation().getY() < entry.getValue().getMaxH() + offset) {
							// Within kevlar range
							return true;
						} else {
							if (velocity.getY() < 0) {
								Vector fourth = velocity.normalize().multiply(0.25);
								if (bullet.clone().add(fourth).getY() < entry.getValue().getMaxH() + offset) {
									// Bullet close enough to vest
									return true;
								}
							} else {
								return false;
							}
						}
					} else {
						if (velocity.getY() > 0) {

							Vector fourth = velocity.normalize().multiply(0.25);
							if (bullet.clone().add(fourth).getY() > entry.getValue().getMinH() + offset) {
								// Bullet close enough to vest
								return true;
							}
						} else {
							return false;
						}
					}
					break;
				}
			}
		}

		return false;
	}
}
