package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.Blocks</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class BlocksHandle extends Template.Handle {
    /** @See {@link BlocksClass} */
    public static final BlocksClass T = new BlocksClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(BlocksHandle.class, "net.minecraft.server.Blocks", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    public static final Object LADDER = T.LADDER.getSafe();
    /* ============================================================================== */

    public static BlocksHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.server.Blocks</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class BlocksClass extends Template.Class<BlocksHandle> {
        public final Template.StaticField.Converted<Object> LADDER = new Template.StaticField.Converted<Object>();

    }

}

