package me.zombie_striker.qg.config.system.serializers;

/**
 * This interface is used to serialize and deserialize objects to and from a configuration file.
 * It is used by the Config class to handle custom serialization of objects.
 *
 * @param <T> The type of object to be serialized/deserialized.
 */
public interface ConfigSerializer<T> {

    /**
     * Serializes an object to a string representation.
     *
     * @param object The object to be serialized.
     * @return The string representation of the object.
     */
    String serialize(T object);

    /**
     * Deserializes a string representation to an object.
     *
     * @param from The string representation of the object.
     * @return The deserialized object.
     */
    T deserialize(String from);

    /**
     * Returns the class type of the object being serialized/deserialized.
     * @return The class type of the object.
     */
    Class<T> getType();
}
