package com.bergerkiller.bukkit.common.internal.permissions;

import org.bukkit.command.CommandSender;

import java.util.Locale;

/**
 * Checks the permissions of a single permission node, and does not recurse to check
 * for wildcard permissions.
 */
interface SinglePermissionChecker {

    /**
     * Handles a single Permission Node check
     *
     * @param sender to check
     * @param permission to check
     * @return True if permission was granted, False if not
     */
    boolean hasPermissionNoRecurse(CommandSender sender, String permission);

    default boolean permCheckWildcard(CommandSender sender, String node) {
        return permCheckWildcard(sender, node.split("\\."));
    }

    default boolean permCheckWildcard(CommandSender sender, String[] args) {
        // Compute the expected length for the StringBuilder buffer
        int expectedLength = args.length;
        for (String node : args) {
            expectedLength += node.length();
        }
        // Now call the other internal method
        return permCheckWildcard(sender, new StringBuilder(expectedLength), args, 0);
    }

    /**
     * Performs a recursive permission check while taking *-permissions in
     * account
     *
     * @param sender - pass in the sender to check for
     * @param root - use a new buffer
     * @param args - pass in all parts of the permission node
     * @param argIndex - pass in 0
     * @return True if permission is granted, False if not
     */
    default boolean permCheckWildcard(CommandSender sender, StringBuilder root, String[] args, int argIndex) {
        // Check the permission
        String rootText = root.toString();
        if (!rootText.isEmpty() && hasPermissionNoRecurse(sender, rootText)) {
            return true;
        }
        // End of the sequence?
        if (argIndex >= args.length) {
            return false;
        }
        int rootLength = root.length();
        if (rootLength != 0) {
            root.append('.');
            rootLength++;
        }
        final int newArgIndex = argIndex + 1;
        // Check permission with original name
        root.append(args[argIndex].toLowerCase(Locale.ENGLISH));
        if (permCheckWildcard(sender, root, args, newArgIndex)) {
            return true;
        }

        // Try with *-signs
        root.setLength(rootLength);
        root.append('*');
        return permCheckWildcard(sender, root, args, newArgIndex);
    }
}
