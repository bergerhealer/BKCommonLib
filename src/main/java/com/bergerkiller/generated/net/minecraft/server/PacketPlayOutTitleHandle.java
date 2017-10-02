package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutTitle</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class PacketPlayOutTitleHandle extends PacketHandle {
    /** @See {@link PacketPlayOutTitleClass} */
    public static final PacketPlayOutTitleClass T = new PacketPlayOutTitleClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayOutTitleHandle.class, "net.minecraft.server.PacketPlayOutTitle");

    /* ============================================================================== */

    public static PacketPlayOutTitleHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayOutTitle</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutTitleClass extends Template.Class<PacketPlayOutTitleHandle> {
    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutTitle.EnumTitleAction</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    public abstract static class EnumTitleActionHandle extends Template.Handle {
        /** @See {@link EnumTitleActionClass} */
        public static final EnumTitleActionClass T = new EnumTitleActionClass();
        static final StaticInitHelper _init_helper = new StaticInitHelper(EnumTitleActionHandle.class, "net.minecraft.server.PacketPlayOutTitle.EnumTitleAction");

        public static final EnumTitleActionHandle TITLE = T.TITLE.getSafe();
        public static final EnumTitleActionHandle SUBTITLE = T.SUBTITLE.getSafe();
        public static final EnumTitleActionHandle TIMES = T.TIMES.getSafe();
        public static final EnumTitleActionHandle CLEAR = T.CLEAR.getSafe();
        public static final EnumTitleActionHandle RESET = T.RESET.getSafe();
        /* ============================================================================== */

        public static EnumTitleActionHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */

        /**
         * Stores class members for <b>net.minecraft.server.PacketPlayOutTitle.EnumTitleAction</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class EnumTitleActionClass extends Template.Class<EnumTitleActionHandle> {
            public final Template.EnumConstant.Converted<EnumTitleActionHandle> TITLE = new Template.EnumConstant.Converted<EnumTitleActionHandle>();
            public final Template.EnumConstant.Converted<EnumTitleActionHandle> SUBTITLE = new Template.EnumConstant.Converted<EnumTitleActionHandle>();
            @Template.Optional
            public final Template.EnumConstant.Converted<EnumTitleActionHandle> ACTIONBAR = new Template.EnumConstant.Converted<EnumTitleActionHandle>();
            public final Template.EnumConstant.Converted<EnumTitleActionHandle> TIMES = new Template.EnumConstant.Converted<EnumTitleActionHandle>();
            public final Template.EnumConstant.Converted<EnumTitleActionHandle> CLEAR = new Template.EnumConstant.Converted<EnumTitleActionHandle>();
            public final Template.EnumConstant.Converted<EnumTitleActionHandle> RESET = new Template.EnumConstant.Converted<EnumTitleActionHandle>();

        }

    }

}

