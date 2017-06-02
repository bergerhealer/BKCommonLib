package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class EnumProtocolDirectionHandle extends Template.Handle {
    public static final EnumProtocolDirectionClass T = new EnumProtocolDirectionClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EnumProtocolDirectionHandle.class, "net.minecraft.server.EnumProtocolDirection");

    public static final EnumProtocolDirectionHandle SERVERBOUND = T.SERVERBOUND.getSafe();
    public static final EnumProtocolDirectionHandle CLIENTBOUND = T.CLIENTBOUND.getSafe();
    /* ============================================================================== */

    public static EnumProtocolDirectionHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        EnumProtocolDirectionHandle handle = new EnumProtocolDirectionHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static final class EnumProtocolDirectionClass extends Template.Class<EnumProtocolDirectionHandle> {
        public final Template.EnumConstant.Converted<EnumProtocolDirectionHandle> SERVERBOUND = new Template.EnumConstant.Converted<EnumProtocolDirectionHandle>();
        public final Template.EnumConstant.Converted<EnumProtocolDirectionHandle> CLIENTBOUND = new Template.EnumConstant.Converted<EnumProtocolDirectionHandle>();

    }

}

