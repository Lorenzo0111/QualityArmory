package me.zombie_striker.qg.handlers;

import com.cryptomorin.xseries.XEntityType;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;

/**
 * Materials, sounds and entity types that were renamed across Minecraft versions.
 * XSeries resolves the name for the running server and caches it.
 */
public final class MultiVersionLookup {

	private MultiVersionLookup() {
	}

	public static Sound getPling() {
		return XSound.BLOCK_NOTE_BLOCK_PLING.get();
	}

	public static Sound getDragonGrowl() {
		return XSound.ENTITY_ENDER_DRAGON_GROWL.get();
	}

	public static Sound getWoolSound() {
		return XSound.BLOCK_WOOL_BREAK.get();
	}

	public static Sound getHarp() {
		return XSound.BLOCK_NOTE_BLOCK_HARP.get();
	}

	public static Material getINKSAC() {
		return XMaterial.INK_SAC.get();
	}

	public static Material getIronShovel() {
		return XMaterial.IRON_SHOVEL.get();
	}

	public static Material getCarrotOnAStick() {
		return XMaterial.CARROT_ON_A_STICK.get();
	}

	public static Material getGoldHoe() {
		return XMaterial.GOLDEN_HOE.get();
	}

	public static Material getGoldShovel() {
		return XMaterial.GOLDEN_SHOVEL.get();
	}

	public static Material getGoldPick() {
		return XMaterial.GOLDEN_PICKAXE.get();
	}

	public static Material getGrassPath() {
		return XMaterial.DIRT_PATH.get();
	}

	public static Material getWool() {
		return XMaterial.WHITE_WOOL.get();
	}

	public static Material getWood() {
		return XMaterial.OAK_PLANKS.get();
	}

	public static Material getGlass() {
		return XMaterial.YELLOW_STAINED_GLASS_PANE.get();
	}

	public static Material getGunpowder() {
		return XMaterial.GUNPOWDER.get();
	}

	public static Material getSkull() {
		return XMaterial.PLAYER_HEAD.get();
	}

	public static Material getMycil() {
		return XMaterial.MYCELIUM.get();
	}

	public static EntityType getZombiePig() {
		return XEntityType.ZOMBIFIED_PIGLIN.get();
	}
}
