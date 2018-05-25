package me.zombie_striker.qg.handlers;

import org.bukkit.Sound;
import org.bukkit.block.Block;

public class SoundHandler {

	public static Sound getSoundWhenShot(Block b) {
		if(b.getType().name().contains("GLASS"))
			return Sound.BLOCK_GLASS_BREAK;
		if(b.getType().name().contains("STONE"))
			return Sound.BLOCK_STONE_BREAK;
		if(b.getType().name().contains("WOOD"))
			return Sound.BLOCK_WOOD_BREAK;
		if(b.getType().name().contains("SNOW"))
			return Sound.BLOCK_SNOW_BREAK;
		if(b.getType().name().contains("SAND"))
			return Sound.BLOCK_SAND_BREAK;
		if(b.getType().name().contains("WOOL"))
			return Sound.BLOCK_CLOTH_BREAK;
		return Sound.BLOCK_WOOD_BREAK;
	}
}
