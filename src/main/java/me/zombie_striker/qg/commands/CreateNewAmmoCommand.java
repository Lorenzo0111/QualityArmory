package me.zombie_striker.qg.commands;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.config.GunYMLCreator;
import me.zombie_striker.qg.handlers.MultiVersionLookup;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Collections;

final class CreateNewAmmoCommand extends SubCommand {

    CreateNewAmmoCommand() {
        super("createNewAmmo");
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean execute(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "qualityarmory.createnewitem")) return true;
        if (args.length < 2) {
            CommandRegistry.sendHelp(sender);
            return true;
        }
        if (!isPlayer(sender)) return true;

        ItemStack itemInHand = ((Player) sender).getItemInHand();
        if (itemInHand == null) {
            sendPrefixed(sender, QAMain.getMessages().commands.holdAmmoTemplateRequired);
            return true;
        }
        GunYMLCreator.createSkullAmmo(true, QAMain.getInstance().getDataFolder(), false, "custom_" + args[1], args[1],
                args[1], Collections.singletonList("Custom_item"), itemInHand.getType(),
                itemInHand.getDurability(),
                (itemInHand.getType() == MultiVersionLookup.getSkull()
                        ? ((SkullMeta) itemInHand.getItemMeta()).getOwner()
                        : null),
                null, 100, 1, 64);
        sendPrefixed(sender, QAMain.getMessages().commands.ammoCreated);
        sendPrefixed(sender, QAMain.getMessages().commands.ammoCreatedReloadHint);
        return true;
    }
}
