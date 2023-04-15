package me.zombie_striker.qualityarmory.attachments;

public enum AttachmentTypes {

	SILENCER("Silencer"),EXTENDEDMAGS("Extended_Mags"),PAINT("Skin"),JOKE_ITEM("Joke");
	String name;
	private AttachmentTypes(String name) {
	this.name= name;
	}
	
	public AttachmentTypes getAttachByName(String name) {
		for(AttachmentTypes a : AttachmentTypes.values()) {
			if(a.name.equals(name))
				return a;
		}
		return null;
	}
}
