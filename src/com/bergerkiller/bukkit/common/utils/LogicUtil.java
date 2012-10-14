package com.bergerkiller.bukkit.common.utils;

/**
 * Logic operations, such as contains checks
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
			if (v == value)
				return true;
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
			if (v == value)
				return true;
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
			if (v == value)
				return true;
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
			if (v == value)
				return true;
		}
		return false;
	}
}
