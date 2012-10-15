package com.bergerkiller.bukkit.common.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.map.MinecraftFont;
import org.bukkit.map.MapFont.CharacterSprite;

public class StringUtil {
	public static final char CHAT_STYLE_CHAR = '§';
	public static int SPACE_WIDTH = getWidth(' ');

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
			String w = "";
			int x = 0, y = 0, z = 0;
			for (int i = 0; i < s.length; i++) {
				switch (s.length - i) {
					case 1:
						z = Integer.parseInt(s[i]);
						break;
					case 2:
						y = Integer.parseInt(s[i]);
						break;
					case 3:
						x = Integer.parseInt(s[i]);
						break;
					default:
						if (!w.isEmpty()) {
							w += "_";
						}
						w += s[i];
						break;
				}
			}
			World world = Bukkit.getServer().getWorld(w);
			if (world == null)
				return null;
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

	public static String[] remove(String[] input, int index) {
		if (index < 0 || index >= input.length) {
			return input;
		}
		String[] rval = new String[input.length - 1];
		System.arraycopy(input, 0, rval, 0, index);
		System.arraycopy(input, index + 1, rval, index, input.length - index - 1);
		return rval;
	}

	@SuppressWarnings("rawtypes")
	public static String combineNames(Set items) {
		return combineNames((Collection) items);
	}

	@SuppressWarnings("rawtypes")
	public static String combineNames(Collection items) {
		if (items.size() == 0)
			return "";
		String[] sitems = new String[items.size()];
		int i = 0;
		for (Object item : items) {
			sitems[i] = item.toString();
			i++;
		}
		return combineNames(sitems);
	}

	public static String combine(String separator, String... lines) {
		StringBuilder builder = new StringBuilder();
		for (String line : lines) {
			if (line != null && line.length() > 0) {
				if (builder.length() != 0)
					builder.append(separator);
				builder.append(line);
			}
		}
		return builder.toString();
	}

	public static String combine(String separator, Collection<String> lines) {
		StringBuilder builder = new StringBuilder();
		for (String line : lines) {
			if (line != null && line.length() > 0) {
				if (builder.length() != 0)
					builder.append(separator);
				builder.append(line);
			}
		}
		return builder.toString();
	}

	public static String combineNames(String... items) {
		if (items.length == 0)
			return "";
		if (items.length == 1)
			return items[0];
		int count = 1;
		String name = "";
		for (String item : items) {
			name += item;
			if (count == items.length - 1) {
				name += " and ";
			} else if (count != items.length) {
				name += ", ";
			}
			count++;
		}
		return name;
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

	@Deprecated
	public static boolean getBool(String name) {
		return ParseUtil.parseBool(name);
	}

	@Deprecated
	public static boolean isBool(String name) {
		return ParseUtil.isBool(name);
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
			index += to.length(); // Move to the end of the replacement
			index = builder.indexOf(from, index);
		}
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
			if (builder.charAt(i) == fromCode) {
				// Next char is a valid color code?
				if (isChatCode(builder.charAt(i + 1))) {
					builder.setCharAt(i, toCode);
					i++;
				}
			}
		}
		return builder.toString();
	}
}
