package me.zombie_striker.qg.config.system;

import me.zombie_striker.qg.config.system.serializers.ConfigSerializer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A YAML file mapped onto the {@link Config} fields of a class and its {@link Section}s.
 * <p>
 * {@link #load()} does everything in one pass: it migrates old files, writes missing defaults and comments,
 * saves the file if anything changed and assigns the values to the fields.
 *
 * @see Config
 * @see Section
 */
public abstract class BaseConfiguration {
    /**
     * Serializers for types the configuration doesn't handle natively. Primitives, Strings, enums and lists of
     * those are always supported.
     */
    public static final List<ConfigSerializer<?>> SERIALIZERS = new ArrayList<>();
    private static final String VERSION_KEY = "version";
    private static final boolean COMMENTS_SUPPORTED = detectCommentSupport();

    private final File file;
    private final Logger logger;
    private final SortedMap<Integer, Migration> migrations = new TreeMap<>();
    private List<Binding> bindings;
    private Map<String, String[]> sectionComments;
    protected FileConfiguration config;

    protected BaseConfiguration(File file, Logger logger) {
        this.file = file;
        this.logger = logger;
    }

    /**
     * @return The current version of the file. Files with a lower version are migrated when loaded.
     */
    public abstract int getVersion();

    /**
     * @return Lines written at the top of the file. Works on every server version.
     */
    protected String[] getHeader() {
        return new String[0];
    }

    /**
     * Called for every value read from the file before it is assigned to its field.
     * Override it to post-process values, for example to colorize messages.
     *
     * @param field The field the value will be assigned to.
     * @param value The value read from the file, never null.
     * @return The value to assign.
     */
    protected Object transformValue(Field field, Object value) {
        return value;
    }

    /**
     * Registers a migration step. When a file is loaded, {@link Config#oldPath()} values are moved first,
     * then every step with a version higher than the file's runs in ascending order.
     *
     * @param version   The version the step upgrades the file to.
     * @param migration The step.
     */
    protected final void migration(int version, Migration migration) {
        if (version < 1 || version > this.getVersion())
            throw new IllegalArgumentException("Migration version " + version + " is outside 1.." + this.getVersion());
        this.migrations.put(version, migration);
    }

    /**
     * Moves a value to a new path unless the new path is already set. The old path is always removed.
     */
    protected final void move(String from, String to) {
        if (!this.config.contains(from)) return;
        if (!this.config.contains(to)) {
            Object value = this.config.get(from);
            if (value instanceof ConfigurationSection)
                this.config.createSection(to, toMap((ConfigurationSection) value));
            else
                this.config.set(to, value);
        }
        this.config.set(from, null);
    }

    /**
     * Loads the file: migrates it, writes missing defaults and comments, saves it if needed
     * and assigns the values to the fields. Call it again to reload.
     */
    public void load() {
        List<Binding> bindings = this.bindings();
        this.createFile();
        this.config = YamlConfiguration.loadConfiguration(this.file);
        String before = this.config.saveToString();

        boolean migrated = false;
        if (this.config.getKeys(false).isEmpty()) {
            this.config.set(VERSION_KEY, this.getVersion());
        } else {
            migrated = this.migrate(bindings);
        }

        for (Binding binding : bindings) {
            if (!this.config.contains(binding.path))
                this.config.set(binding.path, this.serialize(binding.defaultValue));
        }

        if (migrated)
            this.config = this.reorder(this.config, bindings);

        this.applyHeaderAndComments(bindings);

        if (!this.config.saveToString().equals(before))
            this.save();

        for (Binding binding : bindings)
            this.assign(binding);
    }

    /**
     * Sets a raw value in the file. Call {@link #save()} to persist it and {@link #load()} to update the fields.
     *
     * @param path  The path in the file.
     * @param value The value, or null to remove it.
     */
    public void set(String path, Object value) {
        this.config.set(path, this.serialize(value));
    }

    /**
     * Saves the file to disk.
     */
    public void save() {
        try {
            this.config.save(this.file);
        } catch (IOException e) {
            this.logger.log(Level.SEVERE, "Could not save " + this.file.getName(), e);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void createFile() {
        if (this.file.exists()) return;
        try {
            this.file.getParentFile().mkdirs();
            this.file.createNewFile();
        } catch (IOException e) {
            this.logger.log(Level.SEVERE, "Could not create " + this.file.getName(), e);
        }
    }

    // ---- Migration ----

    /**
     * @return Whether the file was migrated.
     */
    private boolean migrate(List<Binding> bindings) {
        int from = this.config.getInt(VERSION_KEY, 0);
        int to = this.getVersion();
        if (from == to) return false;
        if (from > to) {
            this.logger.warning(this.file.getName() + " is from a newer version (" + from + " > " + to + "). It will be loaded as is.");
            return false;
        }

        for (Binding binding : bindings) {
            for (String oldPath : binding.oldPaths)
                this.move(oldPath, binding.path);
        }

        for (Map.Entry<Integer, Migration> step : this.migrations.tailMap(from + 1).entrySet())
            step.getValue().migrate(this.config);

        pruneEmptySections(this.config);
        this.config.set(VERSION_KEY, to);
        this.logger.info("Migrated " + this.file.getName() + " from version " + from + " to " + to);
        return true;
    }

    /**
     * Rebuilds a migrated file so keys follow the declared order, like a freshly generated file.
     * Values without a field (raw values, custom sections) keep their place at the end of their section.
     */
    private FileConfiguration reorder(FileConfiguration source, List<Binding> bindings) {
        YamlConfiguration ordered = new YamlConfiguration();
        ordered.set(VERSION_KEY, source.get(VERSION_KEY));
        for (Binding binding : bindings)
            ordered.set(binding.path, source.get(binding.path));
        for (String path : source.getKeys(true)) {
            if (!source.isConfigurationSection(path) && !ordered.contains(path))
                ordered.set(path, source.get(path));
        }
        return ordered;
    }

    /**
     * Converts a section to nested maps, so it can be recreated at another path.
     */
    protected static Map<String, Object> toMap(ConfigurationSection section) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (String key : section.getKeys(false)) {
            Object value = section.get(key);
            map.put(key, value instanceof ConfigurationSection ? toMap((ConfigurationSection) value) : value);
        }
        return map;
    }

    /**
     * Removes sections left empty after their keys were moved.
     */
    private static void pruneEmptySections(ConfigurationSection section) {
        for (String key : section.getKeys(false)) {
            ConfigurationSection child = section.getConfigurationSection(key);
            if (child == null) continue;

            pruneEmptySections(child);
            if (child.getKeys(false).isEmpty())
                section.set(key, null);
        }
    }

    // ---- Comments ----

    private static boolean detectCommentSupport() {
        try {
            ConfigurationSection.class.getMethod("setComments", String.class, List.class);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    @SuppressWarnings("deprecation")
    private void applyHeaderAndComments(List<Binding> bindings) {
        String[] header = this.getHeader();

        if (!COMMENTS_SUPPORTED) {
            // Legacy servers only support a plain header
            if (header.length > 0)
                this.config.options().header(String.join("\n", header)).copyHeader(true);
            return;
        }

        if (header.length > 0)
            this.config.options().setHeader(Arrays.asList(header));
        for (Map.Entry<String, String[]> entry : this.sectionComments.entrySet())
            this.config.setComments(entry.getKey(), Arrays.asList(entry.getValue()));
        for (Binding binding : bindings) {
            if (binding.comment != null)
                this.config.setComments(binding.path, Arrays.asList(binding.comment));
        }
    }

    // ---- Reading values ----

    private void assign(Binding binding) {
        Object raw = this.config.get(binding.path);
        Object value = raw == null ? null : this.convert(raw, binding.field.getType(), binding.field.getGenericType(), binding.path);

        if (value == null) {
            if (raw != null)
                this.logger.warning("Invalid value for " + binding.path + " in " + this.file.getName() + ": '" + raw + "'. Using the default.");
            value = copy(binding.defaultValue);
        } else if (binding.range != null && value instanceof Number) {
            double number = ((Number) value).doubleValue();
            if (number < binding.range.min() || number > binding.range.max()) {
                this.logger.warning(binding.path + " in " + this.file.getName() + " must be between "
                        + binding.range.min() + " and " + binding.range.max() + ", but is " + number + ". Using the default.");
                value = copy(binding.defaultValue);
            }
        }

        if (value != null)
            value = this.transformValue(binding.field, value);

        try {
            binding.field.set(binding.owner, value);
        } catch (IllegalAccessException e) {
            this.logger.log(Level.SEVERE, "Could not set " + binding.path, e);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Object convert(Object raw, Class<?> type, Type genericType, String path) {
        if (List.class.isAssignableFrom(type)) {
            if (!(raw instanceof List)) return null;
            Class<?> elementType = resolveListElementType(genericType);
            List<Object> result = new ArrayList<>();
            for (Object element : (List<?>) raw) {
                Object converted = element == null ? null : this.convert(element, elementType, elementType, path);
                if (converted == null)
                    this.logger.warning("Ignoring invalid entry in " + path + " (" + this.file.getName() + "): '" + element + "'");
                else
                    result.add(converted);
            }
            return result;
        }

        if (Map.class.isAssignableFrom(type)) {
            if (raw instanceof ConfigurationSection) return ((ConfigurationSection) raw).getValues(false);
            return raw instanceof Map ? raw : null;
        }

        if (raw instanceof ConfigurationSection || raw instanceof List) return null;
        String string = String.valueOf(raw).trim();

        try {
            if (type == boolean.class || type == Boolean.class) {
                if (raw instanceof Boolean) return raw;
                if (string.equalsIgnoreCase("true")) return true;
                if (string.equalsIgnoreCase("false")) return false;
                return null;
            }
            if (type == int.class || type == Integer.class) {
                if (raw instanceof Number && ((Number) raw).doubleValue() % 1 == 0) return ((Number) raw).intValue();
                return Integer.parseInt(string);
            }
            if (type == long.class || type == Long.class) {
                if (raw instanceof Number && ((Number) raw).doubleValue() % 1 == 0) return ((Number) raw).longValue();
                return Long.parseLong(string);
            }
            if (type == double.class || type == Double.class) {
                if (raw instanceof Number) return ((Number) raw).doubleValue();
                return Double.parseDouble(string);
            }
            if (type == float.class || type == Float.class) {
                if (raw instanceof Number) return ((Number) raw).floatValue();
                return Float.parseFloat(string);
            }
            if (type == String.class) return String.valueOf(raw);
            if (type.isEnum()) return Enum.valueOf((Class<Enum>) type, string.toUpperCase(Locale.ROOT));

            for (ConfigSerializer<?> serializer : SERIALIZERS) {
                if (serializer.getType().equals(type))
                    return serializer.deserialize(string);
            }
            if (type.isInstance(raw)) return raw;
        } catch (IllegalArgumentException e) {
            // Includes NumberFormatException and unknown enum constants
            return null;
        }

        return null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Object serialize(Object value) {
        if (value == null) return null;
        if (value instanceof Enum) return ((Enum<?>) value).name();
        if (value instanceof List) {
            List<Object> result = new ArrayList<>();
            for (Object element : (List<?>) value)
                result.add(this.serialize(element));
            return result;
        }
        for (ConfigSerializer serializer : SERIALIZERS) {
            if (serializer.getType().isInstance(value))
                return serializer.serialize(value);
        }
        return value;
    }

    private static Object copy(Object value) {
        if (value instanceof List) return new ArrayList<>((List<?>) value);
        if (value instanceof Map) return new LinkedHashMap<>((Map<?, ?>) value);
        return value;
    }

    private static Class<?> resolveListElementType(Type genericType) {
        if (!(genericType instanceof ParameterizedType)) return Object.class;

        Type[] arguments = ((ParameterizedType) genericType).getActualTypeArguments();
        if (arguments.length == 0) return Object.class;
        if (arguments[0] instanceof Class) return (Class<?>) arguments[0];
        if (arguments[0] instanceof ParameterizedType && ((ParameterizedType) arguments[0]).getRawType() instanceof Class)
            return (Class<?>) ((ParameterizedType) arguments[0]).getRawType();
        return Object.class;
    }

    // ---- Field discovery ----

    /**
     * Collects the bindings once. It can't run in the constructor because subclass fields
     * aren't initialized yet at that point.
     */
    private List<Binding> bindings() {
        if (this.bindings == null) {
            this.bindings = new ArrayList<>();
            this.sectionComments = new LinkedHashMap<>();
            this.collect(this, "", this.bindings);
        }
        return this.bindings;
    }

    private void collect(Object instance, String prefix, List<Binding> result) {
        for (Field field : instance.getClass().getDeclaredFields()) {
            int modifiers = field.getModifiers();
            if (field.isSynthetic() || Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers)) continue;

            Config config = field.getAnnotation(Config.class);
            Section section = field.getType().getAnnotation(Section.class);
            if (config == null && section == null) continue;

            field.setAccessible(true);
            Object value;
            try {
                value = field.get(instance);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Could not read " + field, e);
            }

            if (config != null) {
                String key = config.value().isEmpty() ? toKebabCase(field.getName()) : config.value();
                result.add(new Binding(instance, field, prefix + key, config.oldPath(),
                        field.getAnnotation(Comment.class), field.getAnnotation(Range.class), copy(value)));
                continue;
            }

            if (value == null)
                throw new IllegalStateException("Section field " + field + " must be initialized");

            String path = prefix + (section.value().isEmpty() ? toKebabCase(field.getName()) : section.value());
            Comment comment = field.isAnnotationPresent(Comment.class) ? field.getAnnotation(Comment.class) : field.getType().getAnnotation(Comment.class);
            if (comment != null)
                this.sectionComments.put(path, comment.value());
            this.collect(value, path + ".", result);
        }
    }

    static String toKebabCase(String name) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c)) {
                boolean previousLower = i > 0 && !Character.isUpperCase(name.charAt(i - 1));
                boolean nextLower = i + 1 < name.length() && Character.isLowerCase(name.charAt(i + 1));
                if (i > 0 && (previousLower || nextLower)) builder.append('-');
                builder.append(Character.toLowerCase(c));
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    private static final class Binding {
        private final Object owner;
        private final Field field;
        private final String path;
        private final String[] oldPaths;
        private final String[] comment;
        private final Range range;
        private final Object defaultValue;

        private Binding(Object owner, Field field, String path, String[] oldPaths, Comment comment, Range range, Object defaultValue) {
            this.owner = owner;
            this.field = field;
            this.path = path;
            this.oldPaths = oldPaths;
            this.comment = comment == null ? null : comment.value();
            this.range = range;
            this.defaultValue = defaultValue;
        }
    }
}
