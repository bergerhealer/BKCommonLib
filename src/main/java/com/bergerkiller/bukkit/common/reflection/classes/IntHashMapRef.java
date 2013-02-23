package com.bergerkiller.bukkit.common.reflection.classes;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;

/**
 * @deprecated: Use the wrapper instead
 */
@Deprecated
@SuppressWarnings("unchecked")
public class IntHashMapRef {
	public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("IntHashMap");
	private static final MethodAccessor<Object> get = TEMPLATE.getMethod("get", int.class);
	private static final MethodAccessor<Void> put = TEMPLATE.getMethod("a", int.class, Object.class);
	private static final MethodAccessor<Object> remove = TEMPLATE.getMethod("d", int.class);
	private static final MethodAccessor<Object> clear = TEMPLATE.getMethod("c");

	public static <T> T get(Object instance, int key) {
		return (T) get.invoke(instance, key);
	}

	public static <T> T remove(Object instance, int key) {
		return (T) remove.invoke(instance, key);
	}

	public static void put(Object instance, int key, Object value) {
		put.invoke(instance, key, value);
	}

	public static void clear(Object instance) {
		clear.invoke(instance);
	}
}
