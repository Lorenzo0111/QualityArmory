package me.zombie_striker.qualityarmory.attachments;

import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.qualityarmory.QAMain;
import me.zombie_striker.qualityarmory.api.QualityArmory;
import me.zombie_striker.qualityarmory.commands.QualityArmoryCommand;
import me.zombie_striker.qualityarmory.guns.Gun;

public class AttachmentBase extends Gun {

	private MaterialStorage base;
	private String newName = null;

	public AttachmentBase(MaterialStorage baseItem, MaterialStorage currentMaterial, String name, String displayname) {
		super(displayname, currentMaterial, displayname);
		this.base = baseItem;
		this.newName = name;
	}

	public MaterialStorage getBase() {
		return base;
	}

	@Override
	public String getName() {
		return newName;
	}

}
