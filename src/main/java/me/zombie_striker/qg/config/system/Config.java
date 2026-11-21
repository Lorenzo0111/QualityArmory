package me.zombie_striker.qg.config.system;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field as a configuration value. The field's initial value is the default.
 * <p>
 * The path is relative to the enclosing {@link Section}. When {@link #value()} is empty,
 * the key is the field name in kebab-case ({@code autoReload} becomes {@code auto-reload}).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Config {

    /**
     * @return The key, relative to the enclosing section. Empty to derive it from the field name.
     */
    String value() default "";

    /**
     * @return Absolute paths this value was stored at in older versions. The first one found is moved to the
     * current path during migration. List older names too, so files several versions behind still migrate.
     */
    String[] oldPath() default {};
}
