package com.bergerkiller.generated.net.minecraft.network.protocol;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.EnumProtocolDirection</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.EnumProtocolDirection")
public abstract class EnumProtocolDirectionHandle extends Template.Handle {
    /** @See {@link EnumProtocolDirectionClass} */
    public static final EnumProtocolDirectionClass T = Template.Class.create(EnumProtocolDirectionClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    public static final EnumProtocolDirectionHandle SERVERBOUND = T.SERVERBOUND.getSafe();
    public static final EnumProtocolDirectionHandle CLIENTBOUND = T.CLIENTBOUND.getSafe();
    /* ============================================================================== */

    public static EnumProtocolDirectionHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.network.protocol.EnumProtocolDirection</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EnumProtocolDirectionClass extends Template.Class<EnumProtocolDirectionHandle> {
        public final Template.EnumConstant.Converted<EnumProtocolDirectionHandle> SERVERBOUND = new Template.EnumConstant.Converted<EnumProtocolDirectionHandle>();
        public final Template.EnumConstant.Converted<EnumProtocolDirectionHandle> CLIENTBOUND = new Template.EnumConstant.Converted<EnumProtocolDirectionHandle>();

    }

}

