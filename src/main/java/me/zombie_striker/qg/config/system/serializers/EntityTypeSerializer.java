package me.zombie_striker.qg.config.system.serializers;

import org.bukkit.entity.EntityType;

public class EntityTypeSerializer implements ConfigSerializer<EntityType> {

    @Override
    public String serialize(EntityType object) {
        return object.name();
    }

    @Override
    public EntityType deserialize(String from) {
        return EntityType.valueOf(from);
    }

    @Override
    public Class<EntityType> getType() {
        return EntityType.class;
    }

}
