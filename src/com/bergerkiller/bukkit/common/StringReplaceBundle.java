package com.bergerkiller.bukkit.common;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;

/**
 * Can perform a large amount of String replacements at once
 */
public class StringReplaceBundle {
	private List<Entry<String, String>> entries = new ArrayList<Entry<String, String>>();

	/**
	 * Adds a single entry to the replaced Strings
	 * 
	 * @param from String
	 * @param to String
	 * @return this String Replace Bundle
	 */
	public StringReplaceBundle add(String from, String to) {
		this.entries.add(new SimpleEntry<String, String>(from, to));
		return this;
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
}
