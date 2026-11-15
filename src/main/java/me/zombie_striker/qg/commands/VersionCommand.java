package me.zombie_striker.qg.commands;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.utils.ErrorReporter;
import me.zombie_striker.qg.utils.LocalUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

final class VersionCommand extends SubCommand {
    private String changelog;

    VersionCommand() {
        super("version");
    }

    @Override
    public boolean isSuggested(CommandSender sender) {
        return true;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        sender.sendMessage(QAMain.getMessages().general.prefix + ChatColor.WHITE + " This server is using version " + ChatColor.GREEN
                + QAMain.getInstance().getDescription().getVersion() + ChatColor.WHITE + " of QualityArmory");
        sender.sendMessage("--==Changelog==--");
        if (changelog == null)
            changelog = LocalUtils.colorize(readChangelog());
        sender.sendMessage(changelog);
        return true;
    }

    /**
     * @return The first lines of the bundled changelog.
     */
    private static String readChangelog() {
        StringBuilder builder = new StringBuilder();
        InputStream in = VersionCommand.class.getResourceAsStream("/changelog.txt");
        if (in == null) return "";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            for (int i = 0; i < 7; i++) {
                String line = reader.readLine();
                if (line == null || line.length() <= 1)
                    break;
                if (i == 6) {
                    builder.append("......\n");
                    break;
                }
                builder.append("\n").append(line);
            }
        } catch (IOException e) {
            ErrorReporter.report("reading the changelog", e);
        }
        return builder.toString();
    }
}
