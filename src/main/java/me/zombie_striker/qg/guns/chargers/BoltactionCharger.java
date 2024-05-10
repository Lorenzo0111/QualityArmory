package me.zombie_striker.qg.guns.chargers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.guns.utils.WeaponSounds;

public class BoltactionCharger implements ChargingHandler {

    List<UUID> timeC = new ArrayList<>();
    List<UUID> timeR = new ArrayList<>();

    public BoltactionCharger() { ChargingManager.add(this); }

    @Override
    public boolean isCharging(final Player player) { return this.timeC.contains(player.getUniqueId()); }

    @Override
    public boolean shoot(final Gun g, final Player player, final ItemStack stack) {
        this.timeR.add(player.getUniqueId());
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (QAMain.isVersionHigherThan(1, 9)) {
                        player.getWorld().playSound(player.getLocation(), g.getChargingSound(), 1, 1f);
                    } else
                        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 5, 1);
                } catch (final Error e) {
                    try {
                        player.getWorld().playSound(player.getLocation(), Sound.valueOf("CLICK"), 5, 1);
                    } catch (Error | Exception e43) {
                    }
                }
            }
        }.runTaskLater(QAMain.getInstance(), 10);
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (QAMain.isVersionHigherThan(1, 9)) {
                        player.getWorld().playSound(player.getLocation(), g.getChargingSound(), 1, 1f);
                    } else
                        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 5, 1);
                } catch (final Error e) {
                    try {
                        player.getWorld().playSound(player.getLocation(), Sound.valueOf("CLICK"), 5, 1);
                    } catch (Error | Exception e43) {
                    }
                }
                BoltactionCharger.this.timeR.remove(player.getUniqueId());
            }
        }.runTaskLater(QAMain.getInstance(), (int) g.getDelayBetweenShotsInSeconds() * 20);
        return true;
    }

    @Override
    public String getName() { return ChargingManager.BOLT; }

    @Override
    public String getDefaultChargingSound() {
        return WeaponSounds.RELOAD_BOLT.getSoundName();
        // g.getChargingSound()
    }

}
