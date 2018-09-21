package me.zombie_striker.qg.handlers;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.zombie_striker.qg.Main;

public class BulletWoundHandler {

	public static HashMap<UUID, Double> bleedoutMultiplier = new HashMap<>();
	public static HashMap<UUID, Double> bloodLevel = new HashMap<>();

	public static BukkitTask task = null;

	public static void bulletHit(Player player, double bulletSeverity) {
		if (Main.enableBleeding) {
			double current = bleedoutMultiplier.containsKey(player.getUniqueId())
					? bleedoutMultiplier.get(player.getUniqueId())
					: 0;

			player.sendMessage(Main.S_BLEEDOUT_STARTBLEEDING);

			current -= bulletSeverity;
			bleedoutMultiplier.put(player.getUniqueId(), current);
			if (!bloodLevel.containsKey(player.getUniqueId())) {
				bloodLevel.put(player.getUniqueId(), Main.bulletWound_initialbloodamount);
			}
		}
	}

	public static void startTimer() {
		if (Main.enableBleeding) {
			task = new BukkitRunnable() {
				@Override
				public void run() {
					for (Entry<UUID, Double> e : bleedoutMultiplier.entrySet()) {
						Player online = Bukkit.getPlayer(e.getKey());
						if (online != null) {
							if (!bloodLevel.containsKey(online.getUniqueId()))
								bloodLevel.put(online.getUniqueId(), 0.0);
							double bloodlevel = bloodLevel.get(online.getUniqueId()) + e.getValue()
									+ Main.bulletWound_BloodIncreasePerSecond;

							if (bloodlevel / Main.bulletWound_initialbloodamount <= 0.75)

								try {
									online.removePotionEffect(PotionEffectType.CONFUSION);
									online.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 499, 3));
								} catch (Error | Exception e4) {
								}
							if (bloodlevel / Main.bulletWound_initialbloodamount <= 0.40)
								try {
									online.removePotionEffect(PotionEffectType.BLINDNESS);
									online.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 499, 1));
								} catch (Error | Exception e4) {
								}
							if (bloodlevel / Main.bulletWound_initialbloodamount == 0.0)
								online.damage(1);
							if (bleedoutMultiplier.get(online.getUniqueId()) < 0)
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

							if (bloodlevel >= Main.bulletWound_initialbloodamount)
								bloodLevel.remove(online.getUniqueId());
							else
								bloodLevel.put(online.getUniqueId(), bloodlevel);
						}
					}
				}
			}.runTaskTimer(Main.getInstance(), 0, 20);
		}
	}

}
