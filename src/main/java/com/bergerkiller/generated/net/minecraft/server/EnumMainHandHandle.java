package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.EnumMainHand</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
public abstract class EnumMainHandHandle extends Template.Handle {
    /** @See {@link EnumMainHandClass} */
    public static final EnumMainHandClass T = new EnumMainHandClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EnumMainHandHandle.class, "net.minecraft.server.EnumMainHand");

    public static final EnumMainHandHandle LEFT = T.LEFT.getSafe();
    public static final EnumMainHandHandle RIGHT = T.RIGHT.getSafe();
    /* ============================================================================== */

    public static EnumMainHandHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.server.EnumMainHand</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EnumMainHandClass extends Template.Class<EnumMainHandHandle> {
        public final Template.EnumConstant.Converted<EnumMainHandHandle> LEFT = new Template.EnumConstant.Converted<EnumMainHandHandle>();
        public final Template.EnumConstant.Converted<EnumMainHandHandle> RIGHT = new Template.EnumConstant.Converted<EnumMainHandHandle>();

    }

}

