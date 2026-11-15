package me.zombie_striker.qg.handlers;

import com.cryptomorin.xseries.XPotion;
import me.zombie_striker.qg.QAMain;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map.Entry;
import java.util.UUID;

public class BulletWoundHandler {


	public static BukkitTask task = null;

	public static void bulletHit(Player player, double bulletSeverity) {
		if (QAMain.getConfiguration().combat.bleeding.enabled) {
			double current = QAMain.getPlayerState().getBleedoutMultiplier().containsKey(player.getUniqueId())
					? QAMain.getPlayerState().getBleedoutMultiplier().get(player.getUniqueId())
					: 0;

			player.sendMessage(QAMain.getMessages().bleeding.start);

			current -= bulletSeverity;
			QAMain.getPlayerState().getBleedoutMultiplier().put(player.getUniqueId(), current);
			if (!QAMain.getPlayerState().getBloodLevel().containsKey(player.getUniqueId())) {
				QAMain.getPlayerState().getBloodLevel().put(player.getUniqueId(), QAMain.getConfiguration().combat.bleeding.initialBloodLevel);
			}
		}
	}

	public static void startTimer() {
		// Reloads call this again, so stop the previous timer first
		if (task != null) {
			task.cancel();
			task = null;
		}
		if (QAMain.getConfiguration().combat.bleeding.enabled) {
			task = new BukkitRunnable() {
				@Override
				public void run() {
					for (Entry<UUID, Double> e : QAMain.getPlayerState().getBleedoutMultiplier().entrySet()) {
						Player online = Bukkit.getPlayer(e.getKey());
						if (online != null) {
							if (!QAMain.getPlayerState().getBloodLevel().containsKey(online.getUniqueId()))
								QAMain.getPlayerState().getBloodLevel().put(online.getUniqueId(), 0.0);
							double bloodlevel = QAMain.getPlayerState().getBloodLevel().get(online.getUniqueId()) + e.getValue()
									+ QAMain.getConfiguration().combat.bleeding.bloodIncreasePerSecond;

							if (bloodlevel / QAMain.getConfiguration().combat.bleeding.initialBloodLevel <= 0.75)

								online.removePotionEffect(XPotion.NAUSEA.getPotionEffectType());
								online.addPotionEffect(new PotionEffect(XPotion.NAUSEA.getPotionEffectType(), 499, 3));
							if (bloodlevel / QAMain.getConfiguration().combat.bleeding.initialBloodLevel <= 0.40)
								online.removePotionEffect(PotionEffectType.BLINDNESS);
								online.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 499, 1));
							double initialBlood = QAMain.getConfiguration().combat.bleeding.initialBloodLevel;
							double bloodRatio = initialBlood > 0 ? bloodlevel / initialBlood : bloodlevel;
							if (bloodRatio <= 1e-9)
								online.damage(1);
							if (QAMain.getPlayerState().getBleedoutMultiplier().get(online.getUniqueId()) < 0)
								// online.getWorld().spawnParticle(Particle.REDSTONE,
								// online.getLocation().getX(), online.getLocation().getY()+1.5,
								// online.getLocation().getZ(), 0, 1.0, 0.0, 0.0, 1);
								for (int i = 0; i < 5; i++) {
									double x = Math.random() - 0.5;
									double z = Math.random() - 0.5;
									double yofset = Math.random();
									ParticleHandlers.spawnParticle(1, 0, 0,
											online.getLocation().add(x, 0.8 + yofset, z));
								}

							if (bloodlevel >= QAMain.getConfiguration().combat.bleeding.initialBloodLevel)
								QAMain.getPlayerState().getBloodLevel().remove(online.getUniqueId());
							else
								QAMain.getPlayerState().getBloodLevel().put(online.getUniqueId(), bloodlevel);
						}
					}
				}
			}.runTaskTimer(QAMain.getInstance(), 0, 20);
		}
	}

}
