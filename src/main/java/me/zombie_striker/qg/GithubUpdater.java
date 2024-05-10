package me.zombie_striker.qg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GithubUpdater {

    public static boolean autoUpdate(final Plugin main, final String author, final String githubProject, final String jarname) {
        if (main.getDescription().getVersion().endsWith("SNAPSHOT")) {
            QAMain.getInstance().getLogger()
                    .info("[Updater] You are using a BETA version of the plugin. Updater is disabled, please check updates manually.");
            return false;
        }

        try {
            final String version = main.getDescription().getVersion();
            final String parseVersion = version.replace(".", "");

            String tagname = null;
            final URL api = URI.create("https://api.github.com/repos/" + author + "/" + githubProject + "/releases/latest").toURL();
            final URLConnection con = api.openConnection();
            con.setConnectTimeout(15000);
            con.setReadTimeout(15000);

            JsonObject json = null;
            try {
                json = JsonParser.parseReader(new InputStreamReader(con.getInputStream())).getAsJsonObject();
            } catch (Error | Exception e45) {
                return false;
            }
            tagname = json.get("tag_name").getAsString();

            final String parsedTagName = tagname.replace(".", "");

            final int latestVersion = Integer.parseInt(parsedTagName.substring(1).replaceAll("[^\\d.]", ""));
            final int parsedVersion = Integer.parseInt(parseVersion.replaceAll("[^\\d.]", ""));

            final URL download = URI
                    .create("https://github.com/" + author + "/" + githubProject + "/releases/download/" + tagname + "/" + jarname).toURL();

            if (latestVersion > parsedVersion) {
                Bukkit.getConsoleSender()
                        .sendMessage(ChatColor.GREEN + "Found a new version of " + ChatColor.GOLD + main.getDescription().getName() + ": "
                                + ChatColor.WHITE + tagname + ChatColor.LIGHT_PURPLE + " downloading now!!");

                new BukkitRunnable() {

                    @Override
                    public void run() {
                        try {

                            final InputStream in = download.openStream();

                            File pluginFile = null;

                            try {
                                pluginFile = new File(URLDecoder
                                        .decode(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8"));
                            } catch (final UnsupportedEncodingException e) {
                                throw new RuntimeException("You don't have a good text codec on your system", e);
                            }

                            // File temp = new File("plugins/update");
                            // if (!temp.exists()) {
                            // temp.mkdir();
                            // }

                            final File tempInCaseSomethingGoesWrong = new File(main.getName() + "-backup.jar");
                            GithubUpdater.copy(new FileInputStream(pluginFile), new FileOutputStream(tempInCaseSomethingGoesWrong));

                            // Path path = new File("plugins/update" + File.separator + "COD.jar").toPath();
                            pluginFile.setWritable(true, false);
                            pluginFile.delete();
                            // Files.copy(in, pluginFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            GithubUpdater.copy(in, new FileOutputStream(pluginFile));

                            if (pluginFile.length() < 1000) {
                                // Plugin is too small. Keep old version in case new one is
                                // incomplete/nonexistant
                                GithubUpdater.copy(new FileInputStream(tempInCaseSomethingGoesWrong), new FileOutputStream(pluginFile));
                            } else {
                                // Plugin is valid, and we can delete the temp
                                tempInCaseSomethingGoesWrong.delete();
                            }

                        } catch (final IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.runTaskLaterAsynchronously(main, 0);
                return true;
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static long copy(final InputStream in, final OutputStream out) throws IOException {
        long bytes = 0;
        final byte[] buf = new byte[0x1000];
        while (true) {
            final int r = in.read(buf);
            if (r == -1)
                break;
            out.write(buf, 0, r);
            bytes += r;
            // debug("Another 4K, current: " + r);
        }
        out.flush();
        out.close();
        in.close();
        return bytes;
    }

}
