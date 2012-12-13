package com.bergerkiller.bukkit.common;

import java.util.logging.Level;

import org.bukkit.Bukkit;

public class Common {
	/**
	 * Defines the Minecraft version that runs on the server<br>
	 * If none is specified, this value is an empty String
	 */
	public static final String MC_VERSION;
	static {
		String version = "";
		if (!checkVersion(version)) {
			StringBuilder builder = new StringBuilder();
			for (int a = 0; a < 10; a++) {
				for (int b = 0; b < 10; b++) {
					for (int c = 0; c < 10; c++) {
						// Format:
						// [package].v1_4_5.[trail]
						builder.setLength(0);
						builder.append('v').append(a).append('_').append(b).append('_').append(c);
						version = builder.toString();
						if (checkVersion(version)) {
							a = b = c = 10;
						}
					}
				}
			}
		}
		MC_VERSION = version;
	}
	private static final String MC_VERSION_PACKAGEPART = MC_VERSION.isEmpty() ? "" : ("." + MC_VERSION);
	/**
	 * Defines the net.minecraft.server root path
	 */
	public static final String NMS_ROOT = "net.minecraft.server" + MC_VERSION_PACKAGEPART;
	/**
	 * Defines the org.bukkit.craftbukkit root path
	 */
	public static final String CB_ROOT = "org.bukkit.craftbukkit" + MC_VERSION_PACKAGEPART;

	private static boolean checkVersion(String version) {
		try {
			if (version.isEmpty()) {
				Class.forName("net.minecraft.server.World");
			} else {
				Class.forName("net.minecraft.server." + version + ".World");
			}
			return true;
		} catch (ClassNotFoundException ex) {
			return false;
		}
	}

	/**
	 * Checks whether the version specified is compatible with the Minecraft version used on this server
	 * 
	 * @param version to check, in the v1_4_5 format where 1, 4 and 5 are the version numbers
	 * @return True if the version is compatible, False if not
	 */
	public static boolean isMCVersionCompatible(String version) {
		return MC_VERSION.isEmpty() || version.equals(MC_VERSION);
	}

	/**
	 * BKCommonLib version number, use this to set your dependency version for BKCommonLib-using plugins<br>
	 * <b>Use getVersion() instead if you want the actual, current version! Constants get inlined when compiling!</b>
	 */
	public static final int VERSION = 139;

	/**
	 * Gets the BKCommonLib version number, use this function to compare your own version with the currently installed version
	 * 
	 * @return BKCommonLib version number
	 */
	public static int getVersion() {
		return VERSION;
	}

	/**
	 * Loads one or more classes<br>
	 * Use this method to pre-load certain classes before enabling your plugin
	 * 
	 * @param classNames to load
	 */
	public static void loadClasses(String... classNames) {
		for (String className : classNames) {
			try {
				loadInner(Class.forName(className));
			} catch (ClassNotFoundException ex) {
				throw new RuntimeException("Could not load class '" + className + "' - Update needed?");
			}
		}
	}

	private static void loadInner(Class<?> clazz) {
		for (Class<?> subclass : clazz.getDeclaredClasses()) {
			loadInner(subclass);
		}
	}

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
