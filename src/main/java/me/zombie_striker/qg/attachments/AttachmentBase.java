package me.zombie_striker.qg.attachments;

import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.guns.Gun;

public class AttachmentBase extends Gun {

    private final MaterialStorage base;
    private final Gun baseGun;
    private String newName = null;

    /*
     * public AttachmentBase(MaterialStorage baseItem, MaterialStorage attachedItem,
     * String newname, String newDisplayname, List<String> lore) {
     */
    public AttachmentBase(final MaterialStorage baseItem, final MaterialStorage currentMaterial, final String name,
            final String displayname) {
        super(displayname, currentMaterial);
        this.base = baseItem;
        this.baseGun = QAMain.gunRegister.get(baseItem);
        this.copyFrom(this.baseGun);
        this.setDisplayname(displayname);
        this.newName = name;
        // this.ms = attachedItem;
    }

    public Gun getBaseGun() { return this.baseGun; }

    public MaterialStorage getBase() { return this.base; }

    @Override
    public String getName() { return this.newName; }

}
