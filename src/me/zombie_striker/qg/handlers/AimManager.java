package me.zombie_striker.qg.handlers;

import java.util.HashMap;
import java.util.UUID;

import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.guns.Gun;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class AimManager implements Listener {

	public static HashMap<UUID, Double> accState = new HashMap<>();
	public static HashMap<UUID, Long> lasLocCheck = new HashMap<>();

	public static double getSway(Gun gun,UUID uuid) {
		if(!accState.containsKey(uuid))
			return gun.getSway()*gun.getMovementMultiplier();		
		return gun.getSway()*Math.pow(accState.get(uuid),gun.getMovementMultiplier());		
	}
	
	public AimManager() {
		new BukkitRunnable() {

			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers()) {
					double sway = 1;
					if (Main.isIS(p.getItemInHand()))
						sway /= 2;
					if (p.isSprinting())
						sway *= 1.3;
					if (lasLocCheck.containsKey(p.getUniqueId())) {
						long s = System.currentTimeMillis()
								- lasLocCheck.get(p.getUniqueId());
						if(s<=0)
							s=1;
						if (s < 800) {
							// less than 1.5 sec
							sway *= Math.min(1.3, 800 / s);
						}
					}
					accState.put(p.getUniqueId(), sway);
				}
			}
		}.runTaskTimer(Main.getInstance(), 5, 5);

	}

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if (e.getTo().getX() != e.getFrom().getX()
				|| e.getTo().getZ() != e.getFrom().getZ()) {
			lasLocCheck.put(e.getPlayer().getUniqueId(),
					System.currentTimeMillis());
		}
	}

}
