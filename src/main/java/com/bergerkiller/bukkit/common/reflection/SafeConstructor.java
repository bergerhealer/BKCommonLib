package com.bergerkiller.bukkit.common.reflection;

import java.lang.reflect.Constructor;

/**
 * A safe version of the Constructor
 * 
 * @param <T> type of Class to construct
 */
public class SafeConstructor<T> {
	private Constructor<T> constructor;

	public SafeConstructor(Class<T> type, Class<?>... parameterTypes) {
		try {
			constructor = type.getConstructor(parameterTypes);
			constructor.setAccessible(true);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Checks whether this Constructor is in a valid state<br>
	 * Only if this return true can this Constructor be used without problems
	 * 
	 * @return True if this constructor is valid, False if not
	 */
	public boolean isValid() {
		return constructor != null;
	}

	/**
	 * Constructs a new Instance
	 * 
	 * @param parameters to use for this Constructor
	 * @return A constructed type
	 * @throws RuntimeException if something went wrong while constructing
	 */
	public T newInstance(Object... parameters) {
		try {
			return constructor.newInstance(parameters);
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
}
