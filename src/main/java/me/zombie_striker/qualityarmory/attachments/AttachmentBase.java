package me.zombie_striker.qualityarmory.attachments;

import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.qualityarmory.QAMain;
import me.zombie_striker.qualityarmory.api.QualityArmory;
import me.zombie_striker.qualityarmory.commands.QualityArmoryCommand;
import me.zombie_striker.qualityarmory.guns.Gun;

public class AttachmentBase extends Gun {

	private MaterialStorage base;

	public AttachmentBase(MaterialStorage baseItem, MaterialStorage currentMaterial, String name) {
		super(name, currentMaterial);
		this.base = baseItem;
	}

	public MaterialStorage getBase() {
		return base;
	}

}
