package com.bergerkiller.generated.net.minecraft.world.level.block;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.level.block.FenceBlock</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.level.block.FenceBlock")
public abstract class FenceBlockHandle extends Template.Handle {
    /** @see FenceBlockClass */
    public static final FenceBlockClass T = Template.Class.create(FenceBlockClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static FenceBlockHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.world.level.block.FenceBlock</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class FenceBlockClass extends Template.Class<FenceBlockHandle> {
    }

}

