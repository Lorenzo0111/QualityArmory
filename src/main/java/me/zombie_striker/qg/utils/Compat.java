package me.zombie_striker.qg.utils;

import com.cryptomorin.xseries.reflection.XReflection;

/**
 * Server features that depend on the Minecraft version, checked once instead of catching
 * {@link NoSuchMethodError} at every call. Method checks look for the exact method, so they are
 * correct on forks that backport or remove APIs.
 */
public final class Compat {
    /** Two hands: {@code PlayerInventory#getItemInOffHand} (1.9+). */
    public static final boolean OFFHAND = hasMethod("org.bukkit.inventory.PlayerInventory", "getItemInOffHand");
    /** {@code Player#canSee(Entity)} (1.18+). Older versions can only hide players. */
    public static final boolean CAN_SEE_ENTITY = hasMethod("org.bukkit.entity.Player", "canSee", "org.bukkit.entity.Entity");
    /** {@code ItemMeta#getAttributeModifiers(Attribute)} (1.13.2+). */
    public static final boolean ITEM_ATTRIBUTES = hasMethod("org.bukkit.inventory.meta.ItemMeta", "getAttributeModifiers", "org.bukkit.attribute.Attribute");
    /** {@code ItemMeta#setUnbreakable} (1.11+). */
    public static final boolean UNBREAKABLE_META = hasMethod("org.bukkit.inventory.meta.ItemMeta", "setUnbreakable", "boolean");
    /** {@code Player#sendTitle(String, String, int, int, int)} (1.11+). */
    public static final boolean TITLE_TIMES = hasMethod("org.bukkit.entity.Player", "sendTitle", "java.lang.String", "java.lang.String", "int", "int", "int");
    /** {@code Player#getLocale()} (1.12+). */
    public static final boolean PLAYER_LOCALE = hasMethod("org.bukkit.entity.Player", "getLocale");
    /** {@code World#playSound(Location, String, float, float)}: custom and namespaced sounds by name (1.9+). */
    public static final boolean WORLD_SOUND_BY_NAME = hasMethod("org.bukkit.World", "playSound", "org.bukkit.Location", "java.lang.String", "float", "float");
    /** {@code Player#playSound(Location, String, float, float)}. */
    public static final boolean PLAYER_SOUND_BY_NAME = hasMethod("org.bukkit.entity.Player", "playSound", "org.bukkit.Location", "java.lang.String", "float", "float");
    /** {@code World#spawnParticle} (1.9+). */
    public static final boolean PARTICLES = hasMethod("org.bukkit.World", "spawnParticle", "org.bukkit.Particle", "org.bukkit.Location", "int");
    /** The flattened materials and block data of 1.13+: {@code Block#getBlockData}. */
    public static final boolean FLATTENED = hasMethod("org.bukkit.block.Block", "getBlockData");
    /** The sound names of 1.9+ ({@code Sound.ENTITY_ITEM_PICKUP}). A field check, since Sound is no longer an enum on 1.21.3+. */
    public static final boolean MODERN_SOUNDS = hasField("org.bukkit.Sound", "ENTITY_ITEM_PICKUP");

    private Compat() {
    }

    /**
     * @param minor The minor version, for example 14 for 1.14.
     * @return Whether the server is on that version or newer.
     */
    public static boolean supports(int minor) {
        return XReflection.supports(minor);
    }

    /**
     * Checks whether a public method exists, by class and parameter type names so the check itself
     * doesn't fail on versions where those classes are missing.
     */
    public static boolean hasMethod(String className, String method, String... parameterTypes) {
        try {
            Class<?>[] types = new Class<?>[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++)
                types[i] = primitive(parameterTypes[i]);
            Class.forName(className).getMethod(method, types);
            return true;
        } catch (ClassNotFoundException | NoSuchMethodException | LinkageError e) {
            return false;
        }
    }

    /**
     * @return Whether a public field (or enum constant) exists.
     */
    public static boolean hasField(String className, String field) {
        try {
            Class.forName(className).getField(field);
            return true;
        } catch (ClassNotFoundException | NoSuchFieldException | LinkageError e) {
            return false;
        }
    }

    /**
     * @return Whether a class exists on the server.
     */
    public static boolean hasClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException | LinkageError e) {
            return false;
        }
    }

    private static Class<?> primitive(String name) throws ClassNotFoundException {
        switch (name) {
            case "boolean":
                return boolean.class;
            case "int":
                return int.class;
            case "double":
                return double.class;
            case "float":
                return float.class;
            case "long":
                return long.class;
            default:
                return Class.forName(name);
        }
    }
}
