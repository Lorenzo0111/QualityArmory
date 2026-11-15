package me.zombie_striker.qg.state;

import me.zombie_striker.qg.handlers.EconHandler;
import me.zombie_striker.qg.handlers.ProtocolLibHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

/**
 * Which optional plugins are installed. Detected on enable and on every reload.
 */
public final class Integrations {
    private boolean parties;
    private boolean protocolLib;
    private boolean viaVersion;
    private boolean viaRewind;
    private boolean economy;
    private boolean protocolLibListenersRegistered;

    public void detect() {
        PluginManager plugins = Bukkit.getPluginManager();
        this.parties = plugins.isPluginEnabled("Parties");
        this.viaRewind = plugins.isPluginEnabled("ViaRewind");
        this.viaVersion = plugins.isPluginEnabled("ViaVersion");
        this.protocolLib = plugins.isPluginEnabled("ProtocolLib");

        // Packet listeners stay registered across reloads, so they must only be added once
        if (this.protocolLib && !this.protocolLibListenersRegistered) {
            ProtocolLibHandler.initRemoveArmswing();
            ProtocolLibHandler.initAimBow();
            this.protocolLibListenersRegistered = true;
        }

        this.setupEconomy();
    }

    /**
     * Hooks into Vault. Called again from the shop if the economy plugin loaded after QualityArmory.
     *
     * @return Whether an economy is available.
     */
    public boolean setupEconomy() {
        try {
            this.economy = EconHandler.setupEconomy();
        } catch (LinkageError e) {
            // Vault is not installed
            this.economy = false;
        }
        return this.economy;
    }

    public boolean hasParties() {
        return parties;
    }

    public boolean hasProtocolLib() {
        return protocolLib;
    }

    public boolean hasViaVersion() {
        return viaVersion;
    }

    public boolean hasViaRewind() {
        return viaRewind;
    }

    public boolean hasEconomy() {
        return economy;
    }
}
