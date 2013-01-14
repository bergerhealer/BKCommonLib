package com.bergerkiller.bukkit.common.permissions;

import org.bukkit.permissions.PermissionDefault;

public interface IPermissionDefault {

	/**
	 * Gets the full name of this Permission
	 * 
	 * @return Permission name
	 */
	String getName();

	/**
	 * Gets the default value set for this Permission
	 * 
	 * @return Permission default
	 */
	PermissionDefault getDefault();

	/**
	 * Gets the Permission description
	 * 
	 * @return Permission description
	 */
	String getDescription();
}
