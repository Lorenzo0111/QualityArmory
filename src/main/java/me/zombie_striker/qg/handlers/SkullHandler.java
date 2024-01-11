package me.zombie_striker.qg.handlers;

import java.lang.reflect.Method;
import java.util.Base64;
import java.util.Collection;
import java.util.UUID;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

public class SkullHandler {
    private static final Method GET_NAME;
    private static final Method GET_VALUE;

    static {
        Method name = null;
        Method value = null;

        try {
            name = Property.class.getMethod("name");
            value = Property.class.getMethod("value");
        } catch (Exception | Error e) {
            try {
                name = Property.class.getMethod("getName");
                value = Property.class.getMethod("getValue");
            } catch (Exception | Error ignored) {
            }
        }

        GET_NAME = name;
        GET_VALUE = value;
    }

    /**
     * Return a skull that has a custom texture specified by url.
     *
     * @param url
     *            skin url
     * @return itemstack
     */
    public static ItemStack getCustomSkull(String url) {
        byte[] encodedData = Base64.getUrlEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());//new Base64().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes()));
        return getCustomSkull64(encodedData);
    }

    /**
     * Return a skull that has a custom texture specified by url.
     *
     * @param url
     *            skin url
     * @return itemstack
     */
    @SuppressWarnings("deprecation")
    public static ItemStack getCustomSkull64(byte[] url64) {

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        PropertyMap propertyMap = profile.getProperties();
        if (propertyMap == null) {
            throw new IllegalStateException("Profile doesn't contain a property map");
        }
        String encodedData = new String(url64);
        propertyMap.put("textures", new Property("textures", encodedData));
        ItemStack head = new ItemStack(MultiVersionLookup.getSkull(), 1, (short) 3);
        ItemMeta headMeta = head.getItemMeta();
        Class<?> headMetaClass = headMeta.getClass();
        ReflectionsUtil.getField(headMetaClass, "profile", GameProfile.class).set(headMeta, profile);
        head.setItemMeta(headMeta);
        return head;
    }

    public static String getURL(ItemStack is) {
        if (is.getType() != MultiVersionLookup.getSkull())
            return null;
        ItemMeta headMeta = is.getItemMeta();
        Class<?> headMetaClass = headMeta.getClass();
        GameProfile prof = ReflectionsUtil.getField(headMetaClass, "profile", GameProfile.class).get(headMeta);
        PropertyMap propertyMap = prof.getProperties();
        Collection<Property> textures64 = propertyMap.get("textures");
        String tex64 = getTexture(textures64);
        if (tex64 != null) {
            byte[] decode = null;
            decode = Base64.getDecoder().decode(tex64);
            String string = new String(decode);
            String parsed = string.split("SKIN:{url:\"")[1].split("\"}}}")[0];
            return parsed;
        }
        return null;
    }

    public static String getURL64(ItemStack is) {
        if (is.getType() != MultiVersionLookup.getSkull())
            return null;
        ItemMeta headMeta = is.getItemMeta();
        Class<?> headMetaClass = headMeta.getClass();
        GameProfile prof = ReflectionsUtil.getField(headMetaClass, "profile", GameProfile.class).get(headMeta);
        PropertyMap propertyMap = prof.getProperties();
        Collection<Property> textures64 = propertyMap.get("textures");
        String tex64 = getTexture(textures64);
        if (tex64 != null) {
            return tex64;
        }
        return null;
    }

    private static String getTexture(Collection<Property> properties) {
        try {
            for (Property p : properties) {
                String name = (String) GET_NAME.invoke(p);
                if (name.equals("textures")) {
                    return (String) GET_VALUE.invoke(p);
                }
            }
        } catch (Exception | Error ignored) {}

        return null;
    }

}
