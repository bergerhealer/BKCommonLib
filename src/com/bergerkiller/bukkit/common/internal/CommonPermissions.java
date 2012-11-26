package com.bergerkiller.bukkit.common.internal;

import org.bukkit.command.CommandSender;

import ru.tehkode.permissions.PermissionManager;

import com.nijiko.permissions.PermissionHandler;

public class CommonPermissions {
	/*
	 * Permissions 3.* ONLY
	 */
	private static boolean usePermissions = false;
	private static PermissionHandler permissionHandler = null;
	/*
	 * Permissions Ex ONLY
	 */
	private static boolean usePex = false;
	private static PermissionManager pexManager = null;

	public static boolean has(CommandSender sender, String permissionNode) {
		if (usePex) {
			
		} else if (usePermissions) {
			
		} else {
			
		}
		return sender.hasPermission(permissionNode);
	}
}
