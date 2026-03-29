package com.bergerkiller.generated.net.minecraft.network;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import io.netty.channel.Channel;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.Connection</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.Connection")
public abstract class ConnectionHandle extends Template.Handle {
    /** @see ConnectionClass */
    public static final ConnectionClass T = Template.Class.create(ConnectionClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ConnectionHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static boolean queuePacketUnsafe(Object networkManager, Object packet) {
        return T.queuePacketUnsafe.invoke(networkManager, packet);
    }

    public abstract boolean isConnected();
    public abstract void queue_sendPacketImpl(Object packet, Object packetsendlistener, boolean flush);
    public abstract Channel getChannel();
    public abstract void setChannel(Channel value);
    /**
     * Stores class members for <b>net.minecraft.network.Connection</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ConnectionClass extends Template.Class<ConnectionHandle> {
        public final Template.Field<Channel> channel = new Template.Field<Channel>();

        public final Template.StaticMethod.Converted<Boolean> queuePacketUnsafe = new Template.StaticMethod.Converted<Boolean>();

        public final Template.Method<Boolean> isConnected = new Template.Method<Boolean>();
        public final Template.Method.Converted<Void> queue_sendPacketImpl = new Template.Method.Converted<Void>();

    }

}

