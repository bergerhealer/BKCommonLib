package com.bergerkiller.generated.net.minecraft.world.level.block;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.level.block.FenceGateBlock</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.level.block.FenceGateBlock")
public abstract class FenceGateBlockHandle extends Template.Handle {
    /** @see FenceGateBlockClass */
    public static final FenceGateBlockClass T = Template.Class.create(FenceGateBlockClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static FenceGateBlockHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.world.level.block.FenceGateBlock</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class FenceGateBlockClass extends Template.Class<FenceGateBlockHandle> {
    }

}

