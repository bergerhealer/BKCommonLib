package com.bergerkiller.bukkit.common.collections;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;

/**
 * A map that obtains the value bound to a given Class.
 * Only if the key specified is an instance of a mapped Class, is the value returned.
 * 
 * @param <T> - type of Value
 */
public class ClassMap<T> {
	private final LinkedHashMap<Class<?>, T> classes = new LinkedHashMap<Class<?>, T>();

	/**
	 * Puts a Class : Value pair into this map.
	 * Null types are ignored.
	 * 
	 * @param typeTemplate to put the type of
	 * @param value to put
	 */
	public void put(ClassTemplate<?> typeTemplate, T value) {
		if (typeTemplate == null) {
			return;
		}
		put(typeTemplate.getType(), value);
	}

	/**
	 * Puts a Class : Value pair into this map.
	 * Null types are ignored.
	 * 
	 * @param type to put
	 * @param value to put
	 */
	public void put(Class<?> type, T value) {
		if (type == null) {
			return;
		}
		classes.put(type, value);
	}

	/**
	 * Obtains the value bound to a given instance type
	 * 
	 * @param type of instance
	 * @return the value bound to the instance type
	 */
	public T get(Class<?> type) {
		if (type == null) {
			return null;
		}
		final T value = classes.get(type);
		if (value != null) {
			return value;
		}
		for (Entry<Class<?>, T> entry : classes.entrySet()) {
			if (entry.getKey().isAssignableFrom(type)) {
				return entry.getValue();
			}
		}
		return null;
	}

	/**
	 * Obtains the value bound to a given type of instance
	 * 
	 * @param instance to get the value of
	 * @return the value bound to the instance type
	 */
	public T get(Object instance) {
		if (instance == null) {
			return null;
		}
		final T value = classes.get(instance.getClass());
		if (value != null) {
			return value;
		}
		for (Entry<Class<?>, T> entry : classes.entrySet()) {
			if (entry.getKey().isInstance(instance)) {
				return entry.getValue();
			}
		}
		return null;
	}

	/**
	 * Obtains an unmodifiable map of the classes stored
	 * 
	 * @return Class Instance Map data
	 */
	public Map<Class<?>, T> getData() {
		return Collections.unmodifiableMap(classes);
	}
}
