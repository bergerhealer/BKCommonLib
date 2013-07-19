package com.bergerkiller.bukkit.common.bases.mutable;

/**
 * Allows getting and setting an Object type of value, allowing auto-conversion
 * 
 * @param <T>
 */
public abstract class ObjectAbstract<T> {
	/**
	 * Gets the value
	 * 
	 * @return the value
	 */
	public abstract T get();

	/**
	 * Sets the value
	 * 
	 * @param value to set to
	 * @return this instance
	 */
	public abstract ObjectAbstract<T> set(T value);

	/**
	 * Sets the value to null
	 */
	public void clear() {
		set((T) null);
	}
}
