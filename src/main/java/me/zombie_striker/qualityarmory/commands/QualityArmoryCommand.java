package me.zombie_striker.qualityarmory.commands;

import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.customitemmanager.CustomItemManager;
import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.qualityarmory.ConfigKey;
import me.zombie_striker.qualityarmory.QAMain;
import me.zombie_striker.qualityarmory.ammo.Ammo;
import me.zombie_striker.qualityarmory.api.QualityArmory;
import me.zombie_striker.qualityarmory.api.events.QAGunGiveEvent;
import me.zombie_striker.qualityarmory.armor.ArmorObject;
import me.zombie_striker.qualityarmory.attachments.AttachmentBase;
import me.zombie_striker.qualityarmory.guns.Gun;
import me.zombie_striker.qualityarmory.utils.LocalUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QualityArmoryCommand implements CommandExecutor, TabCompleter {

    private QAMain main;


    private String changelog = null;



    public QualityArmoryCommand(QAMain qaMain) {
        this.main = qaMain;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> s = new ArrayList<String>();
            if (b("give", args[0]))
                s.add("give");
            if (b("drop", args[0]))
                s.add("drop");
            if (b("getResourcepack", args[0]))
                s.add("getResourcepack");
            if (b("sendResourcepack", args[0]))
                s.add("sendResourcepack");
            if (b("version", args[0]))
                s.add("version");
            if (b("dumpItem", args[0]))
                s.add("dumpItem");
            if (enableShop)
                if (b("shop", args[0]))
                    s.add("shop");
            if (enableCrafting)
                if (b("craft", args[0]))
                    s.add("craft");
            // if (b("getOpenGunSlot", args[0]))
            // s.add("getOpenGunSlot");
            if (sender.hasPermission("qualityarmory.reload"))
                if (b("reload", args[0]))
                    s.add("reload");
            if (sender.hasPermission("qualityarmory.createnewitem")) {
            }

            return s;
        }
        if (args.length == 2) {
            List<String> s = new ArrayList<String>();
            if (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("drop")) {
                for (Map.Entry<MaterialStorage, Gun> e : gunRegister.entrySet()) {
                    if (e.getValue() instanceof AttachmentBase) {
                        if (b(e.getValue().getName(), args[1]))
                            s.add(e.getValue().getName());
                    } else if (b(e.getValue().getName(), args[1]))
                        s.add(e.getValue().getName());
                }
                for (Map.Entry<MaterialStorage, Ammo> e : ammoRegister.entrySet())
                    if (b(e.getValue().getName(), args[1]))
                        s.add(e.getValue().getName());
                for (Map.Entry<MaterialStorage, CustomBaseObject> e : miscRegister.entrySet())
                    if (b(e.getValue().getName(), args[1]))
                        s.add(e.getValue().getName());
                for (Map.Entry<MaterialStorage, ArmorObject> e : armorRegister.entrySet())
                    if (b(e.getValue().getName(), args[1]))
                        s.add(e.getValue().getName());
            }
            return s;
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("give")) {
                return null;
            }
        }
        return null;
    }

    public boolean b(String arg, String startsWith) {
        if (arg == null || startsWith == null)
            return false;
        return arg.toLowerCase().startsWith(startsWith.toLowerCase());
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("QualityArmory")) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("version")) {
                    sender.sendMessage(main.getPrefix()+ " This server is using version " + ChatColor.GREEN
                            + main.getDescription().getVersion() + ChatColor.WHITE + " of QualityArmory");
                    sender.sendMessage("--==Changelog==--");
                    if (changelog == null) {
                        StringBuilder builder = new StringBuilder();

                        InputStream in = getClass().getResourceAsStream("/changelog.txt");
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        for (int i = 0; i < 7; i++) {
                            try {
                                String s = reader.readLine();
                                if (s.length() <= 1)
                                    break;
                                if (i == 6) {
                                    builder.append("......\n");
                                    break;
                                }
                                builder.append("\n").append(s);
                            } catch (IOException ignored) {
                            }
                        }

                        try {
                            in.close();
                            reader.close();
                        } catch (IOException e) {
                        }

                        changelog = LocalUtils.colorize(builder.toString());
                    }


                    sender.sendMessage(changelog);
                    return true;
                }
                if (args[0].equalsIgnoreCase("debug")) {
                    if (sender.hasPermission("qualityarmory.debug")) {
                        boolean debug = !(boolean)main.getSetting(ConfigKey.SETTING_DEBUG.getKey());
                        main.setSetting(ConfigKey.SETTING_DEBUG,debug);
                        sender.sendMessage(main.getPrefix() + "Console debugging set to " + debug);
                    } else {
                        sender.sendMessage(main.getPrefix() + ChatColor.RED + S_NOPERM);
                        return true;
                    }
                    return true;

                }
                if (args[0].equalsIgnoreCase("sendResourcepack")) {
                    Player player = null;
                    if (args.length > 1 && sender.hasPermission("qualityarmory.sendresourcepack.other")) {
                        player = Bukkit.getPlayer(args[1]);
                        if (player == null) {
                            sender.sendMessage(prefix + " This player does not exist.");
                            return true;
                        }
                    } else {
                        player = (Player) sender;
                    }
                    namesToBypass.remove(player.getName());
                    resourcepackwhitelist.set("Names_Of_players_to_bypass", namesToBypass);
                    sender.sendMessage(prefix + S_RESOURCEPACK_OPTIN);
                    QualityArmory.sendResourcepack(player, true);
                    return true;
                }
                if (args[0].equalsIgnoreCase("getResourcepack")) {
                    Player player = null;
                    if (args.length > 1) {
                        player = Bukkit.getPlayer(args[1]);
                        if (player == null) {
                            sender.sendMessage(prefix + " This player does not exist.");
                            return true;
                        }
                    } else {
                        player = (Player) sender;
                    }
                    if (!namesToBypass.contains(player.getName())) {
                        namesToBypass.add(player.getName());
                        resourcepackwhitelist.set("Names_Of_players_to_bypass", namesToBypass);
                    }

                    player.sendMessage(prefix + S_RESOURCEPACK_DOWNLOAD);
                    player.sendMessage(CustomItemManager.getResourcepack());
                    player.sendMessage(prefix + S_RESOURCEPACK_BYPASS);

                    return true;
                }
                if (args[0].equalsIgnoreCase("reload")) {
                    if (sender.hasPermission("qualityarmory.reload")) {
                        reloadConfig();
                        reloadVals();
                        sender.sendMessage(prefix + S_RELOAD);
                        return true;
                    } else {
                        sender.sendMessage(prefix + ChatColor.RED + S_NOPERM);
                        return true;
                    }
                }

                if (args[0].equalsIgnoreCase("drop")) {
                    if (!sender.hasPermission("qualityarmory.drop")) {
                        sender.sendMessage(prefix + ChatColor.RED + S_NOPERM);
                        return true;
                    }
                    if (args.length == 1) {
                        sender.sendMessage(prefix + " The item name is required");
                        StringBuilder sb = new StringBuilder();
                        sb.append("Valid items: ");
                        for (Gun g : gunRegister.values()) {
                            sb.append(g.getName() + ", ");
                        }
                        sb.append(ChatColor.GRAY);
                        for (Ammo g : ammoRegister.values()) {
                            sb.append(g.getName() + ", ");
                        }
                        sb.append(ChatColor.WHITE);
                        for (CustomBaseObject g : miscRegister.values()) {
                            sb.append(g.getName() + ", ");
                        }
                        sb.append(ChatColor.GRAY);
                        for (ArmorObject g : armorRegister.values()) {
                            sb.append(g.getName() + ", ");
                        }
                        sb.append(ChatColor.WHITE);
                        sender.sendMessage(prefix + sb.toString());
                        return true;
                    }

                    CustomBaseObject g = QualityArmory.getCustomItemByName(args[1]);
                    if (g != null) {
                        Location loc = null;
                        Location relLoc = null;
                        if (args.length >= 5) {
                            World w = null;
                            if (sender instanceof Player) {
                                relLoc = (loc = ((Player) sender).getLocation());
                            } else if (sender instanceof BlockCommandSender) {
                                relLoc = (loc = ((BlockCommandSender) sender).getBlock().getLocation());
                            }

                            if (args.length >= 6) {
                                if (args[2].equals("~"))
                                    w = relLoc.getWorld();
                                else
                                    w = Bukkit.getWorld(args[5]);
                            } else {
                                w = relLoc.getWorld();
                            }

                            double x = 0;
                            double y = 0;
                            double z = 0;
                            if (args[2].equals("~"))
                                x = relLoc.getX();
                            else
                                x = Double.parseDouble(args[2]);
                            if (args[3].equals("~"))
                                y = relLoc.getY();
                            else
                                y = Double.parseDouble(args[3]);
                            if (args[4].equals("~"))
                                z = relLoc.getZ();
                            else
                                z = Double.parseDouble(args[4]);
                            loc = new Location(w, x, y, z);
                        }
                        if (loc == null) {
                            sender.sendMessage(prefix + " A valid location is required");
                            return true;
                        }
                        ItemStack temp = null;

                        if (g instanceof Gun) {
                            temp = CustomItemManager.getItemType("gun").getItem(g.getItemData().getMat(), g.getItemData().getData(), g.getItemData().getVariant());
                        } else if (g instanceof Ammo) {
                            temp = CustomItemManager.getItemType("gun").getItem(g.getItemData().getMat(), g.getItemData().getData(), g.getItemData().getVariant());
                        } else {
                            temp = CustomItemManager.getItemType("gun").getItem(g.getItemData().getMat(), g.getItemData().getData(), g.getItemData().getVariant());
                            temp.setAmount(g.getCraftingReturn());
                        }
                        if (temp != null) {
                            loc.getWorld().dropItem(loc, temp);
                            sender.sendMessage(prefix + " Dropping item " + g.getName() + " at that location");
                        } else {
                            sender.sendMessage(prefix + " Failed to drop item " + g.getName() + " at that location");
                        }
                    } else {
                        sender.sendMessage(prefix + " Could not find item \"" + args[1] + "\"");
                    }
                    return true;
                }

                if (args[0].equalsIgnoreCase("give")) {
                    if (!sender.hasPermission("qualityarmory.give")) {
                        sender.sendMessage(prefix + ChatColor.RED + S_NOPERM);
                        return true;
                    }
                    if (args.length == 1) {
                        sender.sendMessage(prefix + " The item name is required");
                        StringBuilder sb = new StringBuilder();
                        sb.append("Valid items: ");
                        for (Gun g : gunRegister.values()) {
                            sb.append(g.getName() + ", ");
                        }
                        sb.append(ChatColor.GRAY);
                        for (Ammo g : ammoRegister.values()) {
                            sb.append(g.getName() + ", ");
                        }
                        sb.append(ChatColor.WHITE);
                        for (CustomBaseObject g : miscRegister.values()) {
                            sb.append(g.getName() + ", ");
                        }
                        sb.append(ChatColor.GRAY);
                        for (ArmorObject g : armorRegister.values()) {
                            sb.append(g.getName() + ", ");
                        }
                        sb.append(ChatColor.WHITE);
                        // for (AttachmentBase g : attachmentRegister.values()) {
                        // sb.append(g.getAttachmentName() + ",");
                        // }
                        sender.sendMessage(prefix + sb.toString());
                        return true;
                    }

                    CustomBaseObject g = QualityArmory.getCustomItemByName(args[1]);
                    if (g != null) {
                        Player who = null;
                        if (args.length > 2)
                            who = Bukkit.getPlayer(args[2]);
                        else if (sender instanceof Player)
                            who = ((Player) sender);
                        else {
                            sender.sendMessage("A USER IS REQUIRED FOR CONSOLE. /QA give <gun> <player> <amount>");
                        }
                        if (who == null) {
                            sender.sendMessage("That player is not online");
                            return true;
                        }

                        ItemStack temp;

                        if (g instanceof Gun) {
                            QAGunGiveEvent event = new QAGunGiveEvent(who, (Gun) g, QAGunGiveEvent.Cause.COMMAND);
                            Bukkit.getPluginManager().callEvent(event);
                            if (event.isCancelled()) return true;

                            g = event.getGun();
                            temp = CustomItemManager.getItemType("gun").getItem(g.getItemData().getMat(), g.getItemData().getData(), g.getItemData().getVariant());
                            who.getInventory().addItem(temp);
                        } else if (g instanceof Ammo) {
                            int amount = ((Ammo) g).getMaxItemStack();
                            if (args.length > 3)
                                amount = Integer.parseInt(args[3]);
                            QualityArmory.addAmmoToInventory(who, (Ammo) g, amount);
                        } else {
                            temp = CustomItemManager.getItemType("gun").getItem(g.getItemData().getMat(), g.getItemData().getData(), g.getItemData().getVariant());
                            temp.setAmount(g.getCraftingReturn());
                            who.getInventory().addItem(temp);
                        }

                        sender.sendMessage(prefix + " Adding " + g.getName() + " to " + (sender == who ? "your" : who.getName() + "'s") + " inventory");
                    } else {
                        sender.sendMessage(prefix + " Could not find item \"" + args[1] + "\"");
                    }
                    return true;
                }
            }
            if (sender instanceof Player) {
                final Player player = (Player) sender;
                if (args.length >= 1 && args[0].equalsIgnoreCase("override")) {
                    resourcepackReq.add(player.getUniqueId());
                    sender.sendMessage(prefix + " Overriding resourcepack requirement.");
                    return true;
                }
                if (shouldSend && !resourcepackReq.contains(player.getUniqueId())) {
                    QualityArmory.sendResourcepack(((Player) sender), true);
                    return true;
                }
                if (args.length == 0) {
                    sendHelp(player);
                    return true;
                }
                if (enableCrafting)
                    if (args[0].equalsIgnoreCase("craft")) {
                        if (!sender.hasPermission("qualityarmory.craft")) {
                            sender.sendMessage(prefix + ChatColor.RED + S_NOPERM);
                            return true;
                        }
                        player.openInventory(createCraft(0));
                        return true;

                    }
                if (enableShop)
                    if (args[0].equalsIgnoreCase("shop")) {
                        if (args.length == 2 && sender.hasPermission("qualityarmory.shop.other")) {
                            Player target = Bukkit.getPlayer(args[1]);
                            if (target == null) {
                                sender.sendMessage(prefix + ChatColor.RED + "That player is not online");
                                return true;
                            }

                            target.openInventory(createShop(0));
                            return true;
                        }

                        if (!sender.hasPermission("qualityarmory.shop")) {
                            sender.sendMessage(prefix + ChatColor.RED + S_NOPERM);
                            return true;
                        }
                        player.openInventory(createShop(0));
                        return true;

                    }
                // The user sent an invalid command/no args.
                sendHelp(player);
                return true;
            }
        }
        return true;
    }

    public void sendHelp(CommandSender sender) {
        sender.sendMessage(LocalUtils.colorize(main.getPrefix() + " Commands:"));
        sender.sendMessage(ChatColor.GOLD + "/QA give <Item> <player> <amount>:" + ChatColor.GRAY
                + " Gives the sender the item specified (guns, ammo, misc.)");
        sender.sendMessage(ChatColor.GOLD + "/QA craft:" + ChatColor.GRAY + " Opens the crafting menu.");
        sender.sendMessage(ChatColor.GOLD + "/QA shop: " + ChatColor.GRAY + "Opens the shop menu");
        sender.sendMessage(ChatColor.GOLD + "/QA getResourcepack: " + ChatColor.GRAY
                + "Sends the link to the resourcepack. Disables the resourcepack prompts until /qa sendResourcepack is used");
        sender.sendMessage(ChatColor.GOLD + "/QA sendResourcepack: " + ChatColor.GRAY
                + "Sends the resourcepack prompt. Enables the resourcepack prompt. Enabled by default");

        // sender.sendMessage(
        // ChatColor.GOLD + "/QA listItemIds: " + ChatColor.GRAY + "Lists the materials
        // and data for all items.");
        if (sender.hasPermission("qualityarmory.reload"))
            sender.sendMessage(ChatColor.GOLD + "/QA reload: " + ChatColor.GRAY + "reloads all values in QA.");
        }
    }

}
