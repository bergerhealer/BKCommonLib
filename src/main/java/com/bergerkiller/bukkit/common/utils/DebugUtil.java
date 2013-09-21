package com.bergerkiller.bukkit.common.utils;

import org.bukkit.Material;
import org.bukkit.block.Block;

import com.bergerkiller.bukkit.common.TypedValue;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;

/**
 * Contains utilities to debug code, such as logging objects
 */
public class DebugUtil {

	/**
	 * Applies a random material to a block that does not equal the current material<br>
	 * Materials that are used: <i>STONE, DIRT, GRASS, WOOD, LOG, IRON_ORE, IRON_BLOCK, GOLD_BLOCK, DIAMOND_BLOCK</i>
	 * 
	 * @param block to randomize
	 */
	public static void randomizeBlock(Block block) {
		randomizeBlock(block, Material.STONE, Material.DIRT, Material.GRASS, Material.WOOD, Material.LOG, 
				Material.IRON_ORE, Material.IRON_BLOCK, Material.GOLD_BLOCK, Material.DIAMOND_BLOCK);
	}

	/**
	 * Applies a random material from the list to a block that does not equal the current material
	 * 
	 * @param block to randomize
	 * @param materials to pick from
	 */
	public static void randomizeBlock(Block block, Material... materials) {
		while (true) {
			Material mat = materials[(int) (Math.random() * materials.length)];
			if (!MaterialUtil.isType(block, mat)) {
				block.setType(mat);
				break;
			}
		}
	}

	/**
	 * Formats all the properties of a block to a String. Uses format:<br>
	 * <i>#world [#x, #y, #z] #type</i>
	 * 
	 * @param block to format
	 * @return Formatted String
	 */
	public static String formatBlock(Block block) {
		return formatBlock(block, "#world [#x, #y, #z] #type");
	}

	/**
	 * Formats all the properties of a block to a String<br>
	 * <b>#x</b> = <i>Block X</i><br>
	 * <b>#y</b> = <i>Block Y</i><br>
	 * <b>#z</b> = <i>Block Z</i><br>
	 * <b>#world</b> = <i>World name<i/><br>
	 * <b>#typeid</b> = <i>Block Type Id</i><br>
	 * <b>#type</b> = <i>Block Type</i>
	 * 
	 * @param block to format
	 * @param format to use
	 * @return Formatted String
	 */
	public static String formatBlock(Block block, String format) {
		StringBuilder buffer = new StringBuilder(format);
		StringUtil.replaceAll(buffer, "#x", Integer.toString(block.getX()));
		StringUtil.replaceAll(buffer, "#y", Integer.toString(block.getY()));
		StringUtil.replaceAll(buffer, "#z", Integer.toString(block.getZ()));
		StringUtil.replaceAll(buffer, "#world", block.getWorld().getName());
		StringUtil.replaceAll(buffer, "#typeid", Integer.toString(MaterialUtil.getTypeId(block)));
		StringUtil.replaceAll(buffer, "#type", block.getType().toString());
		return buffer.toString();
	}

	/**
	 * Broadcasts HEARTBEAT with the current time<br>
	 * Can be used to check if a ticked object is still alive
	 */
	public static void heartbeat() {
		CommonUtil.broadcast("HEARTBEAT: " + System.currentTimeMillis());
	}

	/**
	 * Gets a debug variable that can be changed using the /debug command
	 * 
	 * @param name of the value
	 * @param value initial (not null)
	 * @return Typed value for the Variable
	 */
	@SuppressWarnings("unchecked")
	public static <T> TypedValue<T> getVariable(String name, T value) {
		return CommonPlugin.getInstance().getDebugVariable(name, (Class<T>) value.getClass(), value);
	}

	/**
	 * Gets a debug variable that can be changed using the /debug command
	 * 
	 * @param name of the value
	 * @param type of value
	 * @param value initial (can be null)
	 * @return Typed value for the Variable
	 */
	public static <T> TypedValue<T> getVariable(String name, Class<T> type, T value) {
		return CommonPlugin.getInstance().getDebugVariable(name, type, value);
	}
}
