package com.bergerkiller.bukkit.common.utils;

import com.bergerkiller.bukkit.common.StringReplaceBundle;
import com.bergerkiller.bukkit.common.collections.StringMap;
import com.bergerkiller.bukkit.common.collections.StringMapCaseInsensitive;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials;
import com.bergerkiller.bukkit.common.internal.legacy.MaterialsByName;
import com.bergerkiller.bukkit.common.wrappers.BlockData;

import org.bukkit.DyeColor;
import org.bukkit.GrassSpecies;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.material.*;

import java.util.Locale;

import static com.bergerkiller.bukkit.common.utils.MaterialUtil.getFirst;
import static com.bergerkiller.bukkit.common.utils.MaterialUtil.getMaterial;

public class ParseUtil {
    private static final StringMapCaseInsensitive<Boolean> BOOL_NAME_MAP = new StringMapCaseInsensitive<>();

    static {
        // Most converters down below rely on this
        CommonBootstrap.initCommonServerAssertCompatibility();

        // Boolean representing text values
        for (String trueValue : new String[]{"yes", "allow", "allowed", "true", "ye", "y", "t", "on", "enabled", "enable"}) {
            BOOL_NAME_MAP.put(trueValue, Boolean.TRUE);
        }
        for (String falseValue : new String[]{"no", "none", "deny", "denied", "false", "n", "f", "off", "disabled", "disable"}) {
            BOOL_NAME_MAP.put(falseValue, Boolean.FALSE);
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
            } else if (c == '-' && rval.length() == 0) {
                rval.append(c);
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
            if (!Character.isDigit(c) && c != '.' && c != ',' && (c != '-' || i > 0)) {
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
        return BOOL_NAME_MAP.containsKey(text);
    }

    /**
     * Parses the text specified to a boolean
     *
     * @param text to parse
     * @return Parsed value, false when not a known yes value
     */
    public static boolean parseBool(String text) {
        return parseBool(text, Boolean.FALSE).booleanValue();
    }

    /**
     * Parses the text specified to a boolean
     *
     * @param text to parse
     * @param def value to return if the text is not a boolean expression
     * @return Parsed value, or the default
     */
    public static Boolean parseBool(String text, Boolean def) {
        return LogicUtil.fixNull(BOOL_NAME_MAP.get(text), def);
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
        return Conversion.toFloat.convert(text, def);
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
        return Conversion.toDouble.convert(text, def);
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
        return Conversion.toLong.convert(text, def);
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
        return Conversion.toInt.convert(text, def);
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
        return Conversion.toShort.convert(text, def);
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
        return Conversion.toByte.convert(text, def);
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
     * <b>The default value is used to obtain the class to look in, it can not
     * be null!</b>
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
        return parseMaterial(text, def, false);
    }

    /**
     * Tries to parse the text to one of the values in the Material class.
     * When legacy is specified and set to True, only legacy material types will be parsed
     * from name.<br>
     * <b>Deprecated: legacy Material API is deprecated will be removed</b>
     * 
     * @param text
     * @param def
     * @param legacy
     * @return Parsed or default value
     */
    @Deprecated
    public static Material parseMaterial(String text, Material def, boolean legacy) {
        if (LogicUtil.nullOrEmpty(text)) {
            return def;
        }

        // From ID
        if (!CommonCapabilities.MATERIAL_ENUM_CHANGES) {
            try {
                return LogicUtil.fixNull(CommonLegacyMaterials.getMaterialFromId(Integer.parseInt(text)), def);
            } catch (Exception ex) {
            }
        }

        // Replace aliases and find the corresponding Material
        String matName = Materials.MAT_ALIASES.replace(text.trim().toUpperCase(Locale.ENGLISH));

        // Stick LEGACY_ in front, if needed
        String matName_legacy = matName;
        if (legacy) {
            matName = "LEGACY_" + matName;
        }

        Material mat;
        while (true) {
            // First consult the name mapping (faster)
            mat = Materials.MAT_NAME_MAP.get(matName);
            if (mat != null) {
                return mat;
            }

            // Parse it (slower)
            if (legacy && !CommonCapabilities.MATERIAL_ENUM_CHANGES) {
                // 1.12.2 or before; enum values don't start with LEGACY_
                mat = parseEnum(Material.class, matName_legacy, null);
            } else {
                // 1.13 or later; enum values start with LEGACY_ and replacement logic applies
                mat = parseEnum(Material.class, matName, null);
            }
            if (mat != null) {
                return mat;
            }

            // Handle a 'multiple' in the name (sadly, no ES)
            if (matName.endsWith("S")) {
                matName = matName.substring(0, matName.length() - 1);
                matName_legacy = matName_legacy.substring(0, matName_legacy.length() - 1);
            } else {
                return def;
            }
        }
    }

    /**
     * Old version of parseMaterialData - please use the int version, as data is
     * more than a byte<br>
     * <b>Deprecated: data values are part of legacy Material API and will be removed</b>
     */
    @Deprecated
    public static Byte parseMaterialData(String text, Material material, Byte def) {
        final int data = parseMaterialData(text, material, -1);
        if (data < 0 || data > 255) {
            return def;
        } else {
            return (byte) data;
        }
    }

    /**
     * Tries to parse the text to a data value for a Material.<br>
     * <b>Deprecated: data values are part of legacy Material API and will be removed</b>
     *
     * @param text to parse
     * @param material to parse the text against
     * @param def to return on failure (hint: use -1)
     * @return Parsed or default value
     */
    @Deprecated
    public static int parseMaterialData(String text, Material material, int def) {
        // If material is not legacy, this method is invalid so return def here all the time.
        if (!MaterialUtil.isLegacyType(material)) {
            return def;
        }

        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            if (material != null && CommonLegacyMaterials.getMaterialName(material).equals("LEGACY_WOOD")) {
                TreeSpecies ts = parseTreeSpecies(text, null);
                if (ts != null) {
                    return ts.getData();
                }
                return def;
            } else {
                MaterialData dat = BlockData.fromMaterialData(material, 0).newMaterialData();
                if (dat instanceof TexturedMaterial) {
                    TexturedMaterial tdat = (TexturedMaterial) dat;
                    Material mat = parseMaterial(text, null);
                    if (mat == null) {
                        return def;
                    }
                    tdat.setMaterial(mat);
                } else if (dat instanceof Wool) {
                    Wool wdat = (Wool) dat;
                    DyeColor color = parseEnum(DyeColor.class, text, null);
                    if (color == null) {
                        return def;
                    }
                    wdat.setColor(color);
                } else if (dat instanceof Tree) {
                    Tree tdat = (Tree) dat;
                    TreeSpecies species = parseTreeSpecies(text, null);
                    if (species == null) {
                        return def;
                    }
                    tdat.setSpecies(species);
                } else if (dat instanceof Leaves) {
                    Leaves tdat = (Leaves) dat;
                    TreeSpecies species = parseTreeSpecies(text, null);
                    if (species == null) {
                        return def;
                    }
                    tdat.setSpecies(species);
                } else if (dat instanceof LongGrass) {
                    LongGrass ldat = (LongGrass) dat;
                    GrassSpecies species = parseEnum(GrassSpecies.class, text, null);
                    if (species == null) {
                        return def;
                    }
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
        return Conversion.convert(object, type, def);
    }

    private static class Materials {
        private static final StringMap<Material> MAT_NAME_MAP = new StringMap<>();
        private static final StringReplaceBundle MAT_ALIASES = new StringReplaceBundle();

        static {
            // Material by name mapping
            for (Material material : MaterialsByName.getAllMaterials()) {
                MAT_NAME_MAP.putUpper(MaterialsByName.getMaterialName(material), material);
            }
            MAT_NAME_MAP.put("REDSTONETORCH", getFirst("REDSTONE_TORCH", "LEGACY_REDSTONE_TORCH_ON"));
            MAT_NAME_MAP.put("BUTTON", Material.STONE_BUTTON);
            MAT_NAME_MAP.put("PISTON", getFirst("PISTON", "LEGACY_PISTON_BASE"));
            MAT_NAME_MAP.put("STICKPISTON", getFirst("STICKY_PISTON", "LEGACY_PISTON_STICKY_BASE"));
            MAT_NAME_MAP.put("MOSSSTONE", Material.MOSSY_COBBLESTONE);
            MAT_NAME_MAP.put("STONESTAIR", Material.COBBLESTONE_STAIRS);
            MAT_NAME_MAP.put("SANDSTAIR", Material.SANDSTONE_STAIRS);
            MAT_NAME_MAP.put("GOLDAPPLE", Material.GOLDEN_APPLE);
            MAT_NAME_MAP.put("APPLEGOLD", Material.GOLDEN_APPLE);
            MAT_NAME_MAP.put("COBBLEFENCE", getFirst("COBBLESTONE_WALL", "LEGACY_COBBLE_WALL"));
            MAT_NAME_MAP.put("STONEFENCE", getFirst("COBBLESTONE_WALL", "LEGACY_COBBLE_WALL"));
            MAT_NAME_MAP.put("COBBLEWALL", getFirst("COBBLESTONE_WALL", "LEGACY_COBBLE_WALL"));
            MAT_NAME_MAP.put("STONEWALL", getFirst("COBBLESTONE_WALL", "LEGACY_COBBLE_WALL"));

            // Legacy names
            MAT_NAME_MAP.put("LEGACY_REDSTONE_TORCH", getMaterial("LEGACY_REDSTONE_TORCH_ON"));
            MAT_NAME_MAP.put("LEGACY_STONE_BUTTON", getMaterial("LEGACY_STONE_BUTTON"));
            MAT_NAME_MAP.put("LEGACY_PISTON", getMaterial("LEGACY_PISTON_BASE"));
            MAT_NAME_MAP.put("LEGACY_STICKPISTON", getMaterial("LEGACY_PISTON_STICKY_BASE"));
            MAT_NAME_MAP.put("LEGACY_MOSSSTONE", getMaterial("LEGACY_MOSSY_COBBLESTONE"));
            MAT_NAME_MAP.put("LEGACY_STONESTAIR", getMaterial("LEGACY_COBBLESTONE_STAIRS"));
            MAT_NAME_MAP.put("LEGACY_SANDSTAIR", getMaterial("LEGACY_SANDSTONE_STAIRS"));
            MAT_NAME_MAP.put("LEGACY_GOLDAPPLE", getMaterial("LEGACY_GOLDEN_APPLE"));
            MAT_NAME_MAP.put("LEGACY_APPLEGOLD", getMaterial("LEGACY_GOLDEN_APPLE"));
            MAT_NAME_MAP.put("LEGACY_COBBLEFENCE", getMaterial("LEGACY_COBBLE_WALL"));
            MAT_NAME_MAP.put("LEGACY_STONEFENCE", getMaterial("LEGACY_COBBLE_WALL"));
            MAT_NAME_MAP.put("LEGACY_COBBLEWALL", getMaterial("LEGACY_COBBLE_WALL"));
            MAT_NAME_MAP.put("LEGACY_STONEWALL", getMaterial("LEGACY_COBBLE_WALL"));
            MAT_NAME_MAP.put("LEGACY_SLAB", getMaterial("LEGACY_STEP"));
            MAT_NAME_MAP.put("LEGACY_DOUBLE_SLAB", getMaterial("LEGACY_DOUBLE_STEP"));
            MAT_NAME_MAP.put("LEGACY_STONE_BRICK", getMaterial("LEGACY_SMOOTH_BRICK"));

            // Material by name aliases
            if (CommonCapabilities.MATERIAL_ENUM_CHANGES) {
                MAT_ALIASES.add("SPADE", "SHOVEL");
                MAT_ALIASES.add("REDSTONEREPEATER", "REPEATER");
                MAT_ALIASES.add("FIREWORK", "FIREWORK_ROCKET");
            } else {
                MAT_ALIASES.add("SLAB", "STEP");
                MAT_ALIASES.add("STONEBRICK", "SMOOTHBRICK");
                MAT_ALIASES.add("PLANK", "WOOD");
                MAT_ALIASES.add("SHOVEL", "SPADE");
                MAT_ALIASES.add("REDSTONEREPEATER", "DIODE");
                MAT_ALIASES.add("REPEATER", "DIODE");
                MAT_ALIASES.add("PRESSUREPLATE", "PLATE");
            }
            MAT_ALIASES.add(" ", "_").add("DIAM_", "DIAMOND").add("LEAT_", "LEATHER").add("_", "");
            MAT_ALIASES.add("PANTS", "LEGGINGS").add("REDSTONEDUST", "REDSTONE");
            MAT_ALIASES.add("SULPHER", "SULPHUR").add("SULPHOR", "SULPHUR").add("DOORBLOCK", "DOOR");
            MAT_ALIASES.add("LIGHTER", "FLINTANDSTEEL").add("LITPUMPKIN", "JACKOLANTERN");
        }
    }
}
