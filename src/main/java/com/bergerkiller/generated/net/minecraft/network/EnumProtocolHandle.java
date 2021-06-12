package com.bergerkiller.generated.net.minecraft.network;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.EnumProtocol</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.EnumProtocol")
public abstract class EnumProtocolHandle extends Template.Handle {
    /** @See {@link EnumProtocolClass} */
    public static final EnumProtocolClass T = Template.Class.create(EnumProtocolClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    public static final EnumProtocolHandle PLAY = T.PLAY.getSafe();
    /* ============================================================================== */

    public static EnumProtocolHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract Class<?> getPacketClassIn(int id);
    public abstract Class<?> getPacketClassOut(int id);
    public abstract int getPacketIdIn(Class<?> packetClass);
    public abstract int getPacketIdOut(Class<?> packetClass);
    /**
     * Stores class members for <b>net.minecraft.network.EnumProtocol</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EnumProtocolClass extends Template.Class<EnumProtocolHandle> {
        public final Template.EnumConstant.Converted<EnumProtocolHandle> PLAY = new Template.EnumConstant.Converted<EnumProtocolHandle>();

        public final Template.Method<Class<?>> getPacketClassIn = new Template.Method<Class<?>>();
        public final Template.Method<Class<?>> getPacketClassOut = new Template.Method<Class<?>>();
        public final Template.Method<Integer> getPacketIdIn = new Template.Method<Integer>();
        public final Template.Method<Integer> getPacketIdOut = new Template.Method<Integer>();

    }

}

