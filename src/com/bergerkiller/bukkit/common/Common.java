package com.bergerkiller.bukkit.common;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class Common {
	public static boolean isShowcaseEnabled = false;
	public static boolean isSCSEnabled = false;
	public static Plugin bleedingMobsInstance = null;
	public static final int VERSION = 5;

	/**
	 * Handles a reflection field or method missing<br>
	 * Has a special handler for fields and methods defined inside this library
	 * 
	 * @param type of object: field or method 
	 * @param name of the field or method
	 * @param source class for the field or method
	 */
	protected static void handleReflectionMissing(String type, String name, Class<?> source) {
		String msg = type + " '" + name + "' does not exist in class file " + source.getSimpleName();
		Exception ex = new Exception(msg);
		for (StackTraceElement elem : ex.getStackTrace()) {
			if (elem.getClassName().startsWith("com.bergerkiller.bukkit.common.reflection")) {
				Bukkit.getServer().getLogger().log(Level.SEVERE, "[BKCommonLib] " + msg + " (Update BKCommonLib?)");
				return;
			}
		}
		ex.printStackTrace();
	}
}
