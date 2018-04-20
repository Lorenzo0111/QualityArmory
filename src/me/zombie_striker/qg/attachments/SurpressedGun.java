package me.zombie_striker.qg.attachments;

import java.util.List;

import me.zombie_striker.qg.MaterialStorage;
import me.zombie_striker.qg.guns.utils.WeaponSounds;

public class SurpressedGun extends AttachmentBase{

	public SurpressedGun(MaterialStorage baseItem, MaterialStorage attachedItem, String newname, String newDisplayname,
			List<String> lore) {
		super(baseItem, attachedItem, newname, newDisplayname, lore);
		setNewSound(WeaponSounds.SILENCEDSHOT);
	}
	

}
