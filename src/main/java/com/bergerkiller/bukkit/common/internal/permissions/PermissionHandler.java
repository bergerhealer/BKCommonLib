package com.bergerkiller.bukkit.common.internal.permissions;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

import java.util.Locale;

/**
 * Handles permission checks. If a decent permissions plugin is used probably defers to
 * Bukkit Super permissions. Otherwise, will have its own custom logic for handling * wildcard
 * permissions.
 */
public interface PermissionHandler {

    /**
     * Gets a permission instance of a permission node, creates one if one does not exist.
     * If wildcard permissions are emulated, makes sure the permission node has the default
     * based on the wildcard permission that already exists.
     *
     * @param permissionNode Permission Node to get a permission instance of
     * @return Permission
     */
    Permission getOrCreatePermission(String permissionNode);

    /**
     * Checks whether the sender has permission
     *
     * @param sender CommandSender
     * @param permissionNode Permission Node to check
     * @return True of the Sender has permission for the permission node specified
     */
    boolean hasPermission(CommandSender sender, String permissionNode);

    /**
     * Checks whether the sender has permission
     *
     * @param sender CommandSender
     * @param permissionNodePath Permission Node path components making up the path to check
     * @return True of the Sender has permission for the permission node specified
     */
    default boolean hasPermission(CommandSender sender, String[] permissionNodePath) {
        return hasPermission(sender, String.join(".", permissionNodePath).toLowerCase(Locale.ENGLISH));
    }
}
