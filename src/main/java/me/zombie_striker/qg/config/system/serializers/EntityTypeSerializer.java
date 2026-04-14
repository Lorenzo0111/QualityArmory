package me.zombie_striker.qg.config.system.serializers;

import org.bukkit.entity.EntityType;

public class EntityTypeSerializer implements ConfigSerializer<EntityType> {

    @Override
    public String serialize(EntityType object) {
        return object.name();
    }

    @Override
    public EntityType deserialize(String from) {
        if (from == null || from.trim().isEmpty())
            return EntityType.UNKNOWN;

        try {
            return EntityType.valueOf(from.trim());
        } catch (IllegalArgumentException e) {
            return EntityType.UNKNOWN;
        }
    }

    @Override
    public Class<EntityType> getType() {
        return EntityType.class;
    }

}
