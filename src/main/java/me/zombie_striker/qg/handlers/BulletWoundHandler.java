package me.zombie_striker.qg.handlers;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import com.cryptomorin.xseries.XPotion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import me.zombie_striker.qg.util.FoliaRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.zombie_striker.qg.QAMain;

public class BulletWoundHandler {

	public static HashMap<UUID, Double> bleedoutMultiplier = new HashMap<>();
	public static HashMap<UUID, Double> bloodLevel = new HashMap<>();

	public static BukkitTask task = null;

	public static void bulletHit(Player player, double bulletSeverity) {
		if (QAMain.enableBleeding) {
			double current = bleedoutMultiplier.containsKey(player.getUniqueId())
					? bleedoutMultiplier.get(player.getUniqueId())
					: 0;

			player.sendMessage(QAMain.S_BLEEDOUT_STARTBLEEDING);

			current -= bulletSeverity;
			bleedoutMultiplier.put(player.getUniqueId(), current);
			if (!bloodLevel.containsKey(player.getUniqueId())) {
				bloodLevel.put(player.getUniqueId(), QAMain.bulletWound_initialbloodamount);
			}
		}
	}

	public static void startTimer() {
		if (QAMain.enableBleeding) {
			task = new FoliaRunnable() {
				@Override
				public void run() {
					for (Entry<UUID, Double> e : new HashMap<>(bleedoutMultiplier).entrySet()) {
						Player online = Bukkit.getPlayer(e.getKey());
						if (online != null) {
							final Player p = online;
							FoliaRunnable.runEntityTask(QAMain.getInstance(), p, () -> applyBleedEffect(p));
						}
					}
				}
			}.runTaskTimerAsynchronously(QAMain.getInstance(), 0, 20);
		}
	}

	private static void applyBleedEffect(Player online) {
		if (!bleedoutMultiplier.containsKey(online.getUniqueId())) return;
		if (!bloodLevel.containsKey(online.getUniqueId()))
			bloodLevel.put(online.getUniqueId(), 0.0);
		double bleedMult = bleedoutMultiplier.get(online.getUniqueId());
		double bloodlevel = bloodLevel.get(online.getUniqueId()) + bleedMult
				+ QAMain.bulletWound_BloodIncreasePerSecond;

		if (bloodlevel / QAMain.bulletWound_initialbloodamount <= 0.75)
			try {
				online.removePotionEffect(XPotion.NAUSEA.getPotionEffectType());
				online.addPotionEffect(new PotionEffect(XPotion.NAUSEA.getPotionEffectType(), 499, 3));
			} catch (Error | Exception e4) {
			}
		if (bloodlevel / QAMain.bulletWound_initialbloodamount <= 0.40)
			try {
				online.removePotionEffect(PotionEffectType.BLINDNESS);
				online.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 499, 1));
			} catch (Error | Exception e4) {
			}
		if (bloodlevel / QAMain.bulletWound_initialbloodamount == 0.0)
			online.damage(1);
		if (bleedMult < 0)
			for (int i = 0; i < 5; i++) {
				double x = Math.random() - 0.5;
				double z = Math.random() - 0.5;
				double yofset = Math.random();
				ParticleHandlers.spawnParticle(1, 0, 0,
						online.getLocation().add(x, 0.8 + yofset, z));
			}

		if (bloodlevel >= QAMain.bulletWound_initialbloodamount)
			bloodLevel.remove(online.getUniqueId());
		else
			bloodLevel.put(online.getUniqueId(), bloodlevel);
	}

}
