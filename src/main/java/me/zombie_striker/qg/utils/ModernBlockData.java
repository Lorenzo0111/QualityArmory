package me.zombie_striker.qg.utils;

import org.bukkit.block.Block;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;

public final class ModernBlockData {

    public static int getSlabData(Block b) {
        BlockData data = b.getBlockData();
        if (!(data instanceof Slab))
            return -1;
        switch (((Slab) data).getType()) {
            case BOTTOM:
                return 0;
            case TOP:
                return 1;
            default:
                return -1;
        }
    }

    public static int getStairData(Block b) {
        BlockData data = b.getBlockData();
        if (!(data instanceof Stairs))
            return -1;
        Stairs stairs = (Stairs) data;
        int facing;
        switch (stairs.getFacing()) {
            case EAST:
                facing = 0;
                break;
            case WEST:
                facing = 1;
                break;
            case SOUTH:
                facing = 2;
                break;
            default:
                facing = 3;
                break;
        }
        return stairs.getHalf() == Bisected.Half.TOP ? facing + 4 : facing;
    }
}