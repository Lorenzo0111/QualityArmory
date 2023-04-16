package me.zombie_striker.qualityarmory.utils;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HotbarMessagerUtil {

	/**
	 * Sends the hotbar message 'message' to the player 'player'
	 *
	 * @param player
	 * @param message
	 * @throws Exception
	 */
	public static void sendHotBarMessage(Player player, String message) throws Exception {
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', message)));
	}


}