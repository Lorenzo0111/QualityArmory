package me.zombie_striker.qg.handlers;

import com.cryptomorin.xseries.messages.ActionBar;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HotbarMessager {

	/**
	 * Sends the hotbar message 'message' to the player 'player'
	 *
	 * @param player
	 * @param message
	 * @throws Exception
	 */
	public static void sendHotBarMessage(Player player, String message) throws Exception {
		ActionBar.sendActionBar(player, ChatColor.translateAlternateColorCodes('&', message));
	}


}