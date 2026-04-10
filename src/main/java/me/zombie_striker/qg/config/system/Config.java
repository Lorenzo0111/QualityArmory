package me.zombie_striker.qg.config.system;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to mark fields in a configuration class that should be loaded from a configuration file.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Config {

    /**
     * @return The key of the config
     */
    String value();

    /**
     * @return The old path of the config before migration
     */
    String oldPath() default "";
}
