package me.zombie_striker.qg.commands;

import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.customitemmanager.CustomItemManager;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.ammo.Ammo;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.config.MessagesConfig;
import me.zombie_striker.qg.guns.Gun;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * {@code /qa drop <item> <x> <y> <z> [world]}, where coordinates can be {@code ~} for the sender's position.
 */
final class DropCommand extends SubCommand {

    DropCommand() {
        super("drop");
    }

    @Override
    public boolean isSuggested(CommandSender sender) {
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return args.length == 2 ? ItemNames.complete(args[1]) : null;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "qualityarmory.drop")) return true;
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

        Location loc = args.length >= 5 ? parseLocation(sender, args) : null;
        if (loc == null) {
            sendPrefixed(sender, messages.commands.locationRequired);
            return true;
        }

        ItemStack stack = CustomItemManager.getItemType("gun").getItem(item.getItemData().getMat(), item.getItemData().getData(), item.getItemData().getVariant());
        if (stack != null && !(item instanceof Gun) && !(item instanceof Ammo))
            stack.setAmount(item.getCraftingReturn());

        if (stack != null) {
            loc.getWorld().dropItem(loc, stack);
            sendPrefixed(sender, MessagesConfig.format(messages.commands.droppingItem, "item", item.getName()));
        } else {
            sendPrefixed(sender, MessagesConfig.format(messages.commands.dropFailed, "item", item.getName()));
        }
        return true;
    }

    private static Location parseLocation(CommandSender sender, String[] args) {
        Location relLoc = null;
        if (sender instanceof Player)
            relLoc = ((Player) sender).getLocation();
        else if (sender instanceof BlockCommandSender)
            relLoc = ((BlockCommandSender) sender).getBlock().getLocation();

        World world;
        if (args.length >= 6 && !args[2].equals("~"))
            world = Bukkit.getWorld(args[5]);
        else
            world = relLoc.getWorld();

        double x = args[2].equals("~") ? relLoc.getX() : Double.parseDouble(args[2]);
        double y = args[3].equals("~") ? relLoc.getY() : Double.parseDouble(args[3]);
        double z = args[4].equals("~") ? relLoc.getZ() : Double.parseDouble(args[4]);
        return new Location(world, x, y, z);
    }
}
