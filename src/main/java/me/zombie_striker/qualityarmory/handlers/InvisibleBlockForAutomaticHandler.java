package me.zombie_striker.qualityarmory.handlers;

import me.zombie_striker.qualityarmory.QAMain;
import me.zombie_striker.qualityarmory.api.QualityArmory;
import me.zombie_striker.qualityarmory.interfaces.IHandler;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.UUID;

public class InvisibleBlockForAutomaticHandler implements Listener, IHandler {

    private final HashMap<UUID, Location> fakeBlock = new HashMap<>();

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Block targetblock = event.getPlayer().getTargetBlock(null,4);
        if (!QualityArmory.getInstance().isGun(event.getPlayer().getInventory().getItemInMainHand())) {
            if (fakeBlock.containsKey(event.getPlayer().getUniqueId())) {
                Location prev = fakeBlock.remove(event.getPlayer().getUniqueId());
                event.getPlayer().sendBlockChange(prev, prev.getBlock().getBlockData());
            }
        }else {
            if (targetblock.getType() != Material.AIR)
                return;
            if (fakeBlock.containsKey(event.getPlayer().getUniqueId())) {
                Location prev = fakeBlock.get(event.getPlayer().getUniqueId());
                if (targetblock.getLocation() != prev) {
                    event.getPlayer().sendBlockChange(prev, prev.getBlock().getBlockData());
                } else {
                    return;
                }
            }
            fakeBlock.put(event.getPlayer().getUniqueId(), targetblock.getLocation());
            event.getPlayer().sendBlockChange(targetblock.getLocation(), Material.BARRIER.createBlockData());
        }
    }

    @Override
    public void init(QAMain main) {
        Bukkit.getPluginManager().registerEvents(this,main);
    }
}
