package com.bergerkiller.bukkit.common.collections;

import com.bergerkiller.mountiplex.reflection.ClassTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A map that obtains the value bound to a given Class. Only if the key
 * specified is an instance of a mapped Class, is the value returned.
 * This class is multithread-safe. Multiple threads can put new type
 * mappings, or get them at the same time.
 *
 * @param <V> - Value type to map to Class keys
 */
public class ClassMap<V> {

    private final Object lock = new Object();
    private LinkedHashMap<Class<?>, V> classes = new LinkedHashMap<Class<?>, V>();
    private Map<Class<?>, V> classesGetCache = Collections.emptyMap();

    /**
     * Puts a Class : Value pair into this map. Null types are ignored.
     *
     * @param typeTemplate to put the type of
     * @param value to put
     */
    public void put(ClassTemplate<?> typeTemplate, V value) {
        if (typeTemplate == null) {
            return;
        }
        put(typeTemplate.getType(), value);
    }

    /**
     * Puts a Class : Value pair into this map. Null types are ignored.
     *
     * @param type to put
     * @param value to put
     */
    public void put(Class<?> type, V value) {
        if (type == null) {
            return;
        }

        synchronized (lock) {
            LinkedHashMap<Class<?>, V> newClasses = new LinkedHashMap<>(classes);
            newClasses.put(type, value);
            classesGetCache = classes = newClasses;
        }
    }

    /**
     * Obtains the value bound to a given instance type
     *
     * @param type Type of instance
     * @return the value bound to the instance type
     */
    public V get(Class<?> type) {
        return getOrDefault(type, null);
    }

    /**
     * Obtains the value bound to a given instance type, or returns the
     * default value if none match.
     *
     * @param type Type of instance
     * @param defaultValue Defaul value to return when not found
     * @return the value bound to the instance type, or the default value
     */
    public V getOrDefault(Class<?> type, V defaultValue) {
        V value = classesGetCache.get(type);
        if (value != null) {
            return value;
        } else if (type == null) {
            return defaultValue; // Weird? That's how it was...
        }

        // Try to find another entry whose class is a superclass
        // If found, return it as a result and cache it for next time
        for (Entry<Class<?>, V> entry : classes.entrySet()) {
            if (entry.getKey().isAssignableFrom(type)) {
                value = entry.getValue();
                synchronized (lock) {
                    Map<Class<?>, V> newCache = new HashMap<>(classesGetCache);
                    newCache.putIfAbsent(type, value);
                    classesGetCache = newCache;
                }
                return value;
            }
        }

        return defaultValue;
    }

    /**
     * Obtains the value bound to a given type of instance
     *
     * @param instance to get the value of
     * @return the value bound to the instance type
     */
    public V get(Object instance) {
        if (instance == null) {
            return null;
        } else {
            return get(instance.getClass());
        }
    }

    /**
     * Obtains an unmodifiable map of the classes stored. The returned object
     * is not modified when put is called and can be safely iterated.
     *
     * @return Class Instance Map data
     */
    public Map<Class<?>, V> getData() {
        return Collections.unmodifiableMap(classes);
    }
}
