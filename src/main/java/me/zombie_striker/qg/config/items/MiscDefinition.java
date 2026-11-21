package me.zombie_striker.qg.config.items;

import me.zombie_striker.qg.config.system.Config;

import java.io.File;

public class MiscDefinition extends ItemDefinition {
    @Config
    public String miscType;
    @Config
    public int damage = 1;
    @Config
    public double radius = 0;
    @Config
    public int max = 5;
    @Config
    public String soundEquip;
    @Config
    public double soundEquipVolume = 1.0;
    @Config
    public String soundMeleeHit;
    @Config
    public double soundMeleeHitVolume = 1.0;
    @Config
    public Double throwSpeed;
    @Config
    public Boolean useModernParticles;
    /** Seconds before a grenade explodes. */
    @Config
    public Double fuseTime;

    public MiscDefinition(File file) {
        super(file);
    }
}
