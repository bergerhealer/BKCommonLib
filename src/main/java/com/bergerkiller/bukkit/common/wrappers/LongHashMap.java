package com.bergerkiller.bukkit.common.wrappers;

import java.util.ArrayList;
import java.util.Collection;

import com.bergerkiller.bukkit.common.reflection.classes.LongHashMapEntryRef;
import com.bergerkiller.bukkit.common.reflection.classes.LongHashMapRef;

public class LongHashMap<T> extends BasicWrapper {

	public LongHashMap() {
		this(LongHashMapRef.constructor1.newInstance());
	}

	public LongHashMap(Object handle) {
		this.setHandle(handle);
	}

	public int size() {
		return ((net.minecraft.server.LongHashMap) handle).count();
	}

	public boolean contains(long key) {
		return  ((net.minecraft.server.LongHashMap) handle).contains(key);
	}

	@SuppressWarnings("unchecked")
	public T get(long key) {
		return (T) ((net.minecraft.server.LongHashMap) handle).getEntry(key);
	}

	@SuppressWarnings("unchecked")
	public T remove(long key) {
		return (T) ((net.minecraft.server.LongHashMap) handle).remove(key);
	}

	public void put(long key, T value) {
		((net.minecraft.server.LongHashMap) handle).put(key, value);
	}

	@SuppressWarnings("unchecked")
	public Collection<T> getValues() {
		Object[] entries = LongHashMapRef.entriesField.get(handle);
		ArrayList<T> values = new ArrayList<T>(size());
		for (int i = 0; i < entries.length; i++) {
			if (entries[i] != null) {
				values.add((T) LongHashMapEntryRef.entryValue.get(entries[i]));
			}
		}
		return values;
	}

	public long[] getKeys() {
		Object[] entries = LongHashMapRef.entriesField.get(handle);
		long[] keys = new long[size()];
		int keyIndex = 0;
		for (int i = 0; i < entries.length; i++) {
			if (entries[i] != null) {
				keys[keyIndex++] = LongHashMapEntryRef.entryKey.get(entries[i]);
			}
		}
		return keys;
	}
}
