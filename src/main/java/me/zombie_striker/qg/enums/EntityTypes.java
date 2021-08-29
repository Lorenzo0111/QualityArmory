package me.zombie_striker.qg.enums;

import org.bukkit.entity.EntityType;

public enum EntityTypes {
    PIG_ZOMBIE("PIG_ZOMBIE","ZOMBIFIED_PIGLIN"),
    ZOMBIFIED_PIGLIN("PIG_ZOMBIE","ZOMBIFIED_PIGLIN");
    private EntityType entityType = null;
    EntityTypes(String... names) {
        for (String name : names) {
            try{
                entityType = EntityType.valueOf(name);
                return;
            }catch (Throwable ignored){}
        }
    }
    public EntityType getEntityType() {
        return entityType;
    }
}
