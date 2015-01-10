package com.bergerkiller.bukkit.common.reflection;

import com.bergerkiller.bukkit.common.conversion.ConverterPair;

/**
 * Defines the methods to access a certain field
 */
public interface FieldAccessor<T> {
	/**
	 * Checks whether this Field accessor is in a valid state<br>
	 * Only if this return true can this safe accessor be used without problems
	 * 
	 * @return True if this accessor is valid, False if not
	 */
	boolean isValid();

	/**
	 * Gets the value of a field from an instance
	 * 
	 * @param instance to get from
	 * @return value of the field in the instance
	 */
	T get(Object instance);

	/**
	 * Sets the value of a field of an instance
	 * 
	 * @param instance to set the field in
	 * @param value to set to
	 * @return True if setting was successful, False if not
	 */
	boolean set(Object instance, T value);

	/**
	 * Transfers the value of this field from one instance to another
	 * 
	 * @param from instance to copy from
	 * @param to instance to copy to
	 * @return the old value in the to instance
	 */
	T transfer(Object from, Object to);

	/**
	 * Translates the get and set types using a converter pair
	 * 
	 * @param converterPair to use for the translation
	 * @return translated Field accessor
	 */
	<K> TranslatorFieldAccessor<K> translate(ConverterPair<?, K> converterPair);
}
