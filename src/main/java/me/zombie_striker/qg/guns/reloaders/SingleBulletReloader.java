package me.zombie_striker.qg.guns.reloaders;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.guns.utils.WeaponSounds;

public class SingleBulletReloader implements ReloadingHandler {

    List<UUID> timeR = new ArrayList<>();

    public SingleBulletReloader() { ReloadingManager.add(this); }

    @Override
    public boolean isReloading(final Player player) { return this.timeR.contains(player.getUniqueId()); }

    @Override
    public double reload(final Player player, final Gun g, final int amountReloading) {
        this.timeR.add(player.getUniqueId());
        final double time = (g.getReloadTime()) / g.getMaxBullets();
        final double time2 = time * amountReloading;
        for (int i = 0; i < amountReloading; i++) {
            final int finalI = i;
            new BukkitRunnable() {
                int temp = player.getInventory().getHeldItemSlot();

                @Override
                public void run() {
                    if (player.getInventory().getHeldItemSlot() != this.temp)
                        return;
                    QualityArmory.sendHotbarGunAmmoCount(player, g, player.getInventory().getItemInMainHand(), true,
                            g.getMaxBullets() - amountReloading + finalI, g.getMaxBullets());
                    try {
                        player.getWorld().playSound(player.getLocation(), g.getReloadingSound(), 1, 1f);
                    } catch (final Error e) {
                        try {
                            player.getWorld().playSound(player.getLocation(), Sound.valueOf("PISTON_EXTEND"), 5, 4f);
                            player.getWorld().playSound(player.getLocation(), Sound.valueOf("DIG_SAND"), 8, 1.4f);
                        } catch (Error | Exception e43) {
                        }
                    }
                }
            }.runTaskLater(QAMain.getInstance(), (int) (time * i * 20));
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                SingleBulletReloader.this.timeR.remove(player.getUniqueId());

            }
        }.runTaskLater(QAMain.getInstance(), (int) (time2 * 20) + 5);
        return time2;
    }

    @Override
    public String getName() {

        return ReloadingManager.SINGLE_RELOAD;
    }

    @Override
    public String getDefaultReloadingSound() { return WeaponSounds.RELOAD_BULLET.getSoundName(); }
}
