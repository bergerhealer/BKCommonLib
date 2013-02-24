package com.bergerkiller.bukkit.common.collections;

import java.util.AbstractMap.SimpleEntry;
import java.util.ListIterator;
import java.util.Map.Entry;

import com.bergerkiller.bukkit.common.utils.MathUtil;

/**
 * Uses a Double-Double sorted map system to linearly interpolate between values.
 * For example, if there are two entries put in this map:<br>
 * - (key = 2.0; value = 5.0)<br>
 * - (key = 3.0; value = 12.0)<br>
 * Performing get(2.5) would result in a value of 8.5 to be returned.<br><br>
 * 
 * If a key is out of bounds (lower than the minimum, higher than the maximum) the value is clamped.
 * Performing get(4.0) would result in a value of 12.0 to be returned.<br><br>
 * 
 * No get operations should be performed on an empty map, the behaviour is unspecified.
 */
public class InterpolatedMap {
	private final EntryList<Double, Double> entries = new EntryList<Double, Double>();

	public EntryList<Double, Double> getEntries() {
		return entries;
	}

	/**
	 * Checks whether this Interpolated Map is empty or not
	 * 
	 * @return True if the map is empty, False if not
	 */
	public boolean isEmpty() {
		return entries.isEmpty();
	}

	/**
	 * Performs linear interpolation logic to obtain the value at a key
	 * 
	 * @param key to get the interpolated value of
	 * @return value
	 */
	public double get(double key) {
		ListIterator<Entry<Double, Double>> listiter = entries.listIterator();
		Double dKey = Double.valueOf(key);
		int compare;
		Entry<Double, Double> entry = null;
		while (listiter.hasNext()) {
			entry = listiter.next();
			compare = entry.getKey().compareTo(dKey);
			if (compare == 0) {
				// Exact match, no need to interpolate
				return entry.getValue();
			} else if (compare > 0) {
				// This is the first time the entry key is larger
				// Obtain the previous key if available, otherwise we clamp at this first value
				if (listiter.hasPrevious()) {
					// We have a previous (lower key) value, interpolate
					final Entry<Double, Double> previous = listiter.previous();
					// Obtain a stage in the interpolation (value between 0 and 1)
					final double stage = (key - previous.getKey()) / (entry.getKey() - previous.getKey());
					// Interpolate
					return MathUtil.lerp(previous.getValue(), entry.getValue(), stage);
				} else {
					// No previous value, we are at the start, clamp
					return entry.getValue();
				}
			}
		}
		// We went through all entries, but could not find an entry that was larger
		// Depending on whether the list was empty we return the maximum value, or a constant
		if (entry == null) {
			// Map was empty, return a constant
			return 0.0;
		} else {
			// This is the last entry and also the largest
			return entry.getValue();
		}
	}

	/**
	 * Clears this Interpolated Map
	 */
	public void clear() {
		entries.clear();
	}

	/**
	 * Puts a single entry into this Interpolated Map
	 * 
	 * @param key of the entry
	 * @param value of the entry
	 */
	public void put(double key, double value) {
		ListIterator<Entry<Double, Double>> listiter = entries.listIterator();
		Entry<Double, Double> newEntry = new SimpleEntry<Double, Double>(key, value);
		// Deal with an empty list in a quick manner
		if (entries.isEmpty()) {
			entries.add(newEntry);
			return;
		}
		// Check if the new entry should be put before the very first element
		int comp;
		boolean first = true;
		while (listiter.hasNext()) {
			final Entry<Double, Double> entry = listiter.next();
			comp = newEntry.getKey().compareTo(entry.getKey());
			if (first && comp < 0) {
				entries.add(0, newEntry);
				return;
			} else if (comp == 0) {
				entry.setValue(value);
				return;
			} else if (comp > 0) {
				// The key is now greater than the previously ticked element
				// Set a new element at the index
				listiter.add(newEntry);
				return;
			}
			first = false;
		}
		// Nothing was added this run, add a new entry at the end
		entries.add(newEntry);
	}
}
