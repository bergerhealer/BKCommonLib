package com.bergerkiller.bukkit.common.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;

/**
 * Wraps around the java.lang.reflect.Method class to provide an error-free alternative<br>
 * Exceptions are logged, isValid can be used to check if the Field is actually working
 */
public class SafeMethod<T> implements MethodAccessor<T> {
	private Method method;

	public SafeMethod(String methodPath, Class<?>... parameterTypes) {
		if (LogicUtil.nullOrEmpty(methodPath) || !methodPath.contains(".")) {
			Bukkit.getLogger().log(Level.SEVERE, "Method path contains no class: " + methodPath);
			return;
		}
		try {
			String className = StringUtil.getBefore(methodPath, ".");
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
		CommonPlugin.getInstance().handleReflectionMissing("Method", name, source);
	}

	/**
	 * Gets the name of this method as declared in the Class
	 * 
	 * @return Method name
	 */
	public String getName() {
		return method.getName();
	}

	/**
	 * Checks whether this method is overrided in the Class specified
	 * 
	 * @param type to check
	 * @return True of this method is overrided in the type specified, False if not
	 */
	public boolean isOverridedIn(Class<?> type) {
		try {
			Method m = type.getDeclaredMethod(method.getName(), method.getParameterTypes());
			return m.getDeclaringClass() != method.getDeclaringClass();
		} catch (Throwable t) {
			return false;
		}
	}

	@Override
	public boolean isValid() {
		return this.method != null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T invoke(Object instance, Object... args) {
		if (this.method != null) {
			try {
				return (T) this.method.invoke(instance, args);
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
