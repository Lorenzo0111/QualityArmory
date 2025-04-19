package me.zombie_striker.qg.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

public class BlockRegenData {
    private final Material material;
    private Object data;

    public BlockRegenData(Block block) {
        this.material = block.getType();
        try {
            this.data = block.getBlockData();
        } catch (Exception | Error e) {
            this.data = null;
        }
    }

    public void place(Location location) {
        location.getBlock().setType(material);

        try {
            location.getBlock().setBlockData((BlockData) data);
        } catch (Exception | Error ignored) {}
    }
}
