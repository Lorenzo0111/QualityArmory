package me.zombie_striker.qg.utils;

import me.zombie_striker.qg.utils.Compat;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

public class BlockRegenData {
    private final Material material;
    private Object data;

    public BlockRegenData(Block block) {
        this.material = block.getType();
        // Block data exists from 1.13; older versions only restore the material
        this.data = Compat.FLATTENED ? block.getBlockData() : null;
    }

    public void place(Location location) {
        location.getBlock().setType(material);

        if (data != null)
            location.getBlock().setBlockData((BlockData) data);
    }
}
