package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.ArrayList;
import java.util.Collection;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;

public class LongHashMapRef {
	public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("LongHashMap");
	private static final FieldAccessor<Object[]> entriesField = TEMPLATE.getField("entries");
	private static final FieldAccessor<Integer> countField = TEMPLATE.getField("count");

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
}
