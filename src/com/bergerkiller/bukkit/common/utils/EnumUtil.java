package com.bergerkiller.bukkit.common.utils;

import org.bukkit.Difficulty;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.World.Environment;
import org.bukkit.entity.CreatureType;
import org.bukkit.permissions.PermissionDefault;

@SuppressWarnings("deprecation")
public class EnumUtil {
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

	    //from name
    	name = name.trim().toUpperCase().replace(" ", "_").replace("SHOVEL", "SPADE").replace("SLAB", "STEP").replace("GOLDEN", "GOLD");
    	name = name.replace("WOODEN", "WOOD");

    	Material m = parse(Material.class, name, null);
    	if (m != null) return m;
    	if (name.equals("SULPHER")) m = Material.SULPHUR;
    	if (name.equals("SULPHOR")) m = Material.SULPHUR;
    	if (name.equals("CROP")) m = Material.CROPS;
    	if (name.equals("WOODEN_DOOR")) m = Material.WOOD_DOOR;
    	if (name.equals("IRON_DOOR_BLOCK")) m = Material.IRON_DOOR;
    	if (name.equals("REPEATER")) m = Material.DIODE;
    	if (name.equals("REDSTONE_REPEATER")) m = Material.DIODE;
    	if (name.equals("REDSTONE_DUST")) m = Material.REDSTONE;
    	if (name.equals("REDSTONE_TORCH")) m = Material.REDSTONE_TORCH_ON;
    	if (name.equals("STONE_PRESSURE_PLATE")) m = Material.STONE_PLATE;
    	if (name.equals("BUTTON")) m = Material.STONE_BUTTON;
    	if (name.equals("WOOD_PRESSURE_PLATE")) m = Material.WOOD_PLATE;
    	if (name.equals("WOODEN_PRESSURE_PLATE")) m = Material.WOOD_PLATE;
    	if (name.equals("PISTON")) m = Material.PISTON_BASE;
       	if (name.equals("STICKY_PISTON")) m = Material.PISTON_STICKY_BASE;
       	if (name.equals("MOSS_STONE")) m = Material.MOSSY_COBBLESTONE;
       	if (name.equals("STONE_STAIRS")) m = Material.COBBLESTONE_STAIRS;
       	if (name.equals("WOODEN_STAIRS")) m = Material.WOOD_STAIRS;
       	if (name.equals("DIAM_CHESTPLATE")) m = Material.DIAMOND_CHESTPLATE;
       	if (name.equals("DIAM_LEGGINGS")) m = Material.DIAMOND_LEGGINGS;
       	if (name.equals("LEAT_CHESTPLATE")) m = Material.LEATHER_CHESTPLATE;
       	if (name.equals("LEAT_LEGGINGS")) m = Material.LEATHER_LEGGINGS;
       	if (name.equals("LEATHER_PANTS")) m = Material.LEATHER_LEGGINGS;
    	if (name.equals("LIGHTER")) m = Material.FLINT_AND_STEEL;
    	if (name.equals("DOUBLE_SLAB")) m = Material.DOUBLE_STEP;
    	if (name.equals("DOUBLESLAB")) m = Material.DOUBLE_STEP;
    	if (name.equals("BOOK_SHELF")) m = Material.BOOKSHELF;
    	if (name.equals("LIT_PUMPKIN")) m = Material.JACK_O_LANTERN;
    	if (m != null) {
    		return m;
    	} else if (name.endsWith("S")) {
    		return parseMaterial(name.substring(0, name.length() - 1), def);
    	} else {
    		return def;
    	}
	}
}
