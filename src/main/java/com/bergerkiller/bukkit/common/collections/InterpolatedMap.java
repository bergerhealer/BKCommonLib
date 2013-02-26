package com.bergerkiller.bukkit.common.collections;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

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
	private final List<Entry> entries = new ArrayList<Entry>();

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
		ListIterator<Entry> listiter = entries.listIterator();
		Entry entry = null;
		while (listiter.hasNext()) {
			entry = listiter.next();
			if (entry.key == key) {
				// Exact match, no need to interpolate
				return entry.value;
			} else if (entry.key > key) {
				// This is the first time the entry key is larger
				// Obtain the previous key if available, otherwise we clamp at this first value
				if (listiter.hasPrevious()) {
					// We have a previous (lower key) value, interpolate
					final Entry previous = listiter.previous();
					// Obtain a stage in the interpolation (value between 0 and 1)
					final double stage = (key - previous.key) / (entry.key - previous.key);
					// Interpolate
					return MathUtil.lerp(previous.value, entry.value, stage);
				} else {
					// No previous value, we are at the start, clamp
					return entry.value;
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
			return entry.value;
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
		ListIterator<Entry> listiter = entries.listIterator();
		Entry newEntry = new Entry(key, value);
		// Deal with an empty list in a quick manner
		if (entries.isEmpty()) {
			entries.add(newEntry);
			return;
		}
		// Check if the new entry should be put before the very first element
		while (listiter.hasNext()) {
			final Entry entry = listiter.next();
			if (key < entry.key) {
				// The key is now greater than the previously ticked element
				// Set a new element at the index
				if (listiter.hasPrevious()) {
					listiter.previous();
					listiter.add(newEntry);
				} else {
					entries.add(0, newEntry);
				}
				return;
			} else if (key == entry.key) {
				listiter.set(newEntry);
				return;
			}
		}
		// Nothing was added this run, add a new entry at the end
		entries.add(newEntry);
	}

	private static class Entry {
		public final double key, value;

		public Entry(double key, double value) {
			this.key = key;
			this.value = value;
		}
	}
}
