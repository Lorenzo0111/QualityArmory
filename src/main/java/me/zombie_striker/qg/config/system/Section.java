package me.zombie_striker.qg.config.system;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as a configuration section. Fields of a configuration whose type has this annotation are
 * scanned for {@link Config} values, with their path prefixed by the section key.
 * <p>
 * The key is {@link #value()} or, when empty, the name of the field holding the section in kebab-case.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Section {

    /**
     * @return The section key. Empty to derive it from the field name.
     */
    String value() default "";
}
