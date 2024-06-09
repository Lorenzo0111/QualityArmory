package me.zombie_striker.qg.handlers;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;

public class MultiVersionLookup {

	private static Material wood;
	private static Material glasspane;
	private static Material gunpowderr;
	private static Material skull;
	private static Material mycil;
	private static Material wool;
	private static Material grasspath;
	private static Material goldpickaxe;
	private static Material goldshovel;
	private static Material goldhoe;
	private static Material ironshovel;
	private static Material ink;

	private static Material carrotstick;
	
	private static Sound noteHarp;
	private static Sound woolsound;
	private static Sound enderdrag;
	private static Sound pliung;

	private static EntityType zombiePig;

	public static Sound getPling() {
		if (pliung == null) {
			try {
				pliung = Sound.valueOf("BLOCK_NOTE_PLING");
			} catch (Error | Exception e) {
			}
			if (pliung == null)
				pliung = Sound.BLOCK_NOTE_BLOCK_PLING;
		}
		return pliung;
	}

	public static Sound getDragonGrowl() {
		if (enderdrag == null) {
			try {
				enderdrag = Sound.valueOf("ENTITY_ENDERDRAGON_GROWL");
			} catch (Error | Exception e) {
			}
			if (enderdrag == null)
				enderdrag = Sound.ENTITY_ENDER_DRAGON_GROWL;
		}
		return enderdrag;
	}

	public static Sound getWoolSound() {
		if (woolsound == null) {
			try {
				woolsound = Sound.valueOf("BLOCK_CLOTH_BREAK");
			} catch (Error | Exception e) {
			}
			if (woolsound == null)
				woolsound = Sound.BLOCK_WOOL_BREAK;
		}
		return woolsound;
	}

	public static Sound getHarp() {
		if (noteHarp == null) {
			try {
				noteHarp = Sound.valueOf("BLOCK_NOTE_HARP");
			} catch (Error | Exception e) {
			}
			if (noteHarp == null)
				noteHarp = Sound.BLOCK_NOTE_BLOCK_HARP;
		}
		return noteHarp;
	}

	public static Material getINKSAC() {
		if (ink == null) {
			try {
				ink = Material.matchMaterial("INK_SACK");
			} catch (Error | Exception e) {
			}
			if (ink == null)
				ink = Material.INK_SAC;
		}
		return ink;
	}

	public static Material getIronShovel() {
		if (ironshovel == null) {
			try {
				ironshovel = Material.matchMaterial("IRON_SPADE");
			} catch (Error | Exception e) {
			}
			if (ironshovel == null)
				ironshovel = Material.IRON_SHOVEL;
		}
		return ironshovel;
	}

	public static Material getCarrotOnAStick() {
		if (carrotstick == null) {
			try {
				carrotstick = Material.matchMaterial("CARROT_STICK");
			} catch (Error | Exception e) {
			}
			if (carrotstick == null)
				carrotstick = Material.CARROT_ON_A_STICK;
		}
		return carrotstick;
	}
	public static Material getGoldHoe() {
		if (goldhoe == null) {
			try {
				goldhoe = Material.matchMaterial("GOLD_HOE");
			} catch (Error | Exception e) {
			}
			if (goldhoe == null)
				goldhoe = Material.GOLDEN_HOE;
		}
		return goldhoe;
	}

	public static Material getGoldShovel() {
		if (goldshovel == null) {
			try {
				goldshovel = Material.matchMaterial("GOLD_SPADE");
			} catch (Error | Exception e) {
			}
			if (goldshovel == null)
				goldshovel = Material.GOLDEN_SHOVEL;
		}
		return goldshovel;
	}

	public static Material getGoldPick() {
		if (goldpickaxe == null) {
			try {
				goldpickaxe = Material.matchMaterial("GOLD_PICKAXE");
			} catch (Error | Exception e) {
			}
			if (goldpickaxe == null)
				goldpickaxe = Material.GOLDEN_PICKAXE;
		}
		return goldpickaxe;
	}

	public static Material getGrassPath() {
		if (grasspath == null) {
			try {
				grasspath = Material.matchMaterial("GRASS_PATH");
			} catch (Error | Exception e) {
			}
			if (grasspath == null)
				grasspath = Material.DIRT_PATH;
		}
		return grasspath;
	}

	public static Material getWool() {
		if (wool == null) {
			try {
				wool = Material.matchMaterial("WOOL");
			} catch (Error | Exception e) {
			}
			if (wool == null)
				wool = Material.WHITE_WOOL;
		}
		return wool;
	}

	public static Material getWood() {
		if (wood == null) {
			try {
				wood = Material.matchMaterial("WOOD");
			} catch (Error | Exception e) {
			}
			if (wood == null)
				wood = Material.OAK_PLANKS;
		}
		return wood;
	}

	public static Material getGlass() {
		if (glasspane == null) {
			try {
				glasspane = Material.matchMaterial("STAINED_GLASS_PANE");
			} catch (Error | Exception e) {
			}
			if (glasspane == null)
				glasspane = Material.YELLOW_STAINED_GLASS_PANE;
		}
		return glasspane;
	}

	public static Material getGunpowder() {
		if (gunpowderr == null) {
			try {
				gunpowderr = Material.matchMaterial("SULPHUR");
			} catch (Error | Exception e) {
			}
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
			try {
				skull = Material.matchMaterial("SKULL_ITEM");
			} catch (Error | Exception e) {
			}
			if (skull == null)
				skull = Material.PLAYER_HEAD;
		}
		return skull;
	}

	public static Material getMycil() {
		if (mycil == null) {
			try {
				mycil = Material.matchMaterial("MYCEL");
			} catch (Error | Exception e) {
			}
			if (mycil == null)
				mycil = Material.MYCELIUM;
		}
		return mycil;
	}

	public static EntityType getZombiePig() {
		if (zombiePig == null) {
			try {
				zombiePig = EntityType.valueOf("PIG_ZOMBIE");
			} catch (Error | Exception e) {
			}
			if (zombiePig == null)
				zombiePig = EntityType.ZOMBIFIED_PIGLIN;
		}
		return zombiePig;
	}
}
