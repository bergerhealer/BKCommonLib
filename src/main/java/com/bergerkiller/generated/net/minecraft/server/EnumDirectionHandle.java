package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class EnumDirectionHandle extends Template.Handle {
    public static final EnumDirectionClass T = new EnumDirectionClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EnumDirectionHandle.class, "net.minecraft.server.EnumDirection");

    public static final EnumDirectionHandle DOWN = T.DOWN.getSafe();
    public static final EnumDirectionHandle UP = T.UP.getSafe();
    public static final EnumDirectionHandle NORTH = T.NORTH.getSafe();
    public static final EnumDirectionHandle SOUTH = T.SOUTH.getSafe();
    public static final EnumDirectionHandle WEST = T.WEST.getSafe();
    public static final EnumDirectionHandle EAST = T.EAST.getSafe();
    /* ============================================================================== */

    public static EnumDirectionHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        EnumDirectionHandle handle = new EnumDirectionHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static final class EnumDirectionClass extends Template.Class<EnumDirectionHandle> {
        public final Template.EnumConstant.Converted<EnumDirectionHandle> DOWN = new Template.EnumConstant.Converted<EnumDirectionHandle>();
        public final Template.EnumConstant.Converted<EnumDirectionHandle> UP = new Template.EnumConstant.Converted<EnumDirectionHandle>();
        public final Template.EnumConstant.Converted<EnumDirectionHandle> NORTH = new Template.EnumConstant.Converted<EnumDirectionHandle>();
        public final Template.EnumConstant.Converted<EnumDirectionHandle> SOUTH = new Template.EnumConstant.Converted<EnumDirectionHandle>();
        public final Template.EnumConstant.Converted<EnumDirectionHandle> WEST = new Template.EnumConstant.Converted<EnumDirectionHandle>();
        public final Template.EnumConstant.Converted<EnumDirectionHandle> EAST = new Template.EnumConstant.Converted<EnumDirectionHandle>();

    }


    public static class EnumAxisHandle extends Template.Handle {
        public static final EnumAxisClass T = new EnumAxisClass();
        static final StaticInitHelper _init_helper = new StaticInitHelper(EnumAxisHandle.class, "net.minecraft.server.EnumDirection.EnumAxis");

        public static final EnumAxisHandle X = T.X.getSafe();
        public static final EnumAxisHandle Y = T.Y.getSafe();
        public static final EnumAxisHandle Z = T.Z.getSafe();
        /* ============================================================================== */

        public static EnumAxisHandle createHandle(Object handleInstance) {
            if (handleInstance == null) return null;
            EnumAxisHandle handle = new EnumAxisHandle();
            handle.instance = handleInstance;
            return handle;
        }

        /* ============================================================================== */


        public int ordinal() {
            return ((Enum<?>) instance).ordinal();
        }
        public static final class EnumAxisClass extends Template.Class<EnumAxisHandle> {
            public final Template.EnumConstant.Converted<EnumAxisHandle> X = new Template.EnumConstant.Converted<EnumAxisHandle>();
            public final Template.EnumConstant.Converted<EnumAxisHandle> Y = new Template.EnumConstant.Converted<EnumAxisHandle>();
            public final Template.EnumConstant.Converted<EnumAxisHandle> Z = new Template.EnumConstant.Converted<EnumAxisHandle>();

        }

    }

}

