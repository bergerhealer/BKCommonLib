package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.generated.net.minecraft.server.EnumDirectionHandle.EnumAxisHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class EnumDirectionHandle extends Template.Handle {
    public static final EnumDirectionClass T = new EnumDirectionClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EnumDirectionHandle.class, "net.minecraft.server.EnumDirection");


    /* ============================================================================== */

    public static final EnumDirectionHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        EnumDirectionHandle handle = new EnumDirectionHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static final class EnumDirectionClass extends Template.Class {
    }

    public static class EnumAxisHandle extends Template.Handle {
        public static final EnumAxisClass T = new EnumAxisClass();
        static final StaticInitHelper _init_helper = new StaticInitHelper(EnumAxisHandle.class, "net.minecraft.server.EnumDirection.EnumAxis");

        public static final EnumAxisHandle X = T.X.getSafe();
        public static final EnumAxisHandle Y = T.Y.getSafe();
        public static final EnumAxisHandle Z = T.Z.getSafe();

        /* ============================================================================== */

        public static final EnumAxisHandle createHandle(Object handleInstance) {
            if (handleInstance == null) return null;
            EnumAxisHandle handle = new EnumAxisHandle();
            handle.instance = handleInstance;
            return handle;
        }

        /* ============================================================================== */

        public int ordinal() {
            return ((Enum<?>) instance).ordinal();
        }

        public static final class EnumAxisClass extends Template.Class {
            public final Template.EnumConstant.Converted<EnumAxisHandle> X = new Template.EnumConstant.Converted<EnumAxisHandle>();
            public final Template.EnumConstant.Converted<EnumAxisHandle> Y = new Template.EnumConstant.Converted<EnumAxisHandle>();
            public final Template.EnumConstant.Converted<EnumAxisHandle> Z = new Template.EnumConstant.Converted<EnumAxisHandle>();

        }
    }
}
