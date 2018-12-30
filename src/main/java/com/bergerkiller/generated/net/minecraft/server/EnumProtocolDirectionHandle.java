package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.EnumProtocolDirection</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class EnumProtocolDirectionHandle extends Template.Handle {
    /** @See {@link EnumProtocolDirectionClass} */
    public static final EnumProtocolDirectionClass T = new EnumProtocolDirectionClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EnumProtocolDirectionHandle.class, "net.minecraft.server.EnumProtocolDirection", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    public static final EnumProtocolDirectionHandle SERVERBOUND = T.SERVERBOUND.getSafe();
    public static final EnumProtocolDirectionHandle CLIENTBOUND = T.CLIENTBOUND.getSafe();
    /* ============================================================================== */

    public static EnumProtocolDirectionHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.server.EnumProtocolDirection</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EnumProtocolDirectionClass extends Template.Class<EnumProtocolDirectionHandle> {
        public final Template.EnumConstant.Converted<EnumProtocolDirectionHandle> SERVERBOUND = new Template.EnumConstant.Converted<EnumProtocolDirectionHandle>();
        public final Template.EnumConstant.Converted<EnumProtocolDirectionHandle> CLIENTBOUND = new Template.EnumConstant.Converted<EnumProtocolDirectionHandle>();

    }

}

