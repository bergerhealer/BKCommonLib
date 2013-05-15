package com.bergerkiller.bukkit.common;

import java.lang.reflect.Method;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;

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
	 * Defines the net.minecraft.server constant (which is not inlined or relocated).
	 * Implementer note: do NOT change this to a constant or maven shading will rename it.
	 */
	public static final String NMS_ROOT_NONVERSIONED = StringUtil.combine(".", "net", "minecraft", "server");
	/**
	 * Defines the org.bukkit.craftbukkit constant (which is not inlined or relocated).
	 * Implementer note: do NOT change this to a constant or maven shading will rename it.
	 */
	public static final String CB_ROOT_NONVERSIONED = StringUtil.combine(".", "org", "bukkit", "craftbukkit");
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

		// Important: paths defined like this to avoid maven shading relocating it (DO NOT CHANGE!)
		final String NMS_MAIN_CHECKCLASS = ".World";

		// Obtain package version
		StringBuilder builder = new StringBuilder();
		builder.append(NMS_ROOT_NONVERSIONED).append(NMS_MAIN_CHECKCLASS);
		if (CommonUtil.getClass(builder.toString()) == null) {
			int a, b, c;
			for (a = 0; a < 10; a++) {
				for (b = 0; b < 10; b++) {
					for (c = 0; c < 10; c++) {
						// Trim builder back to package path length
						builder.setLength(NMS_ROOT_NONVERSIONED.length() + 1);

						// Format:
						// [package].v1_4_R5.[trail]
						builder.append('v').append(a).append('_').append(b).append('_').append('R').append(c);
						builder.append(NMS_MAIN_CHECKCLASS);

						// Class check and version obtaining
						if (CommonUtil.getClass(builder.toString()) != null) {
							version = builder.substring(NMS_ROOT_NONVERSIONED.length() + 1, builder.length() - NMS_MAIN_CHECKCLASS.length());
							a = b = c = 10;
						}
					}
				}
			}
		}
		String part = version.isEmpty() ? "" : ("." + version);
		MC_VERSION_PACKAGEPART = version;
		NMS_ROOT = NMS_ROOT_NONVERSIONED + part;
		CB_ROOT = CB_ROOT_NONVERSIONED + part;
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
			if (elem.getClassName().startsWith(COMMON_ROOT + ".reflection")) {
				Bukkit.getServer().getLogger().log(Level.SEVERE, "[BKCommonLib] " + msg + " (Update BKCommonLib?)");
				return;
			}
		}
		ex.printStackTrace();
	}
}
