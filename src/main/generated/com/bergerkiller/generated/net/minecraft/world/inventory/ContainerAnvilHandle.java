package com.bergerkiller.generated.net.minecraft.world.inventory;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.inventory.InventoryView;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.inventory.ContainerAnvil</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.inventory.ContainerAnvil")
public abstract class ContainerAnvilHandle extends ContainerHandle {
    /** @see ContainerAnvilClass */
    public static final ContainerAnvilClass T = Template.Class.create(ContainerAnvilClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
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
     * Stores class members for <b>net.minecraft.world.inventory.ContainerAnvil</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ContainerAnvilClass extends Template.Class<ContainerAnvilHandle> {
        public final Template.Field<String> renameText = new Template.Field<String>();

        public final Template.StaticMethod<ContainerAnvilHandle> fromBukkit = new Template.StaticMethod<ContainerAnvilHandle>();

    }

}

