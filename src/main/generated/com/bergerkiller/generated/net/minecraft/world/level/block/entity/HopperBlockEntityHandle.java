package com.bergerkiller.generated.net.minecraft.world.level.block.entity;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.World;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.level.block.entity.HopperBlockEntity</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.level.block.entity.HopperBlockEntity")
public abstract class HopperBlockEntityHandle extends BlockEntityHandle {
    /** @see HopperBlockEntityClass */
    public static final HopperBlockEntityClass T = Template.Class.create(HopperBlockEntityClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static HopperBlockEntityHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static boolean suckItems(World world, Object ihopper) {
        return T.suckItems.invoke(world, ihopper);
    }

    /**
     * Stores class members for <b>net.minecraft.world.level.block.entity.HopperBlockEntity</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class HopperBlockEntityClass extends Template.Class<HopperBlockEntityHandle> {
        public final Template.StaticMethod.Converted<Boolean> suckItems = new Template.StaticMethod.Converted<Boolean>();

    }

}

