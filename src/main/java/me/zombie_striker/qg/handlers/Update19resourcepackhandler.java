package me.zombie_striker.qg.handlers;

import me.zombie_striker.qg.QAMain;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

public class Update19resourcepackhandler implements Listener {

    @EventHandler
    public void onResourcePackStatus(PlayerResourcePackStatusEvent event) {
        QAMain.sentResourcepack.remove(event.getPlayer().getUniqueId());

        if (event.getStatus() == PlayerResourcePackStatusEvent.Status.ACCEPTED
                || event.getStatus() == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED) {
            QAMain.resourcepackReq.add(event.getPlayer().getUniqueId());
        }

        if (event.getStatus() == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED)
            QAMain.resourcepackLoading.remove(event.getPlayer().getUniqueId());

        if (event.getStatus() == PlayerResourcePackStatusEvent.Status.DECLINED) {
            if (QAMain.kickIfDeniedRequest) {
                Bukkit.getScheduler().runTask(QAMain.getInstance(), () -> event.getPlayer().kickPlayer(QAMain.S_KICKED_FOR_RESOURCEPACK));
            }

            // Add to the list, so it doesn't keep spamming the title
            QAMain.resourcepackReq.add(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (!QAMain.resourcepackInvincibility) return;
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        if (QAMain.resourcepackLoading.contains(player.getUniqueId()))
            event.setCancelled(true);
    }

}
