package me.zombie_striker.qg.commands;

import me.zombie_striker.customitemmanager.CustomItemManager;
import me.zombie_striker.qg.QAMain;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

final class GetResourcepackCommand extends SubCommand {

    GetResourcepackCommand() {
        super("getResourcepack");
    }

    @Override
    public boolean isSuggested(CommandSender sender) {
        return true;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player;
        if (args.length > 1) {
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
        QAMain.getResourcepacks().setBypassing(player.getName(), true);

        sendPrefixed(player, QAMain.getMessages().resourcepack.download);
        player.sendMessage(CustomItemManager.getResourcepack(player));
        sendPrefixed(player, QAMain.getMessages().resourcepack.nowBypassing);
        return true;
    }
}
