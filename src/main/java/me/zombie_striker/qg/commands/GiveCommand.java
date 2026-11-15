package me.zombie_striker.qg.commands;

import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.customitemmanager.CustomItemManager;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.ammo.Ammo;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.api.QAGunGiveEvent;
import me.zombie_striker.qg.config.MessagesConfig;
import me.zombie_striker.qg.guns.Gun;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

final class GiveCommand extends SubCommand {

    GiveCommand() {
        super("give");
    }

    @Override
    public boolean isSuggested(CommandSender sender) {
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        // Third argument: online players
        return args.length == 2 ? ItemNames.complete(args[1]) : null;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "qualityarmory.give")) return true;
        MessagesConfig messages = QAMain.getMessages();
        if (args.length == 1) {
            sendPrefixed(sender, messages.commands.itemNameRequired);
            sendPrefixed(sender, ItemNames.validItemsMessage());
            return true;
        }

        CustomBaseObject item = QualityArmory.getCustomItemByName(args[1]);
        if (item == null) {
            sendPrefixed(sender, MessagesConfig.format(messages.commands.itemNotFound, "item", args[1]));
            return true;
        }

        Player who = null;
        if (args.length > 2)
            who = Bukkit.getPlayer(args[2]);
        else if (sender instanceof Player)
            who = (Player) sender;
        else
            sendPrefixed(sender, messages.general.consoleRequiresPlayer);
        if (who == null) {
            sendPrefixed(sender, messages.general.playerNotOnline);
            return true;
        }

        if (item instanceof Gun) {
            QAGunGiveEvent event = new QAGunGiveEvent(who, (Gun) item, QAGunGiveEvent.Cause.COMMAND);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) return true;

            Gun gun = event.getGun();
            item = gun;
            ItemStack stack = CustomItemManager.getItemType("gun").getItem(gun.getItemData().getMat(), gun.getItemData().getData(), gun.getItemData().getVariant());
            Gun.updateAmmo(gun, stack, gun.getMaxBullets());
            who.getInventory().addItem(stack);
        } else if (item instanceof Ammo) {
            int amount = ((Ammo) item).getMaxItemStack();
            if (args.length > 3)
                amount = Integer.parseInt(args[3]);
            QualityArmory.addAmmoToInventory(who, (Ammo) item, amount);
        } else {
            ItemStack stack = CustomItemManager.getItemType("gun").getItem(item.getItemData().getMat(), item.getItemData().getData(), item.getItemData().getVariant());
            stack.setAmount(item.getCraftingReturn());
            who.getInventory().addItem(stack);
        }

        sendPrefixed(sender, sender == who
                ? MessagesConfig.format(messages.commands.givingItemSelf, "item", item.getName())
                : MessagesConfig.format(messages.commands.givingItemOther, "item", item.getName(), "player", who.getName()));
        return true;
    }
}
