package me.zombie_striker.qg.config.system;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.config.system.serializers.ConfigSerializer;
import me.zombie_striker.qg.config.system.serializers.EntityTypeSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class handles the configuration file loading, saving, and migration.
 * It uses reflection to load the fields annotated with @Config and set their values from the configuration file.
 *
 * @see Config
 */
public abstract class BaseConfiguration {
    public static final List<ConfigSerializer<?>> SERIALIZERS = new ArrayList<>();
    private final QAMain plugin = QAMain.getInstance();
    private final File file;
    protected FileConfiguration config;

    static {
        SERIALIZERS.add(new EntityTypeSerializer());
    }

    protected BaseConfiguration(File file) {
        this.file = file;
        this.loadFile();
    }

    /**
     * Loads the configuration file into memory.
     */
    protected void loadFile() {
        this.config = YamlConfiguration.loadConfiguration(this.file);
    }

    /**
     * Loads the default values into the configuration file if it doesn't exist.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void loadDefaults() {
        if (!this.file.exists()) {
            try {
                this.file.getParentFile().mkdirs();
                this.file.createNewFile();
            } catch (Exception e) {
                plugin.handleException(e);
            }
        }

        if (this.config.get("version") == null) {
            this.config.set("version", 0);
        }

        this.migrate();

        for (ConfigFieldBinding binding : this.collectConfigFields()) {
            String path = binding.config.value();
            Object value = this.config.get(path);
            if (value != null) continue;

            try {
                binding.field.setAccessible(true);
                this.config.set(path, binding.field.get(binding.owner));
            } catch (IllegalAccessException e) {
                plugin.handleException(e);
            }
        }

        this.save();
    }

    /**
     * Reloads the configuration file and updates the fields annotated with @Config.
     */
    public void reload() {
        this.loadFile();

        for (ConfigFieldBinding binding : this.collectConfigFields()) {
            String path = binding.config.value();
            Object value;

            if (List.class.isAssignableFrom(binding.field.getType()))
                value = this.getValueList(path, this.resolveListElementType(binding.field));
            else value = this.getValue(path, binding.field.getType());

            if (value == null) continue;

            try {
                binding.field.setAccessible(true);
                binding.field.set(binding.owner, value);
            } catch (IllegalAccessException e) {
                plugin.handleException(e);
            }
        }
    }

    /**
     * Gets the value of a field from the configuration file.
     *
     * @param path The path of the field in the configuration file.
     * @param type The class type of the field.
     * @return The value of the field, or null if it doesn't exist.
     */
    @SuppressWarnings("unchecked")
    public <T> T getValue(String path, Class<T> type) {
        Object raw = this.config.get(path);
        if (raw == null) return null;
        return this.convertRawValue(raw, type);
    }

    /**
     * Gets the value of a field from the configuration file.
     *
     * @param path The path of the field in the configuration file.
     * @param type The class type of the field.
     * @return The value of the field, or null if it doesn't exist.
     */
    public <T> List<T> getValueList(String path, Class<T> type) {
        List<?> values = this.config.getList(path);
        List<T> result = new ArrayList<>();
        if (values == null) return result;

        for (Object value : values) {
            T obj = this.convertRawValue(value, type);
            if (obj != null) result.add(obj);
        }

        return result;
    }

    /**
     * Saves the configuration file to disk.
     */
    public void save() {
        try {
            this.config.save(this.file);
        } catch (Exception e) {
            plugin.handleException(e);
        }
    }

    /**
     * Migrates the configuration file from an old version to the current version.
     * This method checks for fields annotated with @Config that have an oldPath specified.
     * It copies the value from the old path to the new path and removes the old path.
     * If the version in the configuration file is already up to date, it does nothing.
     */
    public void migrate() {
        if (this.getVersion() == 0) return;
        if (this.config.getInt("version") == this.getVersion()) return;

        this.customMigrate();

        for (ConfigFieldBinding binding : this.collectConfigFields()) {
            if (binding.config.oldPath().isEmpty()) continue;

            String oldPath = binding.config.oldPath();
            Object value = this.convertRawValue(this.config.get(oldPath), binding.field.getType());
            if (value == null) continue;

            this.config.set(binding.config.value(), value);
            this.config.set(oldPath, null);
        }

        this.config.set("version", this.getVersion());
        this.save();
    }


    /**
     * @return The version of the configuration file.
     */
    public abstract int getVersion();


    /**
     * This method is called before the default migration process.
     * You can override this method to perform any custom migration logic.
     */
    public void customMigrate() {
    }

