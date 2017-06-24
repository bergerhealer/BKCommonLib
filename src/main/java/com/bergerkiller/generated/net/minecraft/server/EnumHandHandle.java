package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.EnumHand</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
public class EnumHandHandle extends Template.Handle {
    /** @See {@link EnumHandClass} */
    public static final EnumHandClass T = new EnumHandClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EnumHandHandle.class, "net.minecraft.server.EnumHand");

    public static final EnumHandHandle MAIN_HAND = T.MAIN_HAND.getSafe();
    public static final EnumHandHandle OFF_HAND = T.OFF_HAND.getSafe();
    /* ============================================================================== */

    public static EnumHandHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        EnumHandHandle handle = new EnumHandHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.server.EnumHand</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EnumHandClass extends Template.Class<EnumHandHandle> {
        public final Template.EnumConstant.Converted<EnumHandHandle> MAIN_HAND = new Template.EnumConstant.Converted<EnumHandHandle>();
        public final Template.EnumConstant.Converted<EnumHandHandle> OFF_HAND = new Template.EnumConstant.Converted<EnumHandHandle>();

    }

}

