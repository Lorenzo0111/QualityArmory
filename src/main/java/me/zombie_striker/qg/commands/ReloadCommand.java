package me.zombie_striker.qg.commands;

import me.zombie_striker.qg.QAMain;
import org.bukkit.command.CommandSender;

final class ReloadCommand extends SubCommand {

    ReloadCommand() {
        super("reload");
    }

    @Override
    public boolean isSuggested(CommandSender sender) {
        return sender.hasPermission("qualityarmory.reload");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "qualityarmory.reload")) return true;
        QAMain.getInstance().reloadVals();
        sendPrefixed(sender, QAMain.getMessages().general.reloaded);
        return true;
    }
}
