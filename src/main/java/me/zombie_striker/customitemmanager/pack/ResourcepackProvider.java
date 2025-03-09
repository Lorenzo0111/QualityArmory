package me.zombie_striker.customitemmanager.pack;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public interface ResourcepackProvider {

    String getFor(@Nullable Player player);
    Object serialize();

}
