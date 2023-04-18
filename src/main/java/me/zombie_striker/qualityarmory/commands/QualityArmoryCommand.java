package me.zombie_striker.qualityarmory.commands;

import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.customitemmanager.CustomItemManager;
import me.zombie_striker.qualityarmory.ConfigKey;
import me.zombie_striker.qualityarmory.MessageKey;
import me.zombie_striker.qualityarmory.QAMain;
import me.zombie_striker.qualityarmory.api.QualityArmory;
import me.zombie_striker.qualityarmory.api.events.QAGunGiveEvent;
import me.zombie_striker.qualityarmory.guns.Ammo;
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
                for (CustomBaseObject e : main.getCustomItems()) {
                    if (b(e.getName(), args[1]))
                        s.add(e.getName());
                }
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
                    sender.sendMessage(main.getPrefix() + " This server is using version " + ChatColor.GREEN
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
                        boolean debug = !(boolean) main.getSetting(ConfigKey.SETTING_DEBUG.getKey());
                        main.setSetting(ConfigKey.SETTING_DEBUG, debug);
                        sender.sendMessage(main.getPrefix() + "Console debugging set to " + debug);
                    } else {
                        sender.sendMessage(main.getPrefix() + ChatColor.RED + main.getMessagesYml().getOrSet(MessageKey.NO_PERM_COMMAND.getKey(), "You do not have permission to use this command."));
                        return true;
                    }
                    return true;

                }

                if (args[0].equalsIgnoreCase("getResourcepack")) {
                    Player player = null;
                    if (args.length > 1) {
                        player = Bukkit.getPlayer(args[1]);
                        if (player == null) {
                            sender.sendMessage(main.getPrefix() + " This player does not exist.");
                            return true;
                        }
                    } else {
                        player = (Player) sender;
                    }
                    player.sendMessage(CustomItemManager.getResourcepack());
                    return true;
                }
                if (args[0].equalsIgnoreCase("reload")) {
                    if (sender.hasPermission("qualityarmory.reload")) {
                        main.reloadConfig();
                        main.reloadVals();
                        sender.sendMessage(main.getPrefix() + main.getMessagesYml().getOrSet(MessageKey.RELOAD_MESSAGE.getKey(), "The plugin has been reloaded."));
                        return true;
                    } else {
                        sender.sendMessage(main.getPrefix() + ChatColor.RED + main.getMessagesYml().getOrSet(MessageKey.NO_PERM_COMMAND.getKey(), "You do not have permission to use this command."));
                        return true;
                    }
                }

                if (args[0].equalsIgnoreCase("drop")) {
                    if (!sender.hasPermission("qualityarmory.drop")) {
                        sender.sendMessage(main.getPrefix() + ChatColor.RED + main.getMessagesYml().getOrSet(MessageKey.NO_PERM_COMMAND.getKey(), "You do not have permission to use this command."));
                        return true;
                    }
                    if (args.length == 1) {
                        sender.sendMessage(main.getPrefix() + " The item name is required. Press [Tab] to autocomplete.");
                        return true;
                    }

                    CustomBaseObject g = QualityArmory.getInstance().getCustomItemByName(args[1]);
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
                            sender.sendMessage(main.getPrefix() + " A valid location is required");
                            return true;
                        }
                        ItemStack temp = null;

                        if (g instanceof Gun) {
                            temp = CustomItemManager.getItemType("gun").getItem(g.getItemData().getMat(), g.getItemData().getData(), g.getItemData().getVariant());
                        } else if (g instanceof Ammo) {
                            temp = CustomItemManager.getItemType("gun").getItem(g.getItemData().getMat(), g.getItemData().getData(), g.getItemData().getVariant());
                        } else {
                            temp = CustomItemManager.getItemType("gun").getItem(g.getItemData().getMat(), g.getItemData().getData(), g.getItemData().getVariant());
                            temp.setAmount(1);
                        }
                        if (temp != null) {
                            loc.getWorld().dropItem(loc, temp);
                            sender.sendMessage(main.getPrefix() + " Dropping item " + g.getName() + " at that location");
                        } else {
                            sender.sendMessage(main.getPrefix() + " Failed to drop item " + g.getName() + " at that location");
                        }
                    } else {
                        sender.sendMessage(main.getPrefix() + " Could not find item \"" + args[1] + "\"");
                    }
                    return true;
                }

                if (args[0].equalsIgnoreCase("give")) {
                    if (!sender.hasPermission("qualityarmory.give")) {
                        sender.sendMessage(main.getPrefix() + ChatColor.RED + main.getMessagesYml().getOrSet(MessageKey.NO_PERM_COMMAND.getKey(), "You do not have permission to use this command."));
                        return true;
                    }
                    if (args.length == 1) {
                        sender.sendMessage(main.getPrefix() + " The item name is required. Press [Tab] to get list.");
                        return true;
                    }

                    CustomBaseObject g = QualityArmory.getInstance().getCustomItemByName(args[1]);
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
                            int amount = (int) g.getData(ConfigKey.CUSTOMITEM_MAX_ITEM_STACK.getKey());
                            if (args.length > 3)
                                amount = Integer.parseInt(args[3]);
                            QualityArmory.getInstance().addAmmoToInventory(who, (Ammo) g, amount);
                        } else {
                            temp = CustomItemManager.getItemType("gun").getItem(g.getItemData().getMat(), g.getItemData().getData(), g.getItemData().getVariant());
                            temp.setAmount(1);
                            who.getInventory().addItem(temp);
                        }

                        sender.sendMessage(main.getPrefix() + " Adding " + g.getName() + " to " + (sender == who ? "your" : who.getName() + "'s") + " inventory");
                    } else {
                        sender.sendMessage(main.getPrefix() + " Could not find item \"" + args[1] + "\"");
                    }
                    return true;
                }
            }
            if (sender instanceof Player) {
                final Player player = (Player) sender;
                if (args.length == 0) {
                    sendHelp(player);
                    return true;
                }
               // if (enableCrafting)
                    if (args[0].equalsIgnoreCase("craft")) {
                        if (!sender.hasPermission("qualityarmory.craft")) {
                            sender.sendMessage(main.getPrefix() + ChatColor.RED + main.getMessagesYml().getOrSet(MessageKey.NO_PERM_COMMAND.getKey(), "You do not have permission to use this command."));
                            return true;
                        }
                       // player.openInventory(createCraft(0));
                        return true;

                    }
                //if (enableShop)
                    if (args[0].equalsIgnoreCase("shop")) {
                        if (args.length == 2 && sender.hasPermission("qualityarmory.shop.other")) {
                            Player target = Bukkit.getPlayer(args[1]);
                            if (target == null) {
                                sender.sendMessage(main.getPrefix() + ChatColor.RED + "That player is not online");
                                return true;
                            }

                          //  target.openInventory(createShop(0));
                            return true;
                        }

                        if (!sender.hasPermission("qualityarmory.shop")) {
                            sender.sendMessage(main.getPrefix() + ChatColor.RED + main.getMessagesYml().getOrSet(MessageKey.NO_PERM_COMMAND.getKey(), "You do not have permission to use this command."));
                            return true;
                        }
                        //player.openInventory(createShop(0));
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
