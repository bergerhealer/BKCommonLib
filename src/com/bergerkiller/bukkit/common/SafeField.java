package com.bergerkiller.bukkit.common;

import java.lang.reflect.Field;
import java.util.logging.Level;

import org.bukkit.Bukkit;

public class SafeField<T> {

	public SafeField(String methodPath) {
		if (methodPath == null || methodPath.isEmpty() || !methodPath.contains(".")) {
			Bukkit.getLogger().log(Level.SEVERE, "Method path contains no class: " + methodPath);
			return;
		}
		try {
			String className = methodPath.substring(0, methodPath.lastIndexOf('.'));
			String methodName = methodPath.substring(className.length() + 1);
			load(Class.forName(className), methodName);
		} catch (Throwable t) {
			System.out.println("Failed to load method '" + methodPath + "':");
			t.printStackTrace();
		}
	}
	public SafeField(Object value, String name) {
		load(value.getClass(), name);
	}
	public SafeField(Class<?> source, String name) {
		load(source, name);
	}
	private Field field;
	
	private void load(Class<?> source, String name) {
		//try to find the field
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
	
	@SuppressWarnings("unchecked")
	public T get(Object object) {
		if (this.field == null) return null;
		try {
			return (T) this.field.get(object);
		} catch (Throwable t) {
			t.printStackTrace();
			this.field = null;
			return null;
		}
	}
	
	public boolean set(Object object, T value) {
		if (this.field == null) return false;
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
