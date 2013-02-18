package com.bergerkiller.bukkit.common;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.AbstractMap.SimpleEntry;

/**
 * A basic ArrayList implementation for storing entries
 * 
 * @param <K> - key type
 * @param <V> - value type
 */
public class EntryList<K, V> extends ArrayList<Entry<K, V>> {
	private static final long serialVersionUID = 1L;

	/**
	 * Adds a single entry to this list
	 * 
	 * @param key of the entry
	 * @param value of the entry
	 */
	public void add(K key, V value) {
		add(new SimpleEntry<K, V>(key, value));
	}
}
