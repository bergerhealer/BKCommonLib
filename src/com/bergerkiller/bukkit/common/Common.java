package com.bergerkiller.bukkit.common;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class Common {
	public static boolean isShowcaseEnabled = false;
	public static boolean isSCSEnabled = false;
	public static Plugin bleedingMobsInstance = null;
	public static final int VERSION = 4;

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
