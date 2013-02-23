package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import com.bergerkiller.bukkit.common.reflection.SafeConstructor;

@SuppressWarnings("unchecked")
public class IntHashMap extends BasicWrapper {
	private static final ClassTemplate<Object> TEMPLATE = new NMSClassTemplate("IntHashMap");
	private static final SafeConstructor<Object> constructor = TEMPLATE.getConstructor();
	private static final MethodAccessor<Object> get = TEMPLATE.getMethod("get", int.class);
	private static final MethodAccessor<Object> remove = TEMPLATE.getMethod("d", int.class);
	private static final MethodAccessor<Void> put = TEMPLATE.getMethod("a", int.class, Object.class);
	private static final MethodAccessor<Object> clear = TEMPLATE.getMethod("c");
	
	public IntHashMap() {
		this.setHandle(constructor.newInstance());
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
	public <T> T get(int key) {
		return (T) get.invoke(handle);
	}
	
	/**
	 * Remove a value
	 * 
	 * @param key Key
	 * @return Value
	 */
	public <T> T remove(int key) {
		return (T) remove.invoke(handle, key);
	}
	
	/**
	 * Put a value in the map
	 * 
	 * @param key Key
	 * @param value Value
	 */
	public void put(int key, Object value) {
		put.invoke(handle, key, value);
	}
	
	/**
	 * Clear the map
	 */
	public void clear() {
		clear.invoke(handle);
	}
}
