package me.zombie_striker.qg.enums;

import org.bukkit.Material;

public enum Materials {
    DIRT_PATH("GRASS_PATH","DIRT_PATH"),
    GRASS_PATH("GRASS_PATH","DIRT_PATH");
    private Material material = null;
    Materials(String... names) {
        for (String name : names) {
            try{
                material = Material.valueOf(name);
                return;
            }catch (Throwable ignored){}
        }
    }
    public Material getMaterial() {
        return material;
    }
}