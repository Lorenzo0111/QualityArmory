package me.zombie_striker.qualityarmory.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

import me.zombie_striker.qualityarmory.QAMain;

public class Update19resourcepackhandler implements Listener{


	@EventHandler
	public void onResourcepackStatusEvent(PlayerResourcePackStatusEvent event) {
		QAMain.sentResourcepack.remove(event.getPlayer().getUniqueId());
		if (event.getStatus() == PlayerResourcePackStatusEvent.Status.ACCEPTED
				|| event.getStatus() == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED) {
			QAMain.resourcepackReq.add(event.getPlayer().getUniqueId());
		}else if (QAMain.kickIfDeniedRequest) {
			Bukkit.getScheduler().runTask(QAMain.getInstance(), () -> event.getPlayer().kickPlayer(QAMain.S_KICKED_FOR_RESOURCEPACK));
		}

		if (event.getStatus() == PlayerResourcePackStatusEvent.Status.DECLINED) {
			QAMain.resourcepackReq.add(event.getPlayer().getUniqueId()); // Add to the list, so it doesn't keep spamming the title
		}
	}
}