    @SuppressWarnings("unchecked")
    private <T> T convertRawValue(Object raw, Class<T> type) {
        if (raw == null) return null;
        if (type == null) return null;

        if (type.isPrimitive()) {
            if (type.equals(boolean.class)) return (T) Boolean.valueOf(Boolean.parseBoolean(String.valueOf(raw)));
            if (type.equals(int.class)) return (T) Integer.valueOf(Integer.parseInt(String.valueOf(raw)));
            if (type.equals(double.class)) return (T) Double.valueOf(Double.parseDouble(String.valueOf(raw)));
            if (type.equals(long.class)) return (T) Long.valueOf(Long.parseLong(String.valueOf(raw)));
            return null;
        }

        if (type.isInstance(raw)) return (T) raw;
        if (type.equals(String.class)) return (T) String.valueOf(raw);
        if (type.equals(Boolean.class)) return (T) Boolean.valueOf(Boolean.parseBoolean(String.valueOf(raw)));
        if (type.equals(Integer.class)) return (T) Integer.valueOf(Integer.parseInt(String.valueOf(raw)));
        if (type.equals(Double.class)) return (T) Double.valueOf(Double.parseDouble(String.valueOf(raw)));
        if (type.equals(Long.class)) return (T) Long.valueOf(Long.parseLong(String.valueOf(raw)));
        if (Map.class.isAssignableFrom(type) && raw instanceof Map) return (T) raw;

        for (ConfigSerializer<?> serializer : SERIALIZERS) {
            if (serializer.getType().equals(type)) {
                return (T) serializer.deserialize(String.valueOf(raw));
            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private <T> Class<T> resolveListElementType(Field field) {
        Type genericType = field.getGenericType();
        if (!(genericType instanceof ParameterizedType)) return (Class<T>) Object.class;

        Type[] typeArguments = ((ParameterizedType) genericType).getActualTypeArguments();
        if (typeArguments.length == 0) return (Class<T>) Object.class;

        Type firstType = typeArguments[0];
        if (firstType instanceof Class) return (Class<T>) firstType;
        if (firstType instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) firstType).getRawType();
            if (rawType instanceof Class) return (Class<T>) rawType;
        }

        return (Class<T>) Object.class;
    }

    private List<ConfigFieldBinding> collectConfigFields() {
        List<ConfigFieldBinding> fields = new ArrayList<>();
        this.collectConfigFields(this, fields, Collections.newSetFromMap(new IdentityHashMap<Object, Boolean>()));
        return fields;
    }

    private void collectConfigFields(Object instance, List<ConfigFieldBinding> fields, Set<Object> visited) {
        if (instance == null || visited.contains(instance)) return;
        visited.add(instance);

        for (Field field : instance.getClass().getDeclaredFields()) {
            if (field.isSynthetic()) continue;

            Config config = field.getAnnotation(Config.class);
            if (config != null) {
                fields.add(new ConfigFieldBinding(instance, field, config));
                continue;
            }

            if (!this.isNestedConfigField(field)) continue;

            Object nested = this.resolveOrCreateNestedObject(instance, field);
            if (nested == null) continue;
            this.collectConfigFields(nested, fields, visited);
        }
    }

    private boolean isNestedConfigField(Field field) {
        int modifiers = field.getModifiers();
        if (java.lang.reflect.Modifier.isStatic(modifiers)) return false;
        if (java.lang.reflect.Modifier.isTransient(modifiers)) return false;

        Class<?> type = field.getType();
        if (type.isPrimitive() || type.isEnum() || type.isArray()) return false;
        if (type.equals(String.class)) return false;
        if (Number.class.isAssignableFrom(type)) return false;
        if (type.equals(Boolean.class) || type.equals(Character.class)) return false;
        if (List.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type)) return false;
        return !type.getName().startsWith("java.");
    }

    private Object resolveOrCreateNestedObject(Object owner, Field field) {
        try {
            field.setAccessible(true);
            Object value = field.get(owner);
            if (value != null) return value;

            Constructor<?> constructor = field.getType().getDeclaredConstructor();
            constructor.setAccessible(true);
            Object created = constructor.newInstance();
            field.set(owner, created);
            return created;
        } catch (Exception ignored) {
            return null;
        }
    }

    private static final class ConfigFieldBinding {
        private final Object owner;
        private final Field field;
        private final Config config;

        private ConfigFieldBinding(Object owner, Field field, Config config) {
            this.owner = owner;
            this.field = field;
            this.config = config;
        }
    }
}
