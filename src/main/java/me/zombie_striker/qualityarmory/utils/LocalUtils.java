package me.zombie_striker.qualityarmory.utils;

import org.bukkit.ChatColor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LocalUtils {
    private static boolean hexColorSupport;

    static {
        try {
            Class<?> CLASS_CHAT_COLOR_BUNGEE = Class.forName("net.md_5.bungee.api.ChatColor");
            CLASS_CHAT_COLOR_BUNGEE.getDeclaredMethod("of", String.class);
            hexColorSupport = true;
        }catch (Throwable e) {
            hexColorSupport = false;
        }
    }

    private static String translateAlternateHexColorCodes(Character c,String string){
        string = ChatColor.translateAlternateColorCodes(c, string);
        if(string.length() < 7 || !string.contains("#")) return string;
        Matcher matcher = Pattern.compile("&#[A-Fa-f0-9]{6}").matcher(string);
        while (matcher.find()) {
            String match = matcher.group(0);
            string = string.replace(match, net.md_5.bungee.api.ChatColor.of(match).toString());
        }
        return string;
    }

    public static String colorize(String string){
        return hexColorSupport ? translateAlternateHexColorCodes('&',string) : ChatColor.translateAlternateColorCodes('&', string);
    }

    public static List<String> colorize(List<String> strings){
        return strings.stream().map(LocalUtils::colorize).collect(Collectors.toList());
    }

    public static String[] colorize(String... strings){
        return Stream.of(strings).map(LocalUtils::colorize).toArray(String[]::new);
    }

    public static boolean isHexColorSupported() {
        return hexColorSupport;
    }
}