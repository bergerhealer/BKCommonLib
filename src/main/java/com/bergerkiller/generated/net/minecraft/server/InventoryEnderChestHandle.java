package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.nbt.CommonTagList;
import com.bergerkiller.generated.net.minecraft.world.IInventoryHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.InventoryEnderChest</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.server.InventoryEnderChest")
public abstract class InventoryEnderChestHandle extends IInventoryHandle {
    /** @See {@link InventoryEnderChestClass} */
    public static final InventoryEnderChestClass T = Template.Class.create(InventoryEnderChestClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static InventoryEnderChestHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract void loadFromNBT(CommonTagList nbttaglist);
    public abstract CommonTagList saveToNBT();
    /**
     * Stores class members for <b>net.minecraft.server.InventoryEnderChest</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class InventoryEnderChestClass extends Template.Class<InventoryEnderChestHandle> {
        public final Template.Method.Converted<Void> loadFromNBT = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<CommonTagList> saveToNBT = new Template.Method.Converted<CommonTagList>();

    }

}

