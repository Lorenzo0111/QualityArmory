package me.zombie_striker.qg.handlers;

import me.zombie_striker.qg.QAMain;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.HashMap;

public class BlockCollisionUtil {

	private static final HashMap<Material,Double> customBlockHeights = new HashMap<>();

	static{
		for(Material m : Material.values()){
			if(m.name().endsWith("_WALL"))
				customBlockHeights.put(m,1.5);
			if(m.name().endsWith("_FENCE_GATE")||m.name().endsWith("_FENCE"))
				customBlockHeights.put(m,1.5);
			if(m.name().endsWith("_BED"))
				customBlockHeights.put(m,0.5);
			if(m.name().endsWith("_SLAB")||m.name().endsWith("_FENCE"))
				customBlockHeights.put(m,0.5);
			if(m.name().endsWith("DAYLIGHT_DETECTOR"))
				customBlockHeights.put(m,0.4);
			if(m.name().endsWith("CARPET"))
				customBlockHeights.put(m,0.1);
			if(m.name().endsWith("TRAPDOOR"))
				customBlockHeights.put(m,0.2);
		}
	}

	public static double getHeight(Block b){
		Material type = b.getType();
		if (b.getType().name().contains("SLAB") || b.getType().name().contains("STEP")) {
			if (b.getData() == 0)
				return 0.5;
			if (b.getData() == 1)
				return 1;
		}
		if(customBlockHeights.containsKey(type))
			return customBlockHeights.get(type);
		return type.isSolid()?1:0;
	}

	public static boolean isSolidAt(Block b, Location loc){
		if(b.getLocation().getY()+getHeight(b)>loc.getY())
			return true;
		Block temp = b.getRelative(0,-1,0);
		if(temp.getLocation().getY()+getHeight(temp)>loc.getY())
			return true;
		return false;
	}


	public static boolean isSolid(Block b, Location l) {
		if(b.getType().name().contains("SIGN"))
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
			if (!QAMain.blockbullet_halfslabs && ((l.getY() - l.getBlockY() > 0.5 && b.getData() == 0)
					|| (l.getY() - l.getBlockY() <= 0.5 && b.getData() == 1)))
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
