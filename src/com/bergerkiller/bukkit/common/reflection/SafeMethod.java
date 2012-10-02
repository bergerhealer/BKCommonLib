package com.bergerkiller.bukkit.common.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;

import org.bukkit.Bukkit;

/**
 * Wraps around the java.lang.reflect.Method class to provide an error-free alternative<br>
 * Exceptions are logged, isValid can be used to check if the Field is actually working
 */
public class SafeMethod extends SafeBase {
	private Method method;

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

	private void load(Class<?> source, String name, Class<?>... parameterTypes) {
		if (source == null) {
			new Exception("Can not load method '" + name + "' because the class is null!").printStackTrace();
			return;
		}
		// try to find the field
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
		name += "(";
		for (int i = 0; i < parameterTypes.length; i++) {
			if (i > 0) {
				name += ", ";
			}
			name += parameterTypes[i].getSimpleName();
		}
		name += ")";
		handleReflectionMissing("Method", name, source);
	}

	/**
	 * Checks if this Method is valid
	 * 
	 * @return True if valid, False if not
	 */
	public boolean isValid() {
		return this.method != null;
	}

	/**
	 * Executes the method
	 * 
	 * @param instance of the class the method is in, use null if it is a static method
	 * @param args to use for the method
	 * @return A possible returned value from the method, is always null if the method is a void
	 */
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
