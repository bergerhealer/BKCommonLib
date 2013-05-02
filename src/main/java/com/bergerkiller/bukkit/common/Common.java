package com.bergerkiller.bukkit.common;

import java.lang.reflect.Method;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import com.bergerkiller.bukkit.common.utils.CommonUtil;

public class Common {
	/**
	 * Defines the Minecraft version that runs on the server.
	 * To get the package path used, use {@link MC_VERSION_PACKAGEPART} instead.
	 * The package path version is more accurate to use for version checking.
	 */
	public static final String MC_VERSION;
	/**
	 * Defines the Minecraft version, parsed to the package addition, that runs on the server.
	 * If none is specified, this value is an empty String.
	 * Otherwise, the value is of the format: v1_2_R3
	 */
	public static final String MC_VERSION_PACKAGEPART;
	/**
	 * Defines the net.minecraft.server root path
	 */
	public static final String NMS_ROOT;
	/**
	 * Defines the org.bukkit.craftbukkit root path
	 */
	public static final String CB_ROOT;
	/**
	 * Defines the com.bergerkiller.bukkit.common root path of this library
	 */
	public static final String COMMON_ROOT;
	/**
	 * Gets whether the current server software used is the Spigot implementation
	 */
	public static final boolean IS_SPIGOT_SERVER;

	static {
		String version = "";
		if (!checkVersion(version)) {
			StringBuilder builder = new StringBuilder();
			int a, b, c;
			for (a = 0; a < 10; a++) {
				for (b = 0; b < 10; b++) {
					for (c = 0; c < 10; c++) {
						// Format:
						// [package].v1_4_R5.[trail]
						builder.setLength(0);
						builder.append('v').append(a).append('_').append(b).append('_').append('R').append(c);
						version = builder.toString();
						if (checkVersion(version)) {
							a = b = c = 10;
						}
					}
				}
			}
		}
		String part = version.isEmpty() ? "" : ("." + version);
		MC_VERSION_PACKAGEPART = version;
		NMS_ROOT = "net.minecraft.server" + part;
		CB_ROOT = "org.bukkit.craftbukkit" + part;
		COMMON_ROOT = "com.bergerkiller.bukkit.common";
		IS_SPIGOT_SERVER = CommonUtil.getCBClass("Spigot") != null;
		// Find out the MC_VERSION using the CraftServer
		try {
			// Load required classes
			Class<?> server = CommonUtil.getCBClass("CraftServer");
			Class<?> minecraftServer = CommonUtil.getNMSClass("MinecraftServer");
			// Get methods and instances
			Method getServer = server.getDeclaredMethod("getServer");
			Object minecraftServerInstance = getServer.invoke(Bukkit.getServer());
			Method getVersion = minecraftServer.getDeclaredMethod("getVersion");
			// Get the version
			version = (String) getVersion.invoke(minecraftServerInstance);
		} catch (Throwable t) {
		}
		MC_VERSION = version;
	}

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
	 * Checks whether the version specified is compatible with the
	 * Minecraft version used on this server
	 * 
	 * @param version to check, in the v1_4_5_R2 format where 1, 4 and 5
	 * 			are the version numbers and 2 is the build revision
	 * @return True if the version is compatible, False if not
	 */
	public static boolean isMCVersionCompatible(String version) {
		return MC_VERSION_PACKAGEPART.isEmpty() || version.equals(MC_VERSION_PACKAGEPART);
	}

	/**
	 * BKCommonLib version number, use this to set your dependency version 
	 * for BKCommonLib-using plugins<br>
	 * <b>Use getVersion() instead if you want the actual, current version!
	 * Constants get inlined when compiling!</b>
	 */
	public static final int VERSION = 152;

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
