package me.zombie_striker.qg.handlers;

import com.cryptomorin.xseries.XSound;
import org.bukkit.Sound;
import org.bukkit.block.Block;

public class SoundHandler {

	public static Sound getSoundWhenShot(Block b) {
		if(b.getType().name().contains("GLASS"))
			return XSound.BLOCK_GLASS_BREAK.parseSound();
		if(b.getType().name().contains("STONE"))
			return XSound.BLOCK_STONE_BREAK.parseSound();
		if(b.getType().name().contains("WOOD"))
			return XSound.BLOCK_WOOD_BREAK.parseSound();
		if(b.getType().name().contains("SNOW"))
			return XSound.BLOCK_SNOW_BREAK.parseSound();
		if(b.getType().name().contains("SAND"))
			return XSound.BLOCK_SAND_BREAK.parseSound();
		if(b.getType().name().contains("WOOL"))
			return MultiVersionLookup.getWoolSound();
		return XSound.BLOCK_WOOD_BREAK.parseSound();
	}
}
