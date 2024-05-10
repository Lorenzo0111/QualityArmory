package me.zombie_striker.qg.handlers;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.cryptomorin.xseries.messages.ActionBar;

public class HotbarMessager {

    /**
     * Sends the hotbar message 'message' to the player 'player'
     *
     * @param player
     * @param message
     * @throws Exception
     */
    public static void sendHotBarMessage(final Player player, final String message) throws Exception {
        ActionBar.sendActionBar(player, ChatColor.translateAlternateColorCodes('&', message));
    }

}