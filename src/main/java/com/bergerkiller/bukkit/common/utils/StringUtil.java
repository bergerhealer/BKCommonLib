package com.bergerkiller.bukkit.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.map.MinecraftFont;
import org.bukkit.map.MapFont.CharacterSprite;

public class StringUtil {
	public static final char CHAT_STYLE_CHAR = '\u00A7';
	public static final int SPACE_WIDTH = getWidth(' ');
	public static final String[] EMPTY_ARRAY = new String[0];

	/**
	 * Converts a Location to a destination name.
	 * 
	 * @param loc The Location to convert
	 * @return A string representing the destination name.
	 */
	public static String blockToString(Block block) {
		return block.getWorld().getName() + "_" + block.getX() + "_" + block.getY() + "_" + block.getZ();
	}

	/**
	 * Converts a destination name to a String.
	 * 
	 * @param str The String to convert
	 * @return A Location representing the String.
	 */
	public static Block stringToBlock(String str) {
		try {
			String s[] = str.split("_");
			// Saved data needs at least 4 elements
			if (s.length < 4) {
				return null;
			}
			// Parse xyz from last three parts
			int x = Integer.parseInt(s[s.length - 3]);
			int y = Integer.parseInt(s[s.length - 2]);
			int z = Integer.parseInt(s[s.length - 1]);
			// Parse the world name from first parts
			StringBuilder worldName = new StringBuilder(12);
			for (int i = 0; i < s.length - 3; i++) {
				if (i != 0) {
					worldName.append('_');
				}
				worldName.append(s[i]);
			}
			// World exists? If not, can't get a block there
			World world = Bukkit.getServer().getWorld(worldName.toString());
			if (world == null) {
				return null;
			}
			return world.getBlockAt(x, y, z);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Gets the full width of one or more Strings appended
	 * 
	 * @param text to get the total width of (can be one or more parts)
	 * @return The width of all the text combined
	 */
	public static int getWidth(String... text) {
		int width = 0;
		for (String part : text) {
			char character;
			CharacterSprite charsprite;
			for (int i = 0; i < part.length(); i++) {
				character = part.charAt(i);
				if (character == '\n')
					continue;
				if (character == StringUtil.CHAT_STYLE_CHAR) {
					i++;
					continue;
				} else if (character == ' ') {
					width += SPACE_WIDTH;
				} else {
					charsprite = MinecraftFont.Font.getChar(character);
					if (charsprite != null) {
						width += charsprite.getWidth();
					}
				}
			}
		}
		return width;
	}
	
	/**
	 * Get all numbers from a Sting
	 * 
	 * @param str String to be checked
	 * @return Integers the String contained
	 */
	public static int countNrs(String str) {
		String result = "";
		Pattern pattern = Pattern.compile("\\d+");
		Matcher matcher = pattern.matcher(str);
		
		while(matcher.find()) {
			result += matcher.group();
		}
		
		return result != "" ? Integer.valueOf(result) : 0;
	}

	/**
	 * Gets the Width of a certain character in Minecraft Font
	 * 
	 * @param character to get the width of
	 * @return Character width in pixels
	 */
	public static int getWidth(char character) {
		return MinecraftFont.Font.getChar(character).getWidth();
	}

	public static int firstIndexOf(String text, String... values) {
		return firstIndexOf(text, 0, values);
	}

	public static int firstIndexOf(String text, int startindex, String... values) {
		int i = -1;
		int index;
		for (String value : values) {
			if ((index = text.indexOf(value, startindex)) != -1 && (i == -1 || index < i)) {
				i = index;
			}
		}
		return i;
	}

	/**
	 * Gets a String containing a given text appended n times
	 * 
	 * @param text to fill inside the String
	 * @param n times to put in the String
	 * @return new String with a length of [n * text.length()]
	 */
	public static String getFilledString(String text, int n) {
		StringBuffer outputBuffer = new StringBuffer(text.length() * n);
		for (int i = 0; i < n; i++){
		   outputBuffer.append(text);
		}
		return outputBuffer.toString();
	}

	/**
	 * Gets the text before a given separator in a text
	 * 
	 * @param text to use
	 * @param delimiter to find
	 * @return the text before the delimiter, or an empty String if not found
	 */
	public static String getBefore(String text, String delimiter) {
		int index = text.lastIndexOf(delimiter);
		return index >= 0 ? text.substring(0, index) : "";
	}

	/**
	 * Gets the text after a given separator in a text
	 * 
	 * @param text to use
	 * @param delimiter to find
	 * @return the text after the delimiter, or an empty String if not found
	 */
	public static String getAfter(String text, String delimiter) {
		int index = text.indexOf(delimiter);
		return index >= 0 ? text.substring(index + 1) : "";
	}

	/**
	 * Removes a single elements from an array
	 * 
	 * @param input array
	 * @param index in the array to remove
	 * @return modified array
	 */
	public static String[] remove(String[] input, int index) {
		if (index < 0 || index >= input.length) {
			return input;
		}
		String[] rval = new String[input.length - 1];
		System.arraycopy(input, 0, rval, 0, index);
		System.arraycopy(input, index + 1, rval, index, input.length - index - 1);
		return rval;
	}

	/**
	 * Combines all the items in a set using the 'and' and ',' separators
	 * 
	 * @param items to combine
	 * @return Combined items text
	 */
	@SuppressWarnings("rawtypes")
	public static String combineNames(Set items) {
		return combineNames((Collection) items);
	}

	/**
	 * Combines all the items in a collection using the 'and' and ',' separators
	 * 
	 * @param items to combine
	 * @return Combined items text
	 */
	@SuppressWarnings("rawtypes")
	public static String combineNames(Collection items) {
		// If null or empty collection, return empty String
		if (items == null || items.isEmpty()) {
			return "";
		}
		// If only one item, return just this one item
		if (items.size() == 1) {
			Object item = items.iterator().next();
			return item == null ? "" : item.toString();
		}
		// Build the items into one return value
		StringBuilder rval = new StringBuilder();
		int i = 0;
		for (Object item : items) {
			if (i == items.size() - 1) {
				// Last item: Combine using 'and'
				rval.append(" and ");
			} else if (i > 0) {
				// Middle item: Combine using a comma
				rval.append(", ");
			}
			if (item != null) {
				rval.append(item);
			}
			i++;
		}
		return rval.toString();
	}

	/**
	 * Combines all the items in an array using the 'and' and ',' separators
	 * 
	 * @param items to combine
	 * @return Combined items text
	 */
	public static String combineNames(String... items) {
		return combineNames(Arrays.asList(items));
	}

	/**
	 * Combines all the separate parts together with a separator in between
	 * 
	 * @param separator to put in between the parts
	 * @param parts to combine
	 * @return combined parts separated using the separator
	 */
	public static String combine(String separator, String... parts) {
		return combine(separator, Arrays.asList(parts));
	}

	/**
	 * Combines all the separate parts together with a separator in between
	 * 
	 * @param separator to put in between the parts
	 * @param parts to combine
	 * @return combined parts separated using the separator
	 */
	public static String combine(String separator, Collection<String> parts) {
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for (String line : parts) {
			if (!first) {
				builder.append(separator);
			}
			if (line != null) {
				builder.append(line);
			}
			first = false;
		}
		return builder.toString();
	}

	/**
	 * Converts the arguments to turn "-surrounded parts into a single element
	 */
	public static String[] convertArgs(String[] args) {
		ArrayList<String> tmpargs = new ArrayList<String>(args.length);
		boolean isCommenting = false;
		for (String arg : args) {
			if (!isCommenting && (arg.startsWith("\"") || arg.startsWith("'"))) {
				if (arg.endsWith("\"") && arg.length() > 1) {
					tmpargs.add(arg.substring(1, arg.length() - 1));
				} else {
					isCommenting = true;
					tmpargs.add(arg.substring(1));
				}
			} else if (isCommenting && (arg.endsWith("\"") || arg.endsWith("'"))) {
				arg = arg.substring(0, arg.length() - 1);
				arg = tmpargs.get(tmpargs.size() - 1) + " " + arg;
				tmpargs.set(tmpargs.size() - 1, arg);
				isCommenting = false;
			} else if (isCommenting) {
				arg = tmpargs.get(tmpargs.size() - 1) + " " + arg;
				tmpargs.set(tmpargs.size() - 1, arg);
			} else {
				tmpargs.add(arg);
			}
		}
		return tmpargs.toArray(new String[0]);
	}

	/**
	 * Checks if a given Character is a valid chat formatting code
	 * 
	 * @param character to check
	 * @return True if it is a formatting code, False if not
	 */
	public static boolean isChatCode(char character) {
		return LogicUtil.containsChar(character, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'k', 'l', 'm', 'n', 'o', 'r');
	}

	public static int getSuccessiveCharCount(String value, char character) {
		return getSuccessiveCharCount(value, character, 0, value.length() - 1);
	}

	public static int getSuccessiveCharCount(String value, char character, int startindex) {
		return getSuccessiveCharCount(value, character, startindex, value.length() - startindex - 1);
	}

	public static int getSuccessiveCharCount(String value, char character, int startindex, int endindex) {
		int count = 0;
		for (int i = startindex; i <= endindex; i++) {
			if (value.charAt(i) == character) {
				count++;
			} else {
				break;
			}
		}
		return count;
	}

	public static void replaceAll(StringBuilder builder, String from, String to) {
		int index = builder.indexOf(from);
		while (index != -1) {
			builder.replace(index, index + from.length(), to);
			// Move to the end of the replacement
			index += to.length();
			index = builder.indexOf(from, index);
		}
	}

	/**
	 * Obtains a chat color constant from a given color code<br>
	 * If the code is not part of a constant, the default value is returned
	 * 
	 * @param code of the chat color
	 * @param def to return if not found
	 * @return Chat Color of the code
	 */
	public static ChatColor getColor(char code, ChatColor def) {
		for (ChatColor color : ChatColor.values()) {
			if (code == color.toString().charAt(1)) {
				return color;
			}
		}
		return def;
	}

	/**
	 * Converts color codes such as &5 to the Color code representation
	 * 
	 * @param line to work on
	 * @return converted line
	 */
	public static String ampToColor(String line) {
		return swapColorCodes(line, '&', CHAT_STYLE_CHAR);
	}

	/**
	 * Converts color codes to the ampersand representation, such as &5
	 * 
	 * @param line to work on
	 * @return converted line
	 */
	public static String colorToAmp(String line) {
		return swapColorCodes(line, CHAT_STYLE_CHAR, '&');
	}

	/**
	 * Swaps the color coded character
	 * 
	 * @param line to operate on
	 * @param fromCode to replace
	 * @param toCode to replace fromCode with
	 * @return converted String
	 */
	public static String swapColorCodes(String line, char fromCode, char toCode) {
		StringBuilder builder = new StringBuilder(line);
		for (int i = 0; i < builder.length() - 1; i++) {
			// Next char is a valid color code?
			if (builder.charAt(i) == fromCode && isChatCode(builder.charAt(i + 1))) {
				builder.setCharAt(i, toCode);
				i++;
			}
		}
		return builder.toString();
	}
}
