package me.zombie_striker.qg.config;

import me.zombie_striker.qg.utils.ErrorReporter;
import me.zombie_striker.qg.config.items.ItemKeys;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import me.zombie_striker.qg.ammo.Ammo;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.guns.utils.WeaponType;
import me.zombie_striker.qg.handlers.MultiVersionLookup;

public class ArmoryYML {

	FileConfiguration fileConfig;
	File file;
	public boolean saveNow = false;

	public ArmoryYML(File file) {
		this.file = file;
		if (!file.getParentFile().exists())
			file.getParentFile().mkdirs();
		if (!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e) {
				ErrorReporter.report("creating " + file.getName(), e);
			}
		fileConfig = CommentYamlConfiguration.loadConfiguration(file);
	}

	public Object get(String path) {
		return fileConfig.get(ItemKeys.translate(path));
	}
	public long getLong(String path) {
		return fileConfig.getLong(ItemKeys.translate(path));
	}

	public boolean contains(String path) {
		return fileConfig.contains(ItemKeys.translate(path));
	}

	public ArmoryYML set(String name, Object v) {
		return set(false, name,v);
	}
	public ArmoryYML set(boolean force, String name, Object v) {
		name = ItemKeys.translate(name);
		long lastmodifiedFile = file.lastModified();
		long lastmodifiedInternal = contains("last-modified-by-qa") ? getLong("last-modified-by-qa") :  (  contains("allow-user-modifications") && fileConfig.getBoolean("allow-user-modifications") ? 0 : System.currentTimeMillis());

		if(!force && lastmodifiedFile-lastmodifiedInternal > 5000) {
			return this; //The file has been changed sometime after QA made any changes.
		}
		if (!contains(name) || !get(name).equals(v)) {
			fileConfig.set(name, v);
			saveNow = true;
		}
		return this;
	}
	public ArmoryYML verify(String name, Object v) {
		name = ItemKeys.translate(name);
		if (!contains(name)) {
			fileConfig.set(name, v);
			saveNow = true;
		}
		return this;
	}
	
	public void done() {
		verifyAllTagsExist();
		if (saveNow) {
			save();
		}
	}

	public void save() {
		try {
			putTimeStamp();
			fileConfig.save(file);
			saveNow = false;
		} catch (IOException e) {
			ErrorReporter.report("saving " + file.getName(), e);
		}
	}

	@SuppressWarnings("deprecation")
	public ArmoryYML setSkullType(String skullowner) {
		set("id", org.bukkit.SkullType.PLAYER.ordinal());
		set( "material", MultiVersionLookup.getSkull().name());
		set( "skull-owner", skullowner);
		set( "skull-owner-custom-url", Ammo.NO_SKIN_STRING);
		return this;
	}

	public ArmoryYML setInvalid(boolean invalid) {
		set( "invalid", invalid);
		return this;
	}

	public ArmoryYML setLore(List<String> lore) {
		set( "lore", lore);
		return this;
	}
	public ArmoryYML setLore(String... lore) {
		set( "lore", lore);
		return this;
	}

	public ArmoryYML setVariant(int var) {
		set( "variant", var);
		return this;
	}

	public ArmoryYML setDurability(int durib) {
		set( "durability", durib);
		return this;
	}

	public ArmoryYML setPrice(int cost) {
		set( "price", cost);
		return this;
	}
	public ArmoryYML setSoundOnEquip(String sound) {
		set( "sound-equip", sound);
		return this;
	}
	public ArmoryYML setSoundOnHit(String sound) {
		set( "sound-melee-hit", sound);
		return this;
	}

	public ArmoryYML setMaterial(Material mat) {
		set( "material", mat.name());
		return this;
	}
	public ArmoryYML setWeaponSound(WeaponSounds sound) {
		set( "weapon-sounds", sound.getSoundName());
		return this;
	}
	public ArmoryYML setMaxItemStack(int amount){
		set("max-item-stack", amount);
		return this;
	}

	public void verifyAllTagsExist() {
		if(contains("allow-user-modifications")){
			boolean allowChanges = (boolean) get("allow-user-modifications");
			set(true,"allow-user-modifications",null);
			if(!allowChanges)
			putTimeStamp();
		}
		verify("invalid", false);
		verify("weapon-sounds", WeaponSounds.getSoundByType(WeaponType.RIFLE));
		verify("damage", 3);

		verify("durability", 1000);

		verify("price", 1000);
		verify("max-item-stack", 1);
	}

	public ArmoryYML putTimeStamp(){
			fileConfig.set("last-modified-by-qa", System.currentTimeMillis());
			saveNow = true;
		return this;
	}
	public long getTimestamp(){
		return contains("last-modified-by-qa") ? (long) get("last-modified-by-qa"): 0;
	}


}
