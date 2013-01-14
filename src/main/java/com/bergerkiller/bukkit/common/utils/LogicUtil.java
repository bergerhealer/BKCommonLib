package com.bergerkiller.bukkit.common.utils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.minecraft.server.v1_4_6.ItemStack;

/**
 * Logic operations, such as contains checks and collection-type transformations
 */
public class LogicUtil {
	/**
	 * Checks if both values are null or the values equal each other
	 * 
	 * @param value1 to use
	 * @param value2 to use
	 * @return True if value1 and value2 equal or are both null, False if not
	 */
	public static boolean bothNullOrEqual(Object value1, Object value2) {
		return value1 == null ? value2 == null : value1.equals(value2);
	}

	/**
	 * Checks if a Map is null or empty
	 * 
	 * @param map to check
	 * @return True if the collection is null or empty
	 */
	public static boolean nullOrEmpty(Map<?, ?> map) {
		return map == null || map.isEmpty();
	}

	/**
	 * Checks if a Collection is null or empty
	 * 
	 * @param collection to check
	 * @return True if the collection is null or empty
	 */
	public static boolean nullOrEmpty(Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}

	/**
	 * Checks if a String is null or empty
	 * 
	 * @param text to check
	 * @return True if the text is null or empty
	 */
	public static boolean nullOrEmpty(String text) {
		return text == null || text.isEmpty();
	}

	/**
	 * Checks if an Item Stack is null or empty
	 * 
	 * @param item to check
	 * @return True if the item is null or empty
	 */
	public static boolean nullOrEmpty(ItemStack item) {
		return item == null || item.id == 0 || item.count < 1;
	}

	/**
	 * Checks if an Item Stack is null or empty
	 * 
	 * @param item to check
	 * @return True if the item is null or empty
	 */
	public static boolean nullOrEmpty(org.bukkit.inventory.ItemStack item) {
		return item == null || item.getTypeId() == 0 || item.getAmount() < 1;
	}

	/**
	 * Checks if an array is null or empty
	 * 
	 * @param array to check
	 * @return True if the item is null or empty
	 */
	public static boolean nullOrEmpty(Object[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * Returns the default value if the input value is null
	 * 
	 * @param value to fix
	 * @param def to return if the value is null
	 * @return the value or the default
	 */
	public static <T> T fixNull(T value, T def) {
		return value == null ? def : value;
	}

	/**
	 * Tries to get a specific element from a list. 
	 * The default value is returned when:<br>
	 * - The list is null<br>
	 * - The list index is out of bounds
	 * 
	 * @param list to get an element from
	 * @param index of the element to get
	 * @param def value to return on failure
	 * @return The list element, or the default value
	 */
	public static <T> T getList(List<T> list, int index, T def) {
		if (index < 0 || list == null || index >= list.size()) {
			return def;
		} else {
			return list.get(index);
		}
	}

	/**
	 * Tries to get a specific element from an array. 
	 * The default value is returned when:<br>
	 * - The array is null<br>
	 * - The array index is out of bounds
	 * 
	 * @param array to get an element from
	 * @param index of the element to get
	 * @param def value to return on failure
	 * @return The array element, or the default value
	 */
	public static <T> T getArray(T[] array, int index, T def) {
		if (index < 0 || array == null || index >= array.length) {
			return def;
		} else {
			return array[index];
		}
	}

	/**
	 * Constructs a new 1-dimensional Array of a given type and length
	 * 
	 * @param type of the new Array
	 * @param length of the new Array
	 * @return new Array
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] createArray(Class<T> type, int length) {
		return (T[]) Array.newInstance(type, length);
	}

	/**
	 * Converts a collection to an Array
	 * 
	 * @param collection to convert
	 * @param type of the collection and the array to return
	 * @return new Array containing the elements in the collection
	 */
	public static <T> T[] toArray(Collection<T> collection, Class<T> type) {
		return collection.toArray(createArray(type, collection.size()));
	}

	/**
	 * Checks whether one map contains all the contents of another map
	 * 
	 * @param map to check for contents
	 * @param contents to check the map for
	 * @return True if all contents are contained in the map, False if not
	 */
	public static boolean containsAll(Map<?, ?> map, Map<?, ?> contents) {
		for (Map.Entry<?, ?> entry : contents.entrySet()) {
			Object value = map.get(entry.getKey());
			// Null value stored in the map?
			if (value == null) {
				if (entry.getValue() != null || !map.containsKey(entry.getKey())) {
					return false;
				}
			} else if (!value.equals(entry.getValue())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if a list of values contains the value specified
	 * 
	 * @param value to find
	 * @param values to search in
	 * @return True if it is contained, False if not
	 */
	public static <T> boolean contains(T value, T... values) {
		for (T v : values) {
			if (bothNullOrEqual(v, value)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if a list of bytes contains the byte specified
	 * 
	 * @param value to find
	 * @param values to search in
	 * @return True if it is contained, False if not
	 */
	public static boolean containsByte(byte value, byte... values) {
		for (int v : values) {
			if (v == value) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if a list of characters contains the character specified
	 * 
	 * @param value to find
	 * @param values to search in
	 * @return True if it is contained, False if not
	 */
	public static boolean containsChar(char value, char... values) {
		for (int v : values) {
			if (v == value) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if a list of integers contains the integer specified
	 * 
	 * @param value to find
	 * @param values to search in
	 * @return True if it is contained, False if not
	 */
	public static boolean containsInt(int value, int... values) {
		for (int v : values) {
			if (v == value) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if a list of booleans contains the boolean specified
	 * 
	 * @param value to find
	 * @param values to search in
	 * @return True if it is contained, False if not
	 */
	public static boolean containsBool(byte value, byte... values) {
		for (int v : values) {
			if (v == value) {
				return true;
			}
		}
		return false;
	}
}
