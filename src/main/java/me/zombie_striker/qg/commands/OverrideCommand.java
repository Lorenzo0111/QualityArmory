package me.zombie_striker.qg.commands;

import me.zombie_striker.qg.QAMain;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Lets a player use guns without accepting the resourcepack.
 */
final class OverrideCommand extends SubCommand {

    OverrideCommand() {
        super("override");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        QAMain.getResourcepacks().setAccepted(((Player) sender).getUniqueId());
        sendPrefixed(sender, QAMain.getMessages().commands.resourcepackOverride);
        return true;
    }
}
