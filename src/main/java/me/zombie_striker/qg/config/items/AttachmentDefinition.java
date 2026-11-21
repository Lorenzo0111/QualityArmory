package me.zombie_striker.qg.config.items;

import me.zombie_striker.qg.config.system.Config;

import java.io.File;

public class AttachmentDefinition extends GunDefinition {
    @Config
    public String baseGun;

    public AttachmentDefinition(File file) {
        super(file);
    }
}
