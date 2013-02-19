package com.bergerkiller.bukkit.common.utils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Logic operations, such as contains checks and collection-type transformations
 */
public class LogicUtil {
	private static final Map<Class<?>, Class<?>> unboxedToBoxed = new HashMap<Class<?>, Class<?>>();
	private static final Map<Class<?>, Class<?>> boxedToUnboxed = new HashMap<Class<?>, Class<?>>();
	static {
		unboxedToBoxed.put(boolean.class, Boolean.class);
		unboxedToBoxed.put(char.class, Character.class);
		unboxedToBoxed.put(byte.class, Byte.class);
		unboxedToBoxed.put(short.class, Short.class);
		unboxedToBoxed.put(int.class, Integer.class);
		unboxedToBoxed.put(long.class, Long.class);
		unboxedToBoxed.put(float.class, Float.class);
		unboxedToBoxed.put(double.class, Double.class);
		for (Entry<Class<?>, Class<?>> entry : unboxedToBoxed.entrySet()) {
			boxedToUnboxed.put(entry.getValue(), entry.getKey());
		}
	}

	/**
	 * Obtains the unboxed type (int) from a boxed type (Integer)<br>
	 * If the input type has no unboxed type, null is returned
	 * 
	 * @param boxedType to convert
	 * @return the unboxed type
	 */
	public static Class<?> getUnboxedType(Class<?> boxedType) {
		return boxedToUnboxed.get(boxedType);
	}

	/**
	 * Obtains the boxed type (Integer) from an unboxed type (int)<br>
	 * If the input type has no boxed type, null is returned
	 * 
	 * @param unboxedType to convert
	 * @return the boxed type
	 */
	public static Class<?> getBoxedType(Class<?> unboxedType) {
		return unboxedToBoxed.get(unboxedType);
	}

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
	 * Checks whether an element index is within range of a collection
	 * 
	 * @param collection to check
	 * @param index to check
	 * @return True if it is in bounds, False if not
	 */
	public static boolean isInBounds(Collection<?> collection, int index) {
		return collection != null && index >= 0 && index < collection.size();
	}

	/**
	 * Checks whether an element index is within range of an array<br>
	 * Both Object and primitive arrays are supported
	 * 
	 * @param array to check
	 * @param index to check
	 * @return True if it is in bounds, False if not
	 */
	public static boolean isInBounds(Object array, int index) {
		return array != null && index >= 0 && index < Array.getLength(array);
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
	 * Converts a collection to an Array of a possible primitive type<br>
	 * If the type is not primitive, a regular array of Objects is created<br>
	 * Type conversion is possible, allowing a List of String to be turned into Integer[] or int[]<br>
	 * For this reason, this method is slower than toArray, only use it if type conversion is required
	 * 
	 * @param collection to convert
	 * @param componentType of the array to return (can be primitive)
	 * @return new Array containing the elements in the collection, as an Object
	 */
	public static Object toConvertedArray(Collection<?> collection, Class<?> componentType) {
		final int size = collection.size();
		final Iterator<?> iter = collection.iterator();
		if (componentType.isPrimitive()) {
			// Check against all primitive array types
			if (componentType.equals(boolean.class)) {
				boolean[] array = new boolean[size];
				for (int i = 0; i < size; i++) {
					array[i] = ParseUtil.convert(iter.next(), false).booleanValue();
				}
				return array;
			}
			if (componentType.equals(char.class)) {
				char[] array = new char[size];
				for (int i = 0; i < size; i++) {
					array[i] = ParseUtil.convert(iter.next(), '\0').charValue();
				}
				return array;
			}
			if (componentType.equals(byte.class)) {
				byte[] array = new byte[size];
				for (int i = 0; i < size; i++) {
					array[i] = ParseUtil.convert(iter.next(), (byte) 0).byteValue();
				}
				return array;
			}
			if (componentType.equals(short.class)) {
				short[] array = new short[size];
				for (int i = 0; i < size; i++) {
					array[i] = ParseUtil.convert(iter.next(), (short) 0).shortValue();
				}
				return array;
			}
			if (componentType.equals(int.class)) {
				int[] array = new int[size];
				for (int i = 0; i < size; i++) {
					array[i] = ParseUtil.convert(iter.next(), 0).intValue();
				}
				return array;
			}
			if (componentType.equals(long.class)) {
				long[] array = new long[size];
				for (int i = 0; i < size; i++) {
					array[i] = ParseUtil.convert(iter.next(), 0L).longValue();
				}
				return array;
			}
			if (componentType.equals(float.class)) {
				float[] array = new float[size];
				for (int i = 0; i < size; i++) {
					array[i] = ParseUtil.convert(iter.next(), 0f).floatValue();
				}
				return array;
			}
			if (componentType.equals(double.class)) {
				double[] array = new double[size];
				for (int i = 0; i < size; i++) {
					array[i] = ParseUtil.convert(iter.next(), 0.0).doubleValue();
				}
				return array;
			}
			throw new RuntimeException("Unknown primitive type: " + componentType.getName());
		} else {
			Object[] array = createArray(componentType, size);
			for (int i = 0; i < size; i++) {
				array[i] = ParseUtil.convert(iter.next(), componentType);
			}
			return array;
		}
	}

	/**
	 * Converts a collection to an Array
	 * 
	 * @param collection to convert
	 * @param type of the collection and the array to return (can not be primitive)
	 * @return new Array containing the elements in the collection
	 */
	public static <T> T[] toArray(Collection<?> collection, Class<T> type) {
		return collection.toArray(createArray(type, collection.size()));
	}

	/**
	 * A basic retainAll implementation (does not call list.retainAll!)<br>
	 * After this call all elements not contained in elements are removed<br>
	 * Essentially all elements are removed except those contained in the elements Collection
	 * 
	 * @param collection
	 * @param elements to retain
	 * @return True if the list changed, False if not
	 */
	public static boolean retainAll(Collection<?> collection, Collection<?> elements) {
		Iterator<?> iter = collection.iterator();
		boolean changed = false;
		while (iter.hasNext()) {
			if (!elements.contains(iter.next())) {
				iter.remove();
				changed = true;
			}
		}
		return changed;
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
