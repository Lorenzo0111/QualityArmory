package me.zombie_striker.qualityarmory.handlers;

import me.zombie_striker.qualityarmory.ConfigKey;
import me.zombie_striker.qualityarmory.MessageKey;
import me.zombie_striker.qualityarmory.QAMain;
import me.zombie_striker.qualityarmory.api.QualityArmory;
import me.zombie_striker.qualityarmory.interfaces.IHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

public class ResourcePackHandler implements IHandler, Listener {
    private QAMain main;
    @EventHandler
    public void onResourcepackStatus(PlayerResourcePackStatusEvent event){
        if((boolean)main.getSettingIfPresent(ConfigKey.SETTING_KICK_IF_RESOURCEPACK_NOT_ACCEPT.getKey(), true)){
            event.getPlayer().kickPlayer((String) main.getMessagesYml().getOrSet(MessageKey.KICK_ON_DECLINE_RESOURCEPACK.getKey(), "You have been kicked because you declined the resourcepack. Make sure you accept the resourcepack to join."));
        }
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        QualityArmory.getInstance().sendResourcepack(event.getPlayer(),false);
    }
    @Override
    public void init(QAMain main) {
        this.main = main;
        Bukkit.getPluginManager().registerEvents(this,main);
    }
}
