package com.bergerkiller.generated.net.minecraft.world.level.block.entity;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.world.item.ItemStackHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity")
public abstract class AbstractFurnaceBlockEntityHandle extends BlockEntityHandle {
    /** @see AbstractFurnaceBlockEntityClass */
    public static final AbstractFurnaceBlockEntityClass T = Template.Class.create(AbstractFurnaceBlockEntityClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static AbstractFurnaceBlockEntityHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static int fuelTime(ItemStackHandle itemstack) {
        return T.fuelTime.invoke(itemstack);
    }

    /**
     * Stores class members for <b>net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class AbstractFurnaceBlockEntityClass extends Template.Class<AbstractFurnaceBlockEntityHandle> {
        public final Template.StaticMethod.Converted<Integer> fuelTime = new Template.StaticMethod.Converted<Integer>();

    }

}

