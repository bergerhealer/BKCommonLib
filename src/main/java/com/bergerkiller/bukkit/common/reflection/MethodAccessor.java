package com.bergerkiller.bukkit.common.reflection;

/**
 * Defines the methods to access a certain method
 */
public interface MethodAccessor<T> {
	/**
	 * Checks whether this Method accessor is in a valid state<br>
	 * Only if this return true can this safe accessor be used without problems
	 * 
	 * @return True if this accessor is valid, False if not
	 */
	boolean isValid();

	/**
	 * Executes the method
	 * 
	 * @param instance of the class the method is in, use null if it is a static method
	 * @param args to use for the method
	 * @return A possible returned value from the method, is always null if the method is a void
	 */
	T invoke(Object instance, Object... args);
}
