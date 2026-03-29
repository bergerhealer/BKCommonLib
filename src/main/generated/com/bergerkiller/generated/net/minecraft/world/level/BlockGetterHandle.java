package com.bergerkiller.generated.net.minecraft.world.level;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.level.BlockGetter</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.level.BlockGetter")
public abstract class BlockGetterHandle extends Template.Handle {
    /** @see BlockGetterClass */
    public static final BlockGetterClass T = Template.Class.create(BlockGetterClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static BlockGetterHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.world.level.BlockGetter</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class BlockGetterClass extends Template.Class<BlockGetterHandle> {
    }

}

