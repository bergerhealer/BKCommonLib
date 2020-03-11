package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.inventory.InventoryView;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.ContainerAnvil</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class ContainerAnvilHandle extends ContainerHandle {
    /** @See {@link ContainerAnvilClass} */
    public static final ContainerAnvilClass T = new ContainerAnvilClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(ContainerAnvilHandle.class, "net.minecraft.server.ContainerAnvil", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static ContainerAnvilHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static ContainerAnvilHandle fromBukkit(InventoryView bukkitView) {
        return T.fromBukkit.invoker.invoke(null,bukkitView);
    }

    public abstract String getRenameText();
    public abstract void setRenameText(String value);
    /**
     * Stores class members for <b>net.minecraft.server.ContainerAnvil</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ContainerAnvilClass extends Template.Class<ContainerAnvilHandle> {
        public final Template.Field<String> renameText = new Template.Field<String>();

        public final Template.StaticMethod<ContainerAnvilHandle> fromBukkit = new Template.StaticMethod<ContainerAnvilHandle>();

    }

}

