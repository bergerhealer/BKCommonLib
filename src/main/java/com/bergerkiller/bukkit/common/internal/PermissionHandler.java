package com.bergerkiller.bukkit.common.internal;

import java.util.Locale;
import java.util.logging.Level;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.bergerkiller.bukkit.common.utils.StringUtil;

/**
 * Takes care of Vault permission checks, and additional logic needed to handle permissions.
 * It's main purpose is redirecting permission checks and providing *-wildcard support.
 */
public class PermissionHandler implements PermissionChecker {
	private static final String PERMISSION_TEST_NODE_ROOT = "bkcommonlib.permission.testnode";
	private static final String PERMISSION_TEST_NODE = PERMISSION_TEST_NODE_ROOT + ".test";
	private static final String PERMISSION_TEST_NODE_ALL = PERMISSION_TEST_NODE_ROOT + ".*";
	private boolean hasSuperWildcardSupport = false;
	private boolean vaultEnabled = false;
	private Permission vaultPermission = null;

	public void updateDependency(Plugin plugin, String pluginName, boolean enabled) {
		if (pluginName.equals("Vault")) {
			if (this.vaultEnabled == enabled) {
				return;
			}
			if (enabled) {
				// Enable the support for Vault
				RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServicesManager().getRegistration(Permission.class);
				if (permissionProvider != null) {
					this.vaultPermission = permissionProvider.getProvider();
					this.vaultEnabled = this.vaultPermission != null;
				}
			} else {
				// Disable the support for Vault
				this.vaultPermission = null;
				this.vaultEnabled = false;
			}
			if (this.vaultEnabled) {
				this.hasSuperWildcardSupport = false;

				// Perform a little experiment with a random player name
				// Give the player a test node, and see if the *-permission is handled right
				// Also pass in an invalid value to check that there are no inconsistencies

				// Set up the test permission
				org.bukkit.permissions.Permission perm = findPerm(PERMISSION_TEST_NODE);
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
					if (!this.vaultPermission.playerHas(world, testPlayerName, PERMISSION_TEST_NODE)) {
						break;
					}
				}
				// Check for permission failure (perhaps there is a consistent permission thing)
				if (i < (maxTries - 1)) {
					// Grant permission with the *-node
					this.vaultPermission.playerAdd(world, testPlayerName, PERMISSION_TEST_NODE_ALL);
					// See if it has the desired effect
					this.hasSuperWildcardSupport = this.vaultPermission.playerHas(world, testPlayerName, PERMISSION_TEST_NODE);
					// Undo permission change
					this.vaultPermission.playerRemove(world, testPlayerName, PERMISSION_TEST_NODE_ALL);
				}

				// Undo the previously added Bukkit Permission
				Bukkit.getPluginManager().removePermission(perm);

				// Debug enable message
				String enablemsg;
				if (this.hasSuperWildcardSupport) {
					enablemsg = "Now using Vault for permissions, relying on Permission-plugin handled wildcards";
				} else {
					enablemsg = "Now using Vault for permissions, using it's own wildcard system";
				}
				CommonPlugin.LOGGER_PERMISSIONS.log(Level.INFO, enablemsg);
			}
		}
	}

	private org.bukkit.permissions.Permission findPerm(String node) {
		org.bukkit.permissions.Permission perm = Bukkit.getPluginManager().getPermission(node);
		if (perm == null) {
			// Figure out what permission default to use
			// This is done by checking all *-names, and if they exist, using that default

			// ===================================
			// TRUE found or anything else: TRUE
			// NOT_OP AND OP found: TRUE
			// NOT_OP found: NOT_OP
			// OP found: OP
			// otherwise: FALSE
			// ===================================
			PermissionDefaultFinder finder = new PermissionDefaultFinder();
			permCheckWildcard(finder, null, new StringBuilder(node.length()), node.split("\\."), 0);

			// Use permission default FALSE to avoid OP-players having automatic permissions for *-nodes
			perm = new org.bukkit.permissions.Permission(node, finder.getDefault());
			Bukkit.getPluginManager().addPermission(perm);
		}
		return perm;
	}

	@Override
	public boolean handlePermission(CommandSender sender, String permission) {
		// Initialize the permission (and it's default) prior to check
		org.bukkit.permissions.Permission perm = findPerm(permission);

		// Resort back to the default logic
		if (this.vaultEnabled) {
			// On a new line to avoid possible preliminary field access
			if (!this.vaultPermission.hasSuperPermsCompat()) {
				// Use a call here
				// First, make sure to handle our permission defaults
				if (perm.getDefault().getValue(sender.isOp())) {
					return true;
				}

				// Handle the remainder using Vault
				if (sender instanceof Player) {
					Player p = (Player) sender;
					return this.vaultPermission.playerHas(p.getWorld(), p.getName(), permission);
				} else {
					return this.vaultPermission.has(sender, permission);
				}
			}
		}
		// Resort to the simpler Bukkit Super Permissions
		return sender.hasPermission(permission);
	}

	public boolean hasPermission(CommandSender sender, String[] permissionNode) {
		if (hasSuperWildcardSupport) {
			return handlePermission(sender, StringUtil.combine(".", permissionNode).toLowerCase(Locale.ENGLISH));
		}
		return permCheckWildcard(this, sender, permissionNode);
	}

	public boolean hasPermission(CommandSender sender, String permissionNode) {
		String lowerNode = permissionNode.toLowerCase(Locale.ENGLISH);
		if (handlePermission(sender, lowerNode)) {
			return true;
		}
		// Only if no *-wildcard support is available internally do we check that as well
		return !hasSuperWildcardSupport && permCheckWildcard(this, sender, lowerNode);
	}

	private static boolean permCheckWildcard(PermissionChecker checker, CommandSender sender, String node) {
		return permCheckWildcard(checker, sender, node.split("\\."));
	}

	private static boolean permCheckWildcard(PermissionChecker checker, CommandSender sender, String[] args) {
		// Compute the expected length for the StringBuilder buffer
		int expectedLength = args.length;
		for (String node : args) {
			expectedLength += node.length();
		}
		// Now call the other internal method
		return permCheckWildcard(checker, sender, new StringBuilder(expectedLength), args, 0);
	}

	/**
	 * Performs a recursive permission check while taking *-permissions in account
	 * 
	 * @param sender - pass in the sender to check for
	 * @param root - use a new buffer
	 * @param args - pass in all parts of the permission node
	 * @param argIndex - pass in 0
	 * @return True if permission is granted, False if not
	 */
	private static boolean permCheckWildcard(PermissionChecker checker, CommandSender sender, StringBuilder root, String[] args, int argIndex) {
		// End of the sequence?
		if (argIndex >= args.length) {
			return checker.handlePermission(sender, root.toString());
		}
		int rootLength = root.length();
		if (rootLength != 0) {
			root.append('.');
			rootLength++;
		}
		final int newArgIndex = argIndex + 1;
		// Check permission with original name
		root.append(args[argIndex].toLowerCase(Locale.ENGLISH));
		if (permCheckWildcard(checker, sender, root, args, newArgIndex)) {
			return true;
		}

		// Try with *-signs
		root.setLength(rootLength);
		root.append('*');
		return permCheckWildcard(checker, sender, root, args, newArgIndex);
	}

	private static final class PermissionDefaultFinder implements PermissionChecker {
		private boolean hasTRUE, hasOP, hasNOTOP;

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
		public boolean handlePermission(CommandSender sender, String permission) {
			org.bukkit.permissions.Permission perm = Bukkit.getPluginManager().getPermission(permission);
			if (perm == null) {
				return false;
			}
			switch (perm.getDefault()) {
				case TRUE : this.hasTRUE = true; break;
				case OP : this.hasOP = true; break;
				case NOT_OP : this.hasNOTOP = true; break;
			}
			if (hasOP && hasNOTOP) {
				hasTRUE = true;
			}
			// Quit checking if we found out it's TRUE
			return hasTRUE;
		}
	}
}
