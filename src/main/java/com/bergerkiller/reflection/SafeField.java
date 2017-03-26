package com.bergerkiller.reflection;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.conversion.ConverterPair;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.logging.Level;

/**
 * Wraps around the java.lang.reflect.Field class to provide an error-free
 * alternative<br>
 * Exceptions are logged, isValid can be used to check if the Field is actually
 * working
 *
 * @param <T> type of the Field
 */
public class SafeField<T> implements FieldAccessor<T> {

    private Field field;

    public SafeField(Field field) {
        if (field != null && !field.isAccessible()) {
            try {
                field.setAccessible(true);
            } catch (SecurityException ex) {
                ex.printStackTrace();
                field = null;
            }
        }
        this.field = field;
    }

    public SafeField(String fieldPath, Class<?> fieldType) {
        if (LogicUtil.nullOrEmpty(fieldPath) || !fieldPath.contains(".")) {
        	Common.LOGGER.log(Level.SEVERE, "Field path contains no class: " + fieldPath);
            return;
        }
        try {
            String className = StringUtil.getLastBefore(fieldPath, ".");
            String fieldName = fieldPath.substring(className.length() + 1);
            Class<?> type = Class.forName(Common.SERVER.getClassName(className));
            load(type, fieldName, fieldType);
        } catch (Throwable t) {
            System.out.println("Failed to load field '" + fieldPath + "':");
            t.printStackTrace();
        }
    }

    public SafeField(Object value, String name, Class<?> fieldType) {
        load(value == null ? null : value.getClass(), name, fieldType);
    }

    public SafeField(Class<?> source, String name, Class<?> fieldType) {
        load(source, name, fieldType);
    }

    private void load(Class<?> source, String name, Class<?> fieldType) {
        if (source == null) {
        	Logging.LOGGER_REFLECTION.log(Level.WARNING, "Can not load field '" + name + "' because the class is null!", new Exception());
            return;
        }
        // try to find the field
        String fixedName = Common.SERVER == null ? name : Common.SERVER.getFieldName(source, name);
        String dispName = name.equals(fixedName) ? name : (name + "[" + fixedName + "]");
        try {
            this.field = findRaw(source, fixedName);
            if (this.field != null) {
            	if (fieldType != null && !this.field.getType().equals(fieldType)) {
            		Logging.LOGGER_REFLECTION.log(Level.WARNING, "Field '" + name + "'" +
            				               " in class " + source.getName() +
            				               " is of type " + this.field.getType().getSimpleName() +
            				               " while we expect type " + fieldType.getSimpleName());
            		this.field = null;
            	} else {
                    this.field.setAccessible(true);
                    return;
            	}
            }
        } catch (SecurityException ex) {
            new Exception("No permission to access field '" + dispName + "' in class file '" + source.getSimpleName() + "'").printStackTrace();
            return;
        }
        Logging.LOGGER_REFLECTION.handleReflectionMissing("Field", dispName, source);
    }

    @Override
    public boolean isValid() {
        return this.field != null;
    }

    /**
     * Gets whether this Field is a static Field
     *
     * @return True if static, False if not
     */
    public boolean isStatic() {
        return this.field == null ? false : Modifier.isStatic(this.field.getModifiers());
    }

