package me.zombie_striker.qg.handlers;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import me.zombie_striker.qg.QAMain;

public class BlockCollisionUtil {

    private static final HashMap<Material, Double> customBlockHeights = new HashMap<>();

    static {
        for (final Material m : Material.values()) {
            if (m.name().endsWith("_WALL"))
                BlockCollisionUtil.customBlockHeights.put(m, 1.5);
            if (m.name().endsWith("_FENCE_GATE") || m.name().endsWith("_FENCE"))
                BlockCollisionUtil.customBlockHeights.put(m, 1.5);
            if (m.name().endsWith("_BED"))
                BlockCollisionUtil.customBlockHeights.put(m, 0.5);
            if (m.name().endsWith("_SLAB") || m.name().endsWith("_FENCE"))
                BlockCollisionUtil.customBlockHeights.put(m, 0.5);
            if (m.name().endsWith("DAYLIGHT_DETECTOR"))
                BlockCollisionUtil.customBlockHeights.put(m, 0.4);
            if (m.name().endsWith("CARPET"))
                BlockCollisionUtil.customBlockHeights.put(m, 0.1);
            if (m.name().endsWith("TRAPDOOR"))
                BlockCollisionUtil.customBlockHeights.put(m, 0.2);
        }
    }

    @SuppressWarnings("deprecation")
    public static double getHeight(final Block b) {
        final Material type = b.getType();
        if (b.getType().name().contains("SLAB") || b.getType().name().contains("STEP")) {
            if (b.getData() == 0)
                return 0.5;
            if (b.getData() == 1)
                return 1;
        }
        if (BlockCollisionUtil.customBlockHeights.containsKey(type))
            return BlockCollisionUtil.customBlockHeights.get(type);
        return type.isSolid() ? 1 : 0;
    }

    public static boolean isSolidAt(final Block b, final Location loc) {
        if (b.getLocation().getY() + BlockCollisionUtil.getHeight(b) > loc.getY())
            return true;
        final Block temp = b.getRelative(0, -1, 0);
        if (temp.getLocation().getY() + BlockCollisionUtil.getHeight(temp) > loc.getY())
            return true;
        return false;
    }

    @SuppressWarnings("deprecation")
    public static boolean isSolid(final Block b, final Location l) {
        if (b.getType().name().equals("SNOW"))
            return false;
        if (b.getType().name().contains("SIGN"))
            return false;
        if (b.getType().name().endsWith("CARPET")) {
            return false;
        }
        if (b.getType() == Material.WATER) {
            if (QAMain.blockbullet_water)
                return true;
        }
        if (b.getType().name().contains("LEAVE")) {
            if (QAMain.blockbullet_leaves)
                return true;
        }
        if (b.getType().name().contains("SLAB") || b.getType().name().contains("STEP")) {
            if (!QAMain.blockbullet_halfslabs
                    && ((l.getY() - l.getBlockY() > 0.5 && b.getData() == 0) || (l.getY() - l.getBlockY() <= 0.5 && b.getData() == 1)))
                return false;
            return true;
        }
        if (b.getType().name().contains("BED_") || b.getType().name().contains("_BED")
                || b.getType().name().contains("DAYLIGHT_DETECTOR")) {
            if (!QAMain.blockbullet_halfslabs && (l.getY() - l.getBlockY() > 0.5))
                return false;
            return true;
        }
        if (b.getType().name().contains("DOOR")) {
            if (QAMain.blockbullet_door)
                return true;
            return false;
        }
        if (b.getType().name().contains("GLASS")) {
            if (QAMain.blockbullet_glass)
                return true;
            return false;
        }

        if (b.getType().name().contains("STAIR")) {
            if (b.getData() < 4 && (l.getY() - l.getBlockY() < 0.5))
                return true;
            if (b.getData() >= 4 && (l.getY() - l.getBlockY() > 0.5))
                return true;
            switch (b.getData()) {
            case 0:
            case 4:
                return l.getX() - (0.5 + l.getBlockX()) > 0;
            case 1:
            case 5:
                return l.getX() - (0.5 + l.getBlockX()) < 0;
            case 2:
            case 6:
                return l.getZ() - (0.5 + l.getBlockZ()) > 0;
            case 3:
            case 7:
                return l.getZ() - (0.5 + l.getBlockZ()) < 0;

            }
        }

        if (b.getType().name().endsWith("FERN")) {
            return false;
        }
        if (b.getType().isOccluding()) {
            return true;
        }
        return false;
    }
}
