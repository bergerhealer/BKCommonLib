package com.bergerkiller.bukkit.common.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.bukkit.DyeColor;
import org.bukkit.GrassSpecies;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.material.Leaves;
import org.bukkit.material.LongGrass;
import org.bukkit.material.MaterialData;
import org.bukkit.material.TexturedMaterial;
import org.bukkit.material.Tree;
import org.bukkit.material.Wool;

import com.bergerkiller.bukkit.common.StringReplaceBundle;
import com.bergerkiller.bukkit.common.conversion.ConversionTable;

public class ParseUtil {
	private static final Set<String> yesValues = new HashSet<String>();
	private static final Set<String> noValues = new HashSet<String>();
	private static final HashMap<String, Material> MAT_SPECIAL = new HashMap<String, Material>();
	private static final StringReplaceBundle MAT_REPLACE = new StringReplaceBundle();

	static {
		yesValues.addAll(Arrays.asList("yes", "allow", "allowed", "true", "ye", "y", "t", "on", "enabled", "enable"));
		noValues.addAll(Arrays.asList("no", "none", "deny", "denied", "false", "n", "f", "off", "disabled", "disable"));
		// Material replacement algorithm
		MAT_REPLACE.add(" ", "_").add("DIAM_", "DIAMOND").add("LEAT_", "LEATHER").add("_", "");
		MAT_REPLACE.add("SHOVEL", "SPADE").add("SLAB", "STEP").add("GOLDEN", "GOLD").add("WOODEN", "WOOD");
		MAT_REPLACE.add("PRESSUREPLATE", "PLATE").add("PANTS", "LEGGINGS");
		MAT_REPLACE.add("REDSTONEDUST", "REDSTONE").add("REDSTONEREPEATER", "DIODE");
		MAT_REPLACE.add("SULPHER", "SULPHUR").add("SULPHOR", "SULPHUR").add("DOORBLOCK", "DOOR").add("REPEATER", "DIODE");
		MAT_REPLACE.add("LIGHTER", "FLINTANDSTEEL").add("LITPUMPKIN", "JACKOLANTERN");
		// Special name cases
		MAT_SPECIAL.put("CROP", Material.CROPS);
		MAT_SPECIAL.put("REDSTONETORCH", Material.REDSTONE_TORCH_ON);
		MAT_SPECIAL.put("BUTTON", Material.STONE_BUTTON);
		MAT_SPECIAL.put("PISTON", Material.PISTON_BASE);
		MAT_SPECIAL.put("STICKPISTON", Material.PISTON_STICKY_BASE);
		MAT_SPECIAL.put("MOSSSTONE", Material.MOSSY_COBBLESTONE);
		MAT_SPECIAL.put("STONESTAIR", Material.COBBLESTONE_STAIRS);
		MAT_SPECIAL.put("SANDSTAIR", Material.SANDSTONE_STAIRS);
		MAT_SPECIAL.put("GOLDAPPLE", Material.GOLDEN_APPLE);
		MAT_SPECIAL.put("APPLEGOLD", Material.GOLDEN_APPLE);
	}

	private static Material parseMaterialMain(String name, Material def) {
		Material m = parseEnum(Material.class, name, null);
		if (m != null) {
			return m;
		}
		m = MAT_SPECIAL.get(name);
		if (m != null)  {
			return m;
		}
		if (name.endsWith("S")) {
			return parseMaterialMain(name.substring(0, name.length() - 1), def);
		} else {
			return def;
		}
	}

