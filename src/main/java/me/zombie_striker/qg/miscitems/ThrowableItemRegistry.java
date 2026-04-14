package me.zombie_striker.qg.miscitems;

import org.bukkit.entity.Entity;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Holds active throwable state (pulled pins, etc.)
 */
public final class ThrowableItemRegistry {

    private static final Map<Entity, ThrowableItems.ThrowableHolder> ITEMS = new ConcurrentHashMap<>();

    private ThrowableItemRegistry() {
    }

    public static ThrowableItems.ThrowableHolder get(Entity entity) {
        return ITEMS.get(entity);
    }

    public static boolean containsKey(Entity entity) {
        return ITEMS.containsKey(entity);
    }

    public static void put(Entity entity, ThrowableItems.ThrowableHolder holder) {
        ITEMS.put(entity, holder);
    }

    public static void remove(Entity entity) {
        ITEMS.remove(entity);
    }

    public static Set<Entity> keySet() {
        return ITEMS.keySet();
    }

    public static void clear() {
        ITEMS.clear();
    }
}
