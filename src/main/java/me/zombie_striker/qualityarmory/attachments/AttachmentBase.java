package me.zombie_striker.qualityarmory.attachments;

import me.zombie_striker.qualityarmory.QAMain;
import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.qualityarmory.guns.Gun;

public class AttachmentBase extends Gun {

	private MaterialStorage base;
	private Gun baseGun;
	private String newName = null;

	/*
	 * public AttachmentBase(MaterialStorage baseItem, MaterialStorage attachedItem,
	 * String newname, String newDisplayname, List<String> lore) {
	 */
	public AttachmentBase(MaterialStorage baseItem, MaterialStorage currentMaterial, String name, String displayname) {
		super(displayname, currentMaterial);
		this.base = baseItem;
		baseGun = QAMain.gunRegister.get(baseItem);
		copyFrom(baseGun);
		this.setDisplayname(displayname);
		this.newName = name;
		// this.ms = attachedItem;
	}

	public Gun getBaseGun() {
		return baseGun;
	}

	public MaterialStorage getBase() {
		return base;
	}

	@Override
	public String getName() {
		return newName;
	}

}
