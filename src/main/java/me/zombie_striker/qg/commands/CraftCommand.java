package me.zombie_striker.qg.commands;

import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.crafting.Crafting;
import me.zombie_striker.qg.guns.Gun;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * {@code /qa craft [item]}: opens the crafting menu, or crafts the item directly.
 */
final class CraftCommand extends SubCommand {

    CraftCommand() {
        super("craft");
    }

    @Override
    public boolean isSuggested(CommandSender sender) {
        return QAMain.getConfiguration().menu.enableCrafting;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!QAMain.getConfiguration().menu.enableCrafting) return false;
        Player player = (Player) sender;
        if (!hasPermission(sender, "qualityarmory.craft")) return true;

        if (args.length == 2) {
            CustomBaseObject item = QualityArmory.getCustomItemByName(args[1]);
            if (item == null) {
                QAMain.openCraftMenu(player);
                return true;
            }

            if (item instanceof Gun) {
                if (QAMain.getConfiguration().permissions.requireCraft && !player.hasPermission("qualityarmory.craftgun")) {
                    sendPrefixed(player, ChatColor.RED + QAMain.getMessages().general.noPermission);
                    return true;
                }
                if (QAMain.getConfiguration().permissions.perWeapon.craft && !player.hasPermission("qualityarmory.craftgun." + item.getName())) {
                    sendPrefixed(player, ChatColor.RED + QAMain.getMessages().general.noPermission);
                    return true;
                }
            }

            if (!Crafting.hasIngredients(player, item)) {
                sendPrefixed(player, QAMain.getMessages().crafting.missingIngredients);
                return true;
            }

            Crafting.removeIngredients(player, item);
            ItemStack result = QualityArmory.getCustomItemAsItemStack(item);
            result.setAmount(item.getCraftingReturn());
            player.getInventory().addItem(result);
            return true;
        }

        QAMain.openCraftMenu(player);
        return true;
    }
}
