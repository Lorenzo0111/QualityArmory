package me.zombie_striker.qg.config.items;

import me.zombie_striker.qg.config.system.Config;

import java.io.File;

public class ArmorDefinition extends ItemDefinition {
    @Config
    public String miscType;
    @Config
    public double maxProtectionHeight = 0;
    @Config
    public double minProtectionHeight = 0;
    @Config
    public int protection = 0;
    @Config
    public boolean stopsHeadshots = false;

    public ArmorDefinition(File file) {
        super(file);
    }
}
