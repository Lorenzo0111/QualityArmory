package me.zombie_striker.qg.handlers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

import me.zombie_striker.qg.Main;

public class Update18resourcepackhandler implements Listener{


	@EventHandler
	public void onResourcepackStatusEvent(PlayerResourcePackStatusEvent event) {
		if (event.getStatus() == PlayerResourcePackStatusEvent.Status.ACCEPTED
				|| event.getStatus() == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED)
			Main.resourcepackReq.add(event.getPlayer().getUniqueId());
	}
}
