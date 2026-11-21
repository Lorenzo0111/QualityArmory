package me.zombie_striker.qg.commands;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.guns.Gun;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

final class DumpItemCommand extends SubCommand {

    DumpItemCommand() {
        super("dumpItem");
    }

    @Override
    public boolean isSuggested(CommandSender sender) {
        return true;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "qualityarmory.debug")) return true;
        if (!(sender instanceof Player)) return true;

        Gun gun = QualityArmory.getGunInHand((Player) sender);
        if (gun == null) {
            sendPrefixed(sender, QAMain.getMessages().commands.holdGunRequired);
            return true;
        }
        sendPrefixed(sender, ChatColor.YELLOW + gun.toString());
        return true;
    }
}
