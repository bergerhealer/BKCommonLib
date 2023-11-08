package com.bergerkiller.bukkit.common.internal.permissions;

import com.bergerkiller.bukkit.common.ToggledState;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.Locale;

/**
 * Takes care of Vault permission checks, and additional logic needed to handle
 * permissions. It's main purpose is redirecting permission checks and providing
 * *-wildcard support. Not used if the permissions plugin already natively handles
 * *-wildcard properly. Uses Vault if available.
 */
class PermissionHandlerWildcard implements PermissionHandler, SinglePermissionChecker {
    private boolean hasSuperWildcardSupport = false;
    private final ToggledState needsWildcardCheck = new ToggledState(false);

    /**
     * Can be overridden in an implementation to detect whether *-wildcard permissions are supported
     * by the underlying implementation
     *
     * @return True when *-wildcard support exists, False if not
     */
    protected boolean detectSuperWildcardSupport() {
        return false; // Without Vault there is no way to check this, so assume no
    }

    private boolean hasSuperWildcardSupport() {
        if (this.needsWildcardCheck.clear()) {
            this.hasSuperWildcardSupport = detectSuperWildcardSupport();
        }
        return this.hasSuperWildcardSupport;
    }

    @Override
    public org.bukkit.permissions.Permission getOrCreatePermission(String node) {
        org.bukkit.permissions.Permission perm = Bukkit.getPluginManager().getPermission(node);
        if (perm == null) {
            // Figure out what permission default to use
            // This is done by checking all *-names, and if they exist, using that default
            PermissionDefault permDefault = PermissionDefaultFinder.findDefault(node);
            perm = new org.bukkit.permissions.Permission(node, permDefault);
            Bukkit.getPluginManager().addPermission(perm);
        }
        return perm;
    }

    @Override
    public boolean hasPermissionNoRecurse(CommandSender sender, String permission) {
        // Initialize the permission (and it's default) prior to check
        org.bukkit.permissions.Permission perm = getOrCreatePermission(permission);

        // Resort to the simpler Bukkit Super Permissions
        return sender.hasPermission(perm);
    }

    @Override
    public boolean hasPermission(CommandSender sender, String[] permissionNode) {
        if (hasSuperWildcardSupport()) {
            return hasPermissionNoRecurse(sender, StringUtil.join(".", permissionNode).toLowerCase(Locale.ENGLISH));
        }
        return permCheckWildcard(sender, permissionNode);
    }

    @Override
    public boolean hasPermission(CommandSender sender, String permissionNode) {
        String lowerNode = permissionNode.toLowerCase(Locale.ENGLISH);
        if (hasPermissionNoRecurse(sender, lowerNode)) {
            return true;
        }
        // Only if no *-wildcard support is available internally do we check that as well
        return !hasSuperWildcardSupport() && permCheckWildcard(sender, lowerNode);
    }
}
