package com.bergerkiller.bukkit.common.internal.permissions;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

/**
 * Recursively queries the wildcard nodes of a permission node to figure out
 * what the permission default should be.
 * <ul>
 *     <li>TRUE found for a node: TRUE</li>
 *     <li>Both NOT_OP and OP for a node: TRUE</li>
 *     <li>NOT_OP found: NOT_OP</li>
 *     <li>OP found: OP</li>
 *     <li>Otherwise: FALSE</li>
 * </ul>
 */
class PermissionDefaultFinder implements SinglePermissionChecker {
    private boolean hasTRUE, hasOP, hasNOTOP;

    public static PermissionDefault findDefault(String permissionNode) {
        PermissionDefaultFinder finder = new PermissionDefaultFinder();
        finder.permCheckWildcard(null, new StringBuilder(permissionNode.length()), permissionNode.split("\\."), 0);
        return finder.getDefault();
    }

    public PermissionDefault getDefault() {
        if (hasTRUE) {
            return PermissionDefault.TRUE;
        } else if (hasOP) {
            return PermissionDefault.OP;
        } else if (hasNOTOP) {
            return PermissionDefault.NOT_OP;
        } else {
            return PermissionDefault.FALSE;
        }
    }

    @Override
    public boolean hasPermissionNoRecurse(CommandSender sender, String permission) {
        org.bukkit.permissions.Permission perm = Bukkit.getPluginManager().getPermission(permission);
        if (perm == null) {
            return false;
        }
        switch (perm.getDefault()) {
            case TRUE:
                this.hasTRUE = true;
                break;
            case OP:
                this.hasOP = true;
                break;
            case NOT_OP:
                this.hasNOTOP = true;
                break;
            default:
                break;
        }
        if (hasOP && hasNOTOP) {
            hasTRUE = true;
        }
        // Quit checking if we found out it's TRUE
        return hasTRUE;
    }
}
