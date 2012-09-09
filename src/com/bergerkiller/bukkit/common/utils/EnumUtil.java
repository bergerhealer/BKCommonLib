package com.bergerkiller.bukkit.common.utils;

import org.bukkit.Difficulty;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.World.Environment;
import org.bukkit.entity.CreatureType;
import org.bukkit.permissions.PermissionDefault;

import com.bergerkiller.bukkit.common.StringReplaceBundle;

@SuppressWarnings("deprecation")
public class EnumUtil {
	private static final StringReplaceBundle MAT_REPLACE = new StringReplaceBundle();
	static {
		MAT_REPLACE.add(" ", "_").add("DIAM_", "DIAMOND").add("LEAT_", "LEATHER").add("_", "");
		MAT_REPLACE.add("SHOVEL", "SPADE").add("SLAB", "STEP").add("GOLDEN", "GOLD").add("WOODEN", "WOOD");
		MAT_REPLACE.add("PRESSUREPLATE", "PLATE").add("PANTS", "LEGGINGS");
		MAT_REPLACE.add("REDSTONEDUST", "REDSTONE").add("REDSTONEREPEATER", "DIODE");
		MAT_REPLACE.add("SULPHER", "SULPHUR").add("SULPHOR", "SULPHUR").add("DOORBLOCK", "DOOR").add("REPEATER", "DIODE");
		MAT_REPLACE.add("LIGHTER", "FLINTANDSTEEL").add("LITPUMPKIN", "JACKOLANTERN");
	}

	public static <E extends Enum<E>> E parse(Class<E> enumeration, String name, E def) {
		return parse(enumeration.getEnumConstants(), name, def);
	}
	public static <E extends Enum<E>> E parse(E[] values, String name, E def) {
		if (name == null || name.length() == 0) return def;
		name = name.toUpperCase().replace("_", "").replace(" ", "");
		String[] enumNames = new String[values.length];
		int i;
		for (i = 0; i < enumNames.length; i++) {
			enumNames[i] = values[i].toString().toUpperCase().replace("_", "");
			if (enumNames[i].equals(name)) return values[i];
		}
		for (i = 0; i < enumNames.length; i++) {
			if (enumNames[i].contains(name)) return values[i];
		}
		for (i = 0; i < enumNames.length; i++) {
			if (name.contains(enumNames[i])) return values[i];
		}
		return def;
	}
	@SuppressWarnings("unchecked")
	public static <E extends Enum<E>> E parse(String name, E def) {
		return parse((Class<E>) def.getClass(), name, def);
	}
	
	public static PermissionDefault parsePermissionDefault(String name, PermissionDefault def) {
		return parse(PermissionDefault.class, name, def);
	}
	public static GameMode parseGameMode(String name, GameMode def) {
		return parse(GameMode.class, name, def);
	}
	public static Environment parseEnvironment(String name, Environment def) {
		return parse(Environment.class, name, def);
	}
	public static Difficulty parseDifficulty(String name, Difficulty def) {
		return parse(Difficulty.class, name, def);
	}
	public static TreeSpecies parseTreeSpecies(String name, TreeSpecies def) {
		name = name.toLowerCase();
		if (name.contains("oak")) {
			return TreeSpecies.GENERIC;
		} else if (name.contains("pine") || name.contains("spruce")) {
			return TreeSpecies.REDWOOD;
		} else {
			return parse(TreeSpecies.class, name, def);
		}
	}
	public static DyeColor parseDyeColor(String name, DyeColor def) {
		return parse(DyeColor.class, name, def);
	}
	public static CreatureType parseCreatureType(String name, CreatureType def) {
		return parse(CreatureType.class, name, def);
	}
	public static Material parseMaterial(String name, Material def) {
		//from ID
	    try {
	    	Material m = Material.getMaterial(Integer.parseInt(name));
	    	return m == null ? def : m;
	    } catch (Exception ex) {}
	    name = MAT_REPLACE.replace(name.trim().toUpperCase());
	    return parseMaterialMain(name, def);
	}

	private static Material parseMaterialMain(String name, Material def) {
    	Material m = parse(Material.class, name, null);
    	if (m != null) return m;
    	if (name.equals("CROP")) return Material.CROPS;
    	if (name.equals("REDSTONETORCH")) return Material.REDSTONE_TORCH_ON;
    	if (name.equals("BUTTON")) return Material.STONE_BUTTON;
    	if (name.equals("PISTON")) return Material.PISTON_BASE;
       	if (name.equals("STICKYPISTON")) return Material.PISTON_STICKY_BASE;
       	if (name.equals("MOSSSTONE")) return Material.MOSSY_COBBLESTONE;
       	if (name.equals("STONESTAIRS")) return Material.COBBLESTONE_STAIRS;
    	if (name.endsWith("S")) {
    		return parseMaterialMain(name.substring(0, name.length() - 1), def);
    	} else {
    		return def;
    	}
	}
}