	/**
	 * Attempts to filter all non-numeric values from the text specified<br><br>
	 * - Commas are changed to dots<br>
	 * - Text after a space is excluded<br>
	 * - Non-digit information is erased<br>
	 * - Prefixed text is ignored<br>
	 * - A single dot maximum is enforced<br>
	 * - Null input returns an empty String instead
	 * 
	 * @param text to filter
	 * @return filtered text
	 */
	public static String filterNumeric(String text) {
		if (text == null) {
			return "";
		}
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
		if (LogicUtil.nullOrEmpty(text)) {
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
	 * Checks if a given value is a full valid boolean
	 * 
	 * @param text to check
	 * @return True if it is a boolean, False if it isn't
	 */
	public static boolean isBool(String text) {
		text = text.toLowerCase(Locale.ENGLISH).trim();
		return yesValues.contains(text) || noValues.contains(text);
	}

	/**
	 * Parses the text specified to a boolean
	 * 
	 * @param text to parse
	 * @return Parsed value, false when not a known yes value
	 */
	public static boolean parseBool(String text) {
		return yesValues.contains(text.toLowerCase(Locale.ENGLISH).trim());
	}

	/**
	 * Parses the text specified to a boolean
	 * 
	 * @param text to parse
	 * @param def value to return if the text is not a boolean expression
	 * @return Parsed value, or the default
	 */
	public static Boolean parseBool(String text, Boolean def) {
		String val = text.toLowerCase(Locale.ENGLISH).trim();
		if (yesValues.contains(val)) {
			return true;
		} else if (noValues.contains(val)) {
			return false;
		} else {
			return def;
		}
	}

	/**
	 * Tries to parse the text specified to a float
	 * 
	 * @param text to parse
	 * @param def to return on failure
	 * @return Parsed or default value
	 */
	public static float parseFloat(String text, float def) {
		return parseFloat(text, Float.valueOf(def)).floatValue();
	}

	/**
	 * Tries to parse the text specified to a float
	 * 
	 * @param text to parse
	 * @param def to return on failure
	 * @return Parsed or default value
	 */
	public static Float parseFloat(String text, Float def) {
		try {
			return Float.parseFloat(filterNumeric(text));
		} catch (Exception ex) {
			return def;
		}
	}

	/**
	 * Tries to parse the text specified to a double
	 * 
	 * @param text to parse
	 * @param def to return on failure
	 * @return Parsed or default value
	 */
	public static double parseDouble(String text, double def) {
		return parseDouble(text, Double.valueOf(def)).doubleValue();
	}

	/**
	 * Tries to parse the text specified to a double
	 * 
	 * @param text to parse
	 * @param def to return on failure
	 * @return Parsed or default value
	 */
	public static Double parseDouble(String text, Double def) {
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
		return parseLong(text, Long.valueOf(def)).longValue();
	}

	/**
	 * Tries to parse the text specified to a long
	 * 
	 * @param text to parse
	 * @param def to return on failure
	 * @return Parsed or default value
	 */
	public static Long parseLong(String text, Long def) {
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
		return parseInt(text, Integer.valueOf(def)).intValue();
	}

	/**
	 * Tries to parse the text specified to an int
	 * 
	 * @param text to parse
	 * @param def to return on failure
	 * @return Parsed or default value
	 */
	public static Integer parseInt(String text, Integer def) {
		try {
			return Integer.parseInt(filterNumeric(text));
		} catch (Exception ex) {
			return def;
		}
	}

	/**
	 * Tries to parse the text specified to a short
	 * 
	 * @param text to parse
	 * @param def to return on failure
	 * @return Parsed or default value
	 */
	public static float parseShort(String text, short def) {
		return parseShort(text, Short.valueOf(def)).shortValue();
	}

	/**
	 * Tries to parse the text specified to a short
	 * 
	 * @param text to parse
	 * @param def to return on failure
	 * @return Parsed or default value
	 */
	public static Short parseShort(String text, Short def) {
		try {
			return Short.parseShort(filterNumeric(text));
		} catch (Exception ex) {
			return def;
		}
	}

	/**
	 * Tries to parse the text specified to a byte
	 * 
	 * @param text to parse
	 * @param def to return on failure
	 * @return Parsed or default value
	 */
	public static float parseByte(String text, byte def) {
		return parseByte(text, Byte.valueOf(def)).byteValue();
	}

	/**
	 * Tries to parse the text specified to a byte
	 * 
	 * @param text to parse
	 * @param def to return on failure
	 * @return Parsed or default value
	 */
	public static Byte parseByte(String text, Byte def) {
		try {
			return Byte.parseByte(filterNumeric(text));
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
		if (!LogicUtil.nullOrEmpty(timestring)) {
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

	/**
	 * Tries to parse the text to one of the values in the array specified
	 * 
	 * @param values array to look for a value
	 * @param text to parse
	 * @param def to return on failure
	 * @return Parsed or default value
	 */
	public static <T> T parseArray(T[] values, String text, T def) {
		if (LogicUtil.nullOrEmpty(text)) {
			return def;
		}
		text = text.toUpperCase(Locale.ENGLISH).replace("_", "").replace(" ", "");
		String[] names = new String[values.length];
		int i;
		for (i = 0; i < names.length; i++) {
			names[i] = values[i].toString().toUpperCase(Locale.ENGLISH).replace("_", "");
			if (names[i].equals(text)) {
				return values[i];
			}
		}
		for (i = 0; i < names.length; i++) {
			if (names[i].contains(text)) {
				return values[i];
			}
		}
		for (i = 0; i < names.length; i++) {
			if (text.contains(names[i])) {
				return values[i];
			}
		}
		return def;
	}

	/**
	 * Tries to parse the text to one of the values in the Enum specified<br>
	 * <b>The default value is used to obtain the class to look in, it can not be null!</b>
	 * 
	 * @param text to parse
	 * @param def to return on failure
	 * @return Parsed or default value
	 */
	@SuppressWarnings("unchecked")
	public static <T> T parseEnum(String text, T def) {
		return parseEnum((Class<T>) def.getClass(), text, def);
	}

	/**
	 * Tries to parse the text to one of the values in the Enum specified
	 * 
	 * @param enumClass to look for a value
	 * @param text to parse
	 * @param def to return on failure
	 * @return Parsed or default value
	 */
	public static <T> T parseEnum(Class<T> enumClass, String text, T def) {
		if (!enumClass.isEnum()) {
			throw new IllegalArgumentException("Class '" + enumClass.getSimpleName() + "' is not an Enumeration!");
		}
		return parseArray(enumClass.getEnumConstants(), text, def);
	}

	/**
	 * Tries to parse the text to one of the values in the TreeSpecies class
	 * 
	 * @param text to parse
	 * @param def to return on failure
	 * @return Parsed or default value
	 */
	public static TreeSpecies parseTreeSpecies(String text, TreeSpecies def) {
		text = text.toLowerCase(Locale.ENGLISH);
		if (text.contains("oak")) {
			return TreeSpecies.GENERIC;
		} else if (text.contains("pine") || text.contains("spruce")) {
			return TreeSpecies.REDWOOD;
		} else {
			return parseEnum(TreeSpecies.class, text, def);
		}
	}

	/**
	 * Tries to parse the text to one of the values in the Material class
	 * 
	 * @param text to parse
	 * @param def to return on failure
	 * @return Parsed or default value
	 */
	public static Material parseMaterial(String text, Material def) {
		if (LogicUtil.nullOrEmpty(text)) {
			return def;
		}
		// from ID
		try {
			Material m = Material.getMaterial(Integer.parseInt(text));
			return m == null ? def : m;
		} catch (Exception ex) {
		}
		text = MAT_REPLACE.replace(text.trim().toUpperCase(Locale.ENGLISH));
		return parseMaterialMain(text, def);
	}

	/**
	 * Tries to parse the text to a data value for a Material
	 * 
	 * @param text to parse
	 * @param material to parse the text against
	 * @param def to return on failure
	 * @return Parsed or default value
	 */
	public static Byte parseMaterialData(String text, Material material, Byte def) {
		try {
			return Byte.parseByte(text);
		} catch (NumberFormatException ex) {
			if (material == Material.WOOD) {
				TreeSpecies ts = parseTreeSpecies(text, null);
				if (ts != null) {
					return ts.getData();
				}
				return def;
			} else {
				MaterialData dat = material.getNewData((byte) 0);
				if (dat instanceof TexturedMaterial) {
					TexturedMaterial tdat = (TexturedMaterial) dat;
					Material mat = parseMaterial(text, null);
					if (mat == null)
						return def;
					tdat.setMaterial(mat);
				} else if (dat instanceof Wool) {
					Wool wdat = (Wool) dat;
					DyeColor color = parseEnum(DyeColor.class, text, null);
					if (color == null)
						return def;
					wdat.setColor(color);
				} else if (dat instanceof Tree) {
					Tree tdat = (Tree) dat;
					TreeSpecies species = parseTreeSpecies(text, null);
					if (species == null)
						return def;
					tdat.setSpecies(species);
				} else if (dat instanceof Leaves) {
					Leaves tdat = (Leaves) dat;
					TreeSpecies species = parseTreeSpecies(text, null);
					if (species == null)
						return def;
					tdat.setSpecies(species);
				} else if (dat instanceof LongGrass) {
					LongGrass ldat = (LongGrass) dat;
					GrassSpecies species = parseEnum(GrassSpecies.class, text, null);
					if (species == null)
						return def;
					ldat.setSpecies(species);
				} else {
					return def;
				}
				return dat.getData();
			}
		}
	}

	/**
	 * Tries to convert a given Object to the type specified
	 * 
	 * @param object to convert
	 * @param type to convert to
	 * @return The convered object, or null if not possible
	 */
	public static <T> T convert(Object object, Class<T> type) {
		return convert(object, type, null);
	}

	/**
	 * Tries to convert a given Object to the type specified<br>
	 * <b>The default value can not be null!</b>
	 * 
	 * @param object to convert
	 * @param def to return on failure
	 * @return The convered object, or the default if not possible
	 */
	@SuppressWarnings("unchecked")
	public static <T> T convert(Object object, T def) {
		return convert(object, (Class<T>) def.getClass(), def);
	}

	/**
	 * Tries to convert a given Object to the type specified
	 * 
	 * @param object to convert
	 * @param type to convert to
	 * @param def to return on failure
	 * @return The convered object, or the default if not possible
	 */
	public static <T> T convert(Object object, Class<T> type, T def) {
		return ConversionTable.convert(object, type, def);
	}
}
