package com.bergerkiller.bukkit.common.reflection;

import java.lang.reflect.Constructor;

import com.bergerkiller.bukkit.common.conversion.Converter;

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

	public SafeConstructor(Constructor<T> constructor) {
		this.constructor = constructor;
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

	/**
	 * Obtains a new Class Contructor that uses this contructor and converts the output
	 * 
	 * @param converter to use for the output
	 * @return translated output
	 */
	@SuppressWarnings("unchecked")
	public <K> SafeConstructor<K> translateOutput(final Converter<K> converter) {
		return new SafeConstructor<K>((Constructor<K>) this.constructor) {
			@Override
			public K newInstance(Object... parameters) {
				return converter.convert(super.newInstance(parameters));
			}
		};
	}
}
