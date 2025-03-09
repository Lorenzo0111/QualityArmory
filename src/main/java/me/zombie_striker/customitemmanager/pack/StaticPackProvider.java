package me.zombie_striker.customitemmanager.pack;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class StaticPackProvider implements ResourcepackProvider {
    private final String url;

    public StaticPackProvider(String url) {
        this.url = url;
    }

    @Override
    public String getFor(@Nullable Player player) {
        return this.url;
    }

    @Override
    public Object serialize() {
        return url;
    }
}
