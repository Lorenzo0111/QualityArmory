package me.zombie_striker.qg.commands;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.api.QualityArmory;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

final class SendResourcepackCommand extends SubCommand {

    SendResourcepackCommand() {
        super("sendResourcepack");
    }

    @Override
    public boolean isSuggested(CommandSender sender) {
        return true;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player;
        if (args.length > 1 && sender.hasPermission("qualityarmory.sendresourcepack.other")) {
            player = Bukkit.getPlayer(args[1]);
            if (player == null) {
                sendPrefixed(sender, QAMain.getMessages().general.playerNotFound);
                return true;
            }
        } else if (isPlayer(sender)) {
            player = (Player) sender;
        } else {
            return true;
        }
        QAMain.getResourcepacks().setBypassing(player.getName(), false);
        sendPrefixed(sender, QAMain.getMessages().resourcepack.nowOptedIn);
        QualityArmory.sendResourcepack(player, true);
        return true;
    }
}
