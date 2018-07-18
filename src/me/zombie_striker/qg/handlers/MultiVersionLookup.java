package me.zombie_striker.qg.handlers;

import org.bukkit.Material;
import org.bukkit.Sound;

public class MultiVersionLookup {

	private static Material wood;
	private static Material glasspane;
	private static Material gunpowderr;
	private static Material skull;
	private static Material mycil;
	private static Material wool;
	private static Material goldpickaxe;
	private static Material goldshovel;
	private static Material goldhoe;
	private static Material ironshovel;
	private static Material ink;

	private static Sound noteHarp;
	private static Sound woolsound;
	private static Sound enderdrag;
	private static Sound pliung;

	public static Sound getPling() {
		if (pliung == null) {
			pliung = Sound.valueOf("BLOCK_NOTE_PLING");
			if (pliung == null)
				pliung = Sound.BLOCK_NOTE_BLOCK_PLING;
		}
		return pliung;
	}

	public static Sound getDragonGrowl() {
		if (enderdrag == null) {
			enderdrag = Sound.valueOf("ENTITY_ENDERDRAGON_GROWL");
			if (enderdrag == null)
				enderdrag = Sound.ENTITY_ENDER_DRAGON_GROWL;
		}
		return enderdrag;
	}

	public static Sound getWoolSound() {
		if (woolsound == null) {
			woolsound = Sound.valueOf("BLOCK_CLOTH_BREAK");
			if (woolsound == null)
				woolsound = Sound.BLOCK_WOOL_BREAK;
		}
		return woolsound;
	}

	public static Sound getHarp() {
		if (noteHarp == null) {
			noteHarp = Sound.valueOf("BLOCK_NOTE_HARP");
			if (noteHarp == null)
				noteHarp = Sound.BLOCK_NOTE_BLOCK_HARP;
		}
		return noteHarp;
	}

	
	public static Material getINKSAC() {
		if (ink == null) {
			ink = Material.matchMaterial("INK_SACK");
			if (ink == null)
				ink = Material.INK_SAC;
		}
		return ink;
	}

	public static Material getIronShovel() {
		if (ironshovel == null) {
			ironshovel = Material.matchMaterial("IRON_SPADE");
			if (ironshovel == null)
				ironshovel = Material.IRON_SHOVEL;
		}
		return ironshovel;
	}

	public static Material getGoldHoe() {
		if (goldhoe == null) {
			goldhoe = Material.matchMaterial("GOLD_HOE");
			if (goldhoe == null)
				goldhoe = Material.GOLDEN_HOE;
		}
		return goldhoe;
	}

	public static Material getGoldShovel() {
		if (goldshovel == null) {
			goldshovel = Material.matchMaterial("GOLD_SPADE");
			if (goldshovel == null)
				goldshovel = Material.GOLDEN_SHOVEL;
		}
		return goldshovel;
	}

	public static Material getGoldPick() {
		if (goldpickaxe == null) {
			goldpickaxe = Material.matchMaterial("GOLD_PICKAXE");
			if (goldpickaxe == null)
				goldpickaxe = Material.GOLDEN_PICKAXE;
		}
		return goldpickaxe;
	}

	public static Material getWool() {
		if (wool == null) {
			wool = Material.matchMaterial("WOOL");
			if (wool == null)
				wool = Material.WHITE_WOOL;
		}
		return wool;
	}

	public static Material getWood() {
		if (wood == null) {
			wood = Material.matchMaterial("WOOD");
			if (wood == null)
				wood = Material.OAK_PLANKS;
		}
		return wood;
	}

	public static Material getGlass() {
		if (glasspane == null) {
			glasspane = Material.matchMaterial("STAINED_GLASS_PANE");
			if (glasspane == null)
				glasspane = Material.YELLOW_STAINED_GLASS_PANE;
		}
		return glasspane;
	}

	public static Material getGunpowder() {
		if (gunpowderr == null) {
			gunpowderr = Material.matchMaterial("SULPHUR");
			if (gunpowderr == null)
				try {
					gunpowderr = Material.GUNPOWDER;
				} catch (Error | Exception e45) {
				}
		}
		return gunpowderr;
	}

	public static Material getSkull() {
		if (skull == null) {
			skull = Material.matchMaterial("SKULL_ITEM");
			if (skull == null)
				skull = Material.PLAYER_HEAD;
		}
		return skull;
	}

	public static Material getMycil() {
		if (mycil == null) {
			mycil = Material.matchMaterial("MYCEL");
			if (mycil == null)
				mycil = Material.MYCELIUM;
		}
		return mycil;
	}
}
