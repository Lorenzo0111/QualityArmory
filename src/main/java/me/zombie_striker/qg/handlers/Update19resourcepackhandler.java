package me.zombie_striker.qg.handlers;

import me.zombie_striker.qg.utils.FoliaRunnable;
import me.zombie_striker.qg.QAMain;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

public class Update19resourcepackhandler implements Listener {

    @EventHandler
    public void onResourcePackStatus(PlayerResourcePackStatusEvent event) {
        QAMain.getResourcepacks().clearSent(event.getPlayer().getUniqueId());

        if (event.getStatus() == PlayerResourcePackStatusEvent.Status.ACCEPTED
                || event.getStatus() == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED) {
            QAMain.getResourcepacks().setAccepted(event.getPlayer().getUniqueId());
        }

        if (event.getStatus() == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED)
            QAMain.getResourcepacks().setLoading(event.getPlayer().getUniqueId(), false);

        if (event.getStatus() == PlayerResourcePackStatusEvent.Status.DECLINED) {
            if (QAMain.getConfiguration().resourcepack.kickIfDenied) {
                final Player player = event.getPlayer();
                FoliaRunnable.runEntityTask(QAMain.getInstance(), player, () -> player.kickPlayer(QAMain.getMessages().resourcepack.kickMessage));
            }

            // Add to the list, so it doesn't keep spamming the title
            QAMain.getResourcepacks().setAccepted(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (!QAMain.getConfiguration().resourcepack.invincibility) return;
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        if (QAMain.getResourcepacks().isLoading(player.getUniqueId()))
            event.setCancelled(true);
    }

}
