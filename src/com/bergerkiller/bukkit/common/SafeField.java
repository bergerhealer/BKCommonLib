package com.bergerkiller.bukkit.common;

import java.lang.reflect.Field;
import java.util.logging.Level;

import org.bukkit.Bukkit;

public class SafeField<T> {

	public SafeField(String fieldPath) {
		if (fieldPath == null || fieldPath.isEmpty() || !fieldPath.contains(".")) {
			Bukkit.getLogger().log(Level.SEVERE, "Field path contains no class: " + fieldPath);
			return;
		}
		try {
			String className = fieldPath.substring(0, fieldPath.lastIndexOf('.'));
			String methodName = fieldPath.substring(className.length() + 1);
			load(Class.forName(className), methodName);
		} catch (Throwable t) {
			System.out.println("Failed to load field '" + fieldPath + "':");
			t.printStackTrace();
		}
	}

	public SafeField(Object value, String name) {
		load(value == null ? null : value.getClass(), name);
	}

	public SafeField(Class<?> source, String name) {
		load(source, name);
	}

	private Field field;

	private void load(Class<?> source, String name) {
		if (source == null) {
			new Exception("Can not load field '" + name + "' because the class is null!").printStackTrace();
			return;
		}
		// try to find the field
		Class<?> tmp = source;
		while (tmp != null) {
			try {
				this.field = tmp.getDeclaredField(name);
				this.field.setAccessible(true);
				return;
			} catch (NoSuchFieldException ex) {
				tmp = tmp.getSuperclass();
			} catch (SecurityException ex) {
				new Exception("No permission to access field '" + name + "' in class file '" + source.getSimpleName() + "'").printStackTrace();
				return;
			}
		}
		new Exception("Field '" + name + "' does not exist in class file '" + source.getSimpleName() + "'!").printStackTrace();
	}

	public boolean isValid() {
		return this.field != null;
	}

	/**
	 * Transfers the value from one instance to the other
	 * 
	 * @param from instance
	 * @param to instance
	 * @return the old value of the to instance
	 */
	public T transfer(Object from, Object to) {
		if (this.field == null)
			return null;
		T old = get(to);
		set(to, get(from));
		return old;
	}

	@SuppressWarnings("unchecked")
	public T get(Object object) {
		if (this.field == null)
			return null;
		try {
			return (T) this.field.get(object);
		} catch (Throwable t) {
			t.printStackTrace();
			this.field = null;
			return null;
		}
	}

	public boolean set(Object object, T value) {
		if (this.field == null)
			return false;
		try {
			this.field.set(object, value);
			return true;
		} catch (Throwable t) {
			t.printStackTrace();
			this.field = null;
			return false;
		}
	}

	public static <T> boolean set(Object source, String fieldname, T value) {
		return new SafeField<T>(source, fieldname).set(source, value);
	}

}
