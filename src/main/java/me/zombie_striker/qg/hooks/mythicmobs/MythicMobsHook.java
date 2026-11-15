package me.zombie_striker.qg.hooks.mythicmobs;

import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Adds the QualityArmory mechanics to MythicMobs:
 * <ul>
 * <li>qashoot{gun=name;sway=1.0} shoots a gun at the target</li>
 * <li>qaequip{item=name;slot=HAND} equips a QualityArmory item to the mob</li>
 * </ul>
 */
public class MythicMobsHook implements Listener {

	@EventHandler
	public void onMechanicLoad(MythicMechanicLoadEvent event) {
		String name = event.getMechanicName();
		if (name.equalsIgnoreCase("qashoot") || name.equalsIgnoreCase("qualityarmoryshoot")) {
			event.register(new ShootMechanic(event.getConfig()));
		} else if (name.equalsIgnoreCase("qaequip") || name.equalsIgnoreCase("qualityarmoryequip")) {
			event.register(new EquipMechanic(event.getConfig()));
		}
	}
}
