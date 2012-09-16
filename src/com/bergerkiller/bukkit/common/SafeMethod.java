package com.bergerkiller.bukkit.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;

import org.bukkit.Bukkit;

public class SafeMethod {

	public SafeMethod(String methodPath, Class<?>... parameterTypes) {
		if (methodPath == null || methodPath.isEmpty() || !methodPath.contains(".")) {
			Bukkit.getLogger().log(Level.SEVERE, "Method path contains no class: " + methodPath);
			return;
		}
		try {
			String className = methodPath.substring(0, methodPath.lastIndexOf('.'));
			String methodName = methodPath.substring(className.length() + 1);
			load(Class.forName(className), methodName, parameterTypes);
		} catch (Throwable t) {
			System.out.println("Failed to load method '" + methodPath + "':");
			t.printStackTrace();
		}
	}
	public SafeMethod(Object value, String name, Class<?>... parameterTypes) {
		load(value == null ? null : value.getClass(), name, parameterTypes);
	}
	public SafeMethod(Class<?> source, String name, Class<?>... parameterTypes) {
		load(source, name, parameterTypes);
	}
	private Method method;

	private void load(Class<?> source, String name, Class<?>... parameterTypes) {
		if (source == null) {
			new Exception("Can not load method '" + name + "' because the class is null!").printStackTrace();
			return;
		}
		//try to find the field
	    Class<?> tmp = source;
	    while (tmp != null) {
	    	try {
	    		this.method = tmp.getDeclaredMethod(name, parameterTypes);
	    		this.method.setAccessible(true);
	    		return;
	    	} catch (NoSuchMethodException ex) {
	    		tmp = tmp.getSuperclass();
	    	} catch (SecurityException ex) {
	    		new Exception("No permission to access method '" + name + "' in class file '" + source.getSimpleName() + "'").printStackTrace();
	    		return;
	    	}
	    }
	    new Exception("Method '" + name + "' does not exist in class file '" + source.getSimpleName() + "'!").printStackTrace();
	}

	public Object invoke(Object instance, Object... args) {
		if (this.method != null) {
			try {
				return this.method.invoke(instance, args);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
