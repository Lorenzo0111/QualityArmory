package me.zombie_striker.qg.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import me.zombie_striker.qg.ammo.Ammo;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.guns.utils.WeaponType;
import me.zombie_striker.qg.handlers.MultiVersionLookup;

public class ArmoryYML {

	FileConfiguration fileConfig;
	File save;
	public boolean saveNow = false;

	public ArmoryYML(File file) {
		save = file;
		if (!file.getParentFile().exists())
			file.getParentFile().mkdirs();
		if (!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		fileConfig = CommentYamlConfiguration.loadConfiguration(file);
	}

	protected void setNoOverride(String name, Object v) {
		if (!fileConfig.contains(name)) {
			fileConfig.set(name, v);
			saveNow = true;
		}
	}

	public void setNoSave(boolean force, String name, Object v) {
		if (!fileConfig.contains(name)
				|| ((fileConfig.getBoolean("allowUpdates") || force) && !fileConfig.get(name).equals(v))) {
			fileConfig.set(name, v);
			saveNow = true;
		}
	}

	public void set(boolean force, String name, Object v) {
		if (!fileConfig.contains(name)
				|| ((fileConfig.getBoolean("allowUpdates") || force) && !fileConfig.get(name).equals(v))) {
			fileConfig.set(name, v);
			saveNow = true;
		}
	}

	public void done() {
		verifyAllTagsExist();
		if (saveNow)
			save();
	}

	public void save() {
		try {
			fileConfig.save(save);
			saveNow = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	public ArmoryYML setSkullType(String skullowner) {
		set(false, "id", org.bukkit.SkullType.PLAYER.ordinal());
		set(false, "material", MultiVersionLookup.getSkull().name());
		set(false, "skull_owner", skullowner);
		set(false, "skull_owner_custom_url", Ammo.NO_SKIN_STRING);
		return this;
	}

	public ArmoryYML setInvalid(boolean invalid) {
		set(false, "invalid", invalid);
		return this;
	}

	public ArmoryYML setLore(List<String> lore) {
		set(false, "lore", lore);
		return this;
	}

	public ArmoryYML setVariant(int var) {
		set(false, "variant", var);
		return this;
	}

	public ArmoryYML setDurability(int durib) {
		set(false, "durability", durib);
		return this;
	}

	public ArmoryYML setPrice(int cost) {
		set(false, "price", cost);
		return this;
	}
	public ArmoryYML setMaterial(Material mat) {
		//h.setNoSave(false, "material", Material.DIAMOND_AXE.name());
		set(false,  "material", mat.name());
		return this;
	}

	public void verifyAllTagsExist() {
		setNoOverride("allowUpdates", true);
		setNoOverride("invalid", false);
		setNoOverride("lore", new ArrayList<String>());
		setNoOverride("material", Material.DIAMOND_AXE.name());
		setNoOverride("variant", 0);
		setNoOverride("weapontype", WeaponType.RIFLE);
		setNoOverride("weaponsounds", WeaponSounds.getSoundByType(WeaponType.RIFLE));
		//StringBuilder validGuns = new StringBuilder();
		//for (WeaponType g : WeaponType.values()) {
		//	validGuns.append(g.name() + ", ");
		//}
		//setNoOverride("_VALID_WEAPON_TYPES", validGuns.toString());
		setNoOverride("damage", 3);
		
		setNoOverride("durability", 1000);
		
		setNoOverride("price", 1000);
	}
}