    @Override
    public T transfer(Object from, Object to) {
        if (this.field == null) {
            return null;
        }
        T old = get(to);
        set(to, get(from));
        return old;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get(Object object) {
        if (this.field == null) {
            return null;
        }
        try {
            return (T) this.field.get(object);
        } catch (Throwable t) {
            if (!this.isStatic() && object == null) {
                throw new IllegalArgumentException("Non-static field requires a non-null instance");
            }
            t.printStackTrace();
            this.field = null;
            return null;
        }
    }

    @Override
    public boolean set(Object object, T value) {
        if (this.field != null) {
            try {
                this.field.set(object, value);
                return true;
            } catch (Throwable t) {
                if (!this.isStatic() && object == null) {
                    throw new IllegalArgumentException("Non-static field requires a non-null instance");
                }
                t.printStackTrace();
                this.field = null;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder(20);
        final int mod = field.getModifiers();
        if (Modifier.isPublic(mod)) {
            text.append("public ");
        } else if (Modifier.isPrivate(mod)) {
            text.append("private ");
        } else if (Modifier.isProtected(mod)) {
            text.append("protected ");
        }
        if (Modifier.isStatic(mod)) {
            text.append("static ");
        }
        return text.append(field.getType().getName()).append(" ").append(field.getName()).toString();
    }

    /**
     * Gets the name of this field as declared in the Class
     *
     * @return Field name
     */
    public String getName() {
        return field.getName();
    }

    /**
     * Gets the Class type of this field as declared inthe Class
     *
     * @return Field type
     */
    public Class<?> getType() {
        return field.getType();
    }

    @Override
    public <K> TranslatorFieldAccessor<K> translate(ConverterPair<?, K> converterPair) {
        return new TranslatorFieldAccessor<K>(this, converterPair);
    }

    /**
     * Tries to set a Field for a certain Object
     *
     * @param source to set a Field for
     * @param fieldname to set
     * @param value to set to
     */
    @SuppressWarnings("unchecked")
	public static <T> void set(Object source, String fieldname, T value) {
    	if (value == null) {
    		new SafeField<T>(source, fieldname, null).set(source, value);
    	} else {
    		new SafeField<T>(source, fieldname, (Class<T>) value.getClass()).set(source, value);
    	}
    }

    /**
     * Tries to set a static Field in a certain Class
     *
     * @param clazz to set the static field in
     * @param fieldname of the static field
     * @param value to set to
     */
    @SuppressWarnings("unchecked")
	public static <T> void setStatic(Class<?> clazz, String fieldname, T value) {
    	if (value == null) {
    		new SafeField<T>(clazz, fieldname, null).set(null, value);
    	} else {
    		new SafeField<T>(clazz, fieldname, (Class<T>) value.getClass()).set(null, value);
    	}
    }

    /**
     * Tries to get a Field from a certain Object
     *
     * @param source to get the Field from
     * @param fieldname to get
     * @return The Field value, or null if not possible
     */
    public static <T> T get(Object source, String fieldname, Class<T> fieldType) {
        return new SafeField<T>(source, fieldname, fieldType).get(source);
    }

    /**
     * Tries to get a static Field from a class
     *
     * @param clazz to get the field value for
     * @param fieldname of the field value
     * @return The Field value, or null if not possible
     */
    public static <T> T get(Class<?> clazz, String fieldname, Class<T> fieldType) {
        return new SafeField<T>(clazz, fieldname, fieldType).get(null);
    }

    /**
     * Creates a new SafeField instance pointing to the field found in the given
     * class type
     *
     * @param type - class type to find the field in
     * @param name - field name
     * @return new SafeField
     */
    public static <T> SafeField<T> create(Class<?> type, String fieldname, Class<T> fieldType) {
        return new SafeField<T>(type, fieldname, fieldType);
    }

    /**
     * Creates a new SafeField instance pointing to the field found in the given
     * class type. Exposes to the outside world using a translator.
     *
     * @param type - class type to find the field in
     * @param name - field name
     * @param converterPair - used to convert between exposed and stored types
     * @return new TranslatorFieldAccessor backed by a SafeField
     */
    public static <T> TranslatorFieldAccessor<T> create(Class<?> type, String name, ConverterPair<?, T> converterPair) {
        return create(type, name, converterPair.getOutputTypeB()).translate(converterPair);
    }

    /**
     * Checks whether a certain field is available in a Class
     *
     * @param type of Class
     * @param name of the field
     * @return True if available, False if not
     */
    public static boolean contains(Class<?> type, String name, Class<?> fieldType) {
    	Field f = findRaw(type, Common.SERVER.getFieldName(type, name));
    	return f != null && (fieldType == null || fieldType.equals(f.getType()));
    }

    /**
     * Tries to recursively find a field in a Class
     *
     * @param type of Class
     * @param name of the field
     * @return the Field, or null if not found
     */
    private static Field findRaw(Class<?> type, String fieldName) {
        Class<?> tmp = type;
        // Try to find the field in the current and all Super Classes
        while (tmp != null) {
            try {
                return tmp.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ex) {
                tmp = tmp.getSuperclass();
            }
        }
        // Interfaces don't contain fields, so nothing found at this point
        return null;
    }
}
