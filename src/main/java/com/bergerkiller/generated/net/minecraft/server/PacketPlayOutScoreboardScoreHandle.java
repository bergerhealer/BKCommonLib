package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class PacketPlayOutScoreboardScoreHandle extends Template.Handle {
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

    public static final class PacketPlayOutScoreboardScoreClass extends Template.Class<PacketPlayOutScoreboardScoreHandle> {
    }


    public static class EnumScoreboardActionHandle extends Template.Handle {
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

        public static final class EnumScoreboardActionClass extends Template.Class<EnumScoreboardActionHandle> {
            public final Template.EnumConstant.Converted<EnumScoreboardActionHandle> CHANGE = new Template.EnumConstant.Converted<EnumScoreboardActionHandle>();
            public final Template.EnumConstant.Converted<EnumScoreboardActionHandle> REMOVE = new Template.EnumConstant.Converted<EnumScoreboardActionHandle>();

        }

    }

}

