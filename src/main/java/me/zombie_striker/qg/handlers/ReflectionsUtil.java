package me.zombie_striker.qg.handlers;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import static org.apache.commons.lang.reflect.MethodUtils.invokeMethod;

public class ReflectionsUtil {
	/**
	 * Retrieve a field accessor for a specific field type and name.
	 *
	 * @param target
	 *            the target type
	 * @param name
	 *            the name of the field, or NULL to ignore
	 * @param fieldType
	 *            a compatible field type
	 * @return the field accessor
	 */
	public static <T> FieldAccessor<T> getField(Class<?> target, String name, Class<T> fieldType) {
		return getField(target, name, fieldType, 0);
	}

	// Common method
	private static <T> FieldAccessor<T> getField(Class<?> target, String name, Class<T> fieldType, int index) {
		for (final Field field : target.getDeclaredFields()) {
			if ((name == null || field.getName().equals(name)) && fieldType.isAssignableFrom(field.getType())
					&& index-- <= 0) {
				field.setAccessible(true);

				// A function for retrieving a specific field value
				return new FieldAccessor<T>() {
					@SuppressWarnings("unchecked")
					@Override
					public T get(Object target) {
						try {
							return (T) field.get(target);
						} catch (IllegalAccessException e) {
							throw new RuntimeException("Cannot access reflection.", e);
						}
					}

					@Override
					public void set(Object target, Object value) {
						try {
							field.set(target, value);
						} catch (IllegalAccessException e) {
							throw new RuntimeException("Cannot access reflection.", e);
						}
					}

					@Override
					public boolean hasField(Object target) {
						// target instanceof DeclaringClass
						return field.getDeclaringClass().isAssignableFrom(target.getClass());
					}
				};
			}
		}

		// Search in parent classes
		if (target.getSuperclass() != null)
			return getField(target.getSuperclass(), name, fieldType, index);
		throw new IllegalArgumentException("Cannot find field with type " + fieldType);
	}

	/**
	 * Returns an enum constant
	 *
	 * @param enumClass
	 *            The class of the enum
	 * @param name
	 *            The name of the enum constant
	 *
	 * @return The enum entry or null
	 */
	public static Object getEnumConstant(Class<?> enumClass, String name) {
		if (!enumClass.isEnum()) {
			return null;
		}
		for (Object o : enumClass.getEnumConstants()) {
			try {
				if (name.equals(invokeMethod(o, "name", new Class[0]))) {
					return o;
				}
			} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * An interface for retrieving the field content.
	 *
	 * @param <T>
	 *            field type
	 */
	public interface FieldAccessor<T> {
		/**
		 * Retrieve the content of a field.
		 *
		 * @param target
		 *            the target object, or NULL for a static field
		 * @return the value of the field
		 */
		T get(Object target);

		/**
		 * Set the content of a field.
		 *
		 * @param target
		 *            the target object, or NULL for a static field
		 * @param value
		 *            the new value of the field
		 */
		void set(Object target, Object value);

		/**
		 * Determine if the given object has this field.
		 *
		 * @param target
		 *            the object to test
		 * @return TRUE if it does, FALSE otherwise
		 */
		boolean hasField(Object target);
	}

}
