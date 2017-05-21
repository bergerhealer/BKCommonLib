package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class EnumHandHandle extends Template.Handle {
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

    public static final class EnumHandClass extends Template.Class<EnumHandHandle> {
        public final Template.EnumConstant.Converted<EnumHandHandle> MAIN_HAND = new Template.EnumConstant.Converted<EnumHandHandle>();
        public final Template.EnumConstant.Converted<EnumHandHandle> OFF_HAND = new Template.EnumConstant.Converted<EnumHandHandle>();

    }
}
