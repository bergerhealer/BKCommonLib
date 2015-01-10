package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.reflection.classes.IntHashMapRef;

/**
 * Wrapper class for the nms.IntHashMap implementation
 * 
 * @param <T> - value type
 */
public class IntHashMap<T> extends BasicWrapper {

	public IntHashMap() {
		this.setHandle(IntHashMapRef.constructor.newInstance());
	}

	public IntHashMap(Object handle) {
		this.setHandle(handle);
	}

	/**
	 * Get a value
	 * 
	 * @param key Key
	 * @return Value
	 */
	@SuppressWarnings("unchecked")
	public T get(int key) {
		return (T) IntHashMapRef.get.invoke(handle, key);
	}

	/**
	 * Checks whether a key is stored
	 * 
	 * @param key to check
	 * @return True if the key is stored, False if not
	 */
	public boolean contains(int key) {
		return IntHashMapRef.contains.invoke(handle, key);
	}

	/**
	 * Remove a value
	 * 
	 * @param key Key
	 * @return Value
	 */
	@SuppressWarnings("unchecked")
	public T remove(int key) {
		return (T) IntHashMapRef.remove.invoke(handle, key);
	}

	/**
	 * Put a value in the map
	 * 
	 * @param key Key
	 * @param value Value
	 */
	public void put(int key, Object value) {
		IntHashMapRef.put.invoke(handle, key, value);
	}

	/**
	 * Clear the map
	 */
	public void clear() {
		IntHashMapRef.clear.invoke(handle);
	}
}
