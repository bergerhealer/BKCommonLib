package com.bergerkiller.bukkit.common.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
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
import com.bergerkiller.bukkit.common.wrappers.nbt.CommonTag;

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
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static <T> T convert(Object object, Class<T> type, T def) {
		if (object == null) {
			return def;
		}
		// Initial cast possible? If so, return there
		if (type.isAssignableFrom(object.getClass())) {
			return type.cast(object);
		}
		// Initial downgrading
		if (object instanceof CommonTag) {
			object = ((CommonTag) object).getData();
		}
		if (object instanceof Map) {
			object = ((Map) object).values();
		}
		// Type conversion: forced casts
		Object rval = def;
		try {
			if (type.equals(String.class)) {
				if (object instanceof Collection) {
					Collection collection = (Collection) object;
					StringBuilder builder = new StringBuilder(collection.size() * 100);
					boolean first = true;
					for (Object element : collection) {
						if (!first) {
							builder.append('\n');
						}
						builder.append(convert(element, String.class, ""));
						first = false;
					}
					rval = builder.toString();
				} else {
					rval = object.toString();
				}
			} else if (type.equals(Material.class)) {
				rval = parseMaterial(object.toString(), (Material) def);
			} else if (type.isEnum()) {
				rval = ParseUtil.parseEnum(type, object.toString(), def);
			} else if (type.isArray()) {
				// If not a collection, use a list with the object as single element
				if (!(object instanceof Collection)) {
					object = Arrays.asList(object);
				}
				// Convert collection to an array
				rval = LogicUtil.toConvertedArray((Collection) object, type.getComponentType());
			} else if (type == Byte.class) {
				if (object instanceof Number) {
					rval = ((Number) object).byteValue();
				} else {
					Integer val = parseInt(object.toString(), (Integer) def);
					rval = val == null ? null : val.byteValue();
				}
			} else if (type == Short.class) {
				if (object instanceof Number) {
					rval = ((Number) object).shortValue();
				} else {
					Integer val = parseInt(object.toString(), (Integer) def);
					rval = val == null ? null : val.shortValue();
				}
			} else if (type == Integer.class) {
				if (object instanceof Number) {
					rval = ((Number) object).intValue();
				} else {
					rval = parseInt(object.toString(), (Integer) def);
				}
			} else if (type == Double.class) {
				if (object instanceof Number) {
					rval = ((Number) object).doubleValue();
				} else {
					rval = parseDouble(object.toString(), (Double) def);
				}
			} else if (type == Float.class) {
				if (object instanceof Number) {
					rval = ((Number) object).floatValue();
				} else {
					rval = parseFloat(object.toString(), (Float) def);
				}
			} else if (type == Long.class) {
				if (object instanceof Number) {
					rval = ((Number) object).longValue();
				} else {
					rval = parseLong(object.toString(), (Long) def);
				}
			}
		} catch (Exception ex) {
			rval = def;
		}
		return (T) rval;
	}
}
