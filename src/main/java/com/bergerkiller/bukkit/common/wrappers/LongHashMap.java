package com.bergerkiller.bukkit.common.wrappers;

import java.util.ArrayList;
import java.util.Collection;

import com.bergerkiller.bukkit.common.reflection.classes.LongHashMapEntryRef;
import com.bergerkiller.bukkit.common.reflection.classes.LongHashMapRef;
import com.bergerkiller.bukkit.common.utils.MathUtil;

/**
 * A wrapper around the internal LongHashMap implementation.
 * This type of HashMap allows storing values at long keys.
 * Ideally, two int values are merged into one long to map against 2D-coordinates.
 * This type of class is used by, for example, the internal chunk storage.
 *
 * @param <V> - Value type
 */
public class LongHashMap<V> extends BasicWrapper {

	public LongHashMap() {
		this(LongHashMapRef.constructor1.newInstance());
	}

	/**
	 * Constructs a new LongHashMap with an initial capacity as specified<br>
	 * <b>Warning: this method was added in v1.54 and is not compatible with MC 1.5.2.</b>
	 * 
	 * @param initialCapacity for the new LongHashMap
	 */
	public LongHashMap(int initialCapacity) {
		this();
		// Initial capacity is 16 by default...if less it is pointless to decrease
		if (initialCapacity > 16) {
			LongHashMapRef.setCapacity.invoke(handle, initialCapacity);
		}
	}

	public LongHashMap(Object handle) {
		this.setHandle(handle);
	}

	public int size() {
		return ((net.minecraft.server.LongHashMap) handle).count();
	}

	/**
	 * Checks whether this Long HashMap contains the coordinates specified.<br>
	 * <b>Warning: this method was added in v1.54 and is not compatible with MC 1.5.2.</b>
	 * 
	 * @param msw - most significant part of the key
	 * @param lsw - least signfificant part of the key
	 * @return True if contained, False if not
	 */
	public boolean contains(int msw, int lsw) {
		return contains(MathUtil.longHashToLong(msw, lsw));
	}

	public boolean contains(long key) {
		return  ((net.minecraft.server.LongHashMap) handle).contains(key);
	}

	/**
	 * Gets the value stored at the coordinates specified.<br>
	 * <b>Warning: this method was added in v1.54 and is not compatible with MC 1.5.2.</b>
	 * 
	 * @param msw - most significant part of the key
	 * @param lsw - least signfificant part of the key
	 * @return The value stored at the key, or null if none stored
	 */
	public V get(int msw, int lsw) {
		return get(MathUtil.longHashToLong(msw, lsw));
	}

	@SuppressWarnings("unchecked")
	public V get(long key) {
		return (V) ((net.minecraft.server.LongHashMap) handle).getEntry(key);
	}

	/**
	 * Removes and obtains the value stored at the coordinates specified.<br>
	 * <b>Warning: this method was added in v1.54 and is not compatible with MC 1.5.2.</b>
	 * 
	 * @param msw - most significant part of the key
	 * @param lsw - least signfificant part of the key
	 * @return The removed value previously stored at the key, or null if none was stored
	 */
	public V remove(int msw, int lsw) {
		return remove(MathUtil.longHashToLong(msw, lsw));
	}

	@SuppressWarnings("unchecked")
	public V remove(long key) {
		return (V) ((net.minecraft.server.LongHashMap) handle).remove(key);
	}

	/**
	 * Puts a value at the coordinates specified.<br>
	 * <b>Warning: this method was added in v1.54 and is not compatible with MC 1.5.2.</b>
	 * 
	 * @param msw - most significant part of the key
	 * @param lsw - least signfificant part of the key
	 * @param value to put at the coordinates
	 */
	public void put(int msw, int lsw, V value) {
		put(MathUtil.longHashToLong(msw, lsw), value);
	}

	public void put(long key, V value) {
		((net.minecraft.server.LongHashMap) handle).put(key, value);
	}

	@SuppressWarnings("unchecked")
	public Collection<V> getValues() {
		Object[] entries = LongHashMapRef.entriesField.get(handle);
		ArrayList<V> values = new ArrayList<V>(size());
		for (int i = 0; i < entries.length; i++) {
			if (entries[i] != null) {
				values.add((V) LongHashMapEntryRef.entryValue.get(entries[i]));
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
