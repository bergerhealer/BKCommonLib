package com.bergerkiller.bukkit.common.permissions;

import org.bukkit.permissions.PermissionDefault;

public interface IPermissionDefault {
		
	public String getName();
	public PermissionDefault getDefault();
	public String getDescription();

}
