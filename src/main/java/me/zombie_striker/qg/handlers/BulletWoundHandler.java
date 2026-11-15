package me.zombie_striker.qg.handlers;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import com.cryptomorin.xseries.XPotion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import me.zombie_striker.qg.utils.FoliaRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.zombie_striker.qg.QAMain;

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
			task = new FoliaRunnable() {
				@Override
				public void run() {
					for (Entry<UUID, Double> e : new HashMap<>(QAMain.getPlayerState().getBleedoutMultiplier()).entrySet()) {
						Player online = Bukkit.getPlayer(e.getKey());
						if (online != null) {
							final Player p = online;
							FoliaRunnable.runEntityTask(QAMain.getInstance(), p, () -> applyBleedEffect(p));
						}
					}
				}
			}.runTaskTimer(QAMain.getInstance(), 0, 20);
		}
	}

	private static void applyBleedEffect(Player online) {
		if (!QAMain.getPlayerState().getBleedoutMultiplier().containsKey(online.getUniqueId())) return;
		if (!QAMain.getPlayerState().getBloodLevel().containsKey(online.getUniqueId())) {
			// Wound state is gone (fully healed / cleared): drop the bleed-out instead of restarting from 0.
			QAMain.getPlayerState().getBleedoutMultiplier().remove(online.getUniqueId());
			return;
		}
		double bleedMult = QAMain.getPlayerState().getBleedoutMultiplier().get(online.getUniqueId());
		double bloodlevel = QAMain.getPlayerState().getBloodLevel().get(online.getUniqueId()) + bleedMult
				+ QAMain.getConfiguration().combat.bleeding.bloodIncreasePerSecond;

		if (bloodlevel / QAMain.getConfiguration().combat.bleeding.initialBloodLevel <= 0.75)
			online.removePotionEffect(XPotion.NAUSEA.getPotionEffectType());
			online.addPotionEffect(new PotionEffect(XPotion.NAUSEA.getPotionEffectType(), 499, 3));
		if (bloodlevel / QAMain.getConfiguration().combat.bleeding.initialBloodLevel <= 0.40)
			online.removePotionEffect(PotionEffectType.BLINDNESS);
			online.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 499, 1));
		if (bloodlevel / QAMain.getConfiguration().combat.bleeding.initialBloodLevel == 0.0)
			online.damage(1);
		if (bleedMult < 0)
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
