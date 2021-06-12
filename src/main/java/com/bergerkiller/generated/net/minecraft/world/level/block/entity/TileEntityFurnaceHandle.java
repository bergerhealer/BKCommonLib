package com.bergerkiller.generated.net.minecraft.world.level.block.entity;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.world.item.ItemStackHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.level.block.entity.TileEntityFurnace</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.level.block.entity.TileEntityFurnace")
public abstract class TileEntityFurnaceHandle extends TileEntityHandle {
    /** @See {@link TileEntityFurnaceClass} */
    public static final TileEntityFurnaceClass T = Template.Class.create(TileEntityFurnaceClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static TileEntityFurnaceHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static int fuelTime(ItemStackHandle itemstack) {
        return T.fuelTime.invoke(itemstack);
    }

    /**
     * Stores class members for <b>net.minecraft.world.level.block.entity.TileEntityFurnace</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class TileEntityFurnaceClass extends Template.Class<TileEntityFurnaceHandle> {
        public final Template.StaticMethod.Converted<Integer> fuelTime = new Template.StaticMethod.Converted<Integer>();

    }

}

