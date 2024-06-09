package me.zombie_striker.qg.handlers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.cryptomorin.xseries.reflection.XReflection;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.guns.Gun;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class AimManager extends BukkitRunnable implements Listener {
	private static final Map<UUID, Double> SWAYS = new HashMap<>();
	private static final Map<UUID, Long> LAST_MOVEMENT = new HashMap<>();

	public static double getSway(Gun gun,UUID uuid) {
		if(!SWAYS.containsKey(uuid))
			return gun.getSway()*gun.getMovementMultiplier();
		return gun.getSway()*Math.pow(SWAYS.get(uuid),gun.getMovementMultiplier());
	}
	
	public AimManager() {
		this.runTaskTimerAsynchronously(QAMain.getInstance(), 10, 10);
	}

	@Override
	public void run() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			double sway = 1;
			Gun g = QualityArmory.getGunInHand(p);
			if (g!=null) {
				if (p.isSneaking() && g.isEnableSwaySneakModifier())
					sway *= QAMain.swayModifier_Sneak;
				if (p.isSprinting() && g.isEnableSwayRunModifier()) {
					sway *= QAMain.swayModifier_Run;
				}
				if (XReflection.supports(9) && !QualityArmory.isIronSights(p.getInventory().getItemInMainHand())) {
					sway *= g.getSwayUnscopedMultiplier();
				}
			}

			if (LAST_MOVEMENT.containsKey(p.getUniqueId())) {
				long s = System.currentTimeMillis()
						- LAST_MOVEMENT.get(p.getUniqueId());
				if (s <= 0)
					s = 1;
				if (s < 800) {
					// less than 1.5 sec
					if (g==null || g.isEnableSwayMovementModifier())
						sway *= Math.min(QAMain.swayModifier_Walk, 800 / s);
				}
			}

			SWAYS.put(p.getUniqueId(), sway);
		}
	}

	@EventHandler
	public void onMove(@NotNull PlayerMoveEvent e) {
		if (e.getTo() != null && e.getTo().getX() != e.getFrom().getX()
				|| e.getTo().getZ() != e.getFrom().getZ()) {
			LAST_MOVEMENT.put(e.getPlayer().getUniqueId(),
					System.currentTimeMillis());
		}
	}

	@EventHandler
	public void onQuit(@NotNull PlayerQuitEvent e) {
		LAST_MOVEMENT.remove(e.getPlayer().getUniqueId());
		SWAYS.remove(e.getPlayer().getUniqueId());
	}
}
