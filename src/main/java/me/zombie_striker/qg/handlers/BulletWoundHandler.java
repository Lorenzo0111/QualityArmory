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

import me.zombie_striker.qg.QAMain;

public class BulletWoundHandler {

    public static HashMap<UUID, Double> bleedoutMultiplier = new HashMap<>();
    public static HashMap<UUID, Double> bloodLevel = new HashMap<>();

    public static BukkitTask task = null;

    public static void bulletHit(final Player player, final double bulletSeverity) {
        if (QAMain.enableBleeding) {
            double current = BulletWoundHandler.bleedoutMultiplier.containsKey(player.getUniqueId())
                    ? BulletWoundHandler.bleedoutMultiplier.get(player.getUniqueId())
                    : 0;

            player.sendMessage(QAMain.S_BLEEDOUT_STARTBLEEDING);

            current -= bulletSeverity;
            BulletWoundHandler.bleedoutMultiplier.put(player.getUniqueId(), current);
            if (!BulletWoundHandler.bloodLevel.containsKey(player.getUniqueId())) {
                BulletWoundHandler.bloodLevel.put(player.getUniqueId(), QAMain.bulletWound_initialbloodamount);
            }
        }
    }

    public static void startTimer() {
        if (QAMain.enableBleeding) {
            BulletWoundHandler.task = new BukkitRunnable() {
                @Override
                public void run() {
                    for (final Entry<UUID, Double> e : BulletWoundHandler.bleedoutMultiplier.entrySet()) {
                        final Player online = Bukkit.getPlayer(e.getKey());
                        if (online != null) {
                            if (!BulletWoundHandler.bloodLevel.containsKey(online.getUniqueId()))
                                BulletWoundHandler.bloodLevel.put(online.getUniqueId(), 0.0);
                            final double bloodlevel = BulletWoundHandler.bloodLevel.get(online.getUniqueId()) + e.getValue()
                                    + QAMain.bulletWound_BloodIncreasePerSecond;

                            if (bloodlevel / QAMain.bulletWound_initialbloodamount <= 0.75)

                                try {
                                    online.removePotionEffect(PotionEffectType.NAUSEA);
                                    online.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 499, 3));
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
                            if (BulletWoundHandler.bleedoutMultiplier.get(online.getUniqueId()) < 0)
                                // online.getWorld().spawnParticle(Particle.REDSTONE,
                                // online.getLocation().getX(), online.getLocation().getY()+1.5,
                                // online.getLocation().getZ(), 0, 1.0, 0.0, 0.0, 1);
                                for (int i = 0; i < 5; i++) {
                                    final double x = Math.random() - 0.5;
                                    final double z = Math.random() - 0.5;
                                    final double yofset = Math.random();
                                    ParticleHandlers.spawnParticle(1, 0, 0, online.getLocation().add(x, 0.8 + yofset, z));
                                }

                            if (bloodlevel >= QAMain.bulletWound_initialbloodamount)
                                BulletWoundHandler.bloodLevel.remove(online.getUniqueId());
                            else
                                BulletWoundHandler.bloodLevel.put(online.getUniqueId(), bloodlevel);
                        }
                    }
                }
            }.runTaskTimer(QAMain.getInstance(), 0, 20);
        }
    }

}
