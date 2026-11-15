package me.zombie_striker.qg.commands;

import me.zombie_striker.qg.QAMain;
import org.bukkit.command.CommandSender;

final class DebugCommand extends SubCommand {

    DebugCommand() {
        super("debug");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "qualityarmory.debug")) return true;
        QAMain.getConfiguration().debug.enabled = !QAMain.getConfiguration().debug.enabled;
        sendPrefixed(sender, "Console debugging set to " + QAMain.getConfiguration().debug.enabled);
        return true;
    }
}
