package com.bergerkiller.bukkit.common.utils;

public class ParseUtil {
	/**
	 * Attempts to filter all non-numeric values from the text specified<br><br>
	 * - Commas are changed to dots<br>
	 * - Text after a space is excluded<br>
	 * - Non-digit information is erased<br>
	 * - A single dot maximum is enforced
	 * 
	 * @param text to filter
	 * @return filtered text
	 */
	public static String filterNumeric(String text) {
		StringBuilder rval = new StringBuilder(text.length());
		boolean hasComma = false;
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (Character.isDigit(c)) {
				rval.append(c);
			} else if (c == ' ') {
				break;
			} else if ((c == ',' || c == '.') && !hasComma) {
				rval.append('.');
				hasComma = true;
			}
		}
		return rval.toString();
	}

	/**
	 * Checks if the given value is a full valid number
	 * 
	 * @param text to check
	 * @return True if it is a number, False if it isn't
	 */
	public static boolean isNumeric(String text) {
		if (text == null || text.isEmpty()) {
			return false;
		}
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (!Character.isDigit(c) && c != '.' && c != ',') {
				return false;
			}
		}
		return true;
	}

	/**
	 * Tries to parse the text specified to a double
	 * 
	 * @param text to parse
	 * @param def to return on failure
	 * @return Parsed or default value
	 */
	public static double parseDouble(String text, double def) {
		try {
			return Double.parseDouble(filterNumeric(text));
		} catch (Exception ex) {
			return def;
		}
	}

	/**
	 * Tries to parse the text specified to a long
	 * 
	 * @param text to parse
	 * @param def to return on failure
	 * @return Parsed or default value
	 */
	public static long parseLong(String text, long def) {
		try {
			return Long.parseLong(filterNumeric(text));
		} catch (Exception ex) {
			return def;
		}
	}

	/**
	 * Tries to parse the text specified to an int
	 * 
	 * @param text to parse
	 * @param def to return on failure
	 * @return Parsed or default value
	 */
	public static int parseInt(String text, int def) {
		try {
			return Integer.parseInt(filterNumeric(text));
		} catch (Exception ex) {
			return def;
		}
	}
}
