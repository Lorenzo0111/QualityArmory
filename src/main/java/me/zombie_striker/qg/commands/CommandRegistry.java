package me.zombie_striker.qg.commands;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.config.MessagesConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Handles {@code /qualityarmory}. Commands run in two phases, like before the split:
 * <ol>
 *     <li>Commands for any sender ({@link #register}).</li>
 *     <li>Player commands ({@link #registerPlayer}), which require the resourcepack to be accepted when it
 *     is enabled. Unknown commands and no arguments show the help.</li>
 * </ol>
 */
public final class CommandRegistry implements TabExecutor {
    private final Map<String, SubCommand> commands = new LinkedHashMap<>();
    private final Map<String, SubCommand> playerCommands = new LinkedHashMap<>();
    private final List<SubCommand> suggestionOrder = new ArrayList<>();

    public CommandRegistry register(SubCommand command) {
        commands.put(key(command.getName()), command);
        return this;
    }

    public CommandRegistry registerPlayer(SubCommand command) {
        playerCommands.put(key(command.getName()), command);
        return this;
    }

    /**
     * Sets the order commands are suggested in. Commands not listed are never suggested.
     */
    public CommandRegistry suggestInOrder(String... names) {
        suggestionOrder.clear();
        for (String name : names) {
            SubCommand command = find(name);
            if (command != null) suggestionOrder.add(command);
        }
        return this;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            SubCommand sub = commands.get(key(args[0]));
            if (sub != null && sub.execute(sender, args))
                return true;
        }

        if (!(sender instanceof Player))
            return true;
        Player player = (Player) sender;

        SubCommand override = playerCommands.get("override");
        if (args.length > 0 && override != null && key(args[0]).equals("override") && override.execute(sender, args))
            return true;

        if (QAMain.getConfiguration().resourcepack.enabled && !QAMain.getResourcepacks().hasAccepted(player.getUniqueId())) {
            me.zombie_striker.qg.api.QualityArmory.sendResourcepack(player, true);
            return true;
        }

        if (args.length > 0) {
            SubCommand sub = playerCommands.get(key(args[0]));
            if (sub != null && sub != override && sub.execute(sender, args))
                return true;
        }

        sendHelp(sender);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> suggestions = new ArrayList<>();
            String typed = args[0].toLowerCase(Locale.ROOT);
            for (SubCommand sub : suggestionOrder) {
                if (sub.isSuggested(sender) && sub.getName().toLowerCase(Locale.ROOT).startsWith(typed))
                    suggestions.add(sub.getName());
            }
            return suggestions;
        }

        SubCommand sub = find(args[0]);
        if (sub == null)
            return args.length == 2 ? new ArrayList<>() : null;
        return sub.tabComplete(sender, args);
    }

    private SubCommand find(String name) {
        SubCommand command = commands.get(key(name));
        return command != null ? command : playerCommands.get(key(name));
    }

    private static String key(String name) {
        return name.toLowerCase(Locale.ROOT);
    }

    /**
     * Sends the {@code /qa} help.
     */
    public static void sendHelp(CommandSender sender) {
        MessagesConfig.Help help = QAMain.getMessages().help;
        sender.sendMessage(QAMain.getMessages().general.prefix + help.header);
        sender.sendMessage(help.give);
        sender.sendMessage(help.craft);
        sender.sendMessage(help.shop);
        sender.sendMessage(help.getResourcepack);
        sender.sendMessage(help.sendResourcepack);
        if (sender.hasPermission("qualityarmory.reload"))
            sender.sendMessage(help.reload);
        if (sender.hasPermission("qualityarmory.createnewitem"))
            sender.sendMessage(help.createNewAmmo);
    }
}
