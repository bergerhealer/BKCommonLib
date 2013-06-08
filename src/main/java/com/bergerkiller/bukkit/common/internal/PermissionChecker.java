package com.bergerkiller.bukkit.common.internal;

import org.bukkit.command.CommandSender;

public interface PermissionChecker {
	/**
	 * Handles a single Permission Node check
	 * 
	 * @param sender to check
	 * @param permission to check
	 * @return True if permission was granted, False if not
	 */
	public boolean handlePermission(CommandSender sender, String permission);
}
