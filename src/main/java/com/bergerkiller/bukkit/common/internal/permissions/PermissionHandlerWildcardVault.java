package com.bergerkiller.bukkit.common.internal.permissions;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

/**
 * Used when the Vault plugin is installed with a functional permission handler registered
 */
class PermissionHandlerWildcardVault extends PermissionHandlerWildcard {
    private static final String PERMISSION_TEST_NODE_ROOT = "bkcommonlib.permission.testnode";
    private static final String PERMISSION_TEST_NODE = PERMISSION_TEST_NODE_ROOT + ".test";
    private static final String PERMISSION_TEST_NODE_ALL = PERMISSION_TEST_NODE_ROOT + ".*";

    private final net.milkbowl.vault.permission.Permission vault;

    public PermissionHandlerWildcardVault(net.milkbowl.vault.permission.Permission vault) {
        this.vault = vault;
    }

    @Override
    public boolean hasPermissionNoRecurse(CommandSender sender, String permission) {
        if (vault.hasSuperPermsCompat()) {
            return super.hasPermissionNoRecurse(sender, permission);
        }

        // Initialize the permission (and it's default) prior to check
        org.bukkit.permissions.Permission perm = getOrCreatePermission(permission);

        // Use a call here
        // First, make sure to handle our permission defaults
        if (perm.getDefault().getValue(sender.isOp())) {
            return true;
        }

        // Handle the remainder using Vault
        if (sender instanceof Player) {
            Player p = (Player) sender;
            return vault.playerHas(p.getWorld(), p.getName(), permission);
        } else {
            return vault.has(sender, permission);
        }
    }

    @Override
    protected boolean detectSuperWildcardSupport() {

        // Bugfix for UPerms: hangs on retrieving the non-existent player profile name
        if (vault.getClass().getName().equals("me.TechsCode.UltraPermissions.hooks.pluginHooks.VaultPermissionHook")) {
            return true; // Appears to support it
        }

        // LuckPerms supports wildcards (not really used, we dont use this fallback class for luckperms)
        if (vault.getClass().getName().equals("me.lucko.luckperms.bukkit.vault.LuckPermsVaultPermission")) {
            return true;
        }

        // Perform a little experiment with a random player name
        // Give the player a test node, and see if the *-permission is handled right
        // Also pass in an invalid value to check that there are no inconsistencies
        // Set up the test permission
        org.bukkit.permissions.Permission perm = getOrCreatePermission(PERMISSION_TEST_NODE);
        perm.setDefault(PermissionDefault.FALSE);

        // Find a player that does not have the test permission (this is kinda pointless, but hey, we are secure!)
        final int maxTries = 10000;
        final String world = null;
        final String testPlayerNameBase = "TestPlayer";
        StringBuilder testPlayerNameBldr = new StringBuilder(testPlayerNameBase);
        String testPlayerName = testPlayerNameBase;
        int i;
        for (i = 0; i < maxTries; i++) {
            testPlayerNameBldr.setLength(testPlayerNameBase.length());
            testPlayerNameBldr.append(i);
            testPlayerName = testPlayerNameBldr.toString();
            try {
                if (!vault.playerHas(world, testPlayerName, PERMISSION_TEST_NODE)) {
                    break;
                }
            } catch (Throwable t) {
                // Failure
                i = maxTries;
            }
        }

        // Check for permission failure (perhaps there is a consistent permission thing)
        boolean hasSupport = false;
        if (i < (maxTries - 1)) {
            try {
                // Grant permission with the *-node
                vault.playerAdd(world, testPlayerName, PERMISSION_TEST_NODE_ALL);

                // Adding was successful, ALWAYS do the removal phase
                // See if it had the desired effect
                try {
                    hasSupport = vault.playerHas(world, testPlayerName, PERMISSION_TEST_NODE);
                } catch (Throwable t) {
                }
                // Undo permission change
                try {
                    vault.playerRemove(world, testPlayerName, PERMISSION_TEST_NODE_ALL);
                } catch (Throwable t) {
                }
            } catch (Throwable t) {
            }
        }

        // Undo the previously added Bukkit Permission
        Bukkit.getPluginManager().removePermission(perm);

        return hasSupport;
    }
}
