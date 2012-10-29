package com.bergerkiller.bukkit.common.reflection;

/**
 * A field implementation that allows direct getting and setting
 */
public abstract class SafeDirectField<T, C> {

	/**
	 * Gets the value of a field from an instance
	 * 
	 * @param instance to get from
	 * @return value of the field in the instance
	 */
	public abstract T get(C instance);

	/**
	 * Sets the value of a field of an instance
	 * 
	 * @param instance to set the field in
	 * @param value to set to
	 */
	public abstract void set(C instance, T value);

	/**
	 * Transfers the value of this field from one instance to another
	 * 
	 * @param from instance to copy from
	 * @param to instance to copy to
	 */
	public void transfer(C from, C to) {
		set(to, get(from));
	}
}
