package me.zombie_striker.qg.utils;

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
            final Class<?> CLASS_CHAT_COLOR_BUNGEE = Class.forName("net.md_5.bungee.api.ChatColor");
            CLASS_CHAT_COLOR_BUNGEE.getDeclaredMethod("of", String.class);
            LocalUtils.hexColorSupport = true;
        } catch (final Throwable e) {
            LocalUtils.hexColorSupport = false;
        }
    }

    private static String translateAlternateHexColorCodes(final Character c, String string) {
        string = ChatColor.translateAlternateColorCodes(c, string);
        if (string.length() < 7 || !string.contains("#"))
            return string;
        final Matcher matcher = Pattern.compile("&#[A-Fa-f0-9]{6}").matcher(string);
        while (matcher.find()) {
            final String match = matcher.group(0);
            string = string.replace(match, net.md_5.bungee.api.ChatColor.of(match).toString());
        }
        return string;
    }

    public static String colorize(final String string) {
        return LocalUtils.hexColorSupport ? LocalUtils.translateAlternateHexColorCodes('&', string)
                : ChatColor.translateAlternateColorCodes('&', string);
    }

    public static List<String> colorize(final List<String> strings) {
        return strings.stream().map(LocalUtils::colorize).collect(Collectors.toList());
    }

    public static String[] colorize(final String... strings) { return Stream.of(strings).map(LocalUtils::colorize).toArray(String[]::new); }

    public static boolean isHexColorSupported() { return LocalUtils.hexColorSupport; }
}