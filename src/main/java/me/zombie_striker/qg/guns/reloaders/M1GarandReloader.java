package me.zombie_striker.qg.guns.reloaders;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.guns.utils.WeaponSounds;

public class M1GarandReloader implements ReloadingHandler {

    public M1GarandReloader() { ReloadingManager.add(this); }

    List<UUID> timeR = new ArrayList<>();

    @Override
    public boolean isReloading(final Player player) { return this.timeR.contains(player.getUniqueId()); }

    @Override
    public double reload(final Player player, final Gun g, final int amountReloading) {
        this.timeR.add(player.getUniqueId());
        player.getWorld().playSound(player.getLocation(), WeaponSounds.RELOAD_CLICK.getSoundName(), 1, 1f);
        new BukkitRunnable() {
            @Override
            public void run() {
                player.getWorld().playSound(player.getLocation(), g.getReloadingSound(), 1, 1f);
                M1GarandReloader.this.timeR.remove(player.getUniqueId());
            }
        }.runTaskLater(QAMain.getInstance(), Math.max((int) ((g.getReloadTime() * 20.0) - 10.0), 10));
        return g.getReloadTime();
    }

    @Override
    public String getName() { return ReloadingManager.M1GARAND_RELOAD; }

    @Override
    public String getDefaultReloadingSound() { return WeaponSounds.RELOAD_SLIDE.getSoundName(); }
}
