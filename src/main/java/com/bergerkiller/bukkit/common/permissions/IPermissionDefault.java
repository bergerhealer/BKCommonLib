package com.bergerkiller.bukkit.common.permissions;

import org.bukkit.permissions.PermissionDefault;

public interface IPermissionDefault {

	/**
	 * Gets the full name of this Permission
	 * 
	 * @return Permission name
	 */
	public String getName();

	/**
	 * Gets the default value set for this Permission
	 * 
	 * @return Permission default
	 */
	public PermissionDefault getDefault();

	/**
	 * Gets the Permission description
	 * 
	 * @return Permission description
	 */
	public String getDescription();
}
