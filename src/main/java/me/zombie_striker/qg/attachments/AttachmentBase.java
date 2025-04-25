package me.zombie_striker.qg.attachments;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.qg.guns.Gun;

public class AttachmentBase extends Gun {

    private final MaterialStorage base;
    private final Gun baseGun;
    private final String newName;

    public AttachmentBase(MaterialStorage baseItem, MaterialStorage currentMaterial, String name, String displayname) {
        super(displayname, currentMaterial);

        setDisplayname(displayname);

        this.base = baseItem;
        this.newName = name;

        baseGun = QAMain.gunRegister.get(baseItem);
        copyFrom(baseGun);
    }

    public MaterialStorage getBase() {
        return base;
    }

    public Gun getBaseGun() {
        return baseGun;
    }

    @Override
    public String getName() {
        return newName;
    }

}
