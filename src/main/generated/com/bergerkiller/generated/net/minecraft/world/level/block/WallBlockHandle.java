package com.bergerkiller.generated.net.minecraft.world.level.block;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.level.block.WallBlock</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.level.block.WallBlock")
public abstract class WallBlockHandle extends Template.Handle {
    /** @see WallBlockClass */
    public static final WallBlockClass T = Template.Class.create(WallBlockClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static WallBlockHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.world.level.block.WallBlock</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class WallBlockClass extends Template.Class<WallBlockHandle> {
    }

}

