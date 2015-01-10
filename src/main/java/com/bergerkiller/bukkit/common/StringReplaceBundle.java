package com.bergerkiller.bukkit.common;

import java.util.AbstractMap.SimpleEntry;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.bergerkiller.bukkit.common.collections.EntryList;
import com.bergerkiller.bukkit.common.config.ConfigurationNode;

/**
 * Can perform a large amount of String replacements at once.
 * The entries added may not contain null from or to.
 */
public class StringReplaceBundle {
	private EntryList<String, String> entries = new EntryList<String, String>();

	/**
	 * Adds a single entry to the replaced Strings
	 * 
	 * @param from String
	 * @param to String
	 * @return this String Replace Bundle
	 */
	public StringReplaceBundle add(String from, String to) {
		if (from == null) {
			throw new IllegalArgumentException("Can not use a 'from' key of null");
		}
		if (to == null) {
			throw new IllegalArgumentException("Can not use a 'to' value of null");
		}
		this.entries.add(new SimpleEntry<String, String>(from, to));
		return this;
	}

	/**
	 * Gets the replaced value of an entry
	 * 
	 * @param from key of the entry
	 * @return value, or null if not found
	 */
	public String get(String from) {
		for (Entry<String, String> entry : this.entries) {
			if (entry.getKey().equals(from)) {
				return entry.getValue();
			}
		}
		return null;
	}

	/**
	 * Removes a single entry, returning the value if removed
	 * 
	 * @param from key of the entry to remove
	 * @return value of the removed entry, or null if none
	 */
	public String remove(String from) {
		Iterator<Entry<String, String>> iter = this.entries.iterator();
		Entry<String, String> entry;
		while (iter.hasNext()) {
			entry = iter.next();
			if (entry.getKey().equals(from)) {
				iter.remove();
				return entry.getValue();
			}
		}
		return null;
	}

	/**
	 * Performs all replacements contained on the String specified
	 * 
	 * @param input String
	 * @return output String
	 */
	public String replace(String input) {
		StringBuilder output = new StringBuilder(input);
		int index;
		for (Entry<String, String> entry : this.entries) {
			index = 0;
			while ((index = output.indexOf(entry.getKey(), index)) != -1) {
				output.replace(index, index + entry.getKey().length(), entry.getValue());
				index += entry.getValue().length();
			}
		}
		return output.toString();
	}

	/**
	 * Gets all the key/value pairs
	 * 
	 * @return list of key/value pairs
	 */
	public List<Entry<String, String>> getEntries() {
		return this.entries;
	}

	/**
	 * Clears all entries stored
	 * 
	 * @return this bundle
	 */
	public StringReplaceBundle clear() {
		this.entries.clear();
		return this;
	}

	/**
	 * Loads all the entries stored in the configuration node specified
	 * 
	 * @param node to load
	 * @return this bundle
	 */
	public StringReplaceBundle load(ConfigurationNode node) {
		for (String key : node.getKeys()) {
			add(key, node.get(key, key));
		}
		return this;
	}

	/**
	 * Saves all the entries stored to the configuration node specified
	 * 
	 * @param node to save to
	 * @return this bundle
	 */
	public StringReplaceBundle save(ConfigurationNode node) {
		for (Entry<String, String> entry : this.entries) {
			node.set(entry.getKey(), entry.getValue());
		}
		return this;
	}
}
