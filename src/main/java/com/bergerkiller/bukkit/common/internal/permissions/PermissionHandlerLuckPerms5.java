package com.bergerkiller.bukkit.common.internal.permissions;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.Result;
import net.luckperms.api.node.Node;
import net.luckperms.api.platform.PlayerAdapter;
import net.luckperms.api.util.Tristate;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.EnumMap;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Used on servers running Luckperms 5.0.39 or later, which internally
 * natively handle * wildcard permissions and no special logic is needed there for that.
 * But if no permission rule is set, the bukkit permission defaults don't work, so for
 * that we do have to add special logic.
 */
class PermissionHandlerLuckPerms5 implements PermissionHandler {
    private final PlayerAdapter<Player> playerAdapter;
    private final ConcurrentHashMap<String, DefaultChecker> fallbackDefaults = new ConcurrentHashMap<>();

    public PermissionHandlerLuckPerms5() {
        this.playerAdapter = LuckPermsProvider.get().getPlayerAdapter(Player.class);
    }

    @Override
    public Permission getOrCreatePermission(String permissionNode) {
        Permission perm = Bukkit.getPluginManager().getPermission(permissionNode);
        if (perm == null) {
            perm = new Permission(permissionNode, PermissionDefault.FALSE);
            Bukkit.getPluginManager().addPermission(perm);
        }
        return perm;
    }

    @Override
    public boolean hasPermission(CommandSender sender, String permissionNode) {
        // Make sure it is lowercased first, as is done in the wildcard fallback code
        permissionNode = permissionNode.toLowerCase(Locale.ENGLISH);

        // If not a Player do the usual stuff.
        // When a permission is defined this can be optimized, as there is no special
        // wildcard handling for the * nodes in that case. It'll already be handled
        // by luckperms itself.
        {
            Permission permission = Bukkit.getPluginManager().getPermission(permissionNode);
            if (permission != null) {
                return sender.hasPermission(permission);
            }
        }

        // Query permission for players, which tells whether permission is granted and whether a node is configured in luckperms
        if (sender instanceof Player) {
            Result<Tristate, Node> result = playerAdapter.getPermissionData((Player) sender).queryPermission(permissionNode);
            if (result.node() != null) {
                return result.result().asBoolean();
            }
        }

        return hasDefaultPermission(sender, permissionNode);
    }

    private boolean hasDefaultPermission(CommandSender sender, String permissionNode) {
        return fallbackDefaults.computeIfAbsent(permissionNode,
                    s -> checker(PermissionDefaultFinder.findDefault(s)))
                .hasPermission(sender);
    }

    private static final EnumMap<PermissionDefault, DefaultChecker> DEFAULT_CHECKERS = new EnumMap<>(PermissionDefault.class);
    static {
        DEFAULT_CHECKERS.put(PermissionDefault.FALSE, s -> false);
        DEFAULT_CHECKERS.put(PermissionDefault.TRUE, s -> true);
        DEFAULT_CHECKERS.put(PermissionDefault.OP, CommandSender::isOp);
        DEFAULT_CHECKERS.put(PermissionDefault.NOT_OP, s -> !s.isOp());
    }
    DefaultChecker checker(PermissionDefault def) {
        DefaultChecker checker = DEFAULT_CHECKERS.get(def);
        return checker != null ? checker : s -> def.getValue(s.isOp());
    }

    @FunctionalInterface
    private interface DefaultChecker {
        boolean hasPermission(CommandSender sender);
    }
}
