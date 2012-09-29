package com.bergerkiller.bukkit.common.utils;

public class ParseUtil {
	/**
	 * Attempts to filter all non-numeric values from the text specified<br><br>
	 * - Commas are changed to dots<br>
	 * - Text after a space is excluded<br>
	 * - Non-digit information is erased<br>
	 * - Prefixed text is ignored<br>
	 * - A single dot maximum is enforced
	 * 
	 * @param text to filter
	 * @return filtered text
	 */
	public static String filterNumeric(String text) {
		StringBuilder rval = new StringBuilder(text.length());
		boolean hasComma = false;
		boolean hasDigit = false;
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (Character.isDigit(c)) {
				rval.append(c);
				hasDigit = true;
			} else if (c == ' ') {
				if (hasDigit) {
					break;
				}
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

	/**
	 * Parses a time value from a String. Supported formats:<br>
	 * - Seconds only (can be a double value)<br>
	 * - Minutes:Seconds (int values)<br>
	 * - Hours:Minutes:Seconds (int values)
	 * 
	 * @param timestring to parse
	 * @return time in milliseconds
	 */
	public static long parseTime(String timestring) {
		long rval = 0;
		if (timestring != null && !timestring.isEmpty()) {
			String[] parts = timestring.split(":");
			if (parts.length == 1) {
				//Seconds display only
				rval = (long) (ParseUtil.parseDouble(parts[0], 0.0) * 1000);
			} else if (parts.length == 2) {
				//Min:Sec
				rval = ParseUtil.parseLong(parts[0], 0) * 60000;
				rval += ParseUtil.parseLong(parts[1], 0) * 1000;
			} else if (parts.length == 3) {
				//Hour:Min:Sec
				rval = ParseUtil.parseLong(parts[0], 0) * 3600000;
				rval += ParseUtil.parseLong(parts[1], 0) * 60000;
				rval += ParseUtil.parseLong(parts[2], 0) * 1000;
			}
		}
		return rval;
	}
}
