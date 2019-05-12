package me.zombie_striker.qg.attachments;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.MaterialStorage;
import me.zombie_striker.qg.guns.Gun;

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
		this.setDisplayName(displayname);
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
