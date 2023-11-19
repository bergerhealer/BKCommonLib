package com.bergerkiller.bukkit.common.internal.permissions;

import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.softdependency.SoftDependency;
import com.bergerkiller.bukkit.common.softdependency.SoftServiceDependency;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * Tracks at runtime as dependencies enable/disable, and automatically switches out the
 * permission handler that should be used.
 */
public class PermissionHandlerSelector {
    private PermissionHandler handler = null;
    private final SoftDependency<Plugin> luckoPerms5;
    private final SoftServiceDependency<net.milkbowl.vault.permission.Permission> vaultPermissions;
    private final List<Option> options;
    private Option currOption = null;

    public PermissionHandlerSelector(CommonPlugin plugin) {
        luckoPerms5 = SoftDependency.build(plugin, "LuckPerms")
                .withInitializer(p -> {
                    try {
                        // Check this has the version 5+ API
                        Class.forName("net.luckperms.api.LuckPermsProvider");
                        return p;
                    } catch (Throwable t) {
                        return null;
                    }
                })
                .whenEnable(this::detectPermOption)
                .whenDisable(this::detectPermOption)
                .create();

        vaultPermissions = new SoftServiceDependency<net.milkbowl.vault.permission.Permission>(plugin, "net.milkbowl.vault.permission.Permission") {
            @Override
            protected net.milkbowl.vault.permission.Permission initialize(Object service) {
                return net.milkbowl.vault.permission.Permission.class.cast(service);
            }

            @Override
            protected void onEnable() {
                detectPermOption();
            }

            @Override
            protected void onDisable() {
                detectPermOption();
            }
        };

        options = Arrays.asList(
                Option.of("LuckPerms5", () -> luckoPerms5.get() != null, PermissionHandlerLuckPerms5::new),
                Option.of("Vault", vaultPermissions::isEnabled, () -> new PermissionHandlerWildcardVault(vaultPermissions.get())),
                Option.of("Default", () -> true, PermissionHandlerWildcard::new)
        );
    }

    public PermissionHandler current() {
        return handler;
    }

    public void detectPermOption() {
        for (Option opt : options) {
            if (!opt.active.getAsBoolean()) {
                continue;
            }

            if (opt != currOption) {
                currOption = opt;
                handler = opt.ctor.get();
                //Logging.LOGGER_PERMISSIONS.log(Level.INFO, "Switching to permission manager " + opt.name);
            }
            break;
        }
    }

    private static class Option {
        public final String name;
        public final BooleanSupplier active;
        public final Supplier<PermissionHandler> ctor;

        public Option(String name, BooleanSupplier active, Supplier<PermissionHandler> ctor) {
            this.name = name;
            this.active = active;
            this.ctor = ctor;
        }

        public static Option of(String name, BooleanSupplier active, Supplier<PermissionHandler> ctor) {
            return new Option(name, active, ctor);
        }
    }
}
