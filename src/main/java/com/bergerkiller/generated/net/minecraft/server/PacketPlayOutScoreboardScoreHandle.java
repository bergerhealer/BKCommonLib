package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutScoreboardScore</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class PacketPlayOutScoreboardScoreHandle extends Template.Handle {
    /** @See {@link PacketPlayOutScoreboardScoreClass} */
    public static final PacketPlayOutScoreboardScoreClass T = new PacketPlayOutScoreboardScoreClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayOutScoreboardScoreHandle.class, "net.minecraft.server.PacketPlayOutScoreboardScore");

    /* ============================================================================== */

    public static PacketPlayOutScoreboardScoreHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        PacketPlayOutScoreboardScoreHandle handle = new PacketPlayOutScoreboardScoreHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayOutScoreboardScore</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutScoreboardScoreClass extends Template.Class<PacketPlayOutScoreboardScoreHandle> {
    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutScoreboardScore.EnumScoreboardAction</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    public static class EnumScoreboardActionHandle extends Template.Handle {
        /** @See {@link EnumScoreboardActionClass} */
        public static final EnumScoreboardActionClass T = new EnumScoreboardActionClass();
        static final StaticInitHelper _init_helper = new StaticInitHelper(EnumScoreboardActionHandle.class, "net.minecraft.server.PacketPlayOutScoreboardScore.EnumScoreboardAction");

        public static final EnumScoreboardActionHandle CHANGE = T.CHANGE.getSafe();
        public static final EnumScoreboardActionHandle REMOVE = T.REMOVE.getSafe();
        /* ============================================================================== */

        public static EnumScoreboardActionHandle createHandle(Object handleInstance) {
            if (handleInstance == null) return null;
            EnumScoreboardActionHandle handle = new EnumScoreboardActionHandle();
            handle.instance = handleInstance;
            return handle;
        }

        /* ============================================================================== */

        /**
         * Stores class members for <b>net.minecraft.server.PacketPlayOutScoreboardScore.EnumScoreboardAction</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class EnumScoreboardActionClass extends Template.Class<EnumScoreboardActionHandle> {
            public final Template.EnumConstant.Converted<EnumScoreboardActionHandle> CHANGE = new Template.EnumConstant.Converted<EnumScoreboardActionHandle>();
            public final Template.EnumConstant.Converted<EnumScoreboardActionHandle> REMOVE = new Template.EnumConstant.Converted<EnumScoreboardActionHandle>();

        }

    }

}

