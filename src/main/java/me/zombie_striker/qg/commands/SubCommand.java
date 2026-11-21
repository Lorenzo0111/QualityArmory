package me.zombie_striker.qg.commands;

import me.zombie_striker.qg.QAMain;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@code /qa <name>} subcommand.
 */
public abstract class SubCommand {
    private final String name;

    protected SubCommand(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * @param args All arguments, including the subcommand name at index 0.
     * @return Whether the command was handled. Returning false lets the registry continue with the player
     * commands, like the original command handler did.
     */
    public abstract boolean execute(CommandSender sender, String[] args);

    /**
     * @return Whether the subcommand is suggested when tab completing the first argument.
     */
    public boolean isSuggested(CommandSender sender) {
        return false;
    }

    /**
     * @param args All arguments, including the subcommand name at index 0. Always at least 2.
     * @return Suggestions for the last argument, or null to suggest online players.
     */
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return args.length == 2 ? new ArrayList<>() : null;
    }

    /**
     * Checks a permission and tells the sender when they don't have it.
     */
    protected static boolean hasPermission(CommandSender sender, String permission) {
        if (sender.hasPermission(permission)) return true;
        sendPrefixed(sender, ChatColor.RED + QAMain.getMessages().general.noPermission);
        return false;
    }

    /**
     * Tells console senders that the command needs a player.
     */
    protected static boolean isPlayer(CommandSender sender) {
        if (sender instanceof Player) return true;
        sendPrefixed(sender, QAMain.getMessages().general.playerOnly);
        return false;
    }

    protected static void sendPrefixed(CommandSender sender, String message) {
        sender.sendMessage(QAMain.getMessages().general.prefix + message);
    }
}
