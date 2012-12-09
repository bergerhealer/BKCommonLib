package com.bergerkiller.bukkit.common;

import java.util.logging.Level;

import org.bukkit.Bukkit;

import com.bergerkiller.bukkit.nover.NoVerClassLoader;

public class Common {
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

	static {
		NoVerClassLoader.MC_VERSION = MC_VERSION;
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
	 * Checks whether the version specified is compatible with the Minecraft version used on this server
	 * 
	 * @param version to check, in the v1_4_5 format where 1, 4 and 5 are the version numbers
	 * @return True if the version is compatible, False if not
	 */
	public static boolean isMCVersionCompatible(String version) {
		return MC_VERSION.isEmpty() || version.equals(MC_VERSION);
	}

	/**
	 * Automatically applies package versioning to the classes included in your plugin<br>
	 * An additional class loader will be inserted to load these classes<br><br>
	 * 
	 * It is recommended to include a static block in your main plugin class where you call this method<br>
	 * Make sure that no native classes are exposed through your main plugin class, as those will not be caught in time<br><br>
	 * 
	 * Alternatively, you could include the following line in your plugin.yml:<br>
	 * <i>class-loader-of: BKCommonLib</i><br>
	 * 
	 * <b>Example:</b>
	 * <pre>
	 * {@code
	 * static {
	 *     Common.undoPackageVersioning();
	 * }
	 * </pre>
	 * 
	 * @param pluginClass - the main plugin class instance of your plugin
	 */
	public static void undoPackageVersioning(Class<?> pluginClass) {
		NoVerClassLoader.undoPackageVersioning(pluginClass);
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
