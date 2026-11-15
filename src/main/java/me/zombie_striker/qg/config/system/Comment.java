package me.zombie_striker.qg.config.system;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A comment written above a value or section in the file, one element per line.
 * <p>
 * Comments are only written on servers whose YAML API supports them (Spigot 1.18.1+). Older servers ignore them.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface Comment {
    String[] value();
}
