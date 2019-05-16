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
	
	boolean overrideVerify = false;

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

	public Object get(String path) {
		return fileConfig.get(path);
	}

	public boolean contains(String path) {
		return fileConfig.contains(path);
	}

	protected void setNoOverride(String name, Object v) {
		if (!fileConfig.contains(name)) {
			fileConfig.set(name, v);
			saveNow = true;
		}
	}

	public ArmoryYML setNoSave(boolean force, String name, Object v) {
		return set(force, name, v);
	}

	public ArmoryYML set(boolean force, String name, Object v) {
		if (!force && contains("invalid") && (boolean) get("invalid"))
			return this;
		if (contains("AllowUserModifications") && fileConfig.getBoolean("AllowUserModifications"))
			return this;
		if (!contains(name) || !get(name).equals(v)) {
			fileConfig.set(name, v);
			saveNow = true;
		}
		return this;
	}

	public ArmoryYML dontVerify() {
		this.overrideVerify = true;
		return this;
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
		set(false, "material", mat.name());
		return this;
	}
	public ArmoryYML setWeaponSound(WeaponSounds sound) {
		set(false, "weaponsounds", sound.getSoundName());
		return this;
	}

	public void verifyAllTagsExist() {
		if(overrideVerify)
			return;
		setNoOverride("AllowUserModifications", (contains("allowUpdates") ? (!(boolean) get("allowUpdates")) : false));
		setNoOverride("allowUpdates", null);
		setNoOverride("invalid", false);
		setNoOverride("lore", new ArrayList<String>());
		setNoOverride("material", Material.DIAMOND_AXE.name());
		setNoOverride("variant", 0);
		//setNoOverride("weapontype", WeaponType.RIFLE.name());
		setNoOverride("weaponsounds", WeaponSounds.getSoundByType(WeaponType.RIFLE));
		setNoOverride("damage", 3);

		setNoOverride("durability", 1000);

		setNoOverride("price", 1000);
	}
}
