package me.zombie_striker.qg.commands;

import me.zombie_striker.qg.QAMain;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * {@code /qa shop [player]}: opens the shop menu for yourself, or for another player.
 */
final class ShopCommand extends SubCommand {

    ShopCommand() {
        super("shop");
    }

    @Override
    public boolean isSuggested(CommandSender sender) {
        return QAMain.getConfiguration().menu.enableShop;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!QAMain.getConfiguration().menu.enableShop) return false;

        if (args.length == 2 && sender.hasPermission("qualityarmory.shop.other")) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sendPrefixed(sender, QAMain.getMessages().general.playerNotOnline);
                return true;
            }
            QAMain.openShopMenu(target);
            return true;
        }

        if (!hasPermission(sender, "qualityarmory.shop")) return true;
        QAMain.openShopMenu((Player) sender);
        return true;
    }
}
