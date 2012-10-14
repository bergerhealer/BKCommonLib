package com.bergerkiller.bukkit.common.utils;

import org.bukkit.Difficulty;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.World.Environment;
import org.bukkit.entity.CreatureType;
import org.bukkit.permissions.PermissionDefault;

@Deprecated
public class EnumUtil {
	@Deprecated
	public static <E extends Enum<E>> E parse(Class<E> enumeration, String name, E def) {
		return ParseUtil.parseEnum(enumeration, name, def);
	}

	@Deprecated
	public static <E extends Enum<E>> E parse(E[] values, String name, E def) {
		return ParseUtil.parseArray(values, name, def);
	}

	@Deprecated
	public static <E extends Enum<E>> E parse(String name, E def) {
		return ParseUtil.parseEnum(name, def);
	}

	@Deprecated
	public static PermissionDefault parsePermissionDefault(String name, PermissionDefault def) {
		return parse(PermissionDefault.class, name, def);
	}

	@Deprecated
	public static GameMode parseGameMode(String name, GameMode def) {
		return parse(GameMode.class, name, def);
	}

	@Deprecated
	public static Environment parseEnvironment(String name, Environment def) {
		return parse(Environment.class, name, def);
	}

	@Deprecated
	public static Difficulty parseDifficulty(String name, Difficulty def) {
		return parse(Difficulty.class, name, def);
	}

	@Deprecated
	public static TreeSpecies parseTreeSpecies(String name, TreeSpecies def) {
		return ParseUtil.parseTreeSpecies(name, def);
	}

	@Deprecated
	public static DyeColor parseDyeColor(String name, DyeColor def) {
		return parse(DyeColor.class, name, def);
	}

	@Deprecated
	public static CreatureType parseCreatureType(String name, CreatureType def) {
		return parse(CreatureType.class, name, def);
	}

	@Deprecated
	public static Material parseMaterial(String name, Material def) {
		return ParseUtil.parseMaterial(name, def);
	}
}
