package com.bergerkiller.bukkit.common.server;

import java.io.File;
import java.util.Collection;

public interface CommonServer {

	/**
	 * Tries to initialize the server
	 * 
	 * @return True if initializing was successful, False if not
	 */
	public boolean init();

	/**
	 * Called after the init() method successfully detected the server and initialized the server.
	 * In here processing that depends on the CommonServer instance being fully initialized can be continued.
	 */
	public void postInit();

	/**
	 * Gets the versioning information of the server
	 * 
	 * @return server versioning description
	 */
	public String getServerVersion();

	/**
	 * Gets the full name of the server
	 * 
	 * @return server name
	 */
	public String getServerName();

	/**
	 * Gets a more detailed description of the server, excluding the server version
	 * 
	 * @return server description
	 */
	public String getServerDescription();

	/**
	 * Gets the real Class name for the given Path, allowing Class path translations to occur
	 * 
	 * @param path to the Class to fix
	 * @return the real Class path
	 */
	public String getClassName(String path);

	/**
	 * Obtains the real method name for a given method, allowing name translations to occur
	 * 
	 * @param type of Class the method is in
	 * @param methodName of the method to fix
	 * @param params of the method
	 * @return the (translated) method name
	 */
	public String getMethodName(Class<?> type, String methodName, Class<?>... params);

	/**
	 * Obtains the real field name for a given field, allowing name translations to occur
	 * 
	 * @param type of Class the field is in
	 * @param fieldName of the field to fix
	 * @return the (translated) field name
	 */
	public String getFieldName(Class<?> type, String fieldName);

	/**
	 * Checks whether BKCommonLib is compatible with this server
	 * 
	 * @return True if compatible, False if not
	 */
	public boolean isCompatible();

	/**
	 * Gets the version of Minecraft the server supports
	 * 
	 * @return Minecraft version
	 */
	public String getMinecraftVersion();

	/**
	 * Gets the File Location where the regions of a world are contained
	 * 
	 * @param worldName to get the regions folder for
	 * @return Region folder
	 */
	public File getWorldRegionFolder(String worldName);

	/**
	 * Gets the File Location where a world is contained
	 * 
	 * @return World folder
	 */
	public File getWorldFolder(String worldName);

	/**
	 * Gets a Collection of all worlds that can be loaded without creating it
	 * 
	 * @return Loadable world names
	 */
	public Collection<String> getLoadableWorlds();

	/**
	 * Checks whether the World contained in the folder specified can be loaded
	 * 
	 * @param worldFolder to check
	 * @return True if the world can be loaded, False if not
	 */
	public boolean isLoadableWorld(String worldName);
}
