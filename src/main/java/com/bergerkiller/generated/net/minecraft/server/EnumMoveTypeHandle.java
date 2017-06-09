package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.EnumMoveType</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class EnumMoveTypeHandle extends Template.Handle {
    /** @See {@link EnumMoveTypeClass} */
    public static final EnumMoveTypeClass T = new EnumMoveTypeClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EnumMoveTypeHandle.class, "net.minecraft.server.EnumMoveType");

    public static final EnumMoveTypeHandle SELF = T.SELF.getSafe();
    public static final EnumMoveTypeHandle PLAYER = T.PLAYER.getSafe();
    public static final EnumMoveTypeHandle PISTON = T.PISTON.getSafe();
    public static final EnumMoveTypeHandle SHULKER_BOX = T.SHULKER_BOX.getSafe();
    public static final EnumMoveTypeHandle SHULKER = T.SHULKER.getSafe();
    /* ============================================================================== */

    public static EnumMoveTypeHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        EnumMoveTypeHandle handle = new EnumMoveTypeHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.server.EnumMoveType</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EnumMoveTypeClass extends Template.Class<EnumMoveTypeHandle> {
        public final Template.EnumConstant.Converted<EnumMoveTypeHandle> SELF = new Template.EnumConstant.Converted<EnumMoveTypeHandle>();
        public final Template.EnumConstant.Converted<EnumMoveTypeHandle> PLAYER = new Template.EnumConstant.Converted<EnumMoveTypeHandle>();
        public final Template.EnumConstant.Converted<EnumMoveTypeHandle> PISTON = new Template.EnumConstant.Converted<EnumMoveTypeHandle>();
        public final Template.EnumConstant.Converted<EnumMoveTypeHandle> SHULKER_BOX = new Template.EnumConstant.Converted<EnumMoveTypeHandle>();
        public final Template.EnumConstant.Converted<EnumMoveTypeHandle> SHULKER = new Template.EnumConstant.Converted<EnumMoveTypeHandle>();

    }

}

