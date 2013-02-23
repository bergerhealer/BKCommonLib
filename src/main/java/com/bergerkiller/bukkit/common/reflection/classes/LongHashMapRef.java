package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.ArrayList;
import java.util.Collection;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;

public class LongHashMapRef {
	public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("LongHashMap");
	private static final FieldAccessor<Object[]> entriesField = TEMPLATE.getField("entries");
	private static final FieldAccessor<Integer> countField = TEMPLATE.getField("count");
	private static final MethodAccessor<Boolean> contains = TEMPLATE.getMethod("contains", long.class);
	private static final MethodAccessor<Object> get = TEMPLATE.getMethod("getEntry", long.class);
	private static final MethodAccessor<Object> remove = TEMPLATE.getMethod("remove", long.class);
	private static final MethodAccessor<Void> put = TEMPLATE.getMethod("put", long.class, Object.class);

	public static Collection<Object> getValues(Object instance) {
		Object[] entries = getEntries(instance);
		ArrayList<Object> values = new ArrayList<Object>(entries.length);
		for (int i = 0; i < entries.length; i++) {
			if (entries[i] != null) {
				values.add(LongHashMapEntryRef.entryValue.get(entries[i]));
			}
		}
		return values;
	}

	public static Object[] getEntries(Object instance) {
		return entriesField.get(instance);
	}

	public static void setEntries(Object instance, Object[] entries) {
		entriesField.set(instance, entries);
		countField.set(instance, entries.length);
	}

	public static boolean contains(Object instance, long key) {
		return contains.invoke(instance, key);
	}

	public static Object get(Object instance, long key) {
		return get.invoke(instance, key);
	}

	public static Object remove(Object instance, long key) {
		return remove.invoke(instance, key);
	}

	public static void put(Object instance, long key, Object value) {
		put.invoke(instance, key, value);
	}
}
