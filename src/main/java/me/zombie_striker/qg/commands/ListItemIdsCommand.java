package me.zombie_striker.qg.commands;

import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.qg.QAMain;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

final class ListItemIdsCommand extends SubCommand {

    ListItemIdsCommand() {
        super("listItemIds");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "qualityarmory.getmaterialsused")) return true;
        list(sender, ChatColor.GREEN, QAMain.getItemRegistry().getMisc().values());
        list(sender, ChatColor.GOLD, QAMain.getItemRegistry().getGuns().values());
        list(sender, ChatColor.AQUA, QAMain.getItemRegistry().getAmmo().values());
        // Continues to the player commands, which show the help, as it always did
        return false;
    }

    private static void list(CommandSender sender, ChatColor color, Iterable<? extends CustomBaseObject> items) {
        for (CustomBaseObject item : items)
            sender.sendMessage(color + item.getName() + ": " + ChatColor.WHITE
                    + item.getItemData().getMat().name() + " : " + item.getItemData().getData());
    }
}
