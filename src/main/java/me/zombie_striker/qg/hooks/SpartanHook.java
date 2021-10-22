package me.zombie_striker.qg.hooks;

import me.vagdedes.spartan.api.PlayerViolationEvent;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.guns.utils.GunUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SpartanHook implements Listener {

    @EventHandler
    public void onFlag(PlayerViolationEvent event) {
        Gun gun = QualityArmory.getGunInHand(event.getPlayer());
        if (gun == null) return;

        if (GunUtil.isDelay(gun,event.getPlayer())) {
            event.setCancelled(true);
            QAMain.DEBUG("Cancelled spartan violation because player is using gun.");
        }
    }
}
